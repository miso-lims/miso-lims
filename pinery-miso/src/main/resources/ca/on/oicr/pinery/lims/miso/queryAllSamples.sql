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
        ,s.archived archived
        ,s.creationDate inLabCreationDate
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
        ,DATE(rcpt.transferTime) receive_date
        ,s.externalName external_name
        ,s.donorSex sex
        ,tor.alias tissue_origin
        ,tm.alias tissue_preparation
        ,s.region tissue_region
        ,s.secondaryIdentifier tube_id
        ,s.strStatus str_result
        ,s.groupId group_id
        ,s.groupDescription group_id_description
        ,sp.alias purpose
        ,qubit.results qubit_concentration
        ,nanodrop.results nanodrop_concentration
        ,rin.results rin
        ,dv200.results dv200
        ,NULL barcode
        ,NULL barcode_two
        ,NULL barcode_kit
        ,NULL umis
        ,qpcr.results qpcr_percentage_human
        ,qpd.status qcPassed
        ,qpd.description detailedQcStatus
        ,box.locationBarcode boxLocation
        ,box.alias boxAlias
        ,pos.position boxPosition
        ,NULL paired
        ,NULL read_length
        ,NULL targeted_sequencing
        ,'Sample' miso_type
        ,s.preMigrationId premigration_id
        ,sn.alias organism
        ,subp.alias subproject
        ,COALESCE(IF(rcpt.excludeFromPinery, NULL, rcpt.institute), IF(la.excludeFromPinery, NULL, it.alias)) institute
        ,s.initialSlides slides
        ,s.discards discards
        ,stain.name stain
        ,s.slidesConsumed slides_consumed
        ,NULL pdac
        ,s.isSynthetic isSynthetic
        ,NOT ISNULL(dist.recipient) distributed
        ,DATE(dist.transferTime) distribution_date
        ,s.initialVolume initial_volume
        ,s.percentTumour percent_tumour
        ,s.percentNecrosis percent_necrosis
        ,s.markedAreaSize marked_area_size
        ,s.markedAreaPercentTumour marked_area_percent_tumour
        ,refSlide.name reference_slide_id
        ,s.targetCellRecovery target_cell_recovery
        ,s.cellViability cell_viability
        ,NULL spike_in
        ,NULL spike_in_dilution_factor
        ,NULL spike_in_volume_ul
        ,sct.alias sequencing_control_type
        ,custody.recipient AS custody
        ,custody.transferRequestName AS latest_transfer_request
        ,NULL batch_id
