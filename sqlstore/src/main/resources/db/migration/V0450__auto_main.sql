-- identity_consent

ALTER TABLE Identity ADD COLUMN consentLevel varchar(50) NOT NULL DEFAULT 'THIS_PROJECT';
ALTER TABLE Identity CHANGE COLUMN consentLevel consentLevel varchar(50) NOT NULL;


-- deletions

CREATE TABLE Deletion (
  deletionId bigint NOT NULL AUTO_INCREMENT,
  securityProfileId bigint,
  targetType varchar(50) NOT NULL,
  targetId bigint NOT NULL,
  description varchar(255) NOT NULL,
  userId bigint NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (deletionId),
  CONSTRAINT fk_deletion_securityProfile FOREIGN KEY (securityProfileId) REFERENCES SecurityProfile(profileId),
  CONSTRAINT fk_deletion_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


