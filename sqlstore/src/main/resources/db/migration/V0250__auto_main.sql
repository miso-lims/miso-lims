-- boxable_view

UPDATE BoxPosition SET targetType = 'SAMPLE' WHERE targetType LIKE 'Sample%';
UPDATE BoxPosition SET targetType = 'LIBRARY' WHERE targetType LIKE 'Library%';
UPDATE BoxPosition SET targetType = 'DILUTION' WHERE targetType = 'Dilution';
UPDATE BoxPosition SET targetType = 'POOL' WHERE targetType = 'Pool';
