ALTER TABLE LibraryDilution ADD creator bigint(20) NOT NULL AFTER dilutionUserName;
UPDATE LibraryDilution SET creator = (SELECT userId FROM User WHERE fullName = dilutionUserName);
ALTER TABLE LibraryDilution ADD CONSTRAINT fk_libraryDilution_creator FOREIGN KEY (creator) REFERENCES User (userId);
ALTER TABLE LibraryDilution DROP COLUMN dilutionUserName;
