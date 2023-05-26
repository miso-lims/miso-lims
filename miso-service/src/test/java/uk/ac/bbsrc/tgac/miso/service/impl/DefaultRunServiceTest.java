package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.GetLaneContents;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.persistence.RunStore;

public class DefaultRunServiceTest {

  private static final LocalDate ORIGINAL_DATE = LocalDate.of(2021, 11, 15);
  private static final Date ORIGINAL_TIME = Date.from(LocalDateTime.of(2021, 11, 15, 0, 0).toInstant(ZoneOffset.UTC));
  private static final long RUN_ID = 1l;
  private static final String RUN_ALIAS = "RUN1_ALIAS";
  private static final String SEQUENCER_NAME = "SEQ1";
  private static final String CONTAINER_MODEL_BARCODE = "CONTAINER_MODEL";
  private static final String CONTAINER_SERIAL_NO = "CONTAINER";

  @Mock
  private UserService userService;
  @Mock
  private RunStore runStore;
  @Mock
  private InstrumentService instrumentService;
  @Mock
  private SequencingContainerModelService containerModelService;
  @Mock
  private ContainerService containerService;
  @Mock
  private PoolService poolService;
  @Mock
  private KitDescriptorService kitDescriptorService;
  @Mock
  private SequencingParametersService sequencingParametersService;
  @Mock
  private RunPartitionService runPartitionService;
  @Mock
  private AuthorizationManager authorizationManager;

  @InjectMocks
  private DefaultRunService sut;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    User notificationUser = makeUser(2L, "notification");
    Mockito.when(authorizationManager.getCurrentUser()).thenReturn(notificationUser);
    Mockito.when(userService.getByLoginName("notification")).thenReturn(notificationUser);
    Mockito.when(instrumentService.getByName(SEQUENCER_NAME)).thenReturn(makeSequencer());
    Mockito.when(containerModelService.find(Mockito.any(), Mockito.eq(CONTAINER_MODEL_BARCODE), Mockito.eq(1)))
        .thenReturn(makeContainerModel());
    Mockito.when(runStore.getByAlias(RUN_ALIAS)).thenReturn(makeSavedRun());
    Mockito.when(sequencingParametersService.listByInstrumentModelId(1L))
        .thenReturn(Arrays.asList(makeSequencingParameters()));
    Mockito.when(runPartitionService.get(Mockito.any(Run.class), Mockito.any(Partition.class)))
        .thenAnswer((invocation) -> {
          Run run = (Run) invocation.getArguments()[0];
          if (!RUN_ALIAS.equals(run.getAlias())) {
            return null;
          }
          Partition part = (Partition) invocation.getArguments()[1];
          RunPartition runPart = new RunPartition();
          runPart.setRunId(run.getId());
          runPart.setPartitionId(part.getId());
          return runPart;
        });
    Mockito.when(containerService.listByBarcode(CONTAINER_SERIAL_NO)).thenReturn(Arrays.asList(makeContainer()));
  }

  @Test
  public void testProcessNotificationNoChange() throws Exception {
    Run notificationRun = makeRun();
    Predicate<SequencingParameters> filterParameters = (params) -> false;
    GetLaneContents getLaneContents = (lane) -> Optional.empty();
    assertFalse(sut.processNotification(notificationRun, 1, CONTAINER_MODEL_BARCODE, CONTAINER_SERIAL_NO,
        SEQUENCER_NAME, filterParameters,
        getLaneContents, null));
    Mockito.verify(runStore, Mockito.times(0)).save(Mockito.any());
  }

  @Test
  public void testProcessNotificationDontOverwriteUserHealth() throws Exception {
    Run notificationRun = makeRun();
    notificationRun.setHealth(HealthType.Running);

    Predicate<SequencingParameters> filterParameters = (params) -> false;
    GetLaneContents getLaneContents = (lane) -> Optional.empty();

    assertFalse(sut.processNotification(notificationRun, 1, CONTAINER_MODEL_BARCODE, CONTAINER_SERIAL_NO,
        SEQUENCER_NAME, filterParameters,
        getLaneContents, null));
    Mockito.verify(runStore, Mockito.times(0)).save(Mockito.any());
  }

  private static Run makeSavedRun() {
    Run run = makeRun();
    run.setId(RUN_ID);
    run.setName("RUN1");
    User user = makeUser(1L, "user");
    run.setCreator(user);
    run.setCreationTime(ORIGINAL_TIME);
    run.setLastModifier(user);
    run.setLastModified(ORIGINAL_TIME);
    run.setSequencer(makeSequencer());
    RunPosition runPos = new RunPosition();
    runPos.setContainer(makeContainer());
    runPos.setRun(run);
    run.getRunPositions().add(runPos);
    ChangeLog change = run.createChangeLog("updated health", "health", user);
    run.getChangeLog().add(change);
    return run;
  }

  private static Run makeRun() {
    IlluminaRun run = new IlluminaRun();
    run.setAlias(RUN_ALIAS);
    run.setFilePath("/path/to/run");
    run.setStartDate(ORIGINAL_DATE);
    run.setCompletionDate(ORIGINAL_DATE);
    run.setHealth(HealthType.Completed);
    return run;
  }

  private static User makeUser(long id, String loginName) {
    User user = new UserImpl();
    user.setId(id);
    user.setLoginName(loginName);
    return user;
  }

  private static Instrument makeSequencer() {
    Instrument inst = new InstrumentImpl();
    inst.setId(1L);
    inst.setName(SEQUENCER_NAME);
    inst.setInstrumentModel(makeInstrumentModel());
    return inst;
  }

  private static InstrumentModel makeInstrumentModel() {
    InstrumentModel model = new InstrumentModel();
    model.setId(1L);
    model.setInstrumentType(InstrumentType.SEQUENCER);
    model.setPlatformType(PlatformType.ILLUMINA);
    model.setNumContainers(1);
    model.setDataManglingPolicy(InstrumentDataManglingPolicy.NONE);
    return model;
  }

  private static SequencerPartitionContainer makeContainer() {
    SequencerPartitionContainer container = new SequencerPartitionContainerImpl();
    container.setPartitionLimit(1);
    container.setIdentificationBarcode(CONTAINER_SERIAL_NO);
    container.setModel(makeContainerModel());
    return container;
  }

  private static SequencingContainerModel makeContainerModel() {
    SequencingContainerModel model = new SequencingContainerModel();
    model.setId(1L);
    model.setAlias("Container Model");
    model.setIdentificationBarcode(CONTAINER_MODEL_BARCODE);
    model.getInstrumentModels().add(makeInstrumentModel());
    model.setPartitionCount(1);
    model.setPlatformType(PlatformType.ILLUMINA);
    return model;
  }

  private static SequencingParameters makeSequencingParameters() {
    SequencingParameters params = new SequencingParameters();
    params.setId(1L);
    params.setName("Seq Params");
    return params;
  }

}
