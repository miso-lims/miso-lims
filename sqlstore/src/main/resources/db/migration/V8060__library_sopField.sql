CREATE TABLE LibrarySopFieldValue (
  libraryId BIGINT NOT NULL,
  sopFieldId BIGINT NOT NULL,
  value VARCHAR(255),
  PRIMARY KEY (libraryId, sopFieldId),
  CONSTRAINT fk_librarySopFieldValue_library FOREIGN KEY (libraryId) REFERENCES Library(libraryId),
  CONSTRAINT fk_librarySopFieldValue_sopField FOREIGN KEY (sopFieldId) REFERENCES SopField(sopFieldId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
