package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample.ArrayRunSampleId;
import uk.ac.bbsrc.tgac.miso.core.data.RunItemQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateArrayRunSampleDaoIT extends AbstractDAOTest {

  @PersistenceContext
  private EntityManager entityManager;

  private HibernateArrayRunSampleDao sut;

  @BeforeEach
  public void setup() {
    sut = new HibernateArrayRunSampleDao();
    sut.setEntityManager(entityManager);
  }

  @Test
  public void testSaveNew() throws Exception {
    ArrayRun run = currentSession().find(ArrayRun.class, 1L);
    Array array = currentSession().find(Array.class, 1L);
    SampleImpl sample = currentSession().find(SampleImpl.class, 19L);
    RunItemQcStatus status = currentSession().find(RunItemQcStatus.class, 3L);
    UserImpl user = currentSession().find(UserImpl.class, 1L);
    ArrayRunSample row = new ArrayRunSample(run, array, "R01C01", sample);
    row.setQcStatus(status);
    row.setQcNote("new qc");
    row.setQcUser(user);
    row.setQcDate(LocalDate.of(2026, 4, 10));

    sut.save(row);
    clearSession();

    ArrayRunSample saved = currentSession().find(ArrayRunSample.class, new ArrayRunSampleId(run, array, "R01C01", sample));
    assertNotNull(saved);
    assertEquals(3L, saved.getQcStatus().getId());
    assertEquals("new qc", saved.getQcNote());
    assertEquals(1L, saved.getQcUser().getId());
    assertEquals(LocalDate.of(2026, 4, 10), saved.getQcDate());
  }

  @Test
  public void testSaveExisting() throws Exception {
    ArrayRun run = currentSession().find(ArrayRun.class, 2L);
    Array array = currentSession().find(Array.class, 2L);
    SampleImpl sample = currentSession().find(SampleImpl.class, 26L);
    RunItemQcStatus status = currentSession().find(RunItemQcStatus.class, 2L);
    UserImpl user = currentSession().find(UserImpl.class, 1L);
    ArrayRunSample row = currentSession().find(ArrayRunSample.class, new ArrayRunSampleId(run, array, "R02C01", sample));
    assertNotNull(row);

    row.setQcStatus(status);
    row.setQcNote("updated qc");
    row.setQcUser(user);
    row.setQcDate(LocalDate.of(2026, 4, 11));

    sut.save(row);
    clearSession();

    ArrayRunSample saved = currentSession().find(ArrayRunSample.class, new ArrayRunSampleId(run, array, "R02C01", sample));
    assertNotNull(saved);
    assertEquals(2L, saved.getQcStatus().getId());
    assertEquals("updated qc", saved.getQcNote());
    assertEquals(1L, saved.getQcUser().getId());
    assertEquals(LocalDate.of(2026, 4, 11), saved.getQcDate());
  }

  @Test
  public void testGet() throws Exception {
    ArrayRun run = currentSession().find(ArrayRun.class, 2L);
    Array array = currentSession().find(Array.class, 2L);
    SampleImpl sample = currentSession().find(SampleImpl.class, 26L);

    ArrayRunSample result = sut.get(run, array, "R02C01", sample);

    assertNotNull(result);
    assertEquals("keep qc", result.getQcNote());
    assertEquals(1L, result.getQcStatus().getId());
    assertEquals(1L, result.getQcUser().getId());
  }

  @Test
  public void testGetNone() throws Exception {
    ArrayRun run = currentSession().find(ArrayRun.class, 1L);
    Array array = currentSession().find(Array.class, 1L);
    SampleImpl wrongSample = currentSession().find(SampleImpl.class, 26L);

    ArrayRunSample result = sut.get(run, array, "R01C01", wrongSample);

    assertNull(result);
  }

  @Test
  public void testListByRunId() throws Exception {
    List<ArrayRunSample> samples = sut.listByRunId(2L);

    assertEquals(2, samples.size());
    assertTrue(samples.stream().anyMatch(sample -> "R02C01".equals(sample.getPosition())
        && sample.getSample() != null
        && sample.getSample().getId() == 26L
        && "keep qc".equals(sample.getQcNote())));
    assertTrue(samples.stream().anyMatch(sample -> "R03C01".equals(sample.getPosition())
        && sample.getSample() != null
        && sample.getSample().getId() == 27L
        && "remove qc".equals(sample.getQcNote())));
  }

  @Test
  public void testDelete() throws Exception {
    ArrayRun run = currentSession().find(ArrayRun.class, 2L);
    Array array = currentSession().find(Array.class, 2L);
    SampleImpl removedSample = currentSession().find(SampleImpl.class, 27L);
    SampleImpl keptSample = currentSession().find(SampleImpl.class, 26L);
    ArrayRunSampleId removedId = new ArrayRunSampleId(run, array, "R03C01", removedSample);
    ArrayRunSampleId keptId = new ArrayRunSampleId(run, array, "R02C01", keptSample);

    ArrayRunSample row = currentSession().find(ArrayRunSample.class, removedId);
    assertNotNull(row);

    sut.delete(row);
    clearSession();

    assertNull(currentSession().find(ArrayRunSample.class, removedId));
    ArrayRunSample kept = currentSession().find(ArrayRunSample.class, keptId);
    assertNotNull(kept);
    assertEquals("keep qc", kept.getQcNote());
    assertEquals(1L, kept.getQcStatus().getId());
  }
}
