INSERT INTO User (userId, active, admin, fullName, internal, loginName, roles, password, email) VALUES
(1,1,1,'admin',1,'admin','ROLE_ADMIN,ROLE_INTERNAL','{SHA-1}d033e22ae348aeb5660fc2140aec35850c4da997','admin@admin'), -- password 'admin'
(3,1,0,'user',1,'user','ROLE_INTERNAL','{bcrypt}$2a$10$vqYii//w2shSZnt/4uNyIeeU4FGQIB4QJeisv9l16xVRQ1lTOghIO','user@user.user'), -- password 'user'
(4,1,0,'Harry Henderson', 1, 'hhenderson', 'ROLE_INTERNAL', '{bcrypt}$2a$10$vqYii//w2shSZnt/4uNyIeeU4FGQIB4QJeisv9l16xVRQ1lTOghIO', 'hhenderson@somewhere.maybe');

INSERT INTO RunLibraryQcStatus(statusId, description, qcPassed) VALUES
(1, 'Passed', TRUE),
(2, 'Failed', FALSE),
(3, 'Top-up required', NULL),
(4, 'DeleteMe', FALSE);

INSERT INTO Pipeline(pipelineId, alias) VALUES
(1, 'Default'),
(2, 'Special'),
(3, 'Delete me');

INSERT INTO Sop(sopId, alias, version, category, url, archived) VALUES
(1, 'Sample SOP 1', '1.0', 'SAMPLE', 'http://sops.test.com/sample/1/1', FALSE),
(2, 'Sample SOP 2', '1.0', 'SAMPLE', 'http://sops.test.com/sample/2/1', FALSE),
(3, 'Library SOP 1', '1.0', 'LIBRARY', 'http://sops.test.com/library/1/1', FALSE),
(4, 'Library SOP 1', '2.0', 'LIBRARY', 'http://sops.test.com/library/1/2', FALSE),
(5, 'SOP to delete', '1.0', 'LIBRARY', 'http://sops.test.com/library/x/1', TRUE),
(6, 'Run SOP 1', '1.0', 'RUN', 'http://sops.test.com/run/1/1', FALSE);

INSERT INTO ScientificName(scientificNameId, alias) VALUES
(1, 'Homo sapiens'),
(2, 'Mus musculus'),
(3, 'Delete me'),
(4, 'Panthera tigris altaica'),
(5, 'Rattus rat');

INSERT INTO SequencingControlType (sequencingControlTypeId, alias) VALUES
(1, 'Positive'),
(2, 'Negative'),
(3, 'Delete Me');

INSERT INTO TissuePieceType (tissuePieceTypeId, abbreviation, name, archived) VALUES
(1, 'LCM' , 'LCM Tube', FALSE),
(2, 'UNU', 'Unused', FALSE),
(3, 'C', 'Curls', FALSE);

INSERT INTO PartitionQCType(partitionQcTypeId, description, noteRequired, orderFulfilled, analysisSkipped) VALUES
(1, 'OK', FALSE, TRUE, FALSE),
(2, 'OK''d by collaborator', FALSE, TRUE, FALSE),
(3, 'Failed: Sasquatch Problem', TRUE, FALSE, TRUE),
(4, 'Failed: Instrument problem', FALSE, FALSE, TRUE),
(5, 'Failed: Library preparation problem', FALSE, TRUE, TRUE),
(6, 'Failed: Analysis problem', FALSE, TRUE, TRUE),
(7, 'Failed: Other problem', TRUE, FALSE, TRUE);

INSERT INTO StudyType (typeId, name) VALUES
(1,'Other'),
(12,'RNASeq'),
(11,'Population Genomics'),
(10,'Cancer Genomics'),
(9,'Gene Regulation Study'),
(8,'Forensic or Paleo-genomics'),
(7,'Synthetic Genomics'),
(6,'Epigenetics'),
(5,'Resequencing'),
(4,'Transcriptome Analysis'),
(3,'Metagenomics'),
(2,'Whole Genome Sequencing');

INSERT INTO SampleType (typeId, name) VALUES
(2,'NON GENOMIC'),
(1,'GENOMIC'),
(5,'OTHER'),
(4,'VIRAL RNA'),
(3,'SYNTHETIC'),
(6,'TRANSCRIPTOMIC'),
(7,'METAGENOMIC'),
(8,'METATRANSCRIPTOMIC');

INSERT INTO Workstation(workstationId, alias, description) VALUES
(1, 'Workstation 1', 'first bench'),
(2, 'Workstation 2', 'second bench'),
(3, 'Workstation 3', 'third bench');

INSERT INTO AttachmentCategory(categoryId, alias) VALUES
(1, 'Submission Forms'),
(2, 'Tapestation'),
(3, 'Fragment Analyzer');

INSERT INTO RunPurpose(purposeId, alias) VALUES
(1, 'Production'),
(2, 'QC'),
(3, 'Unused');

INSERT INTO ReferenceGenome (referenceGenomeId, alias, defaultScientificNameId) VALUES
(1, 'Human hg19 random', 1),
(2, 'Human hg19', 1),
(3, 'Human hg18 random', 1),
(4, 'Sasquatch sg12 random', NULL);

