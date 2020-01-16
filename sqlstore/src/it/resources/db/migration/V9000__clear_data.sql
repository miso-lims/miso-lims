SET FOREIGN_KEY_CHECKS=0;

DELETE FROM Array;
DELETE FROM ArrayChangeLog;
DELETE FROM ArrayModel;
DELETE FROM ArrayPosition;
DELETE FROM ArrayRun;
DELETE FROM ArrayRunChangeLog;
DELETE FROM Attachment;
DELETE FROM AttachmentCategory;
DELETE FROM Box;
DELETE FROM BoxChangeLog;
DELETE FROM BoxPosition;
DELETE FROM BoxSize;
DELETE FROM BoxUse;
DELETE FROM Deletion;
DELETE FROM DetailedLibrary;
DELETE FROM DetailedLibraryAliquot;
DELETE FROM DetailedLibraryTemplate;
DELETE FROM DetailedQcStatus;
DELETE FROM DetailedSample;
DELETE FROM Experiment;
DELETE FROM _Group;
DELETE FROM Identity;
DELETE FROM IndexFamily;
DELETE FROM Indices;
DELETE FROM Institute;
DELETE FROM Instrument;
DELETE FROM InstrumentModel;
DELETE FROM InstrumentPosition;
DELETE FROM Kit;
DELETE FROM KitDescriptor;
DELETE FROM KitDescriptorChangeLog;
DELETE FROM Kit_Note;
DELETE FROM Lab;
DELETE FROM Library;
DELETE FROM LibraryAliquot;
DELETE FROM LibraryAliquotChangeLog;
DELETE FROM LibraryChangeLog;
DELETE FROM LibraryDesign;
DELETE FROM LibraryDesignCode;
DELETE FROM Library_Index;
DELETE FROM Library_Note;
DELETE FROM LibraryQC;
DELETE FROM LibrarySelectionType;
DELETE FROM LibrarySpikeIn;
DELETE FROM LibraryStrategyType;
DELETE FROM LibraryTemplate;
DELETE FROM LibraryTemplate_Index1;
DELETE FROM LibraryTemplate_Index2;
DELETE FROM LibraryTemplate_Project;
DELETE FROM LibraryType;
DELETE FROM Note;
DELETE FROM _Partition;
DELETE FROM PartitionQCType;
DELETE FROM Pool;
DELETE FROM PoolChangeLog;
DELETE FROM Pool_LibraryAliquot;
DELETE FROM Pool_Note;
DELETE FROM PoolOrder;
DELETE FROM PoolOrder_LibraryAliquot;
DELETE FROM PoolQC;
DELETE FROM PoolQcControl;
DELETE FROM Printer;
DELETE FROM Project;
DELETE FROM ProjectChangeLog;
DELETE FROM QcControl;
DELETE FROM QCType;
DELETE FROM ReferenceGenome;
DELETE FROM Run;
DELETE FROM RunChangeLog;
DELETE FROM RunIllumina;
DELETE FROM RunIonTorrent;
DELETE FROM RunLS454;
DELETE FROM RunOxfordNanopore;
DELETE FROM RunPacBio;
DELETE FROM RunPurpose;
DELETE FROM Run_Partition;
DELETE FROM Run_Partition_LibraryAliquot;
DELETE FROM Run_SequencerPartitionContainer;
DELETE FROM Sample;
DELETE FROM SampleAliquot;
DELETE FROM SampleAliquotSingleCell;
DELETE FROM SampleChangeLog;
DELETE FROM SampleClass;
DELETE FROM Sample_Note;
DELETE FROM SampleNumberPerProject;
DELETE FROM SamplePurpose;
DELETE FROM SampleQC;
DELETE FROM SampleSingleCell;
DELETE FROM SampleSlide;
DELETE FROM SampleStock;
DELETE FROM SampleStockSingleCell;
DELETE FROM SampleTissue;
DELETE FROM SampleTissuePiece;
DELETE FROM SampleTissueProcessing;
DELETE FROM SampleType;
DELETE FROM SampleValidRelationship;
DELETE FROM SequencerPartitionContainer;
DELETE FROM SequencerPartitionContainerChangeLog;
DELETE FROM SequencerPartitionContainer_Partition;
DELETE FROM SequencingContainerModel;
DELETE FROM SequencingContainerModel_InstrumentModel;
DELETE FROM SequencingOrder;
DELETE FROM SequencingParameters;
DELETE FROM ServiceRecord;
DELETE FROM Stain;
DELETE FROM StainCategory;
DELETE FROM StorageLocation;
DELETE FROM StorageLocationMap;
DELETE FROM Study;
DELETE FROM StudyChangeLog;
DELETE FROM Submission;
DELETE FROM Submission_Experiment;
DELETE FROM TargetedSequencing;
DELETE FROM TargetedSequencing_KitDescriptor;
DELETE FROM TissueMaterial;
DELETE FROM TissueOrigin;
DELETE FROM TissueType;
DELETE FROM Transfer;
DELETE FROM Transfer_Library;
DELETE FROM Transfer_LibraryAliquot;
DELETE FROM Transfer_Pool;
DELETE FROM Transfer_Sample;
DELETE FROM User;
DELETE FROM User_Group;
DELETE FROM Workset;
DELETE FROM Workset_Library;
DELETE FROM Workset_LibraryAliquot;
DELETE FROM Workset_Sample;

SET FOREIGN_KEY_CHECKS=1;
