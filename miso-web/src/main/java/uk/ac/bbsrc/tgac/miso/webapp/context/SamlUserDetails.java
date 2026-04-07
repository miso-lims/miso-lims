package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;

import uk.ac.bbsrc.tgac.miso.core.security.ProvisionedUserDetails;

public class SamlUserDetails implements ProvisionedUserDetails, Saml2AuthenticatedPrincipal {

  private final String username;
  private final String fullName;
  private final String email;
  private final Collection<? extends GrantedAuthority> authorities;
  private final Saml2AuthenticatedPrincipal samlPrincipal;

  public SamlUserDetails(String username, String fullName, String email,
      Collection<? extends GrantedAuthority> authorities, Saml2AuthenticatedPrincipal samlPrincipal) {
    this.username = username;
    this.fullName = fullName;
    this.email = email;
    this.authorities = authorities;
    this.samlPrincipal = samlPrincipal;
  }

  @Override
  public String getFullName() {
    return fullName;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getName() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public <A> List<A> getAttribute(String name) {
    return samlPrincipal.getAttribute(name);
  }

  @Override
  public Map<String, List<Object>> getAttributes() {
    return samlPrincipal.getAttributes();
  }

  @Override
  public <A> A getFirstAttribute(String name) {
    return samlPrincipal.getFirstAttribute(name);
  }

  @Override
  public String getRelyingPartyRegistrationId() {
    return samlPrincipal.getRelyingPartyRegistrationId();
  }

  @Override
  public List<String> getSessionIndexes() {
    return samlPrincipal.getSessionIndexes();
  }

}