FROM Sample s
LEFT JOIN DetailedQcStatus qpd ON qpd.detailedQcStatusId = s.detailedQcStatusId 
LEFT JOIN Sample parent ON parent.sampleId = s.parentId 
LEFT JOIN SampleClass sc ON sc.sampleClassId = s.sampleClassId 
LEFT JOIN Project p ON p.projectId = s.project_projectId 
LEFT JOIN Subproject subp ON subp.subprojectId = s.subprojectId
LEFT JOIN SamplePurpose sp ON sp.samplePurposeId = s.samplePurposeId 
LEFT JOIN TissueType tt ON tt.tissueTypeId = s.tissueTypeId
LEFT JOIN TissueOrigin tor ON tor.tissueOriginId = s.tissueOriginId 
LEFT JOIN TissueMaterial tm ON tm.tissueMaterialId = s.tissueMaterialId
LEFT JOIN Lab la ON s.labId = la.labId
LEFT JOIN Institute it ON la.instituteId = it.instituteId
LEFT JOIN Sample refSlide ON refSlide.sampleId = s.referenceSlideId
LEFT JOIN Stain stain ON stain.stainId = s.stain
LEFT JOIN ScientificName sn ON sn.scientificNameId = s.scientificNameId
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
  SELECT xfers.sampleId, xferinst.alias institute, xfer.transferTime, xferlab.excludeFromPinery
  FROM Transfer_Sample xfers
  JOIN Transfer xfer ON xfer.transferId = xfers.transferId
  JOIN Lab xferlab ON xferlab.labId = xfer.senderLabId
  JOIN Institute xferinst ON xferinst.instituteId = xferlab.instituteId
  WHERE xfer.senderLabId IS NOT NULL
) rcpt ON rcpt.sampleId = s.sampleId
LEFT JOIN (
  SELECT xfers.sampleId, xfer.recipient, xfer.transferTime
  FROM Transfer_Sample xfers
  JOIN Transfer xfer ON xfer.transferId = xfers.transferId
  WHERE xfer.recipient IS NOT NULL
) dist ON dist.sampleId = s.sampleId
LEFT JOIN SequencingControlType sct ON sct.sequencingControlTypeId = s.sequencingControlTypeId
LEFT JOIN (
  SELECT xfer.transferId, xfers.sampleId, xfer.transferTime, COALESCE(rg.name, xfer.recipient) AS recipient,
    xfer.transferRequestName
  FROM Transfer_Sample xfers
  JOIN Transfer xfer ON xfer.transferId = xfers.transferId
  LEFT JOIN _Group rg ON rg.groupId = xfer.recipientGroupId
) custody ON custody.sampleId = s.sampleId
LEFT JOIN (
  SELECT xfer.transferId, xfers.sampleId, xfer.transferTime
  FROM Transfer_Sample xfers
  JOIN Transfer xfer ON xfer.transferId = xfers.transferId
) custody2 ON (
  custody2.sampleId = s.sampleId
  AND (
    custody.transferTime < custody2.transferTime 
    OR (
      custody.transferTime = custody2.transferTime
      AND custody.transferId < custody2.transferId
    )
  )
)
WHERE custody2.transferId IS NULL
 
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
        ,l.archived archived 
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
        ,DATE(rcpt.transferTime) receive_date
        ,NULL external_name 
        ,NULL sex
        ,NULL tissue_origin 
        ,NULL tissue_preparation 
        ,NULL tissue_region 
        ,NULL tube_id 
        ,NULL str_result 
        ,l.groupId group_id 
        ,l.groupDescription group_id_description 
        ,NULL purpose 
        ,qubit.results qubit_concentration 
        ,NULL nanodrop_concentration 
        ,NULL rin
        ,NULL dv200
        ,bc1.sequence barcode 
        ,bc2.sequence barcode_two
        ,bc1.indexFamily barcode_kit
        ,l.umis
        ,NULL qpcr_percentage_human 
        ,qpd.status qcPassed
        ,qpd.description detailedQcStatus
        ,box.locationBarcode boxLocation 
        ,box.alias boxAlias 
        ,pos.position boxPosition 
        ,l.paired paired 
        ,l.dnaSize read_length 
        ,NULL targeted_sequencing 
        ,'Library' miso_type 
        ,l.preMigrationId premigration_id 
        ,NULL organism 
        ,NULL subproject
        ,IF(rcpt.excludeFromPinery, NULL, rcpt.institute) institute
        ,NULL slides
        ,NULL discards
        ,NULL stain
        ,NULL slides_consumed
        ,pdac.results pdac
        ,NULL isSynthetic
        ,NOT ISNULL(dist.recipient) distributed
        ,DATE(dist.transferTime) distribution_date
        ,l.initialVolume initial_volume
        ,NULL percent_tumour
        ,NULL percent_necrosis
        ,NULL marked_area_size
        ,NULL marked_area_percent_tumour
        ,NULL reference_slide_id
        ,NULL target_cell_recovery
        ,NULL cell_viability
        ,lsi.alias spike_in
        ,l.spikeInDilutionFactor spike_in_dilution_factor
        ,l.spikeInVolume spike_in_volume_ul
        ,NULL sequencing_control_type
        ,custody.recipient AS custody
        ,custody.transferRequestName AS latest_transfer_request
        ,IF(
            l.creationDate IS NULL OR l.sopId IS NULL OR l.kitLot IS NULL,
            NULL,
            CONCAT(DATE_FORMAT(l.creationDate, '%Y-%m-%d'), '_u', l.creator, '_s', l.sopId, '_k', l.kitDescriptorId, '-', l.kitLot)
        ) batch_id
