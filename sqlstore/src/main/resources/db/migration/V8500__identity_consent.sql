ALTER TABLE Identity ADD COLUMN consentLevel varchar(50) NOT NULL DEFAULT 'THIS_PROJECT';
ALTER TABLE Identity CHANGE COLUMN consentLevel consentLevel varchar(50) NOT NULL;
