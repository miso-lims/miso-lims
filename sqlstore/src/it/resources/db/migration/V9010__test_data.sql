INSERT INTO `User` (`userId`, `active`, `admin`, `fullName`, `internal`, `loginName`, `password`, `email`)
VALUES (1, TRUE, TRUE, 'admin', TRUE, 'admin', 'admin', 'admin@admin'),
(2, FALSE, FALSE, 'Notification Server', TRUE, 'notification', 'notification', 'notification@notification'),
(3, TRUE, FALSE, 'user' , TRUE, 'user', 'user', 'user@user');

INSERT INTO `_Group`(`groupId`, `name`, `description`) VALUES
(1, 'TestGroup', 'Is full of testing'),
(2, 'TestGroup2', 'More testing');

INSERT INTO `User_Group` (`users_userId`, `groups_groupId`)
VALUES (3,1),(1,1);

INSERT INTO Sop(sopId, alias, version, category, url, archived) VALUES
(1, 'Sample SOP 1', '1.0', 'SAMPLE', 'http://sops.test.com/sample/1/1', FALSE),
(2, 'Sample SOP 2', '1.0', 'SAMPLE', 'http://sops.test.com/sample/2/1', FALSE),
(3, 'Library SOP 1', '1.0', 'LIBRARY', 'http://sops.test.com/library/1/1', FALSE),
(4, 'Library SOP 1', '2.0', 'LIBRARY', 'http://sops.test.com/library/1/2', FALSE),
(5, 'Run SOP 1', '1.0', 'RUN', 'http://sops.test.com/run/1/1', FALSE);

INSERT INTO ScientificName(scientificNameId, alias) VALUES
(1, 'Homo sapiens'),
(2, 'Mus musculus'),
(3, 'Delete me');

INSERT INTO SequencingControlType(sequencingControlTypeId, alias) VALUES
(1, 'Positive'),
(2, 'Negative');

INSERT INTO Workstation(workstationId, alias) VALUES
(1, 'Workstation 1'),
(2, 'Workstation 2'),
(3, 'Workstation 3');

INSERT INTO LibrarySpikeIn(spikeInId, alias) VALUES
(1, 'ERCC Mix 1'),
(2, 'ERCC Mix 2');

INSERT INTO StorageLocationMap(mapId, filename, description) VALUES
(1, 'floor_one.html', 'floor one map'),
(2, 'floor_two.html', 'floor two map');

INSERT INTO DetailedQcStatus (detailedQcStatusId, description, status, noteRequired, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'Passed', TRUE, FALSE, 1, '2019-05-28 13:17:00', 1, '2019-05-28 13:17:00'),
(2, 'Failed', FALSE, FALSE, 1, '2019-05-28 13:17:00', 1, '2019-05-28 13:17:00');

INSERT INTO PartitionQCType (partitionQcTypeId, description, noteRequired, orderFulfilled, analysisSkipped) VALUES
(1, 'OK', FALSE, TRUE, FALSE),
(2, 'Failed: Instrument problem', FALSE, FALSE, TRUE),
(3, 'Failed: Other problem', TRUE, FALSE, TRUE);

INSERT INTO SampleType (typeId, name) VALUES
(2,'NON GENOMIC'),
(1,'GENOMIC'),
(5,'OTHER'),
(4,'VIRAL RNA'),
(3,'SYNTHETIC'),
(6,'TRANSCRIPTOMIC'),
(7,'METAGENOMIC'),
(8,'METATRANSCRIPTOMIC');

INSERT INTO StainCategory (stainCategoryId, name) VALUES
(1, 'Category One'),
(2, 'Category Two');

INSERT INTO Stain (stainId, stainCategoryId, name) VALUES
(1, 1, 'Stain One'),
(2, 1, 'Stain Two'),
(3, NULL, 'Stain Three');

INSERT INTO AttachmentCategory(categoryId, alias) VALUES
(1, 'Category One'),
(2, 'Category Two');

INSERT INTO Attachment (attachmentId, filename, path, creator, created, categoryId) VALUES
(1, 'file1.txt', '/path/1234567890', 1, '2018-10-02 12:29:00', 1);

INSERT INTO IndexFamily(indexFamilyId, platformType, name) VALUES
  (1, 'ILLUMINA', 'TruSeq Single Index'),
  (2, 'LS454', '454 Rapid Library'),
  (3, 'ILLUMINA', 'Nextera Dual Index'),
  (4, 'ILLUMINA', 'NEXTflex 8bp'),
  (5, 'ILLUMINA', 'Nextera'),
  (6, 'ILLUMINA', 'RBC1'),
  (7, 'ILLUMINA', 'Illumina 6bp'),
  (8, 'ILLUMINA', 'Agilent'),
  (9, 'ILLUMINA', 'TruSeq smRNA'),
  (10, 'ILLUMINA', 'TCRindex2'),
  (11, 'ILLUMINA', 'SureSelect XT2'),
  (12, 'ILLUMINA', 'NEXTflex 6bp');

INSERT INTO `Indices` (`indexId`, `name`, `sequence`, `indexFamilyId`)
VALUES
    (12,'Index 12','CTTGTA',1),
    (11,'Index 11','GGCTAC',1),
    (10,'Index 10','TAGCTT',1),
    (9,'Index 9','GATCAG',1),
    (8,'Index 8','ACTTGA',1),
    (7,'Index 7','CAGATC',1),
    (6,'Index 6','GCCAAT',1),
    (5,'Index 5','ACAGTG',1),
    (4,'Index 4','TGACCA',1),
    (3,'Index 3','TTAGGC',1),
    (2,'Index 2','CGATGT',1),
    (1,'Index 1','ATCACG',1),
    (24,'Index 24','GGTAGC',1),
    (23,'Index 23','GAGTGG',1),
    (22,'Index 22','CGTACG',1),
    (21,'Index 21','GTTTCG',1),
    (20,'Index 20','GTGGCC',1),
    (19,'Index 19','GTGAAA',1),
    (18,'Index 18','GTCCGC',1),
    (17,'Index 17','GTAGAG',1),
    (16,'Index 16','CCGTCC',1),
    (15,'Index 15','ATGTCA',1),
    (14,'Index 14','AGTTCC',1),
    (13,'Index 13','AGTCAA',1),
    (48,'Index 48','TCGGCA',1),
    (47,'Index 47','TCGAAG',1),
    (46,'Index 46','TCCCGA',1),
    (45,'Index 45','TCATTC',1),
    (44,'Index 44','TATAAT',1),
    (43,'Index 43','TACAGC',1),
    (42,'Index 42','TAATCG',1),
    (41,'Index 41','GACGAC',1),
    (40,'Index 40','CTCAGA',1),
    (39,'Index 39','CTATAC',1),
    (38,'Index 38','CTAGCT',1),
    (37,'Index 37','CGGAAT',1),
    (36,'Index 36','CCAACA',1),
    (35,'Index 35','CATTTT',1),
    (34,'Index 34','CATGGC',1),
    (33,'Index 33','CAGGCG',1),
    (32,'Index 32','CACTCA',1),
    (31,'Index 31','CACGAT',1),
    (30,'Index 30','CACCGG',1),
    (29,'Index 29','CAACTA',1),
    (28,'Index 28','CAAAAG',1),
    (27,'Index 27','ATTCCT',1),
    (26,'Index 26','ATGAGC',1),
    (25,'Index 25','ACTGAT',1),
    (60,'RL12','ACTCGCGTCGT',2),
    (59,'RL11','ACTATACGAGT',2),
    (58,'RL10','ACTACGTCTCT',2),
    (57,'RL9','ACGTAGATCGT',2),
    (56,'RL8','ACGTACTGTGT',2),
    (55,'RL7','ACGTACACACT',2),
    (54,'RL6','ACGCGTCTAGT',2),
    (53,'RL5','ACGAGTAGACT',2),
    (52,'RL4','ACGACACGTAT',2),
    (51,'RL3','ACACTACTCGT',2),
    (50,'RL2','ACACGTAGTAT',2),
    (49,'RL1','ACACGACGACT',2),
    (80,'N508','CTAAGCCT',3),
    (79,'N507','AAGGAGTA',3),
    (78,'N506','ACTGCATA',3),
    (77,'N505','GTAAGGAG',3),
    (76,'N504','AGAGTAGA',3),
    (75,'N503','TATCCTCT',3),
    (74,'N502','CTCTCTAT',3),
    (73,'N501','TAGATCGC',3),
    (72,'N712','GTAGAGGA',3),
    (71,'N711','AAGAGGCA',3),
    (70,'N710','CGAGGCTG',3),
    (69,'N709','GCTACGCT',3),
    (68,'N708','CAGAGAGG',3),
    (67,'N707','CTCTCTAC',3),
    (66,'N706','TAGGCATG',3),
    (65,'N705','GGACTCCT',3),
    (64,'N704','TCCTGAGC',3),
    (63,'N703','AGGCAGAA',3),
    (62,'N702','CGTACTAG',3),
    (61,'N701','TAAGGCGA',3);

