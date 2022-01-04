UPDATE Run SET
  completionDate = startDate,
  lastModifier = (SELECT userId FROM User WHERE loginName = 'admin'),
  lastModified = NOW()
WHERE health = 'Failed' AND completionDate IS NULL;
