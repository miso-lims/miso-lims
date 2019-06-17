RENAME TABLE PoolOrder TO SequencingOrder;
ALTER TABLE SequencingOrder CHANGE COLUMN poolOrderId sequencingOrderId bigint(20) NOT NULL AUTO_INCREMENT;

DROP VIEW IF EXISTS OrderCompletion_Items;
DROP VIEW IF EXISTS OrderCompletion;
DROP VIEW IF EXISTS OrderCompletion_Backing;

DROP TRIGGER IF EXISTS PoolOrderInsert;
DROP TRIGGER IF EXISTS PoolOrderDelete;
DROP TRIGGER IF EXISTS PoolOrderChange;
