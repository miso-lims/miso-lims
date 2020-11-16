package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification;

public class HibernateTransferNotificationDaoIT
    extends AbstractHibernateSaveDaoTest<TransferNotification, HibernateTransferNotificationDao> {

  public HibernateTransferNotificationDaoIT() {
    super(TransferNotification.class, 1L, 4);
  }

  @Override
  public HibernateTransferNotificationDao constructTestSubject() {
    HibernateTransferNotificationDao sut = new HibernateTransferNotificationDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public TransferNotification getCreateItem() {
    TransferNotification notification = new TransferNotification();
    notification.setTransfer((Transfer) currentSession().get(Transfer.class, 1L));
    notification.setRecipientName("You");
    notification.setRecipientEmail("you@example.com");
    notification.setCreator((User) currentSession().get(UserImpl.class, 1L));
    notification.setCreated(new Date());
    return notification;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<TransferNotification, String> getUpdateParams() {
    return new UpdateParameters<>(2L, TransferNotification::getRecipientName, TransferNotification::setRecipientName, "someone");
  }

  @Test
  public void testListByTransfer() throws Exception {
    Transfer transfer = (Transfer) currentSession().get(Transfer.class, 1L);
    List<TransferNotification> results = getTestSubject().listByTransfer(transfer);
    assertEquals(2, results.size());
    for (TransferNotification notification : results) {
      assertEquals(1L, notification.getTransfer().getId());
    }
  }

  @Test
  public void testListPending() throws Exception {
    List<TransferNotification> results = getTestSubject().listPending(0, 10);
    assertEquals(1, results.size());
    assertNull(results.get(0).getSentTime());
    assertNull(results.get(0).getSendSuccess());
  }

  @Test
  public void testListFailurePending() throws Exception {
    List<TransferNotification> results = getTestSubject().listFailurePending(10);
    assertEquals(1, results.size());
    TransferNotification notification = results.get(0);
    assertNotNull(notification.getSentTime());
    assertFalse(notification.getSendSuccess());
    assertNull(notification.getFailureSentTime());
  }

  @Test
  public void testDelete() throws Exception {
    TransferNotification notification = (TransferNotification) currentSession().get(TransferNotification.class, 4L);
    assertNotNull(notification);
    getTestSubject().delete(notification);
    clearSession();
    assertNull(currentSession().get(TransferNotification.class, 4L));
  }

}
