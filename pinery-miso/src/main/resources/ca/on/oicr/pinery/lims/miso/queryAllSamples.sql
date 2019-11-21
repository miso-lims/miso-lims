SELECT s.alias NAME
        ,s.description description
        ,s.NAME id
        ,parent.NAME parentId
        ,COALESCE(tt.sampleTypeName, sc.alias) sampleType
        ,sc.sampleCategory sample_category
        ,NULL sampleType_platform
        ,NULL sampleType_description
        ,tt.alias tissueType
        ,p.shortName project
        ,sai.archived archived
        ,sai.creationDate inLabCreationDate
        ,s.created created
        ,s.creator createdById
        ,s.lastModified modified
        ,s.lastModifier modifiedById
        ,s.identificationBarcode tubeBarcode
        ,s.volume volume
        ,s.discarded discarded
        ,s.concentration concentration
        ,s.locationBarcode storageLocation
        ,NULL kitName
        ,NULL kitDescription
        ,NULL library_design_code
        ,dist.transferDate receive_date
        ,i.externalName external_name
        ,i.donorSex sex
        ,tor.alias tissue_origin
        ,tm.alias tissue_preparation
        ,st.region tissue_region
        ,st.secondaryIdentifier tube_id
        ,ss.strStatus str_result
        ,sai.groupId group_id
        ,sai.groupDescription group_id_description
        ,sp.alias purpose
        ,qubit.results qubit_concentration
        ,nanodrop.results nanodrop_concentration
        ,rin.results rin
        ,dv200.results dv200
        ,NULL barcode
        ,NULL barcode_two
        ,NULL umis
        ,qpcr.results qpcr_percentage_human
        ,s.qcPassed qcPassed
        ,qpd.description detailedQcStatus
        ,box.locationBarcode boxLocation
        ,box.alias boxAlias
        ,pos.position boxPosition
        ,NULL paired
        ,NULL read_length
        ,NULL targeted_sequencing
        ,'Sample' miso_type
        ,sai.preMigrationId premigration_id
        ,s.scientificName organism
        ,subp.alias subproject
        ,COALESCE(rcpt.institute, it.alias) institute
        ,slide.initialSlides slides
        ,slide.discards discards
        ,stain.name stain
        ,piece.slidesConsumed slides_consumed
        ,NULL pdac
        ,sai.isSynthetic isSynthetic
        ,NOT ISNULL(dist.recipient) distributed
        ,dist.transferDate distribution_date
FROM Sample s
LEFT JOIN DetailedSample sai ON sai.sampleId = s.sampleId 
LEFT JOIN DetailedQcStatus qpd ON qpd.detailedQcStatusId = sai.detailedQcStatusId 
LEFT JOIN Sample parent ON parent.sampleId = sai.parentId 
LEFT JOIN SampleClass sc ON sc.sampleClassId = sai.sampleClassId 
LEFT JOIN Project p ON p.projectId = s.project_projectId 
LEFT JOIN Subproject subp ON subp.subprojectId = sai.subprojectId 
LEFT JOIN Identity i ON i.sampleId = s.sampleId  
LEFT JOIN SampleAliquot sa ON sa.sampleId = sai.sampleId 
LEFT JOIN SamplePurpose sp ON sp.samplePurposeId = sa.samplePurposeId 
LEFT JOIN SampleTissue st ON st.sampleId = s.sampleId 
LEFT JOIN TissueType tt ON tt.tissueTypeId = st.tissueTypeId
LEFT JOIN TissueOrigin tor ON tor.tissueOriginId = st.tissueOriginId 
LEFT JOIN TissueMaterial tm ON tm.tissueMaterialId = st.tissueMaterialId
LEFT JOIN Lab la ON st.labId = la.labId
LEFT JOIN Institute it ON la.instituteId = it.instituteId
LEFT JOIN SampleStock ss ON sai.sampleId = ss.sampleId
LEFT JOIN SampleSlide slide ON slide.sampleId = s.sampleId
LEFT JOIN Stain stain ON stain.stainId = slide.stain
LEFT JOIN SampleTissuePiece piece ON piece.sampleId = s.sampleId
LEFT JOIN (
	    SELECT sqc.sample_sampleId, MAX(sqc.qcId) AS qcId
	    FROM (
            SELECT sample_sampleId, type, MAX(date) AS maxDate
	        FROM SampleQC
	        JOIN QCType ON QCType.qcTypeId = SampleQC.type
	        WHERE QCType.name = 'Qubit'
	        GROUP By sample_sampleId, type
	        ) maxQubitDates
	    JOIN SampleQC sqc ON sqc.sample_sampleId = maxQubitDates.sample_sampleId
	        AND sqc.date = maxQubitDates.maxDate
	        AND sqc.type = maxQubitDates.type
	    GROUP BY sqc.sample_sampleId
		) newestQubit ON newestQubit.sample_sampleId = s.sampleId
