ALTER TABLE Library ADD COLUMN `kitDescriptorId` bigint(20);
ALTER TABLE Library ADD CONSTRAINT library_kitDescriptor_fkey FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);
UPDATE Library SET kitDescriptorId = (SELECT kitDescriptorId FROM DetailedLibrary WHERE DetailedLibrary.libraryId = Library.libraryId);
ALTER TABLE DetailedLibrary DROP FOREIGN KEY libraryAdditionalInfo_kitDescriptor_fkey;
ALTER TABLE DetailedLibrary DROP COLUMN kitDescriptorId;

UPDATE Library SET kitDescriptorId = (SELECT DISTINCT KitDescriptor.kitDescriptorId
  FROM KitDescriptor
   JOIN Kit ON KitDescriptor.kitDescriptorId = Kit.kitDescriptorId
   JOIN Experiment_Kit ON Experiment_Kit.kits_kitId = Kit.kitId
   JOIN Experiment ON Experiment.experimentId = Experiment_Kit.experiments_experimentId
   JOIN Pool ON Pool.poolId = Experiment.pool_poolId
   JOIN Pool_Dilution ON Pool_Dilution.pool_poolId = Pool.poolId
   JOIN LibraryDilution ON LibraryDilution.dilutionId = Pool_Dilution.dilution_dilutionId
  WHERE KitDescriptor.kitType = 'LIBRARY' AND LibraryDilution.library_libraryId = Library.libraryId)
 WHERE kitDescriptorId IS NULL;
