-- requisitionId
ALTER TABLE Sample ADD COLUMN requisitionId varchar(50);

-- qcs
-- StartNoTest
ALTER TABLE QCType DROP INDEX uk_qcType_byTarget;
-- EndNoTest


CREATE TABLE QcControl (
  controlId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  qcTypeId bigint(20) NOT NULL,
  alias varchar(100) NOT NULL,
  CONSTRAINT fk_qcControl_qcType FOREIGN KEY (qcTypeId) REFERENCES QCType (qcTypeId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE QCType ADD COLUMN instrumentModelId bigint(20);
ALTER TABLE QCType ADD CONSTRAINT fk_qcType_instrumentModel FOREIGN KEY (instrumentModelId) REFERENCES InstrumentModel (instrumentModelId);
ALTER TABLE QCType ADD COLUMN kitDescriptorId bigint(20);
ALTER TABLE QCType ADD CONSTRAINT fk_qcType_kitDescriptor FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);

ALTER TABLE SampleQC ADD COLUMN instrumentId bigint(20);
ALTER TABLE SampleQC ADD CONSTRAINT fk_sampleQc_instrument FOREIGN KEY (instrumentId) REFERENCES Instrument (instrumentId);
ALTER TABLE SampleQC ADD COLUMN kitLot varchar(50);

CREATE TABLE SampleQcControl (
  qcControlId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  qcId bigint(20) NOT NULL,
  controlId bigint(20) NOT NULL,
  lot varchar(50) NOT NULL,
  qcPassed BOOLEAN NOT NULL,
  CONSTRAINT fk_sampleQcControl_qc FOREIGN KEY (qcId) REFERENCES SampleQC (qcId),
  CONSTRAINT fk_sampleQcControl_control FOREIGN KEY (controlId) REFERENCES QcControl (controlId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE LibraryQC ADD COLUMN instrumentId bigint(20);
ALTER TABLE LibraryQC ADD CONSTRAINT fk_libraryQc_instrument FOREIGN KEY (instrumentId) REFERENCES Instrument (instrumentId);
ALTER TABLE LibraryQC ADD COLUMN kitLot varchar(50);

CREATE TABLE LibraryQcControl (
  qcControlId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  qcId bigint(20) NOT NULL,
  controlId bigint(20) NOT NULL,
  lot varchar(50) NOT NULL,
  qcPassed BOOLEAN NOT NULL,
  CONSTRAINT fk_libraryQcControl_qc FOREIGN KEY (qcId) REFERENCES LibraryQC (qcId),
  CONSTRAINT fk_libraryQcControl_control FOREIGN KEY (controlId) REFERENCES QcControl (controlId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE PoolQC ADD COLUMN instrumentId bigint(20);
ALTER TABLE PoolQC ADD CONSTRAINT fk_poolQc_instrument FOREIGN KEY (instrumentId) REFERENCES Instrument (instrumentId);
ALTER TABLE PoolQC ADD COLUMN kitLot varchar(50);

CREATE TABLE PoolQcControl (
  qcControlId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  qcId bigint(20) NOT NULL,
  controlId bigint(20) NOT NULL,
  lot varchar(50) NOT NULL,
  qcPassed BOOLEAN NOT NULL,
  CONSTRAINT fk_poolQcControl_qc FOREIGN KEY (qcId) REFERENCES PoolQC (qcId),
  CONSTRAINT fk_poolQcControl_control FOREIGN KEY (controlId) REFERENCES QcControl (controlId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE ContainerQC ADD COLUMN instrumentId bigint(20);
ALTER TABLE ContainerQC ADD CONSTRAINT fk_containerQc_instrument FOREIGN KEY (instrumentId) REFERENCES Instrument (instrumentId);
ALTER TABLE ContainerQC ADD COLUMN kitLot varchar(50);

CREATE TABLE ContainerQcControl (
  qcControlId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  qcId bigint(20) NOT NULL,
  controlId bigint(20) NOT NULL,
  lot varchar(50) NOT NULL,
  qcPassed BOOLEAN NOT NULL,
  CONSTRAINT fk_containerQcControl_qc FOREIGN KEY (qcId) REFERENCES ContainerQC (qcId),
  CONSTRAINT fk_containerQcControl_control FOREIGN KEY (controlId) REFERENCES QcControl (controlId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE QCType MODIFY COLUMN description varchar(255);
ALTER TABLE QCType MODIFY COLUMN units varchar(20);

