CREATE TABLE SampleChangeLog (
  sampleId bigint(20) NOT NULL REFERENCES Sample(sampleId),
  columnsChanged text NOT NULL,
  securityProfile_profileId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TRIGGER SampleChange BEFORE UPDATE ON Sample
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, securityProfile_profileId, message) VALUES (
    NEW.sampleId,
    CONCAT_WS(',',
      CASE WHEN NEW.accession <> OLD.accession THEN 'accession' END,
      CASE WHEN NEW.name <> OLD.name THEN 'name' END,
      CASE WHEN NEW.description <> OLD.description THEN 'description' END,
      CASE WHEN NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
      CASE WHEN NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
      CASE WHEN NEW.sampleType <> OLD.sampleType THEN 'sampleType' END,
      CASE WHEN NEW.receivedDate <> OLD.receivedDate THEN 'receivedDate' END,
      CASE WHEN NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
      CASE WHEN NEW.alias <> OLD.alias THEN 'alias' END,
      CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN 'project_projectId' END,
      CASE WHEN NEW.scientificName <> OLD.scientificName THEN 'scientificName' END,
      CASE WHEN NEW.taxonIdentifier <> OLD.taxonIdentifier THEN 'taxonIdentifier' END),
    NEW.securityProfile_profileId,
    CONCAT(
      (SELECT DISTINCT fullname FROM User, SecurityProfile WHERE profileId = NEW.securityProfile_profileId AND owner_userId = userId),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN NEW.accession <> OLD.accession THEN  CONCAT('accession: ', OLD.accession, ' → ', NEW.accession) END,
        CASE WHEN NEW.name <> OLD.name THEN  CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.description <> OLD.description THEN  CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN NEW.identificationBarcode <> OLD.identificationBarcode THEN  CONCAT('identification: ', OLD.identificationBarcode, ' → ', NEW.identificationBarcode) END,
        CASE WHEN NEW.locationBarcode <> OLD.locationBarcode THEN  CONCAT('location: ', OLD.locationBarcode, ' → ', NEW.locationBarcode) END,
        CASE WHEN NEW.sampleType <> OLD.sampleType THEN  CONCAT('type: ', OLD.sampleType, ' → ', NEW.sampleType) END,
        CASE WHEN NEW.receivedDate <> OLD.receivedDate THEN  CONCAT('received: ', OLD.receivedDate, ' → ', NEW.receivedDate) END,
        CASE WHEN NEW.qcPassed <> OLD.qcPassed THEN  CONCAT('qcPassed: ', OLD.qcPassed, ' → ', NEW.qcPassed) END,
        CASE WHEN NEW.alias <> OLD.alias THEN  CONCAT('alias: ', OLD.alias, ' → ', NEW.alias) END,
        CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN CONCAT('project: ', (SELECT name FROM Project WHERE projectId = OLD.project_projectId), ' → ', (SELECT name FROM Project WHERE projectId = NEW.project_projectId)) END,
        CASE WHEN NEW.scientificName <> OLD.scientificName THEN  CONCAT('scientificName: ', OLD.scientificName, ' → ', NEW.scientificName) END,
        CASE WHEN NEW.taxonIdentifier <> OLD.taxonIdentifier THEN  CONCAT('taxonIdentifier: ', OLD.taxonIdentifier, ' → ', NEW.taxonIdentifier) END)));

CREATE TRIGGER SampleInsert AFTER INSERT ON Sample
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, securityProfile_profileId, message) VALUES (
    NEW.sampleId,
    NULL,
    NEW.securityProfile_profileId,
    CONCAT(
      (SELECT DISTINCT fullname FROM User, SecurityProfile WHERE profileId = NEW.securityProfile_profileId AND owner_userId = userId),
      ' created sample.'));
