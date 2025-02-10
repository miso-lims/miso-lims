ALTER TABLE Instrument
  ADD CONSTRAINT uk_instrument_identificationBarcode UNIQUE (identificationBarcode);
