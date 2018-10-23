-- instrument_out_of_service

ALTER TABLE ServiceRecord CHANGE COLUMN shutdownTime startTime timestamp NULL DEFAULT NULL;
ALTER TABLE ServiceRecord CHANGE COLUMN restoredTime endTime timestamp NULL DEFAULT NULL;
ALTER TABLE ServiceRecord ADD COLUMN outOfService BOOLEAN NOT NULL DEFAULT TRUE;


