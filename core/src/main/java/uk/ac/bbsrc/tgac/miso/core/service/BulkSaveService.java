package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import org.hibernate.TransactionException;
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

  public static final Thread.UncaughtExceptionHandler EXCEPTION_LOGGER = new Thread.UncaughtExceptionHandler() {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
      if (!(e instanceof BulkValidationException)) {
        LoggerFactory.getLogger(BulkSaveService.class).error("Bulk save failed", e);
      }
    }
  };

  public AuthorizationManager getAuthorizationManager();

  public TransactionTemplate getTransactionTemplate();

  public List<T> listByIdList(List<Long> ids) throws IOException;

  public default BulkSaveOperation<T> startBulkCreate(List<T> items) throws IOException {
    return startBulkOperation(items, this::create);
  }

  public default BulkSaveOperation<T> startBulkUpdate(List<T> items) throws IOException {
    return startBulkOperation(items, this::update);
  }

  public default BulkSaveOperation<T> startBulkOperation(List<T> items, ThrowingFunction<T, Long, IOException> action) throws IOException {
    BulkSaveOperation<T> operation = new BulkSaveOperation<>(items, getAuthorizationManager().getCurrentUser());
    // Authentication is tied to the thread, so use this same auth in the new thread
    Authentication auth = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
    Thread thread = new Thread(() -> {
      SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(auth);
      getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {

        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
          while (!operation.isComplete()) {
            try {
              T item = operation.getNextItem();
              operation.addSuccess(action.apply(item));
            } catch (ValidationException e) {
              operation.addFailure(e);
            } catch (Exception e) {
              operation.setFailed(e);
            }
          }
          if (!operation.isSuccess()) {
            // Need to throw exception to roll back the transaction
            Exception exception = operation.getException();
            if (exception instanceof RuntimeException) {
              throw (RuntimeException) exception;
            } else {
              throw new TransactionException("Transaction failed", operation.getException());
            }
          }
        }
      });

    });
    thread.setUncaughtExceptionHandler(EXCEPTION_LOGGER);
    thread.start();
    return operation;
  }

}
