import uuid

from confluent_kafka import Consumer

class ConsumerResult:
    def __init__(self, topics, recv_expected, collect):
        self.messages = []
        self.recv_expected = recv_expected
        self.collect = collect

        self.consumer = Consumer({
            "bootstrap.servers": "kafka.kafka.svc.cluster.local:9092",
            "group.id": "test-consumer-" + str(uuid.uuid4()),
            "auto.offset.reset": "latest",
            "enable.auto.commit": False
        })

        self.consumer.subscribe(topics=topics)
        self.consumer.poll(timeout=5.0)

def consumer_job(holder):
    recv_count = 0
    while recv_count< holder.recv_expected:
        msg = holder.consumer.poll(timeout=10.0)
        if msg is None or msg.error():
            continue

        recv_count += 1
        if holder.collect:
            decoded = msg.value().decode()
            holder.messages.append(decoded)

    holder.consumer.close()
