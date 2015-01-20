USE lims;

ALTER TABLE `lims`.`Sample` CHANGE `qcPassed` `qcPassed` VARCHAR(5) DEFAULT NULL;
UPDATE `lims`.`Sample` SET qcPassed="false" WHERE qcPassed="0";
UPDATE `lims`.`Sample` SET qcPassed="true" WHERE qcPassed="1";

//UPDATE `lims`.`Sample` SET qcPassed=null WHERE qcPassed="false" AND receivedDate IS null;