ALTER TABLE `SampleCVSlide` CHANGE COLUMN `cuts` `slides` int NOT NULL DEFAULT 0;
ALTER TABLE `SampleLCMTube` CHANGE COLUMN `cutsConsumed` `slidesConsumed` int NOT NULL DEFAULT 0;
