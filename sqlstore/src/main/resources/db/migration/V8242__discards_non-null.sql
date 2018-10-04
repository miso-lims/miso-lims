UPDATE SampleSlide SET discards = 0 WHERE discards IS NULL;
ALTER TABLE SampleSlide CHANGE COLUMN discards discards INT(11) NOT NULL DEFAULT 0;