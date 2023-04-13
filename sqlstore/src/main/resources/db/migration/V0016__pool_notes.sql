CREATE TABLE `Pool_Note` (
  `pool_poolId` bigint NOT NULL,
  `notes_noteId` bigint NOT NULL,
  PRIMARY KEY (`pool_poolId`,`notes_noteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
