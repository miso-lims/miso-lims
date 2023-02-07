package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.StudyService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.persistence.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;

public class DefaultExperimentServiceTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private final Experiment experiment = new Experiment();

  @Mock
  private ExperimentStore experimentStore;
  @Mock
  private InstrumentModelService platformService;
  @Mock
  private LibraryService libraryService;
  @Mock
  private StudyService studyService;
  @Mock
  private SecurityStore securityStore;
  @Mock
  private KitDescriptorService kitService;
  @Mock
  private AuthorizationManager authorizationManager;
  @Mock
  private NamingSchemeHolder namingSchemeHolder;
  @Mock
  private NamingScheme namingScheme;
  @InjectMocks
  private DefaultExperimentService sut;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(namingSchemeHolder.getPrimary()).thenReturn(namingScheme);
    Mockito.when(namingScheme.validateName(ArgumentMatchers.any())).thenReturn(ValidationResult.success());
    experiment.setInstrumentModel(new InstrumentModel());
    experiment.getInstrumentModel().setId(2L);
    experiment.setLibrary(new LibraryImpl());
    experiment.getLibrary().setId(3L);
    experiment.setStudy(new StudyImpl());
    experiment.getStudy().setId(5L);
  }

  @Test
  public void testSaveExperiment() throws IOException {
    final long expectedReturn = 1L;

    when(experimentStore.save(experiment)).thenReturn(expectedReturn);
    when(platformService.get(experiment.getInstrumentModel().getId())).thenReturn(experiment.getInstrumentModel());
    when(libraryService.get(experiment.getLibrary().getId())).thenReturn(experiment.getLibrary());
    when(studyService.get(experiment.getStudy().getId())).thenReturn(experiment.getStudy());

    assertEquals(expectedReturn, sut.create(experiment));
    verify(experimentStore, times(2)).save(experiment);
  }


  @Test
  public void testGetExperimentById() throws IOException {
    long inputId = 1L;
    when(experimentStore.get(inputId)).thenReturn(experiment);

    assertEquals(experiment, sut.get(inputId));

    verify(experimentStore).get(inputId);
  }

}
