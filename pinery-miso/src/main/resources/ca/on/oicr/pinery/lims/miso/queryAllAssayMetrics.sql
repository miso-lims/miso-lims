SElECT am.assayId
  ,m.alias AS name
  ,m.category
  ,sub.alias AS subcategoryName
  ,sub.sortPriority AS subcategorySortPriority
  ,ldc.code AS subcategoryLibraryDesignCode
  ,m.units
  ,m.thresholdType
  ,am.minimumThreshold
  ,am.maximumThreshold
  ,m.sortPriority
  ,m.nucleicAcidType
  ,tm.alias AS tissueMaterial
  ,tt.alias AS tissueType
  ,m.negateTissueType
  ,tor.alias AS tissueOrigin
  ,cm.alias AS containerModel
  ,m.readLength
  ,m.readLength2
FROM Metric m
JOIN Assay_Metric am ON am.metricId = m.metricId
LEFT JOIN MetricSubcategory sub ON sub.subcategoryId = m.subcategoryId
LEFT JOIN LibraryDesignCode ldc ON ldc.libraryDesignCodeId = sub.libraryDesignCodeId
LEFT JOIN TissueMaterial tm ON tm.tissueMaterialId = m.tissueMaterialId
LEFT JOIN TissueType tt ON tt.tissueTypeId = m.tissueTypeId
LEFT JOIN TissueOrigin tor ON tor.tissueOriginId = m.tissueOriginId
LEFT JOIN SequencingContainerModel cm ON cm.sequencingContainerModelId = m.containerModelId
