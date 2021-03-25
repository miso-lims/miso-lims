-- kitlot_expand
ALTER TABLE Library MODIFY COLUMN kitLot varchar(255);

-- transfer_volume
ALTER TABLE Transfer_Sample
  ADD COLUMN distributedVolume DECIMAL(16,10),
  ADD COLUMN distributedBoxAlias varchar(255),
  ADD COLUMN distributedBoxPosition varchar(3);

ALTER TABLE Transfer_Library
  ADD COLUMN distributedVolume DECIMAL(16,10),
  ADD COLUMN distributedBoxAlias varchar(255),
  ADD COLUMN distributedBoxPosition varchar(3);

ALTER TABLE Transfer_LibraryAliquot
  ADD COLUMN distributedVolume DECIMAL(16,10),
  ADD COLUMN distributedBoxAlias varchar(255),
  ADD COLUMN distributedBoxPosition varchar(3);

ALTER TABLE Transfer_Pool
  ADD COLUMN distributedVolume DECIMAL(16,10),
  ADD COLUMN distributedBoxAlias varchar(255),
  ADD COLUMN distributedBoxPosition varchar(3);

