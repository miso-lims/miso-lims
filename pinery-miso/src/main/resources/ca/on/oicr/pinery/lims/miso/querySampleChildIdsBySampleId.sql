SELECT child.name id
FROM Sample child
JOIN Sample parent ON parent.sampleId = child.parentId
WHERE parent.name = ?

UNION ALL

SELECT child.name id
FROM Library child
JOIN Sample parent ON parent.sampleId = child.sample_sampleId
WHERE parent.name = ?
