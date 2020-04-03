SELECT NAME 
        ,COUNT(*) count 
        ,COUNT(CASE  
                WHEN archived = true 
                        THEN archived 
                END) archivedCount 
        ,MIN(created) earliest 
        ,MAX(updated) latest 
        ,MAX(active) active 
        ,MAX(clinical) clinical
        ,MAX(secondaryNaming) secondaryNamingScheme
FROM ( 
        SELECT COALESCE(sp.shortName, sp.alias) NAME 
                ,s.archived archived 
                ,s.created created 
                ,s.lastModified updated 
                ,sp.status IN ('ACTIVE', 'PENDING') active 
                ,sp.clinical clinical
                ,sp.secondaryNaming secondaryNaming
        FROM Sample s
        INNER JOIN Project sp ON sp.projectId = s.project_projectId 
         
UNION ALL 
         
        SELECT COALESCE(lp.shortName, lp.alias) NAME 
                ,l.archived archived 
                ,l.created created 
                ,l.lastModified updated
                ,lp.status IN ('ACTIVE', 'PENDING') active
                ,lp.clinical clinical
                ,lp.secondaryNaming secondaryNaming
        FROM Library l
        INNER JOIN Sample ls ON l.sample_sampleId = ls.sampleId 
        INNER JOIN Project lp ON lp.projectId = ls.project_projectId 
        ) combined 
GROUP BY NAME
