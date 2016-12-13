--StartNoTest
SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SET @time = NOW();

INSERT INTO TissueOrigin (alias, description, createdBy, creationDate, updatedBy, lastUpdated) VALUES
('Mo', 'Mouth', @user, @time, @USER, @time),
('Np', 'Nasopharynx', @user, @time, @user, @time),
('To', 'Throat', @user, @time, @user, @time),
('Hp', 'Hypopharynx', @user, @time, @user, @time);
--EndNoTest