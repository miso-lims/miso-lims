SELECT userId INTO @user FROM User WHERE loginName = 'admin';
INSERT INTO Institute(alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES ('London Health Sciences Center', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
INSERT INTO Lab(instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES ((SELECT instituteId FROM Institute WHERE alias = 'London Health Sciences Center'), 'Not Specified', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
