INSERT INTO IndexFamily (name, platformType, archived)
VALUES ('Nextera PCR ATAC-seq', 'ILLUMINA', 0);

SELECT indexFamilyId INTO @famId FROM IndexFamily WHERE name = 'Nextera PCR ATAC-seq';
INSERT INTO Indices (name, sequence, position, indexFamilyId) VALUES
('Ad2-1', 'TAAGGCGA', 1, @famId),
('Ad2-2', 'CGTACTAG', 1, @famId),
('Ad2-3', 'AGGCAGAA', 1, @famId),
('Ad2-4', 'TCCTGAGC', 1, @famId),
('Ad2-5', 'GGACTCCT', 1, @famId),
('Ad2-6', 'TAGGCATG', 1, @famId),
('Ad2-7', 'CTCTCTAC', 1, @famId),
('Ad2-8', 'CAGAGAGG', 1, @famId),
('Ad2-9', 'GCTACGCT', 1, @famId),
('Ad2-10', 'CGAGGCTG', 1, @famId),
('Ad2-11', 'AAGAGGCA', 1, @famId),
('Ad2-12', 'GTAGAGGA', 1, @famId),
('Ad2-13', 'GTCGTGAT', 1, @famId),
('Ad2-14', 'ACCACTGT', 1, @famId),
('Ad2-15', 'TGGATCTG', 1, @famId),
('Ad2-16', 'CCGTTTGT', 1, @famId),
('Ad2-17', 'TGCTGGGT', 1, @famId),
('Ad2-18', 'GAGGGGTT', 1, @famId),
('Ad2-19', 'AGGTTGGG', 1, @famId),
('Ad2-20', 'GTGTGGTG', 1, @famId),
('Ad2-21', 'TGGGTTTC', 1, @famId),
('Ad2-22', 'TGGTCACA', 1, @famId),
('Ad2-23', 'TTGACCCT', 1, @famId),
('Ad2-24', 'CCACTCCT', 1, @famId);
