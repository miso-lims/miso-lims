SELECT link.assayId
  ,t.alias AS name
  ,tor.alias AS tissueOrigin
  ,t.negateTissueOrigin
  ,tt.alias AS tissueType
  ,t.negateTissueType
  ,sc.alias AS extractionSampleType
  ,ldc.code AS librarySourceTemplateType
  ,t.libraryQualificationMethod
  ,qldc.code AS libraryQualificationSourceTemplateType
  ,t.repeatPerTimepoint
  ,t.permittedSamples
FROM Assay_AssayTest link
JOIN AssayTest t ON t.testId = link.testId
LEFT JOIN TissueOrigin tor ON tor.tissueOriginId = t.tissueOriginId
LEFT JOIN TissueType tt ON tt.tissueTypeId = t.tissueTypeId
LEFT JOIN SampleClass sc ON sc.sampleClassId = t.extractionClassId
LEFT JOIN LibraryDesignCode ldc ON ldc.libraryDesignCodeId = t.libraryDesignCodeId
LEFT JOIN LibraryDesignCode qldc ON qldc.libraryDesignCodeId = t.libraryQualificationDesignCodeId