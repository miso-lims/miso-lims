-- fix_deletes
DROP TRIGGER IF EXISTS ArrayPositionDelete;

ALTER TABLE Experiment ADD CONSTRAINT fk_experiment_study FOREIGN KEY (study_studyId) REFERENCES Study (studyId);
ALTER TABLE Experiment ADD CONSTRAINT fk_experiment_lastModifier FOREIGN KEY (lastModifier) REFERENCES User (userId);
ALTER TABLE Experiment_Kit ADD CONSTRAINT fk_experiment_kit FOREIGN KEY (kits_kitId) REFERENCES Kit (kitId);
ALTER TABLE Experiment_Kit ADD CONSTRAINT fk_experiment_kit_experiment FOREIGN KEY (experiments_experimentId) REFERENCES Experiment (experimentId);
ALTER TABLE Submission_Experiment ADD CONSTRAINT fk_submission_experiment FOREIGN KEY (experiments_experimentId) REFERENCES Experiment (experimentId);
ALTER TABLE Submission_Experiment ADD CONSTRAINT fk_submission_experiment_submission FOREIGN KEY (submission_submissionId) REFERENCES Submission (submissionId);

-- new_library_fields
CREATE TABLE Workstation (
  workstationId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  description varchar(255),
  PRIMARY KEY (workstationId),
  UNIQUE KEY uk_workstation_alias (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Library ADD COLUMN thermalCyclerId bigint;
ALTER TABLE Library ADD CONSTRAINT fk_library_thermalCycler FOREIGN KEY (thermalCyclerId) REFERENCES Instrument (instrumentId);
ALTER TABLE Library ADD COLUMN workstationId bigint;
ALTER TABLE Library ADD CONSTRAINT fk_library_workstation FOREIGN KEY (workstationId) REFERENCES Workstation (workstationId);

-- qc_type_kits
ALTER TABLE QCType DROP FOREIGN KEY fk_qcType_kitDescriptor;

CREATE TABLE QCType_KitDescriptor (
  qcTypeId bigint NOT NULL,
  kitDescriptorId bigint NOT NULL,
  PRIMARY KEY (qcTypeId, kitDescriptorId),
  CONSTRAINT fk_kitDescriptor_qcType FOREIGN KEY (qcTypeId) REFERENCES QCType (qcTypeId),
  CONSTRAINT fk_qcType_kitDescriptor FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE SampleQC ADD COLUMN kitDescriptorId bigint;
ALTER TABLE SampleQC ADD CONSTRAINT fk_sampleQc_kit FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);

ALTER TABLE LibraryQC ADD COLUMN kitDescriptorId bigint;
ALTER TABLE LibraryQC ADD CONSTRAINT fk_libraryQc_kit FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);

ALTER TABLE PoolQC ADD COLUMN kitDescriptorId bigint;
ALTER TABLE PoolQC ADD CONSTRAINT fk_poolQc_kit FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);

ALTER TABLE ContainerQC ADD COLUMN kitDescriptorId bigint;
ALTER TABLE ContainerQC ADD CONSTRAINT fk_containerQc_kit FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);

INSERT INTO QCType_KitDescriptor (qcTypeId, kitDescriptorId)
SELECT qcTypeId, kitDescriptorId
FROM QCType
WHERE kitDescriptorId IS NOT NULL;

UPDATE SampleQC
JOIN QCType ON QCType.qcTypeId = SampleQC.type
SET SampleQC.kitDescriptorId = QCType.kitDescriptorId;

UPDATE LibraryQC
JOIN QCType ON QCType.qcTypeId = LibraryQC.type
SET LibraryQC.kitDescriptorId = QCType.kitDescriptorId;

UPDATE PoolQC
JOIN QCType ON QCType.qcTypeId = PoolQC.type
SET PoolQC.kitDescriptorId = QCType.kitDescriptorId;

UPDATE ContainerQC
JOIN QCType ON QCType.qcTypeId = ContainerQC.type
SET ContainerQC.kitDescriptorId = QCType.kitDescriptorId;

ALTER TABLE QCType DROP COLUMN kitDescriptorId;

-- change_printers
ALTER TABLE Printer MODIFY layout varchar(2048) NOT NULL;
ALTER TABLE Printer ADD COLUMN height DOUBLE NOT NULL DEFAULT 0;
ALTER TABLE Printer ADD COLUMN width DOUBLE NOT NULL DEFAULT 0;

UPDATE Printer SET width = 8.3, height = 7.7 WHERE layout = 'AVERY_8363';
UPDATE Printer SET width = 12, height = 38 WHERE layout = 'BPT_635_488';
UPDATE Printer SET width = 14, height = 4.666 WHERE layout = 'JTT_183';
UPDATE Printer SET width = 8.3, height = 4.08 WHERE layout LIKE 'JTT_7%';
UPDATE Printer SET width = 41, height = 15 WHERE layout LIKE 'THT_155_490%';
UPDATE Printer SET width = 25, height = 25 WHERE layout = 'THT_179_492';
UPDATE Printer SET width = 22.86, height =  19.05 WHERE layout = 'FTT_152C1_1WH';
UPDATE Printer SET width = 38, height = 12 WHERE layout = 'THT_181_492_3';

