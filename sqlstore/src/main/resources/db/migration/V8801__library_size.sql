ALTER TABLE Library ADD COLUMN dnaSize bigint(20);

UPDATE Library SET dnaSize = (SELECT insertSize FROM LibraryQC WHERE LibraryQC.library_libraryId = Library.libraryId AND insertSize IS NOT NULL AND insertSize != 0 ORDER BY qcDate LIMIT 1);
