package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.KitComponent;
import uk.ac.bbsrc.tgac.miso.core.data.KitComponentDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitComponentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitComponentDescriptorStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;

public class SQLKitComponentDAOTest extends AbstractDAOTest {

  @InjectMocks
  private SQLKitComponentDAO dao;

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private NoteStore noteDAO;

  @Mock
  private SecurityStore securityDAO;

  @Mock
  private ChangeLogStore changeLogDAO;

  @Mock
  private KitComponentDescriptorStore kitComponentDescriptorDAO;

  private final User user = new UserImpl();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    user.setUserId(1L);
    when(securityDAO.getUserById(anyLong())).thenReturn(user);
    when(changeLogDAO.listAllById(anyString(), anyLong())).thenReturn(new ArrayList<ChangeLog>());
    KitComponentDescriptor mockComponentDescriptor = Mockito.mock(KitComponentDescriptor.class);
    when(mockComponentDescriptor.getId()).thenReturn(1L);
    when(kitComponentDescriptorDAO.getKitComponentDescriptorById(anyLong())).thenReturn(mockComponentDescriptor);
  }

  @Test
  public void testGet() throws IOException {
    KitComponent kit = dao.get(1L);
    assertIsKit1(kit);
  }

  @Test
  public void testGetNoResults() throws IOException {
    KitComponent kit = dao.lazyGet(666L);
    assertNull(kit);
  }

  @Test
  public void testLazyGet() throws IOException {
    KitComponent kit = dao.get(1L);
    assertIsKit1(kit);
  }

  @Test
  public void testLazyGetNoResults() throws IOException {
    KitComponent kit = dao.lazyGet(1L);
    assertNotNull(kit);
  }

  @Test
  public void testGetKitByIdentificationBarcode() throws IOException {
    KitComponent kit = dao.getKitComponentByIdentificationBarcode("5678");
    assertIsKit2(kit);
  }

  @Test
  public void testGetKitByIdentificationBarcodeNoResults() throws IOException {
    KitComponent kit = dao.getKitComponentByIdentificationBarcode("666");
    assertNull(kit);
  }

  @Test
  public void testListKitComponentsByLocationBarcode() throws IOException {
    List<KitComponent> kits = dao.listKitComponentsByLocationBarcode("Freezer2");
    assertEquals(1, kits.size());
    assertIsKit1(kits.get(0));
  }

  @Test
  public void testGetKitByLotNumber() throws IOException {
    List<KitComponent> kits = dao.listKitComponentsByLotNumber("LOT35");
    if (kits.size() != 1) {
      fail("wrong number of kitComponents returned!");
    }
    KitComponent kit = kits.get(0);
    assertIsKit2(kit);
  }

  @Test
  public void testGetKitByLotNumberNotFound() throws IOException {
    List<KitComponent> kits = dao.listKitComponentsByLotNumber("phantomLOT");
    assertEquals(0, kits.size());
  }

  @Test
  public void testListKitComponentsByReceivedDate() throws IOException {
    assertEquals(2, dao.listKitComponentsByReceivedDate(new LocalDate()).size());
  }

  @Test
  public void testListKitComponentsByExpiryDate() throws IOException {
    assertEquals(2, dao.listKitComponentsByExpiryDate(new LocalDate()).size());
  }

  @Test
  public void testListKitComponentsByExhausted() throws IOException {
    assertEquals(2, dao.listKitComponentsByExhausted(false).size());
  }

  @Test
  public void testListKitComponentsByKitComponentDescriptorId() throws IOException {
    List<KitComponent> kits = dao.listKitComponentsByKitDescriptorId(1L);
    assertEquals(1, kits.size());
    assertIsKit1(kits.get(0));
  }

  @Ignore
  @Test
  public void testListByLibrary() throws IOException {
    dao.listByLibrary(1L);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<KitComponent> kits = dao.listAll();
    assertThat(kits.size(), is(2));
  }

  @Test
  public void testCount() throws IOException {
    assertThat("Count of Kits", dao.count(), is(2));
  }

  @Test
  public void testListByExperiment() throws IOException {
    List<KitComponent> kits = dao.listByExperiment(1L);
    assertThat(kits.size(), is(0));
  }

  @Ignore
  @Test
  public void testListByManufacturer() throws IOException {
    List<KitComponent> kit = dao.listByManufacturer("Roche");
    assertThat(kit.size(), is(2));
  }

  @Ignore
  @Test
  public void testListKitsByType() throws IOException {
    List<KitComponent> kit = dao.listByType(KitType.SEQUENCING);
    assertThat(kit.size(), is(2));
  }

  @Test
  public void testSave() throws IOException {
    KitComponent newKit = makeNewKit();
    assertThat(dao.save(newKit), is(3L));
    KitComponent savedKit = dao.get(3L);
    assertThat(savedKit.getIdentificationBarcode(), is(newKit.getIdentificationBarcode()));
  }

  @Ignore
  @Test
  public void testSaveChangeLog() throws IOException {
    JSONObject json = new JSONObject();
    json.accumulate("userId", 1L);
    json.accumulate("kitComponentId", 1L);
    json.accumulate("locationBarcodeOld", "Freezer2");
    json.accumulate("locationBarcodeNew", "Freezer3");
    json.accumulate("exhausted", false);
    json.accumulate("logDate", new JSONObject().accumulate("time", System.currentTimeMillis()));

    dao.saveChangeLog(json);
    // TODO
  }

  @Ignore
  @Test
  public void testGetChangeLog() throws IOException {
    // TODO
  }

  @Ignore
  @Test
  public void testGetChangeLogByKitComponentId() throws IOException {
    // TODO
  }

  @Test
  public void testIsKitComponentAlreadyLogged() throws IOException {
    assertTrue(dao.isKitComponentAlreadyLogged("1234"));
  }

  @Test
  public void testIsKitComponentAlreadyLoggedNoResults() throws IOException {
    assertFalse(dao.isKitComponentAlreadyLogged("666"));
  }

  @Test
  public void testSaveUpdate() throws IOException {
    KitComponent existingKit = dao.get(1L);
    existingKit.setLotNumber("UPDATED");
    assertThat(dao.save(existingKit), is(1L));
    KitComponent updatedKit = dao.get(1L);
    assertThat(updatedKit.getLotNumber(), is("UPDATED"));
  }

  private KitComponent makeNewKit() throws IOException {
    KitComponent kit = new KitComponentImpl();
    kit.setIdentificationBarcode("KittVsCarr");
    KitDescriptor descriptor = Mockito.mock(KitDescriptor.class);
    KitComponentDescriptor componentDescriptor = Mockito.mock(KitComponentDescriptor.class);
    kit.setKitReceivedDate(new LocalDate());
    kit.setKitExpiryDate(new LocalDate());
    when(descriptor.getId()).thenReturn(1L);
    when(componentDescriptor.getKitDescriptor()).thenReturn(descriptor);

    kit.setKitComponentDescriptor(componentDescriptor);
    return kit;
  }

  private static void assertIsKit1(KitComponent kit) {
    assertNotNull(kit);
    assertEquals(1L, kit.getId());
    assertEquals("1234", kit.getIdentificationBarcode());
    assertEquals("Freezer2", kit.getLocationBarcode());
    assertEquals("LOT34", kit.getLotNumber());
    assertNotNull(kit.getKitReceivedDate());
    assertNotNull(kit.getKitExpiryDate());
    assertFalse(kit.isExhausted());
    assertEquals(1L, kit.getKitComponentDescriptor().getId());
  }

  private static void assertIsKit2(KitComponent kit) {
    assertNotNull(kit);
    assertEquals(2L, kit.getId());
    assertEquals("5678", kit.getIdentificationBarcode());
    assertEquals("Freezer3", kit.getLocationBarcode());
    assertEquals("LOT35", kit.getLotNumber());
    assertNotNull(kit.getKitReceivedDate());
    assertNotNull(kit.getKitExpiryDate());
    assertFalse(kit.isExhausted());
  }
}
