package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;
import org.springframework.test.web.servlet.MvcResult;
import java.util.stream.Stream;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionPause;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary.RequisitionSupplementalLibraryId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample.RequisitionSupplementalSampleId;
import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RequisitionRestController.*;
import java.util.List;
import java.util.function.Function;
import java.util.Arrays;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;


public class RequisitionRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/requisitions";
  private static final Class<Requisition> entityClass = Requisition.class;

  // template test for requsition sample and library testing
  protected <D> void testAddAsync(long requisitionId, String url, List<Long> entityIds, Class<D> entityType,
      String pollUrl, Function<D, Identifiable> getRequisition)
      throws Exception {

    for (Long entityId : entityIds) {
      assertFalse(isContained(requisitionId, entityId, entityType, getRequisition));
      // not already in that req
    }

    String response = pollingResponserHelper("post", entityIds, url, pollUrl, 200);
    for (Long entityId : entityIds) {
      assertTrue(isContained(requisitionId, entityId, entityType, getRequisition));
      // asserts the objects were indeed added
    }
  }

  // template test for requsition sample and library testing
  private <D> void testRemoveAsync(long requisitionId, String url, List<Long> entityIds, Class<D> entityType,
      String pollUrl, Function<D, Identifiable> getRequisition)
      throws Exception {

    for (Long entityId : entityIds) {
      assertTrue(isContained(requisitionId, entityId, entityType, getRequisition));
      // currently associated with that req
    }

    String response = pollingResponserHelper("post", entityIds, url, pollUrl, 200);
    for (Long entityId : entityIds) {
      assertFalse(isContained(requisitionId, entityId, entityType, getRequisition));
      // asserts the objects were indeed removed from that requisition
      // as a result that entity should not have any requisition so a list of the
      // requisitions should be empty
    }
  }

  // template test for requsition sample and library testing
  private <T> boolean isContained(long requisitionId, long entityId, Class<T> entityType,
      Function<T, Identifiable> getRequisition) {
    List<Identifiable> parent =
        Stream.of(entityType.cast(currentSession().get(entityType, entityId))).map(getRequisition)
            .filter(s -> s != null)
            .toList();
    boolean isNotContained = true; // true by default
    if (!parent.isEmpty()) { // if the entity has a parent
      if (parent.get(0).getId() == requisitionId) { // if the parent ids match
        isNotContained = false;
      } else {
        // for debugging
        System.out.println(
            "ACTUAL REQUISITION OF " + entityType.toGenericString() + " " + entityId + " IS " + parent.get(0).getId()
                + ", NOT " + requisitionId);
      }
    }
    return !isNotContained;
  }

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2));
  }

  private <T> void testMove(int id, String type, MoveItemsRequest request, Class<T> targetType,
      Function<T, Identifiable> getRequisition) throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + id + "/" + type + "/move").content(makeJson(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());


    assertTrue(isContained(request.requisitionId(), request.itemIds().get(0), targetType, getRequisition));
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

    Long reqId = 1L;
    Long targetId = 4L;
    RequisitionSupplementalSampleId id = new RequisitionSupplementalSampleId();
    id.setRequisitionId(reqId);
    id.setSample(currentSession().get(SampleImpl.class, targetId));
    Class<RequisitionSupplementalSample> targetClass = RequisitionSupplementalSample.class;


    // the sample should not be a part of any requisition as a supplemental sample at this point
    assertNull(currentSession().get(targetClass, id));



    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + reqId + "/supplementalsamples").content(makeJson(Arrays.asList(targetId)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    RequisitionSupplementalSample sample = currentSession().get(targetClass, id);
    assertNotNull(sample);
    assertEquals(reqId, sample.getRequisitionId());



  }

  @Test
  public void testRemoveSupplementalSamples() throws Exception {
    Long reqId = 2L;
    Long targetId = 502L;

    Class<RequisitionSupplementalSample> targetClass = RequisitionSupplementalSample.class;

    RequisitionSupplementalSampleId id = new RequisitionSupplementalSampleId();
    id.setRequisitionId(reqId);
    id.setSample(currentSession().get(SampleImpl.class, targetId));


    RequisitionSupplementalSample sample = currentSession().get(targetClass, id);
    assertNotNull(sample);
    assertEquals(reqId, sample.getRequisitionId());

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + reqId + "/supplementalsamples/remove")
            .content(makeJson(Arrays.asList(targetId)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    assertNull(currentSession().get(targetClass, id));
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
    Long reqId = 1L;
    Long targetId = 304L;
    Class<RequisitionSupplementalLibrary> targetClass = RequisitionSupplementalLibrary.class;

    RequisitionSupplementalLibraryId id = new RequisitionSupplementalLibraryId();
    id.setRequisitionId(reqId);
    id.setLibrary(currentSession().get(LibraryImpl.class, targetId));


    assertNull(currentSession().get(targetClass, id));

    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/" + reqId + "/supplementallibraries").content(makeJson(Arrays.asList(targetId)))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());



    RequisitionSupplementalLibrary lib = currentSession().get(targetClass, id);
    assertNotNull(lib);
    assertEquals(reqId, lib.getRequisitionId());
  }

  @Test
  public void testRemoveSupplementalLibraries() throws Exception {
    Long reqId = 2L;
    Long targetId = 205L;
    Class<RequisitionSupplementalLibrary> targetClass = RequisitionSupplementalLibrary.class;


    RequisitionSupplementalLibraryId id = new RequisitionSupplementalLibraryId();
    id.setRequisitionId(reqId);
    id.setLibrary(currentSession().get(LibraryImpl.class, targetId));


    RequisitionSupplementalLibrary lib = currentSession().get(targetClass, id);
    assertNotNull(lib);
    assertEquals(reqId, lib.getRequisitionId());

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + reqId + "/supplementallibraries/remove")
            .content(makeJson(Arrays.asList(targetId)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    assertNull(currentSession().get(targetClass, id));

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
    BulkResumeRequest req = new BulkResumeRequest(reqs, resumeDate);

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
