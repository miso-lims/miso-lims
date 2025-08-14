package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.dto.ContainerDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ContainerRestController.SerialNumberValidationDto;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

public class ContainerRestControllerST extends AbstractST {
  private static final String CONTROLLER_BASE = "/rest/containers";
  private static final Class<SequencerPartitionContainerImpl> entityClass = SequencerPartitionContainerImpl.class;


  @Test
  public void testJsonRest() throws Exception {
    // this endpoint is essentially "get by barcode"
    getMockMvc().perform(get(CONTROLLER_BASE + "/PACBIO1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].type").value("Container"))
        .andExpect(jsonPath("$[0].id").value(2))
        .andExpect(jsonPath("$[0].identificationBarcode").value("PACBIO1"))
        .andExpect(jsonPath("$[0].model.id").value(11));
  }

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt",
        Arrays.asList(1, 2, 5002, 5003, 5004, 5005, 5006, 5008, 5009, 5010, 5100, 5101, 6001));
  }

  @Test
  public void testDatatableByPlatform() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/platform/PACBIO", Arrays.asList(2));
  }

  @Test
  public void testCreate() throws Exception {
    ContainerDto dto = new ContainerDto();
    dto.setModel(Dtos.asDto(currentSession().get(SequencingContainerModel.class, 1)));
    dto.setIdentificationBarcode("NEW");

    SequencerPartitionContainerImpl container = baseTestCreate(CONTROLLER_BASE, dto, entityClass, 200);
    assertEquals(dto.getModel().getId().longValue(), container.getModel().getId());
    assertEquals(dto.getIdentificationBarcode(), container.getIdentificationBarcode());

  }

  @Test
  public void testUpdate() throws Exception {
    ContainerDto dto = Dtos.asDto(currentSession().get(entityClass, 1), null);
    dto.setIdentificationBarcode("UPDATED");

    SequencerPartitionContainerImpl updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, entityClass);
    assertEquals(dto.getIdentificationBarcode(), updated.getIdentificationBarcode());
  }

  @Test
  public void testGetSpreadsheet() throws Exception {
    SpreadsheetRequest req = new SpreadsheetRequest();
    req.setFormat("CSV");
    req.setIds(Arrays.asList(1L, 2L));
    req.setSheet("LOADING_CONCENTRATIONS");

    List<String> headers = Arrays.asList("Container", "Partition", "Pool", "Loading Concentration", "Units");
    List<List<String>> rows = Arrays.asList(
        Arrays.asList("MISEQXX", "1", "POOL_1"),
        Arrays.asList("MISEQXX", "2"),
        Arrays.asList("MISEQXX", "3"),
        Arrays.asList("MISEQXX", "4"),
        Arrays.asList("PACBIO1", "1"),
        Arrays.asList("PACBIO1", "2"),
        Arrays.asList("PACBIO1", "3"),
        Arrays.asList("PACBIO1", "4"),
        Arrays.asList("PACBIO1", "5"),
        Arrays.asList("PACBIO1", "6"),
        Arrays.asList("PACBIO1", "7"),
        Arrays.asList("PACBIO1", "8"));
    testSpreadsheetContents(CONTROLLER_BASE + "/spreadsheet", req, headers, rows);
  }

  @Test
  public void testDelete() throws Exception {
    // must be owner or admin to delete
    testBulkDelete(entityClass, 6001, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 1, CONTROLLER_BASE);
  }

  @Test
  public void testValidateSerialNumber() throws Exception {
    SerialNumberValidationDto dto = new SerialNumberValidationDto("MISEQXX", null);

    // null field error
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/validate-serial-number").content(makeJson(dto))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    // mismatched ID error
    dto = new SerialNumberValidationDto("MISEQXX", "2");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/validate-serial-number").content(makeJson(dto))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    // should be correctly validated
    dto = new SerialNumberValidationDto("MISEQXX", "1");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/validate-serial-number").content(makeJson(dto))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

  }
}