INSERT INTO SampleClass (sampleClassId, alias, sampleCategory, sampleSubcategory, suffix, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Identity',             'Identity',          NULL,                    NULL,  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(23, 'Tissue',              'Tissue',            NULL,                    NULL,  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(24, 'Slide',               'Tissue Processing', 'Slide',                 'SL',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(10, 'Tissue Piece',        'Tissue Processing', 'Tissue Piece',          'LCM', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(11, 'gDNA (stock)',        'Stock',             NULL,                    'D_S', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(12, 'gDNA_wga (stock)',    'Stock',             NULL,                    'D_S', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(13, 'whole RNA (stock)',   'Stock',             'RNA (stock)',           'R_S', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(14, 'cDNA (stock)',        'Stock',             NULL,                    'D_S', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(15, 'gDNA (aliquot)',      'Aliquot',           NULL,                    'D_',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(16, 'gDNA_wga (aliquot)',  'Aliquot',           NULL,                    'D_',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(17, 'whole RNA (aliquot)', 'Aliquot',           'RNA (aliquot)',         'R_',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(18, 'smRNA',               'Aliquot',           'RNA (aliquot)',         'SM_', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(19, 'mRNA',                'Aliquot',           'RNA (aliquot)',         'MR_', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(20, 'rRNA_depleted',       'Aliquot',           'RNA (aliquot)',         'WT_', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(21, 'cDNA (aliquot)',      'Aliquot',           NULL,                    'D_',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(25, 'Single Cell',         'Tissue Processing', 'Single Cell',           'SC',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(26, 'Single Cell DNA (stock)',   'Stock',       'Single Cell (stock)',   'D_S', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(27, 'Single Cell DNA (aliquot)', 'Aliquot',     'Single Cell (aliquot)', 'D_',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(28, 'Unused', 'Aliquot', NULL, NULL, 1, '2020-02-28 20:08:00', 1, '2020-02-28 20:08:00');

INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived) VALUES
(10, 11, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(10, 13, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(11, 11, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(11, 12, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(11, 15, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(12, 12, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(12, 16, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(13, 13, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(13, 14, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(13, 17, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(14, 14, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(14, 21, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(17, 18, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(17, 19, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(17, 20, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(13, 20, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 1),
(13, 18, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 1),
(13, 19, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 1),
(11, 16, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 1),
(13, 21, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 1),
(15, 16, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 1),
(15, 15, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 1),
( 1, 23, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 10, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 1),
(23, 11, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 12, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 13, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 14, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 23, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(24, 10, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(24, 11, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(24, 13, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 24, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 25, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(25, 26, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(26, 27, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(11, 28, 1, '2020-02-28 20:08:00', 1, '2020-02-28 20:08:00', 0);

INSERT INTO TissueMaterial(tissueMaterialId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Fresh Frozen', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'FFPE', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(3, 'Blood', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(4, 'Fur', 1, '2020-02-28 20:11:00', 1, '2020-02-28 20:11:00');

INSERT INTO TissueOrigin(tissueOriginId, alias, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Bn', 'Brain', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'Ly', 'Lymphocyte', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(3, 'Pa', 'Pancreas', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(4, 'nn', 'Unknown', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(5, 'Bf', 'Big Foot', 1, '2020-02-28 20:41:00', 1, '2020-02-28 20:41:00');

INSERT INTO TissueType(tissueTypeId, alias, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'R','Reference or non-tumour, non-diseased tissue sample. Typically used as a donor-specific comparison to a diseased tissue, usually a cancer',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(2, 'P','Primary tumour',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(3, 'M','Metastatic tumour',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(4, 'X','Xenograft derived from some tumour. Note: may not necessarily be a mouse xenograft',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(5, 'T','Unclassifed tumour',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(6, 'C','Cell line derived from a tumour',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(7, 'B','Benign tumour',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(8, 'F','Fibroblast cells',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(9, 'E','Endothelial cells',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(10, 'S','Serum from blood where clotting proteins have been removed',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(11, 'A','Cells taken from Ascites fluid',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(12, 'O','Organoid',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43'),
(13, 'U','Unspecified',1,'2017-03-23 22:01:22',1,'2017-03-23 22:01:22'),
(14, 'n','Unknown',1,'2017-05-29 20:02:03',1,'2017-05-29 20:02:03');

INSERT INTO Lab(labId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'University Health Network - BioBank', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'University Health Network - Pathology', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(3, 'Almost Unused Institute - Unused Lab', 3, '2020-02-28 16:01:00', 3, '2020-02-28 16:01:00');

INSERT INTO StainCategory(stainCategoryId, name) VALUES
(1, 'One'),
(2, 'Two'),
(3, 'Three');

INSERT INTO Stain (stainId, name, stainCategoryId) VALUES
(1, 'Cresyl Violet', 1),
(2, 'Hematoxylin+Eosin', 2),
(3, 'Unused', 2);

INSERT INTO SamplePurpose (samplePurposeId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'CNV',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(2, 'Extra',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(3, 'Library',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(4, 'Methylation',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(5, 'PacBio',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(6, 'Research',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(7, 'Sequenom',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(8, 'Stock',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(9, 'Validation',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(10, 'WGA',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00'),
(11, 'Ion Torrent',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00');

INSERT INTO DetailedQcStatus (DetailedQcStatusId, status, description, noteRequired, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1,TRUE,  'Ready',                  0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(2,TRUE,  'OKd by Collaborator',    1,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(4,NULL,  'Waiting: Path Report',   0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(5,FALSE, 'Failed: STR',            1,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(6,FALSE, 'Failed: Diagnosis',      1,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(7,FALSE, 'Failed: QC',             0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(8,NULL,  'Reference Required',     0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(9,FALSE, 'Refused Consent',        0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(10,NULL, 'Waiting: Receive Tissue',0,1,'2016-09-26 15:55:46',1,'2016-09-26 15:55:46');

INSERT INTO QCType (name, description, qcTarget, units, archived, precisionAfterDecimal) VALUES
('RIN', 'RIN', 'Sample', ' ', 0, 1),
('DV200', 'DV200', 'Sample', '%', 0, 2),
('Tape Station', 'Tape Station', 'Library', 'bp', 0, 2),
('Qubit', 'Qubit', 'Library', 'ng/µl', 0, 2),
('qPCR', 'qPCR', 'Library', 'mol/µl', 0, 2);

INSERT INTO LibraryType(libraryTypeId, description, platformType, archived, abbreviation) VALUES
  (1, 'Paired End',  'ILLUMINA',0,'PE'),
  (2, 'Mate Pair',   'ILLUMINA',0,'MP'),
  (17,'Single End',  'ILLUMINA',0,'SE'),
  (27,'Total RNA',   'ILLUMINA',0,'TR'),
  (19,'cDNA',        'PACBIO',  0,NULL),
  (28,'Whole Genome','PACBIO',  0,NULL);

INSERT INTO LibrarySelectionType (librarySelectionTypeId, name, description) VALUES
  (24,'5-methylcytidine antibody','5-methylcytidine antibody desc'),
  (27,'BluePippin','BluePippin desc'),
  (22,'CAGE','CAGE desc'),
  (11,'cDNA','cDNA desc'),
  (14,'CF-H','CF-H desc'),
  (15,'CF-M','CF-M desc'),
  (16,'CF-S','CF-S desc'),
  (13,'CF-T','CF-T desc'),
  (10,'ChIP','ChIP desc'),
  (8, 'DNAse','DNAse desc'),
  (18,'HMPR','HMPR desc'),
  (7, 'Hybrid Selection','Hybrid Selection desc'),
  (23,'MBD2 protein methyl-CpG binding domain','MBD2 protein methyl-CpG binding domain desc'),
  (17,'MF','MF desc'),
  (9, 'MNase','MNase desc'),
  (12,'MSLL','MSLL desc'),
  (5, 'other','other desc'),
  (3, 'PCR','PCR desc'),
  (21,'RACE','RACE desc'),
  (19,'RANDOM','RANDOM desc'),
  (2, 'RANDOM PCR','RANDOM PCR desc'),
  (6, 'Reduced Representation','Reduced Representation desc'),
  (25,'Restriction Digest','Restriction Digest desc'),
  (1, 'RT-PCR','RT-PCR desc'),
  (26,'SageHLS','SageHLS desc'),
  (20,'size fractionation','size fractionation desc'),
  (4, 'unspecified','unspecified desc');

INSERT INTO LibraryStrategyType (libraryStrategyTypeId, name, description) VALUES
  (5, 'AMPLICON','AMPLICON desc'),
  (11,'Bisulfite-Seq','Bisulfite-Seq desc'),
  (8, 'ChIP-Seq','ChIP-Seq desc'),
  (3, 'CLONE','CLONE desc'),
  (6, 'CLONEEND','CLONEEND desc'),
  (14,'CTS','CTS desc'),
  (10,'DNase-Hypersensitivity','DNase-Hypersensitivity desc'),
  (12,'EST','EST desc'),
  (7, 'FINISHING','FINISHING desc'),
  (13,'FL-cDNA','FL-cDNA desc'),
  (16,'MBD-Seq','MBD-Seq desc'),
  (17,'MeDIP-Seq','MeDIP-Seq desc'),
  (9, 'MNase-Seq','MNase-Seq desc'),
  (18,'MRE-Seq','MRE-Seq desc'),
  (15,'OTHER','OTHER desc'),
  (4, 'POOLCLONE','POOLCLONE desc'),
  (19,'RNA-Seq','RNA-Seq desc'),
  (2, 'WCS','WCS desc'),
  (1, 'WGS','WGS desc'),
  (20,'WXS','WXS desc');

INSERT INTO LibraryDesignCode (libraryDesignCodeId, code, description) VALUES
  (1, 'AS','ATAC-Seq'),
  (2, 'CH','ChIP-Seq'),
  (3, 'EX','Exome'),
  (4, 'MR','mRNA'),
  (5, 'SM','smRNA'),
  (6, 'TS','Targeted Sequencing'),
  (7, 'WG','Whole Genome'),
  (8, 'WT','Whole Transcriptome'),
  (16,'TR','Total RNA'),
  (17, 'SC', 'Single Cell'),
  (18, 'UN', 'Unused');

INSERT INTO LibraryDesign (libraryDesignId, name, sampleClassId, librarySelectionType, libraryStrategyType, libraryDesignCodeId) VALUES
  (1, 'WG',                   15, 3, 1, 7),
  (2, 'TS (Hybrid Selection)',15, 7, 5, 6),
  (3, 'TS (PCR)',             15, 3, 5, 6),
  (4, 'EX',                   15, 7,20, 3),
  (6, 'WT',                   21,11,19, 8),
  (7, 'MR',                   21,11,19, 4),
  (8, 'CH',                   15,10, 8, 2),
  (9, 'AS',                   15, 7,15, 1),
  (10,'WG',                   16, 3, 1, 7),
  (11,'TS (Hybrid Selection)',16, 7, 5, 6),
  (12,'TS (PCR)',             16, 3, 5, 6),
  (13,'EX',                   16, 7,20, 3),
  (14,'MR',                   19,11,19, 4),
  (15,'SM',                   18,20,19, 5),
  (16,'WT',                   20,11,19, 8),
  (17,'TR',                   17,11,19,16),
  (18,'Single Cell',          27, 1,19,17),
  (19,'Single Cell CNV',      27, 2, 1,17),
  (20,'Unused',               27, 2, 1,17);

INSERT INTO LibrarySpikeIn(spikeInId, alias) VALUES
  (1, 'Spike-In One'),
  (2, 'Spike-In Two'),
  (3, 'Unused Spike-In');

INSERT INTO KitDescriptor (kitDescriptorId, name, version, manufacturer, partNumber, kitType, platformType, creator, created, lastModifier, lastModified) VALUES
  (1, 'Test Kit', 1, 'TestCo', '123', 'LIBRARY', 'ILLUMINA', 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
  (2, 'Test Kit Two', 2, 'TestCo', '124', 'LIBRARY', 'ILLUMINA', 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
  (3, 'Test Kit Three', 1, 'ACME', '125', 'CLUSTERING', 'ILLUMINA', 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
  (4, 'Test Kit Four', 1, 'DONUT', '126', 'MULTIPLEXING', 'ILLUMINA', 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00');
  
INSERT INTO TargetedSequencing (targetedSequencingId, alias, description, archived, createdBy, updatedBy, creationDate, lastUpdated) VALUES
  (1, 'Test TarSeq One', 'first test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00'),
  (2, 'Test TarSeq Two', 'second test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00'),
  (3, 'Test TarSeq Three', 'third test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00');
  
INSERT INTO TargetedSequencing_KitDescriptor (targetedSequencingId, kitDescriptorId) VALUES
  (1, 1), (2, 1), (3, 2);

INSERT INTO BoxUse (boxUseId, alias) VALUES 
(1, 'DNA'), (2, 'RNA'), (3, 'Libraries'), (4, 'Sequencing'), (5, 'Storage'), (6, 'Tissue');

INSERT INTO BoxSize (boxSizeId, boxSizeRows, boxSizeColumns, scannable) VALUES
(1, 8, 12, 1),
(2, 10, 10, 0),
(3, 3, 3, 0);

INSERT INTO _Group (groupId, description, name) VALUES
(1, 'TestGroup1', 'TestGroupOne'),
(2, 'TestGroup2', 'TestGroupTwo'),
(3, 'DeleteGroup', 'DeleteGroup');

INSERT INTO User_Group (users_userId, groups_groupId)
VALUES (3,1),(3,2),(1,1),(1,3),(4,3);

INSERT INTO LibraryIndexFamily (indexFamilyId, name, platformType) VALUES
  (1, 'Single Index 6bp', 'ILLUMINA'),
  (2, 'Dual Index 6bp', 'ILLUMINA'),
  (3, 'Similar Index Pair', 'ILLUMINA'),
  (4, 'Unused Family', 'ILLUMINA');

INSERT INTO LibraryIndex (indexId, indexFamilyId, name, sequence, position) VALUES
  (1,  1, 'Index 01', 'AAAAAA', 1),
  (2,  1, 'Index 02', 'CCCCCC', 1),
  (3,  1, 'Index 03', 'GGGGGG', 1),
  (4,  1, 'Index 04', 'TTTTTT', 1),
  (5,  2, 'A01',      'AAACCC', 1),
  (6,  2, 'A02',      'CCCAAA', 1),
  (7,  2, 'A03',      'GGGTTT', 1),
  (8,  2, 'A04',      'TTTGGG', 1),
  (9,  2, 'B01',      'AAATTT', 2),
  (10, 2, 'B02',      'CCCGGG', 2),
  (11, 2, 'B03',      'GGGCCC', 2),
  (12, 2, 'B04',      'TTTAAA', 2),
  (13, 3, 'Index 01', 'AAAAAC', 1),
  (14, 3, 'Index 02', 'AAAAGT', 1),
  (15, 4, 'A01', 'AAAAAA', 1),
  (16, 4, 'A02', 'CCCCCC', 1),
  (17, 4, 'B01', 'GGGGGG', 2),
  (18, 4, 'B02', 'TTTTTT', 2);

INSERT INTO InstrumentModel (instrumentModelId, platform, alias, numContainers, instrumentType) VALUES
  (1, 'ILLUMINA', 'Illumina HiSeq 2500', 1, 'SEQUENCER'),
  (2, 'ILLUMINA', 'Illumina MiSeq', 1, 'SEQUENCER'),
  (3, 'PACBIO', 'PacBio RS II', 1, 'SEQUENCER'),
  (4, 'ILLUMINA', 'Illumina iScan', 1, 'ARRAY_SCANNER'),
  (5, 'ILLUMINA', 'Deletable', 1, 'OTHER');
  
INSERT INTO SequencingParameters (parametersId, name, instrumentModelId, readLength, readLength2, createdBy, updatedBy, creationDate, lastUpdated, chemistry) VALUES
  (1, 'Custom (see notes)', 3, 0, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', NULL),
  (2, 'Rapid Run 2x151', 1, 151, 151, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'RAPID_RUN'),
  (3, '1x151', 1, 151, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V4'),
  (4, 'Micro 2x151', 2, 151, 151, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V3');

INSERT INTO SequencingContainerModel (sequencingContainerModelId, alias, identificationBarcode, partitionCount, platformType, fallback) VALUES
(1, 'Generic 4-Lane Illumina Flow Cell', NULL, 4, 'ILLUMINA', 1),
(2, 'Generic 8-Lane Illumina Flow Cell', NULL, 8, 'ILLUMINA', 1),
(3, 'Generic 1-Lane Illumina Flow Cell', NULL, 1, 'ILLUMINA', 1),
(4, 'Generic 1-SMRT-Cell PacBio 8Pac', NULL, 1, 'PACBIO', 1),
(5, 'Generic 2-SMRT-Cell PacBio 8Pac', NULL, 2, 'PACBIO', 1),
(6, 'Generic 3-SMRT-Cell PacBio 8Pac', NULL, 3, 'PACBIO', 1),
(7, 'Generic 4-SMRT-Cell PacBio 8Pac', NULL, 4, 'PACBIO', 1),
(8, 'Generic 5-SMRT-Cell PacBio 8Pac', NULL, 5, 'PACBIO', 1),
(9, 'Generic 6-SMRT-Cell PacBio 8Pac', NULL, 6, 'PACBIO', 1),
(10, 'Generic 7-SMRT-Cell PacBio 8Pac', NULL, 7, 'PACBIO', 1),
(11, 'Generic 8-SMRT-Cell PacBio 8Pac', NULL, 8, 'PACBIO', 1),
(12, 'Generic 9-SMRT-Cell PacBio 8Pac', NULL, 9, 'PACBIO', 1),
(13, 'Generic 10-SMRT-Cell PacBio 8Pac', NULL, 10, 'PACBIO', 1),
(14, 'Generic 11-SMRT-Cell PacBio 8Pac', NULL, 11, 'PACBIO', 1),
(15, 'Generic 12-SMRT-Cell PacBio 8Pac', NULL, 12, 'PACBIO', 1),
(16, 'Generic 13-SMRT-Cell PacBio 8Pac', NULL, 13, 'PACBIO', 1),
(17, 'Generic 14-SMRT-Cell PacBio 8Pac', NULL, 14, 'PACBIO', 1),
(18, 'Generic 15-SMRT-Cell PacBio 8Pac', NULL, 15, 'PACBIO', 1),
(19, 'Generic 16-SMRT-Cell PacBio 8Pac', NULL, 16, 'PACBIO', 1);

INSERT INTO SequencingContainerModel_InstrumentModel (instrumentModelId, sequencingContainerModelId) VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 4),
(3, 5),
(3, 6),
(3, 7),
(3, 8),
(3, 9),
(3, 10),
(3, 11),
(3, 12),
(3, 13),
(3, 14),
(3, 15),
(3, 16),
(3, 17),
(3, 18),
(3, 19);

INSERT INTO Instrument (instrumentId, name, instrumentModelId, defaultPurposeId) VALUES
  (1, 'T2000', 1, 1),
  (2, 'TMS1', 2, 1),
  (3, 'TPB2', 3, 1),
  (4, 'iScan1', 4, NULL),
  (5, 'Deletable', 4, NULL);
  
INSERT INTO Instrument (instrumentId, name, instrumentModelId, serialNumber, dateCommissioned, dateDecommissioned, upgradedInstrumentId, defaultPurposeId) VALUES
  (100, 'HiSeq_100', 1, '100', '2017-01-01', NULL, NULL, 1),
  (101, 'NewHiSeq_101', 1, '101', '2017-02-01', NULL, NULL, 1),
  (102, 'OldHiSeq_102', 1, '102', '2017-01-01', '2017-02-01', 101, 1),
  (200, 'HiSeq_200', 1, '200', '2017-01-01', NULL, NULL, 1),
  (5001, 'PacBio_SR_5001', 3, '5001', '2017-09-21', NULL, NULL, 1),
  (5002, 'HiSeq_SR_5002', 1, '5002', '2017-02-01', NULL, NULL, 1);

INSERT INTO ServiceRecord(recordId, title, details, servicedBy, referenceNumber, serviceDate, startTime, endTime, outOfService) VALUES
  (150, 'Test 150', 'details go here', 'technician1', '12345', '2017-09-05', '2017-09-01 10:00:00', '2017-09-05 10:00:00', 0),
  (151, 'Test 151', NULL, NULL, NULL, '2017-09-12', NULL, NULL, 0),
  (152, 'Test 152', 'details to remove', 'technitchin', 'Riffraff', '2017-09-12', '2017-09-11 11:00:00', '2017-09-12 12:00:00', 1),
  (153, 'Test 153', 'details go here', 'technician2', NULL, '2022-02-22', NULL, NULL, 0);

INSERT INTO Instrument_ServiceRecord(recordId, instrumentId) VALUES
(150, 101),
(151, 101),
(152, 101);

INSERT INTO Project(projectId, name, title, code, created, description, status, referenceGenomeId, lastModified, creator, lastModifier, pipelineId) VALUES
  (1, 'PRO1', 'Project One', 'PONE', '2017-06-27', 'integration test project one', 'ACTIVE', 1, '2017-06-27 14:11:00', 1, 1, 1),
  (2, 'PRO2', 'Project Two', 'PTWO', '2017-07-20', 'integration test project for custom identities', 'ACTIVE', 1, '2017-07-20 16:55:00', 1, 1, 1),
  (3, 'PRO3', 'Test Data', 'TEST', '2017-06-27', 'integration test project three', 'ACTIVE', 1, '2017-06-27 14:12:00', 1, 1, 1),
  (4, 'PRO4', 'Project To Change', 'DELTA', '2017-08-04', 'integration test project for changing fields', 'PROPOSED', 2, '2017-08-04 15:12:00', 1, 1, 1),
  (5, 'PRO5', 'Search Tables Project', 'SRCH', '2017-10-10', 'integration test project five', 'ACTIVE', 1, '2017-10-10 10:10:10', 1, 1, 1),
  (6, 'PRO6', 'Subprojects', 'SUBP', '2020-02-20', 'integration test project six', 'ACTIVE', 1, '2020-02-20 11:32:00', 1, 1, 1),
  (7, 'PRO7', 'Please Delete Me', 'GSLE', '2020-02-25', 'project to delete', 'ACTIVE', 1, '2020-02-25 19:18:00', 3, 3, 1),
  (100001, 'PRO100001', 'BulkLibraryIT', 'LIBT', '2017-07-24', 'bulk library test project', 'ACTIVE', 1, '2017-07-24 16:11:00', 1, 1, 1),
  (110001, 'PRO110001', 'SingleLibraryIT', '1LIB', '2017-08-16', 'single library test project', 'ACTIVE', 1, '2017-08-16 16:11:00', 1, 1, 1),
  (120001, 'PRO120001', 'SinglePoolIT', '1IPO', '2017-08-22', 'single pool test project', 'ACTIVE', 1, '2017-08-22 16:35:00', 1, 1, 1),
  (200001, 'PRO200001', 'BulkPoolIT', 'IPOT', '2017-08-15', 'bulk Pool test project', 'ACTIVE', 1, '2017-08-15 12:22:00', 1, 1, 1),
  (200, 'PRO200', 'HotSorting', 'SORT', '2017-08-09', 'test sorting by BoxPosition in Handsontable', 'ACTIVE', 1, '2017-08-09 11:51:00', 1, 1, 1),
  (300, 'PRO300', 'BulkLibraryAliquotIT', 'DILT', '2017-08-14', 'bulk library aliquot test project', 'ACTIVE', 1, '2017-08-14 11:54:00', 1, 1, 1),
  (400, 'PRO400', 'StudiesIT', 'STUT', '2017-08-16', 'studies test project', 'ACTIVE', 1, '2017-08-16 14:50:00', 1, 1, 1),
  (500, 'PRO500', 'Tubes In Boxes', 'TIB', '2017-08-15', 'test tubes in and out of boxes', 'ACTIVE', 1, '2017-08-15 13:45:00', 1, 1, 1),
  (4440, 'PRO4440', 'Propagate Samples', 'PROP', '2017-10-26', 'propagate samples', 'ACTIVE', 1, '2017-10-26 14:20:00', 1, 1, 1),
  (2200, 'PRO2200', 'Update Via QC', 'UQC', '2018-07-10', 'update via qc', 'ACTIVE', 1, '2018-07-10 12:52:00', 1, 1, 1);

INSERT INTO Subproject(subprojectId, projectId, alias, referenceGenomeId, description, priority, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 6, 'Subproject 1', 1, 'test subproject one', TRUE, 1, '2020-02-20 11:35:00', 1, '2020-02-20 11:35:00'),
(2, 6, 'Subproject 2', 1, 'test subproject two', TRUE, 1, '2020-02-20 11:35:00', 1, '2020-02-20 11:35:00'),
(3, 6, 'Subproject 3', 1, 'test subproject three', TRUE, 1, '2020-02-20 11:35:00', 1, '2020-02-20 11:35:00');
  
INSERT INTO SampleNumberPerProject(projectId, highestSampleNumber, padding, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(100001, 1, 4, 1, '2017-10-11 15:33:00', 1, '2017-10-11 15:33:00');

INSERT INTO Study (studyId, name, project_projectId, alias, studyTypeId, creator, created, lastModifier, lastModified) VALUES
(1, 'STU1', 1, 'Study One', 1, 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
(3, 'STU3', 3, 'Study Three', 1, 1, '2020-02-20 11:53:00', 1, '2020-02-20 11:53:00'),
(400, 'STU400', 400, 'UI Test Study', 1, 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00');

INSERT INTO Assay(assayId, alias, version, archived) VALUES
(1, 'Main Assay', '1.0', FALSE),
(2, 'Alternate Assay', '1.0', TRUE),
(3, 'Alternate Assay', '2.0', FALSE),
(4, 'Bad Assay', '1.0', FALSE);

INSERT INTO Metric(metricId, alias, category, thresholdType, units) VALUES
(1, 'Container Intact', 'RECEIPT', 'BOOLEAN', NULL),
(2, 'WG Library Yield', 'LIBRARY_PREP', 'GE', 'ng/μL'),
(3, 'Min Clusters (PF)', 'LIBRARY_QUALIFICATION', 'GT', 'K/lane'),
(4, 'To Delete', 'FULL_DEPTH_SEQUENCING', 'BOOLEAN', NULL);

INSERT INTO Assay_Metric(assayId, metricId, minimumThreshold, maximumThreshold) VALUES
(1, 1, NULL, NULL),
(1, 2, 10, NULL),
(1, 3, 500, NULL),
(2, 1, NULL, NULL),
(2, 2, 5, NULL),
(2, 3, 500, NULL),
(3, 1, NULL, NULL),
(3, 2, 8, NULL),
(3, 3, 500, NULL);

INSERT INTO AssayTest(testId, alias, tissueTypeId, negateTissueType, extractionClassId, libraryDesignCodeId, libraryQualificationMethod, libraryQualificationDesignCodeId, repeatPerTimepoint) VALUES
(1, 'Tumour WG', 1, TRUE, 11, 7, 'LOW_DEPTH_SEQUENCING', NULL, TRUE),
(2, 'Tumour WT', 1, TRUE, 13, 8, 'LOW_DEPTH_SEQUENCING', NULL, TRUE),
(3, 'Normal WG', 1, FALSE, 11, 7, 'LOW_DEPTH_SEQUENCING', NULL, FALSE),
(4, 'Delete Me', 1, FALSE, 11, 7, 'LOW_DEPTH_SEQUENCING', NULL, FALSE);

INSERT INTO Assay_AssayTest(assayId, testId) VALUES
(1, 1),
(1, 2),
(1, 3);

INSERT INTO Requisition(requisitionId, alias, creator, created, lastModifier, lastModified) VALUES
(1, 'Req One', 1, '2021-07-21 11:31:00', 1, '2021-07-21 11:31:00'),
(2, 'Req Two', 3, '2021-07-21 11:31:00', 3, '2021-07-21 11:31:00');

INSERT INTO Requisition_Assay(requisitionId, assayId) VALUES
(1, 1),
(2, 3);

-- Identities
INSERT INTO Sample (sampleId, project_projectId, name, alias, description, identificationBarcode, sampleType, scientificNameId, volume, volumeUnits, concentration, concentrationUnits, creator, created, lastModifier, lastModified,
  nonStandardAlias, isSynthetic, sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcUser, qcDate, archived, discriminator,
  externalName, donorSex, consentLevel) VALUES
(1, 3, 'SAM1', 'TEST_0001', 'Identity', '11111', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:00:00', 1, '2017-07-20 09:00:00',
  FALSE, FALSE, 1, NULL, NULL, NULL, NULL, 1, NULL, 1, '2017-07-20', 0, 'Identity',
  'TEST_external_1', 'MALE', 'THIS_PROJECT'),
(201, 200, 'SAM201', 'SORT_0001', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00',
  FALSE, FALSE, 1, NULL, NULL, NULL, NULL, 1, NULL, 1, '2017-08-09', 0, 'Identity',
  'SORT_identity_1', 'UNKNOWN', 'THIS_PROJECT'),
(301, 300, 'SAM301', 'DILT_0001', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00',
  FALSE, FALSE, 1, NULL, NULL, NULL, NULL, 1, NULL, 1, '2017-08-14', 0, 'Identity',
  'DILT_identity_1', 'FEMALE', 'THIS_PROJECT'),
(501, 500, 'SAM501', 'TIB_0001', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-15 13:45:00', 1, '2017-08-15 13:45:00',
  FALSE, FALSE, 1, NULL, NULL, NULL, NULL, 1, NULL, 1, '2017-08-15', 0, 'Identity',
  'TIB_identity_1', 'UNKNOWN', 'THIS_PROJECT'),
(4441, 4440, 'SAM4441', 'PROP_0001', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-10-26 14:40:00', 1, '2017-10-26 14:40:00',
  FALSE, FALSE, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-26', 0, 'Identity',
  'PROP_identity_1', 'UNKNOWN', 'THIS_PROJECT'),
(100001, 100001, 'SAM100001', 'LIBT_0001', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 1, NULL, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Identity',
  'LIBT_identity1', 'UNKNOWN', 'THIS_PROJECT'),
(110001, 110001, 'SAM110001', '1LIB_0001', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 1, NULL, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Identity',
  '1LIB_identity1', 'UNKNOWN', 'THIS_PROJECT'),
(120001, 120001, 'SAM120001', '1IPO_0001', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 1, NULL, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Identity',
  '1IPO_identity1', 'UNKNOWN', 'THIS_PROJECT'),
(200001, 200001, 'SAM200001', 'IPOT_0001', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 1, NULL, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Identity',
  'IPOT_identity1', 'UNKNOWN', 'THIS_PROJECT');

-- Tissues
INSERT INTO Sample (sampleId, project_projectId, name, alias, description, identificationBarcode, sampleType, scientificNameId, volume, volumeUnits, concentration, concentrationUnits, creator, created, lastModifier, lastModified,
  nonStandardAlias, isSynthetic, sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcUser, qcDate, archived, discriminator,
  tissueOriginId, tissueTypeId, secondaryIdentifier, labId, region, passageNumber, tubeNumber, timesReceived, tissueMaterialId, requisitionId) VALUES
(2, 3, 'SAM2', 'TEST_0001_Bn_R_nn_1-1', 'Tissue', '22222', 'GENOMIC', 1, 30, 'MICROLITRES', NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 23, 1, NULL, NULL, NULL, 1, NULL, 1, '2017-07-20', 0, 'Tissue',
  1, 1, 'tube 1', 2, 'cortex', NULL, 1, 1, 2, 1),
(202, 200, 'SAM202', 'SORT_0001_nn_n_1-1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00',
  FALSE, FALSE, 23, 201, NULL, NULL, NULL, 1, NULL, 1, '2017-08-09', 0, 'Tissue',
  1, 1, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
(302, 300, 'SAM302', 'DILT_0001_nn_n_1-1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00',
  FALSE, FALSE, 23, 301, NULL, NULL, NULL, 1, NULL, 1, '2017-08-14', 0, 'Tissue',
  1, 1, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
(502, 500, 'SAM502', 'TIB_0001_nn_n_1-1', NULL, 'TIB_SamTissue', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-15 13:45:00', 1, '2017-08-15 13:45:00',
  FALSE, FALSE, 23, 501, NULL, NULL, NULL, 1, NULL, 1, '2017-08-15', 0, 'Tissue',
  1, 1, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
(4442, 4440, 'SAM4442', 'PROP_0001_nn_n_1-1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-10-26 14:40:00', 1, '2017-10-26 14:40:00',
  FALSE, FALSE, 23, 4441, 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 'Tissue',
  4, 14, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
(100002, 100001, 'SAM100002', 'LIBT_0001_Ly_P_nn_1-1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 23, 100001, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Tissue',
  2, 2, NULL, NULL, NULL, NULL, 1, 1, NULL, 2),
(110002, 110001, 'SAM110002', '1LIB_0001_Ly_P_1-1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 23, 110001, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Tissue',
  2, 2, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
(120002, 120001, 'SAM120002', '1IPO_0001_Ly_P_1-1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 23, 120001, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Tissue',
  2, 2, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL),
(200002, 200001, 'SAM200002', 'IPOT_0001_Pa_P_1-1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 23, 200001, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Tissue',
  3, 2, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL);

-- Slides
INSERT INTO Sample (sampleId, project_projectId, name, alias, description, identificationBarcode, sampleType, scientificNameId, volume, volumeUnits, concentration, concentrationUnits, creator, created, lastModifier, lastModified,
  nonStandardAlias, isSynthetic, sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcUser, qcDate, archived, discriminator,
  initialSlides, slides) VALUES
(3, 3, 'SAM3', 'TEST_0001_Bn_R_nn_1-1_SL01', 'Slide', '33333', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 24, 2, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'Slide',
  15, 15),
(4443, 4440, 'SAM4443', 'PROP_0001_nn_n_1-1_SL01', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-10-26 14:40:00', 1, '2017-10-26 14:40:00',
  FALSE, FALSE, 24, 4442, 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 'Slide',
  3, 3);

-- Tissue Pieces
INSERT INTO Sample (sampleId, project_projectId, name, alias, description, identificationBarcode, sampleType, scientificNameId, volume, volumeUnits, concentration, concentrationUnits, creator, created, lastModifier, lastModified,
  nonStandardAlias, isSynthetic, sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcUser, qcDate, archived, discriminator,
  tissuePieceType, slidesConsumed) VALUES
(4, 3, 'SAM4', 'TEST_0001_Bn_R_nn_1-1_C01', 'Curls', '44444', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 10, 3, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'TissuePiece',
  3, 1),
(5, 3, 'SAM5', 'TEST_0001_Bn_R_nn_1-1_LCM01', 'LCM Tube', '55555', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 10, 3, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'TissuePiece',
  1, 10),
(4444, 4440, 'SAM4444', 'PROP_0001_nn_n_1-1_LCM01', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-10-26 14:40:00', 1, '2017-10-26 14:40:00',
  FALSE, FALSE, 10, 4443, 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 'TissuePiece',
  1, 1);

-- Stocks
INSERT INTO Sample (sampleId, project_projectId, name, alias, description, identificationBarcode, sampleType, scientificNameId, volume, volumeUnits, concentration, concentrationUnits, creator, created, lastModifier, lastModified,
  nonStandardAlias, isSynthetic, sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcUser, qcDate, archived, discriminator,
  strStatus, dnaseTreated) VALUES
(6, 3, 'SAM6', 'TEST_0001_Bn_R_nn_1-1_D_S1', 'gDNA stock', '66666', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 11, 5, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'Stock',
  'SUBMITTED', 0),
(7, 3, 'SAM7', 'TEST_0001_Bn_R_nn_1-1_R_S1', 'whole RNA stock', '77777', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 13, 2, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'StockRna',
  'NOT_SUBMITTED', 0),
(10, 3, 'SAM10', 'TEST_0001_Bn_R_nn_1-1_D_S2', 'cDNA stock', '10101', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 14, 7, 2, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'Stock',
  'PASS', 0),
(203, 200, 'SAM203', 'SORT_0001_nn_n_1-1_D_S1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00',
  FALSE, FALSE, 11, 202, NULL, NULL, NULL, 1, NULL, 1, '2017-08-09', 0, 'Stock',
  'NOT_SUBMITTED', 0),
(303, 300, 'SAM303', 'DILT_0001_nn_n_1-1_D_S1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00',
  FALSE, FALSE, 11, 302, NULL, NULL, NULL, 1, NULL, 1, '2017-08-14', 0, 'Stock',
  'NOT_SUBMITTED', 0),
(503, 500, 'SAM503', 'TIB_0001_nn_n_1-1_D_S1', NULL, 'TIB_SamStock', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-15 13:45:00', 1, '2017-08-15 13:45:00',
  FALSE, FALSE, 11, 502, NULL, NULL, NULL, 1, NULL, 1, '2017-08-15', 0, 'Stock',
  'PASS', 0),
(4445, 4440, 'SAM4445', 'PROP_0001_nn_n_1-1_D_S1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-10-26 14:40:00', 1, '2017-10-26 14:40:00',
  FALSE, FALSE, 14, 4442, 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 'Stock',
  'NOT_SUBMITTED', 0),
(4446, 4440, 'SAM4446', 'PROP_0001_nn_n_1-1_R_S1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-10-26 14:40:00', 1, '2017-10-26 14:40:00',
  FALSE, FALSE, 13, 4442, 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 'Stock',
  'NOT_SUBMITTED', 1),
(100003, 100001, 'SAM100003', 'LIBT_0001_Ly_P_1-1_D_S1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 11, 100002, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Stock',
  'NOT_SUBMITTED', 0),
(110003, 110001, 'SAM110003', '1LIB_0001_Ly_P_1-1_D_S1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 11, 110002, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Stock',
  'NOT_SUBMITTED', 0),
(120003, 120001, 'SAM120003', '1IPO_0001_Ly_P_1-1_D_S1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 11, 120002, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Stock',
  'NOT_SUBMITTED', 0),
(200003, 200001, 'SAM200003', 'IPOT_0001_Pa_P_1-1_D_S1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 11, 200002, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Stock',
  'NOT_SUBMITTED', 0);

-- Aliquots
INSERT INTO Sample (sampleId, project_projectId, name, alias, description, identificationBarcode, sampleType, scientificNameId, volume, volumeUnits, concentration, concentrationUnits, creator, created, lastModifier, lastModified,
  nonStandardAlias, isSynthetic, sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcUser, qcDate, archived, discriminator,
  samplePurposeId) VALUES
(8, 3, 'SAM8', 'TEST_0001_Bn_R_nn_1-1_D_1', 'gDNA aliquot', '88888', 'GENOMIC', 1, NULL, NULL,NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 15, 6, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'Aliquot',
  9),
(9, 3, 'SAM9', 'TEST_0001_Bn_R_nn_1-1_R_1', 'whole RNA aliquot', '99999', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 17, 7, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'AliquotRna',
  3),
(11, 3, 'SAM11', 'TEST_0001_Bn_R_nn_1-1_D_2', 'cDNA aliquot', '11011', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 21, 10, 2, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'Aliquot',
  4),
(12, 3, 'SAM12', 'TEST_0001_Bn_R_nn_1-1_R_1_SM_1', 'smRNA', '12121', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 3, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 18, 9, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'AliquotRna',
  6),
(13, 3, 'SAM13', 'TEST_0001_Bn_R_nn_1-1_R_1_MR_1', 'mRNA', '13131', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 19, 9, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'AliquotRna',
  7),
(14, 3, 'SAM14', 'TEST_0001_Bn_R_nn_1-1_R_1_WT_1', 'rRNA_depleted', '14141', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00',
  FALSE, FALSE, 20, 9, 1, '7357', 'TEST', 1, NULL, 1, '2017-07-20', 0, 'AliquotRna',
  3),
(204, 200, 'SAM204', 'SORT_0001_nn_n_1-1_D_1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00',
  FALSE, FALSE, 15, 203, NULL, NULL, NULL, 1, NULL, 1, '2017-08-09', 0, 'Aliquot',
  NULL),
(205, 200, 'SAM205', 'SORT_0001_nn_n_1-1_D_2', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00',
  FALSE, FALSE, 15, 203, NULL, NULL, NULL, 1, NULL, 1, '2017-08-09', 0, 'Aliquot',
  NULL),
(206, 200, 'SAM206', 'SORT_0001_nn_n_1-1_D_3', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00',
  FALSE, FALSE, 15, 203, NULL, NULL, NULL, 1, NULL, 1, '2017-08-09', 0, 'Aliquot',
  NULL),
(304, 300, 'SAM304', 'DILT_0001_nn_n_1-1_D_1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00',
  FALSE, FALSE, 15, 303, NULL, NULL, NULL, 1, NULL, 1, '2017-08-14', 0, 'Aliquot',
  NULL),
(305, 300, 'SAM305', 'DILT_0001_nn_n_1-1_D_2', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00',
  FALSE, FALSE, 15, 303, NULL, NULL, NULL, 1, NULL, 1, '2017-08-14', 0, 'Aliquot',
  NULL),
(504, 500, 'SAM504', 'TIB_0001_nn_n_1-1_D_1', NULL, 'TIB_SamAliquot', 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-08-15 13:45:00', 1, '2017-08-15 13:45:00',
  FALSE, FALSE, 15, 503, NULL, NULL, NULL, 1, NULL, 1, '2017-08-15', 0, 'Aliquot',
  NULL),
(2201, 2200, 'SAM2201', 'UQC_0001', NULL, NULL, 'GENOMIC', 1, 50, 'MICROLITRES', 60, 'NANOGRAMS_PER_MICROLITRE', 1, '2018-07-10 12:53:00', 1, '2018-07-10 12:53:00',
  FALSE, FALSE, 23, 4441, 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 'Aliquot',
  NULL),
(4447, 4440, 'SAM4447', 'PROP_0001_nn_n_1-1_R_1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-10-26 14:40:00', 1, '2017-10-26 14:40:00',
  FALSE, FALSE, 17, 4446, 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 'AliquotRna',
  3),
(100004, 100001, 'SAM100004', 'LIBT_0001_Ly_P_1-1_D1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 15, 100003, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Aliquot',
  NULL),
(110004, 110001, 'SAM110004', '1LIB_0001_Ly_P_1-1_D1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 15, 110003, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Aliquot',
  NULL),
(120004, 120001, 'SAM120004', '1IPO_0001_Ly_P_1-1_D1', NULL, NULL, 'GENOMIC', 1, NULL, NULL,NULL, NULL,  1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 15, 120003, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Aliquot',
  NULL),
(200004, 200001, 'SAM200004', 'IPOT_0001_Pa_P_1-1_D_1', NULL, NULL, 'GENOMIC', 1, NULL, NULL, NULL, NULL, 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00',
  FALSE, FALSE, 15, 200003, NULL, NULL, NULL, 1, NULL, 1, '2017-07-24', 0, 'Aliquot',
  NULL);

INSERT INTO Transfer(transferId, transferTime, senderLabId, recipientGroupId, creator, created, lastModifier, lastModified) VALUES
(1, '2017-07-20 12:00:00', 1, 1, 1, '2017-07-20 12:53:00', 1, '2017-07-20 12:53:00'),
(2, '2017-07-20 12:00:00', 1, 1, 3, '2017-07-20 12:53:00', 3, '2017-07-20 12:53:00');

INSERT INTO Transfer_Sample(transferId, sampleId, received, qcPassed, qcNote) VALUES
(1, 2, TRUE, TRUE, NULL),
(2, 100002, TRUE, TRUE, NULL);

INSERT INTO Library(libraryId, name, alias, identificationBarcode, description, sample_sampleId, platformType, libraryType, librarySelectionType, libraryStrategyType, creationDate,
  creator, created, lastModifier, lastModified, detailedQcStatusId, qcUser, qcDate, dnaSize, volume, concentration, locationBarcode, kitDescriptorId, discarded, volumeUnits, concentrationUnits, spikeInId, spikeInDilutionFactor, spikeInVolume, lowQuality,
  discriminator, archived, libraryDesign, libraryDesignCodeId, nonStandardAlias, sopId, kitLot, index1Id, index2Id) VALUES
  (1, 'LIB1', 'TEST_0001_Bn_R_PE_300_WG', '11211', 'description lib 1', 8, 'ILLUMINA', 1, 3, 1,  '2016-11-07', 
    1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00', 1, 1, '2017-07-20', 300, 5.0, 2.75, NULL, 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, 1, 7, FALSE, NULL, NULL, NULL, NULL),
  (204, 'LIB204', 'SORT_0001_nn_n_PE_204_WG', NULL, 'description', 204, 'ILLUMINA', 1, 3, 1, '2017-08-09',
    1, '2017-08-09 11:58:00', 1, '2017-08-09 11:58:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (205, 'LIB205', 'SORT_0001_nn_n_PE_205_WG', NULL, 'description', 205, 'ILLUMINA', 1, 3, 1, '2017-08-09',
    1, '2017-08-09 11:58:00', 1, '2017-08-09 11:58:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (206, 'LIB206', 'SORT_0001_nn_n_PE_206_WG', NULL, 'description', 206, 'ILLUMINA', 1, 3, 1, '2017-08-09',
    1, '2017-08-09 11:58:00', 1, '2017-08-09 11:58:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (304, 'LIB304', 'DILT_0001_nn_n_PE_304_WG', NULL, 'description', 304, 'ILLUMINA', 1, 3, 1, '2017-08-14',
    1, '2017-08-14 12:05:00', 1, '2017-08-14 12:05:00', NULL, NULL, NULL, 304, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 5, NULL),
  (305, 'LIB305', 'DILT_0001_nn_n_PE_305_WG', NULL, 'description', 305, 'ILLUMINA', 1, 3, 1, '2017-08-14',
    1, '2017-08-14 12:05:00', 1, '2017-08-14 12:05:00', NULL, NULL, NULL, 305, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 6, NULL),
  (306, 'LIB306', 'DILT_0001_nn_n_PE_306_WG', NULL, 'description', 304, 'ILLUMINA', 1, 3, 1, '2017-08-14',
    1, '2017-08-14 12:05:00', 1, '2017-08-14 12:05:00', NULL, NULL, NULL, 306, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 9, NULL),
  (504, 'LIB504', 'TIB_0001_nn_n_PE_404_WG', 'TIB_Lib', NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (505, 'LIB505', 'TIB_0001_nn_n_PE_505_WG', 'TIB_Lib2', NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (600, 'LIB600', 'TIB_0001_nn_n_PE_600_WG', 'BADLIB', NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2018-06-26 11:38:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, TRUE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 9, NULL),
  (601, 'LIB601', 'TIB_0001_nn_n_PE_601_WG', 'SimLib1', NULL, 504, 'ILLUMINA', 1, 3, 1, '2018-06-26',
    1, '2018-06-26 11:38:00', 1, '2018-06-26 11:38:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 13, NULL),
  (602, 'LIB602', 'TIB_0001_nn_n_PE_602_WG', 'SimLib2', NULL, 504, 'ILLUMINA', 1, 3, 1, '2018-06-26',
    1, '2018-06-26 11:38:00', 1, '2018-06-26 11:38:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 14, NULL),
  (603, 'LIB603', 'TIB_0001_nn_n_PE_603_WG', 'SameLib1', NULL, 504, 'ILLUMINA', 1, 3, 1, '2018-06-26',
    1, '2018-06-26 11:38:00', 1, '2018-06-26 11:38:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 8, NULL),
  (604, 'LIB604', 'TIB_0001_nn_n_PE_604_WG', 'SameLib2', NULL, 504, 'ILLUMINA', 1, 3, 1, '2018-06-26',
    1, '2018-06-26 11:38:00', 1, '2018-06-26 11:38:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 8, NULL),
  (700, 'LIB700', 'TIB_0001_nn_n_PE_700_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, 700, 100, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (701, 'LIB701', 'TIB_0001_nn_n_PE_701_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, 701, 100, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (801, 'LIB801', 'TIB_0001_nn_n_PE_801_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, 801, 100, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (802, 'LIB802', 'TIB_0001_nn_n_PE_802_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, 802, 100, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (803, 'LIB803', 'TIB_0001_nn_n_PE_803_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, 803, 100, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (804, 'LIB804', 'TIB_0001_nn_n_PE_804_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, NULL, 60, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (805, 'LIB805', 'TIB_0001_nn_n_PE_805_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, NULL, 100, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (806, 'LIB806', 'TIB_0001_nn_n_PE_806_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, NULL, 60, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (807, 'LIB807', 'TIB_0001_nn_n_PE_807_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, NULL, 60, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (901, 'LIB901', 'TIB_0001_nn_n_PE_901_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, NULL, -30, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (2201, 'LIB2201', 'TIB_0001_nn_n_PE_2201_WG', NULL, NULL, 504, 'ILLUMINA', 1, 3, 1, '2017-08-15',
    1, '2017-08-15 13:55:00', 1, '2017-08-15 13:55:00', NULL, NULL, NULL, NULL, 50, 60, NULL, 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (100001, 'LIB100001', 'LIBT_0001_Ly_P_PE_251_WG', 'libbar100001', 'libdesc100001', 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    3, '2017-07-24 16:11:00', 3, '2017-07-24 16:11:00', 7, 1, '2017-07-24', 251,  2.5,  10, NULL, 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', 1, 'TEN', 12.34, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 5, 9),
  (100002, 'LIB100002', 'LIBT_0001_Ly_P_PE_252_WG', 'libbar100002', 'libdesc100002', 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 7, 1, '2017-07-24', 252,  4,    6.3, NULL, 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', 1, 'TEN', 12.34, FALSE,
    'DetailedLibrary', 0, 1, 7, FALSE, NULL, NULL, 6, 10),
  (100003, 'LIB100003', 'LIBT_0001_Ly_P_PE_253_WG', NULL,           NULL,            100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (100004, 'LIB100004', 'LIBT_0001_Ly_P_PE_254_WG', NULL,           'libdesc100004', 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (100005, 'LIB100005', 'LIBT_0001_Ly_P_PE_255_WG', NULL,           'libdesc100005', 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (100006, 'LIB100006', 'LIBT_0001_Ly_P_PE_256_WG', NULL,           'libdesc100006', 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (100007, 'LIB100007', 'LIBT_0001_Ly_P_PE_257_WG', NULL,           'libdesc100007', 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (100008, 'LIB100008', 'LIBT_0001_Ly_P_PE_258_WG', NULL,           'libdesc100008', 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (110001, 'LIB110001', '1LIB_0001_Ly_P_PE_251_WG', 'libbar110001', 'libdesc110001', 110004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 7, 1, '2017-07-24',    251,  2.5,  10, NULL, 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 5, 9),
  (110002, 'LIB110002', '1LIB_0001_Ly_P_PE_252_WG', 'libbar110002', 'libdesc110002', 110004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 7, 1, '2017-07-24',    252,  4,    6.3, 'lib_location_110002', 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, 1, 7, FALSE, NULL, NULL, 5, 9),
  (110003, 'LIB110003', '1LIB_0001_Ly_P_PE_253_WG', NULL,           NULL,            110004, 'ILLUMINA', 1, NULL, NULL, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (110004, 'LIB110004', '1LIB_0001_Ly_P_PE_254_WG', NULL,           'libdesc110004', 110004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, 5, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, NULL, NULL),
  (110005, 'LIB110005', '1LIB_0001_Ly_P_PE_255_WG', NULL,           'libdesc110005', 110004, 'ILLUMINA', 1, NULL, NULL, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, 5, NULL, NULL, 1, 0, 'MICROLITRES', NULL, NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 3, FALSE, NULL, NULL, 5, 9),
  (120001, 'LIB120001', '1IPO_0001_Ly_P_PE_251_WG', 'libbar120001', 'libdesc120001', 110004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 7, 1, '2017-07-24',    251,  2.5,  10, NULL, 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, NULL, NULL, 1, NULL),
  (120002, 'LIB120002', '1IPO_0001_Ly_P_PE_252_WG', 'libbar120002', 'libdesc120002', 110004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 7, 1, '2017-07-24',    252,  4,    6.3, 'lib_location_120002', 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, 1, 7, FALSE, NULL, NULL, 2, NULL),
  (200001, 'LIB200001', 'IPOT_0001_Pa_P_PE_251_WG', 'libbar200001', 'libdesc200001', 200004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 7, 1, '2017-07-24',    251,  2.5,  10, NULL, 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, NULL, 7, FALSE, 3, 'KITLOTONE', NULL, NULL),
  (200002, 'LIB200002', 'IPOT_0001_Pa_P_PE_252_WG', 'libbar200002', 'libdesc200002', 200004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 7, 1, '2017-07-24',    252,  4,    6.3, NULL, 1, 0, 'MICROLITRES', 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, NULL, FALSE,
    'DetailedLibrary', 0, 1, 7, FALSE, 3, 'KITLOTONE', NULL, NULL);

INSERT INTO LibraryAliquot (aliquotId, name, alias, concentration, concentrationUnits, libraryId, identificationBarcode, creationDate, creator, lastModifier, lastUpdated, volumeUsed, discriminator, libraryDesignCodeId, nonStandardAlias) VALUES
(1, 'LDI1', 'TEST_0001_Bn_R_PE_300_WG', 5.9, 'NANOGRAMS_PER_MICROLITRE', 1, '12321', '2017-07-20', 1, 1, '2017-07-20 09:01:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(304, 'LDI304', 'DILT_0001_nn_n_PE_304_WG', 7.97, 'NANOGRAMS_PER_MICROLITRE', 304, '300304', '2017-08-14', 3, 3, '2017-08-14 12:25:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(305, 'LDI305', 'DILT_0001_nn_n_PE_305_WG', 7.97, 'NANOGRAMS_PER_MICROLITRE', 305, '300305', '2017-08-14', 1, 1, '2017-08-14 12:25:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(504, 'LDI504', 'TIB_0001_nn_n_PE_404_WG', 5.9, 'NANOGRAMS_PER_MICROLITRE', 504, 'TIB_Dil', '2017-08-15', 1, 1, '2017-08-15 13:55:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(505, 'LDI505', 'TIB_0001_nn_n_PE_404_WG', 3.3, 'NANOGRAMS_PER_MICROLITRE', 504, 'TIB_replaceDil', '2017-08-15', 1, 1, '2017-08-15 13:55:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(701, 'LDI701', 'TIB_0001_nn_n_PE_404_WG', 2.2, 'NANOGRAMS_PER_MICROLITRE', 504, 'test_pooling_1', '2017-10-16', 1, 1, '2017-10-16 15:59:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(702, 'LDI702', 'TIB_0001_nn_n_PE_404_WG', 2.2, 'NANOGRAMS_PER_MICROLITRE', 504, 'test_pooling_2', '2017-10-16', 1, 1, '2017-10-16 15:59:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(800, 'LDI800', 'TIB_0001_nn_n_PE_600_WG', 2.2, 'NANOGRAMS_PER_MICROLITRE', 600, 'low_quality_library', '2018-06-26', 1, 1, '2018-06-26 11:39:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(801, 'LDI801', 'TIB_0001_nn_n_PE_601_WG', 2.2, 'NANOGRAMS_PER_MICROLITRE', 601, 'similar_index_1', '2018-06-26', 1, 1, '2018-06-26 11:39:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(802, 'LDI802', 'TIB_0001_nn_n_PE_602_WG', 2.2, 'NANOGRAMS_PER_MICROLITRE', 602, 'similar_index_2', '2018-06-26', 1, 1, '2018-06-26 11:39:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(803, 'LDI803', 'TIB_0001_nn_n_PE_603_WG', 2.2, 'NANOGRAMS_PER_MICROLITRE', 603, 'same_index_1', '2018-06-26', 1, 1, '2018-06-26 11:39:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(804, 'LDI804', 'TIB_0001_nn_n_PE_604_WG', 2.2, 'NANOGRAMS_PER_MICROLITRE', 604, 'same_index_2', '2018-06-26', 1, 1, '2018-06-26 11:39:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(901, 'LDI901', 'TIB_0001_nn_n_PE_404_WG', 4.3, 'NANOGRAMS_PER_MICROLITRE', 504, 'auto_calculate_pool_1', '2018-07-12', 1, 1, '2018-07-12 09:43:00', 14.7, 'DetailedLibraryAliquot', 7, FALSE),
(902, 'LDI902', 'TIB_0001_nn_n_PE_404_WG', 1.7, 'NANOGRAMS_PER_MICROLITRE', 504, 'auto_calculate_pool_2', '2018-07-12', 1, 1, '2018-07-12 09:43:00', 21.3, 'DetailedLibraryAliquot', 7, FALSE),
(1001, 'LDI1001', 'TIB_0001_nn_n_PE_804_WG', 1.7, 'NANOGRAMS_PER_MICROLITRE', 804, 'edit_volumeused_1', '2018-07-12', 1, 1, '2018-07-12 09:43:00', 40, 'DetailedLibraryAliquot', 7, FALSE),
(1002, 'LDI1002', 'TIB_0001_nn_n_PE_805_WG', 1.7, 'NANOGRAMS_PER_MICROLITRE', 805, 'edit_volumeused_2', '2018-07-12', 1, 1, '2018-07-12 09:43:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(1003, 'LDI1003', 'TIB_0001_nn_n_PE_806_WG', 1.7, 'NANOGRAMS_PER_MICROLITRE', 806, 'edit_volumeused_3', '2018-07-12', 1, 1, '2018-07-12 09:43:00', 40, 'DetailedLibraryAliquot', 7, FALSE),
(1004, 'LDI1004', 'TIB_0001_nn_n_PE_807_WG', 1.7, 'NANOGRAMS_PER_MICROLITRE', 807, 'edit_volumeused_4', '2018-07-12', 1, 1, '2018-07-12 09:43:00', 40, 'DetailedLibraryAliquot', 7, FALSE),
(120001, 'LDI120001', '1IPO_0001_Ly_P_PE_251_WG', 4, 'NANOGRAMS_PER_MICROLITRE', 120001, NULL, '2017-08-15', 1, 1, '2017-08-15 09:01:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(120002, 'LDI120002', '1IPO_0001_Ly_P_PE_252_WG', 4, 'NANOGRAMS_PER_MICROLITRE', 120002, NULL, '2017-08-15', 1, 1, '2017-08-15 09:01:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(200001, 'LDI200001', 'IPOT_0001_Pa_P_PE_251_WG', 4, 'NANOGRAMS_PER_MICROLITRE', 200001, NULL, '2017-08-15', 1, 1, '2017-08-15 09:01:00', NULL, 'DetailedLibraryAliquot', 7, FALSE),
(200002, 'LDI200002', 'IPOT_0001_Pa_P_PE_252_WG', 3, 'NANOGRAMS_PER_MICROLITRE', 200002, NULL, '2017-08-15', 1, 1, '2017-08-15 09:01:00', NULL, 'DetailedLibraryAliquot', 7, FALSE);

INSERT INTO Pool (poolId, concentration, concentrationUnits, volume, volumeUnits, name, alias, identificationBarcode, description, creationDate, platformType, lastModifier, creator, created, lastModified, qcPassed) VALUES
(1, 8.25, 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, 'IPO1', 'POOL_1', '12341', NULL, '2017-07-20', 'ILLUMINA', 1, 1, '2017-07-20 10:01:00', '2017-07-20 10:01:00', NULL),
(501, 2.2, 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, 'IPO501', 'TIB_Pool', 'TIB_Pool', NULL, '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 13:55:00', '2017-08-15 13:55:00', 1),
(120001, 6.5, 'NANOGRAMS_PER_MICROLITRE', 12, 'MICROLITRES', 'IPO120001', '1IPO_POOL_1', 'ipobar120001', 'ipodesc120001', '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 0),
(120002, 6.5, 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, 'IPO120002', '1IPO_POOL_2', NULL, NULL, '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', NULL),
(120003, 6.5, 'NANOGRAMS_PER_MICROLITRE', 12, 'MICROLITRES', 'IPO120003', '1IPO_POOL_3', 'ipobar120003', 'ipodesc120003', '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 0),
(120004, 6.5, 'NANOGRAMS_PER_MICROLITRE', 12, 'MICROLITRES', 'IPO120004', '1IPO_POOL_4', 'ipobar120004', 'ipodesc120004', '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 0),
(120005, 6.5, 'NANOGRAMS_PER_MICROLITRE', 12, 'MICROLITRES', 'IPO120005', '1IPO_POOL_5', NULL, 'ipodesc120005', '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 0),
(200001, 6.5, 'NANOGRAMS_PER_MICROLITRE', 12, 'MICROLITRES', 'IPO200001', 'IPOT_POOL_1', 'ipobar200001', NULL, '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 0),
(200002, 6.5, 'NANOGRAMS_PER_MICROLITRE', NULL, NULL, 'IPO200002', 'IPOT_POOL_2', NULL, NULL, '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', NULL),
(200003, 6.5, 'NANOGRAMS_PER_MICROLITRE', 7.92, 'MICROLITRES', 'IPO200003', 'IPOT_POOL_3', 'ipobar200003', NULL, '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 1),
(200004, 6.5, 'NANOGRAMS_PER_MICROLITRE', 7.92, 'MICROLITRES', 'IPO200004', 'IPOT_POOL_4', 'ipobar200004', NULL, '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 1),
(200005, 6.5, 'NANOGRAMS_PER_MICROLITRE', 7.92, 'MICROLITRES', 'IPO200005', 'IPOT_POOL_5', 'ipobar200005', NULL, '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 1),
(200006, 6.5, 'NANOGRAMS_PER_MICROLITRE', 7.92, 'MICROLITRES', 'IPO200006', 'IPOT_POOL_6', 'ipobar200006', NULL, '2017-08-15', 'ILLUMINA', 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 1),
(5004, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO5004', 'RUN_POOL_ADD', 'ipobar5004', NULL, '2017-09-27', 'ILLUMINA', 1, 1, '2017-09-27 10:00:00', '2017-09-27 10:00:00', NULL),
(5005, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO5005', 'RUN_POOL_REMOVE', 'ipobar5005', NULL, '2017-09-27', 'ILLUMINA', 1, 1, '2017-09-27 10:00:00', '2017-09-27 10:00:00', NULL),
(5006, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO5006', 'RUN_POOL_INITIAL', 'ipobar50056', NULL, '2017-09-27', 'ILLUMINA', 1, 1, '2017-09-27 10:00:00', '2017-09-27 10:00:00', NULL),
(5007, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO5007', 'RUN_POOL_REPLACE', 'ipobar5007', NULL, '2017-09-27', 'ILLUMINA', 1, 1, '2017-09-27 10:00:00', '2017-09-27 10:00:00', NULL),
(5101, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO5101', 'POOL_SEARCH_1', 'ipobar5101', 'swimming', '2017-09-27', 'ILLUMINA', 1, 1, '2017-09-27 10:00:00', '2017-09-27 10:00:00', NULL),
(5102, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO5102', 'POOL_SEARCH_2', 'ipobar5102', 'cats', '2017-09-27', 'ILLUMINA', 1, 1, '2017-09-27 10:00:00', '2017-09-27 10:00:00', NULL),
(5103, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO5103', 'POOL_WITH_ORDERS', 'ipobar5103', 'sergeant', '2017-09-27', 'ILLUMINA', 1, 1, '2017-09-27 10:00:00', '2017-09-27 10:00:00', NULL),
(5104, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO5104', 'POOL_WITH_COMPLETED_ORDERS', 'ipobar5104', 'mission accomplished', '2017-09-27', 'ILLUMINA', 1, 1, '2017-09-27 10:00:00', '2017-09-27 10:00:00', NULL),
(5105, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO5105', 'POOL_NOT_READY', 'ipobar5105', 'unprepared', '2017-09-27', 'ILLUMINA', 1, 1, '2017-09-27 10:00:00', '2017-09-27 10:00:00', NULL),
(701, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO701', 'TEST_POOLING_ALIQUOTS', 'ipobar701', 'test pool', '2017-10-17', 'ILLUMINA', 3, 3, '2017-10-17 10:00:00', '2017-10-17 10:00:00', NULL),
(702, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO702', 'TEST_REMOVING_ALIQUOTS', 'ipobar702', 'test pool', '2017-10-17', 'ILLUMINA', 1, 1, '2017-10-17 10:00:00', '2017-10-17 10:00:00', NULL),
(801, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO801', 'TEST_NO_INDEX_WARNING', 'ipobar801', 'no indices', '2018-06-22', 'ILLUMINA', 1, 1, '2018-06-22 10:15:00', '2018-06-22', NULL),
(802, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO802', 'TEST_SIMILAR_INDEX_WARNING', 'ipobar802', 'similar index', '2018-06-22', 'ILLUMINA', 1, 1, '2018-06-22 10:15:00', '2018-06-22', NULL),
(803, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO803', 'TEST_SAME_INDEX_WARNING', 'ipobar803', 'same index', '2018-06-22', 'ILLUMINA', 1, 1, '2018-06-22 10:15:00', '2018-06-22', NULL),
(804, 4, 'NANOGRAMS_PER_MICROLITRE', 4, 'MICROLITRES', 'IPO804', 'TEST_BAD_LIBRARY_WARNING', 'ipobar804', 'low quality library', '2018-06-22', 'ILLUMINA', 1, 1, '2018-06-22 10:15:00', '2018-06-22', NULL);

INSERT INTO Pool (poolId, concentration, concentrationUnits, volume, volumeUnits, name, alias, identificationBarcode, description, creationDate, platformType, lastModifier, creator, created, lastModified, qcPassed) VALUES
(2201, 50, 'NANOGRAMS_PER_MICROLITRE', 60, 'MICROLITRES', 'IPO2201', 'AUTO_UPDATE_QC', 'ipobar2201', 'autoupdate qc', '2018-06-22', 'ILLUMINA', 1, 1, '2018-06-22 10:15:00', '2018-06-22', NULL);

INSERT INTO Pool_LibraryAliquot (poolId, aliquotId) VALUES
(1, 1),
(120001, 120001),
(120001, 120002),
(120002, 120001),
(120002, 120002),
(200001, 200001),
(200001, 200002),
(200002, 200001),
(200002, 200002),
(200003, 200001),
(200003, 200002),
(200004, 200001),
(200005, 200002),
(200005, 120001),
(200006, 200001),
(200006, 120001),
(501, 504),
(702, 702),
(801, 200001),
(801, 200002),
(802, 801),
(802, 802),
(803, 803),
(803, 804),
(804, 800);

INSERT INTO SequencingOrder (sequencingOrderId, poolId, partitions, parametersId, createdBy, updatedBy, creationDate, lastUpdated, purposeId) VALUES
(1, 5103, 2, 4, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00', 1),
(2, 5103, 2, 3, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00', 1),
(3, 5102, 2, 2, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00', 1),
(4, 5104, 2, 2, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00', 1),
(5, 801, 2, 2, 1, 1, '2018-06-26 10:10:00', '2018-06-26 10:10:00', 1),
(6, 802, 2, 2, 1, 1, '2018-06-26 10:10:00', '2018-06-26 10:10:00', 1),
(7, 803, 2, 2, 1, 1, '2018-06-26 10:10:00', '2018-06-26 10:10:00', 1),
(8, 804, 2, 2, 1, 1, '2018-06-26 10:10:00', '2018-06-26 10:10:00', 1);

INSERT INTO PoolOrder (poolOrderId, alias, description, purposeId, parametersId, partitions, draft, poolId, sequencingOrderId, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Pool Order One', 'first pool order', 2, 3, 1, FALSE, NULL, NULL, 1, '2019-07-24 16:15:00', 1, '2019-07-24 16:15:00'),
(2, 'Pool Order Two', 'second pool order', 1, 2, 2, FALSE, 802, 6, 1, '2019-07-24 16:15:00', 1, '2019-07-24 16:15:00'),
(3, 'Pool Order Three', NULL, 1, NULL, NULL, FALSE, NULL, NULL, 3, '2019-07-24 16:15:00', 3, '2019-07-24 16:15:00');

INSERT INTO PoolOrder_LibraryAliquot (poolOrderId, aliquotId, proportion) VALUES
(1, 1001, 1),
(1, 1002, 1),
(2, 801, 1),
(2, 802, 1),
(3, 1001, 1);

INSERT INTO Box (boxId, boxSizeId, boxUseId, name, alias, lastModifier, creator, created, lastModified) VALUES
(1, 1, 1, 'BOX1', 'First Box', 1, 1, '2017-07-20 13:01:01', '2017-07-20 13:01:01'),
(2, 1, 1, 'BOX2', 'Boxxy', 1, 1, '2018-08-30 15:15:00', '2018-08-30 15:15:00'),
(500, 1, 1, 'BOX500', 'Tubes In Boxes Test', 1, 1, '2017-08-15 13:55:00', '2017-08-15 13:55:00'),
(501, 1, 1, 'BOX501', 'Second box for Tubes in Boxes test', 3, 3, '2017-08-16 16:40:00', '2017-08-16 16:40:00'),
(502, 1, 2, 'BOX502', 'Editable box', 1, 1, '2017-08-16 16:40:00', '2017-08-16 16:40:00'),
(100001, 1, 1, 'BOX100001', 'Bulk Boxables Test', 1, 1, '2017-12-19 15:04:00', '2017-12-19 15:04:00');

INSERT INTO BoxPosition (boxId, targetId, targetType, position) VALUES
(1, 1,   'LIBRARY',  'A01'),
(1, 205, 'SAMPLE',   'A07'),
(1, 1,   'LIBRARY_ALIQUOT', 'B02'),
(1, 206, 'SAMPLE',   'B05'),
(1, 1,   'POOL',     'C03'),
(1, 204, 'SAMPLE',   'C06'),
(1, 2,   'SAMPLE',   'D04'),
(1, 3,   'SAMPLE',   'E05'),
(1, 4,   'SAMPLE',   'F06'),
(1, 7,   'SAMPLE',   'G07'),
(1, 8,   'SAMPLE',   'H08'),
(500, 502, 'SAMPLE', 'A01'),
(500, 504, 'LIBRARY', 'B01'),
(500, 504, 'LIBRARY_ALIQUOT', 'C01'),
(500, 501, 'POOL', 'D01'),
(500, 505, 'LIBRARY', 'F10'),
(100001, 100006, 'LIBRARY', 'A02'),
(100001, 100007, 'LIBRARY', 'A03'); 

INSERT INTO SequencerPartitionContainer (containerId, identificationBarcode, sequencingContainerModelId, lastModifier, creator, created, lastModified) VALUES
(1, 'MISEQXX', 1, 1, 1, '2017-07-20 13:30:01', '2017-07-20 13:30:01'),
(2, 'PACBIO1', 11, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5002, 'EXISTING', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5003, 'REMOVABLE', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5004, 'ADDPOOLS', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5005, 'REMOVE_POOL', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5006, 'REPLACE_POOL', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5008, 'FAIL_LANE', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5009, 'FAIL_LANE_WITH_NOTE', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5010, 'FAIL_LANE_TO_PASS', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5100, 'SEARCH_POOLS', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(5101, 'POOL_COMPLETED_ORDER', 2, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02'),
(6001, 'CHANGEABLE', 1, 3, 3, '2017-10-03 14:45', '2017-10-03 14:45');

INSERT INTO _Partition (containerId, partitionId, partitionNumber, pool_poolId) VALUES
(1, 11, 1, 1),(1, 12, 2, NULL),(1, 13, 3, NULL),(1, 14, 4, NULL),
(2, 21, 1, NULL),(2, 22, 2, NULL),(2, 23, 3, NULL),(2, 24, 4, NULL),(2, 25, 5, NULL),(2, 26, 6, NULL),(2, 27, 7, NULL),(2, 28, 8, NULL),
(5002, 5101, 1, NULL),(5002, 5102, 2, NULL),(5002, 5103, 3, NULL),(5002, 5104, 4, NULL),(5002, 5105, 5, NULL),(5002, 5106, 6, NULL),(5002, 5107, 7, NULL),(5002, 5108, 8, NULL),
(5003, 5201, 1, NULL),(5003, 5202, 2, NULL),(5003, 5203, 3, NULL),(5003, 5204, 4, NULL),(5003, 5205, 5, NULL),(5003, 5206, 6, NULL),(5003, 5207, 7, NULL),(5003, 5208, 8, NULL),
(5004, 5401, 1, NULL),(5004, 5402, 2, NULL),(5004, 5403, 3, NULL),(5004, 5404, 4, NULL),(5004, 5405, 5, NULL),(5004, 5406, 6, NULL),(5004, 5407, 7, NULL),(5004, 5408, 8, NULL),
(5005, 5501, 1, 5005),(5005, 5502, 2, 5005),(5005, 5503, 3, NULL),(5005, 5504, 4, NULL),(5005, 5505, 5, NULL),(5005, 5506, 6, NULL),(5005, 5507, 7, NULL),(5005, 5508, 8, NULL),
(5006, 5601, 1, 5006),(5006, 5602, 2, NULL),(5006, 5603, 3, NULL),(5006, 5604, 4, NULL),(5006, 5605, 5, NULL),(5006, 5606, 6, NULL),(5006, 5607, 7, NULL),(5006, 5608, 8, NULL),
(5008, 5801, 1, NULL),(5008, 5802, 2, NULL),(5008, 5803, 3, NULL),(5008, 5804, 4, NULL),(5008, 5805, 5, NULL),(5008, 5806, 6, NULL),(5008, 5807, 7, NULL),(5008, 5808, 8, NULL),
(5009, 5901, 1, NULL),(5009, 5902, 2, NULL),(5009, 5903, 3, NULL),(5009, 5904, 4, NULL),(5009, 5905, 5, NULL),(5009, 5906, 6, NULL),(5009, 5907, 7, NULL),(5009, 5908, 8, NULL),
(5010, 5111, 1, NULL),(5010, 5112, 2, NULL),(5010, 5113, 3, NULL),(5010, 5114, 4, NULL),(5010, 5115, 5, NULL),(5010, 5116, 6, NULL),(5010, 5117, 7, NULL),(5010, 5118, 8, NULL),
(5100, 51001, 1, NULL),(5100, 51002, 2, NULL),(5100, 51003, 3, NULL),(5100, 51004, 4, NULL),(5100, 51005, 5, NULL),(5100, 51006, 6, NULL),(5100, 51007, 7, NULL),(5100, 51008, 8, NULL),
(5101, 51011, 1, 5104),(5101, 51012, 2, 5104),(5101, 51013, 3, NULL),(5101, 51014, 4, NULL),(5101, 51015, 5, NULL),(5101, 51016, 6, NULL),(5101, 51017, 7, NULL),(5101, 51018, 8, NULL),
(6001, 60011, 1, NULL),(6001, 60012, 2, NULL),(6001, 60013, 3, NULL),(6001, 60014, 4, NULL);

INSERT INTO Run (runId, name, alias, instrumentId, startDate, completionDate, health, creator, created, lastModifier, lastModified) VALUES
(1, 'RUN1', 'MiSeq_Run_1', 2, '2017-08-02', '2017-08-03', 'Completed', 1, '2017-08-02 10:03:02', 1, '2017-08-03 10:03:02'),
(2, 'RUN2', 'PacBio_Run_1', 3, '2017-08-01', NULL, 'Running', 3, '2017-08-01 10:03:02', 3, '2017-08-01 10:03:02');

INSERT INTO Run (runId, name, alias, instrumentId, sequencingParameters_parametersId, description, filePath, startDate, completionDate, health, creator, created, lastModifier, lastModified) VALUES
(5001, 'RUN5001', 'Change_Values_Run', 5002, 2, 'description', '/filePath', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5002, 'RUN5002', 'Add_Existing_Container_Run', 5002, 2, 'add existing container to run', '/existing', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5003, 'RUN5003', 'Remove_Existing_Container_Run', 5002, 2, 'remove container from run', '/removable', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5004, 'RUN5004', 'Add_Pools_To_Container_Run', 5002, 2, 'add pools to container on run', '/add/pools', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5005, 'RUN5005', 'Remove_Pools_From_Container_Run', 5002, 2, 'remove pools from container on run', '/remove/pools', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5006, 'RUN5006', 'Replace_Pool_In_Container_Run', 5002, 2, 'replace pool in container on run', '/replace/pool', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5008, 'RUN5008', 'Fail_Lane_In_Container_Run', 5002, 2, 'fail lane in container on run', '/fail/lane', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5009, 'RUN5009', 'Fail_Lane_With_Note_Run', 5002, 2, 'fail lane with note on run', '/fail/note', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5010, 'RUN5010', 'Fail_Lane_To_Ok_Run', 5002, 2, 'fail lane change to ok', '/fail/ok', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5100, 'RUN5100', 'Search_Pool_Run', 2, 4, 'test pool searches', '/test/searches', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00'),
(5101, 'RUN5101', 'Pool_Completed_Orders_Run', 2, 2, 'sequencing orders are complete', '/complete/orders', '2017-09-05', NULL, 'Running', 1, '2017-09-05 11:00:00', 1, '2017-09-05 11:00:00');

INSERT INTO RunIllumina (runId, pairedEnd) VALUES (1, 1);
INSERT INTO RunIllumina (runId, callCycle, imgCycle, numCycles, scoreCycle, pairedEnd) VALUES
  (5001, 35, 34, 75, 33, 1),
  (5002, 50, 50, 50, 50, 1),
  (5003, 50, 50, 50, 50, 1),
  (5004, 50, 50, 50, 50, 1),
  (5005, 50, 50, 50, 50, 1),
  (5006, 50, 50, 50, 50, 1),
  (5008, 50, 50, 50, 50, 1),
  (5009, 50, 50, 50, 50, 1),
  (5010, 50, 50, 50, 50, 1),
  (5100, NULL, NULL, NULL, NULL, 1),
  (5101, NULL, NULL, NULL, NULL, 1);

INSERT INTO RunPacBio (runId) VALUES (2);

INSERT INTO Run_SequencerPartitionContainer (Run_runId, containers_containerId) VALUES
(1, 1),
(2, 2),
(5003, 5003),
(5004, 5004),
(5005, 5005),
(5006, 5006),
(5008, 5008),
(5009, 5009),
(5010, 5010),
(5100, 5100),
(5101, 5101);

INSERT INTO Run_Partition (runId, partitionId, purposeId, lastModifier) VALUES
(1, 11, 1, 1),
(1, 12, 1, 1),
(1, 13, 1, 1),
(1, 14, 1, 1),
(2, 21, 1, 1),
(2, 22, 1, 1),
(2, 23, 1, 1),
(2, 24, 1, 1),
(2, 25, 1, 1),
(2, 26, 1, 1),
(2, 27, 1, 1),
(2, 28, 1, 1),
(5003, 5201, 1, 1),
(5003, 5202, 1, 1),
(5003, 5203, 1, 1),
(5003, 5204, 1, 1),
(5003, 5205, 1, 1),
(5003, 5206, 1, 1),
(5003, 5207, 1, 1),
(5003, 5208, 1, 1),
(5004, 5401, 1, 1),
(5004, 5402, 1, 1),
(5004, 5403, 1, 1),
(5004, 5404, 1, 1),
(5004, 5405, 1, 1),
(5004, 5406, 1, 1),
(5004, 5407, 1, 1),
(5004, 5408, 1, 1),
(5005, 5501, 1, 1),
(5005, 5502, 1, 1),
(5005, 5503, 1, 1),
(5005, 5504, 1, 1),
(5005, 5505, 1, 1),
(5005, 5506, 1, 1),
(5005, 5507, 1, 1),
(5005, 5508, 1, 1),
(5006, 5601, 1, 1),
(5006, 5602, 1, 1),
(5006, 5603, 1, 1),
(5006, 5604, 1, 1),
(5006, 5605, 1, 1),
(5006, 5606, 1, 1),
(5006, 5607, 1, 1),
(5006, 5608, 1, 1),
(5008, 5801, 1, 1),
(5008, 5802, 1, 1),
(5008, 5803, 1, 1),
(5008, 5804, 1, 1),
(5008, 5805, 1, 1),
(5008, 5806, 1, 1),
(5008, 5807, 1, 1),
(5008, 5808, 1, 1),
(5009, 5901, 1, 1),
(5009, 5902, 1, 1),
(5009, 5903, 1, 1),
(5009, 5904, 1, 1),
(5009, 5905, 1, 1),
(5009, 5906, 1, 1),
(5009, 5907, 1, 1),
(5009, 5908, 1, 1),
(5010, 5111, 1, 1),
(5010, 5112, 1, 1),
(5010, 5113, 1, 1),
(5010, 5114, 1, 1),
(5010, 5115, 1, 1),
(5010, 5116, 1, 1),
(5010, 5117, 1, 1),
(5010, 5118, 1, 1),
(5100, 51001, 1, 1),
(5100, 51002, 1, 1),
(5100, 51003, 1, 1),
(5100, 51004, 1, 1),
(5100, 51005, 1, 1),
(5100, 51006, 1, 1),
(5100, 51007, 1, 1),
(5100, 51008, 1, 1),
(5101, 51011, 1, 1),
(5101, 51012, 1, 1),
(5101, 51013, 1, 1),
(5101, 51014, 1, 1),
(5101, 51015, 1, 1),
(5101, 51016, 1, 1),
(5101, 51017, 1, 1),
(5101, 51018, 1, 1);

INSERT INTO Note(noteId, creationDate, internalOnly, text, owner_userId) VALUES
  (1, '2017-08-22', 1, 'LIB110005 existing note', 3),
  (2, '2017-08-25', 1, 'IPO120001 existing note', 3);

INSERT INTO Library_Note(library_libraryId, notes_noteId) VALUES
  (110005, 1);

INSERT INTO Pool_Note(pool_poolId, notes_noteId) VALUES
  (120001, 2);

INSERT INTO ArrayModel(arrayModelId, alias, arrayModelRows, arrayModelColumns) VALUES
(1, 'Test BeadChip', 8, 1),
(2, 'Unused', 8, 1);

INSERT INTO Array(arrayId, alias, arrayModelId, serialNumber, description, creator, created, lastModifier, lastModified) VALUES
(1, 'Array_1', 1, '1234', 'test array', 1, '2018-01-26 17:11:00', 1, '2018-01-26 17:11:00'),
(2, 'Array_2', 1, '5678', 'array to delete', 3, '2020-02-27 13:16:00', 3, '2020-02-27 13:16:00');

INSERT INTO ArrayPosition(arrayId, position, sampleId) VALUES
(1, 'R01C01', 8),
(2, 'R01C01', 8);

INSERT INTO ArrayRun(arrayRunId, alias, instrumentId, arrayId, health, startDate, creator, created, lastModifier, lastModified) VALUES
(1, 'ArrayRun_1', 4, 1, 'Running', '2018-02-02', 3, '2018-02-02 15:40:00', 3, '2018-02-02 15:40:00'),
(2, 'to_delete', 4, 1, 'Running', '2025-07-10', 3, "2025-07-10", 3, "2025-07-10"),
(3, 'ArrayRun_3', 4, 1, 'Running', '2018-02-02', 3, '2018-02-02 15:40:00', 3, '2018-02-02 15:40:00');

INSERT INTO QCType(qcTypeId, name, description, qcTarget, units, archived, precisionAfterDecimal, correspondingField, autoUpdateField) VALUES
(101, 'test edit qc', '', 'Sample', 'test units', FALSE, 2, 'NONE', FALSE),
(102, 'update volume qc', '', 'Sample', 'µL', FALSE, 2, 'VOLUME', TRUE),
(103, 'update concentration qc', '', 'Sample', 'nM', FALSE, 2, 'CONCENTRATION', TRUE),
(104, 'test edit qc', '', 'Library', 'test units', FALSE, 2, 'NONE', FALSE),
(105, 'update volume qc', '', 'Library', 'µL', FALSE, 2, 'VOLUME', TRUE),
(106, 'update concentration qc', '', 'Library', 'nM', FALSE, 2, 'CONCENTRATION', TRUE),
(107, 'test edit qc', '', 'Pool', 'test units', FALSE, 2, 'NONE', FALSE),
(108, 'update volume qc', '', 'Pool', 'µL', FALSE, 2, 'VOLUME', TRUE),
(109, 'update concentration qc', '', 'Pool', 'nM', FALSE, 2, 'CONCENTRATION', TRUE),
(110, 'unused qc', '', 'Sample', 'things', FALSE, 2, 'NONE', FALSE);

INSERT INTO QcControl(controlId, qcTypeId, alias) VALUES
(1, 110, 'standard control');

INSERT INTO SampleQC(sample_sampleId, creator, date, type, results, created, lastModified) VALUES
(2201, 1, '2018-07-10', 101, 4.3, '2018-07-10 14:29:00', '2018-07-10 14:29:00');

INSERT INTO WorksetCategory(categoryId, alias) VALUES
(1, 'Category A'),
(2, 'Category B'),
(3, 'DeleteCategory');

INSERT INTO WorksetStage(stageId, alias) VALUES
(1, 'Extraction'),
(2, 'Library Preparation'),
(3, 'Sequencing'),
(4, 'DeleteStage');

INSERT INTO Workset(worksetId, alias, description, creator, created, lastModifier, lastModified) VALUES
(1, 'Workset One', 'Workset One description', 1, '2018-08-03 13:12:00', 1, '2018-08-03 13:12:00'),
(2, 'Workset Two', 'Workset Two description', 3, '2018-08-03 13:12:00', 3, '2018-08-03 13:12:00');

INSERT INTO LibraryQC(library_libraryId, creator, date, type, results, created, lastModified) VALUES
(2201, 1, '2018-07-10', 104, 4.3, '2018-07-10 14:29:00', '2018-07-10 14:29:00');

INSERT INTO PoolQC(pool_poolId, creator, date, type, results, created, lastModified) VALUES
(2201, 1, '2018-07-10', 107, 4.3, '2018-07-10 14:29:00', '2018-07-10 14:29:00');

INSERT INTO Workset_Sample(worksetId, sampleId) VALUES
(1, 100001),
(1, 100002),
(1, 100003);

INSERT INTO Workset_Library(worksetId, libraryId) VALUES
(1, 100001),
(1, 100002),
(1, 100003);

INSERT INTO Workset_LibraryAliquot(worksetId, aliquotId) VALUES
(1, 120001),
(1, 120002);

INSERT INTO StorageLabel(labelId, label) VALUES
(1, 'Label One'),
(2, 'Label Two'),
(3, 'Label to delete');

INSERT INTO StorageLocationMap(mapId, filename, description) VALUES
(1, 'floor_one.html', 'floor one map'),
(2, 'floor_two.html', 'floor two map'),
(3, 'unused.html', 'unused map');

INSERT INTO StorageLocation(locationId, locationUnit, parentLocationId, alias, creator, created, lastModifier, lastModified) VALUES
(1, 'ROOM', NULL, 'Room One', 1, '2019-05-22 13:10:00', 1, '2019-05-22 13:10:00'),
(2, 'ROOM', NULL, 'Room Two', 1, '2019-05-22 13:10:00', 1, '2019-05-22 13:10:00'),
(3, 'FREEZER', 1, 'Freezer One', 1, '2019-05-22 13:10:00', 1, '2019-05-22 13:10:00'),
(4, 'ROOM', NULL, 'Empty Room', 3, '2020-02-27 15:46:00', 3, '2020-02-27 15:46:00'),
(5, 'FREEZER', 1, 'Empty Freezer', 3, '2020-02-27 15:46:00', 3, '2020-02-27 15:46:00'),
(6, 'SHELF', 5, '1', 3, '2020-02-27 15:46:00', 3, '2020-02-27 15:46:00'),
(7, 'LOOSE_STORAGE', 6, '1', 3, '2020-02-27 15:46:00', 3, '2020-02-27 15:46:00');

INSERT INTO StorageLocation_ServiceRecord(recordId, locationId) VALUES
(153, 3);

INSERT INTO LibraryTemplate(libraryTemplateId, alias, defaultVolume, platformType, libraryTypeId, librarySelectionTypeId, libraryStrategyTypeId, kitDescriptorId, indexFamilyId, volumeUnits) VALUES
(1, 'TestLibTemp', 12.34, 'ILLUMINA', 1, 3, 5, 1, 1, 'MICROLITRES');

INSERT INTO LibraryTemplate_Index1(libraryTemplateId, `position`, indexId) VALUES
(1, 'A01', 1),
(1, 'A02', 2);

INSERT INTO DetailedLibraryTemplate(libraryTemplateId, libraryDesignId, libraryDesignCodeId) VALUES
(1, 3, 6);

INSERT INTO Experiment(experimentId, name, title, alias, study_studyId, instrumentModelId, library_libraryId, creator, created, lastModifier, lastModified) VALUES
(1, 'EXP1', 'Experiment One', 'Experiment One', 3, 2, 1, 1, '2020-02-20 11:47:00', 1, '2020-02-20 11:47:00'),
(2, 'EXP2', 'Unused Experiment', 'Unused Experiment', 3, 2, 1, 1, '2020-03-02 16:32:00', 1, '2020-03-02 16:32:00');

INSERT INTO Experiment_Run_Partition(experiment_experimentId, run_runId, partition_partitionId) VALUES
(1, 1, 11);

INSERT INTO Submission(submissionId, title, alias, creationDate) VALUES
(1, 'Submission One', 'Submission One', '2020-02-20');

INSERT INTO Submission_Experiment(submission_submissionId, experiments_experimentId) VALUES
(1, 1);

INSERT INTO DeliverableCategory (categoryId, name) VALUES
(1, "Data Release"),
(2, "Clinical Report"),
(3, "To Delete");

INSERT INTO Deliverable(deliverableId, name, categoryId) VALUES
(1, 'deliverable1', 1),
(2, 'deliverable2', 2),
(3, 'To Delete', 1);

INSERT INTO ContactRole(contactRoleId, name) VALUES
(2, 'role1'),
(3, 'role2'),
(4, 'To Delete');

INSERT INTO Printer(printerId, name, driver, backend, configuration, enabled, width, height, layout) VALUES
(1, 'Printer', 'BRADY', 'BRADY_FTP', '{"host:"127.0.0.1","pin":"0000"}', TRUE, 25, 25, '[{"element":"text", "contents":{"use":"ALIAS"}}]');

INSERT INTO ApiKey(keyId, userId, apiKey, apiSecret, creator, created) VALUES
(2, 1, "asdf", "ghjk", 1, "2025-07-08")
ON DUPLICATE KEY UPDATE 
  userId = 1,
  apiKey = "asdf",
  apiSecret = "ghjk",
  creator = 1,
  created = "2025-07-08";


-- Keep this at bottom - checked to verify that script has completed and constants all loaded
INSERT INTO AttachmentCategory(categoryId, alias) VALUES (4, 'last entry');