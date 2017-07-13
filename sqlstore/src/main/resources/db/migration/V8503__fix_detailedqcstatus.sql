-- Was done in an OICR site-specific migration and missing from mainline
ALTER TABLE DetailedQcStatus MODIFY status BOOLEAN NULL;
