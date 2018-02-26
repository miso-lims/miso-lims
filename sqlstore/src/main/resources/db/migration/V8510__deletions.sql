CREATE TABLE Deletion (
  deletionId bigint(20) NOT NULL AUTO_INCREMENT,
  securityProfileId bigint(20),
  targetType varchar(50) NOT NULL,
  targetId bigint(20) NOT NULL,
  description varchar(255) NOT NULL,
  userId bigint(20) NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (deletionId),
  CONSTRAINT fk_deletion_securityProfile FOREIGN KEY (securityProfileId) REFERENCES SecurityProfile(profileId),
  CONSTRAINT fk_deletion_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
