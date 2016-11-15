package com.eaglegenomics.simlims.core.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eaglegenomics.simlims.core.Activity;
import com.eaglegenomics.simlims.core.Protocol;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Concrete implementation of in-memory protocol and activity caches, otherwise
 * all other methods remain abstract and unimplemented.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public abstract class AbstractProtocolManager implements ProtocolManager {
  protected static final Log log = LogFactory
      .getLog(AbstractProtocolManager.class);

  private transient Map<String, Protocol> protocolCache = new HashMap<>();
  private transient Map<String, Activity> activityCache = new HashMap<>();

  @Override
  public void cacheProtocol(Protocol protocol) {
    if (log.isDebugEnabled()) {
      log.debug("Caching protocol " + protocol);
    }
    protocolCache.put(protocol.getUniqueIdentifier(), protocol);
    for (Activity activity : protocol.getActivityAliasMap().values()) {
      if (log.isDebugEnabled()) {
        log.debug("Caching activity " + activity);
      }
      activityCache.put(activity.getUniqueIdentifier(), activity);
    }
  }

  @Override
  public Collection<Protocol> listAllProtocols() {
    return protocolCache.values();
  }

  @Override
  public Collection<Activity> listAllActivities() {
    return activityCache.values();
  }

  @Override
  public Protocol getProtocol(String protocolUniqueIdentifier) {
    return protocolCache.get(protocolUniqueIdentifier);
  }

  @Override
  public Activity getActivity(String activityUniqueIdentifier) {
    return activityCache.get(activityUniqueIdentifier);
  }
}