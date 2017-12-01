package uk.ac.bbsrc.tgac.miso.core.security;

import org.springframework.security.core.GrantedAuthority;

public enum MisoAuthority implements GrantedAuthority {
  /** Administrator */
  ROLE_ADMIN,
  /** Regular User */
  ROLE_INTERNAL,
  /** External collaborator */
  ROLE_EXTERNAL;

  @Override
  public String getAuthority() {
    return name();
  }
}