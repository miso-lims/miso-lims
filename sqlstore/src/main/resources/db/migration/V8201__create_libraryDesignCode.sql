CREATE TABLE `LibraryDesignCode` (
  `libraryDesignCodeId` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(2) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`libraryDesignCodeId`),
  UNIQUE KEY `libraryDesignCode_unique` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;