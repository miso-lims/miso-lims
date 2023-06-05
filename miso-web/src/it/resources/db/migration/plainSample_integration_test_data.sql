INSERT INTO User (userId, active, admin, fullName, internal, loginName, roles, password, email) VALUES
(1,1,1,'admin',1,'admin','ROLE_ADMIN,ROLE_INTERNAL','{SHA-1}d033e22ae348aeb5660fc2140aec35850c4da997','admin@admin'), -- password 'admin'
(3,1,0,'user',1,'user','ROLE_INTERNAL','{bcrypt}$2a$10$vqYii//w2shSZnt/4uNyIeeU4FGQIB4QJeisv9l16xVRQ1lTOghIO','user@user.user'); -- password 'user'

INSERT INTO _Group (groupId, description, name) VALUES
(1, 'TestGroup1', 'TestGroupOne'),
(2, 'TestGroup2', 'TestGroupTwo');

INSERT INTO User_Group (users_userId, groups_groupId)
VALUES (3,1),(3,2),(1,1);

INSERT INTO Pipeline(pipelineId, alias) VALUES
(1, 'Default'),
(2, 'Special');

INSERT INTO DetailedQcStatus (DetailedQcStatusId, status, description, noteRequired, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1,TRUE,  'Ready',                  0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44'),
(2,FALSE, 'Failed: QC',             0,1,'2016-09-26 15:55:44',1,'2016-09-26 15:55:44');

INSERT INTO ScientificName(scientificNameId, alias) VALUES
(1, 'Homo sapiens'),
(2, 'Triticum aestivum');

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

INSERT INTO ReferenceGenome (referenceGenomeId, alias) VALUES (1, 'Triticum aestivum');

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

INSERT INTO LibrarySpikeIn(spikeInId, alias) VALUES
  (1, 'Spike-In One'),
  (2, 'Spike-In Two');

INSERT INTO KitDescriptor (kitDescriptorId, name, version, manufacturer, partNumber, kitType, platformType, creator, created, lastModifier, lastModified) VALUES
  (1, 'Test Kit', 1, 'TestCo', '123', 'LIBRARY', 'ILLUMINA', 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
  (2, 'Test Kit Two', 2, 'TestCo', '124', 'LIBRARY', 'ILLUMINA', 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00');

INSERT INTO BoxUse (boxUseId, alias) VALUES
(1, 'DNA'), (2, 'RNA'), (3, 'Libraries'), (4, 'Sequencing'), (5, 'Storage'), (6, 'Tissue');

INSERT INTO BoxSize (boxSizeId, boxSizeRows, boxSizeColumns, scannable) VALUES
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

INSERT INTO InstrumentModel (instrumentModelId, platform, alias, numContainers, instrumentType) VALUES
  (1, 'ILLUMINA', 'Illumina HiSeq 2500', 1, 'SEQUENCER'),
  (2, 'ILLUMINA', 'Illumina MiSeq', 1, 'SEQUENCER'),
  (3, 'PACBIO', 'PacBio RS II', 1, 'SEQUENCER');
  
INSERT INTO SequencingParameters (parametersId, name, instrumentModelId, readLength, readLength2, createdBy, updatedBy, creationDate, lastUpdated, chemistry) VALUES
  (1, 'Custom (see notes)', 3, 0, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', NULL),
  (2, 'Rapid Run 2x151', 1, 151, 151, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'RAPID_RUN'),
  (3, '1x151', 1, 151, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V4'),
  (4, 'Micro 2x151', 2, 151, 151, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V3');

INSERT INTO Instrument (instrumentId, name, instrumentModelId) VALUES
  (1, 'T2500', 1);

INSERT INTO Project(projectId, name, title, code, created, description, status, referenceGenomeId, lastModified, creator, lastModifier, pipelineId) VALUES
  (1, 'PRO1', 'PLAIN', NULL, '2017-06-27', 'integration test project one', 'ACTIVE', 1, '2017-06-27 14:11:00', 1, 1, 1);
  
INSERT INTO Study (studyId, name, project_projectId, alias, studyTypeId, creator, created, lastModifier, lastModified) VALUES
(1, 'STU1',  1, 'PLAIN Study One', 1, 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00');

INSERT INTO Sample (sampleId, name, alias, description, identificationBarcode, sampleType, project_projectId,
scientificNameId, volume, detailedQcStatusId, qcUser, qcDate, lastModifier, creator, created, lastModified, discriminator) VALUES
(1, 'SAM1', 'PLAIN_S0001_1', 'Plain', 'SAM1::PLAIN_S0001_first', 'GENOMIC', 1,
  2, NULL, 1, 1, '2017-07-20', 1, 1, '2017-07-20 09:00:00', '2017-07-20 09:00:00', 'Sample');

INSERT INTO Lab(labId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'University Health Network - BioBank', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'University Health Network - Pathology', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');

INSERT INTO Transfer(transferId, transferTime, senderLabId, recipientGroupId, creator, created, lastModifier, lastModified) VALUES
(1, '2017-07-20 12:00:00', 1, 1, 1, '2017-07-20 12:53:00', 1, '2017-07-20 12:53:00');

INSERT INTO Transfer_Sample(transferId, sampleId, received, qcPassed, qcNote) VALUES
(1, 1, TRUE, TRUE, NULL);

INSERT INTO Library(libraryId, name, alias, identificationBarcode, description, sample_sampleId, platformType, index1Id,
  libraryType, librarySelectionType, libraryStrategyType, creationDate, creator, created, lastModifier, lastModified, detailedQcStatusId, qcUser, qcDate, dnaSize,
  volume, volumeUnits, concentration, concentrationUnits, locationBarcode, kitDescriptorId, discriminator) VALUES
  (1, 'LIB1', 'PLAIN_L0001-1_1', 'LIB1::PLAIN_L0001-1_1', 'plain lib', 1, 'ILLUMINA', 5,
    1, 3, 1,  '2016-11-07', 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00', 1, 1, '2017-07-20', 300,
    5.0, 'MICROLITRES', 2.75, 'NANOGRAMS_PER_MICROLITRE', NULL, 1, 'Library');

INSERT INTO LibraryAliquot (aliquotId, name, alias, concentration, concentrationUnits, libraryId, identificationBarcode, creationDate, creator, lastModifier, lastUpdated, discriminator) VALUES
(1, 'LDI1', 'PLAIN_L0001-1_1', 5.9, 'NANOGRAMS_PER_MICROLITRE', 1, 'LDI1::PLAIN_L0001_1-1', '2017-07-20', 1, 1, '2017-07-20 09:01:00', 'LibraryAliquot');

INSERT INTO Pool (poolId, concentration, volume, name, alias, identificationBarcode, description, creationDate, platformType, lastModifier, creator, created, lastModified, qcPassed) VALUES
(1, 8.25, NULL, 'IPO1', 'POOL_1', 'IPO1::POOL_1', NULL, '2017-07-20', 'ILLUMINA', 1, 1, '2017-07-20 10:01:00', '2017-07-20 10:01:00', NULL);

INSERT INTO Pool_LibraryAliquot (poolId, aliquotId) VALUES
(1, 1);

-- Keep this at bottom - checked to verify that script has completed and constants all loaded
INSERT INTO AttachmentCategory(categoryId, alias) VALUES (1, 'last entry');
