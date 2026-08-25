-- sop_InstrumentId
ALTER TABLE SopField ADD COLUMN instrumentModelId BIGINT;
ALTER TABLE SopField ADD CONSTRAINT fk_sopField_instrumentModel FOREIGN KEY (instrumentModelId) REFERENCES InstrumentModel (instrumentModelId);

