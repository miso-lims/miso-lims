package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

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
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScan;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.spring.ajax.BoxControllerHelperService;

public class BoxControllerHelperServiceTest {

  @InjectMocks
  private BoxControllerHelperService boxControllerHelperService;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Mock
  private SecurityManager securityManager;
  @Mock
  private AuthorizationManager authorizationManager;
  @Mock
  private BoxService boxService;
  @Mock
  private MisoFilesManager misoFileManager;
  @Mock
  private LibraryService libraryService;
  @Mock
  private LibraryDilutionService libraryDilutionService;
  @Mock
  private SampleService sampleService;
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
    mockUser = new UserImpl();
    mockUser.setUserId(1L);
    when(authorizationManager.getCurrentUser()).thenReturn(mockUser);
  }

  @Test
  public final void testDeleteBox() throws Exception {
    final long id = 1L;
    when(boxService.get(anyLong())).thenReturn(mockBox);
    final JSONObject json = new JSONObject();
    json.put("boxId", id);
    final JSONObject response = boxControllerHelperService.deleteBox(null, json);

    verify(boxService).deleteBox(boxService.get(id));

    assertEquals("Box+deleted", response.get("response"));
  }

  @Test
  public final void testDeleteBoxBadJson() throws Exception {
    when(boxService.get(anyLong())).thenReturn(mockBox);
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

    Box box = makeEmptyBox();
    when(scan.getRowCount()).thenReturn(box.getSize().getRows());
    when(scan.getColumnCount()).thenReturn(box.getSize().getColumns());
    when(boxScanner.getScan()).thenReturn(scan);
    when(scan.getReadErrorPositions()).thenReturn(Arrays.asList("A01"));
    when(boxService.get(1L)).thenReturn(box);

    JSONObject json = new JSONObject();
    json.put("boxId", id);
    assertTrue(boxControllerHelperService.getBoxScan(null, json).getJSONArray("errors").size() > 0);
  }

  @Test
  public final void testExportBoxContentsFormNoBoxId() throws Exception {
    when(boxService.get(anyLong())).thenReturn(mockBox);

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
    ArrayList<String> array = new ArrayList<>();
    array.add("a:a:a");
    array.add("b:b:b");
    array.add("c:c:c");
    when(boxService.get(anyLong())).thenThrow(error);

    final JSONObject json = new JSONObject();
    json.put("boxId", id);

    JSONObject response = boxControllerHelperService.exportBoxContentsForm(null, json);
    assertTrue(response.has("error"));
  }

  @Test
  public void testSaveBoxContents() throws Exception {
    // mock lookups
    BoxableView sample = makeSampleView();
    BoxableView library = makeLibraryView();
    when(boxService.getViewsFromBarcodeList(Mockito.any())).thenReturn(Arrays.asList(sample, library));

    // do not add sample/library to box. Testing verifies that this gets done by saveBoxContents by parsing the JSON
    Box box = makeEmptyBox();
    when(boxService.get(box.getId())).thenReturn(box);

    // Create JSON:
    // {"boxId":1,
    // "items":[ { "coordinates": "A01", "identificationBarcode":"1111"}, { "coordinates": "A02", "identificationBarcode":"2222"} ]
    // }
    JSONObject json = new JSONObject();
    JSONArray boxablesJson = new JSONArray();
    JSONObject boxable1Json = new JSONObject();
    boxable1Json.put("identificationBarcode", sample.getIdentificationBarcode());
    boxable1Json.put("coordinates", "A01");
    boxablesJson.add(boxable1Json);
    JSONObject boxable2Json = new JSONObject();
    boxable2Json.put("identificationBarcode", library.getIdentificationBarcode());
    boxable2Json.put("coordinates", "A02");
    boxablesJson.add(boxable2Json);
    json.put("items", boxablesJson);
    json.put("boxId", box.getId());

    JSONObject response = boxControllerHelperService.saveBoxContents(null, json);
    assertTrue(response.has("response"));
    assertFalse(response.has("error"));
    ArgumentCaptor<Box> saveBox = ArgumentCaptor.forClass(Box.class);
    verify(boxService).save(saveBox.capture());
    assertEquals(sample.getIdentificationBarcode(), saveBox.getValue().getBoxable("A01").getIdentificationBarcode());
    assertEquals(library.getIdentificationBarcode(), saveBox.getValue().getBoxable("A02").getIdentificationBarcode());
  }

  @Test
  public void testUpdateOneItem() throws Exception {
    Box box = makeEmptyBox();
    when(boxService.get(box.getId())).thenReturn(box);

    BoxableView sample = makeSampleView();
    when(boxService.getViewByBarcode(Mockito.any())).thenReturn(sample);

    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());
    json.put("position", "A01");
    json.put("barcode", sample.getIdentificationBarcode());

    JSONObject response = boxControllerHelperService.updateOneItem(null, json);
    assertFalse(response.has("error"));
    assertTrue(response.has("boxJSON"));
    assertTrue(response.has("addedToBox"));

    ArgumentCaptor<Box> saveBox = ArgumentCaptor.forClass(Box.class);
    verify(boxService).save(saveBox.capture());
    assertEquals(sample.getIdentificationBarcode(), saveBox.getValue().getBoxable("A01").getIdentificationBarcode());
  }

  @Test
  public void testRemoveTubeFromBox() throws Exception {
    Box box = makeEmptyBox();
    BoxableView sample = makeSampleView();
    box.setBoxable("A01", sample);
    when(boxService.get(box.getId())).thenReturn(box);

    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());
    json.put("position", "A01");

    assertNotNull(box.getBoxable("A01"));

    JSONObject response = boxControllerHelperService.removeTubeFromBox(null, json);
    assertFalse(response.has("error"));
    assertTrue(response.has("boxJSON"));

    ArgumentCaptor<Box> saveBox = ArgumentCaptor.forClass(Box.class);
    verify(boxService).save(saveBox.capture());
    assertNull(saveBox.getValue().getBoxable("A01"));
  }

  @Test
  public void testEmptySingleTube() throws Exception {
    Box box = makeEmptyBox();
    BoxableView sample = makeSampleView();
    box.setBoxable("A01", sample);
    when(boxService.get(box.getId())).thenReturn(box);

    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());
    json.put("position", "A01");

    assertNotNull(box.getBoxable("A01"));
    assertFalse(sample.isDiscarded());

    JSONObject response = boxControllerHelperService.discardSingleTube(null, json);
    assertFalse(response.has("error"));
    assertTrue(response.has("boxJSON"));
    verify(boxService).discardSingleTube(box, "A01");
  }

  @Test
  public void testLookupBoxableByBarcode() throws Exception {
    BoxableView sample = makeSampleView();
    when(boxService.getViewByBarcode(Mockito.any())).thenReturn(sample);

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
    BoxableView sample = makeSampleView();
    sample.setDiscarded(true);
    when(boxService.getViewByBarcode(Mockito.any())).thenReturn(sample);

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
    when(sampleService.getByBarcode(sample.getIdentificationBarcode())).thenReturn(sample);
    Library library = makeLibrary();
    library.setIdentificationBarcode(sample.getIdentificationBarcode());
    when(libraryService.getByBarcode(library.getIdentificationBarcode())).thenReturn(library);

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
    BoxableView sample = makeSampleView();
    box.setBoxable("A01", sample);
    BoxableView library = makeLibraryView();
    box.setBoxable("A02", library);
    assertEquals(2, box.getTubeCount());
    assertFalse(sample.isDiscarded());
    assertFalse(library.isDiscarded());
    when(boxService.get(box.getId())).thenReturn(box);

    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());

    User user = new UserImpl();
    user.setAdmin(true);
    when(authorizationManager.getCurrentUser()).thenReturn(user);

    JSONObject response = boxControllerHelperService.discardEntireBox(null, json);
    System.out.println(response.toString(2));
    assertFalse(response.has("error"));
    assertTrue(response.has("boxJSON"));
    verify(boxService).discardAllTubes(box);
    // box DAO is responsible for actually emptying and removing the tubes
  }

  private static Sample makeSample() {
    Sample sample = new SampleImpl();
    sample.setId(1L);
    sample.setAlias("sample");
    sample.setIdentificationBarcode("1111");
    sample.setDiscarded(false);
    return sample;
  }

  private static BoxableView makeSampleView() {
    return makeBoxable(makeSample());
  }

  private static Library makeLibrary() {
    Library library = new LibraryImpl();
    library.setId(1L);
    library.setAlias("library");
    library.setIdentificationBarcode("2222");
    library.setDiscarded(false);
    return library;
  }

  private static BoxableView makeLibraryView() {
    return makeBoxable(makeLibrary());
  }

  private static BoxableView makeBoxable(Boxable boxable) {
    BoxableView v = new BoxableView();
    BoxableId id = new BoxableId();
    id.setTargetId(boxable.getId());
    id.setTargetType(boxable.getEntityType());
    v.setId(id);
    v.setName(boxable.getName());
    v.setAlias(boxable.getAlias());
    v.setIdentificationBarcode(boxable.getIdentificationBarcode());
    v.setLocationBarcode(boxable.getLocationBarcode());
    v.setVolume(boxable.getVolume());
    v.setDiscarded(boxable.isDiscarded());
    return v;
  }

  private Box makeEmptyBox() {
    Box box = new BoxImpl();
    box.setId(1L);
    box.setAlias("box");
    BoxSize size = new BoxSize();
    size.setRows(8);
    size.setColumns(12);
    box.setSize(size);
    BoxUse use = new BoxUse();
    use.setAlias("use");
    box.setUse(use);
    box.setLocationBarcode("freezer");
    User user = new UserImpl();
    user.setUserId(1L);
    box.setLastModifier(user);
    return box;
  }
}
