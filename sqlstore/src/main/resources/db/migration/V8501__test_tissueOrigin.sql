ALTER TABLE AssayTest
  ADD COLUMN `tissueOriginId` bigint DEFAULT NULL,
  ADD COLUMN `negateTissueOrigin` BOOLEAN NOT NULL DEFAULT FALSE,
  ADD CONSTRAINT fk_assayTest_tissueOrigin FOREIGN KEY (tissueOriginId) REFERENCES TissueOrigin (tissueOriginId);
