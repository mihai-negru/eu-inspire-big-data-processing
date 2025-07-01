import sys
import threading, time
from confluent_kafka import Producer

from schema import *
from consumer import *

import logging

BOUNDARIES_COUNT = 10
CONDOMINIUM_COUNT = 5
GEOGRAPHICAL_NAME_COUNT = 15
ADMINISTRATIVE_UNIT_COUNT = 1

def extract_xml_file(filename) -> str:
    with open(filename, 'r') as f:
        xml_content = f.read()

    return xml_content

def main():
    logging.info("Presenting the demo")

    logging.info("Extract the 'administrative-boundary' content")
    administrative_boundary = extract_xml_file("/app/administrative-boundary.xml")
    logging.info("Extract the 'administrative-unit' content")
    administrative_unit = extract_xml_file("/app/administrative-unit.xml")
    logging.info("Extract the 'condominium' content")
    condominium = extract_xml_file("/app/condominium.xml")
    logging.info("Extract the 'geographical-name' content")
    geographical_name = extract_xml_file("/app/geographical-name.xml")

    producer = Producer({"bootstrap.servers": "kafka.kafka.svc.cluster.local:9092"})
    logging.info("A simple Producer was created successfully for uploading data")

    init_group_holder = ConsumerResult(["command.output"], 1, True)
    init_group_thread = threading.Thread(target=consumer_job, args=(init_group_holder,))

    logging.info("Listening to the topic 'command.output' to extract the group id")
    init_group_thread.start()

    logging.info("Send a 'create group id' command to the Flink")
    producer.produce("command.input", GENERATE_GROUP_ID_MSG().encode("utf-8"))
    producer.flush()
    logging.info("Create group id request was successfully sent")

    init_group_thread.join()

    group_id = extract_group_id(init_group_holder.messages[0])
    logging.info(f"The newly created group id is '{group_id}'")

    validator_holder = ConsumerResult(["validator.output"], BOUNDARIES_COUNT + CONDOMINIUM_COUNT + GEOGRAPHICAL_NAME_COUNT + ADMINISTRATIVE_UNIT_COUNT, False)
    validator_thread = threading.Thread(target=consumer_job, args=(validator_holder,))

    logging.info("Listening to the topic 'validator.output' know when to release the merge command")
    validator_thread.start()

    # Sending raw data
    logging.info("Sending raw administrative boundary data")
    for idx in range(BOUNDARIES_COUNT):
        producer.produce("raw", create_raw_content("FCAdministrativeBoundary", f"boundary[{idx}].voidValue", group_id, administrative_boundary))
    producer.flush()

    logging.info("Sending raw condominium data")
    for idx in range(CONDOMINIUM_COUNT):
        producer.produce("raw", create_raw_content("FCCondominium", f"condominium[{idx}].voidValue", group_id, condominium))
    producer.flush()

    logging.info("Sending raw geographical name data")
    for idx in range(GEOGRAPHICAL_NAME_COUNT):
        producer.produce("raw", create_raw_content("FCGeographicalName", f"condominium[{idx % CONDOMINIUM_COUNT}].voidValue.name[{idx % (GEOGRAPHICAL_NAME_COUNT // CONDOMINIUM_COUNT)}].voidValue", group_id, geographical_name))
    producer.flush()

    logging.info("Send raw administrative unit data")
    for idx in range(ADMINISTRATIVE_UNIT_COUNT):
        producer.produce("raw", create_raw_content("FCAdministrativeUnit", "", group_id, administrative_unit))
    producer.flush()
    # Finished sending data

    validator_thread.join()
    time.sleep(5)

    logging.info("All the data was validated successfully")

    merge_group_holder = ConsumerResult(["command.output"], 1, True)
    merge_thread = threading.Thread(target=consumer_job, args=(merge_group_holder,))

    logging.info("Listening to the topic 'command.output' to extract the merged data set")
    merge_thread.start()

    logging.info("Send a 'merge' command to the Flink")
    producer.produce("command.input", MERGE_GROUP_ID_MSG(group_id).encode("utf-8"))
    producer.flush()
    logging.info("Merge group id request was successfully sent")

    merge_thread.join()
    logging.info("Successfully received the merged data set")

    merged_data_set = extract_merged_data_set(merge_group_holder.messages[0])

    logging.info("The merged data set is: \n\033[0;32m%s\033[0m\n", merged_data_set)

if __name__ == '__main__':
    logging.basicConfig(stream=sys.stderr, level=logging.DEBUG)
    main()
