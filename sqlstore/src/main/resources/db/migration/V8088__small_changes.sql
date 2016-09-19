-- GLT-1133 Add PacBio RS II
INSERT INTO Platform (name, instrumentModel, description, numContainers) VALUES ('PacBio', 'PacBio RS II', '', 1), ('PacBio', 'Sequel', '', 1);

-- GLT-1142 Update Nextera XT index positions
UPDATE Indices SET position = 2 WHERE name LIKE 'S5%';
UPDATE IndexFamily SET name = 'Nextera XT Dual Index' WHERE name = 'Nextera';
UPDATE IndexFamily SET name = 'Nextera DNA Dual Index' WHERE name = 'Nextera Dual Index';