LEFT JOIN SampleQC qubit ON qubit.qcId = newestQubit.qcId
LEFT JOIN (
        SELECT sqc.sample_sampleId, MAX(sqc.qcId) AS qcId
        FROM (
            SELECT sample_sampleId, type, MAX(date) AS maxDate
            FROM SampleQC
            JOIN QCType ON QCType.qcTypeId = SampleQC.type
            WHERE QCType.name = 'Nanodrop'
            GROUP By sample_sampleId, type
            ) maxNanodropDates
        JOIN SampleQC sqc ON sqc.sample_sampleId = maxNanodropDates.sample_sampleId
            AND sqc.date = maxNanodropDates.maxDate
            AND sqc.type = maxNanodropDates.type
        GROUP BY sqc.sample_sampleId
        ) newestNanodrop ON newestNanodrop.sample_sampleId = s.sampleId
LEFT JOIN SampleQC nanodrop ON nanodrop.qcId = newestNanodrop.qcId
LEFT JOIN (
        SELECT sqc.sample_sampleId, MAX(sqc.qcId) AS qcId
        FROM (
            SELECT sample_sampleId, type, MAX(date) AS maxDate
            FROM SampleQC
            JOIN QCType ON QCType.qcTypeId = SampleQC.type
            WHERE QCType.name = 'Human qPCR'
            GROUP By sample_sampleId, type
            ) maxQpcrDates
        JOIN SampleQC sqc ON sqc.sample_sampleId = maxQpcrDates.sample_sampleId
            AND sqc.date = maxQpcrDates.maxDate
            AND sqc.type = maxQpcrDates.type
        GROUP BY sqc.sample_sampleId
        ) newestQpcr ON newestQpcr.sample_sampleId = s.sampleId
LEFT JOIN SampleQC qpcr ON qpcr.qcId = newestQpcr.qcId
LEFT JOIN (
	    SELECT sqc.sample_sampleId, MAX(sqc.qcId) AS qcId
	    FROM (
            SELECT sample_sampleId, type, MAX(date) AS maxDate
	        FROM SampleQC
	        JOIN QCType ON QCType.qcTypeId = SampleQC.type
	        WHERE QCType.name = 'RIN'
	        GROUP By sample_sampleId, type
	        ) maxRinDates
	    JOIN SampleQC sqc ON sqc.sample_sampleId = maxRinDates.sample_sampleId
	        AND sqc.date = maxRinDates.maxDate
	        AND sqc.type = maxRinDates.type
	    GROUP BY sqc.sample_sampleId
		) newestRin ON newestRin.sample_sampleId = s.sampleId
