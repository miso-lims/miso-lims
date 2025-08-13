package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import org.springframework.web.servlet.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.mockito.InjectMocks;
import com.jayway.jsonpath.JsonPath;

import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PrinterDto;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePrinterDao;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.PrinterRestController.*;

import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Set;
import java.util.stream.Collectors;
import uk.ac.bbsrc.tgac.miso.core.service.printing.*;
import org.springframework.transaction.annotation.Transactional;

public class PrinterRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/printers";
  private static final Class<Printer> entityClass = Printer.class;

  @InjectMocks
  private static HibernatePrinterDao dao = new HibernatePrinterDao();

  @Test
  @Transactional
  public void testBoxContents() throws Exception {
    BoxPrintRequest req = new BoxPrintRequest();
    req.setCopies(1);
    req.setBoxes(Arrays.asList(1L, 2L));


    dao.setEntityManager(getEntityManager());
    Printer printerOriginal = currentSession().get(entityClass, 1);
    printerOriginal.setBackend(Backend.DEBUG);
    dao.update(printerOriginal);


    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/1/boxcontents").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    // TODO add assertions
  }

  @Test
  @Transactional
  public void testBoxPositions() throws Exception {

    BoxPositionPrintRequest req = new BoxPositionPrintRequest();
    req.setBoxId(1L);
    req.setCopies(1);
    req.setSortOrder("column");

    req.setPositions(Arrays.asList("A01", "A07", "B02", "B05").stream().collect(Collectors.toSet()));

    dao.setEntityManager(getEntityManager());
    Printer printerOriginal = currentSession().get(entityClass, 1);
    printerOriginal.setBackend(Backend.DEBUG);
    dao.update(printerOriginal);


    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/1/boxpositions").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
    // TODO add assertions
  }

  private PrinterDto makeCreateDto() throws Exception {
    PrinterDto printer = new PrinterDto();
    printer.setName("new");
    printer.setDriver("BRADY");
    printer.setBackend("LINE_PRINTER");
    printer.setAvailable(true);
    printer.setHeight(73.2);
    printer.setWidth(35.5);
    printer.setLayout((ArrayNode) mapper
        .readTree("[{\"element\":\"text\", \"x\":2, \"height\":2, \"y\":2, \"contents\":{\"use\":\"NAME\"}}]"));

    return printer;

  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    PrinterDto printer = makeCreateDto();
    Printer newPrinter = baseTestCreate(CONTROLLER_BASE, printer, entityClass, 201);
    assertEquals(printer.getName(), newPrinter.getName());
    assertEquals(printer.getBackend(), newPrinter.getBackend().toString());
    assertEquals(printer.getDriver(), newPrinter.getDriver().toString());
    assertEquals(printer.isAvailable(), newPrinter.isEnabled());
    assertEquals(printer.getHeight(), newPrinter.getHeight(), 0.0);
    assertEquals(printer.getWidth(), newPrinter.getWidth(), 0.0);
    assertEquals("[{\"element\":\"text\",\"x\":2,\"height\":2,\"y\":2,\"contents\":{\"use\":\"NAME\"}}]",
        newPrinter.getLayout());
  }

  @Test
  public void testCreateFail() throws Exception {
    testCreateUnauthorized(CONTROLLER_BASE, makeCreateDto(), entityClass);
  }

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 2, CONTROLLER_BASE);
  }


  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 2, CONTROLLER_BASE);
  }

  @Test
  public void testDisable() throws Exception {
    assertTrue(currentSession().get(entityClass, 1).isEnabled());
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/disable").content(makeJson(Arrays.asList(1L)))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent());
    assertFalse(currentSession().get(entityClass, 1).isEnabled());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDuplicate() throws Exception {
    DuplicateRequest req = new DuplicateRequest();
    req.setName("duped");
    req.setHeight(17.2);
    req.setWidth(14.5);
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/duplicate").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent());


    // need to get a list of the printers, as the newly duplicated printer has an inconsistent ID each
    // time you run the tests

    String response = getMockMvc().perform(get(CONTROLLER_BASE))
        .andDo(print())
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    Integer printerId = JsonPath.read(response, "$[2].id");

    Printer duplicated = currentSession().get(entityClass, printerId.intValue());
    assertNotNull(duplicated);
    assertEquals(req.getHeight(), duplicated.getHeight(), 0);
    assertEquals(req.getWidth(), duplicated.getWidth(), 0);
    assertEquals(req.getName(), duplicated.getName());
  }

  @Test
  public void testDuplicateFail() throws Exception {
    DuplicateRequest req = new DuplicateRequest();
    req.setName("duped");
    req.setHeight(17.2);
    req.setWidth(14.5);
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/duplicate").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testEnable() throws Exception {
    assertFalse(currentSession().get(entityClass, 2).isEnabled());
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/enable").content(makeJson(Arrays.asList(2L)))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent());
    assertTrue(currentSession().get(entityClass, 2).isEnabled());
  }

  @Test
  public void testGet() throws Exception {
    baseTestGetById(CONTROLLER_BASE, 1)
        .andExpect(jsonPath("$.name").value("Printer"));
  }

  @Test
  public void testGetLayout() throws Exception {

    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/1/layout"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].element").value("text"))
        .andExpect(jsonPath("$[0].contents.use").value("ALIAS"))
        .andExpect(jsonPath("$[0].direction").value("NORMAL"))
        .andExpect(jsonPath("$[0].height").value(0.0))
        .andExpect(jsonPath("$[0].justification").value("LEFT"))
        .andExpect(jsonPath("$[0].lineLimit").value(0))
        .andExpect(jsonPath("$[0].style").value("REGULAR"))
        .andExpect(jsonPath("$[0].x").value(0.0))
        .andExpect(jsonPath("$[0].y").value(0.0));
  }

  @Test
  public void testSetLayout() throws Exception {
    LabelElementText layout = new LabelElementText();
    layout.setContents(PrintableField.ALIAS);
    layout.setHeight(4.5);
    layout.setX(10.1);

    System.out.println(Arrays.asList(makeJson(layout)));

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/layout").content(makeJson(Arrays.asList(layout)))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent());

    // TODO
    Printer printer = currentSession().get(entityClass, 1);
    System.out.println(printer.getLayout());
  }

  @Test
  public void testList() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2));
  }

  @Test
  @Transactional
  public void testSubmit() throws Exception {
    PrintRequest req = new PrintRequest();
    req.setCopies(2);
    req.setIds(Arrays.asList(1L, 2L, 500L));
    req.setPrinterId(1L);
    req.setType("box");

    dao.setEntityManager(getEntityManager());
    Printer printerOriginal = currentSession().get(entityClass, 1);
    printerOriginal.setBackend(Backend.DEBUG);
    dao.update(printerOriginal);

    getMockMvc().perform(post(CONTROLLER_BASE + "/1").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
    // TODO
  }

}
