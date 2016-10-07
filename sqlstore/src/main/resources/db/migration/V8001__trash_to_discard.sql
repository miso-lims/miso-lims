ALTER TABLE Pool CHANGE COLUMN emptied discarded tinyint(1) NOT NULL DEFAULT '0';
ALTER TABLE Library CHANGE COLUMN emptied discarded tinyint(1) NOT NULL DEFAULT '0';
ALTER TABLE Sample CHANGE COLUMN emptied discarded tinyint(1) NOT NULL DEFAULT '0';