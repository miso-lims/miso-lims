CREATE OR REPLACE VIEW AttachmentUsage AS
  SELECT attachmentId, libraries+pools+projects+samples+serviceRecords AS `usage` FROM (
    SELECT a.attachmentId, COUNT(la.libraryId) AS libraries, COUNT(pa.poolId) AS pools, COUNT(pra.projectId) AS projects, COUNT(sa.sampleId) AS samples,
      COUNT(sra.recordId) AS serviceRecords
    FROM Attachment a
    LEFT JOIN Library_Attachment la ON la.attachmentId = a.attachmentId
    LEFT JOIN Pool_Attachment pa ON pa.attachmentId = a.attachmentId
    LEFT JOIN Project_Attachment pra ON pra.attachmentId = a.attachmentId
    LEFT JOIN Sample_Attachment sa ON sa.attachmentId = a.attachmentId
    LEFT JOIN ServiceRecord_Attachment sra ON sra.attachmentId = a.attachmentId
    GROUP BY a.attachmentId
  ) sub;
