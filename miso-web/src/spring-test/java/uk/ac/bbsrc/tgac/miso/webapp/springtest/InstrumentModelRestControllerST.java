package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentModelDto;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.Arrays;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;
import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;



public class InstrumentModelRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/instrumentmodels";
  private static final Class<InstrumentModel> entityClass = InstrumentModel.class;

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2, 3, 4, 5))
        .andExpect(jsonPath("$.aaData[0].alias").value("Illumina HiSeq 2500"))
        .andExpect(jsonPath("$.aaData[0].instrumentType").value("SEQUENCER"))
        .andExpect(jsonPath("$.aaData[0].dataManglingPolicy").value("NONE"));
  }

  private InstrumentModelDto makeCreateDto() throws Exception {
    InstrumentModelDto dto = new InstrumentModelDto();
    dto.setAlias("testmodel");
    dto.setPlatformType("PACBIO");
    dto.setNumContainers(1);
    dto.setInstrumentType("SEQUENCER");
    dto.setDataManglingPolicy("NONE");
    return dto;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // must be admin to create instrument model
    InstrumentModel newModel = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
    assertEquals("testmodel", newModel.getAlias());
    assertEquals(PlatformType.PACBIO, newModel.getPlatformType());
    assertEquals(1, newModel.getNumContainers());
    assertEquals(InstrumentType.SEQUENCER, newModel.getInstrumentType());
    assertEquals(InstrumentDataManglingPolicy.NONE, newModel.getDataManglingPolicy());

  }

  @Test
  public void testCreateFail() throws Exception {
    // must be admin to create instrument model
    testCreateUnauthorized(CONTROLLER_BASE, makeCreateDto(), entityClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // must be admin to update instrument model

    InstrumentModelDto model = Dtos.asDto(currentSession().get(entityClass, 1));
    model.setAlias("updated");
    InstrumentModel updated = baseTestUpdate(CONTROLLER_BASE, model, 1, entityClass);
    assertEquals("updated", updated.getAlias());
  }

  @Test
  public void testUpdateFail() throws Exception {
    // must be admin to update instrument model

    InstrumentModelDto model = Dtos.asDto(currentSession().get(entityClass, 1));
    model.setAlias("updated");
    testUpdateUnauthorized(CONTROLLER_BASE, model, 1, entityClass);
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
