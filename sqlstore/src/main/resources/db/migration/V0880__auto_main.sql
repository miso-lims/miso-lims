-- password_hash
UPDATE User SET password = CONCAT('{SHA-1}', password) WHERE password IS NOT NULL;

