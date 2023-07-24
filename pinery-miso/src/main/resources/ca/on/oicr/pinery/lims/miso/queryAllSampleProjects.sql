SELECT
  COALESCE(proj.code, proj.title) NAME,
  COALESCE(stats.count, 0) count,
  COALESCE(stats.archivedCount, 0) archivedCount,
  stats.earliest,
  stats.latest,
  proj.status IN ('ACTIVE', 'PENDING') active,
  pipe.alias pipeline,
  proj.secondaryNaming secondaryNamingScheme,
  proj.created,
  proj.rebNumber,
  proj.rebExpiry,
  proj.description,
  proj.samplesExpected,
  c.name contactName,
  c.email contactEmail
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
    
    UNION ALL
    
    SELECT project_projectId, FALSE, la.created, la.lastUpdated
    FROM LibraryAliquot la
    JOIN Library l ON l.libraryId = la.libraryId
    JOIN Sample s ON s.sampleId = l.sample_sampleId
  ) items
  GROUP BY project_projectId
) stats ON stats.project_projectId = proj.projectId
LEFT JOIN (
  SELECT
    pc.projectId,
    GROUP_CONCAT(c1.name ORDER BY c1.name DESC SEPARATOR '; ') name,
    GROUP_CONCAT(c1.email ORDER BY c1.name DESC SEPARATOR '; ') email
  FROM Project_Contact pc 
  JOIN Contact c1 ON c1.contactId = pc.contactId WHERE pc.contactRoleId = 1
  GROUP BY pc.projectId
) c ON c.projectId = proj.projectId