CREATE KEYSPACE IF NOT EXISTS inspire WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 };

CREATE TABLE IF NOT EXISTS inspire.transformed (
    id              uuid PRIMARY KEY,
    xml             blob,
    status          text,
    failure_details map<text,text>
);