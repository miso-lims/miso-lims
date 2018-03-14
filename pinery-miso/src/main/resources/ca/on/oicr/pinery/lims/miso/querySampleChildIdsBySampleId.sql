SELECT child.name id FROM Sample child 
JOIN DetailedSample csai ON csai.sampleId = child.sampleId JOIN Sample parent ON parent.sampleId = csai.parentId 
WHERE parent.name = ? UNION ALL SELECT child.name id FROM Library child 
JOIN Sample parent ON parent.sampleId = child.sample_sampleId WHERE parent.name = ?
