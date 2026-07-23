package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;
import uk.ac.bbsrc.tgac.miso.dto.DeliverableCategoryDto;

import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;


public class DeliverableCategoryRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/deliverablecategories";
  private static final Class<DeliverableCategory> entityClass = DeliverableCategory.class;

  private List<DeliverableCategoryDto> makeCreateDtos() {
    DeliverableCategoryDto del1 = new DeliverableCategoryDto();
    del1.setName("delcat1");

    DeliverableCategoryDto del2 = new DeliverableCategoryDto();
    del2.setName("delcat2");

    List<DeliverableCategoryDto> dtos = new ArrayList<DeliverableCategoryDto>();
    dtos.add(del1);
    dtos.add(del2);
    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<DeliverableCategory> delcats = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("delcat1", delcats.get(0).getName());
    assertEquals("delcat2", delcats.get(1).getName());
  }

  @Test
  public void testCreateUnauthorized() throws Exception {
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    DeliverableCategoryDto release = DeliverableCategoryDto.from(currentSession().find(entityClass, 1));
    DeliverableCategoryDto report = DeliverableCategoryDto.from(currentSession().find(entityClass, 2));
    release.setName("release");
    report.setName("report");

    List<DeliverableCategoryDto> dtos = new ArrayList<DeliverableCategoryDto>();
    dtos.add(release);
    dtos.add(report);


    List<DeliverableCategory> deliverableCategorys =
        (List<DeliverableCategory>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            DeliverableCategoryDto::getId);

    assertEquals(1L, deliverableCategorys.get(0).getId());
    assertEquals(2L, deliverableCategorys.get(1).getId());
    assertEquals("release", deliverableCategorys.get(0).getName());
    assertEquals("report", deliverableCategorys.get(1).getName());
  }

  @Test
  public void testUpdateUnauthorized() throws Exception {
    DeliverableCategoryDto release = DeliverableCategoryDto.from(currentSession().find(entityClass, 1));
    DeliverableCategoryDto report = DeliverableCategoryDto.from(currentSession().find(entityClass, 2));
    release.setName("release");
    report.setName("report");

    List<DeliverableCategoryDto> dtos = new ArrayList<DeliverableCategoryDto>();
    dtos.add(release);
    dtos.add(report);
    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);

  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 3, CONTROLLER_BASE);
  }
}
