-- Disable "Trigger does not exist" and other note-level warnings
-- done here because before/after migrate scripts and migrations may run in different sessions
SET sql_notes = 0;
