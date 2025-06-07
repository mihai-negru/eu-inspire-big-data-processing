CREATE KEYSPACE IF NOT EXISTS inspire WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 };

CREATE TABLE IF NOT EXISTS inspire.transformed (
    id              text PRIMARY KEY,
    group_id        text,
    xml_schema      text,
    xml_path        text,
    xml             blob,
    status          text,
    failure_details map<text,text>
);

CREATE TABLE IF NOT EXISTS inspire.executed (
    id              text PRIMARY KEY,
    command         text,
    obj             blob
);