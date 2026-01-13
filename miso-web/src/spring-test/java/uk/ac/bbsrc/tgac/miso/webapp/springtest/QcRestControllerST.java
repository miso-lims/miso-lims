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
    public void testBulkUpdateAsync() throws Exception {
        QcDto dto1 = Dtos.asDto(currentSession().get(sampleEntityClass, 1));
        QcDto dto2 = Dtos.asDto(currentSession().get(sampleEntityClass, 2));
        QcDto dto3 = Dtos.asDto(currentSession().get(libraryEntityClass, 3));
        QcDto dto4 = Dtos.asDto(currentSession().get(poolEntityClass, 4));

        dto1.setDescription("updated one");
        dto1.setQcTarget("Sample");
        dto2.setDescription("updated two");
        dto2.setQcTarget("Sample");
        dto3.setDescription("updated one library");
        dto3.setQcTarget("Library");
        dto4.setDescription("updated one pool");
        dto4.setQcTarget("Pool");


        List<SampleQC> qcs = baseTestBulkUpdateAsync(CONTROLLER_BASE, sampleEntityClass, Arrays.asList(dto1, dto2),
            QcDto::getId);
        assertEquals(dto1.getDescription(), qcs.get(0).getDescription());
        assertEquals(dto2.getDescription(), qcs.get(1).getDescription());

        List<LibraryQC> libraryQcs = baseTestBulkUpdateAsync(CONTROLLER_BASE, libraryEntityClass, Arrays.asList(dto3),
                QcDto::getId);
        assertEquals(dto3.getDescription(), libraryQcs.get(0).getDescription());

        List<PoolQC> poolQcs = baseTestBulkUpdateAsync(CONTROLLER_BASE, poolEntityClass, Arrays.asList(dto4),
                QcDto::getId);
        assertEquals(dto4.getDescription(), poolQcs.get(0).getDescription());

    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testSampleDelete() throws Exception {
        // only admin or owner can delete
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
        // only admin or owner can delete
        assertNotNull(currentSession().get(libraryEntityClass, 3));

        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Library").content(makeJson(Arrays.asList(3L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertNull(currentSession().get(libraryEntityClass, 3));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testPoolDelete() throws Exception {
        // only admin or owner can delete
        assertNotNull(currentSession().get(poolEntityClass, 4));


        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Pool").content(makeJson(Arrays.asList(4L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertNull(currentSession().get(poolEntityClass, 4));
    }


    @Test
    public void testSampleDeleteFail() throws Exception {
        // only admin or owner can delete

        assertNotNull(currentSession().get(sampleEntityClass, 1));

        getMockMvc()
            .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Sample").content(makeJson(Arrays.asList(1L)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLibraryDeleteFail() throws Exception {
        // only admin or owner can delete
        assertNotNull(currentSession().get(libraryEntityClass, 3));

        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Library").content(makeJson(Arrays.asList(3L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPoolDeleteFail() throws Exception {
        // only admin or owner can delete
        assertNotNull(currentSession().get(poolEntityClass, 4));

        getMockMvc()
                .perform(post(CONTROLLER_BASE + "/bulk-delete").param("qcTarget", "Pool").content(makeJson(Arrays.asList(4L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


      @Test
      public void testSampleCreate() throws Exception {
        SampleQC newQc = baseTestCreate(CONTROLLER_BASE, makeSampleCreateDtos().get(0), sampleEntityClass, 201);
        assertEquals(101L, newQc.getType().getId());
      }

    @Test
    public void testLibraryCreate() throws Exception {
        LibraryQC newQc = baseTestCreate(CONTROLLER_BASE, makeLibraryCreateDtos().get(0), libraryEntityClass, 201);
        assertEquals(104L, newQc.getType().getId());
    }

    @Test
    public void testPoolCreate() throws Exception {
        PoolQC newQc = baseTestCreate(CONTROLLER_BASE, makePoolCreateDtos().get(0), poolEntityClass, 201);
        assertEquals(107L, newQc.getType().getId());
    }

    @Test
    public void testSampleUpdate() throws Exception {
      QcDto dto = Dtos.asDto(currentSession().get(sampleEntityClass, 1));

      dto.setDescription("updated one");
      dto.setQcTarget("Sample");
      SampleQC updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, sampleEntityClass);
      assertEquals(dto.getDescription(), updated.getDescription());
    }

    @Test
    public void testLibraryUpdate() throws Exception {
        QcDto dto = Dtos.asDto(currentSession().get(libraryEntityClass, 3));

        dto.setDescription("updated one");
        dto.setQcTarget("Library");
        LibraryQC updated = baseTestUpdate(CONTROLLER_BASE, dto, 3, libraryEntityClass);
        assertEquals(dto.getDescription(), updated.getDescription());
    }

    @Test
    public void testPoolUpdate() throws Exception {
        QcDto dto = Dtos.asDto(currentSession().get(poolEntityClass, 4));

        dto.setDescription("updated one");
        dto.setQcTarget("Pool");
        PoolQC updated = baseTestUpdate(CONTROLLER_BASE, dto, 4, poolEntityClass);
        assertEquals(dto.getDescription(), updated.getDescription());
    }
}
