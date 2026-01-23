package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import org.springframework.web.servlet.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.RequisitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.Assert.*;
import org.springframework.test.web.servlet.MockMvc;


public class QcRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/qcs";
  private static final Class<SampleQC> sampleEntityClass = SampleQC.class;
  private static final Class<LibraryQC> libraryEntityClass = LibraryQC.class;
  private static final Class<PoolQC> poolEntityClass = PoolQC.class;
  private static final Class<RequisitionQC> requisitionEntityClass = RequisitionQC.class;


  private List<QcDto> makeSampleCreateDtos() {
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

  private List<QcDto> makeLibraryCreateDtos() {
      QcDto dto = new QcDto();
      dto.setEntityId(1L);
      dto.setDate("2018-07-10");
      dto.setQcTypeId(104L);
      dto.setResults("4.3");
      dto.setQcTarget("Library");

      return Arrays.asList(dto);
  }

  private List<QcDto> makePoolCreateDtos() {
    QcDto dto = new QcDto();
    dto.setEntityId(1L);
    dto.setDate("2018-07-10");
    dto.setQcTypeId(107L);
    dto.setResults("4.3");
    dto.setQcTarget("Pool");

    return Arrays.asList(dto);
  }

    private List<QcDto> makeRequisitionCreateDtos() {
        QcDto dto = new QcDto();
        dto.setEntityId(1L);
        dto.setDate("2021-07-13");
        dto.setQcTypeId(111L);
        dto.setResults("1.2");
        dto.setQcTarget("Requisition");

        return Arrays.asList(dto);
    }

  @Test
  public void testSampleBulkCreateAsync() throws Exception {
      List<SampleQC> qcs = baseTestBulkCreateAsync(CONTROLLER_BASE, sampleEntityClass, makeSampleCreateDtos());
      assertEquals(1L, qcs.get(0).getEntity().getId());
      assertEquals("2025-07-30", qcs.get(0).getDate().toString());
      assertEquals(101L, qcs.get(0).getType().getId());
      assertEquals(5.4, qcs.get(0).getResults().doubleValue(), 0.0);
      assertEquals(QcTarget.Sample, qcs.get(0).getType().getQcTarget());

      assertEquals(2L, qcs.get(1).getEntity().getId());
      assertEquals("2025-05-05", qcs.get(1).getDate().toString());
      assertEquals(102L, qcs.get(1).getType().getId());
      assertEquals(9.8, qcs.get(1).getResults().doubleValue(), 0.0);
      assertEquals(QcTarget.Sample, qcs.get(1).getType().getQcTarget());
  }

    @Test
    public void testLibraryBulkCreateAsync() throws Exception {
        List<LibraryQC> libraryQcs = baseTestBulkCreateAsync(CONTROLLER_BASE, libraryEntityClass, makeLibraryCreateDtos());
        assertEquals(1L, libraryQcs.get(0).getEntity().getId());
        assertEquals("2018-07-10", libraryQcs.get(0).getDate().toString());
        assertEquals(104L, libraryQcs.get(0).getType().getId());
        assertEquals(4.3, libraryQcs.get(0).getResults().doubleValue(), 0.0);
        assertEquals(QcTarget.Library, libraryQcs.get(0).getType().getQcTarget());

    }

    @Test
    public void testPoolBulkCreateAsync() throws Exception {
        List<PoolQC> poolQcs = baseTestBulkCreateAsync(CONTROLLER_BASE, poolEntityClass, makePoolCreateDtos());
        assertEquals(1L, poolQcs.get(0).getEntity().getId());
        assertEquals("2018-07-10", poolQcs.get(0).getDate().toString());
        assertEquals(107L, poolQcs.get(0).getType().getId());
        assertEquals(4.3, poolQcs.get(0).getResults().doubleValue(), 0.0);
        assertEquals(QcTarget.Pool, poolQcs.get(0).getType().getQcTarget());
    }

    @Test
    public void testRequisitionBulkCreateAsync() throws Exception {
        List<RequisitionQC> requisitionQcs = baseTestBulkCreateAsync(CONTROLLER_BASE, requisitionEntityClass, makeRequisitionCreateDtos());
        assertEquals(1L, requisitionQcs.get(0).getEntity().getId());
        assertEquals("2021-07-13", requisitionQcs.get(0).getDate().toString());
        assertEquals(111L, requisitionQcs.get(0).getType().getId());
        assertEquals(1.2, requisitionQcs.get(0).getResults().doubleValue(), 0.0);
        assertEquals(QcTarget.Requisition, requisitionQcs.get(0).getType().getQcTarget());
    }

    @Test
    public void testBulkSampleUpdateAsync() throws Exception {
        QcDto dto1 = Dtos.asDto(currentSession().get(sampleEntityClass, 1));
        QcDto dto2 = Dtos.asDto(currentSession().get(sampleEntityClass, 2));

        dto1.setDescription("updated one");
        dto2.setDescription("updated two");

        List<SampleQC> qcs = baseTestBulkUpdateAsync(CONTROLLER_BASE, sampleEntityClass, Arrays.asList(dto1, dto2),
                QcDto::getId);
        assertEquals(dto1.getDescription(), qcs.get(0).getDescription());
        assertEquals(dto2.getDescription(), qcs.get(1).getDescription());
    }

    @Test
    public void testBulkLibraryUpdateAsync() throws Exception {
        QcDto dto = Dtos.asDto(currentSession().get(libraryEntityClass, 1));

        dto.setDescription("updated one library");

        List<LibraryQC> libraryQcs = baseTestBulkUpdateAsync(CONTROLLER_BASE, libraryEntityClass, Arrays.asList(dto),
                QcDto::getId);
        assertEquals(dto.getDescription(), libraryQcs.get(0).getDescription());
    }

    @Test
    public void testBulkPoolUpdateAsync() throws Exception {
        QcDto dto = Dtos.asDto(currentSession().get(poolEntityClass, 1));

        dto.setDescription("updated one pool");

        List<PoolQC> poolQcs = baseTestBulkUpdateAsync(CONTROLLER_BASE, poolEntityClass, Arrays.asList(dto),
                QcDto::getId);
        assertEquals(dto.getDescription(), poolQcs.get(0).getDescription());
    }

    @Test
    public void testBulkRequisitionUpdateAsync() throws Exception {
        QcDto dto = Dtos.asDto(currentSession().get(requisitionEntityClass, 1));

        dto.setDescription("updated one requisition");

        List<RequisitionQC> requisitionQcs = baseTestBulkUpdateAsync(CONTROLLER_BASE, requisitionEntityClass, Arrays.asList(dto),
                QcDto::getId);
        assertEquals(dto.getDescription(), requisitionQcs.get(0).getDescription());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testSampleDelete() throws Exception {
        assertNotNull(currentSession().get(sampleEntityClass, 1));

        getMockMvc()
            .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Sample").content(makeJson(Arrays.asList(1L)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        assertNull(currentSession().get(sampleEntityClass, 1));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testLibraryDelete() throws Exception {
        assertNotNull(currentSession().get(libraryEntityClass, 1));

        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Library").content(makeJson(Arrays.asList(1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertNull(currentSession().get(libraryEntityClass, 1));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testPoolDelete() throws Exception {
        assertNotNull(currentSession().get(poolEntityClass, 1));


        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Pool").content(makeJson(Arrays.asList(1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertNull(currentSession().get(poolEntityClass, 1));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testRequisitionDelete() throws Exception {
        assertNotNull(currentSession().get(requisitionEntityClass, 1));
        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Requisition").content(makeJson(Arrays.asList(1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertNull(currentSession().get(requisitionEntityClass, 1));
    }


    @Test
    public void testSampleDeleteFail() throws Exception {
        assertNotNull(currentSession().get(sampleEntityClass, 1));

        getMockMvc()
            .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Sample").content(makeJson(Arrays.asList(1L)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLibraryDeleteFail() throws Exception {
        assertNotNull(currentSession().get(libraryEntityClass, 1));

        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Library").content(makeJson(Arrays.asList(1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPoolDeleteFail() throws Exception {
        assertNotNull(currentSession().get(poolEntityClass, 1));

        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Pool").content(makeJson(Arrays.asList(1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRequisitionDeleteFail() throws Exception {
        assertNotNull(currentSession().get(requisitionEntityClass, 1));

        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Requisition").content(makeJson(Arrays.asList(1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
