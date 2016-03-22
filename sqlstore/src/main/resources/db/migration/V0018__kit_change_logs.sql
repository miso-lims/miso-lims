ALTER TABLE KitDescriptor ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE KitDescriptor ADD CONSTRAINT kitdescriptor_user_userid_fkey FOREIGN KEY(lastModifier) REFERENCES User(userId);

CREATE TABLE KitDescriptorChangeLog (
  kitDescriptorId bigint(20) NOT NULL REFERENCES KitDescriptor(kitDescriptorId),
  columnsChanged text NOT NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;
