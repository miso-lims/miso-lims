CREATE TABLE ApiKey (
  keyId bigint NOT NULL AUTO_INCREMENT,
  userId bigint NOT NULL,
  apiKey varchar(50) NOT NULL,
  apiSecret varchar(255) NOT NULL,
  creator bigint NOT NULL,
  created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (keyId),
  CONSTRAINT fk_apikey_user FOREIGN KEY (userId) REFERENCES User (userId),
  CONSTRAINT fk_apikey_creator FOREIGN KEY (creator) REFERENCES User (userId),
  CONSTRAINT uk_apikey_key UNIQUE (apiKey)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
