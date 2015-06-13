ALTER SESSION SET NLS_TIMESTAMP_TZ_FORMAT='DD-MON-RR HH:MI:SSXFF AM TZR';
ALTER SESSION SET TIME_ZONE='+5:30';

delete from structured_event;
delete from raw_event;
delete from source_metadata;
delete from source_mapping;
delete from index_mapping;

drop table structured_event;
drop table raw_event;
drop table source_metadata;
drop table source_mapping;
drop table index_mapping;

  
CREATE TABLE index_mapping
  (
    search_index VARCHAR2(20),
    source_type  VARCHAR2(20),
    CONSTRAINT index_pk PRIMARY KEY (search_index, source_type)
  );

CREATE TABLE source_mapping
  (
    source_type VARCHAR2(20),
    source VARCHAR2(200) NOT NULL,
    CONSTRAINT source_pk PRIMARY KEY (source_type)
  );

CREATE TABLE source_metadata
  (
    source_type VARCHAR2(20),
    host varchar2(50) DEFAULT 'localhost' NOT NULL,
	source varchar2(200) NOT NULL,
	pattern_layout varchar2(200) NOT NULL,
	timestamp_format varchar2(30),
	CONSTRAINT source_metadata_fk FOREIGN KEY (source_type) REFERENCES source_mapping (source_type)
  );

CREATE TABLE raw_event
  (
    event_id NUMBER GENERATED BY DEFAULT AS IDENTITY (
    START WITH 1 INCREMENT BY 1),
    event_timestamp TIMESTAMP WITH TIME ZONE,
    raw_event_data  VARCHAR2(1000),
    source_type     VARCHAR2(20),
    CONSTRAINT eventid_pk PRIMARY KEY (event_id),
    CONSTRAINT source_type_fk FOREIGN KEY (source_type) REFERENCES source_mapping (source_type)
  );

CREATE TABLE structured_event
  (
    event_id NUMBER GENERATED BY DEFAULT AS IDENTITY (
    START WITH 1 INCREMENT BY 1),
    message_content VARCHAR2(1000),
    CONSTRAINT eventid_fk FOREIGN KEY (event_id) REFERENCES raw_event (event_id)
  );

commit;