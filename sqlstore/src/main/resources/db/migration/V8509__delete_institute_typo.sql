-- StartNoTest
SELECT instituteId INTO @centre FROM Institute WHERE alias = 'Sheba Medical Centre';
DELETE FROM Lab WHERE instituteId = @centre;
DELETE FROM Institute WHERE instituteId = @centre;
-- EndNoTest
