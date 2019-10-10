CREATE TABLE TissuePieceType (
  tissuePieceTypeId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  abbreviation varchar(500) NOT NULL,
  name varchar(500) NOT NULL,
  archived boolean NOT NULL,
  CONSTRAINT uk_tissuePieceType_name UNIQUE (name)
) Engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE SampleLCMTube RENAME TO SampleTissuePiece;
ALTER TABLE SampleTissuePiece ADD COLUMN tissuePieceType bigint(20);

INSERT INTO TissuePieceType (abbreviation, name, archived) VALUES
  ('LCM' , 'LCM Tube', FALSE);

UPDATE SampleTissuePiece SET tissuePieceType = (SELECT tissuePieceTypeId FROM TissuePieceType WHERE name = 'LCM Tube');

UPDATE SampleClass SET sampleSubcategory = 'Tissue Piece' WHERE sampleSubcategory = 'LCM Tube';

ALTER TABLE SampleTissuePiece MODIFY tissuePieceType bigint(20) NOT NULL;
ALTER TABLE SampleTissuePiece ADD CONSTRAINT tissuePieceType_tissuePieceTypeId FOREIGN KEY(tissuePieceType) REFERENCES TissuePieceType(tissuePieceTypeId);

DROP TRIGGER IF EXISTS SampleLCMTubeChange;
