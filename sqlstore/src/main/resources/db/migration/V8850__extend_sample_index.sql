ALTER TABLE SampleIndexFamily
  ADD COLUMN archived tinyint(1) NULL DEFAULT 0,
  ADD COLUMN uniqueDualIndex tinyint NULL DEFAULT 0;

ALTER TABLE SampleIndex
    ADD COLUMN sequence varchar(120) NULL,
    ADD COLUMN position int NOT NULL DEFAULT 1,
    MODIFY COLUMN name varchar(50) NOT NULL,
DROP INDEX uk_sampleIndex_family_name,
    ADD CONSTRAINT uk_sampleIndex_family_name_position UNIQUE (indexFamilyId, name, position);

ALTER TABLE Sample
    ADD COLUMN index2Id bigint NULL;