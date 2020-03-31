CREATE OR REPLACE VIEW SampleHierarchyView AS
SELECT sh.sampleId, ident.externalName, ident.consentLevel, st.tissueOriginId, st.tissueTypeId
FROM SampleHierarchy sh
LEFT JOIN Sample ident ON ident.sampleId = sh.identityId
LEFT JOIN Sample st ON st.sampleId = sh.tissueId;
