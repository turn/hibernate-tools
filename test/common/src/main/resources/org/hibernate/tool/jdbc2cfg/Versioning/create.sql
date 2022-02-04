CREATE TABLE WITH_VERSION (ONE INT, TWO INT, VERSION INT, NAME VARCHAR(256), PRIMARY KEY (ONE))
CREATE TABLE NO_VERSION (ONE INT, TWO INT, NAME VARCHAR(256), PRIMARY KEY (TWO))
CREATE TABLE WITH_REAL_TIMESTAMP (ONE INT, TWO INT, DBTIMESTAMP TIMESTAMP, NAME VARCHAR(256), PRIMARY KEY (ONE))
CREATE TABLE WITH_FAKE_TIMESTAMP (ONE INT, TWO INT, DBTIMESTAMP INT, NAME VARCHAR(256), PRIMARY KEY (ONE))
