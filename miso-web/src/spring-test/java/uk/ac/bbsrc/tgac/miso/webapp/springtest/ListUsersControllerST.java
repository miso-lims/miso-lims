package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.jayway.jsonpath.JsonPath;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class ListUsersControllerST extends AbstractST {

  private static final String USERS_ENDPOINT = "/admin/users";

  @Test
  public void testList() throws Exception {
    String json = testStaticListPage(USERS_ENDPOINT, "data");

    // verify response contains users
    int userCount = JsonPath.<Integer>read(json, "$.length()");
    assertTrue("Expected at least one user in response", userCount > 0);

    // spot check first user has required fields
    assertNotNull(JsonPath.read(json, "$[0].id"));
    assertNotNull(JsonPath.read(json, "$[0].loginName"));
    assertNotNull(JsonPath.read(json, "$[0].fullName"));
    assertNotNull(JsonPath.read(json, "$[0].email"));

    // boolean flags should be present
    assertNotNull(JsonPath.read(json, "$[0].admin"));
    assertNotNull(JsonPath.read(json, "$[0].internal"));
    assertNotNull(JsonPath.read(json, "$[0].active"));
    assertNotNull(JsonPath.read(json, "$[0].loggedIn"));
  }

  @Test
  public void testUserDataIntegrity() throws Exception {
    String json = testStaticListPage(USERS_ENDPOINT, "data");

    // check a sample of users from the JSON response for data consistency
    int jsonUserCount = JsonPath.<Integer>read(json, "$.length()");
    int samplesToCheck = Math.min(3, jsonUserCount);

    for (int i = 0; i < samplesToCheck; i++) {
      String jsonPath = "$[" + i + "]";
      String loginName = JsonPath.read(json, jsonPath + ".loginName");

      // query database for this specific user by login name
      UserImpl expected = currentSession()
          .createQuery("from UserImpl where loginName = :loginName", UserImpl.class)
          .setParameter("loginName", loginName)
          .uniqueResult();

      assertEquals(expected.getId(), readLong(json, jsonPath + ".id"));
      assertEquals(expected.getLoginName(), JsonPath.read(json, jsonPath + ".loginName"));
      assertEquals(expected.getFullName(), JsonPath.read(json, jsonPath + ".fullName"));
      assertEquals(expected.getEmail(), JsonPath.read(json, jsonPath + ".email"));
    }
  }

  @Test
  public void testPageTitle() throws Exception {
    getMockMvc()
        .perform(get(USERS_ENDPOINT).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(model().attribute("title", "Users"));
  }
}
