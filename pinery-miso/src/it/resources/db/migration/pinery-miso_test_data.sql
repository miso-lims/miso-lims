-- fixes BeforeInsertPool trigger created in V0004
DROP TRIGGER IF EXISTS BeforeInsertPool;

DELETE FROM `ReferenceGenome`;
DELETE FROM SampleValidRelationship;
DELETE FROM LibraryDesign;
DELETE FROM SampleClass;
DELETE FROM TissueMaterial;
DELETE FROM TissueOrigin;
DELETE FROM TissueType;
DELETE FROM Lab;
DELETE FROM Institute;
DELETE FROM Stain;
DELETE FROM SamplePurpose;
DELETE FROM DetailedQcStatus;
DELETE FROM QCType;
DELETE FROM LibraryType;
DELETE FROM LibrarySelectionType;
DELETE FROM LibraryStrategyType;
DELETE FROM LibraryDesignCode;
DELETE FROM TargetedSequencing_KitDescriptor;
DELETE FROM TargetedSequencing;
DELETE FROM KitDescriptorChangeLog;
DELETE FROM KitDescriptor;
DELETE FROM BoxUse;
DELETE FROM BoxSize;
DELETE FROM `_Group`;
DELETE FROM `SecurityProfile`;
DELETE FROM `SecurityProfile_ReadGroup`;
DELETE FROM `SecurityProfile_WriteGroup`;
DELETE FROM `SecurityProfile_ReadUser`;
DELETE FROM `SecurityProfile_WriteUser`;
DELETE FROM Indices;
DELETE FROM IndexFamily;
DELETE FROM SequencingParameters;
DELETE FROM PlatformSizes;
DELETE FROM SequencerReference WHERE upgradedSequencerReferenceId IS NOT NULL;
DELETE FROM SequencerReference;
DELETE FROM Platform;
DELETE FROM ProjectOverview;
DELETE FROM Project;
DELETE FROM Note;
DELETE FROM Library_Note;
DELETE FROM Pool_Note;
DELETE FROM Sample;

INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (1, 'Human hg19 random');

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

INSERT INTO QCType (name, description, qcTarget, units, archived, precisionAfterDecimal) VALUES
('RIN', 'RIN', 'Sample', ' ', 0, 1),
('DV200', 'DV200', 'Sample', 'percent', 0, 2),
('Tape Station', 'Tape Station', 'Library', 'bp', 0, 2),
('Qubit', 'Qubit', 'Library', 'ng/ul', 0, 2),
('qPCR', 'qPCR', 'Library', 'mol/ul', 0, 2);

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

INSERT INTO KitDescriptor (kitDescriptorId, name, version, manufacturer, partNumber, kitType, platformType, lastModifier) VALUES
  (1, 'Test Kit', 1, 'TestCo', '123', 'LIBRARY', 'ILLUMINA', 1),
  (2, 'Test Kit Two', 2, 'TestCo', '124', 'LIBRARY', 'ILLUMINA', 1);
  
INSERT INTO TargetedSequencing (targetedSequencingId, alias, description, archived, createdBy, updatedBy, creationDate, lastUpdated) VALUES
  (1, 'Test TarSeq One', 'first test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00'),
  (2, 'Test TarSeq Two', 'second test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00'),
  (3, 'Test TarSeq Three', 'third test targeted sequencing', 0, 1, 1, '2017-08-14 14:00:00', '2017-08-14 14:00:00');
  
INSERT INTO TargetedSequencing_KitDescriptor (targetedSequencingId, kitDescriptorId) VALUES
  (1, 1), (2, 1), (3, 2);

INSERT INTO BoxUse (boxUseId, alias) VALUES 
(1, 'DNA'), (2, 'RNA'), (3, 'Libraries'), (4, 'Sequencing'), (5, 'Storage'), (6, 'Tissue');

INSERT INTO BoxSize (boxSizeId, `rows`, `columns`, `scannable`) VALUES 
(1, 8, 12, 1),
(2, 10, 10, 0);

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

INSERT INTO Platform (platformId, name, instrumentModel, numContainers) VALUES
  (1, 'ILLUMINA', 'Illumina HiSeq 2500', 1),
  (2, 'ILLUMINA', 'Illumina MiSeq', 1),
  (3, 'PACBIO', 'PacBio RS II', 1);
  
