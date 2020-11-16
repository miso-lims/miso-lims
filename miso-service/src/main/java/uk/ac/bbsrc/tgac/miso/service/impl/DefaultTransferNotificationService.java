package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.TransferNotificationService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.TransferNotificationStore;
import uk.ac.bbsrc.tgac.miso.persistence.TransferStore;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultTransferNotificationService extends AbstractSaveService<TransferNotification> implements TransferNotificationService {

  @Autowired
  private TransferNotificationStore transferNotificationStore;
  @Autowired
  private TransferStore transferStore;
  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SaveDao<TransferNotification> getDao() {
    return transferNotificationStore;
  }

  @Override
  protected void collectValidationErrors(TransferNotification object, TransferNotification beforeChange, List<ValidationError> errors)
      throws IOException {
    if (beforeChange == null) {
      List<TransferNotification> others = transferNotificationStore.listByTransfer(object.getTransfer());
      TransferNotification other = others.stream()
          .filter(x -> x.getRecipientEmail().equals(object.getRecipientEmail()) && x.getSendSuccess() == null)
          .findFirst().orElse(null);
      if (other != null) {
        errors.add(new ValidationError("recipientEmail", "A notification is already being sent to this address"));
      }
    }
  }

  @Override
  protected void applyChanges(TransferNotification to, TransferNotification from) throws IOException {
    to.setSentTime(from.getSentTime());
    to.setSendSuccess(from.getSendSuccess());
    to.setFailureSentTime(from.getFailureSentTime());
  }

  @Override
  protected void beforeSave(TransferNotification object) throws IOException {
    if (!object.isSaved()) {
      object.setCreator(authorizationManager.getCurrentUser());
      object.setCreated(new Date());
    }
  }

  @Override
  public List<TransferNotification> listByTransferId(long transferId) throws IOException {
    Transfer transfer = transferStore.get(transferId);
    return transferNotificationStore.listByTransfer(transfer);
  }

  @Override
  public List<TransferNotification> listPending(int holdTimeMinutes, int limit) throws IOException {
    return transferNotificationStore.listPending(holdTimeMinutes, limit);
  }

  @Override
  public List<TransferNotification> listFailurePending(int limit) throws IOException {
    return transferNotificationStore.listFailurePending(limit);
  }

  @Override
  public void bulkDelete(Collection<TransferNotification> notifications, boolean force) throws IOException {
    for (TransferNotification notification : notifications) {
      TransferNotification managed = transferNotificationStore.get(notification.getId());
      authorizationManager.throwIfNonAdminOrMatchingOwner(managed.getCreator());
      if (!force && managed.getSentTime() != null) {
        throw new ValidationException(new ValidationError("Cannot delete a notification after it has already been sent"));
      }
      transferNotificationStore.delete(managed);
    }
  }

}
