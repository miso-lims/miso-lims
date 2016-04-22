ALTER TABLE LibraryAdditionalInfo ADD COLUMN libraryDesign bigint(20) DEFAULT NULL;
ALTER TABLE LibraryAdditionalInfo ADD FOREIGN KEY (libraryDesign) REFERENCES LibraryPropagationRule (libraryPropagationRuleId);
