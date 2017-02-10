package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateChangeLogDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateKitDao;

public class SQLKitDAOTest extends AbstractDAOTest {

  @InjectMocks
  private HibernateKitDao dao;

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private HibernateChangeLogDao changeLogDAO;

  private final User user = new UserImpl();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    user.setUserId(1L);
    when(changeLogDAO.listAllById(anyString(), anyLong())).thenReturn(new ArrayList<ChangeLog>());
  }

  @Test
  public void testGet() throws IOException {
    Kit kit = dao.get(1L);
    assertThat(kit.getLocationBarcode(), is("Freezer2"));
  }

  @Test
  public void testGetKitByIdentificationBarcode() throws IOException {
    Kit kit = dao.getKitByIdentificationBarcode("5678");
    assertThat(kit.getLotNumber(), is("LOT35"));
  }

  @Test
  public void testGetKitByLotNumber() throws IOException {
    Kit kit = dao.getKitByLotNumber("LOT35");
    assertThat(kit.getIdentificationBarcode(), is("5678"));
  }

  @Test
  public void testGetKitByLotNumberNotFound() throws IOException {
    Kit kit = dao.getKitByLotNumber("phantomLOT");
    assertNull(kit);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<Kit> kits = dao.listAll();
    assertThat(kits.size(), is(2));
  }

  @Test
  public void testCount() throws IOException {
    assertThat("Count of Kits", dao.count(), is(2));
  }

  @Test
  public void testListKitsByType() throws IOException {
    List<Kit> kit = dao.listKitsByType(KitType.SEQUENCING);
    assertThat(kit.size(), is(2));
  }

  @Test
  public void testSave() throws IOException {
    Kit newKit = makeNewKit();
    assertThat(dao.save(newKit), is(3L));
    Kit savedKit = dao.get(3L);
    assertThat(savedKit.getIdentificationBarcode(), is(newKit.getIdentificationBarcode()));
  }

  @Test
  public void testSaveUpdate() throws IOException {
    Kit existingKit = dao.get(1L);
    existingKit.setLotNumber("UPDATED");
    assertThat(dao.save(existingKit), is(1L));
    Kit updatedKit = dao.get(1L);
    assertThat(updatedKit.getLotNumber(), is("UPDATED"));
  }

  private Kit makeNewKit() throws IOException {
    Kit kit = new KitImpl();
    kit.setIdentificationBarcode("KittVsCarr");
    KitDescriptor kitDescriptor = dao.getKitDescriptorById(1L);
    kit.setKitDescriptor(kitDescriptor);
    return kit;
  }

  @Test
  public void testGetKitDescriptorById() throws IOException {
    KitDescriptor kitDescriptor = dao.getKitDescriptorById(1L);
    assertThat(kitDescriptor.getName(), is("GS Titanium Sequencing Kit XLR70"));
  }

  @Test
  public void testGetKitDescriptorByIdNotFound() throws IOException {
    KitDescriptor kitDescriptor = dao.getKitDescriptorById(9999L);
    assertNull(kitDescriptor);
  }

  @Test
  public void testGetKitDescriptorByPartNumber() throws IOException {
    KitDescriptor kitDescriptor = dao.getKitDescriptorByPartNumber("05233526001");
    assertThat(kitDescriptor.getName(), is("GS Titanium Sequencing Kit XLR70"));
  }

  @Test
  public void testGetKitDescriptorByPartNumberNotFound() throws IOException {
    KitDescriptor kitDescriptor = dao.getKitDescriptorByPartNumber("doesnotexist");
    assertNull(kitDescriptor);
  }

  @Test
  public void testListAllKitDescriptors() throws IOException {
    List<KitDescriptor> kitDescriptors = dao.listAllKitDescriptors();
    assertThat(kitDescriptors.size(), not(0));
  }

  @Test
  public void testListKitDescriptorsByType() throws IOException {
    List<KitDescriptor> kitDescriptors = dao.listKitDescriptorsByType(KitType.LIBRARY);
    assertThat(kitDescriptors.size(), not(0));
  }

  @Test
  public void testSaveKitDescriptor() throws IOException {
    KitDescriptor newKitDescriptor = makeNewKitDescriptor();
    newKitDescriptor.setLastModifier(user);
    long id = dao.saveKitDescriptor(newKitDescriptor);
    assertThat(id, not(0L));
    KitDescriptor savedKitDescriptor = dao.getKitDescriptorById(id);
    assertThat(newKitDescriptor.getName(), is(savedKitDescriptor.getName()));
  }

  @Test
  public void testSaveKitDescriptorUpdate() throws IOException {
    KitDescriptor existingKitDescriptor = dao.getKitDescriptorById(1L);
    existingKitDescriptor.setName("UPDATED");
    assertThat(dao.saveKitDescriptor(existingKitDescriptor), is(1L));
    KitDescriptor updatedKitDescriptor = dao.getKitDescriptorById(1L);
    assertThat(updatedKitDescriptor.getName(), is("UPDATED"));
  }

  private KitDescriptor makeNewKitDescriptor() {
    KitDescriptor kitDescriptor = new KitDescriptor();
    kitDescriptor.setKitType(KitType.LIBRARY);
    kitDescriptor.setPlatformType(PlatformType.ILLUMINA);
    kitDescriptor.setName("FUNNYKITTY");
    return kitDescriptor;
  }

  @Test
  public void testGetKitDescriptorColumnSizes() throws IOException {
    Map<String, Integer> columnSizes = dao.getKitDescriptorColumnSizes();
    assertThat(columnSizes, hasEntry("name", 255));
  }

}
