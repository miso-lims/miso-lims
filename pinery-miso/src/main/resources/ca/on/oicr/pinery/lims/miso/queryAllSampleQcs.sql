SELECT qc.qcId, qc.date, s.name sampleId, qt.name qcType, qc.results
FROM SampleQC qc
JOIN QCType qt ON qt.qcTypeId = qc.type
JOIN Sample s ON s.sampleId = qc.sample_sampleId
WHERE qc.qcId IN (
  SELECT MAX(qc.qcId) qcId FROM SampleQC qc
  JOIN (
    SELECT sample_sampleId, type, MAX(date) maxDate FROM SampleQC GROUP BY sample_sampleId, type
  ) maxDates ON maxDates.sample_sampleId = qc.sample_sampleId AND maxDates.type = qc.type
  WHERE qc.date = maxDates.maxDate
  GROUP BY qc.sample_sampleId, qc.type
)

UNION ALL

SELECT qc.qcId, qc.date, l.name sampleId, qt.name, qc.results
FROM LibraryQC qc
JOIN QCType qt ON qt.qcTypeId = qc.type
JOIN Library l ON l.libraryId = qc.library_libraryId
WHERE qc.qcId IN (
  SELECT MAX(qc.qcId) qcId FROM LibraryQC qc
  JOIN (
    SELECT library_libraryId, type, MAX(date) maxDate FROM LibraryQC GROUP BY library_libraryId, type
  ) maxDates ON maxDates.library_libraryId = qc.library_libraryId AND maxDates.type = qc.type
  WHERE qc.date = maxDates.maxDate
  GROUP BY qc.library_libraryId, qc.type
)

-- Order is important; see QcConverter.java
ORDER BY date DESC, qcId DESC
