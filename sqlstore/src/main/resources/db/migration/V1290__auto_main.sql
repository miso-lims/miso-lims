-- indices_performance
ALTER TABLE Indices ADD COLUMN realSequences varchar(255);

UPDATE Indices
SET realSequences = (
  SELECT GROUP_CONCAT(sequence ORDER BY sequence SEPARATOR ',')
  FROM Index_RealSequences
  WHERE indexId = Indices.indexId
)
WHERE EXISTS (SELECT 1 FROM Index_RealSequences WHERE indexId = Indices.indexId);

DROP TABLE Index_RealSequences;

ALTER TABLE Library
  ADD COLUMN index1Id bigint,
  ADD COLUMN index2Id bigint,
  ADD CONSTRAINT fk_library_index1 FOREIGN KEY (index1id) REFERENCES Indices (indexId),
  ADD CONSTRAINT fk_library_index2 FOREIGN KEY (index2id) REFERENCES Indices (indexId);

UPDATE Library SET
index1Id = (
  SELECT link.index_indexId
  FROM Library_Index link
  JOIN Indices ind ON ind.indexId = link.index_indexId
  WHERE link.library_libraryId = Library.libraryId
  AND ind.position = 1
),
index2Id = (
  SELECT link.index_indexId
  FROM Library_Index link
  JOIN Indices ind ON ind.indexId = link.index_indexId
  WHERE link.library_libraryId = Library.libraryId
  AND ind.position = 2
);

DROP TABLE Library_Index;

