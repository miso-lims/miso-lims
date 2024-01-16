SELECT s.name AS sampleId, link.requisitionId
FROM Requisition_SupplementalSample link
JOIN Sample s ON s.sampleId = link.sampleId

UNION ALL

SELECT l.name AS sampleId, link.requisitionId
FROM Requisition_SupplementalLibrary link
JOIN Library l ON l.libraryId = link.libraryId
