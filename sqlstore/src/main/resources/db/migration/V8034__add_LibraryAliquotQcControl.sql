CREATE TABLE LibraryAliquotQc(
    qcId BIGINT NOT NULL AUTO_INCREMENT,
    aliquotId BIGINT NOT NULL,

    creator BIGINT NOT NULL,
    date DATE NOT NULL,
    type BIGINT NOT NULL,
    results DECIMAL (16, 10) NOT NULL,
    created TIMESTAMP NOT NULL,
    lastModified TIMESTAMP NOT NULL,
    description VARCHAR(255),
    instrumentId BIGINT,
    kitLot VARCHAR(50),
    kitDescriptorId VARCHAR(255),

    PRIMARY KEY (qcId),

    CONSTRAINT fk_LibraryAliquotQc_libraryAliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId),
    CONSTRAINT fk_LibraryAliquotQc_creator FOREIGN KEY (creator) REFERENCES User (userId),
    CONSTRAINT  fk_LibraryAliquotQc_type FOREIGN KEY (type) REFERENCES QCType (qcTypeId),
    CONSTRAINT fk_LibraryAliquotQc_instrument FOREIGN KEY (instrumentId) REFERENCES Instrument (instrumentId)
)
Engine=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE LibraryAliquotQcControl (
  qcControlId bigint PRIMARY KEY AUTO_INCREMENT,
  qcId bigint NOT NULL,
  controlId bigint NOT NULL,
  lot varchar(50) NOT NULL,
  qcPassed BOOLEAN NOT NULL,
  CONSTRAINT fk_libraryAliquotQcControl_qc FOREIGN KEY (qcId) REFERENCES LibraryAliquotQc (qcId),
  CONSTRAINT fk_libraryAliquotQcControl_control FOREIGN KEY (controlId) REFERENCES QcControl (controlId)
) Engine=InnoDB DEFAULT CHARSET=utf8mb4;