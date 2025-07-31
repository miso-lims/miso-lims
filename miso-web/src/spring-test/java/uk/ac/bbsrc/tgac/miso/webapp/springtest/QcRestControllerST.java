package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import org.springframework.web.servlet.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;
import org.springframework.test.web.servlet.MockMvc;


public class QcRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/qcs";
  private static final Class<SampleQC> entityClass = SampleQC.class;
  // there's library, pool, and sample QC entities
  // they're mostly the same, save for their list of control runs and a field referencing the entity
  // that they are attached to

  // given this, it's acceptable to just test SampleQCs


  private List<QcDto> makeCreateDtos() {
    QcDto dto1 = new QcDto();
    dto1.setEntityId(1L);
    dto1.setDate("2025-07-30");
    dto1.setQcTypeId(101L);
    dto1.setResults("5.4");
    dto1.setQcTarget("Sample");
    

    QcDto dto2 = new QcDto();
    dto2.setEntityId(2L);
    dto2.setDate("2025-05-05");
    dto2.setQcTypeId(102L);
    dto2.setResults("9.8");
    dto2.setQcTarget("Sample");

    return Arrays.asList(dto1, dto2);
  }

  @Test
  public void testBulkCreateAsync() throws Exception {
    List<SampleQC> qcs = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals(101L, qcs.get(0).getType().getId());
    assertEquals(102L, qcs.get(1).getType().getId());
  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
    QcDto dto1 = Dtos.asDto(currentSession().get(entityClass, 1));
    QcDto dto2 = Dtos.asDto(currentSession().get(entityClass, 2));

    dto1.setDescription("updated one");
    dto1.setQcTarget("Sample");
    dto2.setDescription("updated two");
    dto2.setQcTarget("Sample");
    
    // setting QC target is required for the request to go through properly

    List<SampleQC> qcs =
        (List<SampleQC>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, Arrays.asList(dto1, dto2),
            Arrays.asList(1, 2));
    assertEquals(dto1.getDescription(), qcs.get(0).getDescription());
    assertEquals(dto2.getDescription(), qcs.get(1).getDescription());

  }

  @Test
  public void testDelete() throws Exception {
    // only admin or owner can delete
    assertNotNull(currentSession().get(entityClass, 2));

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Sample").content(makeJson(Arrays.asList(2L)))
    .contentType(MediaType.APPLICATION_JSON))
     .andExpect(status().isNoContent());
    assertNull(currentSession().get(entityClass, 2));
  }


  @Test
  public void testCreate() throws Exception {
    SampleQC newQc = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), entityClass, 201);
    assertEquals(101L, newQc.getType().getId());
  }

  @Test
  public void testUpdate() throws Exception {
    QcDto dto = Dtos.asDto(currentSession().get(entityClass, 1));

    dto.setDescription("updated one");
    SampleQC updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, entityClass);
    assertEquals(dto.getDescription(), updated.getDescription());
  }
}
