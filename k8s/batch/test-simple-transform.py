import threading, time
from confluent_kafka import Producer

from schema import *
from consumer import *

TEST_BATCH = 50

def main():
    print("Testing simple-validator")

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

    for _ in range(TEST_BATCH):
        msg = SEND_TO_RAW_MSG(group_id)
        producer.produce("raw", msg.encode("utf-8"))
    producer.flush()

    transform_start_time = time.perf_counter_ns()
    transform_thread.join()
    transform_end_time = time.perf_counter_ns()

    print(f"Done: {transform_end_time - transform_start_time} ns\n")

if __name__ == "__main__":
    main()