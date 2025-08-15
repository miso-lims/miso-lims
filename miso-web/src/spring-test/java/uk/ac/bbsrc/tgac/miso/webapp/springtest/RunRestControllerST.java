package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import jakarta.ws.rs.core.MediaType;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition.RunPartitionId;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot.RunPartitionAliquotId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.RunLibrarySpreadsheets;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.SampleSheet;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.RunPartitionAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.dto.run.IlluminaRunDto;
import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RunRestController.RunPartitionPurposeRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RunRestController.RunPartitionQCRequest;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Arrays;
import java.util.List;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.beans.factory.annotation.Value;

import com.jayway.jsonpath.JsonPath;
// import java.io.BufferedReader;
// import java.io.FileReader;

// import jakarta.transaction.Transactional;
// import javassist.bytecode.ExceptionTable;

import static org.hamcrest.Matchers.*;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.ObjectWriter;

// import org.springframework.test.web.servlet.MvcResult;
// import uk.ac.bbsrc.tgac.miso.dto.Dtos;

// import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
// import uk.ac.bbsrc.tgac.miso.core.data.Run;
// import uk.ac.bbsrc.tgac.miso.core.data.impl.runimpl;
// import uk.ac.bbsrc.tgac.miso.dto.run.IlluminaIlluminaRunDto;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.Assert.*;

// import java.util.List;
// import java.util.Arrays;
import java.util.ArrayList;
// import java.util.Map;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.Set;

// import org.springframework.test.web.servlet.MockMvc;
// import java.util.Date;

public class RunRestControllerST extends AbstractST {
  private static final String CONTROLLER_BASE = "/rest/runs";
  private static final Class<IlluminaRun> entityClass = IlluminaRun.class;
  private static final List<Integer> ALL_IDS = Arrays.asList(1, 2, 5001, 5002, 5003, 5004, 5005, 5006, 5008, 5009, 5010,
      5100, 5101);

  // all Run classes are basically the same besides Illumina (and Oxford Nanopore
  // to some extent),
  // which have some additional fields

  // since the run rest controller is generalized for all Run types, it should be
  // valid to just test
  // the controller with one specific run class. Illumina Run is used here as an
  // arbitrary choice

  @Test
  public void testGetById() throws Exception {
    baseTestGetById(CONTROLLER_BASE, 2)
        .andExpect(jsonPath("$.name").value("RUN2"))
        .andExpect(jsonPath("$.alias").value("PacBio_Run_1"))
        .andExpect(jsonPath("$.status").value("Running"));
  }

