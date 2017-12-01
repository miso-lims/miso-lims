ALTER TABLE LibraryDilution ADD COLUMN lastUpdated TIMESTAMP NOT NULL DEFAULT '1970-01-02 00:00:01';
UPDATE LibraryDilution ldi SET ldi.lastUpdated = (SELECT MAX(changeTime) FROM LibraryChangeLog lcl WHERE lcl.libraryId = ldi.library_libraryId) WHERE ldi.lastUpdated = '1970-01-02 00:00:01';
ALTER TABLE LibraryDilution ALTER COLUMN lastUpdated DROP DEFAULT;
