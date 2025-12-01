package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.jayway.jsonpath.JsonPath;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class ListUsersControllerST extends AbstractST {

  private static final String USERS_ENDPOINT = "/admin/users";

  @Test
  public void testList() throws Exception {
    String json = testStaticListPage(USERS_ENDPOINT, "data");
    List<UserImpl> persisted = currentSession().createQuery("from UserImpl", UserImpl.class).list();

    // verify count matches what we have in db
    assertEquals(persisted.size(), JsonPath.<Integer>read(json, "$.length()").intValue());

    // sanity check: at least one user should exist for admin operations
    assertTrue("Expected at least one user in system", persisted.size() > 0);

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
    List<UserImpl> fromDb = currentSession()
        .createQuery("from UserImpl order by id", UserImpl.class)
        .list();

    // check a sample of users for data consistency
    int samplesToCheck = Math.min(3, fromDb.size());

    for (int i = 0; i < samplesToCheck; i++) {
      UserImpl expected = fromDb.get(i);
      String jsonPath = "$[" + i + "]";

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
