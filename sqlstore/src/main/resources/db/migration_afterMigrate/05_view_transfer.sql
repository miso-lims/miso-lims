CREATE OR REPLACE VIEW PendingTransferGroupView AS
SELECT recipientGroupId AS groupId, COUNT(transferId) AS transfers
FROM Transfer t
WHERE recipientGroupId IS NOT NULL
AND (
  EXISTS (SELECT 1 FROM Transfer_Sample ts WHERE ts.transferId = t.transferId AND (received IS NULL OR qcPassed IS NULL))
  OR EXISTS (SELECT 1 FROM Transfer_Library tl WHERE tl.transferId = t.transferId AND (received IS NULL OR qcPassed IS NULL))
  OR EXISTS (SELECT 1 FROM Transfer_LibraryAliquot tla WHERE tla.transferId = t.transferId AND (received IS NULL OR qcPassed IS NULL))
  OR EXISTS (SELECT 1 FROM Transfer_Pool tp WHERE tp.transferId = t.transferId AND (received IS NULL OR qcPassed IS NULL))
)
GROUP BY recipientGroupId;

CREATE OR REPLACE VIEW ListTransferView AS
SELECT t.transferId,
  t.transferTime,
  t.senderLabId,
  t.senderGroupId,
  t.recipient,
  t.recipientGroupId,
  COALESCE(ts.items, 0) + COALESCE(tl.items, 0) + COALESCE(tla.items, 0) + COALESCE(tp.items, 0) AS items,
  COALESCE(ts.received, 0) + COALESCE(tl.received, 0) + COALESCE(tla.received, 0) + COALESCE(tp.received, 0) AS received,
  COALESCE(ts.receiptPending, 0) + COALESCE(tl.receiptPending, 0) + COALESCE(tla.receiptPending, 0) + COALESCE(tp.receiptPending, 0) AS receiptPending,
  COALESCE(ts.qcPassed, 0) + COALESCE(tl.qcPassed, 0) + COALESCE(tla.qcPassed, 0) + COALESCE(tp.qcPassed, 0) AS qcPassed,
  COALESCE(ts.qcPending, 0) + COALESCE(tl.qcPending, 0) + COALESCE(tla.qcPending, 0) + COALESCE(tp.qcPending, 0) AS qcPending
FROM Transfer t
LEFT JOIN (
  SELECT transferId,
    COUNT(sampleId) AS items,
    SUM(IF(received = TRUE, 1, 0)) AS received,
    SUM(IF(received IS NULL, 1, 0)) AS receiptPending,
    SUM(IF(qcPassed = TRUE, 1, 0)) AS qcPassed,
    SUM(IF(qcPassed IS NULL, 1, 0)) AS qcPending
  FROM Transfer_Sample
  GROUP BY transferId
) ts ON ts.transferId = t.transferId
LEFT JOIN (
  SELECT transferId,
    COUNT(libraryId) AS items,
    SUM(IF(received = TRUE, 1, 0)) AS received,
    SUM(IF(received IS NULL, 1, 0)) AS receiptPending,
    SUM(IF(qcPassed = TRUE, 1, 0)) AS qcPassed,
    SUM(IF(qcPassed IS NULL, 1, 0)) AS qcPending
  FROM Transfer_Library
  GROUP BY transferId
) tl ON tl.transferId = t.transferId
LEFT JOIN (
  SELECT transferId,
    COUNT(aliquotId) AS items,
    SUM(IF(received = TRUE, 1, 0)) AS received,
    SUM(IF(received IS NULL, 1, 0)) AS receiptPending,
    SUM(IF(qcPassed = TRUE, 1, 0)) AS qcPassed,
    SUM(IF(qcPassed IS NULL, 1, 0)) AS qcPending
  FROM Transfer_LibraryAliquot
  GROUP BY transferId
) tla ON tla.transferId = t.transferId
LEFT JOIN (
  SELECT transferId,
    COUNT(poolId) AS items,
    SUM(IF(received = TRUE, 1, 0)) AS received,
    SUM(IF(received IS NULL, 1, 0)) AS receiptPending,
    SUM(IF(qcPassed = TRUE, 1, 0)) AS qcPassed,
    SUM(IF(qcPassed IS NULL, 1, 0)) AS qcPending
  FROM Transfer_Pool
  GROUP BY transferId
) tp ON tp.transferId = t.transferId;
