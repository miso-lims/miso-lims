SELECT o.sequencingOrderId orderId 
        ,lib.NAME libraryId 
        ,bc1.sequence barcode 
        ,bc2.sequence barcode_two 
        ,sp.readLength2 != 0 paired 
        ,sp.readLength read_length 
        ,tr.alias targeted_sequencing 
FROM SequencingOrder o 
LEFT JOIN SequencingParameters sp ON sp.parametersId = o.parametersId 
LEFT JOIN Pool p ON p.poolId = o.poolId 
LEFT JOIN Pool_LibraryAliquot pe ON pe.poolId = p.poolId 
LEFT JOIN LibraryAliquot ld ON ld.aliquotId = pe.aliquotId 
LEFT JOIN TargetedSequencing tr ON tr.targetedSequencingId = ld.targetedSequencingId 
LEFT JOIN Library lib ON lib.libraryId = ld.libraryId 
LEFT JOIN LibraryIndex bc1 ON bc1.indexId = lib.index1Id
LEFT JOIN LibraryIndex bc2 ON bc2.indexId = lib.index2Id
