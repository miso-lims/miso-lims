DELETE FROM Indices;
DELETE FROM IndexFamily;
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

DELETE FROM QCType;
INSERT INTO `QCType` (`qcTypeId`, `name`, `description`, `qcTarget`, `units`)
VALUES
    (2,'Bioanalyzer','Chip-based capillary electrophoresis machine to analyse RNA, DNA, and protein, manufactured by Agilent','Library','nM'),
    (7,'QuBit','Quantitation of DNA, RNA and protein, manufacturered by Invitrogen','Sample','ng/&#181;l'),
    (3,'Bioanalyser','Chip-based capillary electrophoresis machine to analyse RNA, DNA, and protein, manufactured by Agilent','Sample','ng/&#181;l'),
    (4,'QuBit','Quantitation of DNA, RNA and protein, manufacturered by Invitrogen','Library','ng/&#181;l'),
    (6,'SeqInfo QC','Post-run completion run QC step, undertaken by the SeqInfo team, as part of the primary analysis stage.','Run',''),
    (5,'SeqOps QC','Post-run completion run QC step, undertaken by the SeqOps team, to move a run through to the primary analysis stage.','Run',''),
    (1,'qPCR','Quantitative PCR','Library','mol/&#181;l'),
    (8,'poolQcType1', 'qc 1 for pools', 'Pool', 'nM'),
  (9,'poolQcType2', 'qc 2 for pools', 'Pool', 'nM'),
  (10,'poolQcType3', 'qc 3 for pools', 'Pool', 'nM'),
  (11,'poolQcType4', 'qc 4 for pools', 'Pool', 'nM');

DELETE FROM `Printer`;
INSERT INTO Printer(printerId, name, backend, configuration, driver, enabled) VALUES (1, 'foo', 'CUPS', '{}', 'BRADY_1D', 1);
    
DELETE FROM `ReferenceGenome`;
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (1, 'Human hg19 random');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (2, 'Human hg19');
INSERT INTO `ReferenceGenome` (`referenceGenomeId`, `alias`) VALUES (3, 'Human hg18 random');

INSERT INTO `User` (`userId`, `active`, `admin`, `external`, `fullName`, `internal`, `loginName`, `password`, `email`)
VALUES (3,1,0,0,'user',1,'user','user','user@user.user');

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

DELETE FROM `ProjectOverview`;
DELETE FROM `Project`;
INSERT INTO `Project`(`projectId`, `creationDate`, `description`, `name`, `securityProfile_profileId`, `progress`, `alias`, `lastUpdated`, `referenceGenomeId`)
VALUES (1,'2015-08-27 15:40:15','Test project','PRO1',1,'ACTIVE','TEST1','2015-08-27 19:40:40', 1);
INSERT INTO `Project`(`projectId`, `creationDate`, `description`, `name`, `securityProfile_profileId`, `progress`, `alias`, `lastUpdated`, `referenceGenomeId`)
VALUES (2,'2013-11-27 12:20:15','Test project2','PRO2',1,'ACTIVE','TEST2','2015-11-30 15:23:18', 1);
INSERT INTO `Project`(`projectId`, `creationDate`, `description`, `name`, `securityProfile_profileId`, `progress`, `alias`, `lastUpdated`, `referenceGenomeId`)
VALUES (3,'2016-01-27 11:11:15','Test project3','PRO3',1,'ACTIVE','TEST3','2016-02-22 10:43:18', 2);

INSERT INTO ProjectOverview(overviewId, principalInvestigator, startDate, project_projectId)
VALUES
 (1, 'Dr. Professorsworth', NOW(), 1);

