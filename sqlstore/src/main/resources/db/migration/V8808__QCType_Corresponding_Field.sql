ALTER TABLE QCType ADD COLUMN `correspondingField` varchar(50) NOT NULL DEFAULT 'NONE';
ALTER TABLE QCType ADD COLUMN `autoUpdateField` tinyint(1) NOT NULL DEFAULT 0;
