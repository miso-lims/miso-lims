package ca.on.oicr.pinery.lims.miso;

import static org.junit.Assert.*;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;

import ca.on.oicr.pinery.api.AttributeName;
import ca.on.oicr.pinery.api.Box;
import ca.on.oicr.pinery.api.ChangeLog;
import ca.on.oicr.pinery.api.Instrument;
import ca.on.oicr.pinery.api.InstrumentModel;
import ca.on.oicr.pinery.api.Order;
import ca.on.oicr.pinery.api.Run;
import ca.on.oicr.pinery.api.Sample;
import ca.on.oicr.pinery.api.SampleProject;
import ca.on.oicr.pinery.api.Type;
import ca.on.oicr.pinery.api.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-context.xml")
public abstract class AbstractMisoClientIT {

  private static final String SCRIPT_DIR = System.getProperty("basedir") + "/src/it/resources/db/migration/";
  private static final String BASE_SCRIPT = "pinery-miso_base_test_data.sql";

  @Autowired
  private DataSource dataSource;

  @Autowired
  private MisoClient sut;

  /**
   * @return filename of an SQL script with additional data to load after the base data file
   *         (pinery-miso_base_test_data.sql). May return null if there is no additional data
   */
  protected abstract String getAdditionalDataFilename();

  @Before
  public void setup() {
    Resource baseData = new FileSystemResource(getScript(BASE_SCRIPT));
    String script2 = getAdditionalDataFilename();
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(baseData);
    if (script2 != null) {
      Resource additionalData = new FileSystemResource(getScript(script2));
      populator.addScript(additionalData);
    }
    populator.execute(dataSource);
  }

  private File getScript(String filename) {
    File script = new File(SCRIPT_DIR + filename);
    if (!script.exists()) {
      throw new IllegalStateException("Script not found: " + filename);
    }
    return script;
  }

  @Test
  public void testGetSampleById() throws Exception {
    String sampleId = "SAM1";
    Sample s = sut.getSample(sampleId);
    assertNotNull(s);
    assertEquals(sampleId, s.getId());
  }

  @Test
  public void testGetLibrarySampleById() throws Exception {
    String libraryId = "LIB1";
    Sample l = sut.getSample(libraryId);
    assertNotNull(l);
    assertEquals(libraryId, l.getId());
  }

  @Test
  public void testGetLibraryAliquotSampleById() throws Exception {
    String aliquotId = "LDI1";
    Sample d = sut.getSample(aliquotId);
    assertNotNull(d);
    assertEquals(aliquotId, d.getId());
  }

  @Test
  public void testGetAllSamples() throws Exception {
    List<Sample> samples = sut.getSamples();
    assertTrue(samples.size() > 10);
  }

  @Test
  public void testGetSamplesFilteredByProject() throws Exception {
    String project = "PRO2";
    List<Sample> samples = sut.getSamples(null, Sets.newHashSet(project), null, null, null);
    assertFalse(samples.isEmpty());
    samples.forEach(sam -> assertEquals(project, sam.getProject()));
  }

  @Test
  public void testGetSamplesFilteredByArchived() throws Exception {
    List<Sample> samples = sut.getSamples(true, null, null, null, null);
    assertTrue(hasArchivedSamples() != samples.isEmpty());
    samples.forEach(sam -> assertEquals(Boolean.TRUE, sam.getArchived()));
  }

  /**
   * @return true if the data being tested against contains archived samples; false otherwise
   */
  protected abstract boolean hasArchivedSamples();

  @Test
  public void testGetSamplesFilteredByType() throws Exception {
    String type = "Illumina PE Library";
    List<Sample> samples = sut.getSamples(null, null, Sets.newHashSet(type), null, null);
    assertFalse(samples.isEmpty());
    samples.forEach(sam -> assertEquals(type, sam.getSampleType()));
  }

