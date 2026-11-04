package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.*;
import uk.ac.bbsrc.tgac.miso.dto.*;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.SampleSheetRestController.SampleSheetRequest;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    ObjectNode customParameters = JsonMapper.builder().build().createObjectNode();
    customParameters.put("Genome Folder", "/some/path");
    customParameters.put("Custom Read 1 Primer Well", "");
    customParameters.put("Custom Index Primer Well", "");
    customParameters.putNull("Custom Read 2 Primer Well");
    customParameters.putNull("Date");
    Map<String, Map<Integer, Long>> poolIdsByInstrumentPositionAndPartition = new HashMap<>();
    poolIdsByInstrumentPositionAndPartition.put("*", new HashMap<>());
    poolIdsByInstrumentPositionAndPartition.get("*").put(1, 1L);
    SampleSheetRequest request =
        new SampleSheetRequest(null, instrumentModelId, containerModelId, sequencingParametersId,
            null, customParameters, poolIdsByInstrumentPositionAndPartition, null);

    String response = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/3/generate").content(makeJson(request))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    List<String> sectionTitles = Arrays.asList("[Header]", "[Reads]", "[Settings]", "[Data]");
    for (String title : sectionTitles) {
      assertTrue(response.contains(title));
    }
  }

  @Test
  public void testCreateFail() throws Exception {
    testCreateUnauthorized(CONTROLLER_BASE, makeCreateDto(), SampleSheet.class);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    SampleSheetDto dto = makeCreateDto();
    SampleSheet created = baseTestCreate(CONTROLLER_BASE, dto, SampleSheet.class, 201);
    assertEquals(dto.getName(), created.getName());
    assertEquals(dto.getPlatformType(), created.getPlatformType().name());
    assertEquals(1, created.getSections().size());
    SampleSheetSectionDto sectionDto = dto.getSections().get(0);
    SampleSheetSection createdSection = created.getSections().get(0);
    assertEquals(sectionDto.getName(), createdSection.getName());
    assertEquals(sectionDto.getFormat(), createdSection.getFormat().name());
    assertEquals(1, createdSection.getFields().size());
    SampleSheetFieldDto fieldDto = sectionDto.getFields().get(0);
    SampleSheetField createdField = createdSection.getFields().get(0);
    assertEquals(fieldDto.getName(), createdField.getName());
    assertEquals(1, createdField.getSources().size());
    SampleSheetFieldSourceDto sourceDto = fieldDto.getSources().get(0);
    SampleSheetFieldSource createdSource = createdField.getSources().get(0);
    assertEquals(sourceDto.getSource(), createdSource.getSource());
    assertEquals(sourceDto.getSourceProperty(), createdSource.getSourceProperty());
  }

  private SampleSheetDto makeCreateDto() {
    SampleSheetDto dto = new SampleSheetDto();
    dto.setName("Test Sheet");
    dto.setPlatformType("ILLUMINA");
    List<SampleSheetSectionDto> sectionDtos = new ArrayList<>();
    SampleSheetSectionDto sectionDto = new SampleSheetSectionDto();
    sectionDto.setName("Section 1");
    sectionDto.setFormat("COLUMNS");
    List<SampleSheetFieldDto> fieldDtos = new ArrayList<>();
    SampleSheetFieldDto fieldDto = new SampleSheetFieldDto();
    fieldDto.setName("Field 1");
    List<SampleSheetFieldSourceDto> sourceDtos = new ArrayList<>();
    SampleSheetFieldSourceDto sourceDto = new SampleSheetFieldSourceDto();
    sourceDto.setSource(SampleSheetFieldCommonSource.LIBRARY_ALIQUOT.name());
    sourceDto.setSourceProperty(LibraryAliquotProperty.ALIAS.name());
    sourceDtos.add(sourceDto);
    fieldDto.setSources(sourceDtos);
    fieldDtos.add(fieldDto);
    sectionDto.setFields(fieldDtos);
    sectionDtos.add(sectionDto);
    dto.setSections(sectionDtos);
    return dto;
  }


  @Test
  public void testUpdateFail() throws Exception {
    SampleSheetDto dto = SampleSheetDto.from(currentSession().find(SampleSheet.class, 3L));
    dto.setName("Updated Name");
    testUpdateUnauthorized(CONTROLLER_BASE, dto, dto.getId().intValue(), SampleSheet.class);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    SampleSheetDto dto = SampleSheetDto.from(currentSession().find(SampleSheet.class, 3L));
    dto.setName("Updated Name");

    SampleSheet updated = baseTestUpdate(CONTROLLER_BASE, dto, 3, SampleSheet.class);
    assertEquals(dto.getName(), updated.getName());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkDelete() throws Exception {
    // must be admin or creator of requisition to delete
    testBulkDelete(SampleSheet.class, 1, CONTROLLER_BASE);
  }

  @Test
  public void testBulkDeleteFail() throws Exception {
    testDeleteUnauthorized(SampleSheet.class, 1, CONTROLLER_BASE);
  }
}
