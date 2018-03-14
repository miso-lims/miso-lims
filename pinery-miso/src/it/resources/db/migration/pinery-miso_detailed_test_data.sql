-- Additional value types

INSERT INTO SampleClass (sampleClassId, alias, sampleCategory, suffix, dnaseTreatable, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Identity',             'Identity',          NULL,  0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(23, 'Tissue',              'Tissue',            NULL,  0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(24, 'Slide',               'Tissue Processing', 'SL',  0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(10, 'LCM Tube',            'Tissue Processing', 'LCM', 0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(8, 'Curls',                'Tissue Processing', 'C',   0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(11, 'gDNA (stock)',        'Stock',             'D_S', 0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(12, 'gDNA_wga (stock)',    'Stock',             'D_S', 0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(13, 'whole RNA (stock)',   'Stock',             'R_S', 1, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(14, 'cDNA (stock)',        'Stock',             'D_S', 0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(15, 'gDNA (aliquot)',      'Aliquot',           'D_',  0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(16, 'gDNA_wga (aliquot)',  'Aliquot',           'D_',  0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(17, 'whole RNA (aliquot)', 'Aliquot',           'R_',  0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(18, 'smRNA',               'Aliquot',           'SM_', 0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(19, 'mRNA',                'Aliquot',           'MR_', 0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(20, 'rRNA_depleted',       'Aliquot',           'WT_', 0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(21, 'cDNA (aliquot)',      'Aliquot',           'D_',  0, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');

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

INSERT INTO Institute(instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'University Health Network',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00');

INSERT INTO Lab(labId, alias, instituteId, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'BioBank', 1, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'Pathology', 1, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');

INSERT INTO Stain (stainId, name, stainCategoryId) VALUES
(1, 'Cresyl Violet', NULL),
(2, 'Hematoxylin+Eosin', NULL);

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

INSERT INTO `DetailedQcStatus` (DetailedQcStatusId, status, description, noteRequired, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1,TRUE,  'Ready',                  0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(2,TRUE,  'OKd by Collaborator',    1,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(4,NULL,  'Waiting: Path Report',   0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(5,FALSE, 'Failed: STR',            1,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(6,FALSE, 'Failed: Diagnosis',      1,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(7,FALSE, 'Failed: QC',             0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(8,NULL,  'Reference Required',     0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(9,FALSE, 'Refused Consent',        0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(10,NULL, 'Waiting: Receive Tissue',0,1,'2016-09-26 15:55:46',1,'2016-09-26 15:55:46');

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


-- Sample Hierarchy

INSERT INTO DetailedSample (sampleId, sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, concentration, archived) VALUES
(1, 1, NULL, NULL, NULL, NULL, 1, NULL, NULL, 0),
(2, 23, 1, NULL, '7357', 'TEST', 1, NULL, NULL, 0),
(3, 24, 2, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(4, 8, 2, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(5, 10, 3, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(6, 11, 5, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(7, 13, 2, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(8, 15, 6, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(9, 17, 7, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(10, 14, 7, 2, '7357', 'TEST', 1, NULL, NULL, 0),
(11, 21, 10, 2, '7357', 'TEST', 1, NULL, NULL, 0),
(12, 18, 9, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(13, 19, 9, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(14, 20, 9, 1, '7357', 'TEST', 1, NULL, NULL, 0),
(15, 1, NULL, NULL, NULL, NULL, 1, NULL, NULL, 1);

INSERT INTO Identity (sampleId, externalName, donorSex, consentLevel) VALUES
  (1, 'TEST_external_1', 'MALE', 'THIS_PROJECT'),
  (15, 'PRO2_external_1', 'FEMALE', 'THIS_PROJECT');

INSERT INTO `SampleTissue` (sampleId, tissueOriginId, tissueTypeId, secondaryIdentifier, labId, region, passageNumber, tubeNumber, timesReceived, tissueMaterialId) VALUES
  (2, 1, 1, 'tube 1', 2, 'cortex', NULL, 1, 1, 2);

INSERT INTO SampleTissueProcessing(sampleId) VALUES
(3),(4),(5);

INSERT INTO `SampleSlide` (sampleId, slides) VALUES
(3, 15);

INSERT INTO `SampleLCMTube` (sampleId, slidesConsumed) VALUES
(5, 10);

INSERT INTO `SampleStock` (sampleId, strStatus, dnaseTreated) VALUES
(6, 'SUBMITTED', 0),
(7, 'NOT_SUBMITTED', 0),
(10, 'PASS', 0);

INSERT INTO `SampleAliquot` (sampleId, samplePurposeId) VALUES
(8, 9),
(9, 3),
(11, 4),
(12, 6),
(13, 7),
(14, 3);

INSERT INTO DetailedLibrary(libraryId, archived, libraryDesign, libraryDesignCodeId) VALUES
  (1, 0, 1, 7);
