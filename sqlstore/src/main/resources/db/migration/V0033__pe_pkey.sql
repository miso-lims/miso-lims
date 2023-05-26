ALTER TABLE Pool_Elements DROP PRIMARY KEY, ADD PRIMARY KEY(`pool_poolId`,`elementType`,`elementId`);
