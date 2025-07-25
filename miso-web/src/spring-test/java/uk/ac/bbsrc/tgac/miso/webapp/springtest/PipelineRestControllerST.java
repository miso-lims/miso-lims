package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import jakarta.transaction.Transactional;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.dto.PipelineDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class PipelineRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/pipelines";
  private static final Class<Pipeline> entityClass = Pipeline.class;

  private List<PipelineDto> makeCreateDtos() {

    List<PipelineDto> dtos = new ArrayList<PipelineDto>();
    PipelineDto one = new PipelineDto();
    one.setAlias("one");

    PipelineDto two = new PipelineDto();
    two.setAlias("two");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  public void testBulkCreateAsync() throws Exception {
    List<Pipeline> codes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("one", codes.get(0).getAlias());
    assertEquals("two", codes.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
    PipelineDto p1 = Dtos.asDto(currentSession().get(Pipeline.class, 1));
    PipelineDto p2 = Dtos.asDto(currentSession().get(Pipeline.class, 2));

    p1.setAlias("p1");
    p2.setAlias("p2");

    List<PipelineDto> dtos = new ArrayList<PipelineDto>();
    dtos.add(p1);
    dtos.add(p2);

    List<Pipeline> Pipelines =
        (List<Pipeline>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, Arrays.asList(1, 2));
    
    assertEquals(1L, Pipelines.get(0).getId());
    assertEquals(2L, Pipelines.get(1).getId());
    assertEquals("p1", Pipelines.get(0).getAlias());
    assertEquals("p2", Pipelines.get(1).getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeletePipeline() throws Exception {
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 3, CONTROLLER_BASE);
  }
}
