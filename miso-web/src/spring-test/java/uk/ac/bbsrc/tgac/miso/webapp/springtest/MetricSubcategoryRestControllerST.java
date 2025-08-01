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
import uk.ac.bbsrc.tgac.miso.dto.MetricDto;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.dto.MetricSubcategoryDto;

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


public class MetricSubcategoryRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/metricsubcategories";
  private static final Class<MetricSubcategory> entityClass = MetricSubcategory.class;

  private List<MetricSubcategoryDto> makeCreateDtos() {

    List<MetricSubcategoryDto> dtos = new ArrayList<MetricSubcategoryDto>();
    MetricSubcategoryDto one = new MetricSubcategoryDto();
    one.setAlias("one");
    one.setCategory("FULL_DEPTH_SEQUENCING");


    MetricSubcategoryDto two = new MetricSubcategoryDto();
    two.setAlias("two");
    two.setCategory("EXTRACTION");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<MetricSubcategory> codes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals(codes.get(0).getAlias(), "one");
    assertEquals(codes.get(1).getAlias(), "two");
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // MetricSubcategory creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these MetricSubcategorys so only admin can update them
    MetricSubcategoryDto m1 = MetricSubcategoryDto.from(currentSession().get(MetricSubcategory.class, 1));
    MetricSubcategoryDto m2 = MetricSubcategoryDto.from(currentSession().get(MetricSubcategory.class, 2));

    m1.setAlias("m1");
    m2.setAlias("m2");

    List<MetricSubcategoryDto> dtos = new ArrayList<MetricSubcategoryDto>();
    dtos.add(m1);
    dtos.add(m2);

    List<MetricSubcategory> metricSubcategorys =
        (List<MetricSubcategory>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            MetricSubcategoryDto::getId);
    assertEquals("m1", metricSubcategorys.get(0).getAlias());
    assertEquals("m2", metricSubcategorys.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // the admin user made these MetricSubcategorys so only admin can update them
    MetricSubcategoryDto m1 = MetricSubcategoryDto.from(currentSession().get(MetricSubcategory.class, 1));
    MetricSubcategoryDto m2 = MetricSubcategoryDto.from(currentSession().get(MetricSubcategory.class, 2));
    m1.setAlias("m1");
    m2.setAlias("m2");

    List<MetricSubcategoryDto> dtos = new ArrayList<MetricSubcategoryDto>();
    dtos.add(m1);
    dtos.add(m2);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteMetricSubcategory() throws Exception {
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }
}
