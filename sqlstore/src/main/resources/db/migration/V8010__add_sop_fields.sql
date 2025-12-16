
CREATE TABLE SopField (
    sopFieldId BIGINT AUTO_INCREMENT PRIMARY KEY,
    sopId BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    units VARCHAR(50),
    fieldType VARCHAR(50) NOT NULL DEFAULT 'TEXT',
    
    CONSTRAINT fk_sopfield_sop 
        FOREIGN KEY (sopId) REFERENCES Sop(sopId) ON DELETE CASCADE,
    CONSTRAINT unique_sop_field_name 
        UNIQUE KEY (sopId, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;