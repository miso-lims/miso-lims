package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MisoPrintJob;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.DefaultPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;

public class SQLPrintJobDAOTest extends AbstractDAOTest {
  private final static MisoPrintService MPS = new DefaultPrintService();
  private final static User USER = new UserImpl();
  private final static Date DATE = new Date();
  private final static DateFormatter DF = new DateFormatter("yyyy-MM-dd");
  static {
    MPS.setName("foo");
    USER.setUserId(1L);
  }
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private PrintManager printManager;
  @Mock
  private SecurityManager securityManager;

  @InjectMocks
  private SQLPrintJobDAO dao;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSecurityManager(securityManager);
    Mockito.when(printManager.getPrintService(Matchers.anyString())).thenReturn(MPS);
    Mockito.when(securityManager.getUserById(Matchers.anyLong())).thenReturn(USER);
  }

  private void assertDate(Date d) {
    assertEquals(DF.print(DATE, Locale.CANADA), DF.print(d, Locale.CANADA));
  }

  @Test
  public void testGet() throws IOException {
    PrintJob j = dao.get(1L);
    assertNotNull(j);
    assertEquals(USER, j.getPrintUser());
  }

  @Test
  public void testSaveExisting() throws IOException {
    PrintJob j = dao.get(1L);
    j.setPrintDate(DATE);
    dao.save(j);
    PrintJob reloadedJob = dao.get(1L);
    assertDate(reloadedJob.getPrintDate());
  }

  @Test
  public void testSaveNew() throws IOException {
    PrintJob j = new MisoPrintJob();
    j.setPrintUser(USER);
    j.setPrintService(MPS);
    j.setStatus("WHATEVS");
    j.setPrintDate(DATE);
    long id = dao.save(j);
    assertTrue(id > 1L);
    PrintJob reloadedJob = dao.get(id);
    assertDate(reloadedJob.getPrintDate());
  }

  @Test
  public void testListByUser() throws IOException {
    assertTrue(dao.listByUser(USER).size() > 0);
  }

  @Test
  public void testListByPrintService() throws IOException {
    assertTrue(dao.listByPrintService(MPS).size() > 0);
  }

  @Test
  public void testListAndCount() throws IOException {
    Collection<PrintJob> all = dao.listAll();
    assertTrue(all.size() > 0);
    assertEquals(all.size(), dao.count());
  }
}