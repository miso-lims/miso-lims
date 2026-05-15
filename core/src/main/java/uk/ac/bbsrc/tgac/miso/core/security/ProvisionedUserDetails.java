package uk.ac.bbsrc.tgac.miso.core.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface ProvisionedUserDetails extends UserDetails {

  String getFullName();

  String getEmail();

}
