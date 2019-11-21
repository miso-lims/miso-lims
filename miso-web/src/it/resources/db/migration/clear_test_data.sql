-- fixes BeforeInsertPool trigger created in V0004
DROP TRIGGER IF EXISTS BeforeInsertPool;

SET FOREIGN_KEY_CHECKS=0;
DELETE FROM Transfer_Sample;
DELETE FROM Transfer_Library;
DELETE FROM Transfer_LibraryAliquot;
DELETE FROM Transfer_Pool;
DELETE FROM Transfer;
DELETE FROM Workset_Sample;
DELETE FROM Workset_Library;
DELETE FROM Workset_LibraryAliquot;
DELETE FROM Workset;
DELETE FROM BoxPosition;
DELETE FROM BoxChangeLog;
DELETE FROM Box;
DELETE FROM SequencerPartitionContainer_Partition;
DELETE FROM Run_Partition_QC;
DELETE FROM _Partition;
DELETE FROM Run_SequencerPartitionContainer;
DELETE FROM SequencerPartitionContainerChangeLog;
DELETE FROM SequencerPartitionContainer;
DELETE FROM SequencingContainerModel_InstrumentModel;
DELETE FROM SequencingContainerModel;
DELETE FROM RunIllumina;
DELETE FROM RunIonTorrent;
DELETE FROM RunLS454;
DELETE FROM RunPacBio;
DELETE FROM RunOxfordNanopore;
DELETE FROM RunChangeLog;
DELETE FROM Run;
DELETE FROM PoolQC;
DELETE FROM PoolOrder_LibraryAliquot;
DELETE FROM PoolOrder;
DELETE FROM SequencingOrder;
DELETE FROM Pool_LibraryAliquot;
DELETE FROM Pool_Note;
DELETE FROM PoolChangeLog;
DELETE FROM Pool;
DELETE FROM ArrayRunChangeLog;
DELETE FROM ArrayRun;
DELETE FROM ArrayPosition;
DELETE FROM ArrayChangeLog;
DELETE FROM Array;
DELETE FROM ArrayModel;
DELETE FROM LibraryQC;
DELETE FROM LibraryAliquotChangeLog;
DELETE FROM DetailedLibraryAliquot;
DELETE FROM LibraryAliquot;
DELETE FROM DetailedLibrary;
DELETE FROM Library_Index;
DELETE FROM Library_Note;
DELETE FROM LibraryChangeLog;
DELETE FROM Library;
DELETE FROM LibraryDesign;
DELETE FROM LibraryDesignCode;
DELETE FROM LibrarySpikeIn;
DELETE FROM SampleQC;
DELETE FROM SampleAliquotSingleCell;
DELETE FROM SampleAliquot;
DELETE FROM SampleStockSingleCell;
DELETE FROM SampleStock;
DELETE FROM SampleSlide;
DELETE FROM SampleTissuePiece;
DELETE FROM SampleSingleCell;
DELETE FROM SampleTissueProcessing;
DELETE FROM SampleTissue;
DELETE FROM Identity;
DELETE FROM DetailedSample;
DELETE FROM Sample_Note;
DELETE FROM SampleChangeLog;
DELETE FROM Sample;
DELETE FROM SampleValidRelationship;
DELETE FROM SampleClass;
DELETE FROM TissueMaterial;
DELETE FROM TissueOrigin;
DELETE FROM TissueType;
DELETE FROM Lab;
DELETE FROM Institute;
DELETE FROM Stain;
DELETE FROM SamplePurpose;
DELETE FROM DetailedQcStatus;
DELETE FROM QCType;
DELETE FROM LibraryType;
DELETE FROM LibrarySelectionType;
DELETE FROM LibraryStrategyType;
DELETE FROM TargetedSequencing_KitDescriptor;
DELETE FROM TargetedSequencing;
DELETE FROM KitDescriptorChangeLog;
DELETE FROM KitDescriptor;
DELETE FROM BoxUse;
DELETE FROM BoxSize;
DELETE FROM Indices;
DELETE FROM IndexFamily;
DELETE FROM SequencingParameters;
DELETE FROM ServiceRecord;
DELETE FROM Instrument WHERE upgradedInstrumentId IS NOT NULL;
DELETE FROM Instrument;
DELETE FROM InstrumentPosition;
DELETE FROM Experiment;
DELETE FROM InstrumentModel;
DELETE FROM StudyChangeLog;
DELETE FROM Study;
DELETE FROM SampleNumberPerProject;
DELETE FROM ProjectChangeLog;
DELETE FROM Project;
DELETE FROM ReferenceGenome;
DELETE FROM Note;
DELETE FROM Deletion;
DELETE FROM User_Group;
DELETE FROM User;
DELETE FROM _Group;
DELETE FROM StorageLocation;
DELETE FROM StorageLocationMap;
DELETE FROM OrderPurpose;
SET FOREIGN_KEY_CHECKS=1;
