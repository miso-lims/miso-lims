ALTER TABLE Workstation ADD COLUMN identificationBarcode varchar(255);
ALTER TABLE Instrument
  ADD COLUMN identificationBarcode varchar(255),
  ADD COLUMN workstationId bigint(20),
  ADD CONSTRAINT fk_instrument_workstation FOREIGN KEY (workstationId) REFERENCES Workstation (workstationId);
