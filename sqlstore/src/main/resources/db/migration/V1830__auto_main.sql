-- instrument_model
ALTER TABLE InstrumentModel ADD COLUMN defaultRunSopId BIGINT;
ALTER TABLE InstrumentModel ADD CONSTRAINT fk_instrumentModel_defaultRunSop FOREIGN KEY (defaultRunSopId) REFERENCES Sop(sopId);

