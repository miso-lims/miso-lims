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
import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.dto.MetricDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;

import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import java.util.Collections;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class MetricRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/metrics";
  private static final Class<Metric> controllerClass = Metric.class;

  private List<MetricDto> makeCreateDtos() {

    List<MetricDto> dtos = new ArrayList<MetricDto>();
    MetricDto one = new MetricDto();
    one.setAlias("one");
    one.setCategory("ANALYSIS_REVIEW");
    one.setThresholdType("BETWEEN");
    one.setUnits("%");


    MetricDto two = new MetricDto();
    two.setAlias("two");
    two.setCategory("ANALYSIS_REVIEW");
    two.setThresholdType("BETWEEN");
    two.setUnits("%");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<Metric> codes = baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals(codes.get(0).getAlias(), "one");
    assertEquals(codes.get(1).getAlias(), "two");
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // Metric creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these Metrics so only admin can update them
    MetricDto m1 = MetricDto.from(currentSession().get(Metric.class, 1));
    MetricDto m2 = MetricDto.from(currentSession().get(Metric.class, 2));

    m1.setAlias("m1");
    m2.setAlias("m2");

    List<MetricDto> dtos = new ArrayList<MetricDto>();
    dtos.add(m1);
    dtos.add(m2);

    List<Metric> metrics =
        (List<Metric>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, MetricDto::getId);
    assertEquals("m1", metrics.get(0).getAlias());
    assertEquals("m2", metrics.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // the admin user made these Metrics so only admin can update them
    MetricDto m1 = MetricDto.from(currentSession().get(Metric.class, 1));
    MetricDto m2 = MetricDto.from(currentSession().get(Metric.class, 2));
    m1.setAlias("m1");
    m2.setAlias("m2");

    List<MetricDto> dtos = new ArrayList<MetricDto>();
    dtos.add(m1);
    dtos.add(m2);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteMetric() throws Exception {
    testBulkDelete(controllerClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 4, CONTROLLER_BASE);
  }
}
