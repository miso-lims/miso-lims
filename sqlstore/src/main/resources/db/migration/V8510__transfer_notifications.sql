CREATE TABLE TransferNotification (
  notificationId bigint(20) NOT NULL AUTO_INCREMENT,
  transferId bigint(20) NOT NULL,
  recipientName varchar(255) NOT NULL,
  recipientEmail varchar(255) NOT NULL,
  creator bigint(20) NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  sentTime TIMESTAMP NULL DEFAULT NULL,
  sendSuccess BOOLEAN,
  failureSentTime TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (notificationId),
  CONSTRAINT fk_transferNotification_transfer FOREIGN KEY (transferId) REFERENCES Transfer (transferId),
  CONSTRAINT fk_transferNotification_creator FOREIGN KEY (creator) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Contact (
  contactId bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  PRIMARY KEY (contactId),
  CONSTRAINT uk_contact_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
