package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification;

public interface TransferNotificationStore extends SaveDao<TransferNotification> {

  public List<TransferNotification> listByTransfer(Transfer transfer) throws IOException;

  public List<TransferNotification> listPending(int holdTimeMinutes, int limit) throws IOException;

  public List<TransferNotification> listFailurePending(int limit) throws IOException;

  public void delete(TransferNotification notification) throws IOException;

}
