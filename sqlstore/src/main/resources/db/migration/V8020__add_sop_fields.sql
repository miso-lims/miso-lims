CREATE TABLE SopField (
    sopFieldId BIGINT AUTO_INCREMENT PRIMARY KEY,
    sopId BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    units VARCHAR(50),
    fieldType VARCHAR(50) NOT NULL,

    CONSTRAINT fk_sopfield_sop
        FOREIGN KEY (sopId) REFERENCES Sop(sopId)
        ON DELETE CASCADE,

    CONSTRAINT uq_sopfield_sop_name
        UNIQUE KEY (sopId, name),

    INDEX idx_sopfield_sop (sopId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
