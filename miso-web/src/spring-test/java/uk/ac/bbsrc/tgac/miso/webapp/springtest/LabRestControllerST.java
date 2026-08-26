package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.*;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;

import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;


public class LabRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/labs";
  private static final Class<LabImpl> controllerClass = LabImpl.class;

  @Test
  public void testBulkCreateAsync() throws Exception {
    // since there is no permission restrictions on creating labs (i.e. don't need to be admin to create
    // one), there is no create failure test for labs
    LabDto lone = new LabDto();
    lone.setAlias("lab1");

    LabDto ltwo = new LabDto();
    ltwo.setAlias("lab2");

    List<LabDto> dtos = new ArrayList<LabDto>();
    dtos.add(lone);
    dtos.add(ltwo);

    baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, dtos);
  }



  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // admin permissions are not required to update, however only the creator can update a lab, which in
    // this case happens to be the admin user
    LabDto bioBank = Dtos.asDto(currentSession().find(controllerClass, 1));
    LabDto pathology = Dtos.asDto(currentSession().find(controllerClass, 2));

    bioBank.setAlias("bioBank");
    pathology.setAlias("pathology");

    List<LabDto> dtos = new ArrayList<LabDto>();
    dtos.add(bioBank);
    dtos.add(pathology);

    List<LabImpl> labs =
        (List<LabImpl>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, LabDto::getId);

    assertEquals("bioBank", labs.get(0).getAlias(), "| Biobank not updated. |");
    assertEquals("pathology", labs.get(1).getAlias(), "| Pathology not updated | ");
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLab() throws Exception {
    testBulkDelete(controllerClass, 3, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 3, CONTROLLER_BASE);
  }


}
