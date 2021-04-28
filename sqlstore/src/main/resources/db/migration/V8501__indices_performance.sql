ALTER TABLE Indices ADD COLUMN realSequences varchar(255);

UPDATE Indices ind
JOIN Index_RealSequences r ON r.indexId = ind.indexId
SET realSequences = GROUP_CONCAT(r.sequence ORDER BY r.sequence SEPARATOR ',')
GROUP BY r.indexId;

UPDATE Indices
SET realSequences = (
  SELECT GROUP_CONCAT(sequence ORDER BY sequence SEPARATOR ',')
  FROM Index_RealSequences
  WHERE indexId = Indices.indexId
)
WHERE EXISTS (SELECT 1 FROM Index_RealSequences WHERE indexId = Indices.indexId);

DROP TABLE Index_RealSequences;
