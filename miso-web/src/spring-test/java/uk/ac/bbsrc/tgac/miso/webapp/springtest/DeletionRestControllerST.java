package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;


public class DeletionRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/deletions";

  @Test
  public void testDatatable() throws Exception {
    ResultActions result = testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1));
    result.andExpect(jsonPath("$.aaData[0].description").value("last sample"))
        .andExpect(jsonPath("$.aaData[0].targetType").value("Sample"))
        .andExpect(jsonPath("$.aaData[0].targetId").value(1700));
  }
}
