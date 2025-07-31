-- update_libindex_field_size
ALTER TABLE LibraryIndex
  MODIFY COLUMN name VARCHAR(50) NOT NULL;

-- update_sop_url_field_size
-- update_url_field
ALTER TABLE Sop
  MODIFY COLUMN url VARCHAR(500) NOT NULL;

