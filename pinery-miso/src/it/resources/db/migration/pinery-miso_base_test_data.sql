SET FOREIGN_KEY_CHECKS=0;
DELETE FROM SampleHierarchy;
DELETE FROM Transfer_Sample;
DELETE FROM Transfer_Library;
DELETE FROM Transfer_LibraryAliquot;
DELETE FROM Transfer_Pool;
DELETE FROM TransferChangeLog;
DELETE FROM Transfer;
DELETE FROM BoxPosition;
DELETE FROM BoxChangeLog;
DELETE FROM Box;
DELETE FROM _Partition;
DELETE FROM Run_SequencerPartitionContainer;
DELETE FROM ScientificName;
DELETE FROM SequencerPartitionContainerChangeLog;
DELETE FROM SequencerPartitionContainer;
DELETE FROM SequencingContainerModel_InstrumentModel;
DELETE FROM SequencingContainerModel;
DELETE FROM RunIllumina;
DELETE FROM RunIonTorrent;
DELETE FROM RunLS454;
DELETE FROM RunPacBio;
DELETE FROM RunOxfordNanopore;
DELETE FROM RunChangeLog;
DELETE FROM Run;
DELETE FROM SequencingOrder;
DELETE FROM Pool_LibraryAliquot;
DELETE FROM Pool_Note;
DELETE FROM PoolChangeLog;
DELETE FROM Pool;
DELETE FROM LibraryAliquotChangeLog;
DELETE FROM LibraryAliquot;
DELETE FROM Library_Note;
DELETE FROM LibraryChangeLog;
DELETE FROM Library;
DELETE FROM LibraryDesign;
DELETE FROM LibraryDesignCode;
DELETE FROM Sample_Note;
DELETE FROM SampleChangeLog;
DELETE FROM Sample;
DELETE FROM SampleValidRelationship;
DELETE FROM SampleClass;
DELETE FROM TissueMaterial;
DELETE FROM TissueOrigin;
DELETE FROM TissueType;
DELETE FROM Lab;
DELETE FROM Stain;
DELETE FROM SamplePurpose;
DELETE FROM DetailedQcStatus;
DELETE FROM QCType;
DELETE FROM LibraryType;
DELETE FROM LibrarySelectionType;
DELETE FROM LibraryStrategyType;
DELETE FROM TargetedSequencing_KitDescriptor;
DELETE FROM TargetedSequencing;
DELETE FROM KitDescriptorChangeLog;
DELETE FROM KitDescriptor;
DELETE FROM BoxUse;
DELETE FROM BoxSize;
DELETE FROM LibraryIndex;
DELETE FROM LibraryIndexFamily;
DELETE FROM SequencingParameters;
DELETE FROM Instrument WHERE upgradedInstrumentId IS NOT NULL;
DELETE FROM Instrument;
DELETE FROM InstrumentPosition;
DELETE FROM Experiment;
DELETE FROM InstrumentModel;
DELETE FROM StudyChangeLog;
DELETE FROM Study;
DELETE FROM ProjectChangeLog;
DELETE FROM Project;
DELETE FROM ReferenceGenome;
DELETE FROM Note;
DELETE FROM User_Group;
DELETE FROM User;
DELETE FROM _Group;
DELETE FROM RunPurpose;
DELETE FROM TissuePieceType;
DELETE FROM Pipeline;
SET FOREIGN_KEY_CHECKS=1;

INSERT INTO User (userId, active, admin, fullName, internal, loginName, password, email) VALUES
(1,1,1,'admin',1,'admin','admin','admin@admin.admin');

INSERT INTO `_Group` (groupId, description, name) VALUES
(1, 'TestGroup1', 'TestGroup1');

INSERT INTO `User_Group` (`users_userId`, `groups_groupId`)
VALUES (1,1);

INSERT INTO Pipeline(pipelineId, alias) VALUES
(1, 'Default'),
(2, 'Clinical');

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

INSERT INTO ScientificName(scientificNameId, alias) VALUES
(1, 'Homo sapiens');

