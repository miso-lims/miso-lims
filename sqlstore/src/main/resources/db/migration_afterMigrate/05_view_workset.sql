CREATE OR REPLACE VIEW ListWorksetView AS
SELECT
  w.worksetId,
  w.alias,
  (COALESCE(sam.count, 0) + COALESCE(lib.count, 0) + COALESCE(ali.count, 0)) AS itemCount,
  w.description,
  cat.alias AS category,
  stage.alias AS stage,
  w.creator,
  w.created,
  w.lastModifier,
  w.lastModified
FROM Workset w
LEFT JOIN WorksetCategory cat ON cat.categoryId = w.categoryId
LEFT JOIN WorksetStage stage ON stage.stageId = w.stageId
LEFT JOIN (
  SELECT worksetId, COUNT(sampleId) AS count
  FROM Workset_Sample
  GROUP BY worksetId
) sam ON sam.worksetId = w.worksetId
LEFT JOIN (
  SELECT worksetId, COUNT(libraryId) AS count
  FROM Workset_Library
  GROUP BY worksetId
) lib ON lib.worksetId = w.worksetId
LEFT JOIN (
  SELECT worksetId, COUNT(aliquotId) AS count
  FROM Workset_LibraryAliquot
  GROUP BY worksetId
) ali ON ali.worksetId = w.worksetId;
