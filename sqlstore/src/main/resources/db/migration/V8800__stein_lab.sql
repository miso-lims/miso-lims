SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
INSERT INTO Lab(instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES ((SELECT instituteId FROM Institute WHERE alias = 'Ontario Institute for Cancer Research'), 'Lincoln Stein', @admin, CURRENT_TIMESTAMP, @admin, CURRENT_TIMESTAMP);
