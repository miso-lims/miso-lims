ALTER TABLE LibraryAliquot ADD COLUMN alias varchar(100);
ALTER TABLE LibraryAliquot ADD COLUMN dnaSize bigint(20);

-- StartNoTest
UPDATE LibraryAliquot ali
JOIN Library lib ON lib.libraryId = ali.libraryId
SET ali.alias = lib.alias, ali.dnaSize = lib.dnaSize;
-- EndNoTest

ALTER TABLE LibraryAliquot MODIFY COLUMN alias varchar(100) NOT NULL;

CREATE TABLE DetailedLibraryAliquot (
  aliquotId bigint(20) NOT NULL,
  nonStandardAlias BOOLEAN NOT NULL DEFAULT FALSE,
  libraryDesignCodeId bigint(20) NOT NULL,
  groupId varchar(100),
  groupDescription varchar(255),
  PRIMARY KEY (aliquotId),
  CONSTRAINT fk_detailedLibraryAliquot_libraryAliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId),
  CONSTRAINT fk_detailedLibraryAliquot_libraryDesignCode FOREIGN KEY (libraryDesignCodeId) REFERENCES LibraryDesignCode (libraryDesignCodeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO DetailedLibraryAliquot (aliquotId, nonStandardAlias, libraryDesignCodeId)
SELECT ali.aliquotId, dl.nonStandardAlias, dl.libraryDesignCodeId
FROM LibraryAliquot ali
JOIN Library lib ON lib.libraryId = ali.libraryId
JOIN DetailedLibrary dl ON dl.libraryId = lib.libraryId;
