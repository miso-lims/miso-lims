-- changeloggables

ALTER TABLE Experiment ADD COLUMN creator bigint(20);
ALTER TABLE Experiment ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Experiment ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

-- StartNoTest
SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
UPDATE Experiment SET
  created = (SELECT MIN(changeTime) FROM ExperimentChangeLog WHERE experimentId = Experiment.experimentId),
  lastModified = (SELECT MAX(changeTime) FROM ExperimentChangeLog WHERE experimentId = Experiment.experimentId),
  creator = COALESCE((SELECT userId FROM ExperimentChangeLog WHERE experimentId = Experiment.experimentId ORDER BY changeTime ASC LIMIT 1), @admin);
-- EndNoTest

ALTER TABLE Experiment CHANGE COLUMN creator creator bigint(20) NOT NULL;
ALTER TABLE Experiment ADD CONSTRAINT fk_experiment_creator FOREIGN KEY (creator) REFERENCES User (userId);

ALTER TABLE KitDescriptor ADD COLUMN creator bigint(20);
ALTER TABLE KitDescriptor ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE KitDescriptor ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

-- StartNoTest
SET @now = NOW();
UPDATE KitDescriptor SET
  created = COALESCE((SELECT MIN(changeTime) FROM KitDescriptorChangeLog WHERE kitDescriptorId = KitDescriptor.kitDescriptorId), @now),
  lastModified = COALESCE((SELECT MAX(changeTime) FROM KitDescriptorChangeLog WHERE kitDescriptorId = KitDescriptor.kitDescriptorId), @now),
  creator = COALESCE((SELECT userId FROM KitDescriptorChangeLog WHERE kitDescriptorId = KitDescriptor.kitDescriptorId ORDER BY changeTime ASC LIMIT 1), @admin);
-- EndNoTest

ALTER TABLE KitDescriptor CHANGE COLUMN creator creator bigint(20) NOT NULL;
ALTER TABLE KitDescriptor ADD CONSTRAINT fk_kitDescriptor_creator FOREIGN KEY (creator) REFERENCES User (userId);

ALTER TABLE Study ADD COLUMN creator bigint(20);
ALTER TABLE Study ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Study ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

-- StartNoTest
UPDATE Study SET
  created = (SELECT MIN(changeTime) FROM StudyChangeLog WHERE studyId = Study.studyId),
  lastModified = (SELECT MAX(changeTime) FROM StudyChangeLog WHERE studyId = Study.studyId),
  creator = COALESCE((SELECT userId FROM StudyChangeLog WHERE studyId = Study.studyId ORDER BY changeTime ASC LIMIT 1), @admin);
-- EndNoTest

ALTER TABLE Study CHANGE COLUMN creator creator bigint(20) NOT NULL;
ALTER TABLE Study ADD CONSTRAINT fk_study_creator FOREIGN KEY (creator) REFERENCES User (userId);


