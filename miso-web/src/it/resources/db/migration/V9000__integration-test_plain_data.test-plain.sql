-- fixes BeforeInsertPool trigger created in V0004
DROP TRIGGER IF EXISTS BeforeInsertPool;

INSERT INTO `User` (`userId`, `active`, `admin`, `external`, `fullName`, `internal`, `loginName`, `password`, `email`)
VALUES (3,1,0,0,'user',1,'user','user','user@user.user');

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

DELETE FROM `ReferenceGenome`;
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (1, 'Triticum aestivum');

DELETE FROM SampleClass;
DELETE FROM SampleValidRelationship;
DELETE FROM TissueMaterial;
DELETE FROM TissueOrigin;
DELETE FROM TissueType;
DELETE FROM Institute;
DELETE FROM Lab;
DELETE FROM Stain;
DELETE FROM SamplePurpose;
DELETE FROM DetailedQcStatus;

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
DELETE FROM LibraryDesign;

DELETE FROM KitDescriptor;
INSERT INTO KitDescriptor (kitDescriptorId, name, version, manufacturer, partNumber, kitType, platformType, lastModifier) VALUES
  (1, 'Test Kit', 1, 'TestCo', '123', 'LIBRARY', 'ILLUMINA', 1),
  (2, 'Test Kit Two', 2, 'TestCo', '124', 'LIBRARY', 'ILLUMINA', 1);

DELETE FROM BoxUse;
INSERT INTO BoxUse (alias) VALUES ('DNA'), ('RNA'), ('Libraries'), ('Sequencing'), ('Storage'), ('Tissue');

DELETE FROM BoxSize;
INSERT INTO BoxSize (`rows`, `columns`, `scannable`) VALUES (8, 12, 1), (10, 10, 0);

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
DELETE FROM PlatformSizes;
DELETE FROM Platform;
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

DELETE FROM SequencerReference;
INSERT INTO SequencerReference (referenceId, name, platformId, ip) VALUES
  (1, 'T2500', 1, '127.0.0.1');
  
DELETE FROM ProjectOverview;
DELETE FROM Project;
INSERT INTO Project(projectId, name, alias, shortName, creationDate, description, securityProfile_profileId,
  progress, referenceGenomeId, lastUpdated) VALUES
  (1, 'PRO1', 'PLAIN', NULL, '2017-06-27', 'integration test project one', 1, 'ACTIVE', 1, '2017-06-27 14:11:00');
  
INSERT INTO Study (studyId, name, securityProfile_profileId, project_projectId, alias, lastModifier, studyTypeId) VALUES
(1, 'STU1', 1, 1, 'PLAIN Study One', 1, 1);

INSERT INTO Sample (sampleId, name, alias, description, securityProfile_profileId, identificationBarcode, sampleType, receivedDate, project_projectId,
scientificName, volume, qcPassed, lastModifier, creator, created, lastModified) VALUES
(1, 'SAM1', 'PLAIN_S0001_1', 'Plain', 2, 'SAM1::PLAIN_S0001_first', 'GENOMIC', '2017-07-20', 1, 'Triticum aestivum', NULL, 1, 1, 1, '2017-07-20 09:00:00', '2017-07-20 09:00:00');

INSERT INTO Library(libraryId, name, alias, identificationBarcode, description, securityProfile_profileId, sample_sampleId, platformType,
  libraryType, librarySelectionType, libraryStrategyType, creationDate, creator, created, lastModifier, lastModified, qcPassed, dnaSize,
  volume, concentration, locationBarcode, kitDescriptorId) VALUES
  (1, 'LIB1', 'PLAIN_L0001_1-1', 'LIB1::PLAIN_L0001_1-1', 'plain lib', 2, 1, 'ILLUMINA', 1, 3, 1,  '2016-11-07', 
    1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00', 1, 300, 5.0, 2.75, NULL, NULL);

INSERT INTO Library_Index(library_libraryId, index_indexId) VALUES
  (1, 5);

INSERT INTO LibraryDilution (dilutionId, name, concentration, library_libraryId, identificationBarcode, creationDate, dilutionUserName, securityProfile_profileId, lastModifier, lastUpdated) VALUES
(1, 'LDI1', 5.9, 1, 'LDI1::PLAIN_L0001_1-1', '2017-07-20', 'admin', 2, 1, '2017-07-20 09:01:00');

INSERT INTO Pool (poolId, concentration, volume, name, alias, identificationBarcode, description, creationDate, securityProfile_profileId, platformType, ready, lastModifier, creator, created, lastModified, qcPassed) VALUES
(1, 8.25, NULL, 'IPO1', 'POOL_1', 'IPO1::POOL_1', NULL, '2017-07-20', 2, 'ILLUMINA', 1, 1, 1, '2017-07-20 10:01:00', '2017-07-20 10:01:00', NULL);

INSERT INTO Pool_Dilution (pool_poolId, dilution_dilutionId) VALUES
(1, 1);