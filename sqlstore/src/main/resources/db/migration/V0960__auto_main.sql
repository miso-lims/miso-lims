-- sample_class_alias
ALTER TABLE SampleClass ADD COLUMN sampleSubcategory varchar(50);

UPDATE SampleClass SET sampleSubcategory = alias WHERE alias IN ('LCM Tube', 'Slide', 'Single Cell');
UPDATE SampleClass SET sampleSubcategory = 'Single Cell (stock)' WHERE alias = 'Single Cell DNA (stock)';
UPDATE SampleClass SET sampleSubcategory = 'Single Cell (aliquot)' WHERE alias = 'Single Cell DNA (aliquot)';

-- unique_keys
ALTER TABLE SequencingContainerModel ADD CONSTRAINT uk_sequencingContainerModel_platform_alias UNIQUE (platformType, alias);
ALTER TABLE SequencingContainerModel ADD CONSTRAINT uk_sequencingContainerModel_platform_barcode UNIQUE (platformType, identificationBarcode);

-- tissue_piece
CREATE TABLE TissuePieceType (
  tissuePieceTypeId bigint PRIMARY KEY AUTO_INCREMENT,
  abbreviation varchar(500) NOT NULL,
  name varchar(500) NOT NULL,
  archived boolean NOT NULL,
  CONSTRAINT uk_tissuePieceType_name UNIQUE (name)
) Engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE SampleLCMTube RENAME TO SampleTissuePiece;
ALTER TABLE SampleTissuePiece ADD COLUMN tissuePieceType bigint;

INSERT INTO TissuePieceType (abbreviation, name, archived) VALUES
  ('LCM' , 'LCM Tube', FALSE);

UPDATE SampleTissuePiece SET tissuePieceType = (SELECT tissuePieceTypeId FROM TissuePieceType WHERE name = 'LCM Tube');

UPDATE SampleClass SET sampleSubcategory = 'Tissue Piece' WHERE sampleSubcategory = 'LCM Tube';

ALTER TABLE SampleTissuePiece MODIFY tissuePieceType bigint NOT NULL;
ALTER TABLE SampleTissuePiece ADD CONSTRAINT tissuePieceType_tissuePieceTypeId FOREIGN KEY(tissuePieceType) REFERENCES TissuePieceType(tissuePieceTypeId);

DROP TRIGGER IF EXISTS SampleLCMTubeChange;

