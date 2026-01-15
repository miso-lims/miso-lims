-- add_workset_pool
DROP TABLE IF EXISTS Workset_Pool;
CREATE TABLE Workset_Pool(
    worksetId BIGINT NOT NULL,
    poolId BIGINT NOT NULL,
    addedTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (worksetId, poolId),
    CONSTRAINT fk_worksetPool_workset
        FOREIGN KEY (worksetId) REFERENCES Workset(worksetId),
    CONSTRAINT fk_worksetPool_pool
        FOREIGN KEY (poolId) REFERENCES Pool(poolId)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- add_library_aliquot_attachment
CREATE TABLE Library_Aliquot_Attachment (
  aliquotId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (aliquotId, attachmentId),
  CONSTRAINT fk_attachment_library_aliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId),
  CONSTRAINT fk_library_aliquot_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

