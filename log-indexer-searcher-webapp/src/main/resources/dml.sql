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
