package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.MediaType;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.SampleSheetRestController.SampleSheetRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SampleSheetRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/samplesheets";

  @Test
  public void testListByPlatform() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "?platform=ILLUMINA"))
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Empty Illumina"))
        .andExpect(jsonPath("$[0].platformType").value("ILLUMINA"))
        .andExpect(jsonPath("$[1].id").value(3))
        .andExpect(jsonPath("$[1].name").value("Clone Checking"))
        .andExpect(jsonPath("$[1].platformType").value("ILLUMINA"));
  }

  @Test
  public void testGenerate() throws Exception {
    long instrumentModelId = 2L;
    long containerModelId = 3L;
    Long sequencingParametersId = 4L;
    ObjectNode customParameters = new ObjectMapper().createObjectNode();
    customParameters.put("Genome Folder", "/some/path");
    customParameters.put("Custom Read 1 Primer Well", "");
    customParameters.put("Custom Index Primer Well", "");
    customParameters.putNull("Custom Read 2 Primer Well");
    customParameters.putNull("Date");
    Map<String, Map<Integer, Long>> poolIdsByInstrumentPositionAndPartition = new HashMap<>();
    poolIdsByInstrumentPositionAndPartition.put("*", new HashMap<>());
    poolIdsByInstrumentPositionAndPartition.get("*").put(1, 1L);
    SampleSheetRequest request = new SampleSheetRequest(instrumentModelId, containerModelId, sequencingParametersId,
        null, customParameters, poolIdsByInstrumentPositionAndPartition, null);

    String response = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/3/generate").content(makeJson(request))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    List<String> sectionTitles = Arrays.asList("[Header]", "[Reads]", "[Settings]", "[Data]");
    for (String title : sectionTitles) {
      assertTrue(response.contains(title));
    }
  }
}
