-- test Pinery stored procedures

CALL queryAllModels();
CALL queryModelById(1);
CALL queryAllInstruments();
CALL queryInstrumentById(1);
CALL queryInstrumentsByModelId(1);
CALL queryAllOrders();
CALL queryOrderById(1);
CALL queryAllOrderSamples();
CALL queryOrderSamplesByOrderId(1);
CALL queryAllUsers();
CALL queryUserById(1);
CALL queryAllRuns();
CALL queryRunById(1);
CALL queryRunByName('Non_existent');
CALL queryAllRunPositions();
CALL queryRunPositionsByRunId(1);
CALL queryAllRunSamples();
CALL queryRunSamplesByRunId(1);
CALL queryAllSamples();
CALL querySampleById(1);
CALL querySampleChildIdsBySampleId('SAM1');
CALL queryAllSampleTypes();
CALL queryAllSampleProjects();
CALL queryAllSampleChangeLogs();
CALL querySampleChangeLogById(1);