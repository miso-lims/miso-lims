-- increase_service_record_title_length

ALTER TABLE SequencerServiceRecord CHANGE COLUMN `title` `title` VARCHAR(255) NOT NULL;


