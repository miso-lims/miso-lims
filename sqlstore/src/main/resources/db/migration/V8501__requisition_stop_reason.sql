ALTER TABLE Requisition ADD COLUMN stopReason varchar(255);
UPDATE Requisition SET stopReason = 'Unspecified' WHERE stopped = TRUE;
