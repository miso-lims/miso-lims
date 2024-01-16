SELECT name AS sampleId, requisitionId
FROM Sample
WHERE requisitionId IS NOT NULL

UNION ALL

SELECT name AS sampleId, requisitionId
FROM Library
WHERE requisitionId IS NOT NULL
