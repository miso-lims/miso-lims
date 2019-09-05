ALTER TABLE Pool ADD COLUMN `poolOrderId` BIGINT(20);
ALTER TABLE Pool ADD COLUMN `poolOrderMismatch` tinyint(1) NOT NULL DEFAULT 0;
ALTER TABLE Pool ADD CONSTRAINT Pool_to_PoolOrder_id FOREIGN KEY (poolOrderId) REFERENCES PoolOrder (poolOrderId);
