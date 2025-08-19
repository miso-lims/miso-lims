package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.UserDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.UserRestController.PasswordChangeDto;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


public class UserRestControllerST extends AbstractST {


  private static final String CONTROLLER_BASE = "/rest/users";
  private static final Class<UserImpl> entityClass = UserImpl.class;


  // create, update, delete, search, change password
  private UserDto makeCreateDto() throws Exception {
    UserDto dto = new UserDto();
    dto.setAdmin(false);
    dto.setActive(true);
    dto.setInternal(true);
    dto.setLoginName("new");
    dto.setFullName("new guy");
    dto.setPassword("OntarioInstituteOfCancerResearch2025");
    return dto;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // must be admin to create user
    UserImpl user = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
    assertEquals(false, user.isAdmin());
    assertEquals(true, user.isActive());
    assertEquals(true, user.isInternal());
    assertEquals("new", user.getLoginName());
    assertEquals("new guy", user.getFullName());
  }

  @Test
  public void testCreateFail() throws Exception {
    // must be admin to create user
    testCreateUnauthorized(CONTROLLER_BASE, makeCreateDto(), entityClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // must be admin or matching owner to update user

    UserDto user = Dtos.asDto(currentSession().get(entityClass, 1));
    user.setEmail("newemail@gmail.com");
    UserImpl updated = baseTestUpdate(CONTROLLER_BASE, user, 1, entityClass);
    assertEquals(user.getEmail(), updated.getEmail());
  }

  @Test
  public void testUpdateFail() throws Exception {
    // must be admin or matching owner to update user

    UserDto user = Dtos.asDto(currentSession().get(entityClass, 1));
    user.setEmail("newemail@gmail.com");
    testUpdateUnauthorized(CONTROLLER_BASE, user, 1, entityClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // must be admin to delete user
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }


  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE, "hhenderson", Arrays.asList(4));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testChangePassword() throws Exception {
    // must be admin or matching owner to change user password
    PasswordChangeDto dto = new PasswordChangeDto();
    dto.setOldPassword("user");
    dto.setNewPassword("OntarioInstituteOfCancerResearch2025");

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/3/password").content(makeJson(dto)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testChangePasswordFail() throws Exception {
    // must be admin or matching owner to change user password

    PasswordChangeDto dto = new PasswordChangeDto();
    dto.setOldPassword("user");
    dto.setNewPassword("OntarioInstituteOfCancerResearch2025");

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/3/password").content(makeJson(dto)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }
}
