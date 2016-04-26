UPDATE SequencerReference SET platformId = 26 WHERE platformId = 16;

UPDATE SequencingParameters SET platformId = 26 WHERE platformId IN (16, 25);
UPDATE SequencingParameters SET name = 'v3 2×300', paired = 1 WHERE name = '300×200';
