package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.DefaultPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.impl.BradyFtpPrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.impl.BradyCustomStandardTubeBarcodeLabelSchema;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SQLPrintServiceDAOTest extends AbstractDAOTest {
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private final String NEW_NAME = "The new name";

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;
  @Mock
  private PrintManager printManager;
  @InjectMocks
  private SQLPrintServiceDAO dao;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setPrintManager(printManager);
    dao.setMisoFilesManager(null);
    dao.setSecurityManager(null);
    Mockito.when(printManager.getPrintContext("mach4-type-ftp-printer")).thenAnswer(new Answer<PrintContext>() {
      @Override
      public PrintContext answer(InvocationOnMock invocation) throws Throwable {
        return new BradyFtpPrintContext();
      }
    });
    Mockito.when(printManager.getBarcodableSchema(Matchers.anyString())).thenReturn(new BradyCustomStandardTubeBarcodeLabelSchema());
  }

  @Test
  public void testListAllAndCount() throws IOException {
    Collection<MisoPrintService> all = dao.listAll();
    assertTrue(all.size() > 0);
    assertEquals(all.size(), dao.count());
  }

  @Test
  public void testListByContext() throws IOException {
    assertTrue(dao.listByContext("mach4-type-ftp-printer").size() > 0);
  }

  @Test
  public void testGet() throws IOException {
    MisoPrintService service = dao.get(1L);
    assertNotNull(service);
    assertEquals(1L, service.getServiceId());
  }

  @Test
  public void testSaveAndGetByName() throws IOException {
    assertNull(dao.getByName(NEW_NAME));
    MisoPrintService service = dao.get(1L);
    assertNotNull(service);
    service.setName(NEW_NAME);
    assertTrue(dao.save(service) != -1L);
    MisoPrintService serviceByName = dao.getByName(NEW_NAME);
    assertNotNull(serviceByName);
    assertEquals(service.getServiceId(), serviceByName.getServiceId());
    assertEquals(service.getName(), serviceByName.getName());
  }

  @Test
  public void testSaveNew() throws IOException {
    PrintContext ctxt = new BradyFtpPrintContext();
    MisoPrintService service = new DefaultPrintService();
    service.setName(NEW_NAME);
    service.setPrintContext(new BradyFtpPrintContext());
    service.setBarcodableSchema(new BradyCustomStandardTubeBarcodeLabelSchema());
    service.setEnabled(true);
    service.setPrintContext(ctxt);
    service.setPrintServiceFor(Library.class);
    long id = dao.save(service);
    MisoPrintService fetchedService = dao.get(id);
    assertEquals(NEW_NAME, fetchedService.getName());
  }

}