  @Test
  public void testGetIdentityParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Identity").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("SAM1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("11111"));

  }

  @Test
  public void testGetTissueParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Tissue").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(2))
        .andExpect(jsonPath("$[0].name").value("SAM2"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("22222"));

  }

  @Test
  public void testGetTissueProcessingParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Tissue Processing").content(makeJson(ids))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(5))
        .andExpect(jsonPath("$[0].name").value("SAM5"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("55555"));

  }

  @Test
  public void testGetStockParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Stock").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(6))
        .andExpect(jsonPath("$[0].name").value("SAM6"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("66666"));

  }

  @Test
  public void testGetAliquotParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Aliquot").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(8))
        .andExpect(jsonPath("$[0].name").value("SAM8"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("88888"));

  }

  @Test
  public void testGetLibraryParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Library").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("LIB1"))
        .andExpect(jsonPath("$[0].box.id").value("1"))
        .andExpect(jsonPath("$[0].description").value("description lib 1"));

  }

  @Test
  public void testGetLibraryAliquotParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Library Aliquot").content(makeJson(ids))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("LDI1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("12321"));

  }

  @Test
  public void testGetPoolParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Pool").content(makeJson(ids))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("IPO1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("12341"));
  }

  @Test
  public void testGetContainersByRunId() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/containers"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.identificationBarcode").value("MISEQXX"));
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
    // maybe fix position later
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/add").param("position", "B").param("barcode", "EXISTING"))
        .andDo(print())
        .andExpect(status().isNoContent());

    Run updated = currentSession().get(entityClass, 1);
    assertTrue(updated.getSequencerPartitionContainers().stream().anyMatch(x -> x.getId() == 5002L));
  }

  @Test
  public void testRemoveContainer() throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/remove").content(makeJson(Arrays.asList(1L)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    Run updated = currentSession().get(entityClass, 1);
    assertFalse(updated.getSequencerPartitionContainers().stream().anyMatch(x -> x.getId() == 1L));
  }

  @Test
  public void testSetQC() throws Exception {
    RunPartitionQCRequest req = new RunPartitionQCRequest();
    long partitionId = 2L;
    req.setQcTypeId(partitionId);
    List<Long> partitionIds = Arrays.asList(11L, 12L, 13L, 14L);
    req.setPartitionIds(partitionIds);
    req.setNotes("no notes");
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/qc").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    for (long id : partitionIds) {
      RunPartitionId partId = new RunPartitionId();
      partId.setRunId(1L);
      partId.setPartitionId(id);
      RunPartition part = currentSession().get(RunPartition.class, partId);
      assertEquals(partitionId, part.getQcType().getId());
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
      RunPartition part = currentSession().get(RunPartition.class, partId);
      assertEquals(runPurposeId, part.getPurpose().getId());
    }
  }

  @Test
  public void testSaveAliquots() throws Exception {
    RunPartitionAliquotId id = new RunPartitionAliquotId(currentSession().get(entityClass, 5002),
        currentSession().get(PartitionImpl.class, 5101), currentSession().get(ListLibraryAliquotView.class, 304));

    RunPartitionAliquotDto dto = Dtos.asDto(currentSession().get(RunPartitionAliquot.class, id));
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/aliquots").content(makeJson(Arrays.asList(dto)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    RunPartitionAliquot updated = currentSession().get(RunPartitionAliquot.class, id);
    assertEquals(5002L, updated.getRun().getId());
  }

  @Test
  public void testGetPotentialExperiments() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/potentialExperiments"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].studies.*", hasSize(1)))
        .andExpect(jsonPath("$[0].studies[0].id").value(3))
        .andExpect(jsonPath("$[0].studies[0].name").value("STU3"))
        .andExpect(jsonPath("$[0].studies[0].projectId").value(3));

    // TODO

  }

  @Test
  public void testGetPotentialExperimentsExpansions() throws Exception {
    // TODO
    getMockMvc().perform(get(CONTROLLER_BASE + "/2/potentialExperimentExpansions"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].studies.*", hasSize(1)))
        .andExpect(jsonPath("$[0].studies[0].id").value(3))
        .andExpect(jsonPath("$[0].studies[0].name").value("STU3"))
        .andExpect(jsonPath("$[0].studies[0].projectId").value(3));

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

    RunDto dto = Dtos.asDto(currentSession().get(entityClass, 1));
    dto.setAlias("updated");

    IlluminaRun updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, entityClass);
    assertEquals(dto.getAlias(), updated.getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // must be admin or matching owner to delete
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
    req.setIds(Arrays.asList(1L, 2L));
    req.setSheet(RunLibrarySpreadsheets.LIBRARY_SEQUENCING_REPORT.name());

    List<String> headers = Arrays.asList("Instrument Model", "Run Name", "Run Alias", "Pool Name",
        "Library Aliquot Name", "Library Aliquot Alias", "External Name", "Subproject");
    List<List<String>> rows = Arrays.asList(Arrays.asList("Illumina HiSeq 2500", "RUN1", "HiSeq_Run_1", "IPO1", "LDI1",
        "TEST_0001_Bn_R_PE_300_WG", "TEST_external_1"));
    testSpreadsheetContents(CONTROLLER_BASE + "/spreadsheet", req, headers, rows);
  }

  @Test
  public void testGetSampleSheetForRun() throws Exception {
    List<String> headers = Arrays.asList("Sample_ID", "Sample_Name", "I7_Index_ID", "index", "I5_Index_ID", "index2");

    String response = getMockMvc().perform(get(CONTROLLER_BASE + "/1/samplesheet/" + SampleSheet.BCL2FASTQ.name()))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    assertTrue(response.contains("[Data]")); // assert section title
    response = response.split("\\[Data\\]")[1];

    String[] returnedHeaders = new String[headers.size()]; // just for the headers
    String[] rawRows = response.split("\n");

    for (int i = 0; i < 2; i++) { // just for the headers
      String s = rawRows[1].replaceAll("\\r", "");
      s = s.replaceAll("\\n", "");
      s = s.replaceAll("\"", "");
      returnedHeaders = s.split(",");
    }
    checkArray(returnedHeaders, headers);
  }

  @Test
  public void testGetSampleSheetForRunByAlias() throws Exception {
    List<String> headers = Arrays.asList("Sample_ID", "Sample_Name", "I7_Index_ID", "index", "I5_Index_ID", "index2");
    String response = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/alias/HiSeq_Run_1/samplesheet/" + SampleSheet.BCL2FASTQ.name()))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    assertTrue(response.contains("[Data]")); // assert section title
    response = response.split("\\[Data\\]")[1];

    String[] returnedHeaders = new String[headers.size()]; // just for the headers
    String[] rawRows = response.split("\n");

    for (int i = 0; i < 2; i++) { // just for the headers
      String s = rawRows[1].replaceAll("\\r", "");
      s = s.replaceAll("\\n", "");
      s = s.replaceAll("\"", "");
      returnedHeaders = s.split(",");
    }
    checkArray(returnedHeaders, headers);

  }

}
