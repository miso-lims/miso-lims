package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Streams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.PoolProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.ProgressImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.SampleProgressStep;

public class HibernateProgressDaoIT extends AbstractDAOTest {
  private static final long UNKNOWN_WORKFLOW_PROGRESS_ID = 42;
  private static final long WORKFLOW_PROGRESS_ID_1 = 1;
  private static final long WORKFLOW_PROGRESS_ID_2 = 2;

  private static final long UNKNOWN_USER_ID = 42;
  private static final long USER_ID = 3;

  private static final long PROGRESS1_SAMPLE_ID = 1;
  private static final long PROGRESS2_SAMPLE_ID = 2;
  private static final long PROGRESS2_POOL_ID = 1;
  private static final long SAMPLE_ID = 3;
  private static final long POOL_ID = 2;

  // Progress objects that mirror most of the attributes of the objects in the test database
  private Progress progress1;
  private Progress progress2;

  @InjectMocks
  private HibernateProgressDao dao;

  @PersistenceContext
  private EntityManager entityManager;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setEntityManager(entityManager);
    progress1 = createProgress1();
    progress2 = createProgress2();
  }

  @Test
  public void testGetByUnknownId() {
    assertNull(dao.get(UNKNOWN_WORKFLOW_PROGRESS_ID));
  }

  @Test
  public void testGetByKnownId() {
    assertEquivalent(progress1, dao.get(WORKFLOW_PROGRESS_ID_1));
  }

  @Test
  public void testListByUnknownUser() {
    assertEquals(Collections.emptyList(), dao.listByUserId(UNKNOWN_USER_ID));
  }

  @Test
  public void testListByKnownUser() {
    List<Progress> progresses = dao.listByUserId(USER_ID);
    assertEquals(2, progresses.size());

    Progress dbProgress1 = progresses.stream().filter(progress -> progress.getId() == 1L).findFirst().orElse(null);
    assertNotNull(dbProgress1);
    Progress dbProgress2 = progresses.stream().filter(progress -> progress.getId() == 2L).findFirst().orElse(null);
    assertNotNull(dbProgress2);

    assertEquivalent(progress1, dbProgress1);
    assertEquivalent(progress2, dbProgress2);
  }

  @Test
  public void testSaveWithoutSteps() {
    assertEquivalent(createProgressWithoutSteps(), dao.get(save(createProgressWithoutSteps())));
  }

  /**
   * Save Progress, flush changes, and remove progress from current session
   *
   * @return id of saved Progress
   */
  public long save(Progress progress) {
    long id = dao.save(progress).getId();
    Session session = entityManager.unwrap(Session.class);
    session.flush();
    session.clear();
    return id;
  }

  @Test
  public void testSaveNew() {
    assertEquivalent(createProgressWithOrderedSteps(), dao.get(save(createProgressWithOrderedSteps())));
  }

  @Test
  public void testSaveNewWithUnorderedSteps() {
    assertEquivalent(createProgressWithOrderedSteps(), dao.get(save(createProgressWithUnorderedSteps())));
  }

  @Test
  public void testSaveAddStep() {
    Progress actual = dao.get(WORKFLOW_PROGRESS_ID_1);
    ProgressStep step = makePoolProgressStep(POOL_ID, 1);
    step.setProgress(actual);

    actual.getSteps().add(step);
    progress1.getSteps().add(step);

    assertEquivalent(progress1, dao.get(save(actual)));
  }

  private SampleProgressStep makeSampleProgressStep(long sampleId, int stepNumber) {
    SampleProgressStep step = new SampleProgressStep();
    Sample sample = new SampleImpl();
    sample.setId(sampleId);
    step.setInput(sample);
    step.setStepNumber(stepNumber);
    return step;
  }

  private PoolProgressStep makePoolProgressStep(long poolId, int stepNumber) {
    PoolProgressStep step = new PoolProgressStep();
    Pool pool = new PoolImpl();
    pool.setId(poolId);
    step.setInput(pool);
    step.setStepNumber(stepNumber);
    return step;
  }

  private Progress createProgress1() {
    Progress progress = makeProgress(LOAD_SEQUENCER, getDefaultUser(), new Date(), new Date(),
        Collections.singletonList(makeSampleProgressStep(PROGRESS1_SAMPLE_ID, 0)));
    progress.setId(WORKFLOW_PROGRESS_ID_1);

    return progress;
  }

  private Progress createProgress2() {
    Progress progress = makeProgress(LOAD_SEQUENCER, getDefaultUser(), new Date(), new Date(),
        Arrays.asList(makeSampleProgressStep(PROGRESS2_SAMPLE_ID, 0), makePoolProgressStep(PROGRESS2_POOL_ID, 1)));
    progress.setId(WORKFLOW_PROGRESS_ID_2);

    return progress;
  }

  private Progress createProgressWithoutSteps() {
    return makeProgress(LOAD_SEQUENCER, getDefaultUser(), new Date(), new Date(), Collections.emptyList());
  }

  private Progress createProgressWithOrderedSteps() {
    return makeProgress(LOAD_SEQUENCER, getDefaultUser(), new Date(), new Date(),
        Arrays.asList(makeSampleProgressStep(SAMPLE_ID, 0), makePoolProgressStep(POOL_ID, 1)));
  }

  private Progress createProgressWithUnorderedSteps() {
    return makeProgress(LOAD_SEQUENCER, getDefaultUser(), new Date(), new Date(),
        Arrays.asList(makePoolProgressStep(POOL_ID, 1), makeSampleProgressStep(SAMPLE_ID, 0)));
  }

  /**
   * Similar to a Progress constructor. However, an ID is not set, as IDs are dynamically generated.
   */
  private Progress makeProgress(WorkflowName workflowName, User user, Date creationTime, Date lastModified,
      List<ProgressStep> steps) {
    Progress progress = new ProgressImpl();

    progress.setWorkflowName(workflowName);
    progress.setUser(user);
    progress.setCreationTime(creationTime);
    progress.setLastModified(lastModified);
    progress.setSteps(steps);
    for (ProgressStep step : steps) {
      step.setProgress(progress);
    }

    return progress;
  }

  /**
   * Assert that actualProgress and expectedProgress match based on a subset of fields
   */
  private void assertEquivalent(Progress expectedProgress, Progress actualProgress) {
    assertEquals(expectedProgress.getWorkflowName(), actualProgress.getWorkflowName());
    assertEquals(expectedProgress.getUser(), actualProgress.getUser());
    assertTrue(actualProgress.getCreationTime().getTime() <= actualProgress.getLastModified().getTime());

    // The Progress of each ProgressStep is not matched, as the Progress's ID is automatically generated
    // and comparing the fields of each ProgressStep's parent would cause infinite recursion
    assertSameStepNumbers(expectedProgress.getSteps(), actualProgress.getSteps());
  }

  private void assertSameStepNumbers(SortedSet<ProgressStep> expected, SortedSet<ProgressStep> actual) {
    assertEquals(expected.size(), actual.size());
    assertTrue(Streams.zip(expected.stream(), actual.stream(),
        (a, b) -> a.getStepNumber() == b.getStepNumber()).findAny().orElse(true));
  }

  private User getDefaultUser() {
    return (User) entityManager.unwrap(Session.class).get(UserImpl.class, USER_ID);
  }
}
