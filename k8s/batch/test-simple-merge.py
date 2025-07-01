import threading, time
from confluent_kafka import Producer

from schema import *
from consumer import *

TEST_BATCH = 50
MERGE_TEST_BATCH = 1000

RUN_TESTS = 15

def main():
    producer = Producer({"bootstrap.servers": "kafka.kafka.svc.cluster.local:9092"})

    init_group_holder = ConsumerResult(["command.output"], 1, True)
    init_group_thread = threading.Thread(target=consumer_job, args=(init_group_holder,))

    init_group_thread.start()
    producer.produce("command.input", GENERATE_GROUP_ID_MSG().encode("utf-8"))
    producer.flush()
    init_group_thread.join()

    group_id = extract_group_id(init_group_holder.messages[0])

    transform_holder = ConsumerResult(["validator.input"], TEST_BATCH, False)
    transform_thread = threading.Thread(target=consumer_job, args=(transform_holder,))
    transform_thread.start()

    validator_holder = ConsumerResult(["validator.output"], TEST_BATCH, False)
    validator_thread = threading.Thread(target=consumer_job, args=(validator_holder,))
    validator_thread.start()

    for _ in range(TEST_BATCH):
        msg = SEND_TO_RAW_MSG(group_id)
        producer.produce("raw", msg.encode("utf-8"))
    producer.flush()

    transform_thread.join()
    validator_thread.join()

    for idx in range(RUN_TESTS):
        merge_group_holder = ConsumerResult(["command.output"], MERGE_TEST_BATCH, True)
        merge_thread = threading.Thread(target=consumer_job, args=(merge_group_holder,))
        merge_thread.start()

        for _ in range(MERGE_TEST_BATCH):
            msg = MERGE_GROUP_ID_MSG(group_id)
            producer.produce("command.input", msg.encode("utf-8"))
        producer.flush()

        merge_time_start = time.perf_counter_ns()
        merge_thread.join()
        merge_time_end = time.perf_counter_ns()

        print(f"Done [{idx}]: {merge_time_end - merge_time_start} ns\n", flush=True)

        time.sleep(20)

if __name__ == "__main__":
    main()
