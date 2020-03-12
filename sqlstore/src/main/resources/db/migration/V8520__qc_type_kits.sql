ALTER TABLE QCType DROP FOREIGN KEY fk_qcType_kitDescriptor;

CREATE TABLE QCType_KitDescriptor (
  qcTypeId bigint(20) NOT NULL,
  kitDescriptorId bigint(20) NOT NULL,
  PRIMARY KEY (qcTypeId, kitDescriptorId),
  CONSTRAINT fk_kitDescriptor_qcType FOREIGN KEY (qcTypeId) REFERENCES QCType (qcTypeId),
  CONSTRAINT fk_qcType_kitDescriptor FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE SampleQC ADD COLUMN kitDescriptorId bigint(20);
ALTER TABLE SampleQC ADD CONSTRAINT fk_sampleQc_kit FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);

ALTER TABLE LibraryQC ADD COLUMN kitDescriptorId bigint(20);
ALTER TABLE LibraryQC ADD CONSTRAINT fk_libraryQc_kit FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);

ALTER TABLE PoolQC ADD COLUMN kitDescriptorId bigint(20);
ALTER TABLE PoolQC ADD CONSTRAINT fk_poolQc_kit FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);

ALTER TABLE ContainerQC ADD COLUMN kitDescriptorId bigint(20);
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
