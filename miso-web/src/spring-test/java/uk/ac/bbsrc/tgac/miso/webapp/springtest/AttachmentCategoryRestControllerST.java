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
import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.dto.AttachmentCategoryDto;

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


public class AttachmentCategoryRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/attachmentcategories";
  private static final Class<AttachmentCategory> controllerClass = AttachmentCategory.class;


  @Test
  public void testBulkCreateAsync() throws Exception {
    // since there is no permission restrictions on creating attachment categories (i.e. don't need to
    // be admin to create
    // one), there is no create failure test for them
    AttachmentCategoryDto one = new AttachmentCategoryDto();
    one.setAlias("one");
    AttachmentCategoryDto two = new AttachmentCategoryDto();
    two.setAlias("two");

    List<AttachmentCategoryDto> dtos = new ArrayList<AttachmentCategoryDto>();
    dtos.add(one);
    dtos.add(two);

    List<AttachmentCategory> cat = baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, dtos);
    assertEquals(one.getAlias(), cat.get(0).getAlias());
    assertEquals(two.getAlias(), cat.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
    AttachmentCategoryDto one = Dtos.asDto(currentSession().get(controllerClass, 1));
    AttachmentCategoryDto two = Dtos.asDto(currentSession().get(controllerClass, 2));

    one.setAlias("form");
    two.setAlias("tape");

    List<AttachmentCategoryDto> dtos = new ArrayList<AttachmentCategoryDto>();
    dtos.add(one);
    dtos.add(two);

    List<AttachmentCategory> cats =
        (List<AttachmentCategory>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos,
            Arrays.asList(1, 2));

    assertEquals("form", cats.get(0).getAlias());
    assertEquals("tape", cats.get(1).getAlias());
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