FROM Library l 
LEFT JOIN Sample parent ON parent.sampleId = l.sample_sampleId
LEFT JOIN Project sp ON sp.projectId = parent.project_projectId
LEFT JOIN DetailedQcStatus qpd ON qpd.detailedQcStatusId = l.detailedQcStatusId 
LEFT JOIN LibrarySpikeIn lsi ON lsi.spikeInId = l.spikeInId
LEFT JOIN KitDescriptor kd ON kd.kitDescriptorId = l.kitDescriptorId
LEFT JOIN LibraryDesignCode ldc ON l.libraryDesignCodeId = ldc.libraryDesignCodeId
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
                ,IndexFamily.name indexFamily
        FROM Library_Index 
        INNER JOIN Indices ON Indices.indexId = Library_Index.index_indexId
        JOIN IndexFamily ON IndexFamily.indexFamilyId = Indices.indexFamilyId
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
  SELECT xferl.libraryId, xferinst.alias institute, xfer.transferTime, xferlab.excludeFromPinery
  FROM Transfer_Library xferl
  JOIN Transfer xfer ON xfer.transferId = xferl.transferId
  JOIN Lab xferlab ON xferlab.labId = xfer.senderLabId
  JOIN Institute xferinst ON xferinst.instituteId = xferlab.instituteId
  WHERE xfer.senderLabId IS NOT NULL
) rcpt ON rcpt.libraryId = l.libraryId
LEFT JOIN (
  SELECT xferl.libraryId, xfer.recipient, xfer.transferTime
  FROM Transfer_Library xferl
  JOIN Transfer xfer ON xfer.transferId = xferl.transferId
  WHERE xfer.recipient IS NOT NULL
) dist ON dist.libraryId = l.libraryId
LEFT JOIN (
  SELECT xfer.transferId, xferl.libraryId, xfer.transferTime, COALESCE(rg.name, xfer.recipient) AS recipient,
    xfer.transferRequestName
  FROM Transfer_Library xferl
  JOIN Transfer xfer ON xfer.transferId = xferl.transferId
  LEFT JOIN _Group rg ON rg.groupId = xfer.recipientGroupId
) custody ON custody.libraryId = l.libraryId
LEFT JOIN (
  SELECT xfer.transferId, xferl.libraryId, xfer.transferTime
  FROM Transfer_Library xferl
  JOIN Transfer xfer ON xfer.transferId = xferl.transferId
) custody2 ON (
  custody2.libraryId = l.libraryId
  AND (
    custody.transferTime < custody2.transferTime 
    OR (
      custody.transferTime = custody2.transferTime
      AND custody.transferId < custody2.transferId
    )
  )
)
WHERE custody2.transferId IS NULL

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
        ,DATE(rcpt.transferTime) receive_date
        ,NULL external_name 
        ,NULL sex
        ,NULL tissue_origin 
        ,NULL tissue_preparation 
        ,NULL tissue_region 
        ,NULL tube_id 
        ,NULL str_result 
        ,d.groupId group_id 
        ,d.groupDescription group_id_description 
        ,NULL purpose 
        ,NULL qubit_concentration 
        ,NULL nanodrop_concentration 
        ,NULL rin 
        ,NULL dv200 
        ,NULL barcode 
        ,NULL barcode_two 
        ,NULL barcode_kit
        ,NULL umis
        ,NULL qpcr_percentage_human 
        ,qpd.status qcPassed
        ,qpd.description detailedQcStatus
        ,box.locationBarcode boxLocation 
        ,box.alias boxAlias 
        ,pos.position boxPosition 
        ,lib.paired paired 
        ,d.dnaSize read_length 
        ,ts.alias targeted_sequencing 
        ,'Library Aliquot' miso_type 
        ,d.preMigrationId premigration_id 
        ,NULL organism 
        ,NULL subproject
        ,IF(rcpt.excludeFromPinery, NULL, rcpt.institute) institute
        ,NULL slides
        ,NULL discards
        ,NULL stain
        ,NULL slides_consumed
        ,NULL pdac
        ,NULL isSynthetic
        ,NOT ISNULL(dist.recipient) distributed
        ,DATE(dist.transferTime) distribution_date
        ,NULL initial_volume
        ,NULL percent_tumour
        ,NULL percent_necrosis
        ,NULL marked_area_size
        ,NULL marked_area_percent_tumour
        ,NULL reference_slide_id
        ,NULL target_cell_recovery
        ,NULL cell_viability
        ,NULL spike_in
        ,NULL spike_in_dilution_factor
        ,NULL spike_in_volume_ul
        ,NULL sequencing_control_type
        ,custody.recipient AS custody
        ,custody.transferRequestName AS latest_transfer_request
        ,NULL batch_id
