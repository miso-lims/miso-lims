CREATE TABLE Workset_Note (
  worksetId bigint NOT NULL,
  noteId bigint NOT NULL,
  PRIMARY KEY ( worksetId, noteId),
  CONSTRAINT fk_wotksetNote_workset FOREIGN KEY (worksetId) REFERENCES Workset (worksetId),
  CONSTRAINT fk_worksetNote_note FOREIGN KEY (noteId) REFERENCES Note (noteId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;