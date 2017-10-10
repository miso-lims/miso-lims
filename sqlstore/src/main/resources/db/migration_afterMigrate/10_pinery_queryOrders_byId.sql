--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryOrderById//
CREATE PROCEDURE queryOrderById(
  iOrderId BIGINT(20)
) BEGIN
  PREPARE stmt FROM 'SELECT o.poolOrderId orderId
    , o.creationDate createdDate
    , o.createdBy createdById
    , o.lastUpdated modifiedDate
    , o.updatedBy modifiedById
    , pool.platformType platform
    FROM PoolOrder o
    JOIN Pool pool ON pool.poolId = o.poolId
    WHERE poolOrderId = ?';
  SET @poolOrderId = iOrderId;
  EXECUTE stmt USING @poolOrderId;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest