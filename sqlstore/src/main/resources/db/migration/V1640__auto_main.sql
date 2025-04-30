-- unique_instruments
ALTER TABLE Instrument
  ADD CONSTRAINT uk_instrument_identificationBarcode UNIQUE (identificationBarcode);

-- unique_qcType
ALTER TABLE QCType ADD CONSTRAINT uk_qcType UNIQUE (name, qcTarget);

