-- unique_library_template
ALTER TABLE LibraryTemplate ADD CONSTRAINT uk_libraryTemplate_alias UNIQUE (alias);

