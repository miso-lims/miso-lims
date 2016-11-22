--StartNoTest
UPDATE LibraryDesignCode SET description = 'ATAC-Seq' WHERE code = 'AS';
UPDATE LibraryDesignCode SET description = 'ChIP-Seq' WHERE code = 'CH';
UPDATE LibraryDesignCode SET description = 'Exome' WHERE code = 'EX';
UPDATE LibraryDesignCode SET description = 'mRNA' WHERE code = 'MR';
UPDATE LibraryDesignCode SET description = 'smRNA' WHERE code = 'SM';
UPDATE LibraryDesignCode SET description = 'Targeted Sequencing' WHERE code = 'TS';
UPDATE LibraryDesignCode SET description = 'Whole Genome' WHERE code = 'WG';
UPDATE LibraryDesignCode SET description = 'Whole Transcriptome' WHERE code = 'WT';
UPDATE LibraryDesignCode SET description = 'Total RNA' WHERE code = 'TR';
--EndNoTest