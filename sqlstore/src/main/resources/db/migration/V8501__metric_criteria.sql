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
  ADD CONSTRAINT uk_metric_alias_category_subcategory UNIQUE (alias, category, subcategoryId),
  ADD COLUMN sortPriority TINYINT UNSIGNED;

UPDATE Metric
SET category = 'LIBRARY_QUALIFICATION'
WHERE category = 'LOW_PASS_SEQUENCING';
