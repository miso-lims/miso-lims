ALTER TABLE Project
  ADD COLUMN samplesExpected int,
  ADD COLUMN contactId bigint(20),
  ADD CONSTRAINT fk_project_contact FOREIGN KEY (contactId) REFERENCES Contact (contactId);
