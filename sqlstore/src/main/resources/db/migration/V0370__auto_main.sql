-- add_default_box_use_and_size

-- add default BoxUse and BoxSize so users of the Docker version of MISO can make new boxes
INSERT INTO BoxUse (alias) 
SELECT 'Libraries' FROM DUAL
WHERE NOT EXISTS (SELECT * from BoxUse)
LIMIT 1;

INSERT INTO BoxSize (`rows`, `columns`, scannable)
SELECT '8', '12', '1' FROM DUAL
WHERE NOT EXISTS (SELECT * FROM BoxSize)
LIMIT 1;


