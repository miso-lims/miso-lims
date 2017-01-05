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
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.AuthorizationIOException;

/**
 * @author Chris Salt
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserAuthMisoRequestManagerTestSuite {

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
  private LibraryDilution libraryDilution;
  @Mock
  private Pool pool;
  @Mock
  private PoolQC poolQC;
  @Mock
  private emPCR emPCR;
  @Mock
  private uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution emPCRDilution;
  @Mock
  private Experiment experiment;
  @Mock
  private Study study;
  @Mock
  private SequencerPoolPartition sequencerPoolParition;
  @Mock
  private SequencerPartitionContainer sequencerPartitionContainer;
  @Mock
  private Submission submission;
  @Mock
  private Dilution dilution;
  @Mock
  private ProjectOverview projectOverview;
  @Mock
  private Status status;
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
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRun(uk.ac.bbsrc.tgac.miso.core.data.Run)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveRun() throws IOException {
    final long expectedReturn = 1L;

    when(run.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveRun(run)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveRun(run));

    verify(backingManager).saveRun(run);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRun(uk.ac.bbsrc.tgac.miso.core.data.Run)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveRunThrows() throws IOException {

    when(run.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Run");
    userAuthMisoRequestManager.saveRun(run);

    verify(backingManager, never()).saveRun(run);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRunQC(uk.ac.bbsrc.tgac.miso.core.data.RunQC)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveRunQC() throws IOException {
    final long expectedReturn = 1L;
    when(runQC.getRun()).thenReturn(run);
    when(run.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveRunQC(runQC)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveRunQC(runQC));

    verify(backingManager).saveRunQC(runQC);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRunQC(uk.ac.bbsrc.tgac.miso.core.data.RunQC)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveRunQCThrows() throws IOException {
    when(runQC.getRun()).thenReturn(run);
    when(run.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to the parent Run");
    userAuthMisoRequestManager.saveRunQC(runQC);

    verify(backingManager, never()).saveRunQC(runQC);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSample(uk.ac.bbsrc.tgac.miso.core.data.Sample)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveSample() throws IOException {
    final long expectedReturn = 1L;
    when(sample.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveSample(sample)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveSample(sample));

    verify(backingManager).saveSample(sample);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSample(uk.ac.bbsrc.tgac.miso.core.data.Sample)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveSampleThrows() throws IOException {
    when(sample.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Sample");
    userAuthMisoRequestManager.saveSample(sample);

    verify(backingManager, never()).saveSample(sample);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSampleQC(uk.ac.bbsrc.tgac.miso.core.data.SampleQC)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveSampleQC() throws IOException {
    final long expectedReturn = 1L;
    when(sampleQC.getSample()).thenReturn(sample);
    when(sample.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveSampleQC(sampleQC)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveSampleQC(sampleQC));

    verify(backingManager).saveSampleQC(sampleQC);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSampleQC(uk.ac.bbsrc.tgac.miso.core.data.SampleQC)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveSampleQCThrows() throws IOException {
    when(sampleQC.getSample()).thenReturn(sample);
    when(sample.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to the parent Sample");
    userAuthMisoRequestManager.saveSampleQC(sampleQC);

    verify(backingManager, never()).saveSampleQC(sampleQC);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibrary(uk.ac.bbsrc.tgac.miso.core.data.Library)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveLibrary() throws IOException {
    final long expectedReturn = 1L;
    when(library.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveLibrary(library)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveLibrary(library));

    verify(backingManager).saveLibrary(library);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibrary(uk.ac.bbsrc.tgac.miso.core.data.Library)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveLibraryThrows() throws IOException {
    when(library.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Library");
    userAuthMisoRequestManager.saveLibrary(library);

    verify(backingManager, never()).saveLibrary(library);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibraryDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveLibraryDilution() throws IOException {
    final long expectedReturn = 1L;
    when(libraryDilution.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveLibraryDilution(libraryDilution)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveLibraryDilution(libraryDilution));

    verify(backingManager).saveLibraryDilution(libraryDilution);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibraryDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveLibraryDilutionThrows() throws IOException {
    when(libraryDilution.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this LibraryDilution");
    userAuthMisoRequestManager.saveLibraryDilution(libraryDilution);

    verify(backingManager, never()).saveLibraryDilution(libraryDilution);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibraryNote(uk.ac.bbsrc.tgac.miso.core.data.Library, com.eaglegenomics.simlims.core.Note)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveLibraryNote() throws IOException {
    when(library.userCanWrite(any(User.class))).thenReturn(true);
    userAuthMisoRequestManager.saveLibraryNote(library, note);
    verify(backingManager).saveLibraryNote(library, note);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibraryNote(uk.ac.bbsrc.tgac.miso.core.data.Library, com.eaglegenomics.simlims.core.Note)}
   * .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveLibraryNoteThrows() throws IOException {
    when(library.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Library");
    userAuthMisoRequestManager.saveLibraryNote(library, note);

    verify(backingManager, never()).saveLibraryNote(library, note);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibraryQC(uk.ac.bbsrc.tgac.miso.core.data.LibraryQC)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveLibraryQC() throws IOException {
    final long expectedReturn = 1L;
    when(libraryQC.getLibrary()).thenReturn(library);
    when(library.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveLibraryQC(libraryQC)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveLibraryQC(libraryQC));

    verify(backingManager).saveLibraryQC(libraryQC);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibraryQC(uk.ac.bbsrc.tgac.miso.core.data.LibraryQC)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveLibraryQCThrows() throws IOException {
    when(libraryQC.getLibrary()).thenReturn(library);
    when(library.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Library");
    userAuthMisoRequestManager.saveLibraryQC(libraryQC);

    verify(backingManager, never()).saveLibraryQC(libraryQC);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePool(uk.ac.bbsrc.tgac.miso.core.data.Pool)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSavePool() throws IOException {
    final long expectedReturn = 1L;
    when(pool.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.savePool(pool)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.savePool(pool));

    verify(backingManager).savePool(pool);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePool(uk.ac.bbsrc.tgac.miso.core.data.Pool)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSavePoolThrows() throws IOException {
    when(pool.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Pool");
    userAuthMisoRequestManager.savePool(pool);

    verify(backingManager, never()).savePool(pool);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePoolQC(uk.ac.bbsrc.tgac.miso.core.data.PoolQC)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSavePoolQC() throws IOException {
    final long expectedReturn = 1L;
    when(poolQC.getPool()).thenReturn(pool);
    when(pool.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.savePoolQC(poolQC)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.savePoolQC(poolQC));

    verify(backingManager).savePoolQC(poolQC);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePoolQC(uk.ac.bbsrc.tgac.miso.core.data.PoolQC)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSavePoolQCThrows() throws IOException {
    when(poolQC.getPool()).thenReturn(pool);
    when(pool.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Pool");
    userAuthMisoRequestManager.savePoolQC(poolQC);

    verify(backingManager, never()).savePoolQC(poolQC);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveEmPCR(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR)} .
   */
  @Test
  public void testSaveEmPCR() throws IOException {
    final long expectedReturn = 1L;
    when(emPCR.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveEmPCR(emPCR)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveEmPCR(emPCR));
    verify(backingManager).saveEmPCR(emPCR);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveEmPCR(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSaveEmPCRThrows() throws IOException {
    when(emPCR.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this EmPCR");
    userAuthMisoRequestManager.saveEmPCR(emPCR);

    verify(backingManager, never()).saveEmPCR(emPCR);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveEmPCRDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution)}
   * .
   */
  @Test
  public void testSaveEmPCRDilution() throws IOException {
    final long expectedReturn = 1L;
    when(emPCRDilution.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveEmPCRDilution(emPCRDilution)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveEmPCRDilution(emPCRDilution));
    verify(backingManager).saveEmPCRDilution(emPCRDilution);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveEmPCRDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution)}
   * .
   */
  @Test
  public void testSaveEmPCRDilutionThrows() throws IOException {
    when(emPCRDilution.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this EmPCRDilution");
    userAuthMisoRequestManager.saveEmPCRDilution(emPCRDilution);

    verify(backingManager, never()).saveEmPCRDilution(emPCRDilution);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveStudy(uk.ac.bbsrc.tgac.miso.core.data.Study)}
   * .
   */
  @Test
  public void testSaveStudy() throws IOException {
    final long expectedReturn = 1L;
    when(study.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveStudy(study)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveStudy(study));
    verify(backingManager).saveStudy(study);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveStudy(uk.ac.bbsrc.tgac.miso.core.data.Study)}
   * .
   */
  @Test
  public void testSaveStudyThrows() throws IOException {
    when(study.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Study");
    userAuthMisoRequestManager.saveStudy(study);

    verify(backingManager, never()).saveStudy(study);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSequencerPoolPartition(uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition)}
   * .
   */
  @Test
  public void testSaveSequencerPoolPartition() throws IOException {
    final long expectedReturn = 1L;
    when(sequencerPoolParition.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveSequencerPoolPartition(sequencerPoolParition)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveSequencerPoolPartition(sequencerPoolParition));
    verify(backingManager).saveSequencerPoolPartition(sequencerPoolParition);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSequencerPoolPartition(uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition)}
   * .
   */
  @Test
  public void testSaveSequencerPoolPartitionThrows() throws IOException {
    when(sequencerPoolParition.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Partition");
    userAuthMisoRequestManager.saveSequencerPoolPartition(sequencerPoolParition);

    verify(backingManager, never()).saveSequencerPoolPartition(sequencerPoolParition);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSequencerPartitionContainer(uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer)}
   * .
   */
  @Test
  public void testSaveSequencerPartitionContainer() throws IOException {
    final long expectedReturn = 1L;

    when(sequencerPartitionContainer.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveSequencerPartitionContainer(sequencerPartitionContainer)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveSequencerPartitionContainer(sequencerPartitionContainer));
    verify(backingManager).saveSequencerPartitionContainer(sequencerPartitionContainer);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSequencerPartitionContainer(uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer)}
   * .
   */
  @Test
  public void testSaveSequencerPartitionContainerThrows() throws IOException {
    when(sequencerPartitionContainer.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this SequencerPartitionContainer");
    userAuthMisoRequestManager.saveSequencerPartitionContainer(sequencerPartitionContainer);

    verify(backingManager, never()).saveSequencerPartitionContainer(sequencerPartitionContainer);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSubmission(uk.ac.bbsrc.tgac.miso.core.data.Submission)} .
   */
  @Test
  public void testSaveSubmission() throws IOException {
    final long expectedReturn = 1L;

    when(submission.userCanWrite(any(User.class))).thenReturn(true);
    when(backingManager.saveSubmission(submission)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, userAuthMisoRequestManager.saveSubmission(submission));
    verify(backingManager).saveSubmission(submission);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSubmission(uk.ac.bbsrc.tgac.miso.core.data.Submission)} .
   */
  @Test
  public void testSaveSubmissionThrows() throws IOException {
    when(submission.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Submission");
    userAuthMisoRequestManager.saveSubmission(submission);

    verify(backingManager, never()).saveSubmission(submission);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerPoolPartitionById(long)} .
   */
  @Test
  public void testGetSequencerPoolPartitionById() throws IOException {
    long inputId = 1L;
    when(backingManager.getSequencerPoolPartitionById(inputId)).thenReturn(sequencerPoolParition);
    when(sequencerPoolParition.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(sequencerPoolParition, userAuthMisoRequestManager.getSequencerPoolPartitionById(inputId));

    verify(backingManager).getSequencerPoolPartitionById(inputId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerPoolPartitionById(long)} .
   */
  @Test
  public void testGetSequencerPoolPartitionByIdThrows() throws IOException {
    long inputId = 1L;
    when(backingManager.getSequencerPoolPartitionById(inputId)).thenReturn(sequencerPoolParition);
    when(sequencerPoolParition.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Partition " + inputId);

    userAuthMisoRequestManager.getSequencerPoolPartitionById(inputId);

    verify(backingManager).getSequencerPoolPartitionById(inputId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolById(long)} .
   */

  @Test
  public void testGetPoolById() throws IOException {
    long inputId = 1L;
    when(backingManager.getPoolById(inputId)).thenReturn(pool);
    when(pool.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(pool, userAuthMisoRequestManager.getPoolById(inputId));

    verify(backingManager).getPoolById(inputId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolById(long)} .
   */

  @Test
  public void testGetPoolByIdThrows() throws IOException {
    long inputId = 1L;
    when(backingManager.getPoolById(inputId)).thenReturn(pool);
    when(pool.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Pool " + inputId);

    userAuthMisoRequestManager.getPoolById(inputId);

    verify(backingManager).getPoolById(inputId);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolByBarcode(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */
  @Test
  public void testGetPoolByBarcodeStringPlatformType() throws IOException {
    String barcode = "";
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getPoolByBarcode(barcode, platformType)).thenReturn(pool);
    when(pool.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(pool, userAuthMisoRequestManager.getPoolByBarcode(barcode, platformType));

    verify(backingManager).getPoolByBarcode(barcode, platformType);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolByBarcode(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */
  @Test
  public void testGetPoolByBarcodeStringPlatformTypeThrows() throws IOException {
    String barcode = "barcode";
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getPoolByBarcode(barcode, platformType)).thenReturn(pool);
    when(pool.userCanRead(any(User.class))).thenReturn(false);
    Long poolId = 1L;
    when(pool.getId()).thenReturn(poolId);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Pool " + poolId);

    userAuthMisoRequestManager.getPoolByBarcode(barcode, platformType);

    verify(backingManager).getPoolByBarcode(barcode, platformType);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolByIdBarcode(java.lang.String)} .
   */
  @Test
  public void testGetPoolByIdBarcode() throws IOException {
    String barcode = "barcode";
    when(backingManager.getPoolByIdBarcode(barcode)).thenReturn(pool);
    when(pool.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(pool, userAuthMisoRequestManager.getPoolByIdBarcode(barcode));

    verify(backingManager).getPoolByIdBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolByIdBarcode(java.lang.String)} .
   */
  @Test
  public void testGetPoolByIdBarcodeThrows() throws IOException {
    String barcode = "barcode";
    when(backingManager.getPoolByIdBarcode(barcode)).thenReturn(pool);
    when(pool.userCanRead(any(User.class))).thenReturn(false);
    Long poolId = 1L;
    when(pool.getId()).thenReturn(poolId);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Pool " + poolId);

    userAuthMisoRequestManager.getPoolByIdBarcode(barcode);

    verify(backingManager).getPoolByIdBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetPoolByBarcodeString() throws IOException {
    String barcode = "barcode";
    when(backingManager.getPoolByBarcode(barcode)).thenReturn(pool);
    when(pool.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(pool, userAuthMisoRequestManager.getPoolByBarcode(barcode));

    verify(backingManager).getPoolByBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetPoolByBarcodeStringThrows() throws IOException {
    String barcode = "barcode";
    when(backingManager.getPoolByBarcode(barcode)).thenReturn(pool);
    when(pool.userCanRead(any(User.class))).thenReturn(false);
    Long poolId = 1L;
    when(pool.getId()).thenReturn(poolId);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Pool " + poolId);

    userAuthMisoRequestManager.getPoolByBarcode(barcode);

    verify(backingManager).getPoolByBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolQCById(long)} .
   */
  @Test
  public void testGetPoolQCById() throws IOException {
    long inputId = 1L;
    when(backingManager.getPoolQCById(inputId)).thenReturn(poolQC);
    when(poolQC.getPool()).thenReturn(pool);
    when(pool.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(poolQC, userAuthMisoRequestManager.getPoolQCById(inputId));

    verify(backingManager).getPoolQCById(inputId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolQCById(long)} .
   */
  @Test
  public void testGetPoolQCByIdThrows() throws IOException {
    long qcId = 1L;
    long poolId = 2L;
    when(backingManager.getPoolQCById(qcId)).thenReturn(poolQC);
    when(poolQC.getPool()).thenReturn(pool);
    when(poolQC.getId()).thenReturn(qcId);
    when(pool.userCanRead(any(User.class))).thenReturn(false);
    when(pool.getId()).thenReturn(poolId);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read parent Pool " + poolId + " for PoolQC " + qcId);

    userAuthMisoRequestManager.getPoolQCById(qcId);

    verify(backingManager).getPoolQCById(qcId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryById(long)} .
   */
  @Test
  public void testGetLibraryById() throws IOException {
    long inputId = 1L;
    when(backingManager.getLibraryById(inputId)).thenReturn(library);
    when(library.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(library, userAuthMisoRequestManager.getLibraryById(inputId));

    verify(backingManager).getLibraryById(inputId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryById(long)} .
   */
  @Test
  public void testGetLibraryByIdThrows() throws IOException {
    long inputId = 1L;
    when(backingManager.getLibraryById(inputId)).thenReturn(library);
    when(pool.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Library " + inputId);

    userAuthMisoRequestManager.getLibraryById(inputId);

    verify(backingManager).getLibraryById(inputId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetLibraryByBarcode() throws IOException {
    String barcode = "barcode";
    when(backingManager.getLibraryByBarcode(barcode)).thenReturn(library);
    when(library.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(library, userAuthMisoRequestManager.getLibraryByBarcode(barcode));

    verify(backingManager).getLibraryByBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetLibraryByBarcodeThrows() throws IOException {
    String barcode = "barcode";
    when(backingManager.getLibraryByBarcode(barcode)).thenReturn(library);
    when(library.userCanRead(any(User.class))).thenReturn(false);
    long libraryId = 1L;
    when(library.getId()).thenReturn(libraryId);
    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Library " + libraryId);

    userAuthMisoRequestManager.getLibraryByBarcode(barcode);

    verify(backingManager).getLibraryByBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryByAlias(java.lang.String)} .
   */
  @Test
  public void testListLibraryByAlias() throws IOException {
    String alias = "alias";
    when(backingManager.listLibrariesByAlias(alias)).thenReturn(Sets.newHashSet(library));
    when(library.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(library, userAuthMisoRequestManager.listLibrariesByAlias(alias).iterator().next());

    verify(backingManager).listLibrariesByAlias(alias);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryByAlias(java.lang.String)} .
   */
  @Test
  public void testListLibraryByAliasThrows() throws IOException {
    String alias = "alias";
    when(backingManager.listLibrariesByAlias(alias)).thenReturn(Sets.newHashSet(library));
    when(library.userCanRead(any(User.class))).thenReturn(false);
    long libraryId = 1L;
    when(library.getId()).thenReturn(libraryId);
    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Library " + libraryId);

    userAuthMisoRequestManager.listLibrariesByAlias(alias);

    verify(backingManager).listLibrariesByAlias(alias);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getDilutionByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetDilutionByBarcode() throws IOException {
    String barcode = "barcode";
    when(backingManager.getDilutionByBarcode(barcode)).thenReturn(dilution);
    when(dilution.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(dilution, userAuthMisoRequestManager.getDilutionByBarcode(barcode));

    verify(backingManager).getDilutionByBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getDilutionByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetDilutionByBarcodeThrows() throws IOException {
    String barcode = "barcode";
    when(backingManager.getDilutionByBarcode(barcode)).thenReturn(dilution);
    when(dilution.userCanRead(any(User.class))).thenReturn(false);
    long dilutionId = 1L;
    when(dilution.getId()).thenReturn(dilutionId);
    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Dilution " + dilutionId);

    userAuthMisoRequestManager.getDilutionByBarcode(barcode);

    verify(backingManager).getDilutionByBarcode(barcode);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getDilutionByIdAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testGetDilutionByIdAndPlatform() throws IOException {
    long id = 1L;
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getDilutionByIdAndPlatform(id, platformType)).thenReturn(dilution);
    when(dilution.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(dilution, userAuthMisoRequestManager.getDilutionByIdAndPlatform(id, platformType));

    verify(backingManager).getDilutionByIdAndPlatform(id, platformType);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getDilutionByIdAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */

  @Test
  public void testGetDilutionByIdAndPlatformThrows() throws IOException {
    long dilutionId = 1L;
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getDilutionByIdAndPlatform(dilutionId, platformType)).thenReturn(dilution);
    when(dilution.userCanRead(any(User.class))).thenReturn(false);
    when(dilution.getId()).thenReturn(dilutionId);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Dilution " + dilutionId);

    userAuthMisoRequestManager.getDilutionByIdAndPlatform(dilutionId, platformType);

    verify(backingManager).getDilutionByIdAndPlatform(dilutionId, platformType);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getDilutionByBarcodeAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */
  @Test
  public void testGetDilutionByBarcodeAndPlatform() throws IOException {
    String barcode = "barcode";
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getDilutionByBarcodeAndPlatform(barcode, platformType)).thenReturn(dilution);
    when(dilution.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(dilution, userAuthMisoRequestManager.getDilutionByBarcodeAndPlatform(barcode, platformType));

    verify(backingManager).getDilutionByBarcodeAndPlatform(barcode, platformType);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getDilutionByBarcodeAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */
  @Test
  public void testGetDilutionByBarcodeAndPlatformThrows() throws IOException {
    String barcode = "barcode";
    long id = 1L;
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getDilutionByBarcodeAndPlatform(barcode, platformType)).thenReturn(dilution);
    when(dilution.userCanRead(any(User.class))).thenReturn(false);
    when(dilution.getId()).thenReturn(id);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Dilution " + id);

    userAuthMisoRequestManager.getDilutionByBarcodeAndPlatform(barcode, platformType);

    verify(backingManager).getDilutionByBarcodeAndPlatform(barcode, platformType);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryDilutionById(long)} .
   */
  @Test
  public void testGetLibraryDilutionById() throws IOException {
    long dilutionId = 1L;
    when(backingManager.getLibraryDilutionById(dilutionId)).thenReturn(libraryDilution);
    when(libraryDilution.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(libraryDilution, userAuthMisoRequestManager.getLibraryDilutionById(dilutionId));

    verify(backingManager).getLibraryDilutionById(dilutionId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryDilutionById(long)} .
   */
  @Test
  public void testGetLibraryDilutionByIdThrows() throws IOException {
    long dilutionId = 1L;
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getLibraryDilutionById(dilutionId)).thenReturn(libraryDilution);
    when(libraryDilution.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read LibraryDilution " + dilutionId);

    userAuthMisoRequestManager.getLibraryDilutionById(dilutionId);

    verify(backingManager).getLibraryDilutionById(dilutionId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryDilutionByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetLibraryDilutionByBarcode() throws IOException {
    String barcode = "barcode";
    when(backingManager.getLibraryDilutionByBarcode(barcode)).thenReturn(libraryDilution);
    when(libraryDilution.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(libraryDilution, userAuthMisoRequestManager.getLibraryDilutionByBarcode(barcode));

    verify(backingManager).getLibraryDilutionByBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryDilutionByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetLibraryDilutionByBarcodeThrows() throws IOException {
    String barcode = "barcode";
    when(backingManager.getLibraryDilutionByBarcode(barcode)).thenReturn(libraryDilution);
    when(libraryDilution.userCanRead(any(User.class))).thenReturn(false);
    long id = 1L;
    when(libraryDilution.getId()).thenReturn(id);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read LibraryDilution " + id);

    userAuthMisoRequestManager.getLibraryDilutionByBarcode(barcode);

    verify(backingManager).getLibraryDilutionByBarcode(barcode);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryDilutionByBarcodeAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */
  @Test
  public void testGetLibraryDilutionByBarcodeAndPlatform() throws IOException {
    String barcode = "barcode";
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getLibraryDilutionByBarcodeAndPlatform(barcode, platformType)).thenReturn(libraryDilution);
    when(libraryDilution.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(libraryDilution, userAuthMisoRequestManager.getLibraryDilutionByBarcodeAndPlatform(barcode, platformType));

    verify(backingManager).getLibraryDilutionByBarcodeAndPlatform(barcode, platformType);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryDilutionByBarcodeAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */
  @Test
  public void testGetLibraryDilutionByBarcodeAndPlatformThrows() throws IOException {
    String barcode = "barcode";
    long id = 1L;
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getLibraryDilutionByBarcodeAndPlatform(barcode, platformType)).thenReturn(libraryDilution);
    when(libraryDilution.userCanRead(any(User.class))).thenReturn(false);
    when(libraryDilution.getId()).thenReturn(id);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read LibraryDilution " + id);

    userAuthMisoRequestManager.getLibraryDilutionByBarcodeAndPlatform(barcode, platformType);

    verify(backingManager).getLibraryDilutionByBarcodeAndPlatform(barcode, platformType);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryQCById(long)} .
   */
  @Test
  public void testGetLibraryQCById() throws IOException {
    long id = 1L;
    when(backingManager.getLibraryQCById(id)).thenReturn(libraryQC);
    when(libraryQC.getLibrary()).thenReturn(library);
    when(library.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(libraryQC, userAuthMisoRequestManager.getLibraryQCById(id));

    verify(backingManager).getLibraryQCById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryQCById(long)} .
   */
  @Test
  public void testGetLibraryQCByIdThrows() throws IOException {
    long qcId = 1L;
    long libraryId = 2;
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getLibraryQCById(qcId)).thenReturn(libraryQC);
    when(libraryQC.userCanRead(any(User.class))).thenReturn(false);
    when(libraryQC.getLibrary()).thenReturn(library);
    when(library.getId()).thenReturn(libraryId);
    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read parent Library " + libraryId + " for LibraryQC " + qcId);

    userAuthMisoRequestManager.getLibraryQCById(qcId);

    verify(backingManager).getLibraryQCById(qcId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRById(long)} .
   */
  @Test
  public void testGetEmPCRById() throws IOException {
    long id = 1L;
    when(backingManager.getEmPCRById(id)).thenReturn(emPCR);
    when(emPCR.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(emPCR, userAuthMisoRequestManager.getEmPCRById(id));

    verify(backingManager).getEmPCRById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRById(long)} .
   */
  @Test
  public void testGetEmPCRByIdThrows() throws IOException {
    long qcId = 1L;
    when(backingManager.getEmPCRById(qcId)).thenReturn(emPCR);
    when(emPCR.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read emPCR " + qcId);

    userAuthMisoRequestManager.getEmPCRById(qcId);

    verify(backingManager).getEmPCRById(qcId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRDilutionById(long)} .
   */
  @Test
  public void testGetEmPCRDilutionById() throws IOException {
    long id = 1L;
    when(backingManager.getEmPCRDilutionById(id)).thenReturn(emPCRDilution);
    when(emPCRDilution.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(emPCRDilution, userAuthMisoRequestManager.getEmPCRDilutionById(id));

    verify(backingManager).getEmPCRDilutionById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRDilutionById(long)} .
   */
  @Test
  public void testGetEmPCRDilutionByIdThrows() throws IOException {
    long qcId = 1L;
    when(backingManager.getEmPCRDilutionById(qcId)).thenReturn(emPCRDilution);
    when(emPCRDilution.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read emPCRDilution " + qcId);

    userAuthMisoRequestManager.getEmPCRDilutionById(qcId);

    verify(backingManager).getEmPCRDilutionById(qcId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRDilutionByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetEmPCRDilutionByBarcode() throws IOException {
    String barcode = "barcode";
    when(backingManager.getEmPCRDilutionByBarcode(barcode)).thenReturn(emPCRDilution);
    when(emPCRDilution.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(emPCRDilution, userAuthMisoRequestManager.getEmPCRDilutionByBarcode(barcode));

    verify(backingManager).getEmPCRDilutionByBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRDilutionByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetEmPCRDilutionByBarcodeThrows() throws IOException {
    String barcode = "barcode";
    when(backingManager.getEmPCRDilutionByBarcode(barcode)).thenReturn(emPCRDilution);
    when(emPCRDilution.userCanRead(any(User.class))).thenReturn(false);
    long id = 1L;
    when(emPCRDilution.getId()).thenReturn(id);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read emPCRDilution " + id);

    userAuthMisoRequestManager.getEmPCRDilutionByBarcode(barcode);

    verify(backingManager).getEmPCRDilutionByBarcode(barcode);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRDilutionByBarcodeAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */
  @Test
  public void testGetEmPCRDilutionByBarcodeAndPlatform() throws IOException {
    String barcode = "barcode";
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getEmPCRDilutionByBarcodeAndPlatform(barcode, platformType)).thenReturn(emPCRDilution);
    when(emPCRDilution.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(emPCRDilution, userAuthMisoRequestManager.getEmPCRDilutionByBarcodeAndPlatform(barcode, platformType));

    verify(backingManager).getEmPCRDilutionByBarcodeAndPlatform(barcode, platformType);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRDilutionByBarcodeAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
   * .
   */
  @Test
  public void testGetEmPCRDilutionByBarcodeAndPlatformThrows() throws IOException {
    String barcode = "barcode";
    long id = 1L;
    PlatformType platformType = PlatformType.ILLUMINA;
    when(backingManager.getEmPCRDilutionByBarcodeAndPlatform(barcode, platformType)).thenReturn(emPCRDilution);
    when(emPCRDilution.userCanRead(any(User.class))).thenReturn(false);
    when(emPCRDilution.getId()).thenReturn(id);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read emPCRDilution " + id);

    userAuthMisoRequestManager.getEmPCRDilutionByBarcodeAndPlatform(barcode, platformType);

    verify(backingManager).getEmPCRDilutionByBarcodeAndPlatform(barcode, platformType);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerPartitionContainerById(long)} .
   */
  @Test
  public void testGetSequencerPartitionContainerById() throws IOException {
    long id = 1L;
    when(backingManager.getSequencerPartitionContainerById(id)).thenReturn(sequencerPartitionContainer);
    when(sequencerPartitionContainer.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(sequencerPartitionContainer, userAuthMisoRequestManager.getSequencerPartitionContainerById(id));

    verify(backingManager).getSequencerPartitionContainerById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerPartitionContainerById(long)} .
   */
  @Test
  public void testGetSequencerPartitionContainerByIdThrows() throws IOException {
    long id = 1L;
    when(backingManager.getSequencerPartitionContainerById(id)).thenReturn(sequencerPartitionContainer);
    when(sequencerPartitionContainer.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read SequencerPartitionContainer " + id);

    userAuthMisoRequestManager.getSequencerPartitionContainerById(id);

    verify(backingManager).getSequencerPartitionContainerById(id);
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
    when(project.getProjectId()).thenReturn(projectId);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read parent Project " + projectId + " for ProjectOverview " + projectOverviewId);

    userAuthMisoRequestManager.getProjectOverviewById(projectOverviewId);

    verify(backingManager).getProjectOverviewById(projectOverviewId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunById(long)} .
   */
  @Test
  public void testGetRunById() throws IOException {
    long id = 1L;
    when(backingManager.getRunById(id)).thenReturn(run);
    when(run.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(run, userAuthMisoRequestManager.getRunById(id));

    verify(backingManager).getRunById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunById(long)} .
   */
  @Test
  public void testGetRunByIdThrows() throws IOException {
    long id = 1L;
    when(backingManager.getRunById(id)).thenReturn(run);
    when(run.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Run " + id);

    userAuthMisoRequestManager.getRunById(id);

    verify(backingManager).getRunById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunByAlias(java.lang.String)} .
   */
  @Test
  public void testGetRunByAlias() throws IOException {
    String alias = "alias";
    when(backingManager.getRunByAlias(alias)).thenReturn(run);
    when(run.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(run, userAuthMisoRequestManager.getRunByAlias(alias));

    verify(backingManager).getRunByAlias(alias);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunByAlias(java.lang.String)} .
   */
  @Test
  public void testGetRunByAliasThrows() throws IOException {
    String alias = "alias";
    when(backingManager.getRunByAlias(alias)).thenReturn(run);
    when(run.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Run ");

    userAuthMisoRequestManager.getRunByAlias(alias);

    verify(backingManager).getProjectByAlias(alias);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunQCById(long)} .
   */
  @Test
  public void testGetRunQCById() throws IOException {
    long id = 1L;
    when(backingManager.getRunQCById(id)).thenReturn(runQC);
    when(runQC.getRun()).thenReturn(run);
    when(run.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(runQC, userAuthMisoRequestManager.getRunQCById(id));

    verify(backingManager).getRunQCById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunQCById(long)} .
   */
  @Test
  public void testGetRunQCByIdThrows() throws IOException {
    long id = 1L;
    long runId = 2L;
    when(backingManager.getRunQCById(id)).thenReturn(runQC);
    when(runQC.getRun()).thenReturn(run);
    when(run.userCanRead(any(User.class))).thenReturn(false);
    when(run.getId()).thenReturn(runId);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read parent Run " + runId + " for RunQC " + id);

    userAuthMisoRequestManager.getRunQCById(id);

    verify(backingManager).getRunQCById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleById(long)} .
   */
  @Test
  public void testGetSampleById() throws IOException {
    long id = 1L;
    when(backingManager.getSampleById(id)).thenReturn(sample);
    when(sample.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(sample, userAuthMisoRequestManager.getSampleById(id));

    verify(backingManager).getSampleById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleById(long)} .
   */
  @Test
  public void testGetSampleByIdThrows() throws IOException {
    long id = 1L;
    when(backingManager.getSampleById(id)).thenReturn(sample);
    when(sample.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Sample " + id);

    userAuthMisoRequestManager.getSampleById(id);

    verify(backingManager).getSampleById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetSampleByBarcode() throws IOException {
    String barcode = "barcode";
    when(backingManager.getSampleByBarcode(barcode)).thenReturn(sample);
    when(sample.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(sample, userAuthMisoRequestManager.getSampleByBarcode(barcode));

    verify(backingManager).getSampleByBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleByBarcode(java.lang.String)} .
   */
  @Test
  public void testGetSampleByBarcodeThrows() throws IOException {
    String barcode = "barcode";
    when(backingManager.getSampleByBarcode(barcode)).thenReturn(sample);
    when(sample.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Sample ");

    userAuthMisoRequestManager.getSampleByBarcode(barcode);

    verify(backingManager).getSampleByBarcode(barcode);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleQCById(long)} .
   */
  @Test
  public void testGetSampleQCById() throws IOException {
    long id = 1L;
    when(backingManager.getSampleQCById(id)).thenReturn(sampleQC);
    when(sampleQC.getSample()).thenReturn(sample);
    when(sample.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(sampleQC, userAuthMisoRequestManager.getSampleQCById(id));

    verify(backingManager).getSampleQCById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleQCById(long)} .
   */
  @Test
  public void testGetSampleQCByIdThrows() throws IOException {
    long id = 1L, sampleId = 2L;

    when(backingManager.getSampleQCById(id)).thenReturn(sampleQC);
    when(sampleQC.getSample()).thenReturn(sample);
    when(sample.userCanRead(any(User.class))).thenReturn(false);
    when(sample.getId()).thenReturn(sampleId);

    thrown.expect(AuthorizationIOException.class);
    thrown.expectMessage("User null cannot read parent Sample " + sampleId + " for SampleQC " + id);

    userAuthMisoRequestManager.getSampleQCById(id);

    verify(backingManager).getSampleQCById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getStatusByRunName(java.lang.String)} .
   */
  @Test
  public void testGetStatusByRunName() throws IOException {
    String runName = "runName";
    when(backingManager.getRunByAlias(runName)).thenReturn(run);
    when(run.userCanRead(any(User.class))).thenReturn(true);
    when(backingManager.getStatusByRunName(runName)).thenReturn(status);

    assertEquals(status, userAuthMisoRequestManager.getStatusByRunName(runName));

    verify(backingManager).getStatusByRunName(runName);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getStatusByRunName(java.lang.String)} .
   */
  @Test
  public void testGetStatusByRunNameThrows() throws IOException {
    String runName = "runName";
    when(backingManager.getRunByAlias(runName)).thenReturn(run);
    when(run.userCanRead(any(User.class))).thenReturn(false);
    when(backingManager.getStatusByRunName(runName)).thenReturn(status);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read parent Run 0 for Status");

    userAuthMisoRequestManager.getStatusByRunName(runName);

    verify(backingManager).getStatusByRunName(runName);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getStudyById(long)} .
   */
  @Test
  public void testGetStudyById() throws IOException {
    long id = 1L;
    when(backingManager.getStudyById(id)).thenReturn(study);
    when(study.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(study, userAuthMisoRequestManager.getStudyById(id));

    verify(backingManager).getStudyById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getStudyById(long)} .
   */
  @Test
  public void testGetStudyByIdThrows() throws IOException {
    long id = 1L;
    when(backingManager.getStudyById(id)).thenReturn(study);
    when(study.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Study " + id);

    userAuthMisoRequestManager.getStudyById(id);

    verify(backingManager).getStudyById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSubmissionById(long)} .
   */
  @Test
  public void testGetSubmissionById() throws IOException {
    long id = 1L;
    when(backingManager.getSubmissionById(id)).thenReturn(submission);
    when(submission.userCanRead(any(User.class))).thenReturn(true);

    assertEquals(submission, userAuthMisoRequestManager.getSubmissionById(id));

    verify(backingManager).getSubmissionById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSubmissionById(long)} .
   */
  @Test
  public void testGetSubmissionByIdThrows() throws IOException {
    long id = 1L;

    when(backingManager.getSubmissionById(id)).thenReturn(submission);
    when(sample.userCanRead(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Submission " + id);

    userAuthMisoRequestManager.getSubmissionById(id);

    verify(backingManager).getSubmissionById(id);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllProjects()} .
   */
  @Test
  public void testListAllProjects() throws IOException {
    Set projects = new HashSet();
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
    Set projects = new HashSet();
    projects.add(project1);
    projects.add(project2);
    projects.add(project3);
    when(backingManager.listAllProjects()).thenReturn(projects);
    when(project1.userCanRead(any(User.class))).thenReturn(true);
    when(project2.userCanRead(any(User.class))).thenReturn(false);
    when(project3.userCanRead(any(User.class))).thenReturn(true);

    Set filtered = new HashSet();
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