  @Test
  public void testGetSamplesFilteredByDateRange() throws Exception {
    ZonedDateTime after = ZonedDateTime.parse("2016-01-01T00:00:00Z");
    ZonedDateTime before = ZonedDateTime.parse("2017-01-01T00:00:00Z");
    List<Sample> samples = sut.getSamples(null, null, null, before, after);
    assertFalse(samples.isEmpty());
    samples.forEach(sam -> {
      ZonedDateTime created = ZonedDateTime.ofInstant(sam.getCreated().toInstant(), ZoneId.systemDefault());
      ZonedDateTime modified = ZonedDateTime.ofInstant(sam.getModified().toInstant(), ZoneId.systemDefault());
      assertTrue(before.isAfter(created));
      assertTrue(after.isBefore(modified));
    });
  }

  @Test
  public void testGetSampleProjects() throws Exception {
    List<SampleProject> projects = sut.getSampleProjects();
    assertTrue(projects.size() > 1);
  }

  @Test
  public void testGetUserById() throws Exception {
    User user = sut.getUser(1);
    assertNotNull(user);
    assertEquals(Integer.valueOf(1), user.getId());
  }

  @Test
  public void testGetAllUsers() throws Exception {
    List<User> users = sut.getUsers();
    assertFalse(users.isEmpty());
  }

  @Test
  public void testGetOrderById() throws Exception {
    Integer id = 1;
    Order order = sut.getOrder(id);
    assertNotNull(order);
    assertEquals(id, order.getId());
  }

  @Test
  public void testGetAllOrders() throws Exception {
    List<Order> orders = sut.getOrders();
    assertTrue(orders.size() > 1);
  }

  @Test
  public void testGetRunById() throws Exception {
    Run run = sut.getRun(1);
    assertNotNull(run);
    assertEquals(Integer.valueOf(1), run.getId());
  }

  @Test
  public void testGetRunByName() throws Exception {
    String runName = "MiSeq_Run_1";
    Run run = sut.getRun(runName);
    assertNotNull(run);
    assertEquals(runName, run.getName());
  }

  @Test
  public void testGetAllRuns() throws Exception {
    List<Run> runs = sut.getRuns(null);
    assertFalse(runs.isEmpty());
  }

  @Test
  public void testGetSampleTypes() throws Exception {
    List<Type> sampleTypes = sut.getTypes();
    assertTrue(sampleTypes.size() > 2);
  }

  @Test
  public void testGetAttributeNames() throws Exception {
    List<AttributeName> attrs = sut.getAttributeNames();
    assertTrue(attrs.size() > 2);
  }

  @Test
  public void testGetChangesBySampleId() throws Exception {
    String sampleId = "SAM1";
    ChangeLog cl = sut.getChangeLog(sampleId);
    assertNotNull(cl);
    assertEquals(sampleId, cl.getSampleId());
    assertNotNull(cl.getChanges());
    assertFalse(cl.getChanges().isEmpty());
  }

  @Test
  public void testGetAllChanges() throws Exception {
    List<ChangeLog> logs = sut.getChangeLogs();
    assertTrue(logs.size() > 1);
  }

  @Test
  public void testGetInstrumentModelById() throws Exception {
    Integer id = 1;
    InstrumentModel model = sut.getInstrumentModel(id);
    assertNotNull(model);
    assertEquals(id, model.getId());
  }

  @Test
  public void testGetAllInstrumentModels() throws Exception {
    List<InstrumentModel> models = sut.getInstrumentModels();
    assertTrue(models.size() > 1);
  }

  @Test
  public void testGetInstrumentById() throws Exception {
    Integer id = 1;
    Instrument inst = sut.getInstrument(id);
    assertNotNull(inst);
    assertEquals(id, inst.getId());
  }

  @Test
  public void testGetInstrumentsByModel() throws Exception {
    Integer id = 1;
    List<Instrument> instruments = sut.getInstrumentModelInstrument(id);
    assertTrue(instruments.size() > 1);
    instruments.forEach(inst -> assertEquals(id, inst.getModelId()));
  }

  @Test
  public void testGetAllInstruments() throws Exception {
    List<Instrument> instruments = sut.getInstruments();
    assertTrue(instruments.size() > 2);
  }

  @Test
  public void testGetAllBoxes() throws Exception {
    List<Box> boxes = sut.getBoxes();
    assertFalse(boxes.isEmpty());
  }

}
