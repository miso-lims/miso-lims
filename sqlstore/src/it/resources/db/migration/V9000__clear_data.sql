SET FOREIGN_KEY_CHECKS=0;

DELETE FROM ApiKey;
DELETE FROM Array;
DELETE FROM ArrayChangeLog;
DELETE FROM ArrayModel;
DELETE FROM ArrayPosition;
DELETE FROM ArrayRun;
DELETE FROM ArrayRunChangeLog;
DELETE FROM Assay;
DELETE FROM Assay_AssayTest;
DELETE FROM Assay_Metric;
DELETE FROM AssayTest;
DELETE FROM Attachment;
DELETE FROM AttachmentCategory;
DELETE FROM Box;
DELETE FROM BoxChangeLog;
DELETE FROM BoxPosition;
DELETE FROM BoxSize;
DELETE FROM BoxUse;
DELETE FROM Deletion;
DELETE FROM DetailedLibraryTemplate;
DELETE FROM DetailedQcStatus;
DELETE FROM Experiment;
DELETE FROM _Group;
DELETE FROM IndexFamily;
DELETE FROM Indices;
DELETE FROM Instrument;
DELETE FROM InstrumentModel;
DELETE FROM InstrumentPosition;
DELETE FROM Instrument_ServiceRecord;
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
DELETE FROM Metric;
DELETE FROM MetricSubcategory;
DELETE FROM Note;
DELETE FROM _Partition;
DELETE FROM PartitionQCType;
DELETE FROM Pipeline;
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
DELETE FROM QCType_KitDescriptor;
DELETE FROM ReferenceGenome;
DELETE FROM Requisition;
DELETE FROM RequisitionChangeLog;
DELETE FROM RequisitionQc;
DELETE FROM RequisitionQcControl;
DELETE FROM Requisition_Assay;
DELETE FROM Requisition_SupplementalSample;
DELETE FROM Requisition_SupplementalLibrary;
DELETE FROM Run;
DELETE FROM RunChangeLog;
DELETE FROM RunIllumina;
DELETE FROM RunIonTorrent;
DELETE FROM RunLibraryQcStatus;
DELETE FROM RunLS454;
DELETE FROM RunOxfordNanopore;
DELETE FROM RunPacBio;
DELETE FROM RunPurpose;
DELETE FROM Run_Partition;
DELETE FROM Run_Partition_LibraryAliquot;
DELETE FROM Run_SequencerPartitionContainer;
DELETE FROM Sample;
DELETE FROM SampleChangeLog;
DELETE FROM SampleClass;
DELETE FROM Sample_Note;
DELETE FROM SampleNumberPerProject;
DELETE FROM SamplePurpose;
DELETE FROM SampleQC;
DELETE FROM SampleType;
DELETE FROM SampleValidRelationship;
DELETE FROM ScientificName;
DELETE FROM SequencerPartitionContainer;
DELETE FROM SequencerPartitionContainerChangeLog;
DELETE FROM SequencingContainerModel;
DELETE FROM SequencingContainerModel_InstrumentModel;
DELETE FROM SequencingControlType;
DELETE FROM SequencingOrder;
DELETE FROM SequencingParameters;
DELETE FROM ServiceRecord;
DELETE FROM Sop;
DELETE FROM Stain;
DELETE FROM StainCategory;
DELETE FROM StorageLabel;
DELETE FROM StorageLocation;
DELETE FROM StorageLocation_ServiceRecord;
DELETE FROM StorageLocationMap;
DELETE FROM Study;
DELETE FROM StudyChangeLog;
DELETE FROM StudyType;
DELETE FROM Submission;
DELETE FROM Submission_Experiment;
DELETE FROM TargetedSequencing;
DELETE FROM TargetedSequencing_KitDescriptor;
DELETE FROM TissueMaterial;
DELETE FROM TissueOrigin;
DELETE FROM TissuePieceType;
DELETE FROM TissueType;
DELETE FROM Transfer;
DELETE FROM TransferChangeLog;
DELETE FROM Transfer_Library;
DELETE FROM Transfer_LibraryAliquot;
DELETE FROM Transfer_Pool;
DELETE FROM Transfer_Sample;
DELETE FROM User;
DELETE FROM User_Group;
DELETE FROM Workset;
DELETE FROM WorksetCategory;
DELETE FROM WorksetChangeLog;
DELETE FROM Workset_Library;
DELETE FROM Workset_LibraryAliquot;
DELETE FROM Workset_Sample;
DELETE FROM WorksetStage;
DELETE FROM Workstation;

SET FOREIGN_KEY_CHECKS=1;
