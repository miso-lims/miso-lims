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
  public void testUserDataIntegrity() throws Exception {
    String json = testStaticListPage(USERS_ENDPOINT, "data");

    int jsonUserCount = JsonPath.<Integer>read(json, "$.length()");
    assertTrue("Expected at least 3 users in test data", jsonUserCount >= 3);

    for (int i = 0; i < 3; i++) {
      String jsonPath = "$[" + i + "]";
      String loginName = JsonPath.read(json, jsonPath + ".loginName");

      UserImpl expected = currentSession()
          .createQuery("from UserImpl where loginName = :loginName", UserImpl.class)
          .setParameter("loginName", loginName)
          .uniqueResult();

      assertEquals(expected.getId(), readLong(json, jsonPath + ".id"));
      assertEquals(expected.getLoginName(), JsonPath.read(json, jsonPath + ".loginName"));
      assertEquals(expected.getFullName(), JsonPath.read(json, jsonPath + ".fullName"));
      assertEquals(expected.getEmail(), JsonPath.read(json, jsonPath + ".email"));
      assertEquals(expected.isAdmin(), JsonPath.read(json, jsonPath + ".admin"));
      assertEquals(expected.isInternal(), JsonPath.read(json, jsonPath + ".internal"));
      assertEquals(expected.isActive(), JsonPath.read(json, jsonPath + ".active"));
      assertNotNull(JsonPath.read(json, jsonPath + ".loggedIn"));
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
