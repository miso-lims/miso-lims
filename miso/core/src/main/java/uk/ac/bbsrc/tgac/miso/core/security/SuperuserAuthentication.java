package uk.ac.bbsrc.tgac.miso.core.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.eaglegenomics.simlims.core.User;

/**
 * Custom Authentication that sets the user to attribute migration to and authorizes that user to do everything.
 * This basically disables authorization checks and should only be used for migration and notification processing
 */
public class SuperuserAuthentication implements Authentication {

  private static final long serialVersionUID = 1L;

  private final User user;
  
  /**
   * Creates a SuperAuthentication to perform actions as user
   * 
   * @param user
   */
  public SuperuserAuthentication(User user) {
    this.user = user;
  }

  @Override
  public String getName() {
    return user.getLoginName();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public Object getCredentials() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public Object getDetails() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public Object getPrincipal() {
    return user;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public void setAuthenticated(boolean arg0) throws IllegalArgumentException {
    // do nothing (always authenticated)
  }

}
