CREATE TABLE `sequence_data` (
    `sequence_name` varchar(100) NOT NULL,
    `sequence_cur_value` bigint unsigned DEFAULT 1,
    PRIMARY KEY (`sequence_name`)
) ENGINE=MyISAM;

CREATE TABLE BoxSize (
  boxSizeId bigint NOT NULL AUTO_INCREMENT,
  `rows` bigint NOT NULL,
  `columns` bigint NOT NULL,
  scannable boolean DEFAULT 0,
  PRIMARY KEY (boxSizeId),
  UNIQUE (`rows`, `columns`, scannable)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

CREATE TABLE BoxUse (
  boxUseId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  PRIMARY KEY (boxUseId),
  UNIQUE (alias)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

CREATE TABLE Box (
  boxId bigint NOT NULL AUTO_INCREMENT,
  boxSizeId bigint NOT NULL,
  boxUseId bigint NOT NULL,
  name varchar(255) NOT NULL,
  alias varchar(255) NOT NULL,
  description varchar(255) DEFAULT NULL,
  identificationBarcode varchar(255) DEFAULT NULL UNIQUE,
  locationBarcode varchar(255) DEFAULT NULL,
  securityProfile_profileId bigint DEFAULT NULL,
  lastModifier bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (boxId),
  FOREIGN KEY (boxSizeId) REFERENCES BoxSize(boxSizeId),
  FOREIGN KEY (boxUseId) REFERENCES BoxUse(boxUseId),
  FOREIGN KEY(lastModifier) REFERENCES User(userId),
  UNIQUE (name),
  UNIQUE (alias)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

CREATE TABLE BoxPosition (
  boxPositionId bigint NOT NULL,
  boxId bigint NOT NULL REFERENCES Box(boxId) ON DELETE CASCADE,
  `column` bigint NOT NULL,
  `row` bigint NOT NULL,
  lastModifier bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (boxPositionId),
  UNIQUE KEY (`boxId`, `column`, `row`),
  FOREIGN KEY(lastModifier) REFERENCES User(userId)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

CREATE TABLE BoxChangeLog (
  boxId bigint NOT NULL REFERENCES Box(boxId),
  columnsChanged text NOT NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

ALTER TABLE Sample ADD COLUMN (
	boxPositionId bigint,
	emptied boolean NOT NULL DEFAULT 0,
	volume double DEFAULT NULL);

ALTER TABLE Sample
  MODIFY identificationBarcode VARCHAR(255) UNIQUE;

SET @sequence = 0;
UPDATE Sample SET boxPositionId = @sequence := @sequence + 1;

ALTER TABLE Library ADD COLUMN (
	boxPositionId bigint,
	emptied boolean NOT NULL DEFAULT 0,
	volume double DEFAULT NULL);

ALTER TABLE Library
  MODIFY identificationBarcode VARCHAR(255) UNIQUE;

UPDATE Library SET boxPositionId = @sequence := @sequence + 1;

ALTER TABLE Pool ADD COLUMN (
  boxPositionId bigint,
  emptied boolean NOT NULL DEFAULT 0,
  volume double DEFAULT NULL);

ALTER TABLE Pool 
  MODIFY identificationBarcode VARCHAR(255) UNIQUE;

UPDATE Pool SET boxPositionId = @sequence := @sequence + 1;

INSERT INTO sequence_data(sequence_name, sequence_cur_value) VALUES ('box_position_seq', @sequence);
DROP TRIGGER IF EXISTS BeforeInsertPool;
DELIMITER //
CREATE TRIGGER BeforeInsertPool BEFORE INSERT ON Pool
  FOR EACH ROW
  BEGIN
    UPDATE sequence_data
    SET sequence_cur_value = sequence_cur_value + 1
    WHERE sequence_name = 'box_position_seq';

    SET NEW.boxPositionId = (SELECT sequence_cur_value FROM sequence_data WHERE sequence_name = 'box_position_seq');
  END//
DELIMITER ;
