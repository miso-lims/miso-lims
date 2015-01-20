USE lims;

CREATE TABLE `Submission_Partition_Dilution` (
  `submission_submissionId` bigint(20) NOT NULL,
  `partition_partitionId` bigint(20) NOT NULL,
  `dilution_dilutionId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`partition_partitionId`,`dilution_dilutionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

