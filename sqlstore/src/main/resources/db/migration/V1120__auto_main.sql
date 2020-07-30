-- fix-label
UPDATE Printer SET layout = REPLACE(layout, '"2barcode"', '"2dbarcode"');

-- qc_results_required
ALTER TABLE SampleQC MODIFY COLUMN results decimal(16,10) NOT NULL;
ALTER TABLE LibraryQC MODIFY COLUMN results decimal(16,10) NOT NULL;
ALTER TABLE PoolQC MODIFY COLUMN results decimal(16,10) NOT NULL;
ALTER TABLE ContainerQC MODIFY COLUMN results decimal(16,10) NOT NULL;

