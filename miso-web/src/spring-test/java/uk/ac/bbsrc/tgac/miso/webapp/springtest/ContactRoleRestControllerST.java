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
import uk.ac.bbsrc.tgac.miso.core.data.impl.ContactRole;
import uk.ac.bbsrc.tgac.miso.dto.ContactRoleDto;

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


public class ContactRoleRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/contactroles";
  private static final Class<ContactRole> entityClass = ContactRole.class;

  private List<ContactRoleDto> makeCreateDtos() {
    ContactRoleDto con1 = new ContactRoleDto();
    con1.setName("contact 1");

    ContactRoleDto con2 = new ContactRoleDto();
    con2.setName("contact 2");

    List<ContactRoleDto> dtos = new ArrayList<ContactRoleDto>();
    dtos.add(con1);
    dtos.add(con2);
    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<ContactRole> roles = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("contact 1", roles.get(0).getName());
    assertEquals("contact 2", roles.get(1).getName());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // contact roles can only be created by an admin user, so this test expected failure due to
    // insufficient permissions

    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // contact roles can only be updated by an admin user
    ContactRoleDto con2 = Dtos.asDto(currentSession().get(entityClass, 2));
    ContactRoleDto con3 = Dtos.asDto(currentSession().get(entityClass, 3));
    con2.setName("con2");
    con3.setName("con3");

    List<ContactRoleDto> dtos = new ArrayList<ContactRoleDto>();
    dtos.add(con2);
    dtos.add(con3);

    List<ContactRole> contactRoles =
        (List<ContactRole>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            Arrays.asList(2, 3));

    assertEquals(2L, contactRoles.get(0).getId());
    assertEquals(3L, contactRoles.get(1).getId());
    assertEquals("con2", contactRoles.get(0).getName());
    assertEquals("con3", contactRoles.get(1).getName());
  }


  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // contact roles can only be updated by an admin user, so this test expects failure due to
    // insufficient permissions
    ContactRoleDto con2 = Dtos.asDto(currentSession().get(entityClass, 2));
    ContactRoleDto con3 = Dtos.asDto(currentSession().get(entityClass, 3));
    con2.setName("con2");
    con3.setName("con3");

    List<ContactRoleDto> dtos = new ArrayList<ContactRoleDto>();
    dtos.add(con2);
    dtos.add(con3);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }
}
