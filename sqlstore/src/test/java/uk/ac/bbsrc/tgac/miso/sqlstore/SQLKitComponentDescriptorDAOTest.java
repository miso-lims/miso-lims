package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.KitComponentDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitComponentDescriptorImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.store.KitDescriptorStore;

public class SQLKitComponentDescriptorDAOTest extends AbstractDAOTest {

  @InjectMocks
  private SQLKitComponentDescriptorDAO dao;

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private KitDescriptorStore kitDescriptorDAO;

  private final User user = new UserImpl();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    user.setUserId(1L);
  }

  @Test
  public void testGet() throws IOException {
    KitComponentDescriptor kcd = dao.get(1L);
    assertIsKcd1(kcd);
  }

  @Test
  public void testGetNoResults() throws IOException {
    KitComponentDescriptor kcd = dao.get(666L);
    assertNull(kcd);
  }

  @Test
  public void testLazyGet() throws IOException {
    KitComponentDescriptor kcd = dao.lazyGet(1L);
    assertIsKcd1(kcd);
  }

  @Test
  public void testLazyGetNoResults() throws IOException {
    KitComponentDescriptor kcd = dao.lazyGet(666L);
    assertNull(kcd);
  }

  @Test
  public void testGetKitComponentDescriptorById() throws IOException {
    KitComponentDescriptor kcd = dao.getKitComponentDescriptorById(1L);
    assertIsKcd1(kcd);
  }

  @Test
  public void testGetKitComponentDescriptorByIdNoResults() throws IOException {
    KitComponentDescriptor kcd = dao.getKitComponentDescriptorById(666L);
    assertNull(kcd);
  }

  @Test
  public void testGetKitComponentDescriptorByReferenceNumber() throws IOException {
    KitComponentDescriptor kcd = dao.getKitComponentDescriptorByReferenceNumber("1234");
    assertIsKcd1(kcd);
  }

  @Test
  public void testGetKitComponentDescriptorByReferenceNumberNoResults() throws IOException {
    KitComponentDescriptor kcd = dao.getKitComponentDescriptorByReferenceNumber("666");
    assertNull(kcd);
  }

  @Test
  public void testListKitComponentDescriptorsByKitDescriptorId() throws IOException {
    List<KitComponentDescriptor> kcds = dao.listKitComponentDescriptorsByKitDescriptorId(1L);
    assertEquals(1, kcds.size());
    assertIsKcd1(kcds.get(0));
  }

  @Test
  public void testListAll() throws IOException {
    List<KitComponentDescriptor> kcds = (List<KitComponentDescriptor>) dao.listAll();
    assertEquals(2, kcds.size());
    assertIsKcd1(kcds.get(0));
    assertIsKcd2(kcds.get(1));
  }

  @Test
  public void testCount() throws IOException {
    assertEquals(2, dao.count());
  }

  @Test
  public void testSaveKitComponentDescriptor() throws IOException {
    final String name = "test kcd";
    final String ref = "666";
    final KitComponentDescriptor kcd = new KitComponentDescriptorImpl();
    final KitDescriptor mockKitDescriptor = Mockito.mock(KitDescriptor.class);
    final long KitDescriptorId = 1L;
    when(mockKitDescriptor.getId()).thenReturn(KitDescriptorId);
    kcd.setName(name);
    kcd.setReferenceNumber(ref);
    kcd.setKitDescriptor(mockKitDescriptor);
    final long returnedId = dao.saveKitComponentDescriptor(kcd);

    KitComponentDescriptor newKcd = dao.get(returnedId);
    assertEquals(returnedId, newKcd.getId());
    assertEquals(name, newKcd.getName());
    assertEquals(ref, newKcd.getReferenceNumber());
  }

  @Test
  public void testSave() throws IOException {
    final String name = "test kcd";
    final String ref = "666";
    final KitComponentDescriptor kcd = new KitComponentDescriptorImpl();
    final KitDescriptor mockKitDescriptor = Mockito.mock(KitDescriptor.class);
    final long KitDescriptorId = 1L;
    when(mockKitDescriptor.getId()).thenReturn(KitDescriptorId);
    kcd.setName(name);
    kcd.setReferenceNumber(ref);
    kcd.setKitDescriptor(mockKitDescriptor);
    final long returnedId = dao.save(kcd);

    KitComponentDescriptor newKcd = dao.get(returnedId);
    assertEquals(returnedId, newKcd.getId());
    assertEquals(name, newKcd.getName());
    assertEquals(ref, newKcd.getReferenceNumber());
  }

  private static void assertIsKcd1(KitComponentDescriptor kcd) {
    assertNotNull(kcd);
    assertEquals(1L, kcd.getId());
    assertEquals("KitComponentDescriptor1", kcd.getName());
    assertEquals("1234", kcd.getReferenceNumber());
  }

  private static void assertIsKcd2(KitComponentDescriptor kcd) {
    assertNotNull(kcd);
    assertEquals(2L, kcd.getId());
    assertEquals("KitComponentDescriptor2", kcd.getName());
    assertEquals("5678", kcd.getReferenceNumber());
  }

}
