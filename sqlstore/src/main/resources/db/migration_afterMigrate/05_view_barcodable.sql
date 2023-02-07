CREATE OR REPLACE VIEW BarcodableView AS
  SELECT
    aliquotId AS targetId,
    identificationBarcode,
    name,
    alias       AS alias,
    'LIBRARY_ALIQUOT' AS targetType
  FROM LibraryAliquot
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
            FROM SequencerPartitionContainer
  UNION ALL SELECT
              sequencingContainerModelId,
              identificationBarcode,
              NULL              AS name,
              alias             AS alias,
              'CONTAINER_MODEL' AS targetType
            FROM SequencingContainerModel
  UNION ALL SELECT
              workstationId,
              identificationBarcode,
              NULL          AS name,
              alias         AS alias,
              'WORKSTATION' AS targetType
            FROM Workstation
  UNION ALL SELECT
              instrumentId,
              identificationBarcode,
              name         AS name,
              NULL         AS alias,
              'INSTRUMENT' AS targetType
            FROM Instrument;
