SELECT s.name sampleId, scl.message action, scl.userId, scl.changeTime 
FROM SampleChangeLog scl 
JOIN Sample s ON s.sampleId = scl.sampleId 
UNION ALL 
SELECT l.name sampleId, lcl.message action, lcl.userId, lcl.changeTime 
FROM LibraryChangeLog lcl 
JOIN Library l ON l.libraryId = lcl.libraryId
