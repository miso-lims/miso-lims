package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;
import org.junit.Test;

public class EditRunControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/run";
  private static final long EXISTING_RUN_ID = 5001L;
  private static final String EXISTING_RUN_ALIAS = "Change_Values_Run";
  private static final long SEQUENCER_INSTRUMENT_ID = 5002L;
  private static final long NON_SEQUENCER_INSTRUMENT_ID = 4L;

  @Test
  public void testSetupFormByIdOk() throws Exception {
    Map<String, Object> modelMap = baseTestEditModel(CONTROLLER_BASE + "/" + EXISTING_RUN_ID);

    assertEquals("Run " + EXISTING_RUN_ID, modelMap.get("title"));
    assertNotNull(modelMap.get("run"));
    assertNotNull(modelMap.get("runDto"));
    assertNotNull(modelMap.get("runPositions"));
    assertNotNull(modelMap.get("runPartitions"));
    assertNotNull(modelMap.get("runAliquots"));
    assertNotNull(modelMap.get("partitionConfig"));
    assertNotNull(modelMap.get("experimentConfiguration"));
    assertNotNull(modelMap.get("formConfig"));
    assertNotNull(modelMap.get("metrics"));
    assertNotNull(modelMap.get("partitionNames"));
  }

  @Test
  public void testSetupFormByIdNotFound() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/{runId}", 999999L))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testSetupFormByAliasOk() throws Exception {
    Map<String, Object> modelMap = baseTestEditModel(CONTROLLER_BASE + "/alias/" + EXISTING_RUN_ALIAS);
    assertNotNull(modelMap.get("run"));
    assertNotNull(modelMap.get("formConfig"));
  }

  @Test
  public void testSetupFormByAliasNotFound() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/alias/{runAlias}", "NO_SUCH_ALIAS"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testNewRunOk() throws Exception {
    baseTestNewModel(CONTROLLER_BASE + "/new/" + SEQUENCER_INSTRUMENT_ID, "New Run")
        .andExpect(view().name("/WEB-INF/pages/editRun.jsp"))
        .andExpect(model().attributeExists("run"))
        .andExpect(model().attributeExists("partitionConfig"))
        .andExpect(model().attributeExists("formConfig"));
  }

  @Test
  public void testNewRunInstrumentNotFound() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/new/{instrumentId}", 999999L))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testNewRunInstrumentNotSequencerBadRequest() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/new/{instrumentId}", NON_SEQUENCER_INSTRUMENT_ID))
        .andExpect(status().isBadRequest());
  }
}
