-- shortname

ALTER TABLE Project CHANGE COLUMN shortName shortName VARCHAR(255);


-- printer_layout

ALTER TABLE Printer ADD COLUMN layout varchar(255);

UPDATE Printer SET
  layout = CASE
    WHEN driver = 'ZEBRA_8363' THEN 'AVERY_8363'
    ELSE SUBSTRING(driver FROM 7) END,
  driver = CASE
    WHEN driver LIKE 'ZEBRA_%' THEN 'ZEBRA_24DPMM'
    ELSE 'BRADY' END;


