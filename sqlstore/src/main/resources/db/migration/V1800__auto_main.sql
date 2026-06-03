ALTER TABLE RunUltima ADD COLUMN completedFlows smallint DEFAULT NULL;
ALTER TABLE RunUltima ADD COLUMN expectedFlows smallint DEFAULT NULL;
ALTER TABLE RunUltima ADD COLUMN waferShelf tinyint DEFAULT NULL;