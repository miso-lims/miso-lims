CREATE TABLE Workstation (
  workstationId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  description varchar(255),
  PRIMARY KEY (workstationId),
  UNIQUE KEY uk_workstation_alias (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Library ADD COLUMN thermalCyclerId bigint(20);
ALTER TABLE Library ADD CONSTRAINT fk_library_thermalCycler FOREIGN KEY (thermalCyclerId) REFERENCES Instrument (instrumentId);
ALTER TABLE Library ADD COLUMN workstationId bigint(20);
ALTER TABLE Library ADD CONSTRAINT fk_library_workstation FOREIGN KEY (workstationId) REFERENCES Workstation (workstationId);
