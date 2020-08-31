ALTER TABLE SampleClass ADD COLUMN defaultSampleTypeId bigint(20);
ALTER TABLE SampleClass ADD CONSTRAINT fk_sampleClass_defaultSampleType
  FOREIGN KEY (defaultSampleTypeId) REFERENCES SampleType (typeId);
