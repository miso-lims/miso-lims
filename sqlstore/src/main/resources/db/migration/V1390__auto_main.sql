-- fix_arraymodel
ALTER TABLE ArrayModel CHANGE COLUMN `rows` arrayModelRows tinyint(3) UNSIGNED;
ALTER TABLE ArrayModel CHANGE COLUMN `columns` arrayModelColumns tinyint(3) UNSIGNED;

