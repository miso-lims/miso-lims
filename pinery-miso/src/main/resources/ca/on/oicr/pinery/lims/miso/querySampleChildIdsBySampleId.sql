SELECT child.name id
FROM Sample child
JOIN Sample parent ON parent.sampleId = child.parentId
WHERE parent.name = ?

UNION ALL

SELECT child.name id
FROM Library child
JOIN Sample parent ON parent.sampleId = child.sample_sampleId
WHERE parent.name = ?

UNION ALL

SELECT child.name id
FROM LibraryAliquot child
JOIN Library libParent ON libParent.libraryId = child.libraryId
LEFT JOIN LibraryAliquot ldiParent ON ldiParent.aliquotId = child.parentAliquotId
WHERE (child.parentAliquotId IS NULL AND libParent.name = ?)
OR ldiParent.name = ?
