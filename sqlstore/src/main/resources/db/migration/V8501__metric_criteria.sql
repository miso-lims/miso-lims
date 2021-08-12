CREATE TABLE MetricSubcategory(
  subcategoryId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  category varchar(50) NOT NULL,
  libraryDesignCodeId bigint(20),
  sortPriority TINYINT UNSIGNED,
  PRIMARY KEY (subcategoryId),
  CONSTRAINT uk_metricSubcategory_alias_category UNIQUE (alias, category),
  CONSTRAINT fk_metricSubcategory_libraryDesignCode FOREIGN KEY (libraryDesignCodeId) REFERENCES LibraryDesignCode (libraryDesignCodeId) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Metric
  ADD COLUMN subcategoryId bigint(20),
  ADD CONSTRAINT fk_metric_subcategory FOREIGN KEY (subcategoryID) REFERENCES MetricSubcategory (subcategoryId),
  DROP INDEX uk_metric_alias_category,
  ADD COLUMN sortPriority TINYINT UNSIGNED,
  ADD COLUMN nucleicAcidType varchar(10),
  ADD COLUMN tissueMaterialId bigint(20),
  ADD COLUMN tissueTypeId bigint(20),
  ADD COLUMN negateTissueType BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN tissueOriginId bigint(20),
  ADD COLUMN containerModelId bigint(20),
  ADD COLUMN readLength int(11),
  ADD COLUMN readLength2 int(11),
  ADD CONSTRAINT fk_assayMetric_tissueMaterial FOREIGN KEY (tissueMaterialId) REFERENCES TissueMaterial (tissueMaterialId),
  ADD CONSTRAINT fk_assayMetric_tissueType FOREIGN KEY (tissueTypeId) REFERENCES TissueType (tissueTypeId),
  ADD CONSTRAINT fk_assayMetric_tissueOrigin FOREIGN KEY (tissueOriginId) REFERENCES TissueOrigin (tissueOriginId),
  ADD CONSTRAINT fk_assayMetric_containerModel FOREIGN KEY (containerModelId) REFERENCES SequencingContainerModel (sequencingContainerModelId);

UPDATE Metric
SET category = 'LIBRARY_QUALIFICATION'
WHERE category = 'LOW_PASS_SEQUENCING';
