-- Useful MySql procedures for testing

DELIMITER //

DROP PROCEDURE IF EXISTS showCounts//
CREATE PROCEDURE showCounts ()
BEGIN
    
    SELECT 'Projects' AS 'Entity',COUNT(*) AS 'Rows' FROM Project 
    UNION SELECT 'Samples',COUNT(*) FROM Sample 
    UNION SELECT 'SampleAdditionalInfo',COUNT(*) FROM SampleAdditionalInfo 
    UNION SELECT 'Identities',COUNT(*) FROM Identity 
    UNION SELECT 'SampleTissues',COUNT(*) FROM SampleTissue 
    UNION SELECT 'Analyte Stocks',COUNT(*) FROM SampleAdditionalInfo sai JOIN SampleAnalyte sa on sa.sampleId = sai.sampleId JOIN SampleClass sc ON sai.sampleClassId = sc.sampleClassId WHERE sc.isStock = 1
    UNION SELECT 'Analyte Aliquots',COUNT(*) FROM SampleAdditionalInfo sai JOIN SampleAnalyte sa on sa.sampleId = sai.sampleId JOIN SampleClass sc ON sai.sampleClassId = sc.sampleClassId WHERE sc.isStock = 0
    UNION SELECT 'Libraries',COUNT(*) FROM Library
    UNION SELECT 'LibraryAdditionalInfo',COUNT(*) FROM LibraryAdditionalInfo
    UNION SELECT 'LibraryDilutions',COUNT(*) FROM LibraryDilution
    UNION SELECT 'Pool_Elements',COUNT(*) FROM Pool_Elements
    UNION SELECT 'Pools',COUNT(*) FROM Pool
    UNION SELECT 'Partitions',COUNT(*) FROM _Partition
    UNION SELECT 'SequencerPartitionContainer_Partitions',COUNT(*) FROM SequencerPartitionContainer_Partition
    UNION SELECT 'SequencerPartitionContainers',COUNT(*) FROM SequencerPartitionContainer
    UNION SELECT 'Run_SequencerPartitionContainers',COUNT(*) FROM Run_SequencerPartitionContainer
    UNION SELECT 'Runs',COUNT(*) FROM Run
    UNION SELECT 'Status',COUNT(*) FROM Status;
    
END//

DROP PROCEDURE IF EXISTS clearData//
CREATE PROCEDURE clearData ()
BEGIN
    
    DELETE FROM Run_SequencerPartitionContainer;
    DELETE FROM SequencerPartitionContainer_Partition;
    DELETE FROM SequencerPartitionContainer;
    DELETE FROM _Partition;
    DELETE FROM Status;
    DELETE FROM Run;
    DELETE FROM Pool;
    DELETE FROM Pool_Elements;
    DELETE FROM LibraryDilution;
    DELETE FROM LibraryAdditionalInfo;
    DELETE FROM Library;
    DELETE FROM Identity;
    DELETE FROM SampleTissue;
    DELETE FROM SampleAnalyte;
    DELETE FROM SampleAdditionalInfo;
    DELETE FROM Sample;
    DELETE FROM SampleNumberPerProject;
    DELETE FROM Project;
    
END//

DELIMITER ;
