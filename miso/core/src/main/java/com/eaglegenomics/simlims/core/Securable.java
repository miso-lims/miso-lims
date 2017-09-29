package com.eaglegenomics.simlims.core;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p/>
 * Indicates that at least some portions of the class implementing this
 * interface require user credential checking.
 *
 * @author Richard Holland
 * @since 0.0.1
 */
public interface Securable {
  /**
   * Check to see if the specified user has read permissions on this object.
   */
  public boolean userCanRead(User user);

  /**
   * Check to see if the specified user has write permissions on this object.
   */
  public boolean userCanWrite(User user);
}
