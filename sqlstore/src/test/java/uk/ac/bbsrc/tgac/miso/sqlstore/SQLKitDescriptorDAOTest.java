package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class SQLKitDescriptorDAOTest extends AbstractDAOTest {

  @InjectMocks
  private SQLKitDescriptorDAO kitDescriptorDao;

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  private final User user = new UserImpl();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    user.setUserId(1L);
  }

  @Test
  public void testGetKitDescriptorById() throws IOException {
    KitDescriptor kitDescriptor = kitDescriptorDao.getKitDescriptorById(1L);
    assertThat(kitDescriptor.getName(), is("GS Titanium Sequencing Kit XLR70"));
  }

  @Test
  public void testGetKitDescriptorByIdNotFound() throws IOException {
    KitDescriptor kitDescriptor = kitDescriptorDao.getKitDescriptorById(9999L);
    assertNull(kitDescriptor);
  }

  @Test
  public void testGetKitDescriptorByPartNumber() throws IOException {
    KitDescriptor kitDescriptor = kitDescriptorDao.getKitDescriptorByPartNumber("05233526001");
    assertThat(kitDescriptor.getName(), is("GS Titanium Sequencing Kit XLR70"));
  }

  @Test
  public void testGetKitDescriptorByPartNumberNotFound() throws IOException {
    KitDescriptor kitDescriptor = kitDescriptorDao.getKitDescriptorByPartNumber("doesnotexist");
    assertNull(kitDescriptor);
  }

  @Test
  public void testListAllKitDescriptors() throws IOException {
    List<KitDescriptor> kitDescriptors = (List<KitDescriptor>) kitDescriptorDao.listAll();
    assertThat(kitDescriptors.size(), not(0));
  }

  @Test
  public void testListKitDescriptorsByType() throws IOException {
    List<KitDescriptor> kitDescriptors = kitDescriptorDao.listKitDescriptorsByType(KitType.LIBRARY);
    assertThat(kitDescriptors.size(), not(0));
  }

  @Test
  public void testListKitDescriptorsByPlatform() throws IOException {
    List<KitDescriptor> kitDescriptors = kitDescriptorDao.listKitDescriptorsByPlatform(PlatformType.ILLUMINA);
    assertThat(kitDescriptors.size(), not(0));
  }

  @Ignore
  @Test
  public void testSaveKitDescriptor() throws IOException {
    KitDescriptor newKitDescriptor = makeNewKitDescriptor();
    // newKitDescriptor.setLastModifier(user);
    long id = kitDescriptorDao.save(newKitDescriptor);
    assertThat(id, not(0L));
    KitDescriptor savedKitDescriptor = kitDescriptorDao.getKitDescriptorById(id);
    assertThat(newKitDescriptor.getName(), is(savedKitDescriptor.getName()));
  }

  @Test
  public void testSaveKitDescriptorUpdate() throws IOException {
    KitDescriptor existingKitDescriptor = kitDescriptorDao.getKitDescriptorById(1L);
    existingKitDescriptor.setName("UPDATED");
    assertThat(kitDescriptorDao.save(existingKitDescriptor), is(1L));
    KitDescriptor updatedKitDescriptor = kitDescriptorDao.getKitDescriptorById(1L);
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
    Map<String, Integer> columnSizes = kitDescriptorDao.getColumnSizes();
    assertThat(columnSizes, hasEntry("name", 255));
  }

}
