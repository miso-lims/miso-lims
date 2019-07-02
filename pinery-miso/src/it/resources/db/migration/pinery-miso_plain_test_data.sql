-- fixes BeforeInsertPool trigger created in V0004
DROP TRIGGER IF EXISTS BeforeInsertPool;

DELETE FROM BoxPosition;
DELETE FROM BoxChangeLog;
DELETE FROM Box;
DELETE FROM SequencerPartitionContainer_Partition;
DELETE FROM _Partition;
DELETE FROM Run_SequencerPartitionContainer;
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
DELETE FROM DetailedLibrary;
DELETE FROM Library_Index;
DELETE FROM Library_Note;
DELETE FROM LibraryChangeLog;
DELETE FROM Library;
DELETE FROM LibraryDesign;
DELETE FROM LibraryDesignCode;
DELETE FROM SampleAliquot;
DELETE FROM SampleStock;
DELETE FROM SampleSlide;
DELETE FROM SampleLCMTube;
DELETE FROM SampleTissueProcessing;
DELETE FROM SampleTissue;
DELETE FROM Identity;
DELETE FROM DetailedSample;
DELETE FROM Sample_Note;
DELETE FROM SampleChangeLog;
DELETE FROM Sample;
DELETE FROM SampleValidRelationship;
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
DELETE FROM TargetedSequencing_KitDescriptor;
DELETE FROM TargetedSequencing;
DELETE FROM KitDescriptorChangeLog;
DELETE FROM KitDescriptor;
DELETE FROM BoxUse;
DELETE FROM BoxSize;
DELETE FROM Indices;
DELETE FROM IndexFamily;
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

INSERT INTO User (userId, active, admin, external, fullName, internal, loginName, password, email) VALUES
(1,1,1,0,'admin',1,'admin','admin','admin@admin.admin');

INSERT INTO `_Group` (description, name) VALUES
('TestGroup1', 'TestGroup1');

INSERT INTO `User_Group` (`users_userId`, `groups_groupId`)
VALUES (1,1);

INSERT INTO InstrumentModel (instrumentModelId, platform, alias, numContainers, instrumentType) VALUES
  (1, 'ILLUMINA', 'Illumina HiSeq 2500', 1, 'SEQUENCER'),
  (2, 'ILLUMINA', 'Illumina MiSeq', 1, 'SEQUENCER'),
  (3, 'PACBIO', 'PacBio RS II', 1, 'SEQUENCER');
  
