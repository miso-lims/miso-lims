package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionPause;

import static org.hamcrest.Matchers.*;
import org.springframework.test.web.servlet.MvcResult;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionPause;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.Collections;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RequisitionRestController.*;
import java.util.List;
import java.util.function.Function;
import java.util.Arrays;
import java.util.ArrayList;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateRequisitionDao;

import org.springframework.test.web.servlet.MockMvc;



public class RequisitionRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/requisitions";
  private static final Class<Requisition> entityClass = Requisition.class;
  private static final HibernateRequisitionDao dao = new HibernateRequisitionDao();


  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2));
  }

  private <T> void testMove(int id, String type, MoveItemsRequest req, Class<T> targetType,
      Function<T, Identifiable> getReq) throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + id + "/" + type + "/move").content(makeJson(req))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());


    assertTrue(isContained(req.requisitionId(), req.itemIds().get(0), targetType, getReq));
    // ensures that the move has been made

  }

  @Test
  public void testCreate() throws Exception {
    RequisitionDto stoppedReq = new RequisitionDto();
    stoppedReq.setAlias("req2");
    stoppedReq.setStopped(true);
    stoppedReq.setStopReason("too expensive");

    Requisition newReq = baseTestCreate(CONTROLLER_BASE, stoppedReq, entityClass, 201);
    assertEquals(stoppedReq.getAlias(), newReq.getAlias());
    assertEquals(stoppedReq.isStopped(), newReq.isStopped());
    assertEquals(stoppedReq.getStopReason(), newReq.getStopReason());

  }

  @Test
  public void testUpdate() throws Exception {
    RequisitionDto dto = RequisitionDto.from(currentSession().get(entityClass, 1));
    dto.setAlias("updated");

    Requisition updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, entityClass);
    assertEquals(dto.getAlias(), updated.getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // must be admin or creator of requisition to delete
    testBulkDelete(entityClass, 1, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 1, CONTROLLER_BASE);
  }

  @Test
  public void testAddSamples() throws Exception {
    testAddAsync(2L, CONTROLLER_BASE + "/2/samples", Arrays.asList(3L), SampleImpl.class,
        CONTROLLER_BASE + "/samplesupdate",
        SampleImpl::getRequisition);
  }

  @Test
  public void testRemoveSamples() throws Exception {
    testRemoveAsync(1L, CONTROLLER_BASE + "/1/samples/remove", Arrays.asList(2L), SampleImpl.class,
        CONTROLLER_BASE + "/samplesupdate",
        SampleImpl::getRequisition);

  }

  @Test
  public void testMoveSamples() throws Exception {
    MoveItemsRequest req = new MoveItemsRequest(1L, "Req One", 1L, false, "N/A", Arrays.asList(2L));
    testMove(2, "samples", req, SampleImpl.class, SampleImpl::getRequisition);

  }

  @Test
  public void testAddSupplementalSamples() throws Exception {
    // supplemental samples cannot be identities or ghost samples
    dao.setEntityManager(entityManager);

    long reqId = 1L;
    long targetId = 4L;
    Class<SampleImpl> targetClass = SampleImpl.class;
    assertNull(dao.getSupplementalSample(currentSession().get(entityClass, reqId),
        currentSession().get(targetClass, targetId)));


    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + reqId + "/supplementalsamples").content(makeJson(Arrays.asList(targetId)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());


    assertNotNull(dao.getSupplementalSample(currentSession().get(entityClass, reqId),
        currentSession().get(targetClass, targetId)));

  }

  @Test
  public void testRemoveSupplementalSamples() throws Exception {
    long reqId = 2L;
    long targetId = 502L;
    Class<SampleImpl> targetClass = SampleImpl.class;
    dao.setEntityManager(entityManager);

    assertNotNull(dao.getSupplementalSample(currentSession().get(entityClass, reqId),
        currentSession().get(targetClass, targetId)));


    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + reqId + "/supplementalsamples/remove")
            .content(makeJson(Arrays.asList(targetId)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    assertNull(dao.getSupplementalSample(currentSession().get(entityClass, reqId),
        currentSession().get(targetClass, targetId)));

  }

  @Test
  public void testAddLibraries() throws Exception {
    testAddAsync(2L, CONTROLLER_BASE + "/2/libraries", Arrays.asList(1L), LibraryImpl.class,
        CONTROLLER_BASE + "/librariesupdate",
        LibraryImpl::getRequisition);
  }

  @Test
  public void testRemoveLibraries() throws Exception {
    testRemoveAsync(1L, CONTROLLER_BASE + "/1/libraries/remove", Arrays.asList(1L), LibraryImpl.class,
        CONTROLLER_BASE + "/librariesupdate",
        LibraryImpl::getRequisition);
  }

  @Test
  public void testMoveLibraries() throws Exception {
    MoveItemsRequest req = new MoveItemsRequest(1L, "Req One", 1L, false, "N/A", Arrays.asList(204L));
    testMove(204, "libraries", req, LibraryImpl.class, LibraryImpl::getRequisition);
  }

  @Test
  public void testAddSupplementalLibraries() throws Exception {
    long reqId = 1L;
    long targetId = 304L;
    Class<LibraryImpl> targetClass = LibraryImpl.class;
    dao.setEntityManager(entityManager);

    assertNull(dao.getSupplementalLibrary(currentSession().get(entityClass, reqId),
        currentSession().get(targetClass, targetId)));

    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/" + reqId + "/supplementallibraries").content(makeJson(Arrays.asList(targetId)))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    Requisition req = currentSession().get(entityClass, reqId);

    assertNotNull(dao.getSupplementalLibrary(currentSession().get(entityClass, reqId),
        currentSession().get(targetClass, targetId)));
  }

  @Test
  public void testRemoveSupplementalLibraries() throws Exception {
    long reqId = 2L;
    long targetId = 205L;
    Class<LibraryImpl> targetClass = LibraryImpl.class;
    dao.setEntityManager(entityManager);

    assertNotNull(dao.getSupplementalLibrary(currentSession().get(entityClass, reqId),
        currentSession().get(targetClass, targetId)));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + reqId + "/supplementallibraries/remove")
            .content(makeJson(Arrays.asList(targetId)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    assertNull(dao.getSupplementalLibrary(currentSession().get(entityClass, reqId),
        currentSession().get(targetClass, targetId)));
  }

  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/search", "Req One", Arrays.asList(1));
  }

  @Test
  public void testListRunLibraries() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/runlibraries"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].runId").value(1))
        .andExpect(jsonPath("$[0].runAlias").value("MiSeq_Run_1"));
  }


  @Test
  public void testSearchPaused() throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/paused").content(makeJson(Arrays.asList(1L, 2L)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[0].id").value(2))
        .andExpect(jsonPath("$[0].alias").value("Req Two"))
        .andExpect(jsonPath("$[1].id").value(1))
        .andExpect(jsonPath("$[1].alias").value("Req One"));
  }

  @Test
  public void testBulkResume() throws Exception {
    List<Long> reqs = Arrays.asList(1L, 2L);
    String resumeDate = "2025-05-05";
    BulkResumeRequest req = new BulkResumeRequest();
    req.setRequisitionIds(reqs);
    req.setResumeDate(resumeDate);

    ResultActions ac = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-resume").contentType(MediaType.APPLICATION_JSON).content(makeJson(req)));

    MvcResult mvcResult = ac.andExpect(status().isAccepted()).andReturn();

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    String response = pollingResponse(CONTROLLER_BASE + "/bulk/" + id);

    for (Long targetId : reqs) {
      Requisition resumed = currentSession().get(entityClass, targetId);
      RequisitionPause pause = resumed.getPauses().get(resumed.getPauses().size() - 1); // get most recent pause
      assertEquals(resumeDate, pause.getEndDate().toString());
    }
  }
}
