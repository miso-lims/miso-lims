-- freezer_status
-- freezer_status
ALTER TABLE StorageLocation ADD COLUMN retired BOOLEAN NOT NULL DEFAULT 0;


-- assay_tests
CREATE TABLE AssayTest(
  testId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  tissueTypeId bigint,
  negateTissueType BOOLEAN NOT NULL DEFAULT FALSE,
  extractionClassId bigint,
  libraryDesignCodeId bigint,
  libraryQualificationMethod varchar(25) NOT NULL,
  libraryQualificationDesignCodeId bigint,
  repeatPerTimepoint BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (testId),
  CONSTRAINT uk_assayTest_alias UNIQUE (alias),
  CONSTRAINT fk_assayTest_tissueType FOREIGN KEY (tissueTypeId)
    REFERENCES TissueType (tissueTypeId),
  CONSTRAINT fk_assayTest_libraryDesignCode FOREIGN KEY (libraryDesignCodeId)
    REFERENCES LibraryDesignCode (libraryDesignCodeId),
  CONSTRAINT fk_assayTest_qualificationDesignCode FOREIGN KEY (libraryQualificationDesignCodeId)
    REFERENCES LibraryDesignCode (libraryDesignCodeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Assay_AssayTest(
  assayId bigint NOT NULL,
  testId bigint NOT NULL,
  PRIMARY KEY (assayId, testId),
  CONSTRAINT fk_assayTest_assay FOREIGN KEY (assayId) REFERENCES Assay (assayId),
  CONSTRAINT fk_assay_assayTest FOREIGN KEY (testId) REFERENCES AssayTest (testId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

