package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScan;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.spring.ajax.BoxControllerHelperService;

public class BoxControllerHelperServiceTestSuite {

  @InjectMocks
  private BoxControllerHelperService boxControllerHelperService;

  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Mock
  private SecurityManager securityManager;
  @Mock
  private AuthorizationManager authorizationManager;
  @Mock 
  private RequestManager requestManager;
  @Mock
  private MisoFilesManager misoFileManager;
  @Mock
  private User mockUser;
  @Mock
  private Box mockBox;
  @Mock 
  private Sample mockSample;
  @Mock
  private Library mockLibrary;
  @Mock 
  private Authentication authentication;
  @Mock 
  private Boxable boxable;
  @Mock
  private BoxScanner boxScanner;
  @Mock 
  private VisionMateScan scan;
  @Mock
  private File file;
  
  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(authorizationManager.getCurrentUser()).thenReturn(mockUser);
  }
  
  @Test
  public final void testDeleteBox() throws Exception {
    final long id = 1L;
    when(requestManager.getBoxById(anyLong())).thenReturn(mockBox);
    final JSONObject json = new JSONObject();
    json.put("boxId", id);
    final JSONObject response = boxControllerHelperService.deleteBox(null, json);
    
    verify(requestManager).deleteBox(requestManager.getBoxById(id));
    
    assertEquals("Box+deleted", response.get("response"));
  }
  
  @Test
  public final void testDeleteBoxBadJson() throws Exception {
    when(requestManager.getBoxById(anyLong())).thenReturn(mockBox);
    final JSONObject json = new JSONObject();
    
    final JSONObject response1 = boxControllerHelperService.deleteBox(null, json);
    assertTrue(response1.has("error"));
    assertFalse(response1.has("response"));
    
    json.put("boxId", "box");
    exception.expect(JSONException.class);
    boxControllerHelperService.deleteBox(null, json);
  }
  
  @Test
  public final void testGetBoxScanWithReadErrorsReturnsError() throws Exception {
    final long id = 1L;
    final JSONObject error = new JSONObject();
    final JSONObject errorComponents = new JSONObject();
    errorComponents.put("errorPositions", "[]");
    errorComponents.put("successPositions", "[]");
    errorComponents.put("message", "The scanner can not read some positions. Please remove or fix and then rescan: ");
    errorComponents.put("type", "Read Error");
    error.put("errors", errorComponents);
    
    when(boxScanner.getScan()).thenReturn(scan);
    when(scan.hasReadErrors()).thenReturn(true);
    
    JSONObject json = new JSONObject();
    json.put("boxId", id);
    assertEquals(error, boxControllerHelperService.getBoxScan(null, json));
  }
  
  @Test
  public final void testExportBoxContentsFormNoBoxId() throws Exception {
    when(requestManager.getBoxById(anyLong())).thenReturn(mockBox);
    
    final JSONObject json = new JSONObject();
    json.put("boxId", null);
    
    final JSONObject response = boxControllerHelperService.exportBoxContentsForm(null, json);
    
    verify(misoFileManager, never()).getNewFile(any(Class.class), any(String.class), any(String.class));
    
    assertEquals("Missing+boxId", response.get("error"));
  }
  
  @Test
  public final void testExportBoxContentsFormError() throws Exception {
    final long id = 1L;
    final Exception error = new IOException("thrown by mock");
    ArrayList<String> array = new ArrayList<String>();
    array.add("a:a:a");
    array.add("b:b:b");
    array.add("c:c:c");
    when(requestManager.getBoxById(anyLong())).thenThrow(error);
    
    final JSONObject json = new JSONObject();
    json.put("boxId", id);
    
    JSONObject response = boxControllerHelperService.exportBoxContentsForm(null, json);
    assertTrue(response.has("error"));
  }
  
  @Test
  public void testSaveBoxContents() throws Exception {
    // mock lookups
    Sample sample = makeSample();
    when(requestManager.getSampleByBarcode(sample.getIdentificationBarcode())).thenReturn(sample);
    
    Library library = makeLibrary();
    when(requestManager.getLibraryByBarcode(library.getIdentificationBarcode())).thenReturn(library);
    
    // do not add sample/library to box. Testing verifies that this gets done by saveBoxContents by parsing the JSON
    Box box = makeEmptyBox();
    when(requestManager.getBoxById(box.getId())).thenReturn(box);
    
    // Create JSON: 
    // {"boxJSON":{
    //   "id":1,
    //   "boxables":{
    //     "A01":{"identificationBarcode":"1111"},
    //     "A02":{"identificationBarcode":"2222"}
    //   }
    // }}
    JSONObject json = new JSONObject();
    JSONObject boxJson = new JSONObject();
    JSONObject boxablesJson = new JSONObject();
    JSONObject boxable1Json = new JSONObject();
    boxable1Json.put("identificationBarcode", sample.getIdentificationBarcode());
    boxablesJson.put("A01", boxable1Json);
    JSONObject boxable2Json = new JSONObject();
    boxable2Json.put("identificationBarcode", library.getIdentificationBarcode());
    boxablesJson.put("A02", boxable2Json);
    boxJson.put("boxables", boxablesJson);
    boxJson.put("id", box.getId());
    json.put("boxJSON", boxJson);
    
    JSONObject response = boxControllerHelperService.saveBoxContents(null, json);
    assertTrue(response.has("response"));
    assertFalse(response.has("error"));
    ArgumentCaptor<Box> saveBox = ArgumentCaptor.forClass(Box.class);
    verify(requestManager).saveBox(saveBox.capture());
    assertEquals(sample.getIdentificationBarcode(), saveBox.getValue().getBoxable("A01").getIdentificationBarcode());
    assertEquals(library.getIdentificationBarcode(), saveBox.getValue().getBoxable("A02").getIdentificationBarcode());
  }
  
  @Test
  public void testUpdateOneItem() throws Exception {
    Box box = makeEmptyBox();
    when(requestManager.getBoxById(box.getId())).thenReturn(box);
    
    Sample sample = makeSample();
    when(requestManager.getSampleByBarcode(sample.getIdentificationBarcode())).thenReturn(sample);
    
    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());
    json.put("position", "A01");
    json.put("barcode", sample.getIdentificationBarcode());
    
    JSONObject response = boxControllerHelperService.updateOneItem(null, json);
    assertTrue(response.has("boxJSON"));
    assertTrue(response.has("addedToBox"));
    assertFalse(response.has("error"));
    
    ArgumentCaptor<Box> saveBox = ArgumentCaptor.forClass(Box.class);
    verify(requestManager).saveBox(saveBox.capture());
    assertEquals(sample.getIdentificationBarcode(), saveBox.getValue().getBoxable("A01").getIdentificationBarcode());
  }
  
  @Test
  public void testRemoveTubeFromBox() throws Exception {
    Box box = makeEmptyBox();
    Sample sample = makeSample();
    box.setBoxable("A01", sample);
    when(requestManager.getBoxById(box.getId())).thenReturn(box);
    
    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());
    json.put("position", "A01");
    
    assertNotNull(box.getBoxable("A01"));
    
    JSONObject response = boxControllerHelperService.removeTubeFromBox(null, json);
    assertFalse(response.has("error"));
    assertTrue(response.has("boxJSON"));
    
    ArgumentCaptor<Box> saveBox = ArgumentCaptor.forClass(Box.class);
    verify(requestManager).saveBox(saveBox.capture());
    assertNull(saveBox.getValue().getBoxable("A01"));
  }
  
  @Test
  public void testEmptySingleTube() throws Exception {
    Box box = makeEmptyBox();
    Sample sample = makeSample();
    box.setBoxable("A01", sample);
    when(requestManager.getBoxById(box.getId())).thenReturn(box);
    
    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());
    json.put("position", "A01");
    
    assertNotNull(box.getBoxable("A01"));
    assertFalse(sample.isDiscarded());
    
    JSONObject response = boxControllerHelperService.emptySingleTube(null, json);
    assertFalse(response.has("error"));
    assertTrue(response.has("boxJSON"));
    verify(requestManager).emptySingleTube(box, "A01");
  }
  
  @Test
  public void testListAllBoxesTable() throws Exception {
    Box box = makeEmptyBox();
    Collection<Box> boxes = new HashSet<>();
    boxes.add(box);
    when(requestManager.listAllBoxes()).thenReturn(boxes);
    
    JSONObject response = boxControllerHelperService.listAllBoxesTable(null, null);
    assertTrue(response.has("array"));
    JSONArray boxesJson = response.getJSONArray("array");
    assertEquals(1, boxesJson.size());
    JSONArray boxJson = boxesJson.getJSONArray(0);
    // should contain 8 fields
    assertEquals(8, boxJson.size());
  }
  
  @Test
  public void testGetBoxableByBarcode() throws Exception {
    Sample sample = makeSample();
    when(requestManager.getSampleByBarcode(sample.getIdentificationBarcode())).thenReturn(sample);
    Library library = makeLibrary();
    when(requestManager.getLibraryByBarcode(library.getIdentificationBarcode())).thenReturn(library);
    
    // valid lookups
    assertEquals(sample, boxControllerHelperService.getBoxableByBarcode(sample.getIdentificationBarcode()));
    assertEquals(library, boxControllerHelperService.getBoxableByBarcode(library.getIdentificationBarcode()));
    
    // invalid - barcode is not unique
    library.setIdentificationBarcode(sample.getIdentificationBarcode());
    when(requestManager.getLibraryByBarcode(library.getIdentificationBarcode())).thenReturn(library);
    
    exception.expect(DuplicateKeyException.class);
    boxControllerHelperService.getBoxableByBarcode(sample.getIdentificationBarcode());
  }
  
  @Test
  public void testLookupBoxableByBarcode() throws Exception {
    Sample sample = makeSample();
    when(requestManager.getSampleByBarcode(sample.getIdentificationBarcode())).thenReturn(sample);
    
    JSONObject json = new JSONObject();
    json.put("barcode", sample.getIdentificationBarcode());
    
    JSONObject response = boxControllerHelperService.lookupBoxableByBarcode(null, json);
    assertFalse(response.has("error"));
    assertTrue(response.has("boxable"));
    assertTrue(response.getJSONObject("boxable").has("alias"));
    assertEquals(sample.getAlias(), response.getJSONObject("boxable").getString("alias"));
  }
  
  @Test
  public void testLookupBoxableByBarcodeTrashed() throws Exception {
    Sample sample = makeSample();
    sample.setDiscarded(true);
    when(requestManager.getSampleByBarcode(sample.getIdentificationBarcode())).thenReturn(sample);
    
    JSONObject json = new JSONObject();
    json.put("barcode", sample.getIdentificationBarcode());
    
    JSONObject response = boxControllerHelperService.lookupBoxableByBarcode(null, json);
    assertFalse(response.has("error"));
    assertTrue(response.has("boxable"));
    assertTrue(response.has("trashed"));
  }
  
  @Test
  public void testLookupBoxableByBarcodeDuplicate() throws Exception {
    Sample sample = makeSample();
    when(requestManager.getSampleByBarcode(sample.getIdentificationBarcode())).thenReturn(sample);
    Library library = makeLibrary();
    library.setIdentificationBarcode(sample.getIdentificationBarcode());
    when(requestManager.getLibraryByBarcode(library.getIdentificationBarcode())).thenReturn(library);
    
    JSONObject json = new JSONObject();
    json.put("barcode", sample.getIdentificationBarcode());
    
    JSONObject response = boxControllerHelperService.lookupBoxableByBarcode(null, json);
    assertTrue(response.has("error"));
  }
  
  @Test
  public void testLookupBoxableByBarcodeNotFound() throws Exception {
    Sample sample = makeSample();
    
    JSONObject json = new JSONObject();
    json.put("barcode", sample.getIdentificationBarcode());
    
    JSONObject response = boxControllerHelperService.lookupBoxableByBarcode(null, json);
    assertTrue(response.has("error"));
  }
  
  @Test
  public void testEmptyEntireBox() throws Exception {
    Box box = makeEmptyBox();
    Sample sample = makeSample();
    box.setBoxable("A01", sample);
    Library library = makeLibrary();
    box.setBoxable("A02", library);
    assertEquals(2, box.getTubeCount());
    assertFalse(sample.isDiscarded());
    assertFalse(library.isDiscarded());
    when(requestManager.getBoxById(box.getId())).thenReturn(box);
    
    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());
    
    User user = new UserImpl();
    user.setAdmin(true);
    when(authorizationManager.getCurrentUser()).thenReturn(user);
    
    JSONObject response = boxControllerHelperService.emptyEntireBox(null, json);
    System.out.println(response.toString(2));
    assertFalse(response.has("error"));
    assertTrue(response.has("boxJSON"));
    verify(requestManager).emptyAllTubes(box);
    // box DAO is responsible for actually emptying and removing the tubes
  }
  
  private Sample makeSample() {
    Sample sample = new SampleImpl();
    sample.setId(1L);
    sample.setAlias("sample");
    sample.setIdentificationBarcode("1111");
    sample.setBoxPositionId(1L);
    sample.setDiscarded(false);
    return sample;
  }
  
  private Library makeLibrary() {
    Library library = new LibraryImpl();
    library.setId(1L);
    library.setAlias("library");
    library.setIdentificationBarcode("2222");
    library.setBoxPositionId(2L);
    library.setDiscarded(false);
    return library;
  }
  
  private Box makeEmptyBox() {
    Box box = new BoxImpl();
    box.setId(1L);
    BoxSize size = new BoxSize();
    size.setRows(8);
    size.setColumns(12);
    box.setSize(size);
    BoxUse use = new BoxUse();
    use.setAlias("use");
    box.setUse(use);
    box.setLocationBarcode("freezer");
    return box;
  }
}
