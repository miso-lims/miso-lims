-- Subproject_Priority_Notice

ALTER TABLE `Subproject` MODIFY `priority` tinyint NOT NULL;


-- Drop_DetailedSample_Concentration

ALTER TABLE `DetailedSample` DROP COLUMN `concentration`;


