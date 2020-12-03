SELECT
  COALESCE(proj.shortName, proj.alias) NAME,
  COALESCE(count, 0) count,
  COALESCE(archivedCount, 0) archivedCount,
  earliest,
  latest,
  status IN ('ACTIVE', 'PENDING') active,
  pipe.alias = 'Clinical' OR pipe.alias LIKE 'Accredited%' clinical,
  pipe.alias pipeline,
  secondaryNaming secondaryNamingScheme
FROM Project proj
JOIN Pipeline pipe ON pipe.pipelineId = proj.pipelineId
LEFT JOIN (
  SELECT
    project_projectId,
    COUNT(*) count,
    COUNT(CASE WHEN archived = true THEN archived END) archivedCount,
    MIN(created) earliest,
    MAX(lastModified) latest
  FROM (
    SELECT project_projectId, archived, created, lastModified
    FROM Sample
    
    UNION ALL
    
    SELECT project_projectId, l.archived, l.created, l.lastModified
    FROM Library l
    JOIN Sample s ON s.sampleId = l.sample_sampleId
  ) items
  GROUP BY project_projectId
) stats ON stats.project_projectId = proj.projectId
