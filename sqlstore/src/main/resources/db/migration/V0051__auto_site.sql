-- ion_torrent_library
--StartNoTest
INSERT INTO LibraryType (description, platformType) VALUES ('Single End', 'IonTorrent');
--EndNoTest

-- add_kit
--StartNoTest
INSERT INTO KitDescriptor(name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier)
VALUES ('GA_multiplex', 0, 'GA', 1, 0, 'Library', 'Illumina', 'n/a', 1);
--EndNoTest

-- mid_indices
--StartNoTest
INSERT INTO TagBarcodeFamily (name, platformType, archived)
VALUES ('MID', 'IONTORRENT', 1);

SET @familyId = LAST_INSERT_ID();
INSERT INTO TagBarcodes (name, sequence, tagFamilyId)
VALUES ('MID_01', 'ACGAGTGCGT', @familyId),
('MID_02', 'ACGCTCGACA', @familyId),
('MID_03', 'AGACGCACTC', @familyId),
('MID_04', 'AGCACTGTAG', @familyId),
('MID_05', 'ATCAGACACG', @familyId),
('MID_06', 'ATATCGCGAG', @familyId),
('MID_07', 'CGTGTCTCTA', @familyId),
('MID_08', 'CTCGCGTGTC', @familyId),
('MID_10', 'TCTCTATGCG', @familyId),
('MID_11', 'TGATACGTCT', @familyId),
('MID_13', 'CATAGTAGTG', @familyId),
('MID_14', 'CGAGAGATAC', @familyId),
('MID_15', 'ATACGACGTA', @familyId),
('MID_16', 'TCACGTACTA', @familyId),
('MID_17', 'CGTCTAGTAC', @familyId),
('MID_18', 'TCTACGTAGC', @familyId),
('MID_19', 'TGTACTACTC', @familyId),
('MID_20', 'ACGACTACAG', @familyId),
('MID_21', 'CGTAGACTAG', @familyId),
('MID_22', 'TACGAGTATG', @familyId),
('MID_23', 'TACTCTCGTG', @familyId),
('MID_24', 'TAGAGACGAG', @familyId),
('MID_25', 'TCGTCGCTCG', @familyId),
('MID_26', 'ACATACGCGT', @familyId),
('MID_27', 'ACGCGAGTAT', @familyId),
('MID_28', 'ACTACTATGT', @familyId),
('MID_29', 'ACTGTACAGT', @familyId),
('MID_30', 'AGACTATACT', @familyId),
('MID_31', 'AGCGTCGTCT', @familyId),
('MID_32', 'AGTACGCTAT', @familyId),
('MID_33', 'ATAGAGTACT', @familyId),
('MID_34', 'CACGCTACGT', @familyId),
('MID_35', 'CAGTAGACGT', @familyId),
('MID_36', 'CGACGTGACT', @familyId),
('MID_37', 'TACACACACT', @familyId),
('MID_38', 'TACACGTGAT', @familyId),
('MID_39', 'TACAGATCGT', @familyId),
('MID_40', 'TACGCTGTCT', @familyId),
('MID_41', 'TAGTGTAGAT', @familyId),
('MID_42', 'TCGATCACGT', @familyId),
('MID_43', 'TCGCACTAGT', @familyId),
('MID_44', 'TCTAGCGACT', @familyId),
('MID_45', 'TCTATACTAT', @familyId),
('MID_46', 'TGACGTATGT', @familyId);
--EndNoTest

-- yet_more_SVRs
--StartNoTest
INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated) VALUES 
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Primary Tumor Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Primary Tumor Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Metastatic Tumor Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Metastatic Tumor Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Reference Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Reference Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Xenograft Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Xenograft Tissue'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Cell Line'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'gDNA_wga (stock)'),1,NOW(),1,NOW()),
  ((SELECT sampleClassId FROM SampleClass WHERE alias = 'Cell Line'),(SELECT sampleClassId FROM SampleClass WHERE alias = 'cDNA (stock)'),1,NOW(),1,NOW());
--EndNoTest

