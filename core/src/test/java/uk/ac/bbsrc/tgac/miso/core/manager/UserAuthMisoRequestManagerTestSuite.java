package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.junit.Assert.*;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.Mockito.*;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;

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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveProject(uk.ac.bbsrc.tgac.miso.core.data.Project)}
	 * . Tests this method calls expected underlying methods given correct
	 * parameters.
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveProject(uk.ac.bbsrc.tgac.miso.core.data.Project)}
	 * . Tests this method throws an IOException when writeCheck fails.
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
		final long expectedReturn = 1L;

		when(overview.getProject()).thenReturn(project);
		when(project.userCanWrite(any(User.class))).thenReturn(true);
		when(backingManager.saveProjectOverviewNote(overview, note)).thenReturn(expectedReturn);

		assertEquals(expectedReturn, userAuthMisoRequestManager.saveProjectOverviewNote(overview, note));

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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRun(uk.ac.bbsrc.tgac.miso.core.data.Run)}
	 * .
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
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRun(uk.ac.bbsrc.tgac.miso.core.data.Run)}
	 * .
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
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRunQC(uk.ac.bbsrc.tgac.miso.core.data.RunQC)}
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
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRunQC(uk.ac.bbsrc.tgac.miso.core.data.RunQC)}
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSample(uk.ac.bbsrc.tgac.miso.core.data.Sample)}
	 * .
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSample(uk.ac.bbsrc.tgac.miso.core.data.Sample)}
	 * .
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSampleQC(uk.ac.bbsrc.tgac.miso.core.data.SampleQC)}
	 * .
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSampleQC(uk.ac.bbsrc.tgac.miso.core.data.SampleQC)}
	 * .
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSampleNote(uk.ac.bbsrc.tgac.miso.core.data.Sample, com.eaglegenomics.simlims.core.Note)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSaveSampleNote() throws IOException {
		final long expectedReturn = 1L;
		when(sample.userCanWrite(any(User.class))).thenReturn(true);
		when(backingManager.saveSampleNote(sample, note)).thenReturn(expectedReturn);

		assertEquals(expectedReturn, userAuthMisoRequestManager.saveSampleNote(sample, note));

		verify(backingManager).saveSampleNote(sample, note);
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSampleNote(uk.ac.bbsrc.tgac.miso.core.data.Sample, com.eaglegenomics.simlims.core.Note)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSaveSampleNoteThrows() throws IOException {
		when(sample.userCanWrite(any(User.class))).thenReturn(false);

		thrown.expect(IOException.class);
		thrown.expectMessage("User null cannot write to this Sample");
		userAuthMisoRequestManager.saveSampleNote(sample, note);

		verify(backingManager, never()).saveSampleNote(sample, note);
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibrary(uk.ac.bbsrc.tgac.miso.core.data.Library)}
	 * .
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibrary(uk.ac.bbsrc.tgac.miso.core.data.Library)}
	 * .
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
		final long expectedReturn = 1L;
		when(library.userCanWrite(any(User.class))).thenReturn(true);
		when(backingManager.saveLibraryNote(library, note)).thenReturn(expectedReturn);

		assertEquals(expectedReturn, userAuthMisoRequestManager.saveLibraryNote(library, note));

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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibraryQC(uk.ac.bbsrc.tgac.miso.core.data.LibraryQC)}
	 * .
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveLibraryQC(uk.ac.bbsrc.tgac.miso.core.data.LibraryQC)}
	 * .
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
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePool(uk.ac.bbsrc.tgac.miso.core.data.Pool)}
	 * .
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
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePool(uk.ac.bbsrc.tgac.miso.core.data.Pool)}
	 * .
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePoolQC(uk.ac.bbsrc.tgac.miso.core.data.PoolQC)}
	 * .
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePoolQC(uk.ac.bbsrc.tgac.miso.core.data.PoolQC)}
	 * .
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
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveEmPCR(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR)}
	 * .
	 */

	@Test
	public void testSaveEmPCR() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveEmPCRDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution)}
	 * .
	 */

	@Test
	public void testSaveEmPCRDilution() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveExperiment(uk.ac.bbsrc.tgac.miso.core.data.Experiment)}
	 * .
	 */

	@Test
	public void testSaveExperiment() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveStudy(uk.ac.bbsrc.tgac.miso.core.data.Study)}
	 * .
	 */

	@Test
	public void testSaveStudy() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSequencerPoolPartition(uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition)}
	 * .
	 */

	@Test
	public void testSaveSequencerPoolPartition() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSequencerPartitionContainer(uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer)}
	 * .
	 */

	@Test
	public void testSaveSequencerPartitionContainer() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSubmission(uk.ac.bbsrc.tgac.miso.core.data.Submission)}
	 * .
	 */

	@Test
	public void testSaveSubmission() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveEntityGroup(uk.ac.bbsrc.tgac.miso.core.data.EntityGroup)}
	 * .
	 */

	@Test
	public void testSaveEntityGroup() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerPoolPartitionById(long)}
	 * .
	 */

	@Test
	public void testGetSequencerPoolPartitionById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getExperimentById(long)}
	 * .
	 */

	@Test
	public void testGetExperimentById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolById(long)}
	 * .
	 */

	@Test
	public void testGetPoolById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolByBarcode(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testGetPoolByBarcodeStringPlatformType() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolByIdBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetPoolByIdBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolByBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetPoolByBarcodeString() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolQCById(long)}
	 * .
	 */

	@Test
	public void testGetPoolQCById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryById(long)}
	 * .
	 */

	@Test
	public void testGetLibraryById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryByBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetLibraryByBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryByAlias(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetLibraryByAlias() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getDilutionByBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetDilutionByBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getDilutionByIdAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testGetDilutionByIdAndPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getDilutionByBarcodeAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testGetDilutionByBarcodeAndPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryDilutionById(long)}
	 * .
	 */

	@Test
	public void testGetLibraryDilutionById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryDilutionByBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetLibraryDilutionByBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryDilutionByBarcodeAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testGetLibraryDilutionByBarcodeAndPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryQCById(long)}
	 * .
	 */

	@Test
	public void testGetLibraryQCById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRById(long)}
	 * .
	 */

	@Test
	public void testGetEmPCRById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRDilutionById(long)}
	 * .
	 */

	@Test
	public void testGetEmPCRDilutionById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRDilutionByBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetEmPCRDilutionByBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEmPCRDilutionByBarcodeAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testGetEmPCRDilutionByBarcodeAndPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerPartitionContainerById(long)}
	 * .
	 */

	@Test
	public void testGetSequencerPartitionContainerById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getNoteById(long)}
	 * .
	 */

	@Test
	public void testGetNoteById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getProjectById(long)}
	 * .
	 */

	@Test
	public void testGetProjectById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getProjectByAlias(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetProjectByAlias() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getProjectOverviewById(long)}
	 * .
	 */

	@Test
	public void testGetProjectOverviewById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunById(long)}
	 * .
	 */

	@Test
	public void testGetRunById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunByAlias(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetRunByAlias() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunQCById(long)}
	 * .
	 */

	@Test
	public void testGetRunQCById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleById(long)}
	 * .
	 */

	@Test
	public void testGetSampleById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleByBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetSampleByBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleQCById(long)}
	 * .
	 */

	@Test
	public void testGetSampleQCById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getStatusByRunName(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetStatusByRunName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getStudyById(long)}
	 * .
	 */

	@Test
	public void testGetStudyById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSubmissionById(long)}
	 * .
	 */

	@Test
	public void testGetSubmissionById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPlateById(long)}
	 * .
	 */

	@Test
	public void testGetPlateById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPlateByBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetPlateByBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getEntityGroupById(long)}
	 * .
	 */

	@Test
	public void testGetEntityGroupById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllProjects()}
	 * .
	 */

	@Test
	public void testListAllProjects() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllProjectsWithLimit(long)}
	 * .
	 */

	@Test
	public void testListAllProjectsWithLimit() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllProjectsBySearch(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllProjectsBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllOverviewsByProjectId(long)}
	 * .
	 */

	@Test
	public void testListAllOverviewsByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRuns()}
	 * .
	 */

	@Test
	public void testListAllRuns() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunsWithLimit(long)}
	 * .
	 */

	@Test
	public void testListAllRunsWithLimit() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunsBySearch(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllRunsBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunsByProjectId(long)}
	 * .
	 */

	@Test
	public void testListAllRunsByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listRunsByPoolId(long)}
	 * .
	 */

	@Test
	public void testListRunsByPoolId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listRunsBySequencerPartitionContainerId(long)}
	 * .
	 */

	@Test
	public void testListRunsBySequencerPartitionContainerId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLS454Runs()}
	 * .
	 */

	@Test
	public void testListAllLS454Runs() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllIlluminaRuns()}
	 * .
	 */

	@Test
	public void testListAllIlluminaRuns() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSolidRuns()}
	 * .
	 */

	@Test
	public void testListAllSolidRuns() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunQCsByRunId(long)}
	 * .
	 */

	@Test
	public void testListAllRunQCsByRunId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listSequencerPartitionContainersByRunId(long)}
	 * .
	 */

	@Test
	public void testListSequencerPartitionContainersByRunId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listSequencerPartitionContainersByBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testListSequencerPartitionContainersByBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamples()}
	 * .
	 */

	@Test
	public void testListAllSamples() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesWithLimit(long)}
	 * .
	 */

	@Test
	public void testListAllSamplesWithLimit() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesByReceivedDate(long)}
	 * .
	 */

	@Test
	public void testListAllSamplesByReceivedDate() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesBySearch(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllSamplesBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesByProjectId(long)}
	 * .
	 */

	@Test
	public void testListAllSamplesByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSamplesByExperimentId(long)}
	 * .
	 */

	@Test
	public void testListAllSamplesByExperimentId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listSamplesByAlias(java.lang.String)}
	 * .
	 */

	@Test
	public void testListSamplesByAlias() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSampleQCsBySampleId(long)}
	 * .
	 */

	@Test
	public void testListAllSampleQCsBySampleId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraries()}
	 * .
	 */

	@Test
	public void testListAllLibraries() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrariesWithLimit(long)}
	 * .
	 */

	@Test
	public void testListAllLibrariesWithLimit() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrariesBySearch(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllLibrariesBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrariesByProjectId(long)}
	 * .
	 */

	@Test
	public void testListAllLibrariesByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrariesBySampleId(long)}
	 * .
	 */

	@Test
	public void testListAllLibrariesBySampleId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryQCsByLibraryId(long)}
	 * .
	 */

	@Test
	public void testListAllLibraryQCsByLibraryId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listDilutionsBySearch(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListDilutionsBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllDilutionsByProjectAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListAllDilutionsByProjectAndPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutions()}
	 * .
	 */

	@Test
	public void testListAllLibraryDilutions() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsWithLimit(long)}
	 * .
	 */

	@Test
	public void testListAllLibraryDilutionsWithLimit() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsByLibraryId(long)}
	 * .
	 */

	@Test
	public void testListAllLibraryDilutionsByLibraryId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsByPlatform(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListAllLibraryDilutionsByPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsByProjectId(long)}
	 * .
	 */

	@Test
	public void testListAllLibraryDilutionsByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsBySearch(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListAllLibraryDilutionsBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsBySearchOnly(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllLibraryDilutionsBySearchOnly() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryDilutionsByProjectAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListAllLibraryDilutionsByProjectAndPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutions()}
	 * .
	 */

	@Test
	public void testListAllEmPCRDilutions() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByEmPcrId(long)}
	 * .
	 */

	@Test
	public void testListAllEmPCRDilutionsByEmPcrId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByPlatform(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListAllEmPCRDilutionsByPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByProjectId(long)}
	 * .
	 */

	@Test
	public void testListAllEmPCRDilutionsByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsBySearch(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListAllEmPCRDilutionsBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByProjectAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListAllEmPCRDilutionsByProjectAndPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRDilutionsByPoolAndPlatform(long, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListAllEmPCRDilutionsByPoolAndPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRs()}
	 * .
	 */

	@Test
	public void testListAllEmPCRs() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRsByDilutionId(long)}
	 * .
	 */

	@Test
	public void testListAllEmPCRsByDilutionId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPools()}
	 * .
	 */

	@Test
	public void testListAllPools() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPoolsByPlatform(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListAllPoolsByPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPoolsByPlatformAndSearch(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType, java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllPoolsByPlatformAndSearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listReadyPoolsByPlatform(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListReadyPoolsByPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listReadyPoolsByPlatformAndSearch(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType, java.lang.String)}
	 * .
	 */

	@Test
	public void testListReadyPoolsByPlatformAndSearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPoolsByLibraryId(long)}
	 * .
	 */

	@Test
	public void testListPoolsByLibraryId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPoolsBySampleId(long)}
	 * .
	 */

	@Test
	public void testListPoolsBySampleId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPoolQCsByPoolId(long)}
	 * .
	 */

	@Test
	public void testListAllPoolQCsByPoolId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllExperiments()}
	 * .
	 */

	@Test
	public void testListAllExperiments() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllExperimentsWithLimit(long)}
	 * .
	 */

	@Test
	public void testListAllExperimentsWithLimit() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllExperimentsBySearch(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllExperimentsBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllExperimentsByStudyId(long)}
	 * .
	 */

	@Test
	public void testListAllExperimentsByStudyId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudies()}
	 * .
	 */

	@Test
	public void testListAllStudies() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudiesWithLimit(long)}
	 * .
	 */

	@Test
	public void testListAllStudiesWithLimit() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudiesBySearch(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllStudiesBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudiesByProjectId(long)}
	 * .
	 */

	@Test
	public void testListAllStudiesByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudiesByLibraryId(long)}
	 * .
	 */

	@Test
	public void testListAllStudiesByLibraryId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSequencerPoolPartitions()}
	 * .
	 */

	@Test
	public void testListAllSequencerPoolPartitions() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPartitionsBySequencerPartitionContainerId(long)}
	 * .
	 */

	@Test
	public void testListPartitionsBySequencerPartitionContainerId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSequencerPartitionContainers()}
	 * .
	 */

	@Test
	public void testListAllSequencerPartitionContainers() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSubmissions()}
	 * .
	 */

	@Test
	public void testListAllSubmissions() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listRunsByExperimentId(java.lang.Long)}
	 * .
	 */

	@Test
	public void testListRunsByExperimentId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPlates()}
	 * .
	 */

	@Test
	public void testListAllPlates() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPlatesByProjectId(long)}
	 * .
	 */

	@Test
	public void testListAllPlatesByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPlatesBySearch(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllPlatesBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteProject(uk.ac.bbsrc.tgac.miso.core.data.Project)}
	 * .
	 */

	@Test
	public void testDeleteProject() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteStudy(uk.ac.bbsrc.tgac.miso.core.data.Study)}
	 * .
	 */

	@Test
	public void testDeleteStudy() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteExperiment(uk.ac.bbsrc.tgac.miso.core.data.Experiment)}
	 * .
	 */

	@Test
	public void testDeleteExperiment() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteSample(uk.ac.bbsrc.tgac.miso.core.data.Sample)}
	 * .
	 */

	@Test
	public void testDeleteSample() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteLibrary(uk.ac.bbsrc.tgac.miso.core.data.Library)}
	 * .
	 */

	@Test
	public void testDeleteLibrary() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteEmPCR(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR)}
	 * .
	 */

	@Test
	public void testDeleteEmPCR() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteRun(uk.ac.bbsrc.tgac.miso.core.data.Run)}
	 * .
	 */

	@Test
	public void testDeleteRun() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteRunQC(uk.ac.bbsrc.tgac.miso.core.data.RunQC)}
	 * .
	 */

	@Test
	public void testDeleteRunQC() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteSampleQC(uk.ac.bbsrc.tgac.miso.core.data.SampleQC)}
	 * .
	 */

	@Test
	public void testDeleteSampleQC() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteLibraryQC(uk.ac.bbsrc.tgac.miso.core.data.LibraryQC)}
	 * .
	 */

	@Test
	public void testDeleteLibraryQC() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteLibraryDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution)}
	 * .
	 */

	@Test
	public void testDeleteLibraryDilution() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteEmPCRDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution)}
	 * .
	 */

	@Test
	public void testDeleteEmPCRDilution() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteSequencerReference(uk.ac.bbsrc.tgac.miso.core.data.SequencerReference)}
	 * .
	 */

	@Test
	public void testDeleteSequencerReference() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deletePool(uk.ac.bbsrc.tgac.miso.core.data.Pool)}
	 * .
	 */

	@Test
	public void testDeletePool() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deletePoolQC(uk.ac.bbsrc.tgac.miso.core.data.PoolQC)}
	 * .
	 */

	@Test
	public void testDeletePoolQC() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deletePlate(uk.ac.bbsrc.tgac.miso.core.data.Plate)}
	 * .
	 */

	@Test
	public void testDeletePlate() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteEntityGroup(uk.ac.bbsrc.tgac.miso.core.data.EntityGroup)}
	 * .
	 */

	@Test
	public void testDeleteEntityGroup() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteContainer(uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer)}
	 * .
	 */

	@Test
	public void testDeleteContainer() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deletePartition(uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition)}
	 * .
	 */

	@Test
	public void testDeletePartition() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteNote(com.eaglegenomics.simlims.core.Note)}
	 * .
	 */

	@Test
	public void testDeleteNote() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRuns(java.util.Collection)}
	 * .
	 */

	@Test
	public void testSaveRuns() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveRunNote(uk.ac.bbsrc.tgac.miso.core.data.Run, com.eaglegenomics.simlims.core.Note)}
	 * .
	 */

	@Test
	public void testSaveRunNote() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveEmPcrDilution(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution)}
	 * .
	 */

	@Test
	public void testSaveEmPcrDilution() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePlatform(uk.ac.bbsrc.tgac.miso.core.data.Platform)}
	 * .
	 */

	@Test
	public void testSavePlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveStatus(uk.ac.bbsrc.tgac.miso.core.data.Status)}
	 * .
	 */

	@Test
	public void testSaveStatus() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSecurityProfile(com.eaglegenomics.simlims.core.SecurityProfile)}
	 * .
	 */

	@Test
	public void testSaveSecurityProfile() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveSequencerReference(uk.ac.bbsrc.tgac.miso.core.data.SequencerReference)}
	 * .
	 */

	@Test
	public void testSaveSequencerReference() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveKit(uk.ac.bbsrc.tgac.miso.core.data.Kit)}
	 * .
	 */

	@Test
	public void testSaveKit() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveKitDescriptor(uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor)}
	 * .
	 */

	@Test
	public void testSaveKitDescriptor() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#savePlate(uk.ac.bbsrc.tgac.miso.core.data.Plate)}
	 * .
	 */

	@Test
	public void testSavePlate() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveAlert(uk.ac.bbsrc.tgac.miso.core.event.Alert)}
	 * .
	 */

	@Test
	public void testSaveAlert() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveBox(uk.ac.bbsrc.tgac.miso.core.data.Box)}
	 * .
	 */

	@Test
	public void testSaveBox() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryTypeById(long)}
	 * .
	 */

	@Test
	public void testGetLibraryTypeById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryTypeByDescription(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetLibraryTypeByDescription() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryTypeByDescriptionAndPlatform(java.lang.String, uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testGetLibraryTypeByDescriptionAndPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibrarySelectionTypeById(long)}
	 * .
	 */

	@Test
	public void testGetLibrarySelectionTypeById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibrarySelectionTypeByName(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetLibrarySelectionTypeByName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryStrategyTypeById(long)}
	 * .
	 */

	@Test
	public void testGetLibraryStrategyTypeById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryStrategyTypeByName(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetLibraryStrategyTypeByName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getTagBarcodeById(long)}
	 * .
	 */

	@Test
	public void testGetTagBarcodeById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPlatformById(long)}
	 * .
	 */

	@Test
	public void testGetPlatformById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getStatusById(long)}
	 * .
	 */

	@Test
	public void testGetStatusById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerReferenceById(long)}
	 * .
	 */

	@Test
	public void testGetSequencerReferenceById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerReferenceByName(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetSequencerReferenceByName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSequencerReferenceByRunId(long)}
	 * .
	 */

	@Test
	public void testGetSequencerReferenceByRunId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitById(long)}
	 * .
	 */

	@Test
	public void testGetKitById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitByIdentificationBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetKitByIdentificationBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitByLotNumber(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetKitByLotNumber() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitDescriptorById(long)}
	 * .
	 */

	@Test
	public void testGetKitDescriptorById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getKitDescriptorByPartNumber(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetKitDescriptorByPartNumber() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleQcTypeById(long)}
	 * .
	 */

	@Test
	public void testGetSampleQcTypeById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getSampleQcTypeByName(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetSampleQcTypeByName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryQcTypeById(long)}
	 * .
	 */

	@Test
	public void testGetLibraryQcTypeById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getLibraryQcTypeByName(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetLibraryQcTypeByName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunQcTypeById(long)}
	 * .
	 */

	@Test
	public void testGetRunQcTypeById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getRunQcTypeByName(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetRunQcTypeByName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolQcTypeById(long)}
	 * .
	 */

	@Test
	public void testGetPoolQcTypeById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getPoolQcTypeByName(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetPoolQcTypeByName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getAlertById(long)}
	 * .
	 */

	@Test
	public void testGetAlertById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getBoxById(long)}
	 * .
	 */

	@Test
	public void testGetBoxById() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getBoxByBarcode(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetBoxByBarcode() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getBoxByAlias(java.lang.String)}
	 * .
	 */

	@Test
	public void testGetBoxByAlias() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxes()}
	 * .
	 */

	@Test
	public void testListAllBoxes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxesWithLimit(long)}
	 * .
	 */

	@Test
	public void testListAllBoxesWithLimit() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxesBySearch(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllBoxesBySearch() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxesByAlias(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllBoxesByAlias() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllChanges(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllChanges() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSampleTypes()}
	 * .
	 */

	@Test
	public void testListAllSampleTypes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryTypes()}
	 * .
	 */

	@Test
	public void testListAllLibraryTypes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listLibraryTypesByPlatform(java.lang.String)}
	 * .
	 */

	@Test
	public void testListLibraryTypesByPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibrarySelectionTypes()}
	 * .
	 */

	@Test
	public void testListAllLibrarySelectionTypes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryStrategyTypes()}
	 * .
	 */

	@Test
	public void testListAllLibraryStrategyTypes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllTagBarcodes()}
	 * .
	 */

	@Test
	public void testListAllTagBarcodes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllTagBarcodesByPlatform(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllTagBarcodesByPlatform() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllTagBarcodesByStrategyName(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllTagBarcodesByStrategyName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllEmPCRsByProjectId(long)}
	 * .
	 */

	@Test
	public void testListAllEmPCRsByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPoolsByProjectId(long)}
	 * .
	 */

	@Test
	public void testListPoolsByProjectId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPlatforms()}
	 * .
	 */

	@Test
	public void testListAllPlatforms() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listPlatformsOfType(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListPlatformsOfType() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listDistinctPlatformNames()}
	 * .
	 */

	@Test
	public void testListDistinctPlatformNames() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxUses()}
	 * .
	 */

	@Test
	public void testListAllBoxUses() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxUsesStrings()}
	 * .
	 */

	@Test
	public void testListAllBoxUsesStrings() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllBoxSizes()}
	 * .
	 */

	@Test
	public void testListAllBoxSizes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStudyTypes()}
	 * .
	 */

	@Test
	public void testListAllStudyTypes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getBoxablesFromBarcodeList(java.util.List)}
	 * .
	 */

	@Test
	public void testGetBoxablesFromBarcodeList() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSequencerReferences()}
	 * .
	 */

	@Test
	public void testListAllSequencerReferences() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listSequencerReferencesByPlatformType(uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType)}
	 * .
	 */

	@Test
	public void testListSequencerReferencesByPlatformType() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllKits()}
	 * .
	 */

	@Test
	public void testListAllKits() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listKitsByExperimentId(long)}
	 * .
	 */

	@Test
	public void testListKitsByExperimentId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listKitsByManufacturer(java.lang.String)}
	 * .
	 */

	@Test
	public void testListKitsByManufacturer() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listKitsByType(uk.ac.bbsrc.tgac.miso.core.data.type.KitType)}
	 * .
	 */

	@Test
	public void testListKitsByType() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listKitDescriptorsByType(uk.ac.bbsrc.tgac.miso.core.data.type.KitType)}
	 * .
	 */

	@Test
	public void testListKitDescriptorsByType() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllKitDescriptors()}
	 * .
	 */

	@Test
	public void testListAllKitDescriptors() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllSampleQcTypes()}
	 * .
	 */

	@Test
	public void testListAllSampleQcTypes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllLibraryQcTypes()}
	 * .
	 */

	@Test
	public void testListAllLibraryQcTypes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllPoolQcTypes()}
	 * .
	 */

	@Test
	public void testListAllPoolQcTypes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllRunQcTypes()}
	 * .
	 */

	@Test
	public void testListAllRunQcTypes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStatus()}
	 * .
	 */

	@Test
	public void testListAllStatus() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAllStatusBySequencerName(java.lang.String)}
	 * .
	 */

	@Test
	public void testListAllStatusBySequencerName() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listUnreadAlertsByUserId(long)}
	 * .
	 */

	@Test
	public void testListUnreadAlertsByUserId() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAlertsByUserId(long)}
	 * .
	 */

	@Test
	public void testListAlertsByUserIdLong() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#listAlertsByUserId(long, long)}
	 * .
	 */

	@Test
	public void testListAlertsByUserIdLongLong() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#emptySingleTube(uk.ac.bbsrc.tgac.miso.core.data.Box, java.lang.String)}
	 * .
	 */

	@Test
	public void testEmptySingleTube() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#emptyAllTubes(uk.ac.bbsrc.tgac.miso.core.data.Box)}
	 * .
	 */

	@Test
	public void testEmptyAllTubes() {
		// TODO: Implement.
	}

	/**
	 * Test method for
	 * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#deleteBox(uk.ac.bbsrc.tgac.miso.core.data.Box)}
	 * .
	 */

	@Test
	public void testDeleteBox() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#Object()}.
	 */

	@Test
	public void testObject() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#getClass()}.
	 */

	@Test
	public void testGetClass() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#hashCode()}.
	 */

	@Test
	public void testHashCode() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */

	@Test
	public void testEquals() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#clone()}.
	 */

	@Test
	public void testClone() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#toString()}.
	 */

	@Test
	public void testToString() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#notify()}.
	 */

	@Test
	public void testNotify() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#notifyAll()}.
	 */

	@Test
	public void testNotifyAll() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#wait(long)}.
	 */

	@Test
	public void testWaitLong() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#wait(long, int)}.
	 */

	@Test
	public void testWaitLongInt() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#wait()}.
	 */

	@Test
	public void testWait() {
		// TODO: Implement.
	}

	/**
	 * Test method for {@link java.lang.Object#finalize()}.
	 */

	@Test
	public void testFinalize() {
		// TODO: Implement.
	}

}
