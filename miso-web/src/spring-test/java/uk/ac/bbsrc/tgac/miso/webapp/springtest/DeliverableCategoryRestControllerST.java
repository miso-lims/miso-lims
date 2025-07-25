package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;
import uk.ac.bbsrc.tgac.miso.dto.DeliverableCategoryDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class DeliverableCategoryRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/deliverablecategories";
  private static final Class<DeliverableCategory> controllerClass = DeliverableCategory.class;

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
  public void testBulkCreateAsync() throws Exception {
    List<DeliverableCategory> delcats = baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals("delcat1", delcats.get(0).getName());
    assertEquals("delcat2", delcats.get(1).getName());
  }


  @Test
  public void testBulkUpdateAsync() throws Exception {
    DeliverableCategory release = currentSession().get(controllerClass, 1);
    DeliverableCategory report = currentSession().get(controllerClass, 2);
    release.setName("release");
    report.setName("report");

    List<DeliverableCategoryDto> dtos = new ArrayList<DeliverableCategoryDto>();
    dtos.add(DeliverableCategoryDto.from(release));
    dtos.add(DeliverableCategoryDto.from(report));


    List<DeliverableCategory> deliverableCategorys =
        (List<DeliverableCategory>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos,
            Arrays.asList(1, 2));

    assertEquals("release", deliverableCategorys.get(0).getName());
    assertEquals("report", deliverableCategorys.get(1).getName());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(controllerClass, 3, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 3, CONTROLLER_BASE);
  }
}
