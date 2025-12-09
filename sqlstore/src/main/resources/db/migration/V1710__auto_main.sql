-- increase_concentration_digits
-- Update concentration related columns in Sample Table
ALTER TABLE Sample MODIFY COLUMN concentration DECIMAL(16,10);
ALTER TABLE Sample MODIFY COLUMN initialCellConcentration DECIMAL(16,10);
ALTER TABLE Sample MODIFY COLUMN loadingCellConcentration DECIMAL(16,10);
ALTER TABLE Sample MODIFY COLUMN ngUsed DECIMAL(16,10);

-- Update concentration related columns in Library Table
ALTER TABLE Library MODIFY COLUMN concentration DECIMAL(16,10);
ALTER TABLE Library MODIFY COLUMN ngUsed DECIMAL(16,10);

-- Update concentration related columns in LibraryAliquot Table
ALTER TABLE LibraryAliquot MODIFY COLUMN concentration DECIMAL(16,10);
ALTER TABLE LibraryAliquot MODIFY COLUMN ngUsed DECIMAL(16,10);

-- Update concentration related columns in Pool Table
ALTER TABLE Pool MODIFY COLUMN concentration DECIMAL(16,10);

