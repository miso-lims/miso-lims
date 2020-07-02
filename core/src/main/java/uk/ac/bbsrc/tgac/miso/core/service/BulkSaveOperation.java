package uk.ac.bbsrc.tgac.miso.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.service.exception.BulkValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;

public class BulkSaveOperation<T extends Identifiable> {

  private final List<T> pendingItems;
  private final Class<T> itemClass;
  private final User owner;
  private final int totalCount;
  private int progress = 0;
  private final List<Long> savedIds = new ArrayList<>();
  private final Map<Integer, List<ValidationError>> errorsByRow = new HashMap<>();
  private Exception failureException;
  private LocalDateTime completionTime = null;

  private boolean awaitingItemResult = false;

  public BulkSaveOperation(List<T> pendingItems, User owner) {
    this.pendingItems = pendingItems;
    @SuppressWarnings("unchecked")
    Class<T> clazz = (Class<T>) pendingItems.get(0).getClass();
    this.itemClass = clazz;
    this.owner = owner;
    this.totalCount = pendingItems.size();
  }

  public Class<T> getItemClass() {
    return itemClass;
  }

  public User getOwner() {
    return owner;
  }

  public synchronized T getNextItem() {
    if (awaitingItemResult) {
      throw new IllegalStateException("Success or failure of previous item must be recorded");
    }
    awaitingItemResult = true;
    return pendingItems.remove(0);
  }

  public synchronized void addSuccess(long savedId) {
    assertAwaitingResult();
    savedIds.add(savedId);
    progress++;
    maybeSetCompletionTime();
    awaitingItemResult = false;
  }

  public synchronized void addFailure(ValidationException e) {
    assertAwaitingResult();
    errorsByRow.put(progress, e.getErrors());
    progress++;
    maybeSetCompletionTime();
    awaitingItemResult = false;
  }

  public synchronized void setFailed(Exception e) {
    assertAwaitingResult();
    failureException = e;
    progress = totalCount;
    maybeSetCompletionTime();
    awaitingItemResult = false;
  }

  public synchronized boolean isComplete() {
    return progress == totalCount;
  }

  public synchronized LocalDateTime getCompletionTime() {
    return completionTime;
  }

  public synchronized int getTotalCount() {
    return totalCount;
  }

  public synchronized int getProgress() {
    return progress;
  }

  public synchronized boolean isSuccess() {
    assertComplete();
    return failureException == null && errorsByRow.isEmpty();
  }

  public synchronized List<Long> getSavedIds() {
    assertComplete();
    return savedIds;
  }

  public synchronized Exception getException() {
    assertComplete();
    if (failureException != null) {
      return failureException;
    } else if (!errorsByRow.isEmpty()) {
      return new BulkValidationException(errorsByRow);
    } else {
      throw new IllegalStateException("Operation has not failed");
    }
  }

  private synchronized void assertAwaitingResult() {
    if (!awaitingItemResult) {
      throw new IllegalStateException("Success or failure of current item already recorded");
    }
  }

  private synchronized void assertComplete() {
    if (!isComplete()) {
      throw new IllegalStateException("Operation has not completed");
    }
  }

  private void maybeSetCompletionTime() {
    if (isComplete()) {
      completionTime = LocalDateTime.now();
    }
  }

}
