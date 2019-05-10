package com.eaglegenomics.simlims.core.manager;

public class LocalSecurityManager implements SecurityManager {

  @Override
  public boolean canCreateNewUser() {
    return true;
  }

  @Override
  public boolean isPasswordMutable() {
    return true;
  }
}
