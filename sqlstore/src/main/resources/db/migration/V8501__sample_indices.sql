RENAME TABLE IndexFamily TO LibraryIndexFamily;
RENAME TABLE Indices TO LibraryIndex;

CREATE TABLE SampleIndexFamily (
  indexFamilyId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (indexFamilyId),
  CONSTRAINT uk_sampleIndexFamily_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE SampleIndex (
  indexId bigint NOT NULL AUTO_INCREMENT,
  name varchar(24) NOT NULL,
  indexFamilyId bigint NOT NULL,
  PRIMARY KEY (indexId),
  CONSTRAINT fk_sampleIndex_sampleIndexFamily FOREIGN KEY (indexFamilyId)
    REFERENCES SampleIndexFamily(indexFamilyId),
  CONSTRAINT uk_sampleIndex_family_name UNIQUE (indexFamilyId, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
