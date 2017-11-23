package ca.on.oicr.pinery.lims.miso;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MisoClientIT {

  @Autowired
  private MisoClient sut;

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
  public void testGetDilutionSampleById() throws Exception {
    String dilutionId = "LDI1";
    Sample d = sut.getSample(dilutionId);
    assertNotNull(d);
    assertEquals(dilutionId, d.getId());
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
    assertFalse(samples.isEmpty());
    samples.forEach(sam -> assertEquals(Boolean.TRUE, sam.getArchived()));
  }

  @Test
  public void testGetSamplesFilteredByType() throws Exception {
    String type = "Identity";
    List<Sample> samples = sut.getSamples(null, null, Sets.newHashSet(type), null, null);
    assertFalse(samples.isEmpty());
    samples.forEach(sam -> assertEquals(type, sam.getSampleType()));
  }

  @Test
  public void testGetSamplesFilteredByDateRange() throws Exception {
    DateTime after = DateTime.parse("2016-01-01T00:00:00Z");
    DateTime before = DateTime.parse("2017-01-01T00:00:00Z");
    List<Sample> samples = sut.getSamples(null, null, null, before, after);
    assertFalse(samples.isEmpty());
    samples.forEach(sam -> {
      DateTime created = new DateTime(sam.getCreated().getTime());
      DateTime modified = new DateTime(sam.getModified().getTime());
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
    List<Run> runs = sut.getRuns();
    assertFalse(runs.isEmpty());
  }

  @Test
  public void testGetSampleTypes() throws Exception {
    List<Type> sampleTypes = sut.getTypes();
    assertTrue(sampleTypes.size() > 5);
  }

  @Test
  public void testGetAttributeNames() throws Exception {
    List<AttributeName> attrs = sut.getAttributeNames();
    assertTrue(attrs.size() > 10);
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
