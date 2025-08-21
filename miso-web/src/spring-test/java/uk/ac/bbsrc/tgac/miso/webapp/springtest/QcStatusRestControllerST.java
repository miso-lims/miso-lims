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
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.LibraryAliquotQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.LibraryQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.PoolQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionAliquotQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionQcNodePartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionAliquotQcNode.RunPartitionAliquotQcNodeId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionQcNode.RunPartitionQcNodeId;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;
import org.springframework.test.web.servlet.MockMvc;


public class QcStatusRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/qcstatuses";
  private static final Class<SampleQcNode> sampleQC = SampleQcNode.class;
  private static final Class<LibraryQcNode> libraryQC = LibraryQcNode.class;
  private static final Class<PoolQcNode> poolQC = PoolQcNode.class;

  private static final Class<LibraryAliquotQcNode> libraryAliquotQC = LibraryAliquotQcNode.class;
  private static final Class<RunPartitionAliquotQcNode> runPartAlQC = RunPartitionAliquotQcNode.class;
  private static final Class<RunPartitionQcNode> runPartQC = RunPartitionQcNode.class;
  private static final Class<RunQcNode> runQC = RunQcNode.class;

  @Test
  public void testBulkUpdateSampleQc() throws Exception {
    QcNodeDto dto1 = Dtos.asDto(currentSession().get(sampleQC, 1));
    QcNodeDto dto2 = Dtos.asDto(currentSession().get(sampleQC, 2));

    dto1.setQcStatusId(1L);
    dto1.setQcNote("note 1");


    dto2.setQcStatusId(2L);
    dto2.setQcNote("note 2");
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(Arrays.asList(dto1, dto2))))
        .andExpect(status().isNoContent());


    List<SampleQcNode> qcStatuses =
        Arrays.asList(currentSession().get(sampleQC, 1), currentSession().get(sampleQC, 2));

    assertNotNull(qcStatuses.get(0));
    assertNotNull(qcStatuses.get(1));
    assertEquals(dto1.getQcStatusId(), qcStatuses.get(0).getQcStatusId());
    assertEquals(dto2.getQcStatusId(), qcStatuses.get(1).getQcStatusId());
    assertEquals(dto1.getQcNote(), qcStatuses.get(0).getQcNote());
    assertEquals(dto2.getQcNote(), qcStatuses.get(1).getQcNote());
  }

  @Test
  public void testUpdateSampleQc() throws Exception {
    QcNodeDto dto = Dtos.asDto(currentSession().get(sampleQC, 1));

    dto.setQcStatusId(1L);
    getMockMvc()
        .perform(put(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().isNoContent());

    SampleQcNode updated = currentSession().get(sampleQC, 1);
    assertNotNull(updated);
    assertEquals(dto.getQcStatusId(), updated.getQcStatusId());
  }

  @Test
  public void testBulkUpdateLibraryQc() throws Exception {
    QcNodeDto dto1 = Dtos.asDto(currentSession().get(libraryQC, 1));
    QcNodeDto dto2 = Dtos.asDto(currentSession().get(libraryQC, 204));

    dto1.setQcStatusId(1L);
    dto1.setQcNote("note 1");


    dto2.setQcStatusId(2L);
    dto2.setQcNote("note 2");
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(Arrays.asList(dto1, dto2))))
        .andExpect(status().isNoContent());


    List<LibraryQcNode> qcStatuses =
        Arrays.asList(currentSession().get(libraryQC, 1), currentSession().get(libraryQC, 204));

    assertNotNull(qcStatuses.get(0));
    assertNotNull(qcStatuses.get(1));
    assertEquals(dto1.getQcStatusId(), qcStatuses.get(0).getQcStatusId());
    assertEquals(dto2.getQcStatusId(), qcStatuses.get(1).getQcStatusId());
    assertEquals(dto1.getQcNote(), qcStatuses.get(0).getQcNote());
    assertEquals(dto2.getQcNote(), qcStatuses.get(1).getQcNote());
  }


  @Test
  public void testUpdateLibraryQc() throws Exception {
    QcNodeDto dto = Dtos.asDto(currentSession().get(libraryQC, 1));

    dto.setQcStatusId(1L);
    getMockMvc()
        .perform(put(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().isNoContent());

    LibraryQcNode updated = currentSession().get(libraryQC, 1);
    assertNotNull(updated);
    assertEquals(dto.getQcStatusId(), updated.getQcStatusId());
  }

  @Test
  public void testBulkUpdatePoolQc() throws Exception {
    QcNodeDto dto1 = Dtos.asDto(currentSession().get(poolQC, 1));
    QcNodeDto dto2 = Dtos.asDto(currentSession().get(poolQC, 501));


    dto1.setQcPassed(true);
    dto2.setQcPassed(false);
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(Arrays.asList(dto1, dto2))))
        .andExpect(status().isNoContent());


    List<PoolQcNode> qcStatuses =
        Arrays.asList(currentSession().get(poolQC, 1), currentSession().get(poolQC, 501));

    assertNotNull(qcStatuses.get(0));
    assertNotNull(qcStatuses.get(1));
    assertEquals(dto1.getQcPassed(), qcStatuses.get(0).getQcPassed());
    assertEquals(dto2.getQcPassed(), qcStatuses.get(1).getQcPassed());


  }

  @Test
  public void testUpdatePoolQc() throws Exception {
    QcNodeDto dto = Dtos.asDto(currentSession().get(poolQC, 1));

    dto.setQcPassed(false);
    getMockMvc()
        .perform(put(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().isNoContent());

    PoolQcNode updated = currentSession().get(poolQC, 1);
    assertNotNull(updated);
    assertEquals(dto.getQcPassed(), updated.getQcPassed());
  }


  @Test
  public void testBulkUpdateRunQc() throws Exception {
    QcNodeDto dto1 = Dtos.asDto(currentSession().get(runQC, 1));
    QcNodeDto dto2 = Dtos.asDto(currentSession().get(runQC, 2));


    dto1.setQcPassed(true);
    dto2.setQcPassed(false);
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(Arrays.asList(dto1, dto2))))
        .andDo(print())
        .andExpect(status().isNoContent());


    List<RunQcNode> qcStatuses =
        Arrays.asList(currentSession().get(runQC, 1), currentSession().get(runQC, 2));

    assertNotNull(qcStatuses.get(0));
    assertNotNull(qcStatuses.get(1));
    assertEquals(dto1.getQcPassed(), qcStatuses.get(0).getQcPassed());
    assertEquals(dto2.getQcPassed(), qcStatuses.get(1).getQcPassed());


  }

  @Test
  public void testUpdateRunQc() throws Exception {
    QcNodeDto dto = Dtos.asDto(currentSession().get(runQC, 1));

    dto.setQcPassed(false);
    getMockMvc()
        .perform(put(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andDo(print())
        .andExpect(status().isNoContent());

    RunQcNode updated = currentSession().get(runQC, 1);
    assertNotNull(updated);
    assertEquals(dto.getQcPassed(), updated.getQcPassed());
  }

  @Test
  public void testBulkUpdateRunPartitionQc() throws Exception {
    RunPartitionQcNodeId idOne = new RunPartitionQcNodeId();
    idOne.setRun(currentSession().get(runQC, 1));
    idOne.setPartition(currentSession().get(RunPartitionQcNodePartition.class, 11));

    RunPartitionQcNodeId idTwo = new RunPartitionQcNodeId();
    idTwo.setRun(currentSession().get(runQC, 1));
    idTwo.setPartition(currentSession().get(RunPartitionQcNodePartition.class, 12));

    QcNodeDto dto1 = Dtos.asDto(currentSession().get(runPartQC, idOne));
    QcNodeDto dto2 = Dtos.asDto(currentSession().get(runPartQC, idTwo));

    dto1.setQcStatusId(1L);
    dto2.setQcStatusId(2L);

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(Arrays.asList(dto1, dto2))))
        .andExpect(status().isNoContent());


    List<RunPartitionQcNode> qcStatuses =
        Arrays.asList(currentSession().get(runPartQC, idOne), currentSession().get(runPartQC, idTwo));

    assertNotNull(qcStatuses.get(0));
    assertNotNull(qcStatuses.get(1));
    assertEquals(dto1.getQcStatusId(), qcStatuses.get(0).getQcStatusId());
    assertEquals(dto2.getQcStatusId(), qcStatuses.get(1).getQcStatusId());

  }

  @Test
  public void testUpdateRunPartitionQc() throws Exception {
    RunPartitionQcNodeId id = new RunPartitionQcNodeId();
    id.setRun(currentSession().get(runQC, 1));
    id.setPartition(currentSession().get(RunPartitionQcNodePartition.class, 11));

    QcNodeDto dto = Dtos.asDto(currentSession().get(runPartQC, id));

    dto.setQcStatusId(2L);
    getMockMvc()
        .perform(put(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().isNoContent());

    RunPartitionQcNode updated = currentSession().get(runPartQC, id);
    assertNotNull(updated);
    assertEquals(dto.getQcStatusId(), updated.getQcStatusId());
  }


  @Test
  public void testBulkUpdateRunPartitionAliquotQc() throws Exception {

    RunPartitionAliquotQcNodeId idOne = new RunPartitionAliquotQcNodeId();
    idOne.setAliquot(currentSession().get(libraryAliquotQC, 1));
    idOne.setRun(currentSession().get(runQC, 1));
    idOne.setPartition(currentSession().get(RunPartitionQcNodePartition.class, 11));


    RunPartitionAliquotQcNodeId idTwo = new RunPartitionAliquotQcNodeId();
    idTwo.setRun(currentSession().get(runQC, 1));
    idTwo.setPartition(currentSession().get(RunPartitionQcNodePartition.class, 12));
    idTwo.setAliquot(currentSession().get(libraryAliquotQC, 304));

    QcNodeDto dto1 = Dtos.asDto(currentSession().get(runPartAlQC, idOne));
    QcNodeDto dto2 = Dtos.asDto(currentSession().get(runPartAlQC, idTwo));



    dto1.setQcPassed(true);
    dto1.setQcStatusId(1L); // qc passed


    dto2.setQcStatusId(2L); // qc failed
    dto2.setQcPassed(false);

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(Arrays.asList(dto1, dto2))))
        .andDo(print())
        .andExpect(status().isNoContent());


    List<RunPartitionAliquotQcNode> qcStatuses =
        Arrays.asList(currentSession().get(runPartAlQC, idOne), currentSession().get(runPartAlQC, idTwo));

    assertNotNull(qcStatuses.get(0));
    assertNotNull(qcStatuses.get(1));

    assertEquals(dto1.getQcStatusId(), qcStatuses.get(0).getQcStatusId());
    assertEquals(dto1.getQcPassed(), qcStatuses.get(0).getQcPassed());

    assertEquals(dto2.getQcStatusId(), qcStatuses.get(1).getQcStatusId());
    assertEquals(dto2.getQcPassed(), qcStatuses.get(1).getQcPassed());
  }

  @Test
  public void testUpdateRunPartitionAliquotQc() throws Exception {
    RunPartitionAliquotQcNodeId id = new RunPartitionAliquotQcNodeId();
    id.setRun(currentSession().get(runQC, 1));
    id.setPartition(currentSession().get(RunPartitionQcNodePartition.class, 11));
    id.setAliquot(currentSession().get(libraryAliquotQC, 1));

    QcNodeDto dto = Dtos.asDto(currentSession().get(runPartAlQC, id));

    dto.setQcPassed(false);
    dto.setQcStatusId(5L); // qc failed: STR
    getMockMvc()
        .perform(put(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().isNoContent());

    RunPartitionAliquotQcNode updated = currentSession().get(runPartAlQC, id);
    assertNotNull(updated);
    assertEquals(dto.getQcStatusId(), updated.getQcStatusId());
    assertEquals(dto.getQcPassed(), updated.getQcPassed());
  }


  @Test
  public void testBulkUpdateLibraryAliquotQc() throws Exception {
    QcNodeDto dto1 = Dtos.asDto(currentSession().get(libraryAliquotQC, 1));
    QcNodeDto dto2 = Dtos.asDto(currentSession().get(libraryAliquotQC, 304));

    dto1.setQcStatusId(1L);
    dto1.setQcNote("note 1");


    dto2.setQcStatusId(2L);
    dto2.setQcNote("note 2");
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(Arrays.asList(dto1, dto2))))
        .andExpect(status().isNoContent());


    List<LibraryAliquotQcNode> qcStatuses =
        Arrays.asList(currentSession().get(libraryAliquotQC, 1), currentSession().get(libraryAliquotQC, 304));

    assertNotNull(qcStatuses.get(0));
    assertNotNull(qcStatuses.get(1));
    assertEquals(dto1.getQcStatusId(), qcStatuses.get(0).getQcStatusId());
    assertEquals(dto2.getQcStatusId(), qcStatuses.get(1).getQcStatusId());
    assertEquals(dto1.getQcNote(), qcStatuses.get(0).getQcNote());
    assertEquals(dto2.getQcNote(), qcStatuses.get(1).getQcNote());

  }

  @Test
  public void testUpdateLibraryAliquotQc() throws Exception {
    QcNodeDto dto = Dtos.asDto(currentSession().get(libraryAliquotQC, 1));

    dto.setQcNote("updated");
    getMockMvc()
        .perform(put(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().isNoContent());

    LibraryAliquotQcNode updated = currentSession().get(libraryAliquotQC, 1);
    assertNotNull(updated);
    assertEquals(dto.getQcPassed(), updated.getQcPassed());
  }
}
