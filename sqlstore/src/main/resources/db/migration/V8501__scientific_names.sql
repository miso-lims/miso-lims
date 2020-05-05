CREATE TABLE ScientificName (
  scientificNameId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  PRIMARY KEY (scientificNameId),
  CONSTRAINT uk_scientificName_alias UNIQUE (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO ScientificName (alias)
SELECT DISTINCT scientificName FROM Sample;

INSERT INTO ScientificName (alias)
SELECT DISTINCT rg.defaultSciName
FROM ReferenceGenome rg
WHERE defaultSciName IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM ScientificName sn WHERE sn.alias = rg.defaultSciName);

ALTER TABLE Sample ADD COLUMN scientificNameId bigint(20);
ALTER TABLE Sample ADD CONSTRAINT fk_sample_scientificName FOREIGN KEY (scientificNameId) REFERENCES ScientificName (scientificNameId); 
UPDATE Sample s SET s.scientificNameId = (SELECT scientificNameId FROM ScientificName WHERE alias = s.scientificName);
ALTER TABLE Sample MODIFY COLUMN scientificNameId bigint(20) NOT NULL;
ALTER TABLE Sample DROP COLUMN scientificName;

ALTER TABLE ReferenceGenome ADD COLUMN defaultScientificNameId bigint(20);
ALTER TABLE ReferenceGenome ADD CONSTRAINT fk_referenceGenome_defaultScientificName FOREIGN KEY (defaultScientificNameId) REFERENCES ScientificName (scientificNameId);
UPDATE ReferenceGenome rg SET rg.defaultScientificNameId = (SELECT scientificNameId FROM ScientificName WHERE alias = rg.defaultSciName);
ALTER TABLE ReferenceGenome DROP COLUMN defaultSciName;
