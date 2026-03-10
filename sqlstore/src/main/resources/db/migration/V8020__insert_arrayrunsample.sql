INSERT INTO ArrayRun_Sample (arrayRunId, position, arrayId, sampleId, statusId, qcNote, qcUser, qcDate, lastModifier)
SELECT
    ar.arrayRunId,
    ap.position,
    ar.arrayId,
    ap.sampleId,
    NULL,
    NULL,
    NULL,
    NULL,
    ar.lastModifier
FROM ArrayRun ar
JOIN ArrayPosition ap on ap.arrayId = ar.arrayId
WHERE ar.arrayId IS NOT NULL
    AND NOT EXISTS (
        SELECT 1 FROM ArrayRun_Sample ars
        WHERE ars.arrayRunId = ar.arrayRunId
        AND ars.position = ap.position
    );
