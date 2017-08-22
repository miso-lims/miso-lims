-- fixes LibraryDesign changes that were marked "NoTest" in V0130
ALTER TABLE LibraryDesign DROP CONSTRAINT `uk_libraryDesign_name`;
ALTER TABLE LibraryDesign ADD CONSTRAINT `uk_libraryDesign_name_sampleClass` UNIQUE (`name`, `sampleClassId`);

INSERT INTO `User` (`userId`, `active`, `admin`, `external`, `fullName`, `internal`, `loginName`, `password`, `email`)
VALUES (3,1,0,0,'user',1,'user','user','user@user.user');

DELETE FROM `ReferenceGenome`;
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (1, 'Human hg19 random');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (2, 'Human hg19');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (3, 'Human hg18 random');

DELETE FROM SampleClass;
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

DELETE FROM SampleValidRelationship;
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

DELETE FROM TissueMaterial;
INSERT INTO TissueMaterial(alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES
('Fresh Frozen', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
('FFPE', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
('Blood', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');

DELETE FROM TissueOrigin;
INSERT INTO TissueOrigin(tissueOriginId, alias, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Bn', 'Brain', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'Ly', 'Lymphocyte', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(3, 'Pa', 'Pancreas', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');

DELETE FROM TissueType;
INSERT INTO TissueType(tissueTypeId, alias, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'R','Reference or non-tumour, non-diseased tissue sample. Typically used as a donor-specific comparison to a diseased tissue, usually a cancer',1,'2016-09-26 15:55:43',1,'2016-09-26 15:55:43',),
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

DELETE FROM Institute;
INSERT INTO Institute(instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'University Health Network',1,'2017-07-07 16:34:00',1,'2017-07-07 16:34:00');

DELETE FROM Lab;
INSERT INTO Lab(labId, alias, instituteId, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'BioBank', 1, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'Pathology', 1, 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');

DELETE FROM Stain;
INSERT INTO Stain (stainId, name, stainCategoryId) VALUES
(1, 'Cresyl Violet', NULL),
(2, 'Hematoxylin+Eosin', NULL);

DELETE FROM SamplePurpose;
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

DELETE FROM SamplePurpose;
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

DELETE FROM DetailedQcStatus;
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

DELETE FROM QCType;
INSERT INTO QCType (name, description, qcTarget, units, archived, precisionAfterDecimal) VALUES
('RIN', 'RIN', 'Sample', ' ', 0, 1),
('DV200', 'DV200', 'Sample', 'percent', 0, 2),
('Tape Station', 'Tape Station', 'Library', 'bp', 0, 2),
('Qubit', 'Qubit', 'Library', 'ng/ul', 0, 2),
('qPCR', 'qPCR', 'Library', 'mol/ul', 0, 2);

DELETE FROM LibraryType;
INSERT INTO LibraryType(libraryTypeId, description, platformType, archived, abbreviation) VALUES
  (1, 'Paired End',  'ILLUMINA',0,'PE'),
  (2, 'Mate Pair',   'ILLUMINA',0,'MP'),
  (17,'Single End',  'ILLUMINA',0,'SE'),
  (27,'Total RNA',   'ILLUMINA',0,'TR'),
  (19,'cDNA',        'PACBIO',  0,NULL),
  (28,'Whole Genome','PACBIO',  0,NULL);

DELETE FROM LibrarySelectionType;
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

DELETE FROM LibraryStrategyType;
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

DELETE FROM LibraryDesignCode;
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

DELETE FROM LibraryDesign;
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

DELETE FROM KitDescriptor;
INSERT INTO KitDescriptor (kitDescriptorId, name, version, manufacturer, partNumber, kitType, platformType, lastModifier) VALUES
  (1, 'Test Kit', 1, 'TestCo', '123', 'LIBRARY', 'ILLUMINA', 1),
  (2, 'Test Kit Two', 2, 'TestCo', '124', 'LIBRARY', 'ILLUMINA', 1);
  
DELETE FROM TargetedSequencing;
INSERT INTO TargetedSequencing (targetedSequencingId, alias, description, archived, createdBy, updatedBy, creationDate, lastUpdated) VALUES
  (1, 'Test TarSeq One', 'first test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00'),
  (2, 'Test TarSeq Two', 'second test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00'),
  (3, 'Test TarSeq Three', 'third test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00');
  
INSERT INTO TargetedSequencing_KitDescriptor (targetedSequencingId, kitDescriptorId) VALUES
  (1, 1), (2, 1), (3, 2);

DELETE FROM BoxUse;
INSERT INTO BoxUse (alias) VALUES ('DNA'), ('RNA'), ('Libraries'), ('Sequencing'), ('Storage'), ('Tissue');

DELETE FROM BoxSize;
INSERT INTO BoxSize (`rows`, `columns`, `scannable`) VALUES (8, 12, 1), (10, 10, 0);

DELETE FROM `_Group`;
INSERT INTO `_Group` (description, name) VALUES
('TestGroup1', 'TestGroup1'), ('TestGroup2', 'TestGroup2');

INSERT INTO `User_Group` (`users_userId`, `groups_groupId`)
VALUES (3,1),(3,2),(1,1);

DELETE FROM `SecurityProfile`;
DELETE FROM `SecurityProfile_ReadGroup`;
DELETE FROM `SecurityProfile_WriteGroup`;
DELETE FROM `SecurityProfile_ReadUser`;
DELETE FROM `SecurityProfile_WriteUser`;
INSERT INTO `SecurityProfile`(`profileId`, `allowAllInternal`, `owner_userId`) 
VALUES (1,1,1),(2,1,1),(3,1,1),(4,1,1),(5,1,1),(6,1,1),(7,1,1),(8,1,1),(9,1,1),(10,1,1),(11,1,1),(12,1,NULL),(13,1,NULL),(14,1,NULL),(15,1,NULL);

INSERT INTO SecurityProfile_ReadUser(SecurityProfile_profileId, readUser_userId) VALUES (1, 1);
INSERT INTO SecurityProfile_WriteUser(SecurityProfile_profileId, writeUser_userId) VALUES (2, 1);
INSERT INTO SecurityProfile_ReadGroup(SecurityProfile_profileId, readGroup_groupId) VALUES (3, 2);
INSERT INTO SecurityProfile_WriteGroup(SecurityProfile_profileId, writeGroup_groupId) VALUES (4, 2);

DELETE FROM Indices;
DELETE FROM IndexFamily;
INSERT INTO IndexFamily (indexFamilyId, name, platformType) VALUES
  (1, 'Single Index 6bp', 'ILLUMINA'),
  (2, 'Dual Index 6bp', 'ILLUMINA');

INSERT INTO Indices (indexId, indexFamilyId, name, sequence, position) VALUES
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
  (12, 2, 'B04',      'TTTAAA', 2);

DELETE FROM SequencingParameters;
DELETE FROM Platform;
INSERT INTO Platform (platformId, name, instrumentModel, numContainers) VALUES
  (1, 'ILLUMINA', 'Illumina HiSeq 2500', 1),
  (2, 'ILLUMINA', 'Illumina MiSeq', 1),
  (3, 'PACBIO', 'PacBio RS II', 1);

DELETE FROM SequencerReference;
INSERT INTO SequencerReference (referenceId, name, platformId, ip) VALUES
  (1, 'T2000', 1, RAWTOHEX('127.0.0.1')),
  (2, 'TMS1', 2, RAWTOHEX('127.0.0.1')),
  (3, 'TPB1', 3, RAWTOHEX('127.0.0.1'));

DELETE FROM ProjectOverview;
DELETE FROM Project;
INSERT INTO Project(projectId, name, alias, shortName, creationDate, description, securityProfile_profileId,
  progress, referenceGenomeId, lastUpdated) VALUES
  (1, 'PRO1', 'Project One', 'PRO1', '2017-06-27', 'integration test project one', 1, 'ACTIVE', 1, '2017-06-27 14:11:00'),
  (2, 'PRO2', 'Project Two', 'PRO2', '2017-07-20', 'integration test project for custom identities', 2, 'ACTIVE', 1, '2017-07-20 16:55:00'),
  (3, 'PRO3', 'Test Data', 'TEST', '2017-06-27', 'integration test project three', 2, 'ACTIVE', 1, '2017-06-27 14:12:00'),
  (4, 'PRO4', 'Project To Change', 'DELTA', '2017-08-04', 'integration test project for changing fields', 2, 'PROPOSED', 2, '2017-08-04 15:12:00'),
  (100001, 'PRO100001', 'BulkLibraryIT', 'LIBT', '2017-07-24', 'bulk library test project', 1, 'ACTIVE', 1, '2017-07-24 16:11:00'),
  (110001, 'PRO110001', 'SingleLibraryIT', '1LIB', '2017-08-16', 'single library test project', 1, 'ACTIVE', 1, '2017-08-16 16:11:00'),
  (200001, 'PRO200001', 'BulkPoolIT', 'IPOT', '2017-08-15', 'bulk Pool test project', 1, 'ACTIVE', 1, '2017-08-15 12:22:00'),
  (200, 'PRO200', 'HotSorting', 'SORT', '2017-08-09', 'test sorting by BoxPosition in Handsontable', 1, 'ACTIVE', 1, '2017-08-09 11:51:00'),
  (300, 'PRO300', 'BulkDilutionIT', 'DILT', '2017-08-14', 'bulk dilution test project', 1, 'ACTIVE', 1, '2017-08-14 11:54:00'),
  (400, 'PRO400', 'StudiesIT', 'STUT', '2017-08-16', 'studies test project', 1, 'ACTIVE', 1, '2017-08-16 14:50:00');

INSERT INTO Study (studyId, name, securityProfile_profileId, project_projectId, alias, lastModifier, studyTypeId) VALUES
(1, 'STU1', 1, 1, 'Study One', 1, 1),
(400, 'STU400', 1, 400, 'UI Test Study', 1, 1);

INSERT INTO Sample (sampleId, name, alias, description, securityProfile_profileId, identificationBarcode, sampleType, receivedDate, project_projectId,
scientificName, volume, qcPassed, lastModifier, creator, created, lastModified) VALUES
(1, 'SAM1', 'TEST_0001', 'Identity', 2, '11111', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:00:00', '2017-07-20 09:00:00'),
(2, 'SAM2', 'TEST_0001_Bn_R_nn_1-1', 'Tissue', 2, '22222', 'GENOMIC', '2017-07-20', 3, 'Homo sapiens', 30, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(3, 'SAM3', 'TEST_0001_Bn_R_nn_1-1_SL01', 'Slide', 2, '33333', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(4, 'SAM4', 'TEST_0001_Bn_R_nn_1-1_C01', 'Curls', 2, '44444', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(5, 'SAM5', 'TEST_0001_Bn_R_nn_1-1_LCM01', 'LCM Tube', 2, '55555', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(6, 'SAM6', 'TEST_0001_Bn_R_nn_1-1_D_S1', 'gDNA stock', 2, '66666', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(7, 'SAM7', 'TEST_0001_Bn_R_nn_1-1_R_S1', 'whole RNA stock', 2, '77777', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(8, 'SAM8', 'TEST_0001_Bn_R_nn_1-1_D_1', 'gDNA aliquot', 2, '88888', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(9, 'SAM9', 'TEST_0001_Bn_R_nn_1-1_R_1', 'whole RNA aliquot', 2, '99999', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(10, 'SAM10', 'TEST_0001_Bn_R_nn_1-1_D_S2', 'cDNA stock', 2, '10101', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(11, 'SAM11', 'TEST_0001_Bn_R_nn_1-1_D_2', 'cDNA aliquot', 2, '11011', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(12, 'SAM12', 'TEST_0001_Bn_R_nn_1-1_R_1_SM_1', 'smRNA', 2, '12121', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(13, 'SAM13', 'TEST_0001_Bn_R_nn_1-1_R_1_MR_1', 'mRNA', 2, '13131', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(14, 'SAM14', 'TEST_0001_Bn_R_nn_1-1_R_1_WT_1', 'rRNA_depleted', 2, '14141', 'GENOMIC', NULL, 3, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00');

INSERT INTO Sample(sampleId, project_projectId, name, alias, securityProfile_profileId, sampleType, scientificName, creator, created,
  lastModifier, lastModified) VALUES
  (100001, 100001, 'SAM100001', 'LIBT_0001', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (100002, 100001, 'SAM100002', 'LIBT_0001_Ly_P_1-1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (100003, 100001, 'SAM100003', 'LIBT_0001_Ly_P_1-1_D_S1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (100004, 100001, 'SAM100004', 'LIBT_0001_Ly_P_1-1_D1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (110001, 110001, 'SAM110001', '1LIB_0001', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (110002, 110001, 'SAM110002', '1LIB_0001_Ly_P_1-1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (110003, 110001, 'SAM110003', '1LIB_0001_Ly_P_1-1_D_S1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (110004, 110001, 'SAM110004', '1LIB_0001_Ly_P_1-1_D1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (200001, 200001, 'SAM200001', 'IPOT_0001', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (200002, 200001, 'SAM200002', 'IPOT_0001_Pa_P_1-1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (200003, 200001, 'SAM200003', 'IPOT_0001_Pa_P_1-1_D_S1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (200004, 200001, 'SAM200004', 'IPOT_0001_Pa_P_1-1_D_1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00'),
  (201, 200, 'SAM201', 'SORT_0001', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00'),
  (202, 200, 'SAM202', 'SORT_0001_nn_n_1-1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00'),
  (203, 200, 'SAM203', 'SORT_0001_nn_n_1-1_D_S1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00'),
  (204, 200, 'SAM204', 'SORT_0001_nn_n_1-1_D_1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00'),
  (205, 200, 'SAM205', 'SORT_0001_nn_n_1-1_D_2', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00'),
  (206, 200, 'SAM206', 'SORT_0001_nn_n_1-1_D_3', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-09 11:51:00', 1, '2017-08-09 11:51:00'),
  (301, 300, 'SAM301', 'DILT_0001', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00'),
  (302, 300, 'SAM302', 'DILT_0001_nn_n_1-1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00'),
  (303, 300, 'SAM303', 'DILT_0001_nn_n_1-1_D_S1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00'),
  (304, 300, 'SAM304', 'DILT_0001_nn_n_1-1_D_1', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00'),
  (305, 300, 'SAM305', 'DILT_0001_nn_n_1-1_D_2', 1, 'GENOMIC', 'Homo sapiens', 1, '2017-08-14 11:55:00', 1, '2017-08-14 11:55:00');

INSERT INTO DetailedSample (sampleId, sampleClassId, parentId, siblingNumber, groupId, groupDescription, detailedQcStatusId, detailedQcStatusNote, concentration) VALUES
(1, 1, NULL, NULL, NULL, NULL, 1, NULL, NULL),
(2, 23, 1, NULL, '7357', 'TEST', 1, NULL, NULL),
(3, 24, 2, 1, '7357', 'TEST', 1, NULL, NULL),
(4, 8, 2, 1, '7357', 'TEST', 1, NULL, NULL),
(5, 10, 3, 1, '7357', 'TEST', 1, NULL, NULL),
(6, 11, 5, 1, '7357', 'TEST', 1, NULL, NULL),
(7, 13, 2, 1, '7357', 'TEST', 1, NULL, NULL),
(8, 15, 6, 1, '7357', 'TEST', 1, NULL, NULL),
(9, 17, 7, 1, '7357', 'TEST', 1, NULL, NULL),
(10, 14, 7, 2, '7357', 'TEST', 1, NULL, NULL),
(11, 21, 10, 2, '7357', 'TEST', 1, NULL, NULL),
(12, 18, 9, 1, '7357', 'TEST', 1, NULL, NULL),
(13, 19, 9, 1, '7357', 'TEST', 1, NULL, NULL),
(14, 20, 9, 1, '7357', 'TEST', 1, NULL, NULL);

INSERT INTO DetailedSample(sampleId, sampleClassId, parentId, detailedQcStatusId, archived) VALUES
  (100001, 1, NULL, 1, 0),  -- Identity
  (100002, 23, NULL, 1, 0), -- Tissue
  (100003, 11, NULL, 1, 0), -- gDNA (stock)
  (100004, 15, NULL, 1, 0), -- gDNA (aliquot)
  (110001, 1, NULL, 1, 0),  -- Identity
  (110002, 23, NULL, 1, 0), -- Tissue
  (110003, 11, NULL, 1, 0), -- gDNA (stock)
  (110004, 15, NULL, 1, 0), -- gDNA (aliquot)
  (200001, 1, NULL, 1, 0),  -- Identity
  (200002, 23, NULL, 1, 0), -- Tissue
  (200003, 11, NULL, 1, 0), -- gDNA (stock)
  (200004, 15, NULL, 1, 0), -- gDNA (aliquot)
  (201, 1, NULL, 1, 0),
  (202, 23, NULL, 1, 0),
  (203, 11, NULL, 1, 0),
  (204, 15, NULL, 1, 0),
  (205, 15, NULL, 1, 0),
  (206, 15, NULL, 1, 0),
  (301, 1, NULL, 1, 0),
  (302, 23, NULL, 1, 0),
  (303, 11, NULL, 1, 0),
  (304, 15, NULL, 1, 0),
  (305, 15, NULL, 1, 0);

INSERT INTO Identity (sampleId, externalName, donorSex) VALUES
  (1, 'TEST_external_1', 'MALE'),
  (100001, 'LIBT_identity1', 'UNKNOWN'),
  (110001, '1LIB_identity1', 'UNKNOWN'),
  (200001, 'IPOT_identity1', 'UNKNOWN'),
  (201, 'SORT_identity_1', 'UNKNOWN'),
  (301, 'DILT_identity_1', 'FEMALE');

INSERT INTO `SampleTissue` (sampleId, tissueOriginId, tissueTypeId, externalInstituteIdentifier, labId, region, passageNumber, tubeNumber, timesReceived, tissueMaterialId) VALUES
  (2, 1, 1, 'tube 1', 2, 'cortex', NULL, 1, 1, 2),
  (100002, 2, 2, NULL, NULL, NULL, NULL, 1, 1, NULL),
  (110002, 2, 2, NULL, NULL, NULL, NULL, 1, 1, NULL),
  (200002, 3, 2, NULL, NULL, NULL, NULL, 1, 1, NULL),
  (202, NULL, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL),
  (302, NULL, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL);

INSERT INTO SampleTissueProcessing(sampleId) VALUES
(3),(4),(5);

INSERT INTO `SampleSlide` (sampleId, slides) VALUES
(3, 15);

INSERT INTO `SampleLCMTube` (sampleId, slidesConsumed) VALUES
(5, 10);

INSERT INTO `SampleStock` (sampleId, strStatus, dnaseTreated) VALUES
(6, 'SUBMITTED', 0),
(7, 'NOT_SUBMITTED', 0),
(10, 'PASS', 0),
(100003, 'NOT_SUBMITTED', 0),
(110003, 'NOT_SUBMITTED', 0),
(200003, 'NOT_SUBMITTED', 0),
(203, 'NOT_SUBMITTED', 0),
(303, 'NOT_SUBMITTED', 0);

INSERT INTO `SampleAliquot` (sampleId, samplePurposeId) VALUES
(8, 9),
(9, 3),
(11, 4),
(12, 6),
(13, 7),
(14, 3),
(100004, NULL),
(110004, NULL),
(200004, NULL),
(204, NULL),
(205, NULL),
(206, NULL),
(304, NULL),
(305, NULL);

INSERT INTO Library(libraryId, name, alias, identificationBarcode, description, securityProfile_profileId, sample_sampleId, platformType,
  libraryType, librarySelectionType, libraryStrategyType, creationDate, creator, created, lastModifier, lastModified, qcPassed, dnaSize,
  volume, concentration, locationBarcode) VALUES
  (1, 'LIB1', 'TEST_0001_Bn_R_PE_300_WG', '11211', 'description lib 1', 2, 8, 'ILLUMINA', 1, 3, 1,  '2016-11-07', 
    1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00', 1, 300, 5.0, 2.75, NULL),
  (100001, 'LIB100001', 'LIBT_0001_Ly_P_PE_251_WG', 'libbar100001', 'libdesc100001', 1, 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 0,    251,  2.5,  10, NULL),
  (100002, 'LIB100002', 'LIBT_0001_Ly_P_PE_252_WG', 'libbar100002', 'libdesc100002', 1, 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 0,    252,  4,    6.3, NULL),
  (100003, 'LIB100003', 'LIBT_0001_Ly_P_PE_253_WG', NULL,           NULL,            1, 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL),
  (100004, 'LIB100004', 'LIBT_0001_Ly_P_PE_254_WG', NULL,           'libdesc100004', 1, 100004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL),
  (110001, 'LIB110001', '1LIB_0001_Ly_P_PE_251_WG', 'libbar110001', 'libdesc110001', 1, 110004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 0,    251,  2.5,  10, NULL),
  (110002, 'LIB110002', '1LIB_0001_Ly_P_PE_252_WG', 'libbar110002', 'libdesc110002', 1, 110004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 0,    252,  4,    6.3, 'lib_location_110002'),
  (110003, 'LIB110003', '1LIB_0001_Ly_P_PE_253_WG', NULL,           NULL,            1, 110004, 'ILLUMINA', 1, NULL, NULL, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, NULL, NULL, NULL),
  (110004, 'LIB110004', '1LIB_0001_Ly_P_PE_254_WG', NULL,           'libdesc110004', 1, 110004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, 5, NULL, NULL),
  (110005, 'LIB110005', '1LIB_0001_Ly_P_PE_255_WG', NULL,           'libdesc110005', 1, 110004, 'ILLUMINA', 1, NULL, NULL, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', NULL, NULL, 5, NULL, NULL),
  (200001, 'LIB200001', 'IPOT_0001_Pa_P_PE_251_WG', 'libbar200001', 'libdesc200001', 1, 200004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 0,    251,  2.5,  10, NULL),
  (200002, 'LIB200002', 'IPOT_0001_Pa_P_PE_252_WG', 'libbar200002', 'libdesc200002', 1, 200004, 'ILLUMINA', 1, 3, 1, '2017-07-24',
    1, '2017-07-24 16:11:00', 1, '2017-07-24 16:11:00', 0,    252,  4,    6.3, NULL),
  (204, 'LIB204', 'SORT_0001_nn_n_PE_204_WG', NULL, 'description', 1, 204, 'ILLUMINA', 1, 3, 1, '2017-08-09',
    1, '2017-08-09 11:58:00', 1, '2017-08-09 11:58:00', NULL, NULL, NULL, NULL, NULL),
  (205, 'LIB205', 'SORT_0001_nn_n_PE_205_WG', NULL, 'description', 1, 205, 'ILLUMINA', 1, 3, 1, '2017-08-09',
    1, '2017-08-09 11:58:00', 1, '2017-08-09 11:58:00', NULL, NULL, NULL, NULL, NULL),
  (206, 'LIB206', 'SORT_0001_nn_n_PE_206_WG', NULL, 'description', 1, 206, 'ILLUMINA', 1, 3, 1, '2017-08-09',
    1, '2017-08-09 11:58:00', 1, '2017-08-09 11:58:00', NULL, NULL, NULL, NULL, NULL),
  (304, 'LIB304', 'DILT_0001_nn_n_PE_304_WG', NULL, 'description', 1, 304, 'ILLUMINA', 1, 3, 1, '2017-08-14',
    1, '2017-08-14 12:05:00', 1, '2017-08-14 12:05:00', NULL, NULL, NULL, NULL, NULL),
  (305, 'LIB305', 'DILT_0001_nn_n_PE_305_WG', NULL, 'description', 1, 305, 'ILLUMINA', 1, 3, 1, '2017-08-14',
    1, '2017-08-14 12:05:00', 1, '2017-08-14 12:05:00', NULL, NULL, NULL, NULL, NULL),
  (306, 'LIB306', 'DILT_0001_nn_n_PE_306_WG', NULL, 'description', 1, 304, 'ILLUMINA', 1, 3, 1, '2017-08-14',
    1, '2017-08-14 12:05:00', 1, '2017-08-14 12:05:00', NULL, NULL, NULL, NULL, NULL);

INSERT INTO DetailedLibrary(libraryId, kitDescriptorId, archived, libraryDesign, libraryDesignCodeId) VALUES
  (1, 1, 0, 1, 7),
  (100001, 1, 0, NULL, 7),
  (100002, 1, 0, 1, 7),
  (100003, 1, 0, NULL, 7),
  (100004, 1, 0, NULL, 7),
  (110001, 1, 0, NULL, 7),
  (110002, 1, 0, 1, 7),
  (110003, 1, 0, NULL, 7),
  (110004, 1, 0, NULL, 7),
  (110005, 1, 0, NULL, 3),
  (200001, 1, 0, NULL, 7),
  (200002, 1, 0, 1, 7),
  (204, 1, 0, NULL, 7),
  (205, 1, 0, NULL, 7),
  (206, 1, 0, NULL, 7),
  (304, 1, 0, NULL, 7),
  (305, 1, 0, NULL, 7),
  (306, 1, 0, NULL, 7);

INSERT INTO Library_Index(library_libraryId, index_indexId) VALUES
  (100001, 5),
  (100001, 9),
  (100002, 6),
  (100002, 10),
  (110001, 5),
  (110001, 9),
  (110002, 5),
  (110002, 9),
  (110005, 5),
  (110005, 9),
  (304, 5),
  (305, 6),
  (306, 9);
  
INSERT INTO LibraryDilution (dilutionId, name, concentration, library_libraryId, identificationBarcode, creationDate, dilutionUserName, securityProfile_profileId, lastModifier, lastUpdated) VALUES
(1, 'LDI1', 5.9, 1, '12321', '2017-07-20', 'admin', 2, 1, '2017-07-20 09:01:00'),
(304, 'LDI304', 7.97, 304, '300304', '2017-08-14', 'admin', 1, 1, '2017-08-14 12:25:00'),
(305, 'LDI305', 7.97, 305, '300305', '2017-08-14', 'admin', 1, 1, '2017-08-14 12:25:00'),
(200001, 'LDI200001', 4, 200001, NULL, '2017-08-15', 'admin', 2, 1, '2017-08-15 09:01:00'),
(200002, 'LDI200002', 3, 200002, NULL, '2017-08-15', 'admin', 2, 1, '2017-08-15 09:01:00');

INSERT INTO Pool (poolId, concentration, volume, name, alias, identificationBarcode, creationDate, securityProfile_profileId, platformType, ready, lastModifier, creator, created, lastModified, qcPassed) VALUES
(1, 8.25, NULL, 'IPO1', 'POOL_1', '12341', '2017-07-20', 2, 'ILLUMINA', 1, 1, 1, '2017-07-20 10:01:00', '2017-07-20 10:01:00', NULL),
(200001, 6.5, 12, 'IPO200001', 'IPOT_POOL_1', 'ipobar200001', '2017-08-15', 2, 'ILLUMINA', 0, 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 0),
(200002, 6.5, NULL, 'IPO200002', 'IPOT_POOL_2', NULL, '2017-08-15', 2, 'ILLUMINA', 1, 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', NULL),
(200003, 6.5, 7.92, 'IPO200003', 'IPOT_POOL_3', 'ipobar200003', '2017-08-15', 2, 'ILLUMINA', 1, 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 1),
(200004, 6.5, 7.92, 'IPO200004', 'IPOT_POOL_4', 'ipobar200004', '2017-08-15', 2, 'ILLUMINA', 1, 1, 1, '2017-08-15 10:01:00', '2017-08-15 10:01:00', 1);

INSERT INTO Pool_Dilution (pool_poolId, dilution_dilutionId) VALUES
(1, 1),
(200001, 200001),
(200001, 200002),
(200002, 200001),
(200002, 200002),
(200003, 200001),
(200003, 200002),
(200004, 200001);

INSERT INTO Box (boxId, boxSizeId, boxUseId, name, alias, securityProfile_profileId, lastModifier, creator, created, lastModified) VALUES
(1, 1, 1, 'BOX1', 'First Box', 1, 1, 2, '2017-07-20 13:01:01', '2017-07-20 13:01:01');

INSERT INTO BoxPosition (boxId, targetId, targetType, position) VALUES
(1, 1, 'LIBRARY', 'A01'), (1, 1, 'DILUTION', 'B02'), (1, 1, 'POOL', 'C03'), (1, 2, 'SAMPLE', 'D04'), (1, 3, 'SAMPLE', 'E05'), (1, 4, 'SAMPLE', 'F06'), (1, 7, 'SAMPLE', 'G07'), (1, 8, 'SAMPLE', 'H08'),
(1, 204, 'SAMPLE', 'C06'), (1, 205, 'SAMPLE', 'A07'), (1, 206, 'SAMPLE', 'B05'); 

INSERT INTO SequencerPartitionContainer (containerId, securityProfile_profileId, identificationBarcode, platform, lastModifier, creator, created, lastModified) VALUES
(1, 3, 'ABCDEFXX', 2, 1, 1, '2017-07-20 13:30:01', '2017-07-20 13:30:01'),
(2, 4, 'PACBIO1', 3, 1, 1, '2017-07-21 10:03:02', '2017-07-21 10:03:02');

INSERT INTO `_Partition` (partitionId, partitionNumber, pool_poolId) VALUES 
(1, 1, 1), (2, 1, NULL);

INSERT INTO SequencerPartitionContainer_Partition (container_containerId, partitions_partitionId) VALUES
(1, 1), (2, 2);

INSERT INTO Run (runId, name, securityProfile_profileId, alias, sequencerReference_sequencerReferenceId, startDate, completionDate, health, creator, created, lastModifier, lastModified) VALUES
(1, 'RUN1', 5, 'MiSeq_Run_1', 2, '2017-08-02', '2017-08-03', 'Completed', 1, '2017-08-02 10:03:02', 1, '2017-08-03 10:03:02'),
(2, 'RUN2', 5, 'PacBio_Run_1', 3, '2017-08-01', NULL, 'Running', 1, '2017-08-01 10:03:02', 1, '2017-08-01 10:03:02');

INSERT INTO RunIllumina (runId, pairedEnd) VALUES (1, 1);

INSERT INTO RunPacBio (runId) VALUES (2);

INSERT INTO Run_SequencerPartitionContainer (Run_runId, containers_containerId) VALUES (1, 1), (2, 2);