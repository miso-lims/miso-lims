SELECT o.poolOrderId orderId 
        ,lib.NAME libraryId 
        ,bc1.sequence barcode 
        ,bc2.sequence barcode_two 
        ,sp.paired paired 
        ,sp.readLength read_length 
        ,tr.alias targeted_sequencing 
FROM PoolOrder o 
LEFT JOIN SequencingParameters sp ON sp.parametersId = o.parametersId 
LEFT JOIN Pool p ON p.poolId = o.poolId 
LEFT JOIN Pool_Dilution pe ON pe.pool_poolId = p.poolId 
LEFT JOIN LibraryDilution ld ON ld.dilutionId = pe.dilution_dilutionId 
LEFT JOIN TargetedSequencing tr ON tr.targetedSequencingId = ld.targetedSequencingId 
LEFT JOIN Library lib ON lib.libraryId = ld.library_libraryId 
LEFT JOIN ( 
        SELECT library_libraryId 
                ,sequence 
        FROM Library_Index ltb 
        INNER JOIN Indices AS tb ON tb.indexId = ltb.index_indexId AND tb.position = 1 
        ) bc1 ON bc1.library_libraryId = lib.libraryId 
LEFT JOIN ( 
        SELECT library_libraryId 
                ,sequence 
        FROM Library_Index ltb 
        INNER JOIN Indices AS tb ON tb.indexId = ltb.index_indexId AND tb.position = 2 
        ) bc2 ON bc2.library_libraryId = lib.libraryId
