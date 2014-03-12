USE lims;

UPDATE `lims`.`TagBarcodes` SET strategyName="Bioo NEXTflex V1 Directional RNA-Seq Indices" WHERE strategyName="Bioo NEXTflex Directional RNA-Seq Indices";

INSERT INTO `lims`.`TagBarcodes` (name, sequence, platformName, strategyName) VALUES("Index 1", "CGATGT", "Illumina", "Bioo NEXTflex V2 Directional RNA-Seq Indices");
INSERT INTO `lims`.`TagBarcodes` (name, sequence, platformName, strategyName) VALUES("Index 2", "TGACCA", "Illumina", "Bioo NEXTflex V2 Directional RNA-Seq Indices");
INSERT INTO `lims`.`TagBarcodes` (name, sequence, platformName, strategyName) VALUES("Index 3", "ACAGTG", "Illumina", "Bioo NEXTflex V2 Directional RNA-Seq Indices");
INSERT INTO `lims`.`TagBarcodes` (name, sequence, platformName, strategyName) VALUES("Index 4", "GCCAAT", "Illumina", "Bioo NEXTflex V2 Directional RNA-Seq Indices");
INSERT INTO `lims`.`TagBarcodes` (name, sequence, platformName, strategyName) VALUES("Index 5", "CAGATC", "Illumina", "Bioo NEXTflex V2 Directional RNA-Seq Indices");
INSERT INTO `lims`.`TagBarcodes` (name, sequence, platformName, strategyName) VALUES("Index 6", "CTTGTA", "Illumina", "Bioo NEXTflex V2 Directional RNA-Seq Indices");
