/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateStatusDao;

/**
 * @author Chris Salt
 *
 */
public class HibernateStatusDaoTest extends AbstractDAOTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateStatusDao dao;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLStatusDAO#save(uk.ac.bbsrc.tgac.miso.core.data.Status)}.
   * 
   * @throws IOException
   */
  @Test
  public void testSave() throws IOException {
    Status status = new StatusImpl();

    HealthType health = HealthType.Completed;
    Date completion = new Date();
    Date start = new Date();
    Date lastModified = new Date();
    String instrument = "SN7001179";
    String runName = "120323_h1179_0070_BC0JHTACXX";
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!--Illumina RTA Status Report-->\n<Status>\n  <Software>Illumina RTA 1.12.4.2</Software>\n  <RunName>120323_h1179_0070_BC0JHTACXX</RunName>\n  <InstrumentName>H1179</InstrumentName>\n  <RunStarted>Tuesday, March 27, 2012 5:22 PM</RunStarted>\n  <NumCycles>202</NumCycles>\n  <ImgCycle>202</ImgCycle>\n  <ScoreCycle>202</ScoreCycle>\n  <CallCycle>202</CallCycle>\n  <InputDir>E:\\Illumina\\HiSeqTemp\\120323_h1179_0070_BC0JHTACXX</InputDir>\n  <OutputDir>\\\\storage4.stg.oicr.on.ca\\bas005\\archive\\h1179\\120323_h1179_0070_BC0JHTACXX</OutputDir>\n  <Configuration>\n    <CopyAllFiles>true</CopyAllFiles>\n    <CopyImages>False</CopyImages>\n    <DeleteImages>True</DeleteImages>\n    <RunInfoExists>True</RunInfoExists>\n    <IsPairedEndRun>True</IsPairedEndRun>\n    <NumberOfReads>2</NumberOfReads>\n    <NumberOfLanes>8</NumberOfLanes>\n    <TilesPerLane>48</TilesPerLane>\n    <ControlLane>8</ControlLane>\n  </Configuration>\n</Status>\n";

    status.setHealth(health);
    status.setCompletionDate(completion);
    status.setStartDate(start);
    status.setLastUpdated(lastModified);
    status.setInstrumentName(instrument);
    status.setRunAlias(runName);
    status.setXml(xml);

    long id = dao.save(status);

    Status rtn = dao.get(id);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    assertNotNull(rtn);
    assertEquals(health, rtn.getHealth());

    assertEquals(format.format(completion), format.format(rtn.getCompletionDate()));
    assertEquals(format.format(start), format.format(rtn.getStartDate()));
    assertEquals(instrument, rtn.getInstrumentName());
    assertNotSame(format.format(lastModified), rtn.getLastUpdated().toString());
    assertEquals(runName, rtn.getRunAlias());
    // TODO: Better xml assertion.
    assertNotNull(rtn.getXml());

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLStatusDAO#listAll()}.
   * 
   * @throws IOException
   */
  @Test
  public void testListAll() throws IOException {
    List<Status> stats = dao.listAll();
    assertNotNull(stats);
    assertEquals(4, stats.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLStatusDAO#count()}.
   * 
   * @throws IOException
   */
  @Test
  public void testCount() throws IOException {
    int count = dao.count();
    assertEquals(4, count);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLStatusDAO#listAllBySequencerName(java.lang.String)}.
   */
  @Test
  public void testListAllBySequencerName() {
    List<Status> stats = dao.listAllBySequencerName("SN7001179");
    assertNotNull(stats);
    assertEquals(4, stats.size());

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLStatusDAO#get(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGet() throws IOException {
    Long id = 1L;
    HealthType health = HealthType.Completed;
    String completion = "2012-03-31";
    String start = "2012-03-23";
    String instrument = "SN7001179";
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, -1);
    String runName = "120323_h1179_0070_BC0JHTACXX";
    Status status = dao.get(id);

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    assertNotNull(status);
    assertEquals(id, status.getId());
    assertEquals(health, status.getHealth());
    assertEquals(completion, format.format(status.getCompletionDate()));
    assertEquals(start, format.format(status.getStartDate()));
    assertEquals(instrument, status.getInstrumentName());
    assertTrue(status.getLastUpdated().after(calendar.getTime()));
    assertEquals(runName, status.getRunAlias());
    // TODO assert xml is still the same. Converting back isn't straightforward.
    // "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!--Illumina RTA Status Report-->\n<Status>\n <Software>Illumina RTA
    // 1.12.4.2</Software>\n <RunName>120323_h1179_0070_BC0JHTACXX</RunName>\n <InstrumentName>H1179</InstrumentName>\n <RunStarted>Tuesday,
    // March 27, 2012 5:22 PM</RunStarted>\n <NumCycles>202</NumCycles>\n <ImgCycle>202</ImgCycle>\n <ScoreCycle>202</ScoreCycle>\n
    // <CallCycle>202</CallCycle>\n <InputDir>E:\\Illumina\\HiSeqTemp\\120323_h1179_0070_BC0JHTACXX</InputDir>\n
    // <OutputDir>\\\\storage4.stg.oicr.on.ca\\bas005\\archive\\h1179\\120323_h1179_0070_BC0JHTACXX</OutputDir>\n <Configuration>\n
    // <CopyAllFiles>true</CopyAllFiles>\n <CopyImages>False</CopyImages>\n <DeleteImages>True</DeleteImages>\n
    // <RunInfoExists>True</RunInfoExists>\n <IsPairedEndRun>True</IsPairedEndRun>\n <NumberOfReads>2</NumberOfReads>\n
    // <NumberOfLanes>8</NumberOfLanes>\n <TilesPerLane>48</TilesPerLane>\n <ControlLane>8</ControlLane>\n </Configuration>\n</Status>\n";
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLStatusDAO#getByRunAlias(java.lang.String)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGetByRunName() throws IOException {
    Status status = dao.getByRunAlias("120412_h1179_0073_BC075RACXX");
    assertNotNull(status);
  }

}
