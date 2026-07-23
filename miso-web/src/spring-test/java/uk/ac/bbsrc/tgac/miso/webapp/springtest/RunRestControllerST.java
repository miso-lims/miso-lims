package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.core.MediaType;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot.RunPartitionAliquotId;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition.RunPartitionId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.RunLibrarySpreadsheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadSheetFormat;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.RunPartitionAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.dto.run.IlluminaRunDto;
import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RunRestController.RunPartitionPurposeRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RunRestController.RunPartitionQCRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.*;

public class RunRestControllerST extends AbstractST {
  private static final String CONTROLLER_BASE = "/rest/runs";
  private static final Class<IlluminaRun> entityClass = IlluminaRun.class;
  private static final List<Integer> ALL_IDS = Arrays.asList(1, 2, 5001, 5002, 5003, 5004, 5005, 5006, 5008, 5009, 5010,
      5100, 5101);

  @Test
  public void testGetById() throws Exception {
    baseTestGetById(CONTROLLER_BASE, 2)
        .andExpect(jsonPath("$.name").value("RUN2"))
        .andExpect(jsonPath("$.alias").value("PacBio_Run_1"))
        .andExpect(jsonPath("$.status").value("Running"));
  }

  @Test
  public void testGetIdentityParents() throws Exception {
    List<Integer> ids = Arrays.asList(1);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Identity").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("SAM1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("11111"));

  }

  @Test
  public void testGetTissueParents() throws Exception {
    List<Integer> ids = Arrays.asList(1);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Tissue").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id").value(2))
        .andExpect(jsonPath("$[0].name").value("SAM2"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("22222"));

  }

  @Test
  public void testGetTissueProcessingParents() throws Exception {
    List<Integer> ids = Arrays.asList(1);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Tissue Processing").content(makeJson(ids))
                .contentType(MediaType.APPLICATION_JSON))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(5))
        .andExpect(jsonPath("$[0].name").value("SAM5"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("55555"));

  }

  @Test
  public void testGetStockParents() throws Exception {
    List<Integer> ids = Arrays.asList(1);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Stock").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id").value(6))
        .andExpect(jsonPath("$[0].name").value("SAM6"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("66666"));

  }

  @Test
  public void testGetAliquotParents() throws Exception {
    List<Integer> ids = Arrays.asList(1);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Aliquot").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[*].id", containsInAnyOrder(8, 304, 504)))
        .andExpect(jsonPath("$[?(@.id == 304)].name", contains("SAM304")));
  }

  @Test
  public void testGetLibraryParents() throws Exception {
    List<Integer> ids = Arrays.asList(1);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Library").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id").value(304))
        .andExpect(jsonPath("$[0].name").value("LIB304"))
        .andExpect(jsonPath("$[0].description").value("description"));

  }

  @Test
  public void testGetLibraryAliquotParents() throws Exception {
    List<Integer> ids = Arrays.asList(1);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Library Aliquot").content(makeJson(ids))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id").value(304))
        .andExpect(jsonPath("$[0].name").value("LDI304"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("300304"));

  }

  @Test
  public void testGetPoolParents() throws Exception {
    List<Integer> ids = Arrays.asList(1);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Pool").content(makeJson(ids))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("IPO1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("12341"));
  }

  @Test
  public void testGetContainersByRunId() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/containers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].identificationBarcode").value("MISEQXX"));
  }

  @Test
  public void testGetByAlias() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/alias/PacBio_Run_1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.alias").value("PacBio_Run_1"));

  }

  @Test
  public void testDatatableByPlatform() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/platform/PACBIO", Arrays.asList(2));
  }

  @Test
  public void testDatatableBySequencer() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/sequencer/2", Arrays.asList(5100, 5101));
  }

  @Test
  public void testAddContainerByBarcode() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/5002/add")
        .param("position", "")
        .param("barcode", "EXISTING"))
        .andExpect(status().isNoContent());

    Run updated = currentSession().find(entityClass, 5002);
    assertEquals(1, updated.getRunPositions().size());
    RunPosition runPosition = updated.getRunPositions().iterator().next();
    assertEquals(5002L, runPosition.getContainer().getId());
    assertNull(runPosition.getPosition());
  }

  @Test
  public void testRemoveContainer() throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/remove").content(makeJson(Arrays.asList(1L)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    Run updated = currentSession().find(entityClass, 1);
    assertFalse(updated.getSequencerPartitionContainers().stream().anyMatch(x -> x.getId() == 1L));
  }

  @Test
  public void testSetQC() throws Exception {
    RunPartitionQCRequest req = new RunPartitionQCRequest();
    long qcTypeId = 2L;
    req.setQcTypeId(qcTypeId);
    List<Long> partitionIds = Arrays.asList(11L, 12L, 13L, 14L);
    req.setPartitionIds(partitionIds);
    req.setNotes("no notes");
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/qc").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    for (long id : partitionIds) {
      RunPartitionId partId = new RunPartitionId();
      partId.setRunId(1L);
      partId.setPartitionId(id);
      RunPartition part = currentSession().find(RunPartition.class, partId);
      assertEquals(qcTypeId, part.getQcType().getId());
    }
  }

  @Test
  public void testSetPartitionPurposes() throws Exception {
    List<Long> partitionIds = Arrays.asList(11L, 12L, 13L, 14L);
    long runPurposeId = 2L;
    RunPartitionPurposeRequest req = new RunPartitionPurposeRequest();
    req.setPartitionIds(partitionIds);
    req.setRunPurposeId(runPurposeId);

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/partition-purposes").content(makeJson(req))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    for (long id : partitionIds) {
      RunPartitionId partId = new RunPartitionId();
      partId.setRunId(1L);
      partId.setPartitionId(id);
      RunPartition part = currentSession().find(RunPartition.class, partId);
      assertEquals(runPurposeId, part.getPurpose().getId());
    }
  }

  @Test
  public void testSaveAliquots() throws Exception {
    RunPartitionAliquotDto dto = new RunPartitionAliquotDto();
    dto.setRunId(1L);
    dto.setPartitionId(12L);
    dto.setAliquotId(304L);
    dto.setPlatformType("ILLUMINA");
    long qcStatusId = 2L;
    String qcNote = "failed QC";
    dto.setQcStatusId(qcStatusId);
    dto.setQcNote(qcNote);

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/aliquots").content(makeJson(Arrays.asList(dto)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    RunPartitionAliquotId id = new RunPartitionAliquotId(
        currentSession().find(entityClass, 1L),
        currentSession().find(PartitionImpl.class, 12L),
        currentSession().find(ListLibraryAliquotView.class, 304L));
    RunPartitionAliquot updated = currentSession().find(RunPartitionAliquot.class, id);
    assertEquals(qcStatusId, updated.getQcStatus().getId());
    assertEquals(qcNote, updated.getQcNote());
  }

  @Test
  public void testGetPotentialExperiments() throws Exception {
    String response = getMockMvc().perform(get(CONTROLLER_BASE + "/1/potentialExperiments"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    List<?> items = JsonPath.read(response, "$");
    assertNotNull(items);

  }

  @Test
  public void testGetPotentialExperimentsExpansions() throws Exception {
    String response = getMockMvc().perform(get(CONTROLLER_BASE + "/2/potentialExperimentExpansions"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    List<?> items = JsonPath.read(response, "$");
    assertNotNull(items);
  }

  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/search", "RUN51", Arrays.asList(5100, 5101));
  }

  @Test
  public void testGetRecent() throws Exception {
    testList(CONTROLLER_BASE + "/recent", ALL_IDS);
  }

  @Test
  public void testCreate() throws Exception {
    IlluminaRunDto dto = new IlluminaRunDto();
    dto.setInstrumentId(2L);
    dto.setStatus(HealthType.Running.getKey());
    dto.setAlias("new run");
    dto.setPairedEnd(false);

    IlluminaRun newRun = baseTestCreate(CONTROLLER_BASE, dto, entityClass, 200);
    assertEquals(dto.getInstrumentId().longValue(), newRun.getSequencer().getId());
    assertEquals(dto.getStatus(), newRun.getHealth().getKey());
    assertEquals(dto.getAlias(), newRun.getAlias());
  }

  @Test
  public void testUpdate() throws Exception {

    RunDto dto = Dtos.asDto(currentSession().find(entityClass, 1));
    dto.setAlias("updated");

    IlluminaRun updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, entityClass);
    assertEquals(dto.getAlias(), updated.getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 5003, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 5003, CONTROLLER_BASE);
  }

  @Test
  public void testGetSpreadsheet() throws Exception {
    SpreadsheetRequest req = new SpreadsheetRequest();
    req.setFormat("CSV");
    req.setIds(Arrays.asList(1L));
    req.setSheet(RunLibrarySpreadsheets.LIBRARY_SEQUENCING_REPORT.name());

    List<String> headers = Arrays.asList("Instrument Model", "Run Name", "Run Alias", "Pool Name",
        "Library Aliquot Name", "Library Aliquot Alias", "External Name", "Subproject");
    List<List<String>> rows = Arrays.asList(
        Arrays.asList("Illumina HiSeq 2500", "RUN1", "HiSeq_Run_1", "IPO1", "LDI1",
            "TEST_0001_Bn_R_PE_300_WG", "TEST_external_1", ""),
        Arrays.asList("Illumina HiSeq 2500", "RUN1", "HiSeq_Run_1", "IPO501", "LDI304",
            "DILT_0001_nn_n_PE_304_WG", "DILT_identity_1", ""),
        Arrays.asList("Illumina HiSeq 2500", "RUN1", "HiSeq_Run_1", "IPO501", "LDI504",
            "TIB_0001_nn_n_PE_404_WG", "TIB_identity_1", ""));
    String response = getMockMvc().perform(post(CONTROLLER_BASE + "/spreadsheet")
        .content(makeJson(req))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(SpreadSheetFormat.CSV.mediaType()))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String[] rawRows = response.split("\n");

    String[] returnedHeaders = rawRows[0].replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\"", "")
        .split(",", -1);
    checkArray(returnedHeaders, headers);

    List<List<String>> actualRows = new ArrayList<>();
    for (int i = 1; i < rawRows.length; i++) {
      String row = rawRows[i].replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\"", "");
      actualRows.add(Arrays.asList(row.split(",", -1)));
    }

    assertEquals(rows.size(), actualRows.size());
    for (List<String> expectedRow : rows) {
      assertTrue(actualRows.contains(expectedRow));
    }
  }

}
