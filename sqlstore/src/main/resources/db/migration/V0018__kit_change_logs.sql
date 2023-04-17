ALTER TABLE KitDescriptor ADD COLUMN lastModifier bigint NOT NULL DEFAULT 1;
ALTER TABLE KitDescriptor ADD CONSTRAINT kitdescriptor_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);

CREATE TABLE KitDescriptorChangeLog (
  kitDescriptorId bigint NOT NULL REFERENCES KitDescriptor(kitDescriptorId),
  columnsChanged text NOT NULL,
  userId bigint NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
