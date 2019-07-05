ALTER TABLE LibraryAliquot ADD COLUMN parentAliquotId bigint(20);
ALTER TABLE LibraryAliquot ADD CONSTRAINT fk_libraryAliquot_parentAliquot FOREIGN KEY (parentAliquotId) REFERENCES LibraryAliquot (aliquotId);
