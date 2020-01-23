ALTER TABLE _Group ADD COLUMN builtIn BOOLEAN NOT NULL DEFAULT FALSE;

INSERT INTO _Group (name, description, builtIn)
VALUES ('Run Approvers', 'Users allowed to approve run data', TRUE);
