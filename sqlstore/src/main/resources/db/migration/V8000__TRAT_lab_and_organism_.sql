--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';
INSERT INTO Lab (alias, instituteId, createdBy, creationDate, updatedBy, lastUpdated) VALUES
  ('Paul Boutros', (SELECT instituteId FROM Institute WHERE alias = 'Ontario Institute for Cancer Research'), @user, @time, @user, @time);
--EndNoTest