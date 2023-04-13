ALTER TABLE LibraryPropagationRule RENAME TO LibraryDesign;
ALTER TABLE LibraryDesign CHANGE `libraryPropagationRuleId` `libraryDesignId` bigint AUTO_INCREMENT;
ALTER TABLE LibraryDesign CHANGE `platformName` `platformName` varchar(255) NOT NULL;
ALTER TABLE LibraryDesign CHANGE `paired` `paired` boolean NOT NULL;
ALTER TABLE LibraryDesign DROP FOREIGN KEY `FK_lpr_selectiontype`;
ALTER TABLE LibraryDesign CHANGE `librarySelectionType` `librarySelectionType` bigint NOT NULL;
ALTER TABLE LibraryDesign ADD CONSTRAINT `LibraryDesign_librarySelectionType_fkey` FOREIGN KEY (librarySelectionType) REFERENCES LibrarySelectionType (librarySelectionTypeId);
ALTER TABLE LibraryDesign DROP FOREIGN KEY `FK_lpr_strategytype`;
ALTER TABLE LibraryDesign CHANGE `libraryStrategyType` `libraryStrategyType` bigint NOT NULL;
ALTER TABLE LibraryDesign ADD CONSTRAINT `LibraryDesign_libraryStrategyType_fkey` FOREIGN KEY (libraryStrategyType) REFERENCES LibraryStrategyType (libraryStrategyTypeId);
ALTER TABLE LibraryDesign ADD COLUMN `suffix` VARCHAR(5) NOT NULL DEFAULT '';

ALTER TABLE LibraryAdditionalInfo ADD COLUMN libraryDesign bigint DEFAULT NULL;
ALTER TABLE LibraryAdditionalInfo ADD CONSTRAINT `LibraryAdditionalInfo_libraryDesign_fkey` FOREIGN KEY (libraryDesign) REFERENCES LibraryDesign (libraryDesignId);
