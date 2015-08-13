package uk.ac.bbsrc.tgac.miso.notification.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

public class IlluminaTransformerTest {
  
  private static final String h1080_84_raw = "/runs/raw/Completed/111110_h1080_0084_AC08UPACXX";
  private static final String h1080_84_gzip = "/runs/gzipped/Completed/111110_h1080_0084_AC08UPACXX";
  private static final String m753_25_running = "/runs/raw/Running/150812_M00753_0025_000000000-AHEN9";
  
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
    // Note: FULL_PATH expected to be different
    assertEqualFields(run1, run2, IlluminaTransformer.JSON_RUN_NAME);
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
  
}
