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
