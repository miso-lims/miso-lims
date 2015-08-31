
ALTER TABLE `Sample` CHANGE `qcPassed` `qcPassed` VARCHAR(5) DEFAULT NULL;
UPDATE `Sample` SET qcPassed="false" WHERE qcPassed="0";
UPDATE `Sample` SET qcPassed="true" WHERE qcPassed="1";

//UPDATE `Sample` SET qcPassed=null WHERE qcPassed="false" AND receivedDate IS null;
