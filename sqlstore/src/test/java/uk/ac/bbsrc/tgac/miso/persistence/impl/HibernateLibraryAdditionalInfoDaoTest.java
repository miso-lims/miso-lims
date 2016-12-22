package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.store.KitDescriptorStore;

public class HibernateLibraryAdditionalInfoDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private KitDescriptorStore kitDescriptorStore;

  @InjectMocks
  HibernateLibraryAdditionalInfoDao dao;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGetList() throws IOException {
    List<LibraryAdditionalInfo> list = dao.getLibraryAdditionalInfo();
    assertNotNull(list);
    assertEquals(1, list.size());
  }

  @Test
  public void testGetSingle() throws IOException {
    LibraryAdditionalInfo info = dao.getLibraryAdditionalInfo(1L);
    assertNotNull(info);
    assertEquals(Long.valueOf(1L), info.getLibraryId());
  }

  @Test
  public void testGetByLibraryNull() throws IOException {
    LibraryAdditionalInfo info = dao.getLibraryAdditionalInfoByLibraryId(100L);
    assertNull(info);
  }

  @Test
  public void testGetSingleNull() throws IOException {
    LibraryAdditionalInfo info = dao.getLibraryAdditionalInfo(100L);
    assertNull(info);
  }

  @Test
  public void testAdd() throws IOException {
    LibraryAdditionalInfo info = new LibraryAdditionalInfoImpl();
    User user = new UserImpl();
    user.setUserId(1L);
    info.setCreatedBy(user);
    info.setUpdatedBy(user);
    Date now = new Date();
    info.setCreationDate(now);
    info.setLastUpdated(now);
    KitDescriptor kit = mockKitDescriptorInStore(1L);
    info.setPrepKit(kit);
    LibraryDesignCode code = new LibraryDesignCode();
    code.setId(1L);
    info.setLibraryDesignCode(code);
    Library library = new LibraryImpl();
    library.setId(2L);
    info.setLibrary(library);
    info.setLibraryId(2L);

    Long newId = dao.addLibraryAdditionalInfo(info);
    assertNotNull(newId);
    LibraryAdditionalInfo saved = dao.getLibraryAdditionalInfo(newId);
    assertNotNull(saved);
    assertEquals(newId, saved.getLibraryId());
    assertEquals(user.getUserId(), saved.getCreatedBy().getUserId());
    assertEquals(user.getUserId(), saved.getUpdatedBy().getUserId());
    assertEquals(library.getId(), saved.getLibrary().getId());
    assertEquals(kit.getId(), saved.getPrepKit().getId());
  }

  @Test
  public void testDelete() throws IOException {
    LibraryAdditionalInfo info = dao.getLibraryAdditionalInfo(1L);
    assertNotNull(info);
    dao.deleteLibraryAdditionalInfo(info);
    assertNull(dao.getLibraryAdditionalInfo(1L));
  }

  @Test
  public void testUpdate() throws IOException {
    mockKitDescriptorInStore(1L);

    LibraryAdditionalInfo info = dao.getLibraryAdditionalInfo(1L);
    assertNotNull(info);
    assertEquals(1L, info.getPrepKit().getId());
    KitDescriptor newKit = mockKitDescriptorInStore(2L);
    info.setPrepKit(newKit);
    Date oldDate = info.getLastUpdated();

    dao.update(info);
    LibraryAdditionalInfo updated = dao.getLibraryAdditionalInfo(1L);
    assertEquals(newKit.getId(), updated.getPrepKit().getId());
    assertFalse(oldDate.equals(updated.getLastUpdated()));
  }

  /**
   * Creates a mock KitDescriptor with only getKitDescriptorId() set up, and adds it to be returned from kitStore.getKitDescriptorById()
   *
   * @param id
   *          the ID to use for the mock, and retrieval via kitStore
   * @return the mock KitDescriptor
   * @throws IOException
   */
  private KitDescriptor mockKitDescriptorInStore(Long id) throws IOException {
    KitDescriptor kd = Mockito.mock(KitDescriptor.class);
    Mockito.when(kd.getId()).thenReturn(id);
    Mockito.when(kitDescriptorStore.getKitDescriptorById(id)).thenReturn(kd);
    return kd;
  }

}
