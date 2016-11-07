--StartNoTest
INSERT INTO LibraryDesignCode (code, description) VALUES ("TR", "TR");

DELETE FROM LibraryDesign WHERE sampleClassId = (SELECT sampleClassId FROM SampleClass WHERE alias = "cDNA (aliquot)") AND name = "SM";

INSERT INTO LibraryDesign (name, librarySelectionType, libraryStrategyType, libraryDesignCodeId, sampleClassId) VALUES
  ("WG", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "PCR"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "WGS"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "WG"), (SELECT sampleClassId FROM SampleClass WHERE alias = "gDNA_wga (aliquot)")),
  ("TS (Hybrid Selection)", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "Hybrid Selection"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "AMPLICON"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "TS"), (SELECT sampleClassId FROM SampleClass WHERE alias = "gDNA_wga (aliquot)")),
  ("TS (PCR)", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "PCR"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "AMPLICON"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "TS"), (SELECT sampleClassId FROM SampleClass WHERE alias = "gDNA_wga (aliquot)")),
  ("EX", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "Hybrid Selection"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "WXS"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "EX"), (SELECT sampleClassId FROM SampleClass WHERE alias = "gDNA_wga (aliquot)")),
  ("MR", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "cDNA"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "RNA-Seq"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "MR"), (SELECT sampleClassId FROM SampleClass WHERE alias = "mRNA")),
  ("SM", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "size fractionation"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "RNA-Seq"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "SM"), (SELECT sampleClassId FROM SampleClass WHERE alias = "smRNA")),
  ("WT", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "cDNA"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "RNA-Seq"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "WT"), (SELECT sampleClassId FROM SampleClass WHERE alias = "rRNA_depleted")),
  ("TR", (SELECT librarySelectionTypeId FROM LibrarySelectionType WHERE name = "cDNA"), (SELECT libraryStrategyTypeId FROM LibraryStrategyType WHERE name = "RNA-Seq"), (SELECT libraryDesignCodeId FROM LibraryDesignCode WHERE code = "TR"), (SELECT sampleClassId FROM SampleClass WHERE alias = "whole RNA (aliquot)"));
--EndNoTest
