-- Rename old table
ALTER TABLE `BoxSize`
    RENAME TO `BoxSize_old`;

-- Recreate table
CREATE TABLE `BoxSize` (
  `boxSizeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `boxSizeRows` bigint(20) NOT NULL,
  `boxSizeColumns` bigint(20) NOT NULL,
  `scannable` boolean DEFAULT 0 NOT NULL,
  PRIMARY KEY (`boxSizeId`),
  UNIQUE (`boxSizeRows`, `boxSizeColumns`, `scannable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Copy table
INSERT INTO `BoxSize` (`boxSizeId`, `boxSizeRows`, `boxSizeColumns`, `scannable`)
    SELECT `boxSizeId`, `rows`, `columns`, `scannable`
    FROM `BoxSize_old`;

-- Update Box table
ALTER TABLE `Box`
    DROP FOREIGN KEY `fk_box_boxSize`;

ALTER TABLE `Box`
    ADD CONSTRAINT `fk_box_boxSize` FOREIGN KEY (`boxSizeId`) REFERENCES `BoxSize`(`boxSizeId`);

-- Drop old table
DROP TABLE `BoxSize_old`;