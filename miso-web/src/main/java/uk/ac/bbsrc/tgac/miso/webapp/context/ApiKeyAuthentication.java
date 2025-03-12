package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {

  public static final String AUTHORITY = "ROLE_APIUSER";

  private final ApiKey apiKey;

  public ApiKeyAuthentication(ApiKey apiKey) {
    super(makeAuthorities());
    this.apiKey = apiKey;
  }

  @Override
  public Object getCredentials() {
    return apiKey.getSecret();
  }

  @Override
  public Object getPrincipal() {
    return apiKey.getUser().getLoginName();
  }

  private static List<GrantedAuthority> makeAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AUTHORITY));
    return authorities;
  }

}
