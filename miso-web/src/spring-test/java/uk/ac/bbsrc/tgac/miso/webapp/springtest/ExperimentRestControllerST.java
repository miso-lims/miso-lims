package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.dto.ExperimentDto;
import uk.ac.bbsrc.tgac.miso.dto.KitConsumableDto;
import uk.ac.bbsrc.tgac.miso.dto.ExperimentDto.RunPartitionDto;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;
import java.time.LocalDate;


public class ExperimentRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/experiments";
  private static final Class<Experiment> entityClass = Experiment.class;


  @Test
  public void testAddKit() throws Exception {
    KitImpl kit = currentSession().get(KitImpl.class, 1);

    KitConsumableDto dto = Dtos.asDto(kit);
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/2/addkit").content(makeJson(dto)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    Experiment added = currentSession().get(entityClass, 2);
    assertEquals(1, added.getKits().size());
    assertEquals(kit.getId(), added.getKits().iterator().next().getId());
    assertEquals(kit.getName(), added.getKits().iterator().next().getName());
    // just checking that the right kit was added to the experiment
  }

  @Test
  public void testAddPartition() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/2/add").param("runId", "1").param("partitionId", "11"))
        .andExpect(status().isOk());

    Experiment exp2 = currentSession().get(entityClass, 2);
    assertEquals(exp2.getRunPartitions().get(0).getRun(), currentSession().get(Run.class, 1));
  }


  @Test
  public void testList() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE)).andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$[0].alias").value("Experiment One"))
        .andExpect(jsonPath("$[0].library.box.useId").value("1"))
        .andExpect(jsonPath("$[0].partitions[0].partition.containerId").value("1"))
        .andExpect(jsonPath("$[0].id").value("1"));
  }


  @Test
  public void testGetById() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1")).andExpect(status().isOk())
        .andExpect(jsonPath("$.alias").value("Experiment One"))
        .andExpect(jsonPath("$.library.box.useId").value("1"))
        .andExpect(jsonPath("$.partitions[0].partition.containerId").value("1"))
        .andExpect(jsonPath("$.id").value("1"));
  }


  @Test
  public void testCreate() throws Exception {
    ExperimentDto dto = new ExperimentDto();
    dto.setTitle("title");
    dto.setInstrumentModel(Dtos.asDto(currentSession().get(InstrumentModel.class, 2)));
    dto.setLibrary(Dtos.asDto(currentSession().get(LibraryImpl.class, 1), false));
    dto.setPartitions(new ArrayList<RunPartitionDto>());
    dto.setStudy(Dtos.asDto(currentSession().get(StudyImpl.class, 3)));
    Experiment exp = baseTestCreate(CONTROLLER_BASE, dto, entityClass, 200);

    assertEquals(dto.getTitle(), exp.getTitle());
    assertEquals(dto.getInstrumentModel().getId(), exp.getInstrumentModel().getId());
    assertEquals((long) dto.getLibrary().getId(), exp.getLibrary().getId());
    assertTrue(exp.getRunPartitions().isEmpty());
    assertEquals((long) dto.getStudy().getId(), exp.getStudy().getId());

  }

  @Test
  public void testUpdate() throws Exception {
    Experiment exp = currentSession().get(entityClass, 3);

    exp.setTitle("updated");


    Experiment updated = baseTestUpdate(CONTROLLER_BASE, Dtos.asDto(exp), 3, entityClass);
    assertEquals("updated", exp.getTitle()); // the only thing that has been modified is the title
    assertEquals(exp.getAlias(), updated.getAlias());
    assertEquals(exp.getTitle(), updated.getTitle());

    for (int i = 0; i < exp.getChangeLog().size(); i++) {
      assertEquals(exp.getChangeLog().get(i).getId(), updated.getChangeLog().get(i).getId());
    }
    assertEquals(exp.getAccession(), updated.getAccession());
    assertEquals(exp.getDescription(), updated.getDescription());
  }


  @Test
  public void testDelete() throws Exception {
    // must be creator or admin to delete experiment
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 3, CONTROLLER_BASE);
  }
}
