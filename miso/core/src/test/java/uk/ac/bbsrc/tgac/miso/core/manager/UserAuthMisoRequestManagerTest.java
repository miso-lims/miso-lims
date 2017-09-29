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

}
