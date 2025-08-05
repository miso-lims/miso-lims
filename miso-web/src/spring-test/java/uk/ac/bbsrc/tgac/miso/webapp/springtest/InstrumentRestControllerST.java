package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.Assert.*;

import com.jayway.jsonpath.JsonPath;
import static org.hamcrest.Matchers.*;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentDto;
import uk.ac.bbsrc.tgac.miso.dto.ServiceRecordDto;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Arrays;
import java.util.Set;


public class InstrumentRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/instruments";
  private static final Class<InstrumentImpl> entityClass = InstrumentImpl.class;

  @Test
  public void testGetById() throws Exception {
    baseTestGetById(CONTROLLER_BASE, 1)
        .andExpect(jsonPath("$.name").value("T2000"))
        .andExpect(jsonPath("$.instrumentModelAlias").value("Illumina HiSeq 2500"))
        .andExpect(jsonPath("$.status").value("Production"));
  }

  @Test
  public void testListAll() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(11)))
        .andExpect(jsonPath("$[0].name").value("T2000"))
        .andExpect(jsonPath("$[1].instrumentModelAlias").value("Illumina MiSeq"))
        .andExpect(jsonPath("$[3].instrumentType").value("ARRAY_SCANNER"))
        .andExpect(jsonPath("$[6].dateCommissioned").value("2017-02-01"));
  }

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2, 3, 4, 5, 100, 101, 102, 200, 5001, 5002))
        .andExpect(jsonPath("$.aaData[0].name").value("T2000"))
        .andExpect(jsonPath("$.aaData[1].instrumentModelAlias").value("Illumina MiSeq"))
        .andExpect(jsonPath("$.aaData[3].instrumentType").value("ARRAY_SCANNER"))
        .andExpect(jsonPath("$.aaData[6].dateCommissioned").value("2017-02-01"));
  }

  @Test
  public void testDatatableByInstrumentType() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/instrument-type/ARRAY_SCANNER", Arrays.asList(4, 5))
        .andExpect(jsonPath("$.aaData[0].name").value("iScan1"))
        .andExpect(jsonPath("$.aaData[1].name").value("Deletable"));
  }

  @Test
  public void testDatatableByWorkstation() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/workstation/1", Arrays.asList(1))
        .andExpect(jsonPath("$.iTotalRecords").value(1))
        .andExpect(jsonPath("$.aaData[0].name").value("T2000"))
        .andExpect(jsonPath("$.aaData[0].instrumentModelAlias").value("Illumina HiSeq 2500"));
  }

  @Test
  public void testCreateRecord() throws Exception {
    ServiceRecordDto dto = new ServiceRecordDto();

    dto.setTitle("new");
    dto.setServiceDate("2025-07-21");
    dto.setServicedBy("Test Person");

    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/1/servicerecords").content(makeJson(dto)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    Set<ServiceRecord> records = currentSession().get(entityClass, 1).getServiceRecords();    
    assertTrue(records.size() == 1);
    ServiceRecord rec = records.iterator().next();
    assertEquals(dto.getTitle(), rec.getTitle());
    assertEquals(dto.getServiceDate(), rec.getServiceDate().toString());
    assertEquals(dto.getServicedBy(), rec.getServicedByName());
  }

  private InstrumentDto makeCreateDto() throws Exception {
    InstrumentDto dto = new InstrumentDto();
    dto.setName("new instrument");
    dto.setInstrumentModelId(1L);
    dto.setDefaultRunPurposeId(1L);
    return dto;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // must be admin to create instrument
    InstrumentImpl newInstrument = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 201);
    assertEquals("new instrument", newInstrument.getName());
    assertEquals(1L, newInstrument.getInstrumentModel().getId());
    assertEquals(1L, newInstrument.getDefaultRunPurpose().getId());
  }

  @Test
  public void testCreateFail() throws Exception {
    // must be admin to create instrument
    testCreateUnauthorized(CONTROLLER_BASE, makeCreateDto(), entityClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // must be admin to update instrument

    InstrumentDto dto = Dtos.asDto(currentSession().get(entityClass, 1));
    dto.setName("updated");
    InstrumentImpl updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, entityClass);
    assertEquals("updated", updated.getName());
  }

  @Test
  public void testUpdateFail() throws Exception {
    // must be admin to update instrument model

    InstrumentDto dto = Dtos.asDto(currentSession().get(entityClass, 1));
    dto.setName("updated");
    testUpdateUnauthorized(CONTROLLER_BASE, dto, 1, entityClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // must be admin to delete instrument model
    testBulkDelete(entityClass, 5, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 5, CONTROLLER_BASE);
  }
}
