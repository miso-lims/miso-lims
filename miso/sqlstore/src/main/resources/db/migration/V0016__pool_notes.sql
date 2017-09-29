CREATE TABLE `Pool_Note` (
  `pool_poolId` bigint(20) NOT NULL,
  `notes_noteId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`notes_noteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
