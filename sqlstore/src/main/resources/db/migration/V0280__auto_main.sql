-- refactor_pairedEnd_runs

ALTER TABLE RunIllumina ADD COLUMN pairedEnd tinyint NOT NULL DEFAULT '1';
ALTER TABLE RunLS454 ADD COLUMN pairedEnd tinyint NOT NULL DEFAULT '1';

CREATE TABLE RunSolid(
  runId bigint NOT NULL,
  pairedEnd tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`runId`),
  CONSTRAINT runsolid_run_runid FOREIGN KEY (runId) REFERENCES Run (runId)
) ENGINE=InnoDB CHARSET=utf8;

INSERT INTO RunSolid (runId, pairedEnd) SELECT runId, pairedEnd FROM Run WHERE 
  sequencerReference_sequencerReferenceId IN (SELECT sr.referenceId FROM SequencerReference sr 
  JOIN Platform p ON sr.platformId = p.platformId WHERE p.name = 'SOLID');
UPDATE RunIllumina SET pairedEnd = (SELECT pairedEnd FROM Run WHERE RunIllumina.runId = Run.runId);
UPDATE RunLS454 SET pairedEnd = (SELECT pairedEnd FROM Run WHERE RunLS454.runId = Run.runId);

ALTER TABLE Run DROP COLUMN pairedEnd;
ALTER TABLE RunPacBio DROP COLUMN creationDate;


-- fix_detailed_qc

UPDATE Sample
  SET qcPassed = (SELECT status FROM DetailedQcStatus JOIN DetailedSample ON DetailedQcStatus.detailedQcStatusId = DetailedSample.detailedQcStatusId WHERE DetailedSample.sampleId = Sample.sampleId)
  WHERE sampleId IN (SELECT sampleId FROM DetailedSample WHERE detailedQcStatusId IS NOT NULL);


-- merge_slides

CREATE TABLE StainCategory (
  stainCategoryId bigint NOT NULL AUTO_INCREMENT,
  name varchar(20) NOT NULL,
  PRIMARY KEY (stainCategoryId),
  CONSTRAINT staincategory_name UNIQUE(name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Stain (
  stainId bigint NOT NULL AUTO_INCREMENT,
  stainCategoryId bigint DEFAULT NULL,
  name varchar(20) NOT NULL,
  PRIMARY KEY (stainId),
  CONSTRAINT stain_name UNIQUE(name),
  CONSTRAINT stain_staincategory FOREIGN KEY (stainCategoryId) REFERENCES StainCategory (stainCategoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Stain(name) VALUES ('Cresyl Violet'), ('Hematoxylin+Eosin');

CREATE TABLE SampleSlide(
  sampleId bigint NOT NULL,
  slides int NOT NULL DEFAULT '0',
  discards int DEFAULT '0',
  thickness int DEFAULT NULL,
  stain bigint DEFAULT NULL,
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


