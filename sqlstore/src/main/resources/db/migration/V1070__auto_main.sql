-- scientific_names
CREATE TABLE ScientificName (
  scientificNameId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  PRIMARY KEY (scientificNameId),
  CONSTRAINT uk_scientificName_alias UNIQUE (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO ScientificName (alias)
SELECT DISTINCT scientificName FROM Sample;

INSERT INTO ScientificName (alias)
SELECT DISTINCT rg.defaultSciName
FROM ReferenceGenome rg
WHERE defaultSciName IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM ScientificName sn WHERE sn.alias = rg.defaultSciName);

ALTER TABLE Sample ADD COLUMN scientificNameId bigint;
ALTER TABLE Sample ADD CONSTRAINT fk_sample_scientificName FOREIGN KEY (scientificNameId) REFERENCES ScientificName (scientificNameId); 
UPDATE Sample s SET s.scientificNameId = (SELECT scientificNameId FROM ScientificName WHERE alias = s.scientificName);
ALTER TABLE Sample MODIFY COLUMN scientificNameId bigint NOT NULL;
ALTER TABLE Sample DROP COLUMN scientificName;

ALTER TABLE ReferenceGenome ADD COLUMN defaultScientificNameId bigint;
ALTER TABLE ReferenceGenome ADD CONSTRAINT fk_referenceGenome_defaultScientificName FOREIGN KEY (defaultScientificNameId) REFERENCES ScientificName (scientificNameId);
UPDATE ReferenceGenome rg SET rg.defaultScientificNameId = (SELECT scientificNameId FROM ScientificName WHERE alias = rg.defaultSciName);
ALTER TABLE ReferenceGenome DROP COLUMN defaultSciName;

-- user_group_constraints
DELETE FROM User_Group
WHERE users_userId NOT IN (SELECT userId FROM User)
OR groups_groupId NOT IN (SELECT groupId FROM _Group);

ALTER TABLE User_Group ADD CONSTRAINT fk_user_group_user FOREIGN KEY (users_userId) REFERENCES User (userId);
ALTER TABLE User_Group ADD CONSTRAINT fk_user_group_group FOREIGN KEY (groups_groupId) REFERENCES _Group (groupId);