FROM LibraryAliquot d 
LEFT JOIN LibraryAliquot laParent ON laParent.aliquotId = d.parentAliquotId
JOIN Library lib ON lib.libraryId = d.libraryId 
JOIN Sample s ON s.sampleId = lib.sample_sampleId
JOIN Project sp ON sp.projectId = s.project_projectId
LEFT JOIN DetailedQcStatus qpd ON qpd.detailedQcStatusId = d.detailedQcStatusId 
JOIN LibraryType lt ON lt.libraryTypeId = lib.libraryType
LEFT JOIN LibraryDesignCode ldc ON ldc.libraryDesignCodeId = d.libraryDesignCodeId
LEFT JOIN TargetedSequencing ts ON d.targetedSequencingId = ts.targetedSequencingId
LEFT JOIN BoxPosition pos ON pos.targetId = d.aliquotId 
        AND pos.targetType = 'LIBRARY_ALIQUOT' 
LEFT JOIN Box box ON box.boxId = pos.boxId
LEFT JOIN (
  SELECT xferla.aliquotId, xferinst.alias institute, xfer.transferTime, xferlab.excludeFromPinery
  FROM Transfer_LibraryAliquot xferla
  JOIN Transfer xfer ON xfer.transferId = xferla.transferId
  JOIN Lab xferlab ON xferlab.labId = xfer.senderLabId
  JOIN Institute xferinst ON xferinst.instituteId = xferlab.instituteId
  WHERE xfer.senderLabId IS NOT NULL
) rcpt ON rcpt.aliquotId = d.aliquotId
LEFT JOIN (
  SELECT xferla.aliquotId, xfer.recipient, xfer.transferTime
  FROM Transfer_LibraryAliquot xferla
  JOIN Transfer xfer ON xfer.transferId = xferla.transferId
  WHERE xfer.recipient IS NOT NULL
) dist ON dist.aliquotId = d.aliquotId
LEFT JOIN (
  SELECT xfer.transferId, xferl.aliquotId, xfer.transferTime, COALESCE(rg.name, xfer.recipient) AS recipient,
    xfer.transferRequestName
  FROM Transfer_LibraryAliquot xferl
  JOIN Transfer xfer ON xfer.transferId = xferl.transferId
  LEFT JOIN _Group rg ON rg.groupId = xfer.recipientGroupId
) custody ON custody.aliquotId = d.aliquotId
LEFT JOIN (
  SELECT xfer.transferId, xferl.aliquotId, xfer.transferTime
  FROM Transfer_LibraryAliquot xferl
  JOIN Transfer xfer ON xfer.transferId = xferl.transferId
) custody2 ON (
  custody2.aliquotId = d.aliquotId
  AND (
    custody.transferTime < custody2.transferTime 
    OR (
      custody.transferTime = custody2.transferTime
      AND custody.transferId < custody2.transferId
    )
  )
)
WHERE custody2.transferId IS NULL
