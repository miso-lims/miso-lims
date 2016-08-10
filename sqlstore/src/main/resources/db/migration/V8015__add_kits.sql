INSERT INTO KitDescriptor(name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier)
SELECT 'KAPA OnBead', 0, 'KAPA', 1, 0, 'Library', 'Illumina', 'n/a', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM KitDescriptor WHERE name = 'KAPA OnBead');

INSERT INTO KitDescriptor(name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier)
VALUES ('CRISPR Seq Prep', 0, 'CRISPR', 1, 0, 'Library', 'Illumina', 'n/a', 1),
('Nextera XT', 0, 'Nextera', 1, 0, 'Library', 'Illumina', 'n/a', 1);
