SELECT NAME 
        ,COUNT(*) count 
        ,COUNT(CASE  
                WHEN archived = true 
                        THEN archived 
                END) archivedCount 
        ,MIN(created) earliest 
        ,MAX(updated) latest 
FROM ( 
        SELECT COALESCE(sp.shortName, sp.alias) NAME 
                ,sai.archived archived 
                ,s.created created 
                ,s.lastModified updated 
        FROM Sample s 
        LEFT JOIN DetailedSample sai ON sai.sampleId = s.sampleId 
        INNER JOIN Project sp ON sp.projectId = s.project_projectId 
         
UNION ALL 
         
        SELECT COALESCE(lp.shortName, lp.alias) NAME 
                ,lai.archived archived 
                ,l.created created 
                ,l.lastModified updated
        FROM Library l
        LEFT JOIN DetailedLibrary lai ON lai.libraryId = l.libraryId
        INNER JOIN Sample ls ON l.sample_sampleId = ls.sampleId 
        INNER JOIN Project lp ON lp.projectId = ls.project_projectId 
        ) combined 
GROUP BY NAME
