INSERT INTO BoxSize (rows, columns, scannable) VALUES (9, 9, false);

INSERT INTO BoxUse (`alias`)  VALUES ('Storage');
INSERT INTO BoxUse (`alias`)  VALUES ('Libraries');

UPDATE Box
SET boxUseId = (SELECT boxUseId from BoxUse where BoxUse.`alias` = 'Libraries')
WHERE Box.`boxUseId` = (SELECT boxUseId from BoxUse where BoxUse.`alias` = 'Plate');

DELETE FROM BoxUse WHERE BoxUse.alias = 'Plate';