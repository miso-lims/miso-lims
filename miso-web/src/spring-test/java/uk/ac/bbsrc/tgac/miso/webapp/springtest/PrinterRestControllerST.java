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

import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PrinterDto;
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

public class PrinterRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/printers";
  private static final Class<Printer> entityClass = Printer.class;

  @Test
  public void testBoxContents() throws Exception {
    BoxPrintRequest req = new BoxPrintRequest();
    req.setCopies(2);
    req.setBoxes(Arrays.asList(1L, 2L));


    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/1/boxcontents").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void testBoxPositions() throws Exception {

    BoxPositionPrintRequest req = new BoxPositionPrintRequest();
    req.setBoxId(1L);
    req.setCopies(2);
    req.setSortOrder("column");

    req.setPositions(Arrays.asList("A01", "A07", "B02", "B05").stream().collect(Collectors.toSet()));

    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/1/boxpositions").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void testCreate() throws Exception {
    // TODO needs admin perms
    PrinterDto printer = new PrinterDto();
    printer.setName("new");
    printer.setDriver("BRADY");
    printer.setBackend("LINE_PRINTER");
    printer.setAvailable(true);
    printer.setHeight(73.2);
    printer.setWidth(35.5);
    printer.setLayout((ArrayNode) mapper
        .readTree("[{\"element\":\"text\", \"x\":2, \"height\":2, \"y\":2, \"contents\":{\"use\":\"NAME\"}}]"));

    Printer newPrinter = baseTestCreate(CONTROLLER_BASE, printer, entityClass, 201);
    assertEquals(printer.getName(), newPrinter.getName());
    assertEquals(printer.getBackend(), newPrinter.getBackend());
    assertEquals(printer.getDriver(), newPrinter.getDriver());
    assertEquals(printer.isAvailable(), newPrinter.isEnabled());
    assertEquals(printer.getHeight(), newPrinter.getHeight());
    assertEquals(printer.getWidth(), newPrinter.getWidth());
    assertEquals("[{\"element\":\"text\", \"x\":2, \"height\":2, \"y\":2, \"contents\":{\"use\":\"NAME\"}}]",
        newPrinter.getLayout());
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
  public void testDuplicate() throws Exception {
    // TODO needs admin perms
    DuplicateRequest req = new DuplicateRequest();
    req.setName("duped");
    req.setHeight(17.2);
    req.setWidth(14.5);
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/duplicate").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent());

    // there are two printers in the test data
    // therefore this duplicated one will have id 3
    Printer duplicated = currentSession().get(entityClass, 3);
    assertNotNull(duplicated);
    assertEquals(req.getName(), duplicated.getName());
    assertEquals(req.getHeight(), duplicated.getHeight());
    assertEquals(req.getWidth(), duplicated.getWidth());

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

  }

  @Test
  public void testGetLayout() throws Exception {

  }

  @Test
  public void testSetLayout() throws Exception {

  }

  @Test
  public void testList() throws Exception {

  }

  @Test
  public void testSubmit() throws Exception {

  }

}