INSERT INTO KitDescriptor (kitDescriptorId, name, version, manufacturer, partNumber, stockLevel, kitType, platformType, creator, created, lastModifier, lastModified) VALUES
(1, 'Test Kit 1', 1, 'KitMaker', 'k001', 0, 'LIBRARY', 'ILLUMINA', 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
(2, 'Test Kit 2', 1, 'KitMaker', 'k002', 0, 'LIBRARY', 'ILLUMINA', 1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
(3, 'Test QC Kit', 1, 'KitMaker', 'k003', 0, 'QC', 'ILLUMINA', 1, '2020-03-11 13:24:00', 1, '2020-03-11 13:24:00');

INSERT INTO `QCType` (`qcTypeId`, `name`, `description`, `qcTarget`, `units`) VALUES
(2,'Bioanalyzer','Chip-based capillary electrophoresis machine to analyse RNA, DNA, and protein, manufactured by Agilent','Library','nM'),
(7,'QuBit','Quantitation of DNA, RNA and protein, manufacturered by Invitrogen','Sample','ng/µl'),
(3,'Bioanalyser','Chip-based capillary electrophoresis machine to analyse RNA, DNA, and protein, manufactured by Agilent','Sample','ng/µl'),
(4,'QuBit','Quantitation of DNA, RNA and protein, manufacturered by Invitrogen','Library','ng/µl'),
(6,'SeqInfo QC','Post-run completion run QC step, undertaken by the SeqInfo team, as part of the primary analysis stage.','Run',''),
(5,'SeqOps QC','Post-run completion run QC step, undertaken by the SeqOps team, to move a run through to the primary analysis stage.','Run',''),
(1,'qPCR','Quantitative PCR','Library','mol/µl'),
(8,'poolQcType1', 'qc 1 for pools', 'Pool', 'nM'),
(9,'poolQcType2', 'qc 2 for pools', 'Pool', 'nM'),
(10,'poolQcType3', 'qc 3 for pools', 'Pool', 'nM'),
(11,'poolQcType4', 'qc 4 for pools', 'Pool', 'nM'),
(12,'InsertSizeQC', 'Insert Size QC', 'Library', 'bp');

INSERT INTO QcControl (controlId, qcTypeId, alias) VALUES
(1, 11, 'Control 1'),
(2, 11, 'Control 2'),
(3, 11, 'Control 3');

INSERT INTO QCType_KitDescriptor (qcTypeId, kitDescriptorId) VALUES
(7, 3);

INSERT INTO Printer(printerId, name, backend, configuration, driver, layout, enabled) VALUES (1, 'foo', 'CUPS', '{}', 'BRADY', 'AVERY_8363', 1);
    
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`, defaultScientificNameId) VALUES
(1, 'Human hg19 random', 1),
(2, 'Human hg19', 1),
(3, 'Human hg18 random', NULL);

INSERT INTO `Project`(`projectId`, `created`, `description`, `name`, `status`, `alias`, `shortName`, `lastModified`, `referenceGenomeId`, creator, lastModifier) VALUES
(1,'2015-08-27 15:40:15','Test project','PRO1','ACTIVE','TEST1','TEST1','2015-08-27 19:40:40', 1, 1, 1),
(2,'2013-11-27 12:20:15','Test project2','PRO2','ACTIVE','TEST2','TEST2','2015-11-30 15:23:18', 1, 1, 1),
(3,'2016-01-27 11:11:15','Test project3','PRO3','ACTIVE','TEST3','TEST3','2016-02-22 10:43:18', 2, 1, 1);

INSERT INTO Study(studyId, name, description, accession, project_projectId, studyTypeId, alias, creator, created, lastModifier, lastModified)
VALUES (1,'STU1','Test study1',NULL,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study1',1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
(2,'STU2','Test study2',NULL,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study2',1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
(3,'STU3','OICR',NULL,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study3',1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
(4,'STU4','OICR',NULL,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study4',1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
(5,'STU5','OICR',NULL,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study5',1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00'),
(6,'STU6','delete me',NULL,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study6',1, '2018-04-23 15:08:00', 1, '2018-04-23 15:08:00');

INSERT INTO `Institute`(`instituteId`, `alias`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,'Institute A',1,'2016-01-28 14:32:00',1,'2016-01-28 14:32:00'),(2,'Institute B',1,'2016-01-29 09:32:00',1,'2016-01-29 09:32:00');

INSERT INTO `Lab`(`labId`, `instituteId`, `alias`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,1,'Lab A1',1,'2016-02-10 15:35:00',1,'2016-02-10 15:35:00'),(2,1,'Lab A2',1,'2016-02-10 15:35:00',1,'2016-02-10 15:35:00'),
(3,1,'Lab B1',1,'2016-02-10 15:35:00',1,'2016-02-10 15:35:00'),(4,1,'Lab B2',1,'2016-02-10 15:35:00',1,'2016-02-10 15:35:00');

INSERT INTO `TissueOrigin`(`tissueOriginId`, `alias`, `description`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,'Test Origin','for testing',1,'2016-02-19 11:28:00',1,'2016-02-19 11:28:00');

INSERT INTO `TissueType`(`tissueTypeId`, `alias`, `description`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,'Test Type','for testing',1,'2016-02-19 11:28:00',1,'2016-02-19 11:28:00');

INSERT INTO `SampleClass`(`sampleClassId`, `alias`, `sampleCategory`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,'Identity','Identity',1,'2016-04-05 14:57:00',1,'2016-04-05 14:57:00'),
(2,'Primary Tumor Tissue','Tissue',1,'2016-04-05 14:57:00',1,'2016-04-05 14:57:00'),
(3,'Stock','Stock',1,'2017-05-31 14:57:00',1,'2017-05-31 14:57:00'),
(4,'Aliquot','Aliquot',1,'2017-05-31 14:57:00',1,'2017-05-31 14:57:00');

-- Plain Samples
INSERT INTO `Sample`(`sampleId`, `accession`, `name`, `description`, `identificationBarcode`, `locationBarcode`, `sampleType`, `detailedQcStatusId`, `alias`, `project_projectId`, `scientificNameId`, `taxonIdentifier`, sequencingControlTypeId, `lastModifier`, `lastModified`, `creator`, `created`, discriminator) VALUES
(1,NULL,'SAM1','Inherited from TEST_0001','SAM1::TEST_0001_Bn_P_nn_1-1_D_1','Freezer1_1','GENOMIC',1,'TEST_0001_Bn_P_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:30:47',1,'2016-07-07 13:30:47', 'Sample'),
(2,NULL,'SAM2','Inherited from TEST_0001','SAM2::TEST_0001_Bn_R_nn_1-1_D_1','Freezer1_2','GENOMIC',1,'TEST_0001_Bn_R_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:30:48',1,'2016-07-07 13:30:48', 'Sample'),
(3,NULL,'SAM3','Inherited from TEST_0002','SAM3::TEST_0002_Bn_P_nn_1-1_D_1','Freezer1_3','GENOMIC',1,'TEST_0002_Bn_P_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:30:49',1,'2016-07-07 13:30:49', 'Sample'),
(4,NULL,'SAM4','Inherited from TEST_0002','SAM4::TEST_0002_Bn_R_nn_1-1_D_1','Freezer1_4','GENOMIC',1,'TEST_0002_Bn_R_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:30:51',1,'2016-07-07 13:30:51', 'Sample'),
(5,NULL,'SAM5','Inherited from TEST_0003','SAM5::TEST_0003_Bn_P_nn_1-1_D_1','Freezer1_5','GENOMIC',1,'TEST_0003_Bn_P_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:30:53',1,'2016-07-07 13:30:53', 'Sample'),
(6,NULL,'SAM6','Inherited from TEST_0003','SAM6::TEST_0003_Bn_R_nn_1-1_D_1','Freezer1_6','GENOMIC',1,'TEST_0003_Bn_R_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:30:55',1,'2016-07-07 13:30:55', 'Sample'),
(7,NULL,'SAM7','Inherited from TEST_0004','SAM7::TEST_0004_Bn_P_nn_1-1_D_1','Freezer1_7','GENOMIC',1,'TEST_0004_Bn_P_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:30:57',1,'2016-07-07 13:30:57', 'Sample'),
(8,NULL,'SAM8','Inherited from TEST_0004','SAM8::TEST_0004_Bn_R_nn_1-1_D_1','Freezer1_8','GENOMIC',1,'TEST_0004_Bn_R_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:30:59',1,'2016-07-07 13:30:59', 'Sample'),
(9,NULL,'SAM9','Inherited from TEST_0005','SAM9::TEST_0005_Bn_P_nn_1-1_D_1','Freezer1_9','GENOMIC',1,'TEST_0005_Bn_P_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:31:01',1,'2016-07-07 13:31:01', 'Sample'),
(10,NULL,'SAM10','Inherited from TEST_0005','SAM10::TEST_0005_Bn_R_nn_1-1_D_1','Freezer1_10','GENOMIC',1,'TEST_0005_Bn_R_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:31:03',1,'2016-07-07 13:31:03', 'Sample'),
(11,NULL,'SAM11','Inherited from TEST_0006','SAM11::TEST_0006_Bn_P_nn_1-1_D_1','Freezer1_11','GENOMIC',1,'TEST_0006_Bn_P_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:31:05',1,'2016-07-07 13:31:05', 'Sample'),
(12,NULL,'SAM12','Inherited from TEST_0006','SAM12::TEST_0006_Bn_R_nn_1-1_D_1','Freezer1_12','GENOMIC',1,'TEST_0006_Bn_R_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:31:07',1,'2016-07-07 13:31:07', 'Sample'),
(13,NULL,'SAM13','Inherited from TEST_0007','SAM13::TEST_0007_Bn_P_nn_1-1_D_1','Freezer1_13','GENOMIC',1,'TEST_0007_Bn_P_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:31:09',1,'2016-07-07 13:31:09', 'Sample'),
(14,NULL,'SAM14','Inherited from TEST_0007','SAM14::TEST_0007_Bn_R_nn_1-1_D_1','Freezer1_14','GENOMIC',1,'TEST_0007_Bn_R_nn_1-1_D_1',1,1,NULL,NULL,1,'2016-07-07 13:31:11',1,'2016-07-07 13:31:11', 'Sample');

  -- Identities
INSERT INTO `Sample`(`sampleId`, `accession`, `name`, `description`, `identificationBarcode`, `locationBarcode`, `sampleType`, `detailedQcStatusId`, `alias`, `project_projectId`, `scientificNameId`, `taxonIdentifier`, sequencingControlTypeId, `lastModifier`, `lastModified`, `creator`, `created`,
  `sampleClassId`, `archived`, `parentId`, `siblingNumber`, `preMigrationId`, isSynthetic, nonStandardAlias, discriminator,
  externalName, donorSex, consentLevel) VALUES
(15,NULL,'SAM15','identity1','SAM15::TEST_0001_IDENTITY_1','Freezer1_1','GENOMIC',1,'TEST_0001_IDENTITY_1',1,1,NULL,NULL,1,'2016-07-07 13:31:13',1,'2016-07-07 13:31:13',
  1,0,NULL,NULL,NULL,0, FALSE, 'Identity',
  '15_EXT15,EXT15','UNKNOWN', 'THIS_PROJECT'),
(20,NULL,'SAM20','identity2','SAM20::TEST_0002_IDENTITY_2','Freezer1_1','GENOMIC',1,'TEST_0002_IDENTITY_2',1,1,NULL,NULL,1,'2018-02-22 14:34:00',1,'2018-02-22 14:34:00',
  1,0,NULL,NULL,NULL,0, FALSE, 'Identity',
  '20_EXT20,EXT20','UNKNOWN', 'THIS_PROJECT');

INSERT INTO SampleHierarchy(sampleId, identityId, tissueId) VALUES
(15, 15, NULL),
(20, 20, NULL);

-- Tissues
INSERT INTO `Sample`(`sampleId`, `accession`, `name`, `description`, `identificationBarcode`, `locationBarcode`, `sampleType`, `detailedQcStatusId`, `alias`, `project_projectId`, `scientificNameId`, `taxonIdentifier`, sequencingControlTypeId, `lastModifier`, `lastModified`, `creator`, `created`,
  `sampleClassId`, `archived`, `parentId`, `siblingNumber`, `preMigrationId`, isSynthetic, nonStandardAlias, discriminator,
  tissueOriginId, tissueTypeId, timesReceived, tubeNumber, labId) VALUES
(16,NULL,'SAM16','tissue1','SAM16::TEST_0001_TISSUE_1','Freezer1_1','GENOMIC',1,'TEST_0001_TISSUE_1',1,1,NULL,NULL,1,'2016-07-07 13:31:15',1,'2016-07-07 13:31:15',
  2,0,15,1,NULL,1, FALSE, 'Tissue',
  1, 1, 1, 1, 2),
(17,NULL,'SAM17','tissue2','SAM17::TEST_0001_TISSUE_2','Freezer1_1','GENOMIC',1,'TEST_0001_TISSUE_2',1,1,NULL,NULL,1,'2016-07-07 13:31:17',1,'2016-07-07 13:31:17',
  2,0,15,2,1,0, FALSE, 'Tissue',
  1, 1, 1, 2, NULL),
(21,NULL,'SAM21','tissue4','SAM21::TEST_0001_TISSUE_4','Freezer1_1','GENOMIC',1,'TEST_0001_TISSUE_4',1,1,NULL,NULL,1,'2016-07-07 13:31:17',1,'2016-07-07 13:31:17',
  2,0,15,4,NULL,0, FALSE, 'Tissue',
  1, 1, 1, 3, NULL);

INSERT INTO SampleHierarchy(sampleId, identityId, tissueId) VALUES
(16, 15, 16),
(17, 15, 17),
(21, 15, 21);

-- Stocks
INSERT INTO `Sample`(`sampleId`, `accession`, `name`, `description`, `identificationBarcode`, `locationBarcode`, `sampleType`, `detailedQcStatusId`, `alias`, `project_projectId`, `scientificNameId`, `taxonIdentifier`, sequencingControlTypeId, `lastModifier`, `lastModified`, `creator`, `created`, sopId,
  `sampleClassId`, `archived`, `parentId`, `siblingNumber`, `preMigrationId`, isSynthetic, nonStandardAlias, discriminator) VALUES
(18,NULL,'SAM18','stock1','SAM18::TEST_0001_STOCK_1','Freezer1_1','GENOMIC',1,'TEST_0001_STOCK_1',1,1,NULL,NULL,1,'2016-07-07 13:31:19',1,'2016-07-07 13:31:19', 1,
  3,0,17,1,NULL,0, FALSE, 'Stock');

INSERT INTO SampleHierarchy(sampleId, identityId, tissueId) VALUES
(18, 15, 17);

-- Aliquots
INSERT INTO `Sample`(`sampleId`, `accession`, `name`, `description`, `identificationBarcode`, `locationBarcode`, `sampleType`, `detailedQcStatusId`, `alias`, `project_projectId`, `scientificNameId`, `taxonIdentifier`, sequencingControlTypeId, `lastModifier`, `lastModified`, `creator`, `created`,
  `sampleClassId`, `archived`, `parentId`, `siblingNumber`, `preMigrationId`, isSynthetic, nonStandardAlias, discriminator) VALUES
(19,NULL,'SAM19','aliquot1','SAM19::TEST_0001_ALIQUOT_1','Freezer1_1','GENOMIC',1,'TEST_0001_ALIQUOT_1',1,1,NULL,1,1,'2016-07-07 13:31:21',1,'2016-07-07 13:31:21',
  4,0,18,1,NULL,0, FALSE, 'Aliquot');

INSERT INTO SampleHierarchy(sampleId, identityId, tissueId) VALUES
(19, 15, 17);

INSERT INTO `SampleQC`(`sample_sampleId`, `creator`, `date`, `type`, `results`, kitDescriptorId, kitLot) 
VALUES (1,1,'2015-08-27',3,5, NULL, NULL),
(2,1,'2015-08-27',3,5, NULL, NULL),
(3,1,'2015-08-27',3,5, NULL, NULL),
(4,1,'2015-08-27',3,5, NULL, NULL),
(5,1,'2015-08-27',3,5, NULL, NULL),
(6,1,'2015-08-27',3,5, NULL, NULL),
(7,1,'2015-08-27',3,5, NULL, NULL),
(8,1,'2015-08-27',3,5, NULL, NULL),
(9,1,'2015-08-27',3,5, NULL, NULL),
(10,1,'2015-08-27',3,5, NULL, NULL),
(11,1,'2015-08-27',3,5, NULL, NULL),
(12,1,'2015-08-27',3,5, NULL, NULL),
(13,1,'2015-08-27',3,5, NULL, NULL),
(14,1,'2015-08-27',3,5, NULL, NULL),
(14,1,'2015-08-28',3,55, NULL, NULL),
(1,1,'2020-03-11',7,12, 3, 'asdf');

INSERT INTO `SampleChangeLog`(`sampleChangeLogId`, `sampleId`, `columnsChanged`, `userId`, `message`, `changeTime`)
VALUES (1, 1, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:47'),
(2, 1, 'name', 1, 'TGAC -> Earlham', '2016-07-07 13:30:48'),
(3, 1, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:49'),
(4, 2, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:51'),
(5, 3, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:53'),
(6, 4, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:55'),
(7, 5, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:57'),
(8, 6, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:59'),
(9, 7, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:01'),
(10, 8, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:03'),
(11, 9, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:05'),
(12, 10, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:07'),
(13, 11, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:09'),
(14, 12, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:11'),
(15, 13, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:13'),
(16, 14, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:15'),
(17, 15, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:17'),
(18, 16, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:19'),
(19, 17, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:21');

INSERT INTO LibraryType (`libraryTypeId`,`description`,`platformType`,`archived`)
VALUES (1,'Paired End','ILLUMINA', 0),
(2, 'cDNA','PACBIO',0),
(3,'mRNA Seq','ILLUMINA',0),
(4,'8kbp Paired End','LS454',0),
(5,'Rapid Shotgun','LS454',0),
(6,'Small RNA','SOLID',1),
(7,'Whole Transcriptome','SOLID',1),
(8,'SAGE','SOLID',1),
(9,'Long Mate Pair','SOLID',1),
(10,'Fragment','SOLID',1);

INSERT INTO `LibrarySelectionType` (`librarySelectionTypeId`,`name`,`description`)
VALUES (1,'RT-PCR','Source material was selected by reverse transcription PCR'),
(10,'ChIP','Chromatin Immunoprecipitation');

INSERT INTO `LibraryStrategyType` (`libraryStrategyTypeId`,`name`,`description`)
VALUES (1,'WGS','Whole genome shotgun'),
(12,'EST','Single pass sequencing of cDNA templates'),
(14,'CTS','Concatenated Tag Sequencing');

INSERT INTO LibraryDesignCode(libraryDesignCodeId, code, description) 
VALUES (1, 'TT', 'TEST');

INSERT INTO `LibraryDesign`(`libraryDesignId`, `name`, `sampleClassId`, `librarySelectionType`, `libraryStrategyType`, `libraryDesignCodeId`)
VALUES (1, 'DESIGN1', 1, 1, 1, 1), 
(2, 'DESIGN2', 2, 1, 1, 1);

INSERT INTO `Library`(`libraryId`, `name`, `description`, `accession`, `sample_sampleId`, `identificationBarcode`, `locationBarcode`, `libraryType`, `concentration`, `creationDate`, `platformType`, `alias`, `paired`, `librarySelectionType`, `libraryStrategyType`, `detailedQcStatusId`, `lastModifier`, `lastModified`, `creator`, `created`, `kitDescriptorId`, workstationId, discriminator, libraryDesignCodeId, archived, nonStandardAlias, sopId) VALUES
(1,'LIB1','Inherited from TEST_0001',NULL,1,'LIB1::TEST_0001_Bn_P_PE_300_WG','LIBRARY_INBOX_A01',3,0,'2015-08-27','ILLUMINA','TEST_0001_Bn_P_PE_300_WG',1,1,1,1,1,'2016-07-07 13:30:49',1,'2016-07-07 13:30:49', 1, 1, 'DetailedLibrary', 1, FALSE, FALSE, 3),
(2,'LIB2','Inherited from TEST_0001',NULL,2,'LIB2::TEST_0001_Bn_R_PE_300_WG','LIBRARY_INBOX_A02',3,0,'2015-08-27','ILLUMINA','TEST_0001_Bn_R_PE_300_WG',1,1,1,1,1,'2016-07-07 13:30:51',1,'2016-07-07 13:30:51', NULL, 1, 'Library', NULL, NULL, NULL, NULL),
(3,'LIB3','Inherited from TEST_0002',NULL,3,'LIB3::TEST_0002_Bn_P_PE_300_WG','LIBRARY_INBOX_A03',3,0,'2015-08-27','ILLUMINA','TEST_0002_Bn_P_PE_300_WG',1,1,1,1,1,'2016-07-07 13:30:53',1,'2016-07-07 13:30:53', NULL, 1, 'Library', NULL, NULL, NULL, NULL),
(4,'LIB4','Inherited from TEST_0002',NULL,4,'LIB4::TEST_0002_Bn_R_PE_300_WG','LIBRARY_INBOX_A04',3,0,'2015-08-27','ILLUMINA','TEST_0002_Bn_R_PE_300_WG',1,1,1,1,1,'2016-07-07 13:30:55',1,'2016-07-07 13:30:55', NULL, 1, 'Library', NULL, NULL, NULL, NULL),
(5,'LIB5','Inherited from TEST_0003',NULL,5,'LIB5::TEST_0003_Bn_P_PE_300_WG','LIBRARY_INBOX_A05',3,0,'2015-08-27','ILLUMINA','TEST_0003_Bn_P_PE_300_WG',1,1,1,1,1,'2016-07-07 13:30:57',1,'2016-07-07 13:30:57', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(6,'LIB6','Inherited from TEST_0003',NULL,6,'LIB6::TEST_0003_Bn_R_PE_300_WG','LIBRARY_INBOX_A06',3,0,'2015-08-27','ILLUMINA','TEST_0003_Bn_R_PE_300_WG',1,1,1,1,1,'2016-07-07 13:30:59',1,'2016-07-07 13:30:59', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(7,'LIB7','Inherited from TEST_0004',NULL,7,'LIB7::TEST_0004_Bn_P_PE_300_WG','LIBRARY_INBOX_A07',3,0,'2015-08-27','ILLUMINA','TEST_0004_Bn_P_PE_300_WG',1,1,1,1,1,'2016-07-07 13:31:01',1,'2016-07-07 13:31:01', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(8,'LIB8','Inherited from TEST_0004',NULL,8,'LIB8::TEST_0004_Bn_R_PE_300_WG','LIBRARY_INBOX_A08',3,0,'2015-08-27','ILLUMINA','TEST_0004_Bn_R_PE_300_WG',1,1,1,1,1,'2016-07-07 13:31:03',1,'2016-07-07 13:31:03', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(9,'LIB9','Inherited from TEST_0005',NULL,9,'LIB9::TEST_0005_Bn_P_PE_300_WG','LIBRARY_INBOX_A09',3,0,'2015-08-27','ILLUMINA','TEST_0005_Bn_P_PE_300_WG',1,1,1,1,1,'2016-07-07 13:31:05',1,'2016-07-07 13:31:05', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(10,'LIB10','Inherited from TEST_0005',NULL,10,'LIB10::TEST_0005_Bn_R_PE_300_WG','LIBRARY_INBOX_A10',3,0,'2015-08-27','ILLUMINA','TEST_0005_Bn_R_PE_300_WG',1,1,1,1,1,'2016-07-07 13:31:07',1,'2016-07-07 13:31:07', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(11,'LIB11','Inherited from TEST_0006',NULL,11,'LIB11::TEST_0006_Bn_P_PE_300_WG','LIBRARY_INBOX_B01',3,0,'2015-08-27','ILLUMINA','TEST_0006_Bn_P_PE_300_WG',1,1,1,1,1,'2016-07-07 13:31:09',1,'2016-07-07 13:31:09', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(12,'LIB12','Inherited from TEST_0006',NULL,12,'LIB12::TEST_0006_Bn_R_PE_300_WG','LIBRARY_INBOX_B02',3,0,'2015-08-27','ILLUMINA','TEST_0006_Bn_R_PE_300_WG',1,1,1,1,1,'2016-07-07 13:31:11',1,'2016-07-07 13:31:11', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(13,'LIB13','Inherited from TEST_0007',NULL,13,'LIB13::TEST_0007_Bn_P_PE_300_WG','LIBRARY_INBOX_B03',3,0,'2015-08-27','ILLUMINA','TEST_0007_Bn_P_PE_300_WG',1,1,1,1,1,'2016-07-07 13:31:13',1,'2016-07-07 13:31:13', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(14,'LIB14','Inherited from TEST_0007',NULL,14,'LIB14::TEST_0007_Bn_R_PE_300_WG','LIBRARY_INBOX_B04',3,0,'2015-08-27','ILLUMINA','TEST_0007_Bn_R_PE_300_WG',1,1,1,1,1,'2016-07-07 13:31:15',1,'2016-07-07 13:31:15', NULL, NULL, 'Library', NULL, NULL, NULL, NULL),
(15,'LIB15',NULL,NULL,19,'LIB15::TEST_0001_ALIQUOT_1_PE_300_WG',NULL,3,0,'2018-02-15','ILLUMINA','TEST_0001_ALIQUOT_1_PE_300_WG',1,1,1,1,1,'2016-07-07 13:31:15',1,'2016-07-07 13:31:15', NULL, NULL, 'Library', NULL, NULL, NULL, NULL);

INSERT INTO `LibraryChangeLog`(`libraryId`, `columnsChanged`, `userId`, `message`, `changeTime`)
VALUES (1, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:49'),
(2, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:51'),
(3, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:53'),
(4, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:55'),
(5, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:57'),
(6, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:59'),
(7, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:01'),
(8, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:03'),
(9, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:05'),
(10, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:07'),
(11, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:09'),
(12, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:11'),
(13, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:13'),
(14, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:15');

INSERT INTO `Kit`(`kitId`,`identificationBarcode`,`locationBarcode`,`lotNumber`,`kitDate`,`kitDescriptorId`) VALUES
(1,'1234','Freezer2','LOT34',CURRENT_DATE(),1),
(2,'5678','Freezer3','LOT35',CURRENT_DATE(),2);

INSERT INTO `TargetedSequencing`(`targetedSequencingId`,`alias`,`description`, `archived`, `createdBy`,`creationDate`,`updatedBy`,`lastUpdated`) VALUES
(1,'HALO_IBP','Master Chief',0,1,NOW(),1,NOW()),
(2,'Thunderbolts','of lightening, very very frightening',0,1,NOW(),1,NOW());

INSERT INTO TargetedSequencing_KitDescriptor(targetedSequencingId, kitDescriptorId) VALUES
(1,1),
(2,1),
(2,2);

INSERT INTO LibraryAliquot(aliquotId, concentration, libraryId, alias, identificationBarcode, creationDate, creator, lastModifier, name, targetedSequencingId, discriminator, nonStandardAlias) 
VALUES (1,2,1,'TEST_0001_Bn_P_PE_300_WG','LDI1::TEST_0001_Bn_P_PE_300_WG','2015-08-27',1,1,'LDI1',1, 'DetailedLibraryAliquot', FALSE),
(2,2,2,'TEST_0001_Bn_R_PE_300_WG','LDI2::TEST_0001_Bn_R_PE_300_WG','2015-08-27',1,1,'LDI2',NULL, 'LibraryAliquot', NULL),
(3,2,3,'TEST_0002_Bn_P_PE_300_WG','LDI3::TEST_0002_Bn_P_PE_300_WG','2015-08-27',1,1,'LDI3',NULL, 'LibraryAliquot', NULL),
(4,2,4,'TEST_0002_Bn_R_PE_300_WG','LDI4::TEST_0002_Bn_R_PE_300_WG','2015-08-27',1,1,'LDI4',NULL, 'LibraryAliquot', NULL),
(5,2,5,'TEST_0003_Bn_P_PE_300_WG','LDI5::TEST_0003_Bn_P_PE_300_WG','2015-08-27',1,1,'LDI5',NULL, 'LibraryAliquot', NULL),
(6,2,6,'TEST_0003_Bn_R_PE_300_WG','LDI6::TEST_0003_Bn_R_PE_300_WG','2015-08-27',1,1,'LDI6',NULL, 'LibraryAliquot', NULL),
(7,2,7,'TEST_0004_Bn_P_PE_300_WG','LDI7::TEST_0004_Bn_P_PE_300_WG','2015-08-27',1,1,'LDI7',NULL, 'LibraryAliquot', NULL),
(8,2,8,'TEST_0004_Bn_R_PE_300_WG','LDI8::TEST_0004_Bn_R_PE_300_WG','2015-08-27',1,1,'LDI8',NULL, 'LibraryAliquot', NULL),
(9,2,9,'TEST_0005_Bn_P_PE_300_WG','LDI9::TEST_0005_Bn_P_PE_300_WG','2015-08-27',1,1,'LDI9',NULL, 'LibraryAliquot', NULL),
(10,2,10,'TEST_0005_Bn_R_PE_300_WG','LDI10::TEST_0005_Bn_R_PE_300_WG','2015-08-27',1,1,'LDI10',NULL, 'LibraryAliquot', NULL),
(11,2,11,'TEST_0006_Bn_P_PE_300_WG','LDI11::TEST_0006_Bn_P_PE_300_WG','2015-08-27',1,1,'LDI11',NULL, 'LibraryAliquot', NULL),
(12,2,12,'TEST_0006_Bn_R_PE_300_WG','LDI12::TEST_0006_Bn_R_PE_300_WG','2015-08-27',1,1,'LDI12',NULL, 'LibraryAliquot', NULL),
(13,2,13,'TEST_0007_Bn_P_PE_300_WG','LDI13::TEST_0007_Bn_P_PE_300_WG','2015-08-27',1,1,'LDI13',NULL, 'LibraryAliquot', NULL),
(14,2,14,'TEST_0007_Bn_R_PE_300_WG','LDI14::TEST_0007_Bn_R_PE_300_WG','2015-08-27',1,1,'LDI14',NULL, 'LibraryAliquot', NULL),
(15,2,15,'TEST_0001_ALIQUOT_1_PE_300_WG','LDI15::TEST_0001_ALIQUOT_1_PE_300_WG','2018-02-15',1,1,'LDI15',NULL, 'LibraryAliquot', NULL);

INSERT INTO `LibraryQC`(`qcId`, `library_libraryId`, `creator`, `date`, `type`, `results`) VALUES
 (1,1,1,'2015-08-27',4,3),
 (2,2,1,'2015-08-27',4,3),
 (3,3,1,'2015-08-27',4,3),
 (4,4,1,'2015-08-27',4,3),
 (5,5,1,'2015-08-27',4,3),
 (6,6,1,'2015-08-27',4,3),
 (7,7,1,'2015-08-27',4,3),
 (8,8,1,'2015-08-27',4,3),
 (9,9,1,'2015-08-27',4,3),
 (10,10,1,'2015-08-27',4,3),
 (11,11,1,'2015-08-27',4,3),
 (12,12,1,'2015-08-27',4,3),
 (13,13,1,'2015-08-27',4,3),
 (14,14,1,'2015-08-27',4,3),
 (15,2,1,'2015-08-27',12,300);

INSERT INTO `Library_Index` 
VALUES (1,12),(2,11),(3,10),(4,9),(5,8),(6,7),(7,6),(8,5),(9,4),(10,3),(11,2),(12,1),(13,24),(14,23);

INSERT INTO `Pool`(`poolId`, `concentration`, `identificationBarcode`, `name`, `description`, `creationDate`, `platformType`, `alias`, `qcPassed`, `lastModifier`, `lastModified`, `creator`, `created`)
VALUES (1,2,'IPO1::Illumina','IPO1','TEST','2015-08-27','ILLUMINA','Pool 1',NULL,1,'2016-06-07 13:13:30',1,'2016-07-07 13:30:49'),
(2,2,'IPO2::Illumina','IPO2','TEST','2015-08-27','ILLUMINA','Pool 2',NULL,1,'2016-07-07 13:30:51',1,'2016-07-07 13:30:51'),
(3,2,'IPO3::Illumina','IPO3','TEST','2015-08-27','ILLUMINA','Pool 3',NULL,1,'2016-07-07 13:30:53',1,'2016-07-07 13:30:53'),
(4,2,'IPO4::Illumina','IPO4','TEST','2015-08-27','ILLUMINA','Pool 4',NULL,1,'2016-07-07 13:30:55',1,'2016-07-07 13:30:55'),
(5,2,'IPO5::Illumina','IPO5','TEST','2015-08-27','ILLUMINA','Pool 5',NULL,1,'2016-07-07 13:30:57',1,'2016-07-07 13:30:57'),
(6,2,'IPO6::Illumina','IPO6','TEST','2015-08-27','ILLUMINA','Pool 6',NULL,1,'2016-07-07 13:30:59',1,'2016-07-07 13:30:59'),
(7,2,'IPO7::Illumina','IPO7','TEST','2015-08-27','ILLUMINA','Pool 7',NULL,1,'2016-07-07 13:31:01',1,'2016-07-07 13:31:01'),
(8,2,'IPO8::Illumina','IPO8','TEST','2015-08-27','ILLUMINA','Pool 8',NULL,1,'2016-07-07 13:31:03',1,'2016-07-07 13:31:03'),
(9,2,'IPO9::Illumina','IPO9','TEST','2015-08-27','ILLUMINA','Pool 9',NULL,1,'2016-07-07 13:31:05',1,'2016-07-07 13:31:05'),
(10,2,'IPO10::Illumina','IPO10','TEST','2015-08-27','ILLUMINA','Pool 10',NULL,1,'2016-07-07 13:31:07',1,'2016-07-07 13:31:07');

INSERT INTO `Pool_LibraryAliquot`(`poolId`, `aliquotId`) 
VALUES (1,2),
(2,6),
(2,5),
(2,4),
(3,7),
(3,8),
(3,9),
(3,10),
(3,11),
(3,12),
(3,13),
(4,1),
(5,2),
(6,3),
(6,4),
(6,5),
(6,6),
(7,7),
(7,8),
(8,8),
(8,9),
(9,11),
(9,12),
(10,13),
(10,14),
(3,14),
(1,1),
(2,3);

INSERT INTO `PoolQC`(`qcId`, `pool_poolId`, `creator`, `date`, `type`, `results`)
VALUES (1,1,1,'2016-03-18',1,12.3),
(2,1,1,'2016-03-18',11,45.6),
(3,2,1,'2016-03-18',11,7.89);

INSERT INTO PoolQcControl (qcControlId, qcId, controlId, lot, qcPassed) VALUES
(1, 2, 1, 'controllot1', TRUE),
(2, 3, 1, 'controllot1', FALSE),
(3, 3, 2, 'controllot1', TRUE);

INSERT INTO `PoolChangeLog`(`poolId`, `columnsChanged`, `userId`, `message`, `changeTime`)
VALUES (1, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:49'),
(1, 'alias', 1, 'Poll 1 -> Pool 1', '2016-06-07 13:13:30'),
(2, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:51'),
(3, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:53'),
(4, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:55'),
(5, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:57'),
(6, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:59'),
(7, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:01'),
(8, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:03'),
(9, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:05'),
(10, 'qcPassed', 1, 'false -> true', '2016-07-07 13:31:07');

INSERT INTO InstrumentModel(instrumentModelId, platform, alias, description, numContainers, instrumentType) VALUES
(1, 'ILLUMINA', 'Illumina MiSeq', '4-channel flowgram', 1, 'SEQUENCER'),
(16, 'ILLUMINA', 'Illumina HiSeq 2000', '4-channel flowgram', 1, 'SEQUENCER'),
(30, 'ILLUMINA', 'Illumina iScan', NULL, 1, 'ARRAY_SCANNER');

INSERT INTO SequencingParameters (parametersId, name, instrumentModelId, readLength, readLength2, createdBy, updatedBy, creationDate, lastUpdated, chemistry) VALUES
(1, 'HiSeq Params 1', 16, 100, 100, 1, 1, '2019-09-23 10:05:00', '2019-09-23 10:05:00', NULL),
(2, 'Rapid Run 2x151', 16, 151, 151, 1, 1, '2017-09-01 09:00:00', '2017-09-01 09:00:00', 'RAPID_RUN'),
(3, 'MiSeq Params 1', 1, 100, 100, 1, 1, '2019-09-23 10:05:00', '2019-09-23 10:05:00', NULL);

INSERT INTO RunPurpose(purposeId, alias) VALUES
(1, 'Production');

INSERT INTO `Instrument`(`instrumentId`, `name`, `instrumentModelId`, defaultPurposeId) VALUES
(1, 'SN7001179', 16, 1),
(2, 'h1180', 16, 1),
(3, 'iScan_1', 30, 1);

INSERT INTO `Experiment`(`experimentId`, `name`, `description`, `accession`, `title`, `study_studyId`, `alias`, `instrumentModelId`,`lastModifier`, lastModified, creator, created, `library_libraryId`) 
VALUES
(1,'EXP1','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_1',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 1),
(2,'EXP2','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_2',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 2),
(3,'EXP3','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_3',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 3),
(4,'EXP4','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_4',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 3),
(5,'EXP5','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_5',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 4),
(6,'EXP6','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_6',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 4),
(7,'EXP7','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_7',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 4),
(8,'EXP8','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_8',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 4),
(9,'EXP9','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_9',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 5),
(10,'EXP10','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_10',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 5),
(11,'EXP11','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_11',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 5),
(12,'EXP12','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_12',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 5),
(13,'EXP13','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_13',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 6),
(14,'EXP14','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_14',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 6),
(15,'EXP15','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_15',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 6),
(16,'EXP16','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_16',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 6),
(17,'EXP17','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_17',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 7),
(18,'EXP18','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_18',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 7),
(19,'EXP19','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_19',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 7),
(20,'EXP20','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_20',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 7),
(21,'EXP21','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_21',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 8),
(22,'EXP22','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_22',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 8),
(23,'EXP23','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_23',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 8),
(24,'EXP24','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_24',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 8),
(25,'EXP25','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,'EXP_AUTOGEN_STU1_Other_25',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 10),
(26,'EXP26','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',2,'EXP_AUTOGEN_STU1_Other_26',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 10),
(27,'EXP27','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',3,'EXP_AUTOGEN_STU1_Other_27',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 10),
(28,'EXP28','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',4,'EXP_AUTOGEN_STU1_Other_28',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 10),
(29,'EXP29','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',2,'EXP_AUTOGEN_STU1_Other_29',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 9),
(30,'EXP30','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',3,'EXP_AUTOGEN_STU1_Other_30',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 9),
(31,'EXP31','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',4,'EXP_AUTOGEN_STU1_Other_31',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 9),
(32,'EXP32','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',5,'EXP_AUTOGEN_STU1_Other_32',16,1, '2018-04-23 13:39:00', 1, '2018-04-23 13:39:00', 9);

INSERT INTO `ServiceRecord`(`recordId`, `instrumentId`, `title`, `details`, `servicedBy`, `referenceNumber`, `serviceDate`, `startTime`, `endTime`)
VALUES (1,1,'Seq1_Rec1','Test service','Service Person','12345','2016-01-01', '2016-01-01 07:30:00', '2016-01-01 09:00:00'),
(2,1,'Seq1_Rec2',NULL,'Service Person',NULL,'2016-01-21',NULL,NULL),
(3,2,'Seq2_Rec1',NULL,'Service Person',NULL,'2016-01-21',NULL,NULL);

INSERT INTO `Run`(`runId`, `name`, `description`, `accession`, `filePath`, `alias`, `instrumentId`, `lastModifier`, `health`, `completionDate`, `lastModified`, `creator`, `created`, sequencingParameters_parametersId, sopId) 
VALUES (1,'RUN1','BC0JHTACXX',NULL,'/.mounts/labs/prod/archive/h1179/120323_h1179_0070_BC0JHTACXX','120323_h1179_0070_BC0JHTACXX',1,1,'Completed','2012-03-31','2016-07-07 13:30:49',1,'2016-07-07 13:30:49', 1, 5),
(2,'RUN2','AD0VJ9ACXX',NULL,'/.mounts/labs/prod/archive/h1179/120404_h1179_0072_AD0VJ9ACXX','120404_h1179_0072_AD0VJ9ACXX',1,1,'Failed','2012-04-04','2016-07-07 13:30:51',1,'2016-07-07 13:30:51', NULL, NULL),
(3,'RUN3','BC075RACXX',NULL,'/.mounts/labs/prod/archive/h1179/120412_h1179_0073_BC075RACXX','120412_h1179_0073_BC075RACXX',1,1,'Completed','2012-04-20','2016-07-07 13:30:53',1,'2016-07-07 13:30:53', NULL, NULL),
(4,'RUN4','AC0KY7ACXX',NULL,'/.mounts/labs/prod/archive/h1179/120314_h1179_0068_AC0KY7ACXX','120314_h1179_0068_AC0KY7ACXX',1,1,'Completed','2012-03-23','2016-07-07 13:30:55',1,'2016-07-07 13:30:55', NULL, NULL);
INSERT INTO RunIllumina(runId) VALUES (1), (2), (3), (4);

INSERT INTO `RunChangeLog`(`runId`, `columnsChanged`, `userId`, `message`, `changeTime`)
VALUES (1, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:49'),
(2, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:51'),
(3, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:53'),
(4, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:55');

INSERT INTO SequencingContainerModel (sequencingContainerModelId, alias, identificationBarcode, partitionCount, platformType, fallback) VALUES
(1, 'Generic 8-Lane Illumina Flow Cell', NULL, 8, 'ILLUMINA', 1),
(2, 'Generic 2-Lane Illumina Flow Cell', NULL, 2, 'ILLUMINA', 1),
(3, 'HiSeq PE Flow Cell v4', '12345678', 8, 'ILLUMINA', 0);

INSERT INTO SequencingContainerModel_InstrumentModel (sequencingContainerModelId, instrumentModelId) VALUES
(1, 16),
(2, 16),
(3, 16);

INSERT INTO `SequencerPartitionContainer`(`containerId`, `identificationBarcode`, sequencingContainerModelId, `lastModifier`, `lastModified`, `creator`, `created`) 
VALUES (1,'C0JHTACXX',1,1,'2016-07-07 13:30:47',1,'2016-07-07 13:30:47'),
(2,'D0VJ9ACXX',1,1,'2016-07-07 13:30:49',1,'2016-07-07 13:30:49'),
(3,'C075RACXX',1,1,'2016-07-07 13:30:51',1,'2016-07-07 13:30:51'),
(4,'C0KY7ACXX',1,1,'2016-07-07 13:30:53',1,'2016-07-07 13:30:53');

INSERT INTO _Partition (containerId, partitionId, partitionNumber, pool_poolId) VALUES
(1,1,1,1), (1,2,2,NULL), (1,3,3,NULL), (1,4,4,NULL), (1,5,5,NULL), (1,6,6,NULL), (1,7,7,NULL), (1,8,8,NULL),
(2,9,1,NULL), (2,10,2,NULL), (2,11,3,NULL), (2,12,4,NULL), (2,13,5,NULL), (2,14,6,NULL), (2,15,7,NULL), (2,16,8,NULL),
(3,17,1,NULL), (3,18,2,NULL), (3,19,3,NULL), (3,20,4,NULL), (3,21,5,NULL), (3,22,6,NULL), (3,23,7,NULL), (3,24,8,NULL),
(4,25,1,NULL), (4,26,2,NULL), (4,27,3,NULL), (4,28,4,NULL), (4,29,5,NULL), (4,30,6,NULL), (4,31,7,NULL), (4,32,8,NULL);

INSERT INTO `SequencerPartitionContainerChangeLog`(`containerId`, `columnsChanged`, `userId`, `message`, `changeTime`)
VALUES (1, 'identificationBarcode', 1, 'NULL -> real', '2016-07-07 13:30:47'),
(2, 'identificationBarcode', 1, 'NULL -> real', '2016-07-07 13:30:49'),
(3, 'identificationBarcode', 1, 'NULL -> real', '2016-07-07 13:30:51'),
(4, 'identificationBarcode', 1, 'NULL -> real', '2016-07-07 13:30:53');

INSERT INTO `Run_SequencerPartitionContainer`(`Run_runId`, `containers_containerId`) 
VALUES (1,1),(2,2),(3,3),(4,4);

INSERT INTO Run_Partition(runId, partitionId, partitionQcTypeId, notes, purposeId, lastModifier) VALUES
(1, 1, 1, 'it is written', 1, 1),
(1, 2, NULL, NULL, 1, 1),
(1, 3, NULL, NULL, 1, 1),
(1, 4, NULL, NULL, 1, 1),
(1, 5, NULL, NULL, 1, 1),
(1, 6, NULL, NULL, 1, 1),
(1, 7, NULL, NULL, 1, 1),
(1, 8, NULL, NULL, 1, 1),
(2, 9, NULL, NULL, 1, 1),
(2, 10, NULL, NULL, 1, 1),
(2, 11, NULL, NULL, 1, 1),
(2, 12, NULL, NULL, 1, 1),
(2, 13, NULL, NULL, 1, 1),
(2, 14, NULL, NULL, 1, 1),
(2, 15, NULL, NULL, 1, 1),
(2, 16, NULL, NULL, 1, 1),
(3, 17, NULL, NULL, 1, 1),
(3, 18, NULL, NULL, 1, 1),
(3, 19, NULL, NULL, 1, 1),
(3, 20, NULL, NULL, 1, 1),
(3, 21, NULL, NULL, 1, 1),
(3, 22, NULL, NULL, 1, 1),
(3, 23, NULL, NULL, 1, 1),
(3, 24, NULL, NULL, 1, 1),
(4, 25, NULL, NULL, 1, 1),
(4, 26, NULL, NULL, 1, 1),
(4, 27, NULL, NULL, 1, 1),
(4, 28, NULL, NULL, 1, 1),
(4, 29, NULL, NULL, 1, 1),
(4, 30, NULL, NULL, 1, 1),
(4, 31, NULL, NULL, 1, 1),
(4, 32, NULL, NULL, 1, 1);

INSERT INTO Experiment_Run_Partition(experiment_experimentId, run_runId, partition_partitionId)
VALUES
(1, 1, 1),
(2, 1, 1);

INSERT INTO `BoxSize` (`boxSizeId`, `boxSizeRows`, `boxSizeColumns`, `scannable`)
VALUES
('1', '4', '4', '0');

INSERT INTO `BoxUse` (`boxUseId`, `alias`)
VALUES
('1', 'boxuse1'),
('2', 'boxuse2');

INSERT INTO `Box` (`boxId`, `boxSizeId`, `boxUseId`, `name`, `alias`, `description`, `identificationBarcode`, `locationBarcode`, `lastModifier`, `lastModified`, `creator`, `created`)
VALUES
('1','1', '1', 'box1', 'box1alias', 'box1','identificationbarcode1', 'location1', '1', '2017-06-20 16:02:00', '1', '2017-06-20 16:02:00'),
('2','1', '2', 'box2', 'box2alias', 'box2','identificationbarcode2', 'location2', '1', '2017-06-20 16:02:00', '1', '2017-06-20 16:02:00');

INSERT INTO BoxChangeLog(boxChangeLogId, boxId, columnsChanged, userId, message, changeTime)
VALUES
(1, 1, '', 1, 'Box created', '2017-06-20 16:02:00'),
(2, 2, '', 1, 'Box created', '2017-06-20 16:02:00');

INSERT INTO `BoxPosition` (`boxId`, `position`, `targetType`, `targetId`)
VALUES
(1, 'B02', 'SAMPLE', 16),
(1, 'A01', 'SAMPLE', 15),
(2, 'A02', 'SAMPLE', 2);

INSERT INTO `Submission` (`submissionId`, `creationDate`, `submittedDate`, `verified`, `description`, `title`, `accession`, `alias`, `completed`)
VALUES
(1, '2012-04-20', '2012-04-20', 0, 'test description 1', 'title 1', 'accession 1', 'alias 1', 0),
(2, '2012-04-20', '2012-04-20', 1, 'test description 2', 'title 2', 'accession 2', 'alias 2', 1),
(3, '2012-04-20', '2012-04-20', 0, 'test description 3', 'title 3', 'accession 3', 'alias 3', 0);

INSERT INTO `Submission_Experiment` (`submission_submissionId`, `experiments_experimentId`)
VALUES
(1, 1),
(2, 1),
(3, 2);

INSERT INTO `Note`(`noteId`, `creationDate`, `internalOnly`, `text`, `owner_userId`)
VALUES
(1, '2016-02-25', 1, 'first note', 1),
(2, '2016-01-23', 0, 'second note', 1),
(3, '2016-03-11', 0, 'third note', 1);

INSERT INTO `SampleNumberPerProject` (`sampleNumberPerProjectId`, `projectId`, `highestSampleNumber`, `padding`, `createdBy`, `updatedBy`, `creationDate`, `lastUpdated`) VALUES
('1', '1', '1', '4', '1', '1', '2016-01-28 14:32:00', '2016-01-28 14:32:00'),
('2', '3', '9999', '4', '1', '1', '2016-01-28 14:32:00', '2016-01-28 14:32:00');

INSERT INTO ArrayModel(arrayModelId, alias, rows, columns) VALUES
(1, 'Test BeadChip', 8, 1);

INSERT INTO Array(arrayId, alias, arrayModelId, serialNumber, description, creator, created, lastModifier, lastModified) VALUES
(1, 'Array_1', 1, '1234', 'test array', 1, '2018-01-26 17:11:00', 1, '2018-01-26 17:11:00');

INSERT INTO ArrayPosition(arrayId, position, sampleId) VALUES
(1, 'R01C01', 19);

INSERT INTO ArrayRun(arrayRunId, alias, instrumentId, arrayId, health, startDate, creator, created, lastModifier, lastModified) VALUES
(1, 'ArrayRun_1', 3, 1, 'Running', '2018-02-02', 1, '2018-02-02 15:40:00', 1, '2018-02-02 15:40:00');

INSERT INTO WorkflowProgress(workflowProgressId, workflowName, userId) VALUES
(1, 'LOAD_SEQUENCER', 3),
(2, 'LOAD_SEQUENCER', 3);

INSERT INTO WorkflowProgressStep(workflowProgressId, stepNumber) VALUES
(1, 0),
(2, 0),
(2, 1);

INSERT INTO StepSample(workflowProgressId, stepNumber, sampleId) VALUES
(1, 0, 1),
(2, 0, 2);

INSERT INTO StepPool(workflowProgressId, stepNumber, poolId) VALUES
(2, 1, 1);

INSERT INTO LibraryTemplate(libraryTemplateId, alias) VALUES
(1, 'pro1_temp1');

INSERT INTO LibraryTemplate_Project(libraryTemplateId, projectId) VALUES
(1, 1);

INSERT INTO Workset(worksetId, alias, description, creator, created, lastModifier, lastModified) VALUES
(1, 'test', 'test workset', 1, '2018-08-08 14:47:00', 1, '2018-08-08 14:47:00'),
(2, 'two', 'second workset', 1, '2020-03-27 18:50:00', 1, '2020-03-27 18:50:00');

INSERT INTO Workset_Sample(worksetId, sampleId) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 1);

INSERT INTO Workset_Library(worksetId, libraryId) VALUES
(2, 1);

INSERT INTO Workset_LibraryAliquot(worksetId, aliquotId) VALUES
(2, 1);

INSERT INTO SequencingOrder(sequencingOrderId, poolId, partitions, parametersId, purposeId, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 1, 1, 1, 1, 'seq order 1', 1, '2019-09-23 10:30:00', 1, '2019-09-23 10:30:00');

INSERT INTO PoolOrder(poolOrderId, alias, description, purposeId, parametersId, partitions, draft, poolId, sequencingOrderId, createdBy, creationDate, updatedBy, lastUpdated) VALUES
(1, 'pool order 1', 'pool order 1 desc', 1, 2, 1, FALSE, NULL, NULL, 1, '2019-09-23 10:30:00', 1, '2019-09-23 10:30:00'),
(2, 'pool order 2', 'pool order 2 desc', 1, NULL, NULL, FALSE, 2, NULL, 1, '2019-09-23 10:30:00', 1, '2019-09-23 10:30:00');

INSERT INTO PoolOrder_LibraryAliquot (poolOrderId, aliquotId) VALUES
(2,6),
(2,5),
(2,4);

INSERT INTO Transfer(transferId, transferTime, senderLabId, senderGroupId, recipientGroupId, creator, created, lastModifier, lastModified) VALUES
(1, '2016-07-07 12:00:00', 1, NULL, 1, 1, '2016-07-07 15:47:00', 1, '2016-07-07 15:47:00'),
(2, '2020-03-18 12:00:00', 1, NULL, 1, 1, '2020-03-18 12:00:00', 1, '2020-03-18 12:00:00'),
(3, '2020-03-18 12:30:00', NULL, 1, 2, 1, '2020-03-18 12:30:00', 1, '2020-03-18 12:30:00');

INSERT INTO Transfer_Sample(transferId, sampleId) VALUES
(1, 1),
(1, 2),
(1, 3);

INSERT INTO Transfer_Library(transferId, libraryId) VALUES
(2, 1),
(2, 2),
(2, 3),
(3, 1),
(3, 2);
