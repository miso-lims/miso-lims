package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification;

public interface TransferNotificationService extends SaveService<TransferNotification> {

  public List<TransferNotification> listByTransferId(long transferId) throws IOException;

  public List<TransferNotification> listPending(int holdTimeMinutes, int limit) throws IOException;

  public List<TransferNotification> listFailurePending(int limit) throws IOException;

  public void bulkDelete(Collection<TransferNotification> notifications, boolean force) throws IOException;

}
