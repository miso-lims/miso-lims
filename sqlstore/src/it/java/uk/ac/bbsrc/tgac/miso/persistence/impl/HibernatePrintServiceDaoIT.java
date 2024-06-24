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

public class HibernatePrintServiceDaoIT extends AbstractDAOTest {
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
  public void testList() throws IOException {
    Collection<Printer> all = dao.list();
    assertEquals(1, all.size());
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
    assertTrue(dao.update(printerOriginal) > 0L);
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
    printer.setDriver(Driver.BRADY);
    printer.setHeight(5);
    printer.setWidth(10);
    printer.setLayout("[{\"element\":\"text\", \"x\":2, \"height\":2, \"y\":2, \"contents\":{\"use\":\"NAME\"}}]");
    printer.setEnabled(true);
    long id = dao.create(printer);
    Printer fetchedPrinter = dao.get(id);
    assertEquals(NEW_NAME, fetchedPrinter.getName());
  }

}
