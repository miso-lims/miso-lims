package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.service.UserService;

public class MisoLoginFilter extends UsernamePasswordAuthenticationFilter {

  @Autowired
  private UserService userService;

  @Autowired
  private SecurityManager securityManager;

  @Value("${security.ad.emailDomain:#{null}}")
  private String emailDomain;

  public MisoLoginFilter() {
    // Save security context in HTTP session and request attribute (match Spring Security 6 default)
    super.setSecurityContextRepository(
        new DelegatingSecurityContextRepository(
            new HttpSessionSecurityContextRepository(),
            new RequestAttributeSecurityContextRepository()));
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    Authentication authentication = super.attemptAuthentication(request, response);
    if (authentication == null) {
      return null;
    }
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    try {
      securityManager.syncUser(userDetails);
    } catch (IOException e) {
      throw new InternalAuthenticationServiceException("User sync failed", e);
    }
    User dbu;
    try {
      dbu = userService.getByLoginName(userDetails.getUsername().toLowerCase());
    } catch (IOException e) {
      throw new InternalAuthenticationServiceException("User lookup failed", e);
    }
    if (!dbu.isInternal()) {
      throw new InsufficientAuthenticationException("User is not authorized for MISO login");
    }
    return authentication;
  }

  @Override
  protected String obtainUsername(HttpServletRequest request) {
    String username = super.obtainUsername(request);
    if (username != null && emailDomain != null && username.endsWith("@" + emailDomain)) {
      return username.split("@")[0];
    }
    return username;
  }

}
