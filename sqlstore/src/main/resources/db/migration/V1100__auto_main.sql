-- box_types
ALTER TABLE BoxSize ADD COLUMN boxType varchar(20) NOT NULL DEFAULT 'STORAGE';

-- droplistTransferView
DROP VIEW IF EXISTS ListTransferView;
