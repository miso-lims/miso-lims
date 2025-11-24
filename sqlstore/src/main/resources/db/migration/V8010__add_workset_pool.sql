CREATE TABLE `Workset_Pool`(
    `worksetId` BIGINT NOT NULL,
    `poolId` BIGINT NOT NULL,
    PRIMARY KEY (`worksetId`, `poolId`),
    CONSTRAINT fk_workset_pool_workset
        FOREIGN KEY (`worksetId`) REFERENCES `Workset`(`worksetId`),
    CONSTRAINT fk_workset_pool_pool
        FOREIGN KEY (`poolId`) REFERENCES `Pool`(`poolId`)
);

CREATE INDEX idx_workset_pool_worksetId on `Workset_Pool` (`worksetId`);
CREATE INDEX idx_workset_pool_poolId on `Workset_Pool` (`poolId`);