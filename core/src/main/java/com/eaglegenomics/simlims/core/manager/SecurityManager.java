package com.eaglegenomics.simlims.core.manager;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p/>
 * The manager handles security features such as locating and logging in users.
 * It is backed by a SecurityStore, although that is implementation-specific and
 * is not part of the interface.
 * <p/>
 * All methods throw IOException because they may have recourse to backing
 * stores on disk or databases.
 *
 * @author Richard Holland
 * @since 0.0.1
 */
public interface SecurityManager {

  public boolean isPasswordMutable();

  public boolean canCreateNewUser();

}
