CREATE TABLE ContactRole (
  contactRoleId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY(contactRoleId),
  CONSTRAINT UQ_contactRole_name UNIQUE(name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Value to add for all existing contacts
INSERT INTO ContactRole(contactRoleId, name) VALUES (1, "Main Contact");

CREATE TABLE Project_Contact_and_Role (
  projectId bigint NOT NULL,
  contactId bigint NOT NULL,
  contactRoleId bigint NOT NULL,
  PRIMARY KEY (projectId, contactId),
  CONSTRAINT fk_project_contact_and_role_project FOREIGN KEY (projectId) REFERENCES Project (projectId),
  CONSTRAINT fk_project_contact_and_role_contact FOREIGN KEY (contactId) REFERENCES Contact (contactId),
  CONSTRAINT fk_project_contact_and_role_contactRole FOREIGN KEY (contactRoleId) REFERENCES ContactRole (contactRoleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;