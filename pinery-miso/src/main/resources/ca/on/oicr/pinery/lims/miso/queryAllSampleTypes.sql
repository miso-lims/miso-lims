SELECT sc.alias NAME 
        ,'Sample' miso_type 
        ,NULL sampleType_platform 
        ,NULL sampleType_description 
        ,COUNT(*) count 
        ,COUNT(CASE WHEN s.archived = true THEN s.archived END) archivedCount 
        ,MIN(scl.creationDate) earliest 
        ,MAX(scl.lastUpdated) latest 
FROM Sample s
LEFT JOIN SampleClass sc ON sc.sampleClassId = s.sampleClassId 
LEFT JOIN ( 
        SELECT sampleId, MAX(changeTime) lastUpdated, MIN(changeTime) creationDate  
        FROM SampleChangeLog GROUP BY sampleId 
        ) scl ON s.sampleId = scl.sampleId 
GROUP BY s.sampleClassId 
 
UNION 
 
SELECT NULL NAME 
        ,'Library' miso_type 
        ,lt.platformType sampleType_platform 
        ,lt.description sampleType_description 
        ,COUNT(*) count 
        ,COUNT(CASE WHEN l.archived = true THEN l.archived END) archivedCount 
        ,MIN(l.creationDate) earliest 
        ,MAX(lcl.lastUpdated) latest 
FROM Library l
INNER JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType
LEFT JOIN ( 
        SELECT libraryId, MAX(changeTime) lastUpdated 
        FROM LibraryChangeLog GROUP BY libraryId 
        ) lcl ON l.libraryId = lcl.libraryId 
GROUP BY l.libraryType 
 
UNION 
 
SELECT NULL NAME 
        ,'Library Aliquot' miso_type 
        ,lt.platformType sampleType_platform 
        ,lt.description sampleType_description 
        ,COUNT(*) count 
        ,0 archivedCount 
        ,MIN(d.creationDate) earliest 
        ,MAX(d.lastUpdated) latest 
FROM LibraryAliquot d 
INNER JOIN Library l ON l.libraryId = d.libraryId 
INNER JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType 
GROUP BY l.libraryType