INSERT INTO SequencingParameters (parametersId, name, instrumentModelId, readLength, paired, createdBy, updatedBy, creationDate, lastUpdated, chemistry) VALUES
  (1, 'Custom (see notes)', 3, 0, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', NULL),
  (2, 'Rapid Run 2x151', 1, 151, 1, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'RAPID_RUN'),
  (3, '1x151', 1, 151, 0, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V4'),
  (4, 'Micro 2x151', 2, 151, 1, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'V3');

INSERT INTO SequencingContainerModel (sequencingContainerModelId, alias, identificationBarcode, partitionCount, platformType, fallback) VALUES
(1, 'Generic 1-Lane Illumina Flow Cell', NULL, 1, 'ILLUMINA', 1);

INSERT INTO SequencingContainerModel_InstrumentModel (sequencingContainerModelId, instrumentModelId) VALUES
(1, 2);

INSERT INTO Instrument (instrumentId, name, instrumentModelId) VALUES
  (1, 'T2000', 1),
  (2, 'TMS1', 2),
  (3, 'TPB2', 3),
  (4, 'T2001', 1);

INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (1, 'Human hg19 random');

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

INSERT INTO KitDescriptor (kitDescriptorId, name, version, manufacturer, partNumber, kitType, platformType, creator, created, lastModifier, lastModified) VALUES
  (1, 'Test Kit', 1, 'TestCo', '123', 'LIBRARY', 'ILLUMINA', 1, '2018-04-24 12:20:00', 1, '2018-04-24 12:20:00'),
  (2, 'Test Kit Two', 2, 'TestCo', '124', 'LIBRARY', 'ILLUMINA', 1, '2018-04-24 12:20:00', 1, '2018-04-24 12:20:00');
  
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

-- Plain sample data

INSERT INTO Project(projectId, name, alias, shortName, created, description,
  progress, referenceGenomeId, lastModified, creator, lastModifier) VALUES
  (1, 'PRO1', 'Project One', 'PRO1', '2017-06-27', 'integration test project one', 'ACTIVE', 1, '2017-06-27 14:11:00', 1, 1),
  (2, 'PRO2', 'Project Two', 'PRO2', '2017-06-27', 'integration test project two', 'ACTIVE', 1, '2017-06-27 14:11:00', 1, 1);

INSERT INTO Sample (sampleId, name, alias, description, identificationBarcode, sampleType, receivedDate, project_projectId,
scientificName, volume, qcPassed, lastModifier, creator, created, lastModified) VALUES
(1, 'SAM1', 'TEST_0001', 'Identity', '11111', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2016-07-20 09:00:00', '2016-07-20 09:00:00'),
(2, 'SAM2', 'TEST_0001_Bn_R_nn_1-1', 'Tissue', '22222', 'GENOMIC', '2017-07-20', 1, 'Homo sapiens', 30, 1, 1, 1, '2016-07-20 09:01:00', '2016-07-20 09:01:00'),
(3, 'SAM3', 'TEST_0001_Bn_R_nn_1-1_SL01', 'Slide', '33333', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(4, 'SAM4', 'TEST_0001_Bn_R_nn_1-1_C01', 'Curls', '44444', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(5, 'SAM5', 'TEST_0001_Bn_R_nn_1-1_LCM01', 'LCM Tube', '55555', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(6, 'SAM6', 'TEST_0001_Bn_R_nn_1-1_D_S1', 'gDNA stock', '66666', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(7, 'SAM7', 'TEST_0001_Bn_R_nn_1-1_R_S1', 'whole RNA stock', '77777', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(8, 'SAM8', 'TEST_0001_Bn_R_nn_1-1_D_1', 'gDNA aliquot', '88888', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(9, 'SAM9', 'TEST_0001_Bn_R_nn_1-1_R_1', 'whole RNA aliquot', '99999', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(10, 'SAM10', 'TEST_0001_Bn_R_nn_1-1_D_S2', 'cDNA stock', '10101', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(11, 'SAM11', 'TEST_0001_Bn_R_nn_1-1_D_2', 'cDNA aliquot', '11011', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(12, 'SAM12', 'TEST_0001_Bn_R_nn_1-1_R_1_SM_1', 'smRNA', '12121', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(13, 'SAM13', 'TEST_0001_Bn_R_nn_1-1_R_1_MR_1', 'mRNA', '13131', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(14, 'SAM14', 'TEST_0001_Bn_R_nn_1-1_R_1_WT_1', 'rRNA_depleted', '14141', 'GENOMIC', NULL, 1, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00'),
(15, 'SAM15', 'PRO2_0001', 'Identity', '15151', 'GENOMIC', NULL, 2, 'Homo sapiens', NULL, 1, 1, 1, '2017-07-20 09:00:00', '2017-07-20 09:00:00');

INSERT INTO SampleChangeLog(sampleId, columnsChanged, message, userId, changeTime) VALUES
(1,'one','change oneone',1,'2016-07-20 09:00:00'),
(1,'two','change onetwo',1,'2016-07-20 09:00:01'),
(2,'one','change twoone',1,'2016-07-20 09:00:00'),
(2,'two','change twotwo',1,'2016-07-20 09:00:01');

INSERT INTO Library(libraryId, name, alias, identificationBarcode, description, sample_sampleId, platformType,
  libraryType, librarySelectionType, libraryStrategyType, creationDate, creator, created, lastModifier, lastModified, qcPassed, dnaSize,
  volume, concentration, locationBarcode, kitDescriptorId) VALUES
  (1, 'LIB1', 'TEST_0001_Bn_R_PE_300_WG', '11211', 'description lib 1', 8, 'ILLUMINA', 1, 3, 1,  '2016-11-07', 1, '2017-07-20 09:01:00',
    1, '2017-07-20 09:01:00', 1, 300, 5.0, 2.75, NULL, 1);

INSERT INTO Library_Index(library_libraryId, index_indexId) VALUES
  (1, 5);

INSERT INTO LibraryAliquot (aliquotId, name, concentration, libraryId, identificationBarcode, creationDate, creator, lastModifier, lastUpdated) VALUES
(1, 'LDI1', 5.9, 1, '12321', '2017-07-20', 1, 1, '2017-07-20 09:01:00');

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

INSERT INTO `_Partition` (partitionId, partitionNumber, pool_poolId) VALUES 
(1, 1, 1);

INSERT INTO SequencerPartitionContainer_Partition (container_containerId, partitions_partitionId) VALUES
(1, 1);

INSERT INTO Run (runId, name, alias, instrumentId, startDate, completionDate, health, creator, created, lastModifier, lastModified) VALUES
(1, 'RUN1', 'MiSeq_Run_1', 2, '2017-08-02', '2017-08-03', 'Completed', 1, '2017-08-02 10:03:02', 1, '2017-08-03 10:03:02');

INSERT INTO RunIllumina (runId, pairedEnd) VALUES
(1, 1);

INSERT INTO Run_SequencerPartitionContainer (Run_runId, containers_containerId) VALUES
(1, 1);

INSERT INTO SequencingOrder (sequencingOrderId, poolId, partitions, parametersId, createdBy, updatedBy, creationDate, lastUpdated) VALUES
(1, 1, 2, 4, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00'),
(2, 1, 1, 1, 1, 1, '2017-09-30 14:30:00', '2017-09-30 14:30:00');
