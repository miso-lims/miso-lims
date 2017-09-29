package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePrinterDao;

public class HibernatePrintServiceDaoTest extends AbstractDAOTest {
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private final String NEW_NAME = "The new name";

  @Autowired
  private SessionFactory sessionFactory;
  @InjectMocks
  private HibernatePrinterDao dao;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListAllAndCount() throws IOException {
    Collection<Printer> all = dao.listAll();
    assertTrue(all.size() > 0);
    assertEquals(all.size(), dao.count());
  }


  @Test
  public void testGet() throws IOException {
    Printer printer = dao.get(1L);
    assertNotNull(printer);
    assertEquals(1L, printer.getId());
  }

  @Test
  public void testSaveUpdate() throws IOException {
    Printer printerOriginal = dao.get(1L);
    assertNotNull(printerOriginal);
    printerOriginal.setName(NEW_NAME);
    assertTrue(dao.save(printerOriginal) != Printer.UNSAVED_ID);
    Printer printerFetched = dao.get(1L);
    assertNotNull(printerFetched);
    assertEquals(printerOriginal.getId(), printerFetched.getId());
    assertEquals(printerOriginal.getName(), printerFetched.getName());
  }

  @Test
  public void testSaveNew() throws IOException {
    Printer printer = new Printer();
    printer.setName(NEW_NAME);
    printer.setBackend(Backend.CUPS);
    printer.setConfiguration("blah, blah, blah");
    printer.setDriver(Driver.BRADY_1D);
    printer.setEnabled(true);
    long id = dao.save(printer);
    Printer fetchedPrinter = dao.get(id);
    assertEquals(NEW_NAME, fetchedPrinter.getName());
  }

}
