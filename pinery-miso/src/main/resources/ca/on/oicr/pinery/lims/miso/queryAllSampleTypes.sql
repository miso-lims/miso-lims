SELECT sc.alias NAME 
        ,'Sample' miso_type 
        ,NULL sampleType_platform 
        ,NULL sampleType_description 
        ,COUNT(*) count 
        ,COUNT(CASE WHEN sai.archived = true THEN sai.archived END) archivedCount 
        ,MIN(scl.creationDate) earliest 
        ,MAX(scl.lastUpdated) latest 
FROM DetailedSample sai 
JOIN SampleClass sc ON sc.sampleClassId = sai.sampleClassId 
LEFT JOIN ( 
        SELECT sampleId, MAX(changeTime) lastUpdated, MIN(changeTime) creationDate  
        FROM SampleChangeLog GROUP BY sampleId 
        ) scl ON sai.sampleId = scl.sampleId 
GROUP BY sai.sampleClassId 
 
UNION 
 
SELECT NULL NAME 
        ,'Library' miso_type 
        ,lt.platformType sampleType_platform 
        ,lt.description sampleType_description 
        ,COUNT(*) count 
        ,COUNT(CASE WHEN lai.archived = true THEN lai.archived END) archivedCount 
        ,MIN(l.creationDate) earliest 
        ,MAX(lcl.lastUpdated) latest 
FROM Library l 
JOIN DetailedLibrary lai ON lai.libraryId = l.libraryId 
JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType 
LEFT JOIN ( 
        SELECT libraryId, MAX(changeTime) lastUpdated 
        FROM LibraryChangeLog GROUP BY libraryId 
        ) lcl ON l.libraryId = lcl.libraryId 
GROUP BY l.libraryType 
 
UNION 
 
SELECT NULL NAME 
        ,'Dilution' miso_type 
        ,lt.platformType sampleType_platform 
        ,lt.description sampleType_description 
        ,COUNT(*) count 
        ,0 archivedCount 
        ,MIN(d.creationDate) earliest 
        ,MAX(d.lastUpdated) latest 
FROM LibraryDilution d 
LEFT JOIN Library l ON l.libraryId = d.library_libraryId 
JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType 
GROUP BY l.libraryType
