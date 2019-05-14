-- project_changelogs
ALTER TABLE Project ADD COLUMN creator bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE Project MODIFY COLUMN creator bigint(20) NOT NULL;

ALTER TABLE Project ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE Project MODIFY COLUMN lastModifier bigint(20) NOT NULL;

ALTER TABLE Project CHANGE COLUMN creationDate created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Project CHANGE COLUMN lastUpdated lastModified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE ProjectChangeLog (
  projectChangeLogId bigint(20) NOT NULL AUTO_INCREMENT,
  projectId bigint(20) NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint(20) NOT NULL,
  message longtext NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (projectChangeLogId),
  CONSTRAINT fk_projectChangeLog_project FOREIGN KEY (projectId) REFERENCES Project(projectId),
  CONSTRAINT fk_projectChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO ProjectChangeLog(projectId, columnsChanged, userId, message, changeTime)
SELECT projectId, '', 1, 'Project created.', created FROM Project;

-- user_group_constraint
ALTER TABLE User_Group ADD CONSTRAINT uk_user_group UNIQUE (users_userId, groups_groupId);

