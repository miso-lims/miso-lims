INSERT INTO Platform (name, instrumentModel, description, numContainers) VALUES ('Illumina', 'cBot 2', 'Cluster generation system', 1);
INSERT INTO SequencerReference (name, ipAddress, platformId, available) VALUES 
  ('cBot 3', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE instrumentModel = 'cBot 2'), 1),
  ('cBot 4', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE instrumentModel = 'cBot 2'), 1),
  ('cBot 5', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE instrumentModel = 'cBot 2'), 1),
  ('cBot 6', UNHEX('7F000001'), (SELECT platformId FROM Platform WHERE instrumentModel = 'cBot 2'), 1);
