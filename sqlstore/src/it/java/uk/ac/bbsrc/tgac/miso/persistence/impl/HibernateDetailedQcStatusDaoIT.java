package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateDetailedQcStatusDaoIT extends AbstractDAOTest {

  private HibernateDetailedQcStatusDao sut;

  @Before
  public void setup() {
    sut = new HibernateDetailedQcStatusDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    DetailedQcStatus status = sut.get(id);
    assertNotNull(status);
    assertEquals(id, status.getId());
  }

  @Test
  public void testGetByDescription() throws IOException {
    String desc = "Failed";
    DetailedQcStatus status = sut.getByDescription(desc);
    assertNotNull(status);
    assertEquals(desc, status.getDescription());
  }

  @Test
  public void testList() throws IOException {
    List<DetailedQcStatus> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String desc = "Unknown";
    DetailedQcStatus status = new DetailedQcStatusImpl();
    status.setDescription(desc);
    status.setStatus(null);
    status.setNoteRequired(true);
    status.setArchived(false);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    status.setChangeDetails(user);
    long savedId = sut.create(status);

    clearSession();

    DetailedQcStatus saved =
        (DetailedQcStatus) currentSession().get(DetailedQcStatusImpl.class, savedId);
    assertEquals(desc, saved.getDescription());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String desc = "New Desc";
    DetailedQcStatus status =
        (DetailedQcStatus) currentSession().get(DetailedQcStatusImpl.class, id);
    assertNotEquals(desc, status.getDescription());
    status.setDescription(desc);
    sut.update(status);

    clearSession();

    DetailedQcStatus saved =
        (DetailedQcStatus) currentSession().get(DetailedQcStatusImpl.class, id);
    assertEquals(desc, saved.getDescription());
  }

  @Test
  public void testGetUsageBySamples() throws IOException {
    DetailedQcStatus status =
        (DetailedQcStatus) currentSession().get(DetailedQcStatusImpl.class, 1L);
    assertEquals("Passed", status.getDescription());
    assertEquals(26L, sut.getUsageBySamples(status));
  }

  @Test
  public void testGetUsageByLibraries() throws IOException {
    DetailedQcStatus status =
        (DetailedQcStatus) currentSession().get(DetailedQcStatusImpl.class, 1L);
    assertEquals("Passed", status.getDescription());
    assertEquals(15L, sut.getUsageByLibraries(status));
  }

  @Test
  public void testGetUsageByLibraryAliquots() throws IOException {
    DetailedQcStatus status =
        (DetailedQcStatus) currentSession().get(DetailedQcStatusImpl.class, 1L);
    assertEquals("Passed", status.getDescription());
    assertEquals(0L, sut.getUsageByLibraryAliquots(status));
  }

}
