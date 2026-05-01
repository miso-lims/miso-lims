package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.ws.rs.core.MediaType;

import com.jayway.jsonpath.JsonPath;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SopDto;

import java.util.Arrays;
import java.util.List;

public class SopRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/sops";
  private static final Class<Sop> entityClass = Sop.class;

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    SopDto dto = new SopDto();
    dto.setAlias("test sop");
    dto.setVersion("1.0");
    dto.setCategory("SAMPLE");
    dto.setUrl("http://test.com/sop");
    dto.setArchived(false);

    Sop newSop = baseTestCreate(CONTROLLER_BASE, dto, entityClass, 201);
    assertEquals("test sop", newSop.getAlias());
    assertEquals("1.0", newSop.getVersion());
  }

  @Test
  public void testCreateFail() throws Exception {
    SopDto dto = new SopDto();
    dto.setAlias("test sop");
    dto.setVersion("1.0");
    dto.setCategory("SAMPLE");

    testCreateUnauthorized(CONTROLLER_BASE, dto, entityClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    SopDto dto = Dtos.asDto(currentSession().get(entityClass, 1L));
    dto.setAlias("modified sop");

    Sop updatedSop = baseTestUpdate(CONTROLLER_BASE, dto, 1, entityClass);
    assertEquals("modified sop", updatedSop.getAlias());
  }

  @Test
  public void testUpdateFail() throws Exception {
    SopDto dto = Dtos.asDto(currentSession().get(entityClass, 1L));
    dto.setAlias("modified sop");

    testUpdateUnauthorized(CONTROLLER_BASE, dto, 1, entityClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 5, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 5, CONTROLLER_BASE);
  }

  @Test
  public void testDataTableByCategory() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/category/SAMPLE", Arrays.asList(1, 2));
    testDtRequest(CONTROLLER_BASE + "/dt/category/LIBRARY", Arrays.asList(3, 4, 5));
    testDtRequest(CONTROLLER_BASE + "/dt/category/RUN", Arrays.asList(6));
  }
}
