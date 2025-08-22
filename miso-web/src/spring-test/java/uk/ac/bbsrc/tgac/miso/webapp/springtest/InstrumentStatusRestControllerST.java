package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class InstrumentStatusRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/instrumentstatus";

  @Test
  public void testListAll() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(8)))
        .andExpect(jsonPath("$[0].instrument.id").value(2))
        .andExpect(jsonPath("$[0].positions[0].run.type").value("Base"))
        .andExpect(jsonPath("$[1].instrument.id").value(5002))
        .andExpect(jsonPath("$[2].instrument.id").value(3))
        .andExpect(jsonPath("$[3].instrument.id").value(1))
        .andExpect(jsonPath("$[4].instrument.id").value(5001))
        .andExpect(jsonPath("$[5].instrument.id").value(200))
        .andExpect(jsonPath("$[5].instrument.name").value("HiSeq_200"))
        .andExpect(jsonPath("$[6].instrument.id").value(101))
        .andExpect(jsonPath("$[7].instrument.id").value(100));
  }

}
