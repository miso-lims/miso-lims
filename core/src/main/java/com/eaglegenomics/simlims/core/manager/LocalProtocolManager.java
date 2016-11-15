package com.eaglegenomics.simlims.core.manager;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eaglegenomics.simlims.core.Activity;
import com.eaglegenomics.simlims.core.ActivityData;
import com.eaglegenomics.simlims.core.Protocol;
import com.eaglegenomics.simlims.core.Request;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.exception.InvalidProtocolException;
import com.eaglegenomics.simlims.core.store.ProtocolStore;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Basic implementation using local stores. More complex implementations may
 * choose to use web services to communicate with a remote store, or to combine
 * multiple stores.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public class LocalProtocolManager extends AbstractProtocolManager {
  private static final Log log = LogFactory.getLog(LocalProtocolManager.class);

  private ProtocolStore protocolStore;

  /**
   * The store is required. There is no default.
   */
  public void setProtocolStore(ProtocolStore protocolStore) {
    this.protocolStore = protocolStore;
  }

  @Override
  public void validateProtocol(Protocol protocol)
      throws InvalidProtocolException {
    if (log.isDebugEnabled()) {
      log.debug("Validating protocol: " + protocol);
    }
    // At least one activity?
    if (protocol.getActivityAliasMap().size() < 1) {
      throw new InvalidProtocolException(
          "Protocol must contain at least 1 activity.", protocol);
    }
    // Has a valid startpoint?
    if (!protocol.getActivityAliasMap().containsKey(
        protocol.getStartpoint())) {
      throw new InvalidProtocolException("Start point '"
          + protocol.getStartpoint() + "' does not exist.", protocol);
    }
    // Has at least one endpoint?
    if (protocol.getEndpoints().size() < 1) {
      throw new InvalidProtocolException(
          "Protocol must contain at least 1 endpoint.", protocol);
    }
    // Are all endpoints valid?
    for (String endpoint : protocol.getEndpoints()) {
      if (!protocol.getActivityAliasMap().containsKey(endpoint)) {
        throw new InvalidProtocolException("End point '" + endpoint
            + "' does not exist.", protocol);
      }
    }
    // Are all activities valid?
    for (Map.Entry<String, String[]> entry : protocol.getActivityFlowMap()
        .entrySet()) {
      String fromKey = entry.getKey();
      if (!protocol.getActivityAliasMap().containsKey(fromKey)) {
        throw new InvalidProtocolException("Activity alias '" + fromKey
            + "' does not exist.", protocol);
      }
      Activity from = protocol.getActivityAliasMap().get(fromKey);
      for (String toKey : entry.getValue()) {
        if (!protocol.getActivityAliasMap().containsKey(toKey)) {
          throw new InvalidProtocolException("Activity alias '"
              + toKey + "' does not exist.", protocol);
        }
        Activity to = protocol.getActivityAliasMap().get(toKey);
        if (!from.getOutputDataClass().equals(to.getInputDataClass())) {
          throw new InvalidProtocolException(
              "Input/output class types differ when mapping '"
                  + fromKey + "' to '" + toKey + "'",
              protocol);
        }
      }
    }
  }

  @Override
  public Collection<ActivityData> getLockableInputData(User user,
      Activity activity) throws IOException {
    return protocolStore.getLockableInputData(user, activity);
  }

  @Override
  public Collection<ActivityData> lockInputData(User user,
      Collection<ActivityData> inputData) throws SecurityException,
      IOException {
    Collection<ActivityData> lockedInput = new HashSet<>();
    for (ActivityData input : inputData) {
      if (log.isDebugEnabled()) {
        log.debug("Locking: " + input.getUniqueId() + " for "
            + user.getLoginName());
      }
      if (protocolStore.lockInputData(user, input)) {
        lockedInput.add(input);
        if (log.isDebugEnabled()) {
          log.debug("Locked: " + input.getUniqueId() + " for "
              + user.getLoginName());
        }
      } else {
        if (log.isDebugEnabled()) {
          log.debug("Did not lock: " + input.getUniqueId() + " for "
              + user.getLoginName());
        }
      }
    }
    return lockedInput;
  }

  @Override
  public Collection<ActivityData> unlockInputData(User user,
      Collection<ActivityData> inputData) throws SecurityException,
      IOException {
    Collection<ActivityData> unlockedInput = new HashSet<>();
    for (ActivityData input : inputData) {
      if (log.isDebugEnabled()) {
        log.debug("Unlocking: " + input.getUniqueId() + " for "
            + user.getLoginName());
      }
      protocolStore.unlockInputData(user, input);
      unlockedInput.add(input);
      if (log.isDebugEnabled()) {
        log.debug("Unlocked: " + input.getUniqueId() + " for "
            + user.getLoginName());
      }
    }
    return unlockedInput;
  }

  @Override
  public void saveFailedData(User user,
      Map<ActivityData, Throwable> failedData) throws SecurityException,
      IOException {
    for (Map.Entry<ActivityData, Throwable> entry : failedData.entrySet()) {
      ActivityData input = entry.getKey();
      Throwable error = entry.getValue();
      if (log.isDebugEnabled()) {
        log.debug("Saving failed data: " + input.getUniqueId());
      }
      protocolStore.saveFailedData(user, input, error);
      if (log.isDebugEnabled()) {
        log.debug("Saved failed data: " + input.getUniqueId());
      }
    }
  }

  @Override
  public void saveOutputData(
      User user,
      Map<Map.Entry<String, ActivityData>, Collection<Map.Entry<String, ActivityData>>> outputData)
      throws SecurityException, IOException {
    Collection<ActivityData> inputDataEntities = new HashSet<>();
    Collection<ActivityData> outputDataEntities = new HashSet<>();
    for (Map.Entry<Map.Entry<String, ActivityData>, Collection<Map.Entry<String, ActivityData>>> entry : outputData
        .entrySet()) {
      inputDataEntities.add(entry.getKey().getValue());
      for (Map.Entry<String, ActivityData> outputDataEntry : entry
          .getValue()) {
        outputDataEntities.add(outputDataEntry.getValue());
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("Saving output data.");
    }
    protocolStore.saveOutputData(user, inputDataEntities,
        outputDataEntities, outputData);
    if (log.isDebugEnabled()) {
      log.debug("Saved output data.");
    }
  }

  @Override
  public void saveSkippedData(User user, Collection<ActivityData> skippedData)
      throws SecurityException, IOException {
    for (ActivityData input : skippedData) {
      if (log.isDebugEnabled()) {
        log.debug("Saving skipped data: " + input.getUniqueId());
      }
      protocolStore.saveSkippedData(user, input);
      if (log.isDebugEnabled()) {
        log.debug("Saved skipped data: " + input.getUniqueId());
      }
    }
  }

  @Override
  public boolean validateInputDataLocks(User user,
      Collection<ActivityData> inputData) throws IOException {
    for (ActivityData input : inputData) {
      if (log.isDebugEnabled()) {
        log.debug("Checking lock on input " + input.getUniqueId()
            + " for " + user.getLoginName());
      }
      if (!protocolStore.validateInputDataLock(user, input)) {
        return false;
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("All locks still held by " + user.getLoginName());
    }
    return true;
  }

  @Override
  public Collection<ActivityData> getRequestResults(User user,
      Request request, int executionCount) throws SecurityException,
      IOException {
    if (log.isDebugEnabled()) {
      log.debug("Getting results for request " + request + " execution "
          + executionCount + " for " + user.getLoginName());
    }
    return protocolStore.getRequestResults(user, request, executionCount);
  }

  @Override
  public boolean isCurrentExecutionComplete(User user, Request request)
      throws SecurityException, IOException {
    if (log.isDebugEnabled()) {
      log
          .debug("Checking current execution completion status for request "
              + request + " for " + user.getLoginName());
    }
    return protocolStore.isCurrentExecutionComplete(user, request);
  }

  @Override
  public void setupInputData(User user, Collection<ActivityData> inputData)
      throws IOException, SecurityException {
    for (ActivityData input : inputData) {
      if (log.isDebugEnabled()) {
        log.debug("Setting up input data " + input.getUniqueId()
            + " for " + user.getLoginName());
      }
      // We do the security check here because the store doesn't.
      if (!input.userCanWrite(user)) {
        throw new SecurityException();
      }
      protocolStore.queueNewInputData(input);
      if (log.isDebugEnabled()) {
        log.debug("Done setting up input data " + input.getUniqueId()
            + " for " + user.getLoginName());
      }
    }
  }

  @Override
  public Collection<Activity> listAllAccessibleActivities(User user) throws IOException {
    Collection<Activity> accessibleActivities = new HashSet<>();
    for (Activity activity : listAllActivities()) {
      if (activity.userCanWrite(user)) {
        accessibleActivities.add(activity);
      }
    }
    return accessibleActivities;
  }
}