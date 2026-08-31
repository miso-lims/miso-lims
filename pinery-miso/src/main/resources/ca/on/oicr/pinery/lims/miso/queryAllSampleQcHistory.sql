SELECT qc.qcId, qc.date, s.name sampleId, qt.name qcType, qc.results, qt.units
FROM SampleQC qc
JOIN QCType qt ON qt.qcTypeId = qc.type
JOIN Sample s ON s.sampleId = qc.sample_sampleId

UNION ALL

SELECT qc.qcId, qc.date, l.name sampleId, qt.name, qc.results, qt.units
FROM LibraryQC qc
JOIN QCType qt ON qt.qcTypeId = qc.type
JOIN Library l ON l.libraryId = qc.library_libraryId

UNION ALL

SELECT qc.qcId, qc.date, la.name sampleId, qt.name, qc.results, qt.units
FROM LibraryAliquotQc qc
JOIN QCType qt ON qt.qcTypeId = qc.type
JOIN LibraryAliquot la ON la.aliquotId = qc.aliquotId

ORDER BY date DESC, qcId DESC
