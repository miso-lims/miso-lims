ALTER TABLE LibraryPropagationRule RENAME TO LibraryDesign;
ALTER TABLE LibraryDesign CHANGE `libraryPropagationRuleId` `libraryDesignId` bigint(20) AUTO_INCREMENT;
ALTER TABLE LibraryDesign CHANGE `platformName` `platformName` varchar(255) NOT NULL;
ALTER TABLE LibraryDesign CHANGE `paired` `paired` boolean NOT NULL;
ALTER TABLE LibraryDesign CHANGE `librarySelectionType` `librarySelectionType` bigint(20) NOT NULL;
ALTER TABLE LibraryDesign CHANGE `libraryStrategyType` `libraryStrategyType` bigint(20) NOT NULL;
ALTER TABLE LibraryDesign ADD COLUMN `suffix` text NOT NULL DEFAULT '';

ALTER TABLE LibraryAdditionalInfo ADD COLUMN libraryDesign bigint(20) DEFAULT NULL;
ALTER TABLE LibraryAdditionalInfo ADD CONSTRAINT `LibraryAdditionalInfo_libraryDesign_fkey` FOREIGN KEY (libraryDesign) REFERENCES LibraryDesign (libraryDesignId);
