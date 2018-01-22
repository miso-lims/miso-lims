SELECT DISTINCT r.alias
  ,r.instrumentId AS instrumentId
  ,r.runId
  ,r.filePath
  ,r.health
  ,r.startDate
  ,r.completionDate
  ,spc.identificationBarcode
  ,createLog.userId
  ,createLog.changeTime
  ,updateLog.userId
  ,updateLog.changeTime
  ,sp.paired AS paired
  ,sp.readLength AS read_length
FROM Run AS r  
LEFT JOIN SequencingParameters AS sp ON sp.parametersId = r.sequencingParameters_parametersId 
LEFT JOIN Run_SequencerPartitionContainer AS rscp ON rscp.Run_runId = r.runId 
LEFT JOIN SequencerPartitionContainer AS spc ON spc.containerId = rscp.containers_containerId 
LEFT JOIN RunChangeLog AS createLog ON createLog.runId = r.runId 
LEFT JOIN RunChangeLog AS rcl1 ON rcl1.runId = createLog.runId AND createLog.changeTime > rcl1.changeTime 
LEFT JOIN RunChangeLog AS updateLog ON updateLog.runId = r.runId 
LEFT JOIN RunChangeLog AS rcl2 ON rcl2.runId = updateLog.runId AND updateLog.changeTime < rcl2.changeTime 
WHERE rcl1.runId IS NULL AND rcl2.runId IS NULL
