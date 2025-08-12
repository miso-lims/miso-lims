package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.dto.PipelineDto;

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
    List<Pipeline> pipelines = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("one", pipelines.get(0).getAlias());
    assertEquals("two", pipelines.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
    PipelineDto p1 = Dtos.asDto(currentSession().get(entityClass, 1));
    PipelineDto p2 = Dtos.asDto(currentSession().get(entityClass, 2));

    p1.setAlias("p1");
    p2.setAlias("p2");

    List<PipelineDto> dtos = new ArrayList<PipelineDto>();
    dtos.add(p1);
    dtos.add(p2);

    List<Pipeline> pipelines =
        baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, PipelineDto::getId);

    assertEquals(1L, pipelines.get(0).getId());
    assertEquals(2L, pipelines.get(1).getId());
    assertEquals("p1", pipelines.get(0).getAlias());
    assertEquals("p2", pipelines.get(1).getAlias());
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
