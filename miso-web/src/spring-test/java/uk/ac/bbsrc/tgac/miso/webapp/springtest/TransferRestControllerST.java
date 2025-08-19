package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;


import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.*;
import uk.ac.bbsrc.tgac.miso.core.util.TransferType;
import uk.ac.bbsrc.tgac.miso.dto.TransferDto;
import uk.ac.bbsrc.tgac.miso.dto.TransferItemDto;
import uk.ac.bbsrc.tgac.miso.dto.TransferNotificationDto;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
// import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;



public class TransferRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/transfers";
  private static final Class<Transfer> entityClass = Transfer.class;


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testPendingDatatable() throws Exception {
    // pending transfers for the groups that the current user is in
    testDtRequest(CONTROLLER_BASE + "/dt/pending", Arrays.asList(1, 2));
  }

  @Test
  public void testDtByTransferType() throws Exception {
    // transfer types include RECEIPT, INTERNAL, DISTRIBUTION;
    // DISTRIBUTION means that the transfer receipient can't be null
    // INTERNAL means that the transfer's sender group and receipient group can't be null
    // RECEIPT means that the transfer's sender lab can't be null

    testDtRequest(CONTROLLER_BASE + "/dt/" + TransferType.RECEIPT.name(), Arrays.asList(1, 2));

  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // must be group member or admin to create

    TransferDto transfer = new TransferDto();
    transfer.setTransferTime("2025-08-19");

    Transfer saved = baseTestCreate(CONTROLLER_BASE, transfer, entityClass, 200);

    assertEquals(saved.getTransferTime().toString(), transfer.getTransferTime());
  }

  @Test
  public void testCreateFail() throws Exception {
    // must be group member or admin to create

    TransferDto transfer = new TransferDto();
    transfer.setTransferTime("2025-08-19");

    testCreateUnauthorized(CONTROLLER_BASE, transfer, entityClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // must be group member or admin to update

    TransferDto transfer = Dtos.asDto(currentSession().get(entityClass, 1));
    transfer.setRecipient("new guy");


    Transfer updatedTransfer = baseTestUpdate(CONTROLLER_BASE, transfer, 1, entityClass);

    assertEquals(transfer.getRecipient(), updatedTransfer.getRecipient());
  }

  @Test
  public void testUpdateFail() throws Exception {
    // must be group member or admin to update

    TransferDto transfer = Dtos.asDto(currentSession().get(entityClass, 1));
    transfer.setRecipient("new guy");

    testUpdateUnauthorized(CONTROLLER_BASE, transfer, 1, entityClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteTransfer() throws Exception {
    testBulkDelete(entityClass, 2, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 2, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testAddNotification() throws Exception {
    TransferNotificationDto dto = Dtos.asDto(currentSession().get(TransferNotification.class, 3));
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/1/notifications").content(makeJson(dto)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    TransferNotification transferNotif = currentSession().get(TransferNotification.class, 3);
    assertEquals(1L, transferNotif.getTransfer().getId());
  }

  @Test
  public void testAddNotificationFail() throws Exception {
    // must be group member or admin to add notifs
    TransferNotificationDto dto = Dtos.asDto(currentSession().get(TransferNotification.class, 3));
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/1/notifications").content(makeJson(dto)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testResendNotification() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/notifications/1/resend"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkDeleteNotifications() throws Exception {
    List<Long> deleteIds = Arrays.asList(3L, 4L);
    assertEquals(2L, ((TransferNotification) currentSession().get(TransferNotification.class, deleteIds.get(0)))
        .getTransfer().getId());
    assertEquals(2L, ((TransferNotification) currentSession().get(TransferNotification.class, deleteIds.get(1)))
        .getTransfer().getId());

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/2/notifications/bulk-delete").content(makeJson(deleteIds))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    assertNull(currentSession().get(TransferNotification.class, deleteIds.get(0)));
    assertNull(currentSession().get(TransferNotification.class, deleteIds.get(1)));
  }


  @Test
  public void testDeleteNotificationsFail() throws Exception {
    List<Long> deleteIds = Arrays.asList(3L, 4L);
    assertEquals(2L, ((TransferNotification) currentSession().get(TransferNotification.class, deleteIds.get(0)))
        .getTransfer().getId());
    assertEquals(2L, ((TransferNotification) currentSession().get(TransferNotification.class, deleteIds.get(1)))
        .getTransfer().getId());

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/2/notifications/bulk-delete").content(makeJson(deleteIds))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }
}
