  INSERT
INTO raw_event
  (
    event_timestamp,
    raw_event_data,
    source_type
  )
  VALUES
  (
    sysdate,
    'userId=uid1 reqId=axza1122_xasad- sessId=jh289e3h9e8cz_12 This is a sample log statement',
    'info_log'
  );

alter table structured_event add userId varchar2(100);

  INSERT
INTO structured_event
  (
    message_content,
    userId
  )
  VALUES
  (
    'This is a sample log statement',
    'uid1'
  );

  
insert into raw_event (raw_event_data, event_timestamp, source_type) values('dfdf', TO_TIMESTAMP_TZ('13-Jun-15 02.23.04.000 AM +0530'), 'info_log');

select to_timestamp_tz(event_timestamp) from raw_event where event_id=2;

select sm.timestamp_format from source_metadata sm INNER JOIN raw_event re ON re.source_type = sm.source_type where re.event_id=2;

select COLUMN_NAME from ALL_TAB_COLUMNS where TABLE_NAME='STRUCTURED_EVENT';