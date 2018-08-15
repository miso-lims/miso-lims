package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
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
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
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
    assertEquals(sample.getId(), saveBox.getValue().getBoxPositions().get("A01").getBoxableId());
    assertEquals(library.getId(), saveBox.getValue().getBoxPositions().get("A02").getBoxableId());
  }

  @Test
  public void testRemoveTubeFromBox() throws Exception {
    Box box = makeEmptyBox();
    BoxableView sample = makeSampleView();
    box.getBoxPositions().put("A01", new BoxPosition(box, "A01", sample.getId()));
    when(boxService.get(box.getId())).thenReturn(box);

    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());
    json.put("position", "A01");

    assertNotNull(box.getBoxPositions().get("A01"));

    JSONObject response = boxControllerHelperService.removeItemFromBox(null, json);
    assertFalse(response.has("error"));
    assertTrue(response.has("boxJSON"));

    ArgumentCaptor<Box> saveBox = ArgumentCaptor.forClass(Box.class);
    verify(boxService).save(saveBox.capture());
    assertNull(saveBox.getValue().getBoxPositions().get("A01"));
  }

  @Test
  public void testEmptySingleTube() throws Exception {
    Box box = makeEmptyBox();
    BoxableView sample = makeSampleView();
    box.getBoxPositions().put("A01", new BoxPosition(box, "A01", sample.getId()));
    when(boxService.get(box.getId())).thenReturn(box);

    JSONObject json = new JSONObject();
    json.put("boxId", box.getId());
    json.put("position", "A01");

    assertNotNull(box.getBoxPositions().get("A01"));
    assertFalse(sample.isDiscarded());

    JSONObject response = boxControllerHelperService.discardSingleItem(null, json);
    assertFalse(response.has("error"));
    assertTrue(response.has("boxJSON"));
    verify(boxService).discardSingleItem(box, "A01");
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
