ALTER TABLE LibraryAliquot
  ADD COLUMN kitDescriptorId bigint(20),
  ADD COLUMN kitLot varchar(255),
  ADD CONSTRAINT fk_libraryAliquot_kitDescriptor FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);

UPDATE LibraryAliquot la
JOIN Library l ON l.libraryId = la.libraryId
SET la.kitDescriptorId = l.kitDescriptorId, la.kitLot = l.kitLot
WHERE la.targetedSequencingId IS NOT NULL;
