DROP TABLE IF EXISTS Workset_Pool;
CREATE TABLE Workset_Pool(
    worksetId BIGINT NOT NULL,
    poolId BIGINT NOT NULL,
    addedTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (worksetId, poolId),
    CONSTRAINT fk_worksetPool_workset
        FOREIGN KEY (worksetId) REFERENCES Workset(worksetId),
    CONSTRAINT fk_worksetPool_pool
        FOREIGN KEY (poolId) REFERENCES Pool(poolId)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
