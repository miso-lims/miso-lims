INSERT INTO TagBarcodeFamily(name, platformType) VALUES
  ('454', 'LS454'),
  ('iDES 8bp', 'ILLUMINA');

INSERT INTO TagBarcodes(name, sequence, position, tagFamilyId) VALUES
  ('Index 01', 'ATCACG', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),
  ('Index 02', 'CGATGT', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),
  ('Index 03', 'TTAGGC', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),
  ('Index 04', 'TGACCA', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),
  ('Index 05', 'ACAGTG', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),
  ('Index 06', 'GCCAAT', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),
  ('Index 07', 'CAGATC', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),
  ('Index 08', 'ACTTGA', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),
  ('Index 09', 'GATCAG', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),
  ('Index 10', 'TAGCTT', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '454')),

  ('Index 01', 'ACGTCACA', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp')),
  ('Index 02', 'CTAAGTGG', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp')),
  ('Index 03', 'TGTAACCG', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp')),
  ('Index 04', 'TGACCATC', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp')),
  ('Index 05', 'AACTTGGC', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp')),
  ('Index 06', 'TCTCGGTT', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp')),
  ('Index 07', 'GTATGGAC', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp')),
  ('Index 08', 'TTCTGCCA', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp')),
  ('Index 09', 'CCAACGAA', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp')),
  ('Index 10', 'ACCACCTT', 1, (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'iDES 8bp'));