INSERT INTO SequencingParameters (parametersId, name, platformId, readLength, paired, createdBy, updatedBy, creationDate, lastUpdated, chemistry) VALUES
  (1, 'Custom (see notes)', 3, 0, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', NULL),
  (2, 'Rapid Run 2x151', 1, 151, 1, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'RAPID_RUN'),
  (3, '1x151', 1, 151, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V4'),
  (4, 'Micro 2x151', 2, 151, 1, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V3');

INSERT INTO PlatformSizes(platform_platformId, partitionSize) VALUES
  (1, 4),
  (1, 8),
  (2, 1),
  (3, 1),
  (3, 2),
  (3, 3),
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
  (3, 16);

INSERT INTO SequencerReference (referenceId, name, platformId, ip) VALUES
  (1, 'T2000', 1, '127.0.0.1'),
  (2, 'TMS1', 2, '127.0.0.1'),
  (3, 'TPB2', 3, '127.0.0.1'),
  (4, 'T2001', 1, '127.0.0.1');

INSERT INTO `_Group` (description, name) VALUES
('TestGroup1', 'TestGroup1');

INSERT INTO `User_Group` (`users_userId`, `groups_groupId`)
VALUES (1,1);

INSERT INTO `SecurityProfile`(`profileId`, `allowAllInternal`, `owner_userId`) 
VALUES (1,1,1);

INSERT INTO SecurityProfile_ReadUser(SecurityProfile_profileId, readUser_userId) VALUES (1, 1);
INSERT INTO SecurityProfile_WriteUser(SecurityProfile_profileId, writeUser_userId) VALUES (1, 1);
INSERT INTO SecurityProfile_ReadGroup(SecurityProfile_profileId, readGroup_groupId) VALUES (1, 1);
INSERT INTO SecurityProfile_WriteGroup(SecurityProfile_profileId, writeGroup_groupId) VALUES (1, 1);

INSERT INTO Project(projectId, name, alias, shortName, creationDate, description, securityProfile_profileId,
  progress, referenceGenomeId, lastUpdated) VALUES
  (1, 'PRO1', 'Project One', 'PRO1', '2017-06-27', 'integration test project one', 1, 'ACTIVE', 1, '2017-06-27 14:11:00'),
  (2, 'PRO2', 'Project Two', 'PRO2', '2017-06-27', 'integration test project two', 1, 'ACTIVE', 1, '2017-06-27 14:11:00');

INSERT INTO Sample (sampleId, name, alias, description, securityProfile_profileId, identificationBarcode, sampleType, receivedDate, project_projectId,
scientificName, volume, qcPassed, lastModifier, creator, created, lastModified) VALUES
(1, 'SAM1', 'TEST_0001', 'Identity', 1, '11111', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2016-07-20 09:00:00', '2016-07-20 09:00:00'),
(2, 'SAM2', 'TEST_0001_Bn_R_nn_1-1', 'Tissue', 1, '22222', 'GENOMIC', '2017-07-20', 1, 'Homo sapiens', 30, 1, 1, 1, '2016-07-20 09:01:00', '2016-07-20 09:01:00'),
(3, 'SAM3', 'TEST_0001_Bn_R_nn_1-1_SL01', 'Slide', 1, '33333', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(4, 'SAM4', 'TEST_0001_Bn_R_nn_1-1_C01', 'Curls', 1, '44444', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(5, 'SAM5', 'TEST_0001_Bn_R_nn_1-1_LCM01', 'LCM Tube', 1, '55555', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(6, 'SAM6', 'TEST_0001_Bn_R_nn_1-1_D_S1', 'gDNA stock', 1, '66666', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(7, 'SAM7', 'TEST_0001_Bn_R_nn_1-1_R_S1', 'whole RNA stock', 1, '77777', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(8, 'SAM8', 'TEST_0001_Bn_R_nn_1-1_D_1', 'gDNA aliquot', 1, '88888', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(9, 'SAM9', 'TEST_0001_Bn_R_nn_1-1_R_1', 'whole RNA aliquot', 1, '99999', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(10, 'SAM10', 'TEST_0001_Bn_R_nn_1-1_D_S2', 'cDNA stock', 1, '10101', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(11, 'SAM11', 'TEST_0001_Bn_R_nn_1-1_D_2', 'cDNA aliquot', 1, '11011', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(12, 'SAM12', 'TEST_0001_Bn_R_nn_1-1_R_1_SM_1', 'smRNA', 1, '12121', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(13, 'SAM13', 'TEST_0001_Bn_R_nn_1-1_R_1_MR_1', 'mRNA', 1, '13131', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(14, 'SAM14', 'TEST_0001_Bn_R_nn_1-1_R_1_WT_1', 'rRNA_depleted', 1, '14141', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(15, 'SAM15', 'PRO2_0001', 'Identity', 1, '15151', 'GENOMIC', NULL, 2, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:00:00', '2017-07-20 09:00:00');

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

INSERT INTO Identity (sampleId, externalName, donorSex) VALUES
  (1, 'TEST_external_1', 'MALE'),
  (15, 'PRO2_external_1', 'FEMALE');

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

INSERT INTO SampleChangeLog(sampleId, columnsChanged, message, userId, changeTime) VALUES
(1,'one','change oneone',1,'2016-07-20 09:00:00'),
(1,'two','change onetwo',1,'2016-07-20 09:00:01'),
(2,'one','change twoone',1,'2016-07-20 09:00:00'),
(2,'two','change twotwo',1,'2016-07-20 09:00:01');

INSERT INTO Library(libraryId, name, alias, identificationBarcode, description, securityProfile_profileId, sample_sampleId, platformType,
  libraryType, librarySelectionType, libraryStrategyType, creationDate, creator, created, lastModifier, lastModified, qcPassed, dnaSize,
  volume, concentration, locationBarcode, kitDescriptorId) VALUES
  (1, 'LIB1', 'TEST_0001_Bn_R_PE_300_WG', '11211', 'description lib 1', 1, 8, 'ILLUMINA', 1, 3, 1,  '2016-11-07', 
    1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00', 1, 300, 5.0, 2.75, NULL, 1);

INSERT INTO DetailedLibrary(libraryId, archived, libraryDesign, libraryDesignCodeId) VALUES
  (1, 0, 1, 7);

INSERT INTO LibraryDilution (dilutionId, name, concentration, library_libraryId, identificationBarcode, creationDate, dilutionUserName, securityProfile_profileId, lastModifier, lastUpdated) VALUES
(1, 'LDI1', 5.9, 1, '12321', '2017-07-20', 'admin', 1, 1, '2017-07-20 09:01:00');

INSERT INTO Pool (poolId, concentration, volume, name, alias, identificationBarcode, description, creationDate, securityProfile_profileId, platformType, ready, lastModifier, creator, created, lastModified, qcPassed) VALUES
(1, 8.25, NULL, 'IPO1', 'POOL_1', '12341', NULL, '2017-07-20', 1, 'ILLUMINA', 1, 1, 1, '2017-07-20 10:01:00', '2017-07-20 10:01:00', NULL);

INSERT INTO Pool_Dilution (pool_poolId, dilution_dilutionId) VALUES
(1, 1);

INSERT INTO Box (boxId, boxSizeId, boxUseId, name, alias, securityProfile_profileId, lastModifier, creator, created, lastModified) VALUES
(1, 1, 1, 'BOX1', 'First Box', 1, 1, 1, '2017-07-20 13:01:01', '2017-07-20 13:01:01');

INSERT INTO BoxPosition (boxId, targetId, targetType, position) VALUES
(1, 1, 'LIBRARY', 'A01'),
(1, 1, 'DILUTION', 'B02'),
(1, 1, 'POOL', 'C03'),
(1, 2, 'SAMPLE', 'D04'),
(1, 3, 'SAMPLE', 'E05'),
(1, 4, 'SAMPLE', 'F06'),
(1, 7, 'SAMPLE', 'G07'),
(1, 8, 'SAMPLE', 'H08');

INSERT INTO SequencerPartitionContainer (containerId, securityProfile_profileId, identificationBarcode, platform, lastModifier, creator, created, lastModified) VALUES
(1, 1, 'MISEQXX', 2, 1, 1, '2017-07-20 13:30:01', '2017-07-20 13:30:01');

INSERT INTO `_Partition` (partitionId, partitionNumber, pool_poolId) VALUES 
(1, 1, 1);

INSERT INTO SequencerPartitionContainer_Partition (container_containerId, partitions_partitionId) VALUES
(1, 1);

INSERT INTO Run (runId, name, securityProfile_profileId, alias, sequencerReference_sequencerReferenceId, startDate, completionDate, health, creator, created, lastModifier, lastModified) VALUES
(1, 'RUN1', 1, 'MiSeq_Run_1', 2, '2017-08-02', '2017-08-03', 'Completed', 1, '2017-08-02 10:03:02', 1, '2017-08-03 10:03:02');

INSERT INTO RunIllumina (runId, pairedEnd) VALUES
(1, 1);

INSERT INTO Run_SequencerPartitionContainer (Run_runId, containers_containerId) VALUES
(1, 1);

INSERT INTO PoolOrder (poolOrderId, poolId, partitions, parametersId, createdBy, updatedBy, creationDate, lastUpdated) VALUES
(1, 1, 2, 4, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00'),
(2, 1, 1, 1, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00');
