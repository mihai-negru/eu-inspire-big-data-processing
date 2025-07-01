import threading, time
from confluent_kafka import Producer

from schema import *
from consumer import *

RUN_TESTS = 15

TEST_BATCH = 50

def main(idx):
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
    transform_end_time = time.perf_counter_ns()

    validator_thread.join()
    validator_end_time = time.perf_counter_ns()

    print(f"Done [{idx}]: {validator_end_time - transform_end_time} ns\n", flush=True)

if __name__ == "__main__":

    test_counter = 0

    while test_counter < RUN_TESTS:
        main(test_counter)
        test_counter += 1
        time.sleep(30)