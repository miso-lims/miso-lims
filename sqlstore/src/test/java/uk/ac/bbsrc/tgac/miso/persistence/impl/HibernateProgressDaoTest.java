package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOADSEQUENCER;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Streams;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.PoolProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.ProgressImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.SampleProgressStep;

public class HibernateProgressDaoTest extends AbstractDAOTest {
  private static final long UNKNOWN_WORKFLOW_PROGRESS_ID = 42;
  private static final long WORKFLOW_PROGRESS_ID_1 = 1;
  private static final long WORKFLOW_PROGRESS_ID_2 = 2;

  private static final long UNKNOWN_USER_ID = 42;
  private static final long USER_ID = 3;

  // Progress objects that mirror most of the attributes of the objects in the test database
  private Progress progress1;
  private Progress progress2;

  @InjectMocks
  private HibernateProgressDao dao;

  @Autowired
  private SessionFactory sessionFactory;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
    progress1 = createProgress1();
    progress2 = createProgress2();
  }

  @Test
  public void getByUnknownId() {
    assertNull(dao.get(UNKNOWN_WORKFLOW_PROGRESS_ID));
  }

  @Test
  public void getByKnownId() {
    assertEquivalent(progress1, dao.get(WORKFLOW_PROGRESS_ID_1));
  }

  @Test
  public void listByUnknownUser() {
    assertEquals(Collections.emptyList(), dao.listByUserId(UNKNOWN_USER_ID));
  }

  @Test
  public void listByKnownUser() {
    List<Progress> progresses = dao.listByUserId(USER_ID);

    assertEquivalent(progress1, progresses.get(0));
    assertEquivalent(progress2, progresses.get(1));
  }

  @Test
  public void saveWithoutSteps() {
    Progress actual = dao.save(createProgressWithoutSteps());

    assertEquivalent(createProgressWithoutSteps(), dao.get(actual.getId()));
  }

  @Test
  public void saveNew() {
    Progress actual = dao.save(createProgressWithOrderedSteps());

    assertEquivalent(createProgressWithOrderedSteps(), dao.get(actual.getId()));
  }

  @Test
  public void saveNewWithUnorderedSteps() {
    Progress actual = dao.save(createProgressWithUnorderedSteps());

    assertEquivalent(createProgressWithOrderedSteps(), dao.get(actual.getId()));
  }

  @Test
  public void saveAddStep() {
    Progress actual = dao.get(WORKFLOW_PROGRESS_ID_1);
    ProgressStep step = makePoolProgressStep(2);

    actual.getSteps().add(step);
    progress1.getSteps().add(step);

    dao.save(actual);

    assertEquivalent(progress1, dao.get(WORKFLOW_PROGRESS_ID_1));
  }

  @Test
  public void saveAddMultipleSteps() {
    Progress actual = dao.get(WORKFLOW_PROGRESS_ID_2);
    List<ProgressStep> newSteps = Arrays.asList(
        makePoolProgressStep(1),
        makeSampleProgressStep(2),
        makeSampleProgressStep(3),
        makeSampleProgressStep(4),
        makePoolProgressStep(5));

    actual.getSteps().addAll(newSteps);
    progress2.getSteps().addAll(newSteps);

    dao.save(actual);

    assertEquivalent(progress2, dao.get(WORKFLOW_PROGRESS_ID_2));
  }

  /**
   * Create a SampleProgressStep with only a step number.  All other fields are null.
   */
  private SampleProgressStep makeSampleProgressStep(int stepNumber) {
    SampleProgressStep step = new SampleProgressStep();

    step.setStepNumber(stepNumber);

    return step;
  }

  /**
   * Create a PoolProgressStep with only a step number.  All other fields are null.
   */
  private PoolProgressStep makePoolProgressStep(int stepNumber) {
    PoolProgressStep step = new PoolProgressStep();

    step.setStepNumber(stepNumber);

    return step;
  }

  private Progress createProgress1() {
    Progress progress = makeProgress(LOADSEQUENCER, getDefaultUser(), new Date(), new Date(),
        Collections.singletonList(makeSampleProgressStep(1)));
    progress.setId(WORKFLOW_PROGRESS_ID_1);

    return progress;
  }

  private Progress createProgress2() {
    Progress progress = makeProgress(LOADSEQUENCER, getDefaultUser(), new Date(), new Date(),
        Arrays.asList(makeSampleProgressStep(1), makePoolProgressStep(2)));
    progress.setId(WORKFLOW_PROGRESS_ID_2);

    return progress;
  }

  private Progress createProgressWithoutSteps() {
    return makeProgress(LOADSEQUENCER, getDefaultUser(), new Date(0), new Date(1), Collections.emptyList());
  }

  private Progress createProgressWithOrderedSteps() {
    return makeProgress(LOADSEQUENCER, getDefaultUser(), new Date(0), new Date(1),
        Arrays.asList(makeSampleProgressStep(1), makePoolProgressStep(2)));
  }

  private Progress createProgressWithUnorderedSteps() {
    return makeProgress(LOADSEQUENCER, getDefaultUser(), new Date(0), new Date(1),
        Arrays.asList(makePoolProgressStep(2), makeSampleProgressStep(1)));
  }

  /**
   * Similar to a Progress constructor.  However, an ID is not set, as IDs are dynamically generated.
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
    assertSimilarDates(expectedProgress.getCreationTime(), actualProgress.getCreationTime());
    assertSimilarDates(expectedProgress.getLastModified(), actualProgress.getLastModified());

    // The Progress of each ProgressStep is not matched, as the Progress's ID is automatically generated
    //  and comparing the fields of each ProgressStep's parent would cause infinite recursion
    assertSameStepNumbers(expectedProgress.getSteps(), actualProgress.getSteps());
  }

  private void assertSameStepNumbers(SortedSet<ProgressStep> expected, SortedSet<ProgressStep> actual) {
    assertEquals(expected.size(), actual.size());
    assertTrue(Streams.zip(expected.stream(), actual.stream(),
        (a, b) -> a.getStepNumber() == b.getStepNumber()).findAny().orElse(true));
  }

  /**
   * Assert that two dates are within 1 hour of each other
   */
  private void assertSimilarDates(Date expected, Date actual) {
    int oneHourInMs = 3600000;

    assertTrue(Math.abs(expected.getTime() - actual.getTime()) < oneHourInMs);
  }

  private User getDefaultUser() {
    return (User) sessionFactory.getCurrentSession().get(UserImpl.class, USER_ID);
  }

  private List<ProgressStep> addSteps(List<ProgressStep> s1, List<ProgressStep> s2) {
    return Stream.concat(s1.stream(), s2.stream()).collect(Collectors.toList());
  }
}
