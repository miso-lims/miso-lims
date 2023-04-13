-- requisitions
CREATE TABLE Metric(
  metricId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  category varchar(50) NOT NULL,
  thresholdType varchar(20) NOT NULL,
  units varchar(20),
  PRIMARY KEY (metricId),
  CONSTRAINT uk_metric_alias_category UNIQUE (alias, category) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Assay(
  assayId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  version varchar(50) NOT NULL,
  description varchar(255),
  archived BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (assayId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Assay_Metric(
  assayId bigint NOT NULL,
  metricId bigint NOT NULL,
  minimumThreshold DECIMAL(13,3),
  maximumThreshold DECIMAL(13,3),
  PRIMARY KEY (assayId, metricId),
  CONSTRAINT fk_assay_metric FOREIGN KEY (metricId) REFERENCES Metric(metricId),
  CONSTRAINT fk_metric_assay FOREIGN KEY (assayId) REFERENCES Assay(assayId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Requisition(
  requisitionId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  assayId bigint,
  stopped BOOLEAN NOT NULL DEFAULT FALSE,
  creator bigint NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModifier bigint NOT NULL,
  lastModified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (requisitionId),
  CONSTRAINT uk_requisition_alias UNIQUE (alias),
  CONSTRAINT fk_requisition_assay FOREIGN KEY (assayId) REFERENCES Assay(assayId),
  CONSTRAINT fk_requisition_creator FOREIGN KEY (creator) REFERENCES User (userId),
  CONSTRAINT fk_requisition_modifier FOREIGN KEY (lastModifier) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE RequisitionQc(
  qcId bigint NOT NULL AUTO_INCREMENT,
  requisitionId bigint NOT NULL,
  creator bigint NOT NULL,
  `date` DATE NOT NULL,
  type bigint NOT NULL,
  results DECIMAL(16,10) NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  description varchar(255),
  instrumentId bigint,
  kitLot varchar(50),
  kitDescriptorId bigint,
  PRIMARY KEY (qcId),
  CONSTRAINT fk_requisitionQc_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition(requisitionId),
  CONSTRAINT fk_requisitionQc_creator FOREIGN KEY (creator) REFERENCES User(userId),
  CONSTRAINT fk_requisitionQc_type FOREIGN KEY (type) REFERENCES QCType(qcTypeId),
  CONSTRAINT fk_requisitionQc_kit FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor(kitDescriptorId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE RequisitionQcControl (
  qcControlId bigint PRIMARY KEY AUTO_INCREMENT,
  qcId bigint NOT NULL,
  controlId bigint NOT NULL,
  lot varchar(50) NOT NULL,
  qcPassed BOOLEAN NOT NULL,
  CONSTRAINT fk_requisitionQcControl_qc FOREIGN KEY (qcId) REFERENCES RequisitionQc (qcId),
  CONSTRAINT fk_requisitionQcControl_control FOREIGN KEY (controlId) REFERENCES QcControl (controlId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Sample
  CHANGE COLUMN requisitionId requisitionAlias varchar(50),
  ADD COLUMN requisitionId bigint,
  ADD CONSTRAINT fk_sample_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition(requisitionId);

CREATE TABLE TempRequisition (
  tempId int PRIMARY KEY AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  sampleId bigint NOT NULL,
  creator bigint NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModifier bigint NOT NULL,
  lastModified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO TempRequisition (alias, sampleId, creator, created, lastModifier, lastModified)
SELECT requisitionAlias, sampleId, creator, created, lastModifier, lastModified
FROM Sample
WHERE requisitionAlias IS NOT NULL;

DELETE FROM TempRequisition
WHERE EXISTS (
  SELECT 1 FROM Sample other
  WHERE other.requisitionAlias = TempRequisition.alias
  AND (
    other.created < TempRequisition.created
    OR (other.created = TempRequisition.created AND other.sampleId < TempRequisition.sampleId)
  )
);

INSERT INTO Requisition (alias, creator, created, lastModifier, lastModified)
SELECT alias, creator, created, lastModifier, lastModified
FROM TempRequisition;

DROP TABLE TempRequisition;

UPDATE Sample s
SET s.requisitionId = (SELECT requisitionId FROM Requisition r WHERE r.alias = s.requisitionAlias)
WHERE s.requisitionAlias IS NOT NULL;

ALTER TABLE Sample DROP COLUMN requisitionAlias;

CREATE TABLE RequisitionChangeLog (
  requisitionChangeLogId bigint NOT NULL AUTO_INCREMENT,
  requisitionId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (requisitionChangeLogId),
  CONSTRAINT fk_requisitionChangeLog_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition(requisitionId),
  CONSTRAINT fk_requisitionChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO RequisitionChangeLog (requisitionId, columnsChanged, userId, message, changeTime)
SELECT requisitionId, '', creator, 'Requisition created', created FROM Requisition;

CREATE TABLE Requisition_Attachment (
  requisitionId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (requisitionId, attachmentId),
  CONSTRAINT fk_attachment_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition (requisitionId),
  CONSTRAINT fk_requisition_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Requisition_Note (
  requisitionId bigint NOT NULL,
  noteId bigInt NOT NULL,
  PRIMARY KEY (requisitionId, noteId),
  CONSTRAINT fk_note_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition (requisitionId),
  CONSTRAINT fk_requisition_note FOREIGN KEY (noteId) REFERENCES Note (noteId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