UPDATE Printer SET layout = '[{"contents":{"use":"BARCODE"},"element":"2dbarcode","moduleSize":0.125,"x":1.75,"y":1.75},{"contents":{"use":"DATE"},"element":"text","height":0.8,"x":0.5,"y":7.125},{"contents":{"use":"ALIAS"},"element":"text","height":0.8,"lineLimit":15,"x":2.4,"y":7.125}]' WHERE layout = 'AVERY_8363';
UPDATE Printer SET layout = '[{"contents":{"use":"BARCODE_BASE64"},"element":"2dbarcode","height":0.21,"x":2,"y":6},{"contents":{"use":"BARCODE_BASE64"},"element":"2dbarcode","height":0.25,"x":13,"y":1},{"contents":{"use":"LABEL_TEXT"},"element":"text","height":1.4,"lineLimit":20,"x":29,"y":2},{"contents":{"use":"LABEL_TEXT"},"element":"text","height":2,"lineLimit":20,"x":17,"y":8},{"contents":{"use":"NAME"},"element":"text","height":2,"x":17,"y":11}]' WHERE layout = 'BPT_635_488';
UPDATE Printer SET layout = '[{"contents":{"use":"ALIAS"},"element":"textblock","height":0.5,"lineLimit":8,"rowLimit":2,"x":10.7,"y":2.3},{"contents":{"use":"ALIAS"},"element":"textblock","height":0.8,"lineLimit":18,"rowLimit":2,"x":1,"y":1.375},{"contents":[{"use":"DATE"},{"use":"DESCRIPTION"}],"element":"textblock","height":0.8,"lineLimit":12,"rowLimit":2,"x":1,"y":3.375},{"contents":{"use":"ALIAS"},"element":"2dbarcode","height":0.1,"x":6.78,"y":4}]' WHERE layout = 'JTT_183';
UPDATE Printer SET layout = '[{"contents":{"use":"ALIAS"},"element":"2dbarcode","moduleSize":0.1,"x":5.78,"y":3.8},{"contents":[{"use":"DATE"},{"use":"DESCRIPTION"}],"element":"textblock","height":0.8,"lineLimit":12,"rowLimit":2,"x":0.3,"y":3},{"contents":{"use":"ALIAS"},"element":"textblock","height":0.8,"lineLimit":18,"rowLimit":2,"x":0.3,"y":1}]' WHERE layout = 'JTT_7';
UPDATE Printer SET layout = '[{"contents":{"use":"ALIAS"},"element":"2dbarcode","moduleSize":0.1,"x":5.78,"y":3.8},{"contents":[{"use":"DATE"},{"use":"GROUP_DESC"}],"element":"textblock","height":0.8,"lineLimit":12,"rowLimit":2,"x":0.3,"y":3},{"contents":{"use":"ALIAS"},"element":"textblock","height":0.8,"lineLimit":18,"rowLimit":2,"x":0.3,"y":1}]' WHERE layout = 'JTT_7_GROUPDESC';
UPDATE Printer SET layout = '[{"contents":{"use":"ALIAS"},"element":"2dbarcode","moduleSize":0.1,"x":5.78,"y":3.8},{"contents":[{"use":"DATE"},{"use":"CONCENTRATION"}],"element":"textblock","height":0.8,"lineLimit":12,"rowLimit":2,"x":0.3,"y":3},{"contents":{"use":"ALIAS"},"element":"textblock","height":0.8,"lineLimit":18,"rowLimit":2,"x":0.3,"y":1}]' WHERE layout = 'JTT_7S';
UPDATE Printer SET layout = '[{"contents":{"use":"BARCODE"},"element":"2dbarcode","moduleSize":0.3,"x":2,"y":2},{"contents":{"use":"NAME"},"element":"text","height":3,"lineLimit":12,"style":"BOLD","x":7,"y":3},{"contents":[{"use":"LABEL_TEXT"},{"use":"CONCENTRATION"}],"element":"textblock","height":2,"lineLimit":28,"rowLimit":5,"x":7,"y":5}]' WHERE layout = 'THT_155_490';
UPDATE Printer SET layout = '[{"contents":{"use":"NAME"},"direction":"VERTICAL_UP","element":"text","height":2,"lineLimit":10,"x":9,"y":12},{"contents":[{"use":"LABEL_TEXT"},{"use":"CONCENTRATION"},{"use":"DESCRIPTION"}],"direction":"VERTICAL_UP","element":"textblock","height":2,"lineLimit":9,"rowLimit":7,"x":11,"y":12}]' WHERE layout = 'THT_155_490T';
UPDATE Printer SET layout = '[{"contents":{"use":"BARCODE"},"element":"2dbarcode","moduleSize":0.3,"x":11,"y":12},{"contents":[{"use":"LABEL_TEXT"},{"use":"DATE"}],"element":"textblock","height":2,"lineLimit":14,"rowLimit":3,"x":2,"y":4}]' WHERE layout = 'THT_179_492';
UPDATE Printer SET layout = '[{"contents":{"use":"BARCODE"},"element":"2barcode","moduleSize":0.3,"x":3,"y":12},{"contents":[{"use":"LABEL_TEXT"},{"use":"DATE"}],"element":"textblock","height":1.8,"lineLimit":14,"rowLimit":3,"x":0,"y":5}]' WHERE layout = 'FTT_152C1_1WH';
UPDATE Printer SET layout = '[{"contents":{"use":"BARCODE_BASE64"},"element":"2barcode","moduleSize":0.21,"x":3,"y":2},{"contents":{"use":"BARCODE_BASE64"},"element":"2barcode","moduleSize":0.25,"x":17,"y":1},{"contents":{"use":"NAME"},"element":"text","height":1.4,"lineLimit":17,"x":29,"y":2},{"contents":{"use":"ALIAS"},"element":"text","height":2,"style":"BOLD","x":17,"y":8},{"contents":{"use":"NAME"},"element":"text","height":2,"style":"BOLD","x":17,"y":11},{"contents":{"use":"ALIAS"},"element":"text","height":2,"lineLimit":17,"x":17,"y":8},{"contents":{"use":"NAME"},"element":"text","height":2,"style":"BOLD","x":17,"y":11}]' WHERE layout = 'THT_181_492_3';

