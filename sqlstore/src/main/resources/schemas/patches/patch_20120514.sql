
CREATE TABLE `SequencerPartitionContainer` (
  `containerId` bigint(20) NOT NULL AUTO_INCREMENT,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `platformType` varchar(50) DEFAULT NULL,
  `validationBarcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`containerId`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

CREATE TABLE `Partition` (
  `partitionId` bigint(20) NOT NULL AUTO_INCREMENT,
  `partitionNumber` tinyint(4) NOT NULL,
  `pool_poolId` bigint(20) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`partitionId`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

CREATE TABLE `SequencerPartitionContainer_Partition` (
  `container_containerId` bigint(20) NOT NULL,
  `partitions_partitionId` bigint(20) NOT NULL,
  PRIMARY KEY (`container_containerId`,`partitions_partitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `Run_SequencerPartitionContainer` (
  `Run_runId` bigint(20) NOT NULL,
  `containers_containerId` bigint(20) NOT NULL,
  PRIMARY KEY (`Run_runId`,`containers_containerId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `Submission_Partition` (
  `submission_submissionId` bigint(20) NOT NULL,
  `partitions_partitionId` bigint(20) NOT NULL,
  PRIMARY KEY (`submission_submissionId`,`partitions_partitionId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `RunQC_Partition` CHANGE `flowcells_flowcellId` `containers_containerId` BIGINT(20) DEFAULT NULL;
ALTER TABLE `Platform` CHANGE `numFlowcells` `numContainers` TINYINT(4) NOT NULL;

INSERT INTO SequencerPartitionContainer(containerId, securityProfile_profileId, identificationBarcode, locationBarcode, platformType, validationBarcode)
SELECT flowcellId, securityProfile_profileId, identificationBarcode, locationBarcode, platformType, validationBarcode FROM Flowcell;

INSERT INTO Run_SequencerPartitionContainer(Run_runId, containers_containerId)
SELECT Run_runId, flowcells_flowcellId FROM Run_Flowcell;

INSERT INTO Partition(partitionId, partitionNumber, pool_poolId, securityProfile_profileId)
SELECT laneId, laneNumber, pool_poolId, securityProfile_profileId FROM Lane;

INSERT INTO SequencerPartitionContainer_Partition(container_containerId, partitions_partitionId)
SELECT Flowcell_flowcellId, lanes_laneId FROM Flowcell_Lane;

INSERT INTO Partition(partitionId, partitionNumber, pool_poolId, securityProfile_profileId)
SELECT (chamberId+(SELECT MAX(laneId) FROM Lane)), chamberNumber, pool_poolId, securityProfile_profileId FROM Chamber;

INSERT INTO SequencerPartitionContainer_Partition(container_containerId, partitions_partitionId)
SELECT Flowcell_flowcellId, (chambers_chamberId+(SELECT MAX(laneId) FROM Lane)) FROM Flowcell_Chamber;
