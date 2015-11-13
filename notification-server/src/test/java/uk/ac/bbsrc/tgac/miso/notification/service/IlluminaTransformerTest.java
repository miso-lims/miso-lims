package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class IlluminaTransformerTest {

  private static final String h1080_84_raw = "/runs/raw/Completed/111110_h1080_0084_AC08UPACXX";
  private static final String h1080_84_gzip = "/runs/gzipped/Completed/111110_h1080_0084_AC08UPACXX";
  private static final String h1080_84_nostat = "/runs/raw/Completed/noStatus/111110_h1080_0084_AC08UPACXX";
  private static final String h1080_84_nostat_noparam = "/runs/raw/Completed/noStatus_noParams/111110_h1080_0084_AC08UPACXX";
  private static final String h1080_84_noinfo = "/runs/raw/Completed/noRunInfo/111110_h1080_0084_AC08UPACXX";
  private static final String h1080_84_runcomplete = "/runs/raw/Completed/run.complete/111110_h1080_0084_AC08UPACXX";
  private static final String m753_25_running = "/runs/raw/Running/150812_M00753_0025_000000000-AHEN9";
  private static final String h1179_162_gzip = "/runs/raw/Completed/140108_SN7001179_0162_AC3E41ACXX";
  private static final String h1080_84_lastCycle_viaStatus = "/runs/raw/Completed/lastCycle/viaStatus/111110_h1080_0084_AC08UPACXX";
  private static final String h1179_162_gzip_lastCycle_viaCycleTimes = "/runs/raw/Completed/lastCycle/viaCycleTimes/140108_SN7001179_0162_AC3E41ACXX";
  private static final String h1179_162_gzip_lastCycle_fail = "/runs/raw/Completed/lastCycle/fail/140108_SN7001179_0162_AC3E41ACXX";
  private static final String h1080_84_date_viaCycleTimes = "/runs/raw/Completed/dateCompleted/viaCycleTimes/111110_h1080_0084_AC08UPACXX";
  private static final String h1179_162_date_viaRtaComplete = "/runs/raw/Completed/dateCompleted/viaRTAComplete/140108_SN7001179_0162_AC3E41ACXX";
  private static final String h1080_84_date_viaRtaLog = "/runs/raw/Completed/dateCompleted/viaRTALog/111110_h1080_0084_AC08UPACXX";
  private static final String h1179_162_date_fail = "/runs/raw/Completed/dateCompleted/fail/140108_SN7001179_0162_AC3E41ACXX";
  private static final String h1179_162_minimal_1 = "/runs/raw/Completed/minimal/01/140108_SN7001179_0162_AC3E41ACXX";
  private static final String h1179_162_minimal_2 = "/runs/raw/Completed/minimal/02/140108_SN7001179_0162_AC3E41ACXX";

  private String getResourcePath(String path) {
    return this.getClass().getResource(path).getPath();
  }

  private File getResourceFile(String path) {
    return new File(getResourcePath(path));
  }

  private Map<String, String> transform(String... folders) {
    if (folders.length == 0) throw new IllegalArgumentException("No folder(s) specified.");
    Set<File> files = new HashSet<>();
    for (String folder : folders) {
      files.add(getResourceFile(folder));
    }
    IlluminaTransformer transformer = new IlluminaTransformer();
    return transformer.transform(files);
  }

  private void assertComplete(JSONObject run, boolean statusExpected) throws JSONException {
    assertTrue(run.has(IlluminaTransformer.JSON_RUN_NAME));
    assertTrue(run.has(IlluminaTransformer.JSON_FULL_PATH));
    assertTrue(run.has(IlluminaTransformer.JSON_RUN_INFO));
    assertTrue(run.has(IlluminaTransformer.JSON_RUN_PARAMS));
    if (statusExpected) assertTrue(run.has(IlluminaTransformer.JSON_STATUS));
    assertTrue(run.has(IlluminaTransformer.JSON_SEQUENCER_NAME));
    assertTrue(run.has(IlluminaTransformer.JSON_CONTAINER_ID));
    assertTrue(run.has(IlluminaTransformer.JSON_LANE_COUNT));
    assertTrue(run.has(IlluminaTransformer.JSON_NUM_CYCLES));
    assertTrue(run.has(IlluminaTransformer.JSON_START_DATE));
    assertTrue(run.has(IlluminaTransformer.JSON_COMPLETE_DATE));
    assertNotEquals("null", run.getString(IlluminaTransformer.JSON_COMPLETE_DATE));
  }

  private void assertEqualRuns(JSONObject run1, JSONObject run2) throws JSONException {
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_RUN_NAME);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_FULL_PATH);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_RUN_INFO);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_RUN_PARAMS);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_STATUS);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_SEQUENCER_NAME);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_CONTAINER_ID);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_LANE_COUNT);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_NUM_CYCLES);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_START_DATE);
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_COMPLETE_DATE);
  }

  private void assertEqualFields(JSONObject run1, JSONObject run2, String field) throws JSONException {
    assertEquals(run1.getString(field), run2.getString(field));
  }

  @Test
  public void testTransform1Raw() throws JSONException {
    Map<String, String> map = transform(h1080_84_raw);

    JSONArray completed = new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE));
    assertTrue(completed.length() == 1);

    JSONObject run = (JSONObject) completed.get(0);
    assertComplete(run, true);
    assertTrue("111110_h1080_0084_AC08UPACXX".equals(run.getString(IlluminaTransformer.JSON_RUN_NAME)));
  }

  @Test
  public void testTransform1Gzipped() throws JSONException {
    Map<String, String> map = transform(h1080_84_gzip);

    JSONArray completed = new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE));
    assertTrue(completed.length() == 1);

    JSONObject run = (JSONObject) completed.get(0);
    assertComplete(run, true);
    assertTrue("111110_h1080_0084_AC08UPACXX".equals(run.getString(IlluminaTransformer.JSON_RUN_NAME)));
  }

  @Test
  public void testTransform2Gzipped() throws JSONException {
    Map<String, String> map = transform(h1179_162_gzip);

    JSONArray completed = new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE));
    assertTrue(completed.length() == 1);

    JSONObject run = (JSONObject) completed.get(0);
    assertComplete(run, false);
    assertTrue("140108_SN7001179_0162_AC3E41ACXX".equals(run.getString(IlluminaTransformer.JSON_RUN_NAME)));
  }

  @Test
  public void testRawVsGzipped() throws JSONException {
    Map<String, String> map = transform(h1080_84_raw);
    JSONObject raw = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    map = transform(h1080_84_gzip);
    JSONObject gzipped = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertEqualRuns(raw, gzipped);
  }

  @Test
  public void testRunning() throws JSONException {
    Map<String, String> map = transform(m753_25_running);
    JSONArray running = new JSONArray(map.get(IlluminaTransformer.STATUS_RUNNING));
    assertTrue(running.length() == 1);
  }

  @Test
  public void testStatusMissing() throws JSONException {
    Map<String, String> map = transform(h1080_84_raw);
    JSONObject raw = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    // Should be able to get all the same data, except the status field
    map = transform(h1080_84_nostat);
    JSONObject nostat = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_RUN_NAME);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_RUN_INFO);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_RUN_PARAMS);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_SEQUENCER_NAME);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_CONTAINER_ID);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_LANE_COUNT);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_NUM_CYCLES);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_START_DATE);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_COMPLETE_DATE);
  }

  @Test
  public void testStatusAndParamsMissing() throws JSONException {
    Map<String, String> map = transform(h1080_84_raw);
    JSONObject raw = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    // Should be able to get all the same data, except the status and runParams fields
    map = transform(h1080_84_nostat_noparam);
    JSONObject nostat_noparam = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertEqualFields(raw, nostat_noparam, IlluminaTransformer.JSON_RUN_NAME);
    assertEqualFields(raw, nostat_noparam, IlluminaTransformer.JSON_RUN_INFO);
    assertEqualFields(raw, nostat_noparam, IlluminaTransformer.JSON_SEQUENCER_NAME);
    assertEqualFields(raw, nostat_noparam, IlluminaTransformer.JSON_CONTAINER_ID);
    assertEqualFields(raw, nostat_noparam, IlluminaTransformer.JSON_LANE_COUNT);
    assertEqualFields(raw, nostat_noparam, IlluminaTransformer.JSON_NUM_CYCLES);
    assertEqualFields(raw, nostat_noparam, IlluminaTransformer.JSON_START_DATE);
    assertEqualFields(raw, nostat_noparam, IlluminaTransformer.JSON_COMPLETE_DATE);
  }

  @Test
  public void testRunInfoMissing() throws JSONException {
    Map<String, String> map = transform(h1080_84_raw);
    JSONObject raw = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    // Should be able to get all the same data, except the runInfo and laneCount fields
    map = transform(h1080_84_noinfo);
    JSONObject nostat = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_RUN_NAME);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_STATUS);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_RUN_PARAMS);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_SEQUENCER_NAME);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_CONTAINER_ID);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_NUM_CYCLES);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_START_DATE);
    assertEqualFields(raw, nostat, IlluminaTransformer.JSON_COMPLETE_DATE);
  }

  @Test
  public void testRunCompleteFile() throws JSONException {
    Map<String, String> map = transform(h1080_84_runcomplete);
    assertTrue(new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).length() == 1);
  }

  @Test
  public void testLastCycleViaStatus() throws JSONException {
    Map<String, String> map = transform(h1080_84_lastCycle_viaStatus);
    assertTrue(new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).length() == 1);
  }

  @Test
  public void testLastCycleViaCycleTimes() throws JSONException {
    Map<String, String> map = transform(h1179_162_gzip_lastCycle_viaCycleTimes);
    assertTrue(new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).length() == 1);
  }

  @Test
  public void testLastCycleNotFound() throws JSONException {
    Map<String, String> map = transform(h1179_162_gzip_lastCycle_fail);
    assertTrue(new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).length() == 0);
  }

  @Test
  public void testDateCompletedViaCycleTimes() throws JSONException {
    Map<String, String> map = transform(h1080_84_date_viaCycleTimes);
    JSONObject run = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertTrue(run.has(IlluminaTransformer.JSON_COMPLETE_DATE));
    String date = run.getString(IlluminaTransformer.JSON_COMPLETE_DATE);
    assertTrue(date != null && !date.equals(""));
    assertTrue(!date.equals("null"));
  }

  @Test
  public void testDateCompletedViaRtaComplete() throws JSONException {
    Map<String, String> map = transform(h1179_162_date_viaRtaComplete);
    JSONObject run = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertTrue(run.has(IlluminaTransformer.JSON_COMPLETE_DATE));
    String date = run.getString(IlluminaTransformer.JSON_COMPLETE_DATE);
    assertTrue(date != null && !date.equals(""));
    assertTrue(!date.equals("null"));
  }

  @Test
  public void testDateCompletedViaRtaLog() throws JSONException {
    Map<String, String> map = transform(h1080_84_date_viaRtaLog);
    JSONObject run = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertTrue(run.has(IlluminaTransformer.JSON_COMPLETE_DATE));
    String date = run.getString(IlluminaTransformer.JSON_COMPLETE_DATE);
    assertTrue(date != null && !date.equals(""));
    assertTrue(!date.equals("null"));
  }

  @Test
  public void testDateCompletedFail() throws JSONException {
    Map<String, String> map = transform(h1179_162_date_fail);
    JSONObject run = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertTrue(run.has(IlluminaTransformer.JSON_COMPLETE_DATE));
    String date = run.getString(IlluminaTransformer.JSON_COMPLETE_DATE);
    assertTrue(date != null && !date.equals(""));
    assertTrue(date.equals("null"));
  }

  @Test
  public void testMinimalFileSet1() throws JSONException {
    Map<String, String> map = transform(h1179_162_minimal_1);
    JSONObject run = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertComplete(run, false);
  }

  @Test
  public void testMinimalFileSet2() throws JSONException {
    Map<String, String> map = transform(h1179_162_minimal_2);
    JSONObject run = (JSONObject) new JSONArray(map.get(IlluminaTransformer.STATUS_COMPLETE)).get(0);

    assertComplete(run, false);
  }

}
