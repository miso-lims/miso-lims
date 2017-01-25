--StartNoTest
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
INSERT INTO Institute(alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES ('Royal Ontario Museum', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
INSERT INTO Lab(instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES ((SELECT instituteId FROM Institute WHERE alias = 'Royal Ontario Museum'), 'Not Specified', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
--EndNoTest