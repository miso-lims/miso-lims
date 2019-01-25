SELECT o.poolOrderId orderId, o.creationDate createdDate, o.createdBy createdById, 
o.lastUpdated modifiedDate, o.updatedBy modifiedById, pool.platformType platform FROM PoolOrder o 
JOIN Pool pool ON pool.poolId = o.poolId WHERE EXISTS(SELECT * FROM Pool_Dilution WHERE Pool_Dilution.pool_poolId = o.poolId)
