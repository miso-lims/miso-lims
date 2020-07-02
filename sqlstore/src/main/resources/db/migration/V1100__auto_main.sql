-- box_types
ALTER TABLE BoxSize ADD COLUMN boxType varchar(20) NOT NULL DEFAULT 'STORAGE';

-- droplistTransferView
-- Disable "x does not exist" warnings
SET sql_notes = 0;
DROP VIEW IF EXISTS ListTransferView;
SET sql_notes = 1;

