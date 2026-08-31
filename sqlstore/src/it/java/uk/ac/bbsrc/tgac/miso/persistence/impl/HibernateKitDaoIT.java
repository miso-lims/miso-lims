package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class HibernateKitDaoIT extends AbstractDAOTest {

  private AutoCloseable mockito;

  @InjectMocks
  private HibernateKitDao dao;

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @PersistenceContext
  private EntityManager entityManager;

  @Mock
  private HibernateChangeLogDao changeLogDAO;

  private final User user = new UserImpl();

  @BeforeEach
  public void setUp() throws Exception {
    mockito = MockitoAnnotations.openMocks(this);
    dao.setEntityManager(entityManager);
    user.setId(1L);
  }

  @AfterEach
  public void cleanUp() throws Exception {
    mockito.close();
  }

  @Test
  public void testGet() throws IOException {
    Kit kit = dao.get(1L);
    assertEquals("Freezer2", kit.getLocationBarcode());
  }

  @Test
  public void testGetKitByLotNumber() throws IOException {
    Kit kit = dao.getKitByLotNumber("LOT35");
    assertEquals("5678", kit.getIdentificationBarcode());
  }

  @Test
  public void testGetKitByLotNumberNotFound() throws IOException {
    Kit kit = dao.getKitByLotNumber("phantomLOT");
    assertNull(kit);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<Kit> kits = dao.listAll();
    assertEquals(2, kits.size());
  }

  @Test
  public void testSave() throws IOException {
    Kit newKit = makeNewKit();
    long savedId = dao.save(newKit);

    clearSession();

    Kit savedKit = dao.get(savedId);
    assertEquals(newKit.getIdentificationBarcode(), savedKit.getIdentificationBarcode());
  }

  @Test
  public void testSaveUpdate() throws IOException {
    Kit existingKit = dao.get(1L);
    existingKit.setLotNumber("UPDATED");
    assertEquals(1L, dao.save(existingKit));

    clearSession();

    Kit updatedKit = dao.get(1L);
    assertEquals("UPDATED", updatedKit.getLotNumber());
  }

  private Kit makeNewKit() throws IOException {
    Kit kit = new KitImpl();
    kit.setIdentificationBarcode("KittVsCarr");
    KitDescriptor kitDescriptor = dao.getKitDescriptorById(1L);
    kit.setKitDescriptor(kitDescriptor);
    kit.setKitDate(LocalDate.now(ZoneId.systemDefault()));
    kit.setLotNumber("ABCD1234");
    return kit;
  }

  @Test
  public void testGetKitDescriptorById() throws IOException {
    KitDescriptor kitDescriptor = dao.getKitDescriptorById(1L);
    assertEquals("Test Kit 1", kitDescriptor.getName());
  }

  @Test
  public void testGetKitDescriptorByIdNotFound() throws IOException {
    KitDescriptor kitDescriptor = dao.getKitDescriptorById(9999L);
    assertNull(kitDescriptor);
  }

  @Test
  public void testGetKitDescriptorByPartNumber() throws IOException {
    KitDescriptor kitDescriptor = dao.getKitDescriptorByPartNumber("k002", KitType.LIBRARY,
        PlatformType.ILLUMINA);
    assertEquals("Test Kit 2", kitDescriptor.getName());
  }

  @Test
  public void testGetKitDescriptorByPartNumberNotFound() throws IOException {
    KitDescriptor kitDescriptor = dao.getKitDescriptorByPartNumber("doesnotexist", KitType.LIBRARY,
        PlatformType.ILLUMINA);
    assertNull(kitDescriptor);
  }

  @Test
  public void testListAllKitDescriptors() throws IOException {
    List<KitDescriptor> kitDescriptors = dao.listAllKitDescriptors();
    assertEquals(5, kitDescriptors.size());
  }

  @Test
  public void testSaveKitDescriptor() throws IOException {
    KitDescriptor newKitDescriptor = makeNewKitDescriptor();
    newKitDescriptor.setChangeDetails(user);
    long id = dao.saveKitDescriptor(newKitDescriptor);
    assertNotEquals(0L, id);
    KitDescriptor savedKitDescriptor = dao.getKitDescriptorById(id);
    assertEquals(savedKitDescriptor.getName(), newKitDescriptor.getName());
  }

  @Test
  public void testSaveKitDescriptorUpdate() throws IOException {
    KitDescriptor existingKitDescriptor = dao.getKitDescriptorById(1L);
    existingKitDescriptor.setName("UPDATED");
    assertEquals(1L, dao.saveKitDescriptor(existingKitDescriptor));
    KitDescriptor updatedKitDescriptor = dao.getKitDescriptorById(1L);
    assertEquals("UPDATED", updatedKitDescriptor.getName());
  }

  private KitDescriptor makeNewKitDescriptor() {
    KitDescriptor kitDescriptor = new KitDescriptor();
    kitDescriptor.setKitType(KitType.LIBRARY);
    kitDescriptor.setPlatformType(PlatformType.ILLUMINA);
    kitDescriptor.setName("FUNNYKITTY");
    kitDescriptor.setManufacturer("me");
    kitDescriptor.setPartNumber("1234ABCD");
    kitDescriptor.setStockLevel(0);
    return kitDescriptor;
  }

  @Test
  public void testSearchMethod() throws Exception {
    String partNumber = "k003";
    List<KitDescriptor> results = dao.search(KitType.QC, partNumber);
    assertEquals(1, results.size());
    assertEquals(partNumber, results.get(0).getPartNumber());
  }

  @Test
  public void testGetLibraryAliquotsForKdTsRelationship() throws Exception {
    KitDescriptor kit = (KitDescriptor) currentSession().find(KitDescriptor.class, 1L);
    TargetedSequencing target = (TargetedSequencing) currentSession().find(TargetedSequencing.class, 1L);
    List<LibraryAliquot> aliquots = dao.getLibraryAliquotsForKdTsRelationship(kit, target);
    assertNotNull(aliquots);
    assertEquals(1, aliquots.size());
    assertEquals(kit.getId(), aliquots.get(0).getLibrary().getKitDescriptor().getId());
    assertEquals(target.getId(), aliquots.get(0).getTargetedSequencing().getId());
  }

  @Test
  public void testGetUsageByLibraries() throws Exception {
    KitDescriptor kit = (KitDescriptor) currentSession().find(KitDescriptor.class, 2L);
    assertEquals(3, dao.getUsageByLibraries(kit));
  }

  @Test
  public void testGetUsageByContainers() throws Exception {
    KitDescriptor kit = (KitDescriptor) currentSession().find(KitDescriptor.class, 4L);
    assertEquals(2, dao.getUsageByContainers(kit));
  }

  @Test
  public void testGetUsageByRuns() throws Exception {
    KitDescriptor kit = (KitDescriptor) currentSession().find(KitDescriptor.class, 5L);
    assertEquals(2, dao.getUsageByRuns(kit));
  }

  @Test
  public void testGetUsageByQcTypes() throws Exception {
    KitDescriptor kit = (KitDescriptor) currentSession().find(KitDescriptor.class, 3L);
    assertEquals(1, dao.getUsageByQcTypes(kit));
  }

  @Test
  public void testGetKitDescriptorByName() throws Exception {
    String name = "Test Kit 2";
    KitDescriptor kit = dao.getKitDescriptorByName(name);
    assertNotNull(kit);
    assertEquals(name, kit.getName());
  }

}
