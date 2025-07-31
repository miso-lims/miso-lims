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
import uk.ac.bbsrc.tgac.miso.dto.QcNodeDto;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;


import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;
import org.springframework.test.web.servlet.MockMvc;


public class QcStatusRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/qcstatuses";
  private static final Class<SampleQcNode> entityClass = SampleQcNode.class;
  // there's library, pool, and sample QC nodes (and others)
  // they are largely the same, so for endpoint testing,
  // it's acceptable to just test SampleQCs



  @Test
  public void testBulkUpdateAsync() throws Exception {
    QcNodeDto dto1 = Dtos.asDto(currentSession().get(entityClass, 1));
    QcNodeDto dto2 = Dtos.asDto(currentSession().get(entityClass, 2));

    dto1.setQcStatusId(1L);
    dto1.setQcNote("note 1");


    dto2.setQcStatusId(2L);
    dto2.setQcNote("note 2");

    List<SampleQcNode> qcsStatuses = (List<SampleQcNode>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass,
        Arrays.asList(dto1, dto2), Arrays.asList(1, 2));

    assertEquals(dto1.getQcStatusId(), qcsStatuses.get(0).getQcStatusId());
    assertEquals(dto2.getQcStatusId(), qcsStatuses.get(1).getQcStatusId());
    assertEquals(dto1.getQcNote(), qcsStatuses.get(0).getQcNote());
    assertEquals(dto2.getQcNote(), qcsStatuses.get(1).getQcNote());
  }

  @Test
  public void testUpdate() throws Exception {
    QcNodeDto dto = Dtos.asDto(currentSession().get(entityClass, 1));

    dto.setQcStatusId(1L);
    getMockMvc()
        .perform(put(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().isNoContent());

    SampleQcNode updated = currentSession().get(entityClass, 1);
    assertNotNull(updated);
    assertEquals(dto.getQcStatusId(), updated.getQcStatusId());
  }
}
