-- Subproject_Priority_Notice

ALTER TABLE `Subproject` MODIFY `priority` tinyint(1) NOT NULL;


-- Drop_DetailedSample_Concentration

ALTER TABLE `DetailedSample` DROP COLUMN `concentration`;


