CREATE TABLE AssayTest(
  testId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  tissueTypeId bigint(20),
  negateTissueType BOOLEAN NOT NULL DEFAULT FALSE,
  extractionClassId bigint(20),
  libraryDesignCodeId bigint(20),
  libraryQualificationMethod varchar(25) NOT NULL,
  libraryQualificationDesignCodeId bigint(20),
  repeatPerTimepoint BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (testId),
  CONSTRAINT uk_assayTest_alias UNIQUE (alias),
  CONSTRAINT fk_assayTest_tissueType FOREIGN KEY (tissueTypeId)
    REFERENCES TissueType (tissueTypeId),
  CONSTRAINT fk_assayTest_libraryDesignCode FOREIGN KEY (libraryDesignCodeId)
    REFERENCES LibraryDesignCode (libraryDesignCodeId),
  CONSTRAINT fk_assayTest_qualificationDesignCode FOREIGN KEY (libraryQualificationDesignCodeId)
    REFERENCES LibraryDesignCode (libraryDesignCodeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Assay_AssayTest(
  assayId bigint(20) NOT NULL,
  testId bigint(20) NOT NULL,
  PRIMARY KEY (assayId, testId),
  CONSTRAINT fk_assayTest_assay FOREIGN KEY (assayId) REFERENCES Assay (assayId),
  CONSTRAINT fk_assay_assayTest FOREIGN KEY (testId) REFERENCES AssayTest (testId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
