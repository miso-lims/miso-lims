Configuration options to hide columns on bulk edit/create pages. The following `miso.properties`
settings can be set to `false` to hide their respective column groups (all default to `true`):
* `miso.display.bulk.receiptQc` - Receipt Confirmed, Receipt QC Passed, Receipt QC Note
* `miso.display.bulk.requisition` - Requisition Alias, Requisition, Assay
* `miso.display.bulk.qcStatus` - QC Status, QC Note
* `miso.display.bulk.matrixBarcode` - Matrix Barcode
* `miso.display.bulk.discarded` - Discarded
* `miso.display.bulk.defaultDetailedQcStatus` - when `qcStatus` is hidden, sets the default QC
  status value (should match a configured QC status description)
