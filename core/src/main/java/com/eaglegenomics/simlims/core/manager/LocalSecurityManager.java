package com.eaglegenomics.simlims.core.manager;

import org.springframework.transaction.annotation.Transactional;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p/>
 * Basic implementation using local stores. More complex implementations may
 * choose to use web services to communicate with a remote store, or to combine
 * multiple stores.
 *
 * @author Richard Holland
 * @since 0.0.1
 */
@Transactional(rollbackFor = Exception.class)
public class LocalSecurityManager extends AbstractSecurityManager {

  @Override
  public boolean canCreateNewUser() {
    return true;
  }

  @Override
  public boolean isPasswordMutable() {
    return true;
  }
}
