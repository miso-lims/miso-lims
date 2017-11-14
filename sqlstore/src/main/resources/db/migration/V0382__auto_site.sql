-- update_ts_requirements
-- StartNoTest
UPDATE LibraryDesignCode SET targetedSequencingRequired = 0 WHERE code IN ('MR', 'SM', 'WT', 'TR');
-- EndNoTest