LEFT JOIN SampleQC rin ON rin.qcId = newestRin.qcId
LEFT JOIN (
	    SELECT sqc.sample_sampleId, MAX(sqc.qcId) AS qcId
	    FROM (
            SELECT sample_sampleId, type, MAX(date) AS maxDate
	        FROM SampleQC
	        JOIN QCType ON QCType.qcTypeId = SampleQC.type
	        WHERE QCType.name = 'DV200'
	        GROUP By sample_sampleId, type
	        ) maxDv200Dates
	    JOIN SampleQC sqc ON sqc.sample_sampleId = maxDv200Dates.sample_sampleId
	        AND sqc.date = maxDv200Dates.maxDate
	        AND sqc.type = maxDv200Dates.type
	    GROUP BY sqc.sample_sampleId
		) newestDv200 ON newestDv200.sample_sampleId = s.sampleId
LEFT JOIN SampleQC dv200 ON dv200.qcId = newestDv200.qcId
LEFT JOIN BoxPosition pos ON pos.targetId = s.sampleId 
        AND pos.targetType = 'SAMPLE' 
LEFT JOIN Box box ON box.boxId = pos.boxId 
LEFT JOIN (
  SELECT xfers.sampleId, xferinst.alias institute, xfer.transferDate
  FROM Transfer_Sample xfers
  JOIN Transfer xfer ON xfer.transferId = xfers.transferId
  JOIN Lab xferlab ON xferlab.labId = xfer.senderLabId
  JOIN Institute xferinst ON xferinst.instituteId = xferlab.instituteId
  WHERE xfer.senderLabId IS NOT NULL
) rcpt ON rcpt.sampleId = s.sampleId
LEFT JOIN (
  SELECT xfers.sampleId, xfer.recipient, xfer.transferDate
  FROM Transfer_Sample xfers
  JOIN Transfer xfer ON xfer.transferId = xfers.transferId
  WHERE xfer.recipient IS NOT NULL
) dist ON dist.sampleId = s.sampleId
 
UNION ALL
 
SELECT l.alias NAME 
        ,l.description description 
        ,l.NAME id 
        ,parent.NAME parentId 
        ,NULL sampleType 
        ,NULL sample_category
        ,lt.platformType sampleType_platform 
        ,lt.description sampleType_description 
        ,NULL tissueType 
        ,sp.shortName project 
        ,lai.archived archived 
        ,l.creationDate inLabCreationDate
        ,l.created created 
        ,l.creator createdById 
        ,l.lastModified modified 
        ,l.lastModifier modifiedById 
        ,l.identificationBarcode tubeBarcode 
        ,l.volume volume 
        ,l.discarded discarded
        ,l.concentration concentration 
        ,l.locationBarcode storageLocation 
        ,kd.NAME kitName 
        ,kd.description kitDescription 
        ,ldc.code library_design_code 
        ,dist.transferDate receive_date
        ,NULL external_name 
        ,NULL sex
        ,NULL tissue_origin 
        ,NULL tissue_preparation 
        ,NULL tissue_region 
        ,NULL tube_id 
        ,NULL str_result 
        ,lai.groupId group_id 
        ,lai.groupDescription group_id_description 
        ,NULL purpose 
        ,qubit.results qubit_concentration 
        ,NULL nanodrop_concentration 
        ,NULL rin
        ,NULL dv200
        ,bc1.sequence barcode 
        ,bc2.sequence barcode_two
        ,l.umis
        ,NULL qpcr_percentage_human 
        ,l.qcPassed qcPassed 
        ,NULL detailedQcStatus 
        ,box.locationBarcode boxLocation 
        ,box.alias boxAlias 
        ,pos.position boxPosition 
        ,l.paired paired 
        ,l.dnaSize readLength 
        ,NULL targeted_sequencing 
        ,'Library' miso_type 
        ,lai.preMigrationId premigration_id 
        ,NULL organism 
        ,NULL subproject
        ,rcpt.institute institute
        ,NULL slides
        ,NULL discards
        ,NULL stain
        ,NULL slides_consumed
        ,pdac.results pdac
        ,NULL isSynthetic
        ,NOT ISNULL(dist.recipient) distributed
        ,dist.transferDate distribution_date
