DELETE FROM KitDescriptor WHERE platformType IN ('LS454','Solid');
DELETE FROM TagBarcodes WHERE tagFamilyId IN (SELECT tagFamilyId FROM TagBarcodeFamily WHERE platformType = 'LS454');
DELETE FROM TagBarcodeFamily WHERE platformType = 'LS454';
