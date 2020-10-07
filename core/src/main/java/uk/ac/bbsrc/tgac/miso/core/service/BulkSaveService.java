package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.exception.BulkValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingFunction;

public interface BulkSaveService<T extends Identifiable> extends SaveService<T> {

  public AuthorizationManager getAuthorizationManager();

  public TransactionTemplate getTransactionTemplate();

  public List<T> listByIdList(List<Long> ids) throws IOException;

  public default BulkSaveOperation<T> startBulkCreate(List<T> items, Consumer<BulkSaveOperation<T>> callback) throws IOException {
    return startBulkOperation(items, this::create, callback);
  }

  public default BulkSaveOperation<T> startBulkUpdate(List<T> items, Consumer<BulkSaveOperation<T>> callback) throws IOException {
    return startBulkOperation(items, this::update, callback);
  }

  public default BulkSaveOperation<T> startBulkOperation(List<T> items, ThrowingFunction<T, Long, IOException> action,
      Consumer<BulkSaveOperation<T>> callback) throws IOException {
    BulkSaveOperation<T> operation = new BulkSaveOperation<>(items, getAuthorizationManager().getCurrentUser());
    // Authentication is tied to the thread, so use this same auth in the new thread
    Authentication auth = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
    Thread thread = new Thread(() -> {
      SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(auth);
      try {
        getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {

          @Override
          protected void doInTransactionWithoutResult(TransactionStatus status) {
            while (operation.hasMore()) {
              try {
                T item = operation.getNextItem();
                operation.addSuccess(action.apply(item));
              } catch (ValidationException e) {
                operation.addFailure(e);
                status.setRollbackOnly();
              } catch (Exception e) {
                operation.setFailed(e);
                status.setRollbackOnly();
              }
            }
          }
        });
      } catch (Exception e) {
        // Exception during transaction commit
        operation.setFailed(e);
      }

      if (operation.isFailed()) {
        Exception exception = operation.getException();
        if (!(exception instanceof BulkValidationException)) {
          LoggerFactory.getLogger(BulkSaveService.class).error("Bulk save failed", exception);
        }
      }
      if (callback != null) {
        try {
          callback.accept(operation);
        } catch (Exception e) {
          // Changes were committed, so not setting failed
          LoggerFactory.getLogger(BulkSaveService.class).error("Exception thrown in bulk save callback", e);
        }
      }
      operation.setComplete();
    });

    thread.start();
    return operation;
  }

}