DELETE FROM `Study`;
INSERT INTO `Study`(`studyId`, `name`, `description`, `accession`, `securityProfile_profileId`, `project_projectId`, `studyTypeId`, `alias`, `lastModifier`)
VALUES (1,'STU1','Test study1',NULL,1,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study1',1),
(2,'STU2','Test study2',NULL,1,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study2',1),
(3,'STU3','OICR',NULL,1,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study3',1),
(4,'STU4','OICR',NULL,1,1,(SELECT typeId FROM StudyType WHERE name = 'Other'),'Test Study4',1);

DELETE FROM `Experiment`;

DELETE FROM `Sample`;
INSERT INTO `Sample`(`sampleId`, `accession`, `name`, `description`, `securityProfile_profileId`, `identificationBarcode`, `locationBarcode`, `sampleType`, `receivedDate`, `qcPassed`, `alias`, `project_projectId`, `scientificName`, `taxonIdentifier`, `lastModifier`) 
VALUES (1,NULL,'SAM1','Inherited from TEST_0001',1,'SAM1::TEST_0001_Bn_P_nn_1-1_D_1','Freezer1_1','GENOMIC','2015-01-27','true','TEST_0001_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(2,NULL,'SAM2','Inherited from TEST_0001',1,'SAM2::TEST_0001_Bn_R_nn_1-1_D_1','Freezer1_2','GENOMIC','2005-01-27','true','TEST_0001_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(3,NULL,'SAM3','Inherited from TEST_0002',1,'SAM3::TEST_0002_Bn_P_nn_1-1_D_1','Freezer1_3','GENOMIC','2014-01-17','true','TEST_0002_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(4,NULL,'SAM4','Inherited from TEST_0002',1,'SAM4::TEST_0002_Bn_R_nn_1-1_D_1','Freezer1_4','GENOMIC','2015-01-27','true','TEST_0002_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(5,NULL,'SAM5','Inherited from TEST_0003',1,'SAM5::TEST_0003_Bn_P_nn_1-1_D_1','Freezer1_5','GENOMIC','2015-01-27','true','TEST_0003_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(6,NULL,'SAM6','Inherited from TEST_0003',1,'SAM6::TEST_0003_Bn_R_nn_1-1_D_1','Freezer1_6','GENOMIC','2016-01-03','true','TEST_0003_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(7,NULL,'SAM7','Inherited from TEST_0004',1,'SAM7::TEST_0004_Bn_P_nn_1-1_D_1','Freezer1_7','GENOMIC','2015-02-27','true','TEST_0004_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(8,NULL,'SAM8','Inherited from TEST_0004',1,'SAM8::TEST_0004_Bn_R_nn_1-1_D_1','Freezer1_8','GENOMIC','2015-01-07','true','TEST_0004_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(9,NULL,'SAM9','Inherited from TEST_0005',1,'SAM9::TEST_0005_Bn_P_nn_1-1_D_1','Freezer1_9','GENOMIC','2015-01-22','true','TEST_0005_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(10,NULL,'SAM10','Inherited from TEST_0005',1,'SAM10::TEST_0005_Bn_R_nn_1-1_D_1','Freezer1_10','GENOMIC','2015-01-27','true','TEST_0005_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(11,NULL,'SAM11','Inherited from TEST_0006',1,'SAM11::TEST_0006_Bn_P_nn_1-1_D_1','Freezer1_11','GENOMIC','2015-01-27','true','TEST_0006_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(12,NULL,'SAM12','Inherited from TEST_0006',1,'SAM12::TEST_0006_Bn_R_nn_1-1_D_1','Freezer1_12','GENOMIC','2015-01-27','true','TEST_0006_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(13,NULL,'SAM13','Inherited from TEST_0007',1,'SAM13::TEST_0007_Bn_P_nn_1-1_D_1','Freezer1_13','GENOMIC','2015-01-27','true','TEST_0007_Bn_P_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(14,NULL,'SAM14','Inherited from TEST_0007',1,'SAM14::TEST_0007_Bn_R_nn_1-1_D_1','Freezer1_14','GENOMIC','2015-01-27','true','TEST_0007_Bn_R_nn_1-1_D_1',1,'Homo sapiens',NULL,1),
(15,NULL,'SAM15','identity1',1,'SAM15::TEST_0001_IDENTITY_1','Freezer1_1','GENOMIC','2016-04-05','true','TEST_0001_IDENTITY_1',1,'Homo sapiens',NULL,1),
(16,NULL,'SAM16','tissue1',1,'SAM16::TEST_0001_TISSUE_1','Freezer1_1','GENOMIC','2016-04-05','true','TEST_0001_TISSUE_1',1,'Homo sapiens',NULL,1),
(17,NULL,'SAM17','tissue2',1,'SAM17::TEST_0001_TISSUE_2','Freezer1_1','GENOMIC','2016-04-05','true','TEST_0001_TISSUE_2',1,'Homo sapiens',NULL,1);

DELETE FROM `SampleQC`;
INSERT INTO `SampleQC`(`sample_sampleId`, `qcUserName`, `qcDate`, `qcMethod`, `results`) 
VALUES (1,'admin','2015-08-27',1,5),
(2,'admin','2015-08-27',1,5),
(3,'admin','2015-08-27',1,5),
(4,'admin','2015-08-27',1,5),
(5,'admin','2015-08-27',1,5),
(6,'admin','2015-08-27',1,5),
(7,'admin','2015-08-27',1,5),
(8,'admin','2015-08-27',1,5),
(9,'admin','2015-08-27',1,5),
(10,'admin','2015-08-27',1,5),
(11,'admin','2015-08-27',1,5),
(12,'admin','2015-08-27',1,5),
(13,'admin','2015-08-27',1,5),
(14,'admin','2015-08-27',1,5),
(14,'admin','2015-08-28',1,55);

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

DELETE FROM `Library`;
INSERT INTO `Library`(`libraryId`, `name`, `description`, `accession`, `securityProfile_profileId`, `sample_sampleId`, `identificationBarcode`, `locationBarcode`, `libraryType`, `concentration`, `creationDate`, `platformType`, `alias`, `paired`, `librarySelectionType`, `libraryStrategyType`, `qcPassed`, `lastModifier`)
VALUES (1,'LIB1','Inherited from TEST_0001',NULL,1,1,'LIB1::TEST_0001_Bn_P_PE_300_WG','LIBRARY_INBOX_A01',3,0,'2015-08-27','ILLUMINA','TEST_0001_Bn_P_PE_300_WG',1,1,1,'true',1),
(2,'LIB2','Inherited from TEST_0001',NULL,1,2,'LIB2::TEST_0001_Bn_R_PE_300_WG','LIBRARY_INBOX_A02',3,0,'2015-08-27','ILLUMINA','TEST_0001_Bn_R_PE_300_WG',1,1,1,'true',1),
(3,'LIB3','Inherited from TEST_0002',NULL,1,3,'LIB3::TEST_0002_Bn_P_PE_300_WG','LIBRARY_INBOX_A03',3,0,'2015-08-27','ILLUMINA','TEST_0002_Bn_P_PE_300_WG',1,1,1,'true',1),
(4,'LIB4','Inherited from TEST_0002',NULL,1,4,'LIB4::TEST_0002_Bn_R_PE_300_WG','LIBRARY_INBOX_A04',3,0,'2015-08-27','ILLUMINA','TEST_0002_Bn_R_PE_300_WG',1,1,1,'true',1),
(5,'LIB5','Inherited from TEST_0003',NULL,1,5,'LIB5::TEST_0003_Bn_P_PE_300_WG','LIBRARY_INBOX_A05',3,0,'2015-08-27','ILLUMINA','TEST_0003_Bn_P_PE_300_WG',1,1,1,'true',1),
(6,'LIB6','Inherited from TEST_0003',NULL,1,6,'LIB6::TEST_0003_Bn_R_PE_300_WG','LIBRARY_INBOX_A06',3,0,'2015-08-27','ILLUMINA','TEST_0003_Bn_R_PE_300_WG',1,1,1,'true',1),
(7,'LIB7','Inherited from TEST_0004',NULL,1,7,'LIB7::TEST_0004_Bn_P_PE_300_WG','LIBRARY_INBOX_A07',3,0,'2015-08-27','ILLUMINA','TEST_0004_Bn_P_PE_300_WG',1,1,1,'true',1),
(8,'LIB8','Inherited from TEST_0004',NULL,1,8,'LIB8::TEST_0004_Bn_R_PE_300_WG','LIBRARY_INBOX_A08',3,0,'2015-08-27','ILLUMINA','TEST_0004_Bn_R_PE_300_WG',1,1,1,'true',1),
(9,'LIB9','Inherited from TEST_0005',NULL,1,9,'LIB9::TEST_0005_Bn_P_PE_300_WG','LIBRARY_INBOX_A09',3,0,'2015-08-27','ILLUMINA','TEST_0005_Bn_P_PE_300_WG',1,1,1,'true',1),
(10,'LIB10','Inherited from TEST_0005',NULL,1,10,'LIB10::TEST_0005_Bn_R_PE_300_WG','LIBRARY_INBOX_A10',3,0,'2015-08-27','ILLUMINA','TEST_0005_Bn_R_PE_300_WG',1,1,1,'true',1),
(11,'LIB11','Inherited from TEST_0006',NULL,1,11,'LIB11::TEST_0006_Bn_P_PE_300_WG','LIBRARY_INBOX_B01',3,0,'2015-08-27','ILLUMINA','TEST_0006_Bn_P_PE_300_WG',1,1,1,'true',1),
(12,'LIB12','Inherited from TEST_0006',NULL,1,12,'LIB12::TEST_0006_Bn_R_PE_300_WG','LIBRARY_INBOX_B02',3,0,'2015-08-27','ILLUMINA','TEST_0006_Bn_R_PE_300_WG',1,1,1,'true',1),
(13,'LIB13','Inherited from TEST_0007',NULL,1,13,'LIB13::TEST_0007_Bn_P_PE_300_WG','LIBRARY_INBOX_B03',3,0,'2015-08-27','ILLUMINA','TEST_0007_Bn_P_PE_300_WG',1,1,1,'true',1),
(14,'LIB14','Inherited from TEST_0007',NULL,1,14,'LIB14::TEST_0007_Bn_R_PE_300_WG','LIBRARY_INBOX_B04',3,0,'2015-08-27','ILLUMINA','TEST_0007_Bn_R_PE_300_WG',1,1,1,'true',1);

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

DELETE FROM `Kit`;
INSERT INTO `Kit`(`kitId`,`identificationBarcode`,`locationBarcode`,`lotNumber`,`kitDate`,`kitDescriptorId`) VALUES
(1,'1234','Freezer2','LOT34',NOW(),1),
(2,'5678','Freezer3','LOT35',NOW(),2);

DELETE FROM `TargetedSequencing`;
INSERT INTO `TargetedSequencing`(`targetedSequencingId`,`alias`,`description`,`kitDescriptorId`, `archived`, `createdBy`,`creationDate`,`updatedBy`,`lastUpdated`) VALUES
(1,'HALO_IBP','Master Chief',1,0,1,NOW(),1,NOW()),
(2,'Thunderbolts','of lightening, very very frightening',1,0,1,NOW(),1,NOW()),
(3,'Thunderbolts','of lightening, very very frightening',2,0,1,NOW(),1,NOW());


DELETE FROM `LibraryDilution`;
INSERT INTO `LibraryDilution`(`dilutionId`, `concentration`, `library_libraryId`, `identificationBarcode`, `creationDate`, `dilutionUserName`, `name`, `securityProfile_profileId`) 
VALUES (1,2,1,'LDI1::TEST_0001_Bn_P_PE_300_WG','2015-08-27','admin','LDI1',1),
(2,2,2,'LDI2::TEST_0001_Bn_R_PE_300_WG','2015-08-27','admin','LDI2',1),
(3,2,3,'LDI3::TEST_0002_Bn_P_PE_300_WG','2015-08-27','admin','LDI3',1),
(4,2,4,'LDI4::TEST_0002_Bn_R_PE_300_WG','2015-08-27','admin','LDI4',1),
(5,2,5,'LDI5::TEST_0003_Bn_P_PE_300_WG','2015-08-27','admin','LDI5',1),
(6,2,6,'LDI6::TEST_0003_Bn_R_PE_300_WG','2015-08-27','admin','LDI6',1),
(7,2,7,'LDI7::TEST_0004_Bn_P_PE_300_WG','2015-08-27','admin','LDI7',1),
(8,2,8,'LDI8::TEST_0004_Bn_R_PE_300_WG','2015-08-27','admin','LDI8',1),
(9,2,9,'LDI9::TEST_0005_Bn_P_PE_300_WG','2015-08-27','admin','LDI9',1),
(10,2,10,'LDI10::TEST_0005_Bn_R_PE_300_WG','2015-08-27','admin','LDI10',1),
(11,2,11,'LDI11::TEST_0006_Bn_P_PE_300_WG','2015-08-27','admin','LDI11',1),
(12,2,12,'LDI12::TEST_0006_Bn_R_PE_300_WG','2015-08-27','admin','LDI12',1),
(13,2,13,'LDI13::TEST_0007_Bn_P_PE_300_WG','2015-08-27','admin','LDI13',1),
(14,2,14,'LDI14::TEST_0007_Bn_R_PE_300_WG','2015-08-27','admin','LDI14',1);

DELETE FROM `LibraryQC`;
INSERT INTO `LibraryQC`(`qcId`, `library_libraryId`, `qcUserName`, `qcDate`, `qcMethod`, `results`, `insertSize`) 
VALUES (1,1,'admin','2015-08-27',4,3,300),(2,2,'admin','2015-08-27',4,3,300),(3,3,'admin','2015-08-27',4,3,300),
(4,4,'admin','2015-08-27',4,3,300),(5,5,'admin','2015-08-27',4,3,300),(6,6,'admin','2015-08-27',4,3,300),
(7,7,'admin','2015-08-27',4,3,300),(8,8,'admin','2015-08-27',4,3,300),(9,9,'admin','2015-08-27',4,3,300),
(10,10,'admin','2015-08-27',4,3,300),(11,11,'admin','2015-08-27',4,3,300),(12,12,'admin','2015-08-27',4,3,300),
(13,13,'admin','2015-08-27',4,3,300),(14,14,'admin','2015-08-27',4,3,300);

DELETE FROM `Library_Index`;
INSERT INTO `Library_Index` 
VALUES (1,12),(2,11),(3,10),(4,9),(5,8),(6,7),(7,6),(8,5),(9,4),(10,3),(11,2),(12,1),(13,24),(14,23);

DELETE FROM `Pool`;
INSERT INTO `Pool`(`poolId`, `concentration`, `identificationBarcode`, `name`, `description`, `creationDate`, `securityProfile_profileId`, `platformType`, `ready`, `alias`, `qcPassed`) 
VALUES (1,2,'IPO1::Illumina','IPO1','TEST','2015-08-27',2,'ILLUMINA',1,'Pool 1',NULL),
(2,2,'IPO2::Illumina','IPO2','TEST','2015-08-27',3,'ILLUMINA',1,'Pool 2',NULL),
(3,2,'IPO3::Illumina','IPO3','TEST','2015-08-27',4,'ILLUMINA',1,'Pool 3',NULL),
(4,2,'IPO4::Illumina','IPO4','TEST','2015-08-27',5,'ILLUMINA',1,'Pool 4',NULL),
(5,2,'IPO5::Illumina','IPO5','TEST','2015-08-27',6,'ILLUMINA',1,'Pool 5',NULL),
(6,2,'IPO6::Illumina','IPO6','TEST','2015-08-27',7,'ILLUMINA',0,'Pool 6',NULL),
(7,2,'IPO7::Illumina','IPO7','TEST','2015-08-27',8,'ILLUMINA',0,'Pool 7',NULL),
(8,2,'IPO8::Illumina','IPO8','TEST','2015-08-27',9,'ILLUMINA',0,'Pool 8',NULL),
(9,2,'IPO9::Illumina','IPO9','TEST','2015-08-27',10,'ILLUMINA',0,'Pool 9',NULL),
(10,2,'IPO10::Illumina','IPO10','TEST','2015-08-27',11,'ILLUMINA',0,'Pool 10',NULL);

DELETE FROM `Pool_Dilution`;
INSERT INTO `Pool_Dilution`(`pool_poolId`, `dilution_dilutionId`) 
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

INSERT INTO `Experiment`(`experimentId`, `name`, `description`, `accession`, `title`, `securityProfile_profileId`, `study_studyId`, `alias`, `platform_platformId`,`lastModifier`, `pool_poolId`) 
VALUES
(1,'EXP1','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_1',16,1, 1),
(2,'EXP2','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_2',16,1, 2),
(3,'EXP3','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_3',16,1, 3),
(4,'EXP4','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_4',16,1, 3),
(5,'EXP5','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_5',16,1, 4),
(6,'EXP6','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_6',16,1, 4),
(7,'EXP7','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_7',16,1, 4),
(8,'EXP8','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_8',16,1, 4),
(9,'EXP9','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_9',16,1, 5),
(10,'EXP10','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_10',16,1, 5),
(11,'EXP11','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_11',16,1, 5),
(12,'EXP12','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_12',16,1, 5),
(13,'EXP13','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_13',16,1, 6),
(14,'EXP14','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_14',16,1, 6),
(15,'EXP15','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_15',16,1, 6),
(16,'EXP16','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_16',16,1, 6),
(17,'EXP17','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_17',16,1, 7),
(18,'EXP18','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_18',16,1, 7),
(19,'EXP19','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_19',16,1, 7),
(20,'EXP20','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_20',16,1, 7),
(21,'EXP21','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_21',16,1, 8),
(22,'EXP22','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_22',16,1, 8),
(23,'EXP23','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_23',16,1, 8),
(24,'EXP24','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_24',16,1, 8),
(25,'EXP25','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_25',16,1, 10),
(26,'EXP26','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_26',16,1, 10),
(27,'EXP27','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_27',16,1, 10),
(28,'EXP28','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_28',16,1, 10),
(29,'EXP29','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_29',16,1, 9),
(30,'EXP30','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_30',16,1, 9),
(31,'EXP31','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_31',16,1, 9),
(32,'EXP32','TEST',NULL,'PRO1 Illumina Other experiment (Auto-gen)',1,1,'EXP_AUTOGEN_STU1_Other_32',16,1, 9);

DELETE FROM `PoolQC`;
INSERT INTO `PoolQC`(`qcId`, `pool_poolId`, `qcUserName`, `qcDate`, `qcMethod`, `results`)
VALUES (1,1,'person','2016-03-18',1,12.3),
(2,1,'person','2016-03-18',1,45.6),
(3,2,'person','2016-03-18',1,7.89);

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

DELETE FROM `SequencerReference`;
INSERT INTO `SequencerReference`(`referenceId`, `name`, `ip`, `platformId`)
VALUES (1,'SN7001179','localhost',16),
(2,'h1180','localhost',16);

DELETE FROM `SequencerServiceRecord`;
INSERT INTO `SequencerServiceRecord`(`recordId`, `sequencerReferenceId`, `title`, `details`, `servicedBy`, `referenceNumber`, `serviceDate`, `shutdownTime`, `restoredTime`)
VALUES (1,1,'Seq1_Rec1','Test service','Service Person','12345','2016-01-01', '2016-01-01 07:30:00', '2016-01-01 09:00:00'),
(2,1,'Seq1_Rec2',NULL,'Service Person',NULL,'2016-01-21',NULL,NULL),
(3,2,'Seq2_Rec1',NULL,'Service Person',NULL,'2016-01-21',NULL,NULL);

DELETE FROM `RunIllumina`;
DELETE FROM `RunPacBio`;
DELETE FROM `RunLS454`;
DELETE FROM `Run`;
INSERT INTO `Run`(`runId`, `name`, `description`, `accession`, `pairedEnd`, `filePath`, `securityProfile_profileId`, `alias`, `sequencerReference_sequencerReferenceId`, `lastModifier`, `health`, `completionDate`) 
VALUES (1,'RUN1','BC0JHTACXX',NULL,1,'/.mounts/labs/prod/archive/h1179/120323_h1179_0070_BC0JHTACXX',12,'120323_h1179_0070_BC0JHTACXX',1,1,'Completed','2012-03-31'),
(2,'RUN2','AD0VJ9ACXX',NULL,1,'/.mounts/labs/prod/archive/h1179/120404_h1179_0072_AD0VJ9ACXX',13,'120404_h1179_0072_AD0VJ9ACXX',1,1,'Failed','2012-04-04'),
(3,'RUN3','BC075RACXX',NULL,1,'/.mounts/labs/prod/archive/h1179/120412_h1179_0073_BC075RACXX',14,'120412_h1179_0073_BC075RACXX',1,1,'Completed','2012-04-20'),
(4,'RUN4','AC0KY7ACXX',NULL,1,'/.mounts/labs/prod/archive/h1179/120314_h1179_0068_AC0KY7ACXX',15,'120314_h1179_0068_AC0KY7ACXX',1,1,'Completed','2012-03-23');
INSERT INTO RunIllumina(runId) VALUES (1), (2), (3), (4);

INSERT INTO `RunChangeLog`(`runId`, `columnsChanged`, `userId`, `message`, `changeTime`)
VALUES (1, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:49'),
(2, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:51'),
(3, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:53'),
(4, 'qcPassed', 1, 'false -> true', '2016-07-07 13:30:55');

DELETE FROM RunQC;
INSERT INTO `RunQC`(`run_runId`, `qcUserName`, `qcDate`, `qcMethod`, `information`, `doNotProcess`)
VALUES ( 1, 'username1', '2016-01-26', 1, 'information1', 1),
( 2, 'username2', '2016-02-26', 2, 'information2', 0),
( 3, 'username3', '2015-03-26', 3, 'information3', 1);

DELETE FROM `_Partition`;
INSERT INTO `_Partition` (`partitionId`, `partitionNumber`, `pool_poolId`, `securityProfile_profileId`)
VALUES (1,1,1,1);

DELETE FROM `SequencerPartitionContainer`;
INSERT INTO `SequencerPartitionContainer`(`containerId`, `securityProfile_profileId`, `identificationBarcode`, `locationBarcode`, `platform`, `validationBarcode`, `lastModifier`) 
VALUES (1,12,'C0JHTACXX','',16,'',1),(2,13,'D0VJ9ACXX','',16,'',1),(3,14,'C075RACXX','',16,'',1),(4,15,'C0KY7ACXX','',16,'',1);

INSERT INTO `SequencerPartitionContainerChangeLog`(`containerId`, `columnsChanged`, `userId`, `message`, `changeTime`)
VALUES (1, 'validationBarcode', 1, 'NULL -> real', '2016-07-07 13:30:47'),
(2, 'validationBarcode', 1, 'NULL -> real', '2016-07-07 13:30:49'),
(3, 'validationBarcode', 1, 'NULL -> real', '2016-07-07 13:30:51'),
(4, 'validationBarcode', 1, 'NULL -> real', '2016-07-07 13:30:53');

DELETE FROM `SequencerPartitionContainer_Partition`;
INSERT INTO `SequencerPartitionContainer_Partition`(`container_containerId`, `partitions_partitionId`) 
VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(2,9),(2,10),(2,11),(2,12),(2,13),(2,14),(2,15),(2,16),(3,17),(3,18),(3,19),
(3,20),(3,21),(3,22),(3,23),(3,24),(4,25),(4,26),(4,27),(4,28),(4,29),(4,30),(4,31),(4,32);

DELETE FROM RunQC_Partition;
INSERT INTO `RunQC_Partition`(`runQc_runQcId`, `partition_partitionId`)
VALUES (1, 1);

DELETE FROM `Run_SequencerPartitionContainer`;
INSERT INTO `Run_SequencerPartitionContainer`(`Run_runId`, `containers_containerId`) 
VALUES (1,1),(2,2),(3,3),(4,4);

DELETE FROM `BoxSize`;
INSERT INTO `BoxSize` (`boxSizeId`, `rows`, `columns`, `scannable`)
VALUES
('1', '4', '4', '0');

DELETE FROM `BoxUse`;
INSERT INTO `BoxUse` (`boxUseId`, `alias`)
VALUES
('1', 'boxuse1'),
('2', 'boxuse2');

DELETE FROM Box;
INSERT INTO `Box` (`boxId`, `boxSizeId`, `boxUseId`, `name`, `alias`, `description`, `identificationBarcode`, `locationBarcode`, `securityProfile_profileId`, `lastModifier`)
VALUES
('1','1', '1', 'box1', 'box1alias', 'box1', 'barcode1','identifcationbarcode1', '1', '1'),
('2','1', '2', 'box2', 'box2alias', 'box2', 'barcode2','identifcationbarcode2', '1', '1');

DELETE FROM BoxPosition;
INSERT INTO `BoxPosition` (`boxId`, `position`, `targetType`, `targetId`)
VALUES
('1', 'B02', 'Sample', 1),
('2', 'A02', 'Sample', 2);

DELETE FROM Submission;
INSERT INTO `Submission` (`submissionId`, `creationDate`, `submittedDate`, `verified`, `description`, `name`, `title`, `accession`, `alias`, `completed`)
VALUES
(1, '2012-04-20', '2012-04-20', 0, 'test description 1', 'name 1', 'title 1', 'accession 1', 'alias 1', 0),
(2, '2012-04-20', '2012-04-20', 1, 'test description 2', 'name 2', 'title 2', 'accession 2', 'alias 2', 1),
(3, '2012-04-20', '2012-04-20', 0, 'test description 3', 'name 3', 'title 3', 'accession 3', 'alias 3', 0);

DELETE FROM Submission_Partition_Dilution;
INSERT INTO Submission_Partition_Dilution(submission_submissionId, partition_partitionId, dilution_dilutionId) 
VALUES
(3, 1, 1);

DELETE FROM Submission_Experiment;
INSERT INTO `Submission_Experiment` (`submission_submissionId`, `experiments_experimentId`)
VALUES
(1, 1),
(2, 1),
(3, 2);

DELETE FROM Alert;
INSERT INTO `Alert` (
  `alertId`,
  `title`,
  `text`,
  `userId`,
  `date`,
  `isRead`,
  `level`,
) VALUES 
(1, 'Alert 1', 'Alert 1 Text', 1, '2012-04-20', 0, 'INFO'),
(2, 'Alert 2', 'Alert 2 Text', 1, '2012-04-20', 1, 'INFO'),
(3, 'Alert 3', 'Alert 3 Text', 1, '2012-04-20', 0, 'INFO');

DELETE FROM Note;
INSERT INTO `Note`(`noteId`, `creationDate`, `internalOnly`, `text`, `owner_userId`)
VALUES
(1, '2016-02-25', 1, 'first note', 1),
(2, '2016-01-23', 0, 'second note', 1),
(3, '2016-03-11', 0, 'third note', 1);

DELETE FROM `ProjectOverview_note`;

DELETE FROM `Kit_Note`;

DELETE FROM `Sample_Note`;

DELETE FROM `Library_Note`;

DELETE FROM `Run_Note`;

DELETE FROM `Pool_Note`;

-- DetailedSample and DetailedLibrary values
INSERT INTO `SampleClass`(`sampleClassId`, `alias`, `sampleCategory`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,'Identity','Identity',1,'2016-04-05 14:57:00',1,'2016-04-05 14:57:00'),
(2,'Primary Tumor Tissue','Tissue',1,'2016-04-05 14:57:00',1,'2016-04-05 14:57:00');

DELETE FROM `LibraryDesignCode`;
INSERT INTO `LibraryDesignCode`(`code`,`description`) 
VALUES ('TT', 'TEST');

DELETE FROM `LibraryDesign`;
INSERT INTO `LibraryDesign`(`libraryDesignId`, `name`, `sampleClassId`, `librarySelectionType`, `libraryStrategyType`, `libraryDesignCodeId`)
VALUES (1, 'DESIGN1', 1, 1, 1, 1), 
(2, 'DESIGN2', 2, 1, 1, 1);

INSERT INTO `DetailedSample`(`sampleId`, `sampleClassId`, `archived`, `parentId`)
VALUES (15,1,0,NULL),
(16,2,0,15),
(17,2,0,15);

DELETE FROM `Identity`;
INSERT INTO `Identity` (`sampleId`, `externalName`,`donorSex`)
VALUES (15, '15_EXT15,EXT15','UNKNOWN');

INSERT INTO `SampleTissue`(`sampleId`)
VALUES (16),
(17);

DELETE FROM `Institute`;
INSERT INTO `Institute`(`instituteId`, `alias`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,'Institute A',1,'2016-01-28 14:32:00',1,'2016-01-28 14:32:00'),(2,'Institute B',1,'2016-01-29 09:32:00',1,'2016-01-29 09:32:00');

DELETE FROM `Lab`;
INSERT INTO `Lab`(`labId`, `instituteId`, `alias`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,1,'Lab A1',1,'2016-02-10 15:35:00',1,'2016-02-10 15:35:00'),(2,1,'Lab A2',1,'2016-02-10 15:35:00',1,'2016-02-10 15:35:00'),
(3,1,'Lab B1',1,'2016-02-10 15:35:00',1,'2016-02-10 15:35:00'),(4,1,'Lab B2',1,'2016-02-10 15:35:00',1,'2016-02-10 15:35:00');

INSERT INTO `SampleNumberPerProject`
(`sampleNumberPerProjectId`, `projectId`, `highestSampleNumber`, `padding`, `createdBy`, `updatedBy`, `creationDate`, `lastUpdated`)
VALUES ('1', '1', '1', '4', '1', '1', '2016-01-28 14:32:00', '2016-01-28 14:32:00');
INSERT INTO `SampleNumberPerProject`
(`sampleNumberPerProjectId`, `projectId`, `highestSampleNumber`, `padding`, `createdBy`, `updatedBy`, `creationDate`, `lastUpdated`)
VALUES ('2', '3', '9999', '4', '1', '1', '2016-01-28 14:32:00', '2016-01-28 14:32:00');

DELETE FROM `TissueOrigin`;
INSERT INTO `TissueOrigin`(`tissueOriginId`, `alias`, `description`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,'Test Origin','for testing',1,'2016-02-19 11:28:00',1,'2016-02-19 11:28:00');

DELETE FROM `TissueType`;
INSERT INTO `TissueType`(`tissueTypeId`, `alias`, `description`, `createdBy`, `creationDate`, `updatedBy`, `lastUpdated`)
VALUES (1,'Test Type','for testing',1,'2016-02-19 11:28:00',1,'2016-02-19 11:28:00');

DELETE FROM `DetailedLibrary`;
INSERT INTO `DetailedLibrary`(`libraryId`, `kitDescriptorId`, `libraryDesignCodeId`)
VALUES (1,1,1);
