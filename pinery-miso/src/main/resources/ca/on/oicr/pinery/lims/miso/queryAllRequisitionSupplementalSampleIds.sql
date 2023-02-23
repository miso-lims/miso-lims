SELECT s.name AS sampleId, rs.requisitionId
FROM Requisition_SupplementalSample rs
JOIN Sample s ON s.sampleId = rs.sampleId
