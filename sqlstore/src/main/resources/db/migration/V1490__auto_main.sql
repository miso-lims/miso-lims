-- add_ContactRole_type
CREATE TABLE ContactRole (
  contactRoleId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY(contactRoleId),
  CONSTRAINT UQ_contactRole_name UNIQUE(name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Value to add for all existing contacts
INSERT INTO ContactRole(contactRoleId, name) VALUES (1, "Main Contact");

CREATE TABLE Project_Contact (
  projectId bigint NOT NULL,
  contactId bigint NOT NULL,
  contactRoleId bigint NOT NULL,
  PRIMARY KEY (projectId, contactId, contactRoleId),
  CONSTRAINT fk_project_contact_project FOREIGN KEY (projectId) REFERENCES Project (projectId),
  CONSTRAINT fk_project_contact_contact FOREIGN KEY (contactId) REFERENCES Contact (contactId),
  CONSTRAINT fk_project_contact_contactRole FOREIGN KEY (contactRoleId) REFERENCES ContactRole (contactRoleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Migrate all contacts stored in Project table to new Project_Contact table, set default Contact Role to "Main Contact"
INSERT INTO Project_Contact (projectId, contactId, contactRoleId) SELECT projectId, contactId,1 FROM Project WHERE contactId IS NOT NULL;

-- Deleting now redundant contact field in Project
ALTER TABLE Project DROP FOREIGN KEY fk_project_contact;
ALTER TABLE Project DROP COLUMN contactId;

-- test_prohibit_supplemental
ALTER TABLE AssayTest ADD COLUMN permittedSamples varchar(20) NOT NULL DEFAULT "ALL";

