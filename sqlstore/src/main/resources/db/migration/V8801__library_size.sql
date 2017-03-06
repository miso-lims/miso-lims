ALTER TABLE Library ADD COLUMN dnaSize bigint(20);

UPDATE Library SET dnaSize (SELECT insertSize FROM LibraryQC WHERE LibraryQC.library_libraryId = Library.libraryId ORDER BY qcDate LIMIT 1);
