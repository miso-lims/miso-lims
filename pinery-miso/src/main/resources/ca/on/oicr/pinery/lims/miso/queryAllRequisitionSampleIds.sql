SELECT name AS sampleId, requisitionId
FROM Sample
WHERE requisitionId IS NOT NULL
