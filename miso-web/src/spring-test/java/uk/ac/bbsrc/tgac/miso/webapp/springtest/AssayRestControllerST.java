package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.dto.AssayDto;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.*;

public class AssayRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/assays";
  private static final Class<Assay> controllerClass = Assay.class;

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // must be admin to create an assay
    AssayDto assay = new AssayDto();
    assay.setAlias("tester");
    assay.setVersion("1.0");

    Assay newAssay = baseTestCreate(CONTROLLER_BASE, assay, controllerClass, 201);
    assertEquals("tester", newAssay.getAlias());
  }

  @Test
  public void testCreateFail() throws Exception {
    // must be admin to create an assay
    AssayDto assay = new AssayDto();
    assay.setAlias("tester");
    assay.setVersion("1.0");

    testCreateUnauthorized(CONTROLLER_BASE, assay, controllerClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // must be admin to change an assay
    AssayDto assay = AssayDto.from(currentSession().find(controllerClass, 1));

    assay.setAlias("modified");
    Assay updatedAssay = baseTestUpdate(CONTROLLER_BASE, assay, 1, controllerClass);
    assertEquals("modified", updatedAssay.getAlias());
  }

  @Test
  public void testUpdateFail() throws Exception {
    AssayDto assay = AssayDto.from(currentSession().find(controllerClass, 1));

    assay.setAlias("modified");
    testUpdateUnauthorized(CONTROLLER_BASE, assay, 1, controllerClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(controllerClass, 4, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 4, CONTROLLER_BASE);
  }
}
