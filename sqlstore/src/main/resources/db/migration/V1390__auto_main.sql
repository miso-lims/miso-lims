-- fix_arraymodel
ALTER TABLE ArrayModel CHANGE COLUMN `rows` arrayModelRows tinyint UNSIGNED;
ALTER TABLE ArrayModel CHANGE COLUMN `columns` arrayModelColumns tinyint UNSIGNED;