INSERT INTO InstrumentModel (instrumentModelId, platform, alias, numContainers, instrumentType) VALUES
  (1, 'ILLUMINA', 'Illumina HiSeq 2500', 1, 'SEQUENCER'),
  (2, 'ILLUMINA', 'Illumina MiSeq', 1, 'SEQUENCER'),
  (3, 'PACBIO', 'Revio', 8, 'SEQUENCER');

INSERT INTO InstrumentPosition (positionId, instrumentModelId, alias) VALUES
(1, 3, '1_A01'),
(2, 3, '1_A02'),
(3, 3, '1_A03'),
(4, 3, '1_A04'),
(5, 3, '2_B01'),
(6, 3, '2_B02'),
(7, 3, '2_B03'),
(8, 3, '2_B04');
  
INSERT INTO SequencingParameters (parametersId, name, instrumentModelId, readLength, readLength2, createdBy, updatedBy, creationDate, lastUpdated, chemistry, movieTime) VALUES
  (1, 'Custom (see notes)', 3, 0, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', NULL, NULL),
  (2, 'Rapid Run 2x151', 1, 151, 151, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'RAPID_RUN', NULL),
  (3, '1x151', 1, 151, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V4', NULL),
  (4, 'Micro 2x151', 2, 151, 151, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V3', NULL),
  (5, 'Revio Custom', 3, 0, 0, 1, 1, '2025-11-06 07:45:00', '2025-11-06 07:45:00', NULL, 24);

INSERT INTO SequencingContainerModel (sequencingContainerModelId, alias, identificationBarcode, partitionCount, platformType, fallback) VALUES
(1, 'Generic 1-Lane Illumina Flow Cell', NULL, 1, 'ILLUMINA', TRUE),
(2, 'Revio SMRT Cell', NULL, 1, 'PACBIO', FALSE);

INSERT INTO SequencingContainerModel_InstrumentModel (sequencingContainerModelId, instrumentModelId) VALUES
(1, 2),
(2, 3);

INSERT INTO Instrument (instrumentId, name, instrumentModelId) VALUES
  (1, 'T2000', 1),
  (2, 'TMS1', 2),
  (3, 'Revio1', 3),
  (4, 'T2001', 1);

INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (1, 'Human hg19 random');

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

INSERT INTO KitDescriptor (kitDescriptorId, name, version, manufacturer, partNumber, kitType, platformType, creator, created, lastModifier, lastModified) VALUES
  (1, 'Test Kit', 1, 'TestCo', '123', 'LIBRARY', 'ILLUMINA', 1, '2018-04-24 12:20:00', 1, '2018-04-24 12:20:00'),
  (2, 'Test Kit Two', 2, 'TestCo', '124', 'LIBRARY', 'ILLUMINA', 1, '2018-04-24 12:20:00', 1, '2018-04-24 12:20:00'),
  (3, 'PacBio Test Kit', 1, 'PacBio', '125', 'LIBRARY', 'PACBIO', 1, '2025-11-06 08:00:00', 1, '2025-11-06 08:00:00');
  
INSERT INTO BoxUse (boxUseId, alias) VALUES 
(1, 'DNA'), (2, 'RNA'), (3, 'Libraries'), (4, 'Sequencing'), (5, 'Storage'), (6, 'Tissue');

INSERT INTO BoxSize (boxSizeId, `boxSizeRows`, `boxSizeColumns`, `scannable`) VALUES
(1, 8, 12, 1),
(2, 10, 10, 0);

INSERT INTO LibraryIndexFamily (indexFamilyId, name, platformType) VALUES
  (1, 'Single Index 6bp', 'ILLUMINA'),
  (2, 'Dual Index 6bp', 'ILLUMINA');

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
  (12, 2, 'B04',      'TTTAAA', 2);

INSERT INTO Lab(labId, alias, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'University Health Network - BioBank', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00'),
(2, 'University Health Network - Pathology', 1, '2017-07-07 16:34:00', 1, '2017-07-07 16:34:00');
