--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryAllSampleProjects//
CREATE PROCEDURE queryAllSampleProjects() BEGIN
  PREPARE stmt FROM 'SELECT NAME
      , COUNT(*) count
      , COUNT(
        CASE WHEN archived = true THEN archived END
      ) archivedCount
      , MIN(created) earliest
      , MAX(updated) latest
    FROM (
      SELECT sp.shortName NAME
        , sai.archived archived
        , scl.creationDate created
        , scl.lastUpdated updated
      FROM DetailedSample sai
      INNER JOIN Sample s ON s.sampleId = sai.sampleId
      INNER JOIN Project sp ON sp.projectId = s.project_projectId
      INNER JOIN (
        SELECT sampleId
          , MAX(changeTime) as lastUpdated
          , MIN(changeTime) as creationDate
        FROM SampleChangeLog
        GROUP BY sampleId
      ) scl ON s.sampleId = scl.sampleId

      UNION ALL

      SELECT lp.shortName NAME
        , lai.archived archived
        , lcl.creationDate created
        , lcl.lastUpdated updated
      FROM DetailedLibrary lai
      INNER JOIN Library l ON l.libraryId = lai.libraryId
      INNER JOIN Sample ls ON l.sample_sampleId = ls.sampleId
      INNER JOIN Project lp ON lp.projectId = ls.project_projectId
      INNER JOIN (
        SELECT libraryId
          , MAX(changeTime) as lastUpdated
          , MIN(changeTime) as creationDate
        FROM LibraryChangeLog
        GROUP BY libraryId
      ) lcl ON l.libraryId = lcl.libraryId
    ) COMBINED
    GROUP BY NAME';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest