ALTER TABLE Library ADD CONSTRAINT library_boxPositionId_unique UNIQUE (boxPositionId);
ALTER TABLE Pool ADD CONSTRAINT pool_boxPositionId_unique UNIQUE (boxPositionId);
ALTER TABLE Sample ADD CONSTRAINT sample_boxPositionId_unique UNIQUE (boxPositionId);
