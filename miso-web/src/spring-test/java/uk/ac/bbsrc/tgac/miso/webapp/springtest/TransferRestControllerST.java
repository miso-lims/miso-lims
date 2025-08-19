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
import java.util.Transfers;
import java.util.TransferList;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;



public class TransferRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/transfers";
  private static final Class<Transfer> entityClass = Transfer.class;


  @Test
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
    TransferDto transfer = new TransferDto();
    transfer.setTransferTime("2025-08-19");

    Transfer saved = baseTestCreate(CONTROLLER_BASE, transfer, entityClass, 201);

    assertEquals(LimsUtils.getDateFormat().format(saved.getTransferTime()), transfer.getTransferTime());
  }

  @Test
  public void testCreateFail() throws Exception {
    TransferDto transfer = new TransferDto();
    transfer.setTransferTime("2025-08-19");

    testCreateUnauthorized(CONTROLLER_BASE, transfer, entityClass, 201);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    TransferDto transfer = Dtos.asDto(currentSession().get(entityClass, 1));


    Transfer updatedTransfer = baseTestUpdate(CONTROLLER_BASE, transfer, 2, entityClass);
    assertEquals("testing Transfer", updatedTransfer.getAlias());
  }

  @Test
  public void testUpdateFail() throws Exception {

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
  public void testAddNotification() throws Exception {

  }

  @Test
  public void testAddNotificationFail() throws Exception {

  }

  @Test
  public void testResendNotification() throws Exception {

  }

  @Test
  public void testBulkDeleteNotifications() throws Exception {

  }



}