FROM Library l 
LEFT JOIN Sample parent ON parent.sampleId = l.sample_sampleId
LEFT JOIN Project sp ON sp.projectId = parent.project_projectId
LEFT JOIN DetailedLibrary lai ON lai.libraryId = l.libraryId
LEFT JOIN KitDescriptor kd ON kd.kitDescriptorId = l.kitDescriptorId
LEFT JOIN LibraryDesignCode ldc ON lai.libraryDesignCodeId = ldc.libraryDesignCodeId
LEFT JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType
LEFT JOIN (
        SELECT lqc.library_libraryId, MAX(lqc.qcId) AS qcId
        FROM (
            SELECT library_libraryId, type, MAX(date) AS maxDate
            FROM LibraryQC
            JOIN QCType ON QCType.qcTypeId = LibraryQC.type
            WHERE QCType.name = 'Qubit'
            GROUP By library_libraryId, type
            ) maxQubitDates
        JOIN LibraryQC lqc ON lqc.library_libraryId = maxQubitDates.library_libraryId
            AND lqc.date = maxQubitDates.maxDate
            AND lqc.type = maxQubitDates.type
        GROUP BY lqc.library_libraryId
        ) newestQubit ON newestQubit.library_libraryId = l.libraryId
LEFT JOIN LibraryQC qubit ON qubit.qcId = newestQubit.qcId
LEFT JOIN (
        SELECT lqc.library_libraryId, MAX(lqc.qcId) AS qcId
        FROM (
            SELECT library_libraryId, type, MAX(date) AS maxDate
            FROM LibraryQC
            JOIN QCType ON QCType.qcTypeId = LibraryQC.type
            WHERE QCType.name = 'PDAC Confirmed'
            GROUP By library_libraryId, type
            ) maxPdacDates
        JOIN LibraryQC lqc ON lqc.library_libraryId = maxPdacDates.library_libraryId
            AND lqc.date = maxPdacDates.maxDate
            AND lqc.type = maxPdacDates.type
        GROUP BY lqc.library_libraryId
        ) newestPdac ON newestPdac.library_libraryId = l.libraryId
LEFT JOIN LibraryQC pdac ON pdac.qcId = newestPdac.qcId
LEFT JOIN ( 
        SELECT library_libraryId 
                ,sequence 
        FROM Library_Index 
        INNER JOIN Indices ON Indices.indexId = Library_Index.index_indexId 
        WHERE position = 1 
        ) bc1 ON bc1.library_libraryId = l.libraryId 
LEFT JOIN ( 
        SELECT library_libraryId 
                ,sequence 
        FROM Library_Index 
        INNER JOIN Indices ON Indices.indexId = Library_Index.index_indexId 
                WHERE position = 2 
        ) bc2 ON bc2.library_libraryId = l.libraryId 
LEFT JOIN BoxPosition pos ON pos.targetId = l.libraryId 
        AND pos.targetType = 'LIBRARY' 
LEFT JOIN Box box ON box.boxId = pos.boxId
LEFT JOIN (
  SELECT xferl.libraryId, xferinst.alias institute, xfer.transferDate
  FROM Transfer_Library xferl
  JOIN Transfer xfer ON xfer.transferId = xferl.transferId
  JOIN Lab xferlab ON xferlab.labId = xfer.senderLabId
  JOIN Institute xferinst ON xferinst.instituteId = xferlab.instituteId
  WHERE xfer.senderLabId IS NOT NULL
) rcpt ON rcpt.libraryId = l.libraryId
LEFT JOIN (
  SELECT xferl.libraryId, xfer.recipient, xfer.transferDate
  FROM Transfer_Library xferl
  JOIN Transfer xfer ON xfer.transferId = xferl.transferId
  WHERE xfer.recipient IS NOT NULL
) dist ON dist.libraryId = l.libraryId
 
UNION ALL
 
