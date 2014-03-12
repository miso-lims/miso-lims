USE lims;

CREATE TABLE `EntityGroup` (
  `entityGroupId` bigint(20) NOT NULL AUTO_INCREMENT,
  `parentId` bigint(20) NOT NULL,
  `parentType` varchar(255) NOT NULL,
  PRIMARY KEY (`entityGroupId`,`parentId`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `EntityGroup_Elements` (
  `entityGroup_entityGroupId` bigint(20) NOT NULL,
  `entityId` bigint(20) NOT NULL,
  `entityType` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`entityGroup_entityGroupId`,`entityId`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
