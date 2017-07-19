package uk.ac.bbsrc.tgac.miso.core.manager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;

/**
 * @author Chris Salt
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserAuthMisoRequestManagerTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private Project project;
  @Mock
  private User user;
  @Mock
  private RequestManager backingManager;
  @Mock
  private SecurityManager securityManager;
  @Mock
  private RequestManager requestManager;
  @Mock
  private Authentication authentication;
  @Mock
  private SecurityContextHolderStrategy securityContextHolderStrategy;
  @Mock
  private SecurityContextImpl context;
  @Mock
  private ProjectOverview overview;
  @Mock
  private Note note;
  @Mock
  private Run run;
  @Mock
  private RunQC runQC;
  @Mock
  private Sample sample;
  @Mock
  private SampleQC sampleQC;
  @Mock
  private Library library;
  @Mock
  private LibraryQC libraryQC;
  @Mock
  private Pool pool;
  @Mock
  private PoolQC poolQC;
  @Mock
  private Experiment experiment;
  @Mock
  private Study study;
  @Mock
  private Partition sequencerPoolParition;
  @Mock
  private SequencerPartitionContainer sequencerPartitionContainer;
  @Mock
  private Submission submission;
  @Mock
  private LibraryDilution dilution;
  @Mock
  private ProjectOverview projectOverview;
  @Mock
  private Project project1;
  @Mock
  private Project project2;
  @Mock
  private Project project3;

  @Resource
  @InjectMocks
  private UserAuthMisoRequestManager userAuthMisoRequestManager;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    // Set up user auth.
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(context.getAuthentication()).thenReturn(authentication);

    when(securityContextHolderStrategy.getContext()).thenReturn(context);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveProject(uk.ac.bbsrc.tgac.miso.core.data.Project)} . Tests this
   * method calls expected underlying methods given correct parameters.
   *
   * @throws IOException
   */
  @Test
  public void testSaveProject() throws IOException {
    final long expectedReturn = 1L;

    when(backingManager.saveProject(project)).thenReturn(expectedReturn);
    when(project.userCanWrite(any(User.class))).thenReturn(true);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveProject(project));
    verify(backingManager).saveProject(project);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveProject(uk.ac.bbsrc.tgac.miso.core.data.Project)} . Tests this
   * method throws an IOException when writeCheck fails.
   *
   * @throws IOException
   */
  @Test
  public void testSaveProjectThrows() throws IOException {
    // assert you can't save a project when write check fails.

    when(project.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Project");
    userAuthMisoRequestManager.saveProject(project);

    verify(backingManager, never()).saveProject(project);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveProjectOverview(uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveProjectOverview() throws IOException {
    final long expectedReturn = 1L;

    when(overview.getProject()).thenReturn(project);
    when(project.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveProjectOverview(overview)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveProjectOverview(overview));

    verify(backingManager).saveProjectOverview(overview);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveProjectOverview(uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview)}
   * .Tests this method throws an IOException when writeCheck fails.
   * 
   * @throws IOException
   */
  @Test
  public void testSaveProjectOverviewThrows() throws IOException {
    // assert you can't save a Project Overview when write check fails.

    when(overview.getProject()).thenReturn(project);
    when(project.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to the parent Project");
    userAuthMisoRequestManager.saveProjectOverview(overview);

    verify(backingManager, never()).saveProjectOverview(overview);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveProjectOverviewNote(uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview, com.eaglegenomics.simlims.core.Note)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveProjectOverviewNote() throws IOException {
    when(overview.getProject()).thenReturn(project);
    when(project.userCanWrite(any(User.class))).thenReturn(true);
    userAuthMisoRequestManager.saveProjectOverviewNote(overview, note);
    verify(backingManager).saveProjectOverviewNote(overview, note);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveProjectOverviewNote(uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview, com.eaglegenomics.simlims.core.Note)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveProjectOverviewNoteThrows() throws IOException {
    when(overview.getProject()).thenReturn(project);
    when(project.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to the parent Project");
    userAuthMisoRequestManager.saveProjectOverviewNote(overview, note);

    verify(backingManager, never()).saveProjectOverviewNote(overview, note);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSubmission(uk.ac.bbsrc.tgac.miso.core.data.Submission)} .
   */
  @Test
  public void testSaveSubmission() throws IOException {
    final long expectedReturn = 1L;

    when(backingManager.saveSubmission(submission)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveSubmission(submission));
    verify(backingManager).saveSubmission(submission);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getProjectById(long)} .
   */
  @Test
  public void testGetProjectById() throws IOException {
    long id = 1L;
    when(backingManager.getProjectById(id)).thenReturn(project);
    when(project.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(project, userAuthMisoRequestManager.getProjectById(id));

    verify(backingManager).getProjectById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getProjectById(long)} .
   */
  @Test
  public void testGetProjectByIdThrows() throws IOException {
    long id = 1L;
    when(backingManager.getProjectById(id)).thenReturn(project);
    when(project.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Project " + id);

    userAuthMisoRequestManager.getProjectById(id);

    verify(backingManager).getProjectById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getProjectByAlias(java.lang.String)} .
   */
  @Test
  public void testGetProjectByAlias() throws IOException {
    String alias = "alias";
    when(backingManager.getProjectByAlias(alias)).thenReturn(project);
    when(project.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(project, userAuthMisoRequestManager.getProjectByAlias(alias));

    verify(backingManager).getProjectByAlias(alias);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getProjectByAlias(java.lang.String)} .
   */
  @Test
  public void testGetProjectByAliasThrows() throws IOException {
    String alias = "alias";
    when(backingManager.getProjectByAlias(alias)).thenReturn(project);
    when(project.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Project " + alias);

    userAuthMisoRequestManager.getProjectByAlias(alias);

    verify(backingManager).getProjectByAlias(alias);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getProjectOverviewById(long)} .
   */
  @Test
  public void testGetProjectOverviewById() throws IOException {
    long id = 1L;
    when(backingManager.getProjectOverviewById(id)).thenReturn(projectOverview);
    when(projectOverview.getProject()).thenReturn(project);
    when(project.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(projectOverview, userAuthMisoRequestManager.getProjectOverviewById(id));

    verify(backingManager).getProjectOverviewById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getProjectOverviewById(long)} .
   */
  @Test
  public void testGetProjectOverviewByIdThrows() throws IOException {
    long projectOverviewId = 1L;
    long projectId = 2L;
    when(backingManager.getProjectOverviewById(projectOverviewId)).thenReturn(projectOverview);
    when(projectOverview.getProject()).thenReturn(project);
    when(project.userCanRead(any(User.class))).thenReturn(false);
    when(project.getId()).thenReturn(projectId);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read parent Project " + projectId + " for ProjectOverview " + projectOverviewId);

    userAuthMisoRequestManager.getProjectOverviewById(projectOverviewId);

    verify(backingManager).getProjectOverviewById(projectOverviewId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSubmissionById(long)} .
   */
  @Test
  public void testGetSubmissionById() throws IOException {
    long id = 1L;
    when(backingManager.getSubmissionById(id)).thenReturn(submission);

    assertEquals(submission, userAuthMisoRequestManager.getSubmissionById(id));

    verify(backingManager).getSubmissionById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllProjects()} .
   */
  @Test
  public void testListAllProjects() throws IOException {
    Set<Project> projects = new HashSet<>();
    projects.add(project1);
    projects.add(project2);
    projects.add(project3);
    when(backingManager.listAllProjects()).thenReturn(projects);
    when(project1.userCanRead(any(User.class))).thenReturn(true);
    when(project2.userCanRead(any(User.class))).thenReturn(true);
    when(project3.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(projects, userAuthMisoRequestManager.listAllProjects());
    verify(backingManager).listAllProjects();
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllProjects()} .
   */
  @Test
  public void testListAllProjectsOneUnreadable() throws IOException {
    Set<Project> projects = new HashSet<>();
    projects.add(project1);
    projects.add(project2);
    projects.add(project3);
    when(backingManager.listAllProjects()).thenReturn(projects);
    when(project1.userCanRead(any(User.class))).thenReturn(true);
    when(project2.userCanRead(any(User.class))).thenReturn(false);
    when(project3.userCanRead(any(User.class))).thenReturn(true);

    Set<Project> filtered = new HashSet<>();
    filtered.add(project1);
    filtered.add(project3);

    assertEquals(filtered, userAuthMisoRequestManager.listAllProjects());
    verify(backingManager).listAllProjects();
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllProjectsWithLimit(long)} .
   */
  @Test
  public void testListAllProjectsWithLimit() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllProjectsBySearch(java.lang.String)} .
   */
  @Test
  public void testListAllProjectsBySearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllOverviewsByProjectId(long)} .
   */

  @Test
  public void testListAllOverviewsByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRuns()} .
   */

  @Test
  public void testListAllRuns() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunsWithLimit(long)} .
   */

  @Test
  public void testListAllRunsWithLimit() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunsBySearch(java.lang.String)} .
   */

  @Test
  public void testListAllRunsBySearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunsByProjectId(long)} .
   */

  @Test
  public void testListAllRunsByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listRunsByPoolId(long)} .
   */

  @Test
  public void testListRunsByPoolId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listRunsBySequencerPartitionContainerId(long)} .
   */

  @Test
  public void testListRunsBySequencerPartitionContainerId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLS454Runs()} .
   */

  @Test
  public void testListAllLS454Runs() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllIlluminaRuns()} .
   */

  @Test
  public void testListAllIlluminaRuns() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSolidRuns()} .
   */

  @Test
  public void testListAllSolidRuns() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunQCsByRunId(long)} .
   */

  @Test
  public void testListAllRunQCsByRunId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listSequencerPartitionContainersByRunId(long)} .
   */

  @Test
  public void testListSequencerPartitionContainersByRunId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listSequencerPartitionContainersByBarcode(java.lang.String)} .
   */

  @Test
  public void testListSequencerPartitionContainersByBarcode() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamples()} .
   */

  @Test
  public void testListAllSamples() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesWithLimit(long)} .
   */

  @Test
  public void testListAllSamplesWithLimit() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesByReceivedDate(long)} .
   */

  @Test
  public void testListAllSamplesByReceivedDate() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesBySearch(java.lang.String)} .
   */

  @Test
  public void testListAllSamplesBySearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesByProjectId(long)} .
   */

  @Test
  public void testListAllSamplesByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesByExperimentId(long)} .
   */

  @Test
  public void testListAllSamplesByExperimentId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listSamplesByAlias(java.lang.String)} .
   */

  @Test
  public void testListSamplesByAlias() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSampleQCsBySampleId(long)} .
   */

  @Test
  public void testListAllSampleQCsBySampleId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraries()} .
   */

  @Test
  public void testListAllLibraries() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrariesWithLimit(long)} .
   */

  @Test
  public void testListAllLibrariesWithLimit() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrariesBySearch(java.lang.String)} .
   */

  @Test
  public void testListAllLibrariesBySearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrariesByProjectId(long)} .
   */

  @Test
  public void testListAllLibrariesByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrariesBySampleId(long)} .
   */

  @Test
  public void testListAllLibrariesBySampleId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryQCsByLibraryId(long)} .
   */

  @Test
  public void testListAllLibraryQCsByLibraryId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllDilutionsByProjectAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListAllDilutionsByProjectAndPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutions()} .
   */

  @Test
  public void testListAllLibraryDilutions() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsWithLimit(long)} .
   */

  @Test
  public void testListAllLibraryDilutionsWithLimit() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsByLibraryId(long)} .
   */

  @Test
  public void testListAllLibraryDilutionsByLibraryId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsByPlatform(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListAllLibraryDilutionsByPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsByProjectId(long)} .
   */

  @Test
  public void testListAllLibraryDilutionsByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsBySearchAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListAllLibraryDilutionsBySearchAndPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsBySearchOnly(java.lang.String)} .
   */

  @Test
  public void testListAllLibraryDilutionsBySearchOnly() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsByProjectAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListAllLibraryDilutionsByProjectAndPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutions()} .
   */

  @Test
  public void testListAllEmPCRDilutions() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByEmPcrId(long)} .
   */

  @Test
  public void testListAllEmPCRDilutionsByEmPcrId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByPlatform(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListAllEmPCRDilutionsByPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByProjectId(long)} .
   */

  @Test
  public void testListAllEmPCRDilutionsByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsBySearch(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListAllEmPCRDilutionsBySearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByProjectAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListAllEmPCRDilutionsByProjectAndPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByPoolAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListAllEmPCRDilutionsByPoolAndPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRs()} .
   */

  @Test
  public void testListAllEmPCRs() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRsByDilutionId(long)} .
   */

  @Test
  public void testListAllEmPCRsByDilutionId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPools()} .
   */

  @Test
  public void testListAllPools() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPoolsByPlatform(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListAllPoolsByPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPoolsByPlatformAndSearch(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType, java.lang.String)}
   * .
   */

  @Test
  public void testListAllPoolsByPlatformAndSearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listReadyPoolsByPlatform(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListReadyPoolsByPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listReadyPoolsByPlatformAndSearch(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType, java.lang.String)}
   * .
   */

  @Test
  public void testListReadyPoolsByPlatformAndSearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPoolsByLibraryId(long)} .
   */

  @Test
  public void testListPoolsByLibraryId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPoolsBySampleId(long)} .
   */

  @Test
  public void testListPoolsBySampleId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPoolQCsByPoolId(long)} .
   */

  @Test
  public void testListAllPoolQCsByPoolId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllExperiments()} .
   */

  @Test
  public void testListAllExperiments() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllExperimentsWithLimit(long)} .
   */

  @Test
  public void testListAllExperimentsWithLimit() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllExperimentsBySearch(java.lang.String)} .
   */

  @Test
  public void testListAllExperimentsBySearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllExperimentsByStudyId(long)} .
   */

  @Test
  public void testListAllExperimentsByStudyId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudies()} .
   */

  @Test
  public void testListAllStudies() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudiesWithLimit(long)} .
   */

  @Test
  public void testListAllStudiesWithLimit() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudiesBySearch(java.lang.String)} .
   */

  @Test
  public void testListAllStudiesBySearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudiesByProjectId(long)} .
   */

  @Test
  public void testListAllStudiesByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSequencerPoolPartitions()} .
   */

  @Test
  public void testListAllSequencerPoolPartitions() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPartitionsBySequencerPartitionContainerId(long)} .
   */

  @Test
  public void testListPartitionsBySequencerPartitionContainerId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSequencerPartitionContainers()} .
   */

  @Test
  public void testListAllSequencerPartitionContainers() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSubmissions()} .
   */

  @Test
  public void testListAllSubmissions() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listRunsByExperimentId(java.lang.Long)} .
   */

  @Test
  public void testListRunsByExperimentId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPlates()} .
   */

  @Test
  public void testListAllPlates() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPlatesByProjectId(long)} .
   */

  @Test
  public void testListAllPlatesByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPlatesBySearch(java.lang.String)} .
   */

  @Test
  public void testListAllPlatesBySearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteProject(uk.ac.bbsrc.tgac.miso.core.data.Project)} .
   */

  @Test
  public void testDeleteProject() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteStudy(uk.ac.bbsrc.tgac.miso.core.data.Study)} .
   */

  @Test
  public void testDeleteStudy() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteExperiment(uk.ac.bbsrc.tgac.miso.core.data.Experiment)} .
   */

  @Test
  public void testDeleteExperiment() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteSample(uk.ac.bbsrc.tgac.miso.core.data.Sample)} .
   */

  @Test
  public void testDeleteSample() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteLibrary(uk.ac.bbsrc.tgac.miso.core.data.Library)} .
   */

  @Test
  public void testDeleteLibrary() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteEmPCR(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR)} .
   */

  @Test
  public void testDeleteEmPCR() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteRun(uk.ac.bbsrc.tgac.miso.core.data.Run)} .
   */

  @Test
  public void testDeleteRun() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteRunQC(uk.ac.bbsrc.tgac.miso.core.data.RunQC)} .
   */

  @Test
  public void testDeleteRunQC() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteSampleQC(uk.ac.bbsrc.tgac.miso.core.data.SampleQC)} .
   */

  @Test
  public void testDeleteSampleQC() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteLibraryQC(uk.ac.bbsrc.tgac.miso.core.data.LibraryQC)} .
   */

  @Test
  public void testDeleteLibraryQC() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteLibraryDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution)}
   * .
   */

  @Test
  public void testDeleteLibraryDilution() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteEmPCRDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution)}
   * .
   */

  @Test
  public void testDeleteEmPCRDilution() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteSequencerReference(uk.ac.bbsrc.tgac.miso.core.data.SequencerReference)}
   * .
   */

  @Test
  public void testDeleteSequencerReference() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deletePool(uk.ac.bbsrc.tgac.miso.core.data.Pool)}
   * .
   */

  @Test
  public void testDeletePool() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deletePoolQC(uk.ac.bbsrc.tgac.miso.core.data.PoolQC)} .
   */

  @Test
  public void testDeletePoolQC() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deletePlate(uk.ac.bbsrc.tgac.miso.core.data.Plate)} .
   */

  @Test
  public void testDeletePlate() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteEntityGroup(uk.ac.bbsrc.tgac.miso.core.data.EntityGroup)} .
   */

  @Test
  public void testDeleteEntityGroup() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteContainer(uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer)}
   * .
   */

  @Test
  public void testDeleteContainer() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deletePartition(uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition)}
   * .
   */

  @Test
  public void testDeletePartition() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteNote(com.eaglegenomics.simlims.core.Note)} .
   */

  @Test
  public void testDeleteNote() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRuns(java.util.Collection)} .
   */

  @Test
  public void testSaveRuns() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRunNote(uk.ac.bbsrc.tgac.miso.core.data.Run, com.eaglegenomics.simlims.core.Note)}
   * .
   */

  @Test
  public void testSaveRunNote() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveEmPcrDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution)}
   * .
   */

  @Test
  public void testSaveEmPcrDilution() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePlatform(uk.ac.bbsrc.tgac.miso.core.data.Platform)} .
   */

  @Test
  public void testSavePlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveStatus(uk.ac.bbsrc.tgac.miso.core.data.Status)} .
   */

  @Test
  public void testSaveStatus() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSecurityProfile(com.eaglegenomics.simlims.core.SecurityProfile)}
   * .
   */

  @Test
  public void testSaveSecurityProfile() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSequencerReference(uk.ac.bbsrc.tgac.miso.core.data.SequencerReference)}
   * .
   */

  @Test
  public void testSaveSequencerReference() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveKit(uk.ac.bbsrc.tgac.miso.core.data.Kit)} .
   */

  @Test
  public void testSaveKit() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveKitDescriptor(uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor)}
   * .
   */

  @Test
  public void testSaveKitDescriptor() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePlate(uk.ac.bbsrc.tgac.miso.core.data.Plate)}
   * .
   */

  @Test
  public void testSavePlate() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveAlert(uk.ac.bbsrc.tgac.miso.core.event.Alert)}
   * .
   */

  @Test
  public void testSaveAlert() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveBox(uk.ac.bbsrc.tgac.miso.core.data.Box)} .
   */

  @Test
  public void testSaveBox() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryTypeById(long)} .
   */

  @Test
  public void testGetLibraryTypeById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryTypeByDescription(java.lang.String)} .
   */

  @Test
  public void testGetLibraryTypeByDescription() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryTypeByDescriptionAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testGetLibraryTypeByDescriptionAndPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibrarySelectionTypeById(long)} .
   */

  @Test
  public void testGetLibrarySelectionTypeById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibrarySelectionTypeByName(java.lang.String)} .
   */

  @Test
  public void testGetLibrarySelectionTypeByName() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryStrategyTypeById(long)} .
   */

  @Test
  public void testGetLibraryStrategyTypeById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryStrategyTypeByName(java.lang.String)} .
   */

  @Test
  public void testGetLibraryStrategyTypeByName() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getIndexById(long)} .
   */

  @Test
  public void testGetIndexById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPlatformById(long)} .
   */

  @Test
  public void testGetPlatformById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getStatusById(long)} .
   */

  @Test
  public void testGetStatusById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerReferenceById(long)} .
   */

  @Test
  public void testGetSequencerReferenceById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerReferenceByName(java.lang.String)} .
   */

  @Test
  public void testGetSequencerReferenceByName() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerReferenceByRunId(long)} .
   */

  @Test
  public void testGetSequencerReferenceByRunId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitById(long)} .
   */

  @Test
  public void testGetKitById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitByIdentificationBarcode(java.lang.String)} .
   */

  @Test
  public void testGetKitByIdentificationBarcode() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitByLotNumber(java.lang.String)} .
   */

  @Test
  public void testGetKitByLotNumber() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitDescriptorById(long)} .
   */

  @Test
  public void testGetKitDescriptorById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitDescriptorByPartNumber(java.lang.String)} .
   */

  @Test
  public void testGetKitDescriptorByPartNumber() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleQcTypeById(long)} .
   */

  @Test
  public void testGetSampleQcTypeById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleQcTypeByName(java.lang.String)} .
   */

  @Test
  public void testGetSampleQcTypeByName() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryQcTypeById(long)} .
   */

  @Test
  public void testGetLibraryQcTypeById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryQcTypeByName(java.lang.String)} .
   */

  @Test
  public void testGetLibraryQcTypeByName() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunQcTypeById(long)} .
   */

  @Test
  public void testGetRunQcTypeById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunQcTypeByName(java.lang.String)} .
   */

  @Test
  public void testGetRunQcTypeByName() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolQcTypeById(long)} .
   */

  @Test
  public void testGetPoolQcTypeById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolQcTypeByName(java.lang.String)} .
   */

  @Test
  public void testGetPoolQcTypeByName() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getAlertById(long)} .
   */

  @Test
  public void testGetAlertById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getBoxById(long)} .
   */

  @Test
  public void testGetBoxById() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getBoxByBarcode(java.lang.String)} .
   */

  @Test
  public void testGetBoxByBarcode() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getBoxByAlias(java.lang.String)} .
   */

  @Test
  public void testGetBoxByAlias() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxes()} .
   */

  @Test
  public void testListAllBoxes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxesWithLimit(long)} .
   */

  @Test
  public void testListAllBoxesWithLimit() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxesBySearch(java.lang.String)} .
   */

  @Test
  public void testListAllBoxesBySearch() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxesByAlias(java.lang.String)} .
   */

  @Test
  public void testListAllBoxesByAlias() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllChanges(java.lang.String)} .
   */

  @Test
  public void testListAllChanges() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSampleTypes()} .
   */

  @Test
  public void testListAllSampleTypes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryTypes()} .
   */

  @Test
  public void testListAllLibraryTypes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listLibraryTypesByPlatform(java.lang.String)} .
   */

  @Test
  public void testListLibraryTypesByPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrarySelectionTypes()} .
   */

  @Test
  public void testListAllLibrarySelectionTypes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryStrategyTypes()} .
   */

  @Test
  public void testListAllLibraryStrategyTypes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllIndices()} .
   */

  @Test
  public void testListAllIndices() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllIndicesByPlatform(java.lang.String)} .
   */

  @Test
  public void testListAllIndicesByPlatform() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllIndicesByFamilyName(java.lang.String)} .
   */

  @Test
  public void testListAllIndicesByFamilyName() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRsByProjectId(long)} .
   */

  @Test
  public void testListAllEmPCRsByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPoolsByProjectId(long)} .
   */

  @Test
  public void testListPoolsByProjectId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPlatforms()} .
   */

  @Test
  public void testListAllPlatforms() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPlatformsOfType(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListPlatformsOfType() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listDistinctPlatformNames()} .
   */

  @Test
  public void testListDistinctPlatformNames() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxUses()} .
   */

  @Test
  public void testListAllBoxUses() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxUsesStrings()} .
   */

  @Test
  public void testListAllBoxUsesStrings() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxSizes()} .
   */

  @Test
  public void testListAllBoxSizes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudyTypes()} .
   */

  @Test
  public void testListAllStudyTypes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getBoxablesFromBarcodeList(java.util.List)} .
   */

  @Test
  public void testGetBoxablesFromBarcodeList() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSequencerReferences()} .
   */

  @Test
  public void testListAllSequencerReferences() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listSequencerReferencesByPlatformType(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testListSequencerReferencesByPlatformType() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllKits()} .
   */

  @Test
  public void testListAllKits() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listKitsByExperimentId(long)} .
   */

  @Test
  public void testListKitsByExperimentId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listKitsByManufacturer(java.lang.String)} .
   */

  @Test
  public void testListKitsByManufacturer() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listKitsByType(uk.ac.bbsrc.tgac.miso.core.data.type.KitType)} .
   */

  @Test
  public void testListKitsByType() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listKitDescriptorsByType(uk.ac.bbsrc.tgac.miso.core.data.type.KitType)}
   * .
   */

  @Test
  public void testListKitDescriptorsByType() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllKitDescriptors()} .
   */

  @Test
  public void testListAllKitDescriptors() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSampleQcTypes()} .
   */

  @Test
  public void testListAllSampleQcTypes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryQcTypes()} .
   */

  @Test
  public void testListAllLibraryQcTypes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPoolQcTypes()} .
   */

  @Test
  public void testListAllPoolQcTypes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunQcTypes()} .
   */

  @Test
  public void testListAllRunQcTypes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStatus()} .
   */

  @Test
  public void testListAllStatus() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStatusBySequencerName(java.lang.String)} .
   */

  @Test
  public void testListAllStatusBySequencerName() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listUnreadAlertsByUserId(long)} .
   */

  @Test
  public void testListUnreadAlertsByUserId() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAlertsByUserId(long)} .
   */

  @Test
  public void testListAlertsByUserIdLong() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAlertsByUserId(long, long)} .
   */

  @Test
  public void testListAlertsByUserIdLongLong() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#discardSingleTube(uk.ac.bbsrc.tgac.miso.core.data.Box, java.lang.String)}
   * .
   */

  @Test
  public void testEmptySingleTube() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#discardAllTubes(uk.ac.bbsrc.tgac.miso.core.data.Box)} .
   */

  @Test
  public void testEmptyAllTubes() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteBox(uk.ac.bbsrc.tgac.miso.core.data.Box)} .
   */

  @Test
  public void testDeleteBox() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#Object()}.
   */

  @Test
  public void testObject() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#getClass()}.
   */

  @Test
  public void testGetClass() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#hashCode()}.
   */

  @Test
  public void testHashCode() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
   */

  @Test
  public void testEquals() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#clone()}.
   */

  @Test
  public void testClone() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#toString()}.
   */

  @Test
  public void testToString() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#notify()}.
   */

  @Test
  public void testNotify() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#notifyAll()}.
   */

  @Test
  public void testNotifyAll() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#wait(long)}.
   */

  @Test
  public void testWaitLong() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#wait(long, int)}.
   */

  @Test
  public void testWaitLongInt() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#wait()}.
   */

  @Test
  public void testWait() throws IOException {
    // TODO: Implement.
  }

  /**
   * Test method for {@link java.lang.Object#finalize()}.
   */

  @Test
  public void testFinalize() throws IOException {
    // TODO: Implement.
  }

}
