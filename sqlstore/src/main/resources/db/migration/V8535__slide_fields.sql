ALTER TABLE SampleSlide ADD COLUMN percentTumour DECIMAL(11,8);
ALTER TABLE SampleSlide ADD COLUMN percentNecrosis DECIMAL(11,8);
ALTER TABLE SampleSlide ADD COLUMN markedAreaSize DECIMAL(11,8);
ALTER TABLE SampleSlide ADD COLUMN markedAreaPercentTumour DECIMAL(11,8);

ALTER TABLE SampleTissuePiece ADD COLUMN referenceSlideId bigint(20);
ALTER TABLE SampleTissuePiece ADD CONSTRAINT fk_sampleTissuePiece_referenceSlide FOREIGN KEY (referenceSlideId) REFERENCES SampleSlide(sampleId);
ALTER TABLE SampleStock ADD COLUMN referenceSlideId bigint(20);
ALTER TABLE SampleStock ADD CONSTRAINT fk_sampleStock_referenceSlide FOREIGN KEY (referenceSlideId) REFERENCES SampleSlide(sampleId);
