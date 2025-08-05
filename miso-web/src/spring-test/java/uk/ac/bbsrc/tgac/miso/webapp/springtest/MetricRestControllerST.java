package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;


import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.ThresholdType;
import uk.ac.bbsrc.tgac.miso.dto.MetricDto;



public class MetricRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/metrics";
  private static final Class<Metric> entityClass = Metric.class;

  private List<MetricDto> makeCreateDtos() {

    List<MetricDto> dtos = new ArrayList<MetricDto>();
    MetricDto one = new MetricDto();
    one.setAlias("one");
    one.setCategory("ANALYSIS_REVIEW");
    one.setThresholdType("BETWEEN");
    one.setUnits("%");


    MetricDto two = new MetricDto();
    two.setAlias("two");
    two.setCategory("EXTRACTION");
    two.setThresholdType("GE");
    two.setUnits("%");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<Metric> metrics = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("one", metrics.get(0).getAlias());
    assertEquals(MetricCategory.ANALYSIS_REVIEW, metrics.get(0).getCategory());
    assertEquals(ThresholdType.BETWEEN, metrics.get(0).getThresholdType());
    assertEquals("%", metrics.get(0).getUnits());


    assertEquals("two", metrics.get(1).getAlias());
    assertEquals(MetricCategory.EXTRACTION, metrics.get(1).getCategory());
    assertEquals(ThresholdType.GE, metrics.get(1).getThresholdType());
    assertEquals("%", metrics.get(1).getUnits());

  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // Metric creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
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
        (List<Metric>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, MetricDto::getId);
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

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteMetric() throws Exception {
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }
}
