package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateArrayRunDaoIT extends AbstractDAOTest {

  @PersistenceContext
  private EntityManager entityManager;

  private HibernateArrayRunDao sut;

  @Before
  public void setup() {
    sut = new HibernateArrayRunDao();
    sut.setEntityManager(entityManager);
  }

  @Test
  public void testSaveNew() throws Exception {
    ArrayRun run = new ArrayRun();
    run.setAlias("TestArrayRun");
    Instrument inst = (Instrument) entityManager.unwrap(Session.class).get(InstrumentImpl.class, 3L);
    run.setInstrument(inst);
    run.setHealth(HealthType.Running);
    Date now = new Date();
    LocalDate today = LocalDate.now();
    run.setStartDate(today);
    User user = (User) entityManager.unwrap(Session.class).get(UserImpl.class, 1L);
    run.setCreator(user);
    run.setCreationTime(now);
    run.setLastModifier(user);
    run.setLastModified(now);

    long savedId = sut.create(run);

    ArrayRun saved = sut.get(savedId);
    assertNotNull(saved);
    assertEquals(run.getAlias(), saved.getAlias());
    assertEquals(run.getInstrument().getId(), saved.getInstrument().getId());
    assertEquals(run.getHealth(), saved.getHealth());
    assertEquals(LimsUtils.formatDate(run.getStartDate()), LimsUtils.formatDate(saved.getStartDate()));
  }

  @Test
  public void testSaveExisting() throws Exception {
    long runId = 1L;
    String alias = "NewAlias";
    String desc = "NewDesc";

    ArrayRun run = sut.get(runId);
    assertNotNull(run.getArray());
    assertNotEquals(alias, run.getAlias());
    assertNotEquals(desc, run.getDescription());
    run.setArray(null);
    run.setAlias(alias);
    run.setDescription(desc);

    long savedId = sut.update(run);
    assertEquals(runId, savedId);

    ArrayRun saved = sut.get(savedId);
    assertNotNull(saved);
    assertNull(saved.getArray());
    assertEquals(alias, run.getAlias());
    assertEquals(desc, run.getDescription());
  }

  @Test
  public void testGet() throws Exception {
    ArrayRun run = sut.get(1L);
    assertNotNull(run);
    assertEquals(1L, run.getId());
    assertEquals("ArrayRun_1", run.getAlias());
    assertNotNull(run.getInstrument());
    assertEquals("iScan_1", run.getInstrument().getName());
    assertNotNull(run.getArray());
    assertEquals("Array_1", run.getArray().getAlias());
    assertEquals(HealthType.Running, run.getHealth());
    assertEquals(LocalDate.of(2018, 2, 2), run.getStartDate());
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "ArrayRun_1";
    ArrayRun run = sut.getByAlias(alias);
    assertNotNull(run);
    assertEquals(alias, run.getAlias());
  }

  @Test
  public void testList() throws Exception {
    List<ArrayRun> runs = sut.list();
    assertNotNull(runs);
    assertEquals(1, runs.size());
  }

  @Test
  public void testListByArrayId() throws Exception {
    List<ArrayRun> runs = sut.listByArrayId(1L);
    assertNotNull(runs);
    assertEquals(1, runs.size());
  }

  @Test
  public void testListBySampleId() throws Exception {
    List<ArrayRun> runs = sut.listBySampleId(19L);
    assertNotNull(runs);
    assertEquals(1, runs.size());
  }

  @Test
  public void testCount() throws Exception {
    assertEquals(1, sut.count());
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("ArrayRun"));
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter) throws IOException {
    // verify Hibernate mappings by ensuring that no exception is thrown
    assertNotNull(sut.list(err -> {
      throw new RuntimeException(err);
    }, 0, 10, true, "alias", filter));
  }

}
