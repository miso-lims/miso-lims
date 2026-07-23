package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class DefaultRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest";

  @Test
  public void testUnmappedRequest() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/failure")).andExpect(status().isNotFound());
  }
}
