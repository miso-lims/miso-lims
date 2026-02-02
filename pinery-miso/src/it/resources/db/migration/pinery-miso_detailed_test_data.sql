INSERT INTO SampleClass (sampleClassId, alias, sampleCategory, sampleSubcategory, suffix, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Identity',             'Identity',          NULL,            NULL,  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(23, 'Tissue',              'Tissue',            NULL,            NULL,  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(24, 'Slide',               'Tissue Processing', 'Slide',         'SL',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(10, 'LCM Tube',            'Tissue Processing', 'LCM Tube',      'LCM', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(8, 'Curls',                'Tissue Processing', 'Curls',         'C',   1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(11, 'gDNA (stock)',        'Stock',             NULL,            'D_S', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(12, 'gDNA_wga (stock)',    'Stock',             NULL,            'D_S', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(13, 'whole RNA (stock)',   'Stock',             'RNA (stock)',   'R_S', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(14, 'cDNA (stock)',        'Stock',             NULL,            'D_S', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(15, 'gDNA (aliquot)',      'Aliquot',           NULL,            'D_',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(16, 'gDNA_wga (aliquot)',  'Aliquot',           NULL,            'D_',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(17, 'whole RNA (aliquot)', 'Aliquot',           'RNA (aliquot)', 'R_',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(18, 'smRNA',               'Aliquot',           'RNA (aliquot)', 'SM_', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(19, 'mRNA',                'Aliquot',           'RNA (aliquot)', 'MR_', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(20, 'rRNA_depleted',       'Aliquot',           'RNA (aliquot)', 'WT_', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(21, 'cDNA (aliquot)',      'Aliquot',           NULL,            'D_',  1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');

INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived) VALUES
( 8, 11, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
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
( 8, 13, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
( 1, 23, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23,  8, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 10, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 1),
(23, 11, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 12, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 13, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 14, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 23, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(24, 10, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(24, 11, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(24, 13, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0),
(23, 24, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00', 0);

INSERT INTO TissueMaterial(tissueMaterialId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Fresh Frozen', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'FFPE', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(3, 'Blood', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');

INSERT INTO TissueOrigin(tissueOriginId, alias, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Bn', 'Brain', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'Ly', 'Lymphocyte', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(3, 'Pa', 'Pancreas', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');

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

INSERT INTO Stain (stainId, name, stainCategoryId) VALUES
(1, 'Cresyl Violet', NULL),
(2, 'Hematoxylin+Eosin', NULL);

INSERT INTO TissuePieceType (tissuePieceTypeId, abbreviation, name, v2NamingCode) VALUES
(1, 'LCM', 'LCM Tube', 'TL'),
(2, 'C', 'Curls', 'TC');

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

INSERT INTO LibraryDesignCode (libraryDesignCodeId, code, description) VALUES
  (1, 'AS','ATAC-Seq'),
  (2, 'CH','ChIP-Seq'),
  (3, 'EX','Exome'),
  (4, 'MR','mRNA'),
  (5, 'SM','smRNA'),
  (6, 'TS','Targeted Sequencing'),
  (7, 'WG','Whole Genome'),
  (8, 'WT','Whole Transcriptome'),
  (16,'TR','Total RNA');

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
  (17,'TR',                   17,11,19,16);

INSERT INTO TargetedSequencing (targetedSequencingId, alias, description, archived, createdBy, updatedBy, creationDate, lastUpdated) VALUES
  (1, 'Test TarSeq One', 'first test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00'),
  (2, 'Test TarSeq Two', 'second test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00'),
  (3, 'Test TarSeq Three', 'third test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00');
  
INSERT INTO TargetedSequencing_KitDescriptor (targetedSequencingId, kitDescriptorId) VALUES
  (1, 1), (2, 1), (3, 2);

INSERT INTO Project(projectId, name, title, code, created, description,
  status, referenceGenomeId, lastModified, creator, lastModifier, pipelineId) VALUES
  (1, 'PRO1', 'Project One', 'PRO1', '2017-06-27', 'integration test project one', 'ACTIVE', 1, '2017-06-27 14:11:00', 1, 1, 1),
  (2, 'PRO2', 'Project Two', 'PRO2', '2017-06-27', 'integration test project two', 'ACTIVE', 1, '2017-06-27 14:11:00', 1, 1, 2);

-- Identities
INSERT INTO Sample (sampleId, name, alias, description, identificationBarcode, sampleType, project_projectId, scientificNameId, volume, lastModifier, creator, created, lastModified,
sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcDate, archived, discriminator,
externalName, donorSex, consentLevel) VALUES
(1, 'SAM1', 'TEST_0001', 'Identity', '11111', 'GENOMIC', 1, 1, NULL, 1, 1, '2016-07-20 09:00:00', '2016-07-20 09:00:00',
  1, NULL, NULL, NULL, NULL, 1, NULL, '2016-07-20', 0, 'Identity',
  'TEST_external_1', 'MALE', 'THIS_PROJECT'),
(15, 'SAM15', 'PRO2_0001', 'Identity', '15151', 'GENOMIC', 2, 1, NULL, 1, 1, '2017-07-20 09:00:00', '2017-07-20 09:00:00',
  1, NULL, NULL, NULL, NULL, 1, NULL, '2016-07-20', 1, 'Identity',
  'PRO2_external_1', 'FEMALE', 'THIS_PROJECT');

-- Tissues
INSERT INTO Sample (sampleId, name, alias, description, identificationBarcode, sampleType, project_projectId, scientificNameId, volume, lastModifier, creator, created, lastModified,
sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcDate, archived, discriminator,
tissueOriginId, tissueTypeId, secondaryIdentifier, labId, region, passageNumber, tubeNumber, timesReceived, tissueMaterialId) VALUES
(2, 'SAM2', 'TEST_0001_Bn_R_nn_1-1', 'Tissue', '22222', 'GENOMIC', 1, 1, 30, 1, 1, '2016-07-20 09:01:00', '2016-07-20 09:01:00',
  23, 1, NULL, '7357', 'TEST', 1, NULL, '2016-07-20', 0, 'Tissue',
  1, 1, 'tube 1', 2, 'cortex', NULL, 1, 1, 2);

-- Slides
INSERT INTO Sample (sampleId, name, alias, description, identificationBarcode, sampleType, project_projectId, scientificNameId, volume, lastModifier, creator, created, lastModified,
sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcDate, archived, discriminator,
slides) VALUES
(3, 'SAM3', 'TEST_0001_Bn_R_nn_1-1_SL01', 'Slide', '33333', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  24, 2, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'Slide',
  15);

-- Tissue Pieces
INSERT INTO Sample (sampleId, name, alias, description, identificationBarcode, sampleType, project_projectId, scientificNameId, volume, lastModifier, creator, created, lastModified,
sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcDate, archived, discriminator,
tissuePieceType, slidesConsumed) VALUES
(4, 'SAM4', 'TEST_0001_Bn_R_nn_1-1_C01', 'Curls', '44444', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  8, 2, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'TissuePiece',
  2, 0),
(5, 'SAM5', 'TEST_0001_Bn_R_nn_1-1_LCM01', 'LCM Tube', '55555', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  10, 3, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'TissuePiece',
  1, 10);

-- Stocks
INSERT INTO Sample (sampleId, name, alias, description, identificationBarcode, sampleType, project_projectId, scientificNameId, volume, lastModifier, creator, created, lastModified,
sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcDate, archived, discriminator,
strStatus, dnaseTreated) VALUES
(6, 'SAM6', 'TEST_0001_Bn_R_nn_1-1_D_S1', 'gDNA stock', '66666', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  11, 5, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'Stock',
  'SUBMITTED', 0),
(7, 'SAM7', 'TEST_0001_Bn_R_nn_1-1_R_S1', 'whole RNA stock', '77777', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  13, 2, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'StockRna',
  'NOT_SUBMITTED', 0),
(10, 'SAM10', 'TEST_0001_Bn_R_nn_1-1_D_S2', 'cDNA stock', '10101', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  14, 7, 2, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'Stock',
  'PASS', 0);

-- Aliquots
INSERT INTO Sample (sampleId, name, alias, description, identificationBarcode, sampleType, project_projectId, scientificNameId, volume, lastModifier, creator, created, lastModified,
sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, qcDate, archived, discriminator,
samplePurposeId) VALUES
(8, 'SAM8', 'TEST_0001_Bn_R_nn_1-1_D_1', 'gDNA aliquot', '88888', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  15, 6, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'Aliquot',
  9),
(9, 'SAM9', 'TEST_0001_Bn_R_nn_1-1_R_1', 'whole RNA aliquot', '99999', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  17, 7, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'AliquotRna',
  3),
(11, 'SAM11', 'TEST_0001_Bn_R_nn_1-1_D_2', 'cDNA aliquot', '11011', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  21, 10, 2, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'Aliquot',
  4),
(12, 'SAM12', 'TEST_0001_Bn_R_nn_1-1_R_1_SM_1', 'smRNA', '12121', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  18, 9, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'AliquotRna',
  6),
(13, 'SAM13', 'TEST_0001_Bn_R_nn_1-1_R_1_MR_1', 'mRNA', '13131', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  19, 9, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'AliquotRna',
  7),
(14, 'SAM14', 'TEST_0001_Bn_R_nn_1-1_R_1_WT_1', 'rRNA_depleted', '14141', 'GENOMIC', 1, 1, NULL, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00',
  20, 9, 1, '7357', 'TEST', 1, NULL, '2017-07-20', 0, 'AliquotRna',
  3);

INSERT INTO Transfer(transferId, transferTime, senderLabId, recipientGroupId, creator, created, lastModifier, lastModified) VALUES
(1, '2017-07-20 12:00:00', 1, 1, 1, '2017-07-20 12:53:00', 1, '2017-07-20 12:53:00');

INSERT INTO Transfer_Sample(transferId, sampleId, received, qcPassed, qcNote) VALUES
(1, 2, TRUE, TRUE, NULL);

INSERT INTO SampleChangeLog(sampleId, columnsChanged, message, userId, changeTime) VALUES
(1,'one','change oneone',1,'2016-07-20 09:00:00'),
(1,'two','change onetwo',1,'2016-07-20 09:00:01'),
(2,'one','change twoone',1,'2016-07-20 09:00:00'),
(2,'two','change twotwo',1,'2016-07-20 09:00:01');

INSERT INTO Library(libraryId, name, alias, identificationBarcode, description, sample_sampleId, platformType,
  libraryType, librarySelectionType, libraryStrategyType, creationDate, creator, created, lastModifier, lastModified, detailedQcStatusId, qcDate, dnaSize,
  volume, concentration, locationBarcode, kitDescriptorId, index1Id, discriminator,
  archived, libraryDesign, libraryDesignCodeId, nonStandardAlias) VALUES
  (1, 'LIB1', 'TEST_0001_Bn_R_PE_300_WG', '11211', 'description lib 1', 8, 'ILLUMINA',
    1, 3, 1,  '2016-11-07', 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00', 1, '2017-07-20', 300,
    5.0, 2.75, NULL, 1, 5, 'DetailedLibrary',
    0, 1, 7, FALSE);

INSERT INTO LibraryAliquot (aliquotId, name, alias, concentration, libraryId, identificationBarcode, creationDate, creator, lastModifier, lastUpdated, discriminator, libraryDesignCodeId, nonStandardAlias) VALUES
(1, 'LDI1', 'TEST_0001_Bn_R_PE_300_WG', 5.9, 1, '12321', '2017-07-20', 1, 1, '2017-07-20 09:01:00', 'DetailedLibraryAliquot', 7, FALSE);

INSERT INTO Pool (poolId, concentration, volume, name, alias, identificationBarcode, description, creationDate, platformType, lastModifier, creator, created, lastModified, qcPassed) VALUES
(1, 8.25, NULL, 'IPO1', 'POOL_1', '12341', NULL, '2017-07-20', 'ILLUMINA', 1, 1, '2017-07-20 10:01:00', '2017-07-20 10:01:00', NULL);

INSERT INTO Pool_LibraryAliquot (poolId, aliquotId) VALUES
(1, 1);

INSERT INTO Box (boxId, boxSizeId, boxUseId, name, alias, lastModifier, creator, created, lastModified) VALUES
(1, 1, 1, 'BOX1', 'First Box', 1, 1, '2017-07-20 13:01:01', '2017-07-20 13:01:01');

INSERT INTO BoxPosition (boxId, targetId, targetType, position) VALUES
(1, 1, 'LIBRARY', 'A01'),
(1, 1, 'LIBRARY_ALIQUOT', 'B02'),
(1, 1, 'POOL', 'C03'),
(1, 2, 'SAMPLE', 'D04'),
(1, 3, 'SAMPLE', 'E05'),
(1, 4, 'SAMPLE', 'F06'),
(1, 7, 'SAMPLE', 'G07'),
(1, 8, 'SAMPLE', 'H08');

INSERT INTO SequencerPartitionContainer (containerId, identificationBarcode, sequencingContainerModelId, lastModifier, creator, created, lastModified) VALUES
(1, 'MISEQXX', 1, 1, 1, '2017-07-20 13:30:01', '2017-07-20 13:30:01');

INSERT INTO `_Partition` (containerId, partitionId, partitionNumber, pool_poolId) VALUES 
(1, 1, 1, 1);

INSERT INTO Run (runId, name, alias, instrumentId, startDate, completionDate, health, creator, created, lastModifier, lastModified) VALUES
(1, 'RUN1', 'MiSeq_Run_1', 2, '2017-08-02', '2017-08-03', 'Completed', 1, '2017-08-02 10:03:02', 1, '2017-08-03 10:03:02');

INSERT INTO RunIllumina (runId, pairedEnd) VALUES
(1, 1);

INSERT INTO Run_SequencerPartitionContainer (Run_runId, containers_containerId) VALUES
(1, 1);

INSERT INTO RunPurpose(purposeId, alias) VALUES
(1, 'Production');

INSERT INTO SequencingOrder (sequencingOrderId, poolId, partitions, parametersId, createdBy, updatedBy, creationDate, lastUpdated, purposeId) VALUES
(1, 1, 2, 4, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00', 1),
(2, 1, 1, 1, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00', 1);
