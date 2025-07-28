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
import com.eaglegenomics.simlims.core.Group;
import uk.ac.bbsrc.tgac.miso.dto.GroupDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

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


public class GroupRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/groups";
  private static final Class<Group> entityClass = Group.class;

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testAddMembers() throws Exception {

    // must be admin to add group members
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    ids.add(4L);
    int initial = currentSession().get(entityClass, 2).getUsers().size();


    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/2/users").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    Group expanded = currentSession().get(entityClass, 2);
    assertEquals(3, expanded.getUsers().size());
    assertEquals(ids.size(), expanded.getUsers().size() - initial);
  }

  @Test
  public void testAddFail() throws Exception {
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    ids.add(4L);

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/2/users").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testRemoveMembers() throws Exception {
    // must be admin to remove group members
    List<Long> ids = new ArrayList<Long>();
    ids.add(3L);
    ids.add(1L);

    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/1/users/remove").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    Group shrunk = currentSession().get(entityClass, 1);
    assertEquals(0, shrunk.getUsers().size());
  }

  @Test
  public void removeFail() throws Exception {
    List<Long> ids = new ArrayList<Long>();
    ids.add(3L);
    ids.add(1L);

    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/1/users/remove").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // must be admin to create a group
    GroupDto group = new GroupDto();
    group.setName("new group");
    group.setDescription("a new group");


    Group newGroup = baseTestCreate(CONTROLLER_BASE, group, entityClass, 200);
    assertEquals("new group", newGroup.getName());
  }

  @Test
  public void testCreateFail() throws Exception {
    // must be admin to create a group
    GroupDto group = new GroupDto();
    group.setName("new group");
    group.setDescription("a new group");

    testCreateUnauthorized(CONTROLLER_BASE, group, entityClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // must be admin to change an Group


    Group group = currentSession().get(entityClass, 1);

    group.setName("modified");
    Group updatedGroup = baseTestUpdate(CONTROLLER_BASE, Dtos.asDto(group), 1, entityClass);
    assertEquals("modified", updatedGroup.getName());
  }

  @Test
  public void testUpdateFail() throws Exception {
    Group group = currentSession().get(entityClass, 1);

    group.setName("modified");
    testUpdateUnauthorized(CONTROLLER_BASE, Dtos.asDto(group), 1, entityClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 3, CONTROLLER_BASE);
  }
}