SELECT d.alias name 
        ,NULL description 
        ,d.NAME id 
        ,COALESCE(laParent.name, lib.name) parentId 
        ,NULL sampleType 
        ,NULL sample_category
        ,lt.platformType sampleType_platform 
        ,lt.description sampleType_description 
        ,NULL tissueType 
        ,sp.shortName project 
        ,0 archived 
        ,d.creationDate inLabCreationDate
        ,d.created created 
        ,NULL createdById 
        ,d.lastUpdated modified 
        ,d.lastModifier modifiedById 
        ,d.identificationBarcode tubeBarcode 
        ,d.volume volume 
        ,d.discarded discarded
        ,d.concentration concentration 
        ,NULL storageLocation 
        ,NULL kitName 
        ,NULL kitDescription 
        ,ldc.code library_design_code 
        ,dist.transferDate receive_date
        ,NULL external_name 
        ,NULL sex
        ,NULL tissue_origin 
        ,NULL tissue_preparation 
        ,NULL tissue_region 
        ,NULL tube_id 
        ,NULL str_result 
        ,dla.groupId group_id 
        ,dla.groupDescription group_id_description 
        ,NULL purpose 
        ,NULL qubit_concentration 
        ,NULL nanodrop_concentration 
        ,NULL rin 
        ,NULL dv200 
        ,NULL barcode 
        ,NULL barcode_two 
        ,NULL umis
        ,NULL qpcr_percentage_human 
        ,1 qcPassed 
        ,NULL detailedQcStatus 
        ,box.locationBarcode boxLocation 
        ,box.alias boxAlias 
        ,pos.position boxPosition 
        ,lib.paired paired 
        ,d.dnaSize readLength 
        ,ts.alias targeted_sequencing 
        ,'Library Aliquot' miso_type 
        ,d.preMigrationId premigration_id 
        ,NULL organism 
        ,NULL subproject
        ,rcpt.institute institute
        ,NULL slides
        ,NULL discards
        ,NULL stain
        ,NULL slides_consumed
        ,NULL pdac
        ,NULL isSynthetic
        ,NOT ISNULL(dist.recipient) distributed
        ,dist.transferDate distribution_date
FROM LibraryAliquot d 
LEFT JOIN LibraryAliquot laParent ON laParent.aliquotId = d.parentAliquotId
JOIN Library lib ON lib.libraryId = d.libraryId 
JOIN Sample s ON s.sampleId = lib.sample_sampleId
JOIN Project sp ON sp.projectId = s.project_projectId
JOIN LibraryType lt ON lt.libraryTypeId = lib.libraryType 
LEFT JOIN DetailedLibraryAliquot dla ON dla.aliquotId = d.aliquotId 
LEFT JOIN LibraryDesignCode ldc ON ldc.libraryDesignCodeId = dla.libraryDesignCodeId
LEFT JOIN TargetedSequencing ts ON d.targetedSequencingId = ts.targetedSequencingId
LEFT JOIN BoxPosition pos ON pos.targetId = d.aliquotId 
        AND pos.targetType = 'LIBRARY_ALIQUOT' 
LEFT JOIN Box box ON box.boxId = pos.boxId
LEFT JOIN (
  SELECT xferla.aliquotId, xferinst.alias institute, xfer.transferDate
  FROM Transfer_LibraryAliquot xferla
  JOIN Transfer xfer ON xfer.transferId = xferla.transferId
  JOIN Lab xferlab ON xferlab.labId = xfer.senderLabId
  JOIN Institute xferinst ON xferinst.instituteId = xferlab.instituteId
  WHERE xfer.senderLabId IS NOT NULL
) rcpt ON rcpt.aliquotId = d.aliquotId
LEFT JOIN (
  SELECT xferla.aliquotId, xfer.recipient, xfer.transferDate
  FROM Transfer_LibraryAliquot xferla
  JOIN Transfer xfer ON xfer.transferId = xferla.transferId
  WHERE xfer.recipient IS NOT NULL
) dist ON dist.aliquotId = d.aliquotId
