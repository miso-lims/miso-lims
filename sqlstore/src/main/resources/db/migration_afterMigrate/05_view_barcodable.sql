CREATE OR REPLACE VIEW BarcodableView AS
  SELECT
    dilutionId AS targetId,
    identificationBarcode,
    name,
    NULL       AS alias,
    'DILUTION' AS targetType
  FROM LibraryDilution
  UNION ALL SELECT
              poolId,
              identificationBarcode,
              name,
              alias,
              'POOL' AS targetType
            FROM Pool
  UNION ALL SELECT
              sampleId,
              identificationBarcode,
              name,
              alias,
              'SAMPLE' AS targetType
            FROM Sample
  UNION ALL SELECT
              libraryId,
              identificationBarcode,
              name,
              alias,
              'LIBRARY' AS targetType
            FROM Library
  UNION ALL SELECT
              boxId,
              identificationBarcode,
              name,
              alias,
              'BOX' AS targetType
            FROM Box
  UNION ALL SELECT
              containerId,
              identificationBarcode,
              NULL        AS name,
              NULL        AS alias,
              'CONTAINER' AS targetType
            FROM SequencerPartitionContainer;
