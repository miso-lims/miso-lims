package com.eaglegenomics.simlims.core.manager;

import java.io.IOException;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityManager {

  public boolean isPasswordMutable();

  public boolean canCreateNewUser();

  /**
   * Create/update the user in the MISO database if necessary. Default implementation does nothing
   */
  public default void syncUser(UserDetails userDetails) throws IOException {
    // do nothing
  }

}
