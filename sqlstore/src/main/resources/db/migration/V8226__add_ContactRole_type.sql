CREATE TABLE ContactRole (
  contactRoleId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY(contactRoleId),
  CONSTRAINT UQ_contactRole_name UNIQUE(name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO ContactRole(contactRoleId, name) VALUES (1, "Main Contact");