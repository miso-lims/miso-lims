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
import org.springframework.security.test.context.support.WithMockUser;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcTypeDto;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;
import org.springframework.test.web.servlet.MockMvc;


public class QcTypeRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/qctypes";
  private static final Class<QcType> entityClass = QcType.class;


  private QcTypeDto makeCreateDto() {
    QcTypeDto dto = new QcTypeDto();
    dto.setName("new type");
    dto.setQcTarget(QcTarget.Sample);
    dto.setArchived(false);
    dto.setPrecisionAfterDecimal(0);
    dto.setCorrespondingField(QcCorrespondingField.NONE);
    dto.setAutoUpdateField(false);
    dto.setDescription("newest qc type");
    dto.setInstrumentModelId(1L);

    return dto;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // only admin can create
    QcType newQCType = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
    assertEquals("new type", newQCType.getName());
    assertEquals(QcTarget.Sample, newQCType.getQcTarget());
    assertEquals(false, newQCType.isArchived());
    assertEquals(0, newQCType.getPrecisionAfterDecimal().intValue());
    assertEquals(QcCorrespondingField.NONE, newQCType.getCorrespondingField());
    assertEquals(false, newQCType.isAutoUpdateField());
    assertEquals("newest qc type", newQCType.getDescription());
    assertEquals(1L, newQCType.getInstrumentModel().getId());
  }

  @Test
  public void testCreateFail() throws Exception {
    testCreateUnauthorized(CONTROLLER_BASE, makeCreateDto(), entityClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // only admin can update

    QcTypeDto dto = Dtos.asDto(currentSession().get(entityClass, 101));
    dto.setDescription("updated");
    QcType updated = baseTestUpdate(CONTROLLER_BASE, dto, dto.getId().intValue(), entityClass);
    assertEquals(dto.getDescription(), updated.getDescription());
  }


  @Test
  public void testUpdateFail() throws Exception {
    QcTypeDto dto = Dtos.asDto(currentSession().get(entityClass, 101));
    dto.setDescription("updated");
    testUpdateUnauthorized(CONTROLLER_BASE, dto, dto.getId().intValue(), entityClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // only admin can delete
    testBulkDelete(entityClass, 110, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 110, CONTROLLER_BASE);
  }

}
