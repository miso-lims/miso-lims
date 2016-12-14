package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

public class DefaultExperimentServiceTestSuite {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Mock
  private Experiment experiment;

  @Mock
  private ExperimentStore experimentStore;
  @Mock
  private PlatformStore platformStore;
  @Mock
  private PoolStore poolStore;
  @Mock
  private StudyStore studyStore;
  @Mock
  private SecurityStore securityStore;
  @Mock
  private KitStore kitStore;
  @Mock
  private AuthorizationManager authorizationManager;
  @InjectMocks
  private DefaultExperimentService sut;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#save(uk.ac.bbsrc.tgac.miso.core.data.Experiment)} .
   */
  @Test
  public void testSaveExperiment() throws IOException {
    final long expectedReturn = 1L;
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {

        return null;
      }
    }).when(authorizationManager).throwIfNotWritable(any(SecurableByProfile.class));

    when(experimentStore.save(experiment)).thenReturn(expectedReturn);

    assertEquals(expectedReturn, sut.save(experiment));
    verify(experimentStore).save(experiment);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#save(uk.ac.bbsrc.tgac.miso.core.data.Experiment)} .
   */
  @Test
  public void testSaveExperimentThrows() throws IOException {
    when(experiment.userCanWrite(any(User.class))).thenReturn(false);

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot write to this Experiment");
    sut.save(experiment);

    verify(experimentStore, never()).save(experiment);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#get(long)} .
   */
  @Test
  public void testGetExperimentById() throws IOException {
    long inputId = 1L;
    when(experimentStore.get(inputId)).thenReturn(experiment);
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {

        return null;
      }
    }).when(authorizationManager).throwIfNotReadable(any(SecurableByProfile.class));

    assertEquals(experiment, sut.get(inputId));

    verify(experimentStore).get(inputId);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#get(long)} .
   */
  @Test
  public void testGetExperimentByIdThrows() throws IOException {
    long inputId = 1L;
    when(experimentStore.get(inputId)).thenReturn(experiment);
    doThrow(IOException.class).when(authorizationManager).throwIfNotReadable(any(SecurableByProfile.class));

    thrown.expect(IOException.class);
    thrown.expectMessage("User null cannot read Experiment " + inputId);

    sut.get(inputId);

    verify(experimentStore).get(inputId);
  }
}
