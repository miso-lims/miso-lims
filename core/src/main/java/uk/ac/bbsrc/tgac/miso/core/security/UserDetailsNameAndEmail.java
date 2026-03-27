package uk.ac.bbsrc.tgac.miso.core.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsNameAndEmail extends UserDetails {

  String getFullName();

  String getEmail();

}
