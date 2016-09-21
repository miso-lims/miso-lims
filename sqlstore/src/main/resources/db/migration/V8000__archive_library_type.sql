UPDATE LibraryType SET archived = TRUE WHERE platformType = 'Illumina' AND description NOT IN ('Paired End', 'Mate Pair', 'Single End');
