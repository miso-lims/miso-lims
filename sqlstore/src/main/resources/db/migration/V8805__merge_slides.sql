CREATE TABLE StainCategory (
  stainCategoryId bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(20) NOT NULL,
  PRIMARY KEY (stainCategoryId),
  CONSTRAINT staincategory_name UNIQUE(name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Stain (
  stainId bigint(20) NOT NULL AUTO_INCREMENT,
  stainCategoryId bigint(20) DEFAULT NULL,
  name varchar(20) NOT NULL,
  PRIMARY KEY (stainId),
  CONSTRAINT stain_name UNIQUE(name),
  CONSTRAINT stain_staincategory FOREIGN KEY (stainCategoryId) REFERENCES StainCategory (stainCategoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Stain(name) VALUES ('Cresyl Violet'), ('Hematoxylin+Eosin');

CREATE TABLE SampleSlide(
  sampleId bigint(20) NOT NULL,
  slides int(11) NOT NULL DEFAULT '0',
  discards int(11) DEFAULT '0',
  thickness int(11) DEFAULT NULL,
  stain bigint(20) DEFAULT NULL,
  PRIMARY KEY (sampleId),
  CONSTRAINT sampleSlide_sample_fkey FOREIGN KEY (sampleId) REFERENCES Sample (sampleId),
  CONSTRAINT sampleSlide_stain_fkey FOREIGN KEY (stain) REFERENCES Stain (stainId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO SampleSlide(sampleId, slides, discards, thickness, stain)
  SELECT sampleId, slides, discards, thickness, (SELECT stainId FROM Stain WHERE name = 'Cresyl Violet') FROM SampleCVSlide;

DROP TABLE SampleCVSlide;

INSERT INTO SampleSlide(sampleId, slides, discards, thickness, stain)
  SELECT sampleId, 1, 0, 0, (SELECT stainId FROM Stain WHERE name = 'Hematoxylin+Eosin') FROM DetailedSample WHERE sampleClassId = (SELECT sampleClassId FROM SampleClass WHERE alias = 'H E Slide' and SampleCategory = 'Tissue Processing');

INSERT INTO SampleClass(alias, sampleCategory, suffix, createdBy, creationDate, updatedBy, lastUpdated, dnaseTreatable)
  VALUES ('Slide', 'Tissue Processing', 'SL', (SELECT userId FROM User WHERE loginName = 'admin'), CURRENT_TIMESTAMP, (SELECT userId FROM User WHERE loginName = 'admin'), CURRENT_TIMESTAMP, FALSE);
SET @slideId = LAST_INSERT_ID();

INSERT INTO SampleValidRelationship(parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
  SELECT @slideId, childId, MIN(createdBy), MIN(creationDate), (SELECT userId FROM User WHERE loginName = 'admin'), CURRENT_TIMESTAMP, MIN(archived) FROM SampleValidRelationship
    WHERE parentId IN (SELECT sampleClassId FROM SampleClass WHERE alias IN ('CV Slide', 'H E Slide') and SampleCategory = 'Tissue Processing') GROUP BY childId
  UNION
  SELECT parentId, @slideId, MIN(createdBy), MIN(creationDate), (SELECT userId FROM User WHERE loginName = 'admin'), CURRENT_TIMESTAMP, MIN(archived) FROM SampleValidRelationship
    WHERE childId IN (SELECT sampleClassId FROM SampleClass WHERE alias IN ('CV Slide', 'H E Slide') and SampleCategory = 'Tissue Processing') GROUP BY parentId;

UPDATE DetailedSample SET sampleClassId = @slideID WHERE sampleClassId IN (SELECT sampleClassId FROM SampleClass WHERE alias IN ('CV Slide', 'H E Slide') and SampleCategory = 'Tissue Processing');

DELETE FROM SampleValidRelationship WHERE childId IN (SELECT sampleClassId FROM SampleClass WHERE alias IN ('CV Slide', 'H E Slide') and SampleCategory = 'Tissue Processing');
DELETE FROM SampleValidRelationship WHERE parentId IN (SELECT sampleClassId FROM SampleClass WHERE alias IN ('CV Slide', 'H E Slide') and SampleCategory = 'Tissue Processing');

DELETE FROM SampleClass WHERE alias IN ('CV Slide', 'H E Slide') and SampleCategory = 'Tissue Processing';
