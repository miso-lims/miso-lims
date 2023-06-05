INSERT INTO Project(projectId, name, title, code, created, description,
  status, referenceGenomeId, lastModified, creator, lastModifier, pipelineId) VALUES
  (1, 'PRO1', 'Project One', 'PRO1', '2017-06-27', 'integration test project one', 'ACTIVE', 1, '2017-06-27 14:11:00', 1, 1, 1),
  (2, 'PRO2', 'Project Two', 'PRO2', '2017-06-27', 'integration test project two', 'ACTIVE', 1, '2017-06-27 14:11:00', 1, 1, 2);

INSERT INTO Sample (sampleId, name, alias, description, identificationBarcode, sampleType, project_projectId,
scientificNameId, volume, detailedQcStatusId, qcDate, lastModifier, creator, created, lastModified, discriminator) VALUES
(1, 'SAM1', 'TEST_0001', 'Identity', '11111', 'GENOMIC', 1, 1, NULL, 1, '2016-07-20', 1, 1, '2016-07-20 09:00:00', '2016-07-20 09:00:00', 'Sample'),
(2, 'SAM2', 'TEST_0001_Bn_R_nn_1-1', 'Tissue', '22222', 'GENOMIC', 1, 1, 30, 1, '2016-07-20', 1, 1, '2016-07-20 09:01:00', '2016-07-20 09:01:00', 'Sample'),
(3, 'SAM3', 'TEST_0001_Bn_R_nn_1-1_SL01', 'Slide', '33333', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(4, 'SAM4', 'TEST_0001_Bn_R_nn_1-1_C01', 'Curls', '44444', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(5, 'SAM5', 'TEST_0001_Bn_R_nn_1-1_LCM01', 'LCM Tube', '55555', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(6, 'SAM6', 'TEST_0001_Bn_R_nn_1-1_D_S1', 'gDNA stock', '66666', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(7, 'SAM7', 'TEST_0001_Bn_R_nn_1-1_R_S1', 'whole RNA stock', '77777', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(8, 'SAM8', 'TEST_0001_Bn_R_nn_1-1_D_1', 'gDNA aliquot', '88888', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(9, 'SAM9', 'TEST_0001_Bn_R_nn_1-1_R_1', 'whole RNA aliquot', '99999', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(10, 'SAM10', 'TEST_0001_Bn_R_nn_1-1_D_S2', 'cDNA stock', '10101', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(11, 'SAM11', 'TEST_0001_Bn_R_nn_1-1_D_2', 'cDNA aliquot', '11011', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(12, 'SAM12', 'TEST_0001_Bn_R_nn_1-1_R_1_SM_1', 'smRNA', '12121', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(13, 'SAM13', 'TEST_0001_Bn_R_nn_1-1_R_1_MR_1', 'mRNA', '13131', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(14, 'SAM14', 'TEST_0001_Bn_R_nn_1-1_R_1_WT_1', 'rRNA_depleted', '14141', 'GENOMIC', 1, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:01:00', '2017-07-20 09:01:00', 'Sample'),
(15, 'SAM15', 'PRO2_0001', 'Identity', '15151', 'GENOMIC', 2, 1, NULL, 1, '2017-07-20', 1, 1, '2017-07-20 09:00:00', '2017-07-20 09:00:00', 'Sample');

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
  volume, concentration, locationBarcode, kitDescriptorId, index1Id, discriminator) VALUES
  (1, 'LIB1', 'TEST_0001_Bn_R_PE_300_WG', '11211', 'description lib 1', 8, 'ILLUMINA',
    1, 3, 1,  '2016-11-07', 1, '2017-07-20 09:01:00', 1, '2017-07-20 09:01:00', 1, '2017-07-20', 300,
    5.0, 2.75, NULL, 1, 5, 'Library');

INSERT INTO LibraryAliquot (aliquotId, name, alias, concentration, libraryId, identificationBarcode, creationDate, creator, lastModifier, lastUpdated, discriminator) VALUES
(1, 'LDI1', 'TEST_0001_Bn_R_PE_300_WG', 5.9, 1, '12321', '2017-07-20', 1, 1, '2017-07-20 09:01:00', 'LibraryAliquot');

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
