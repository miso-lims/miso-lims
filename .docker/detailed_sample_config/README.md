To generate the detailed sample data, I restored a cleaned version of the OICR 
production database and did the following:

```
SET @admn = (SELECT userId FROM User WHERE loginName='admin');

UPDATE TissueOrigin SET createdBy=@admn, updatedBy=@admn;
UPDATE TissueType SET createdBy=@admn, updatedBy=@admn;
UPDATE TissueMaterial SET createdBy=@admn, updatedBy=@admn;
UPDATE SampleClass SET createdBy=@admn, updatedBy=@admn;
UPDATE SampleValidRelationship SET createdBy=@admn, updatedBy=@admn;
```

And then dumped the appropriate tables:

```
mysqldump -u USER --compact --no-create-info --complete-insert -p lims BoxUse TissueOrigin TissueType TissueMaterial SamplePurpose SampleClass SampleValidRelationship ReferenceGenome LibraryDesignCode LibraryDesign > V0803__detailed_sample_data.sql
```