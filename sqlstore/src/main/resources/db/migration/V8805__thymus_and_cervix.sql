-- StartNoTest
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
INSERT INTO TissueOrigin(alias, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
  ('Ce', 'Cervix', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP),
  ('Th', 'Thymus', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
-- EndNoTest
