-- custom_parameters

INSERT INTO SequencingParameters(name, platformId, xpath, readLength, paired, createdBy, creationDate, updatedBy, lastUpdated)
  SELECT 'Custom (see notes)', platformId, 'false', 0, FALSE, (SELECT userId FROM User WHERE loginName = 'admin'), CURRENT_TIMESTAMP, (SELECT userId FROM User WHERE loginName = 'admin'), CURRENT_TIMESTAMP FROM Platform;


