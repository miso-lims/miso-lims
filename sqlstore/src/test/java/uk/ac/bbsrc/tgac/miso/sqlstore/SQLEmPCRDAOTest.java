/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.EmPCRDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;

/**
 * @author Chris Salt
 *
 */
public class SQLEmPCRDAOTest extends AbstractDAOTest {

  private static long nextAutoIncrementId = 4L;

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private Store<SecurityProfile> securityProfileDAO;

  @Mock
  private LibraryDilutionStore libraryDilutionDAO;

  @Mock
  private EmPCRDilutionStore emPCRDilutionDAO;

  @Mock
  private NamingScheme namingScheme;

  @InjectMocks
  private SQLEmPCRDAO dao;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(new TgacDataObjectFactory());
    when(namingScheme.generateNameFor(Matchers.any(emPCRDilution.class))).thenReturn("EDI123");
    when(namingScheme.validateName(Matchers.anyString())).thenReturn(ValidationResult.success());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDAO#save(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR)}.
   * 
   * @throws IOException
   */
  @Test
  public void testSave() throws IOException {
    emPCR em = new emPCR();
    LibraryDilution libraryDilution = Mockito.mock(LibraryDilution.class);
    when(libraryDilution.getId()).thenReturn(1L);
    em.setLibraryDilution(libraryDilution);
    mockAutoIncrement(nextAutoIncrementId);
    dao.save(em);
    nextAutoIncrementId += 1;

  }

  private void mockAutoIncrement(long value) {
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDAO#get(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGet() throws IOException {
    LibraryDilution mockDilution = Mockito.mock(LibraryDilution.class);
    SecurityProfile mockProfile = Mockito.mock(SecurityProfile.class);
    when(mockDilution.getId()).thenReturn(1L);
    when(libraryDilutionDAO.get(anyLong())).thenReturn(mockDilution);
    when(mockProfile.getProfileId()).thenReturn(1L);
    when(securityProfileDAO.get(anyLong())).thenReturn(mockProfile);
    long id = 1L;
    Double concentration = 10.00;
    int dilution = 1;
    String date = "2016-03-19";
    String userName = "Bobby Davro";
    String name = "Mr emPCR";
    Long securityProfile = 1L;

    emPCR rtn = dao.get(id);
    assertEquals(id, rtn.getId());
    assertEquals(concentration, rtn.getConcentration());
    assertEquals(dilution, rtn.getLibraryDilution().getId());
    assertEquals(date, rtn.getCreationDate().toString());
    assertEquals(userName, rtn.getPcrCreator());
    assertEquals(name, rtn.getName());
    assertEquals(securityProfile, rtn.getSecurityProfile().getProfileId());

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDAO#lazyGet(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testLazyGet() throws IOException {
    LibraryDilution mockDilution = Mockito.mock(LibraryDilution.class);
    SecurityProfile mockProfile = Mockito.mock(SecurityProfile.class);
    when(mockDilution.getId()).thenReturn(1L);
    when(libraryDilutionDAO.get(anyLong())).thenReturn(mockDilution);
    when(mockProfile.getProfileId()).thenReturn(1L);
    when(securityProfileDAO.get(anyLong())).thenReturn(mockProfile);
    long id = 1L;
    Double concentration = 10.00;
    int dilution = 1;
    String date = "2016-03-19";
    String userName = "Bobby Davro";
    String name = "Mr emPCR";
    Long securityProfile = 1L;

    emPCR rtn = dao.get(id);
    assertEquals(id, rtn.getId());
    assertEquals(concentration, rtn.getConcentration());
    assertEquals(dilution, rtn.getLibraryDilution().getId());
    assertEquals(date, rtn.getCreationDate().toString());
    assertEquals(userName, rtn.getPcrCreator());
    assertEquals(name, rtn.getName());
    assertEquals(securityProfile, rtn.getSecurityProfile().getProfileId());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDAO#listAllByProjectId(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testListAllByProjectId() throws IOException {
    Collection<emPCR> ems = dao.listAllByProjectId(1L);
    assertNotNull(ems);
    assertEquals(3, ems.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDAO#listAll()}.
   * 
   * @throws IOException
   */
  @Test
  public void testListAll() throws IOException {
    Collection<emPCR> ems = dao.listAll();
    assertNotNull(ems);
    assertEquals(3, ems.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDAO#count()}.
   */
  @Test
  public void testCount() throws IOException {
    int count = dao.count();
    assertEquals(3, count);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDAO#listAllByDilutionId(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testListAllByDilutionId() throws IOException {
    Collection<emPCR> ems = dao.listAllByDilutionId(1L);
    assertNotNull(ems);
    assertEquals(3, ems.size());

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDAO#remove(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR)}.
   * 
   * @throws IOException
   */
  @Test
  public void testRemove() throws IOException {
    dao.setCascadeType(CascadeType.REMOVE);
    emPCR em = dao.get(1L);
    List<emPCR> ems = (List<emPCR>) dao.listAll();
    assertTrue(ems.contains(em));
    dao.remove(em);
    ems = (List<emPCR>) dao.listAll();
    assertFalse(ems.contains(em));
  }

}
