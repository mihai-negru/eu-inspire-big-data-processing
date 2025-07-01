import threading, time
from confluent_kafka import Producer

from schema import *
from consumer import *

TEST_BATCH = 1000

def main():
    print("Testing Generate Group Id")

    producer1 = Producer({"bootstrap.servers": "kafka.kafka.svc.cluster.local:9092"})

    consumer1_holder = ConsumerResult(["command.output"], TEST_BATCH, False)
    consumer1_thread = threading.Thread(target=consumer_job, args=(consumer1_holder,))

    consumer1_thread.start()

    for _ in range(TEST_BATCH):
        msg = GENERATE_GROUP_ID_MSG()
        producer1.produce("command.input", msg.encode("utf-8"))
    producer1.flush()

    consumer1_start_time = time.perf_counter_ns()
    consumer1_thread.join()
    consumer1_end_time = time.perf_counter_ns()

    print(f"Done: {consumer1_end_time - consumer1_start_time} ns\n")

if __name__ == "__main__":
    main()