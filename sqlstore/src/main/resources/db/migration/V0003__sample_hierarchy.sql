CREATE TABLE `SampleAnalyte` (
  `sampleAnalyteId` bigint(20) NOT NULL,
  `aliquotNumber` int(11) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `lastUpdated` datetime DEFAULT NULL,
  `purpose` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `stockNumber` int(11) DEFAULT NULL,
  `tubeId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sampleAnalyteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Switch Sample table to InnoDB to permit foreign key constraints.
ALTER TABLE Sample ENGINE = InnoDB ROW_FORMAT = DEFAULT;
ALTER TABLE Sample ADD COLUMN `sampleAnalyteId` BIGINT (20) DEFAULT NULL after taxonIdentifier;
ALTER TABLE Sample ADD FOREIGN KEY (sampleAnalyteId) REFERENCES SampleAnalyte (sampleAnalyteId);