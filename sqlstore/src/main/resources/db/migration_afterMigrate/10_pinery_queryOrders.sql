--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryAllOrders//
CREATE PROCEDURE queryAllOrders() BEGIN
  PREPARE stmt FROM 'SELECT o.poolOrderId orderId
  , o.creationDate createdDate
  , o.createdBy createdById
  , o.lastUpdated modifiedDate
  , o.updatedBy modifiedById
  , pool.platformType platform
  FROM PoolOrder o
  JOIN Pool pool ON pool.poolId = o.poolId';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest