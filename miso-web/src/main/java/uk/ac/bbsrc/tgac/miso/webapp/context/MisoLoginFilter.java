package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.service.UserService;

public class MisoLoginFilter extends UsernamePasswordAuthenticationFilter {
  
  @Autowired
  private UserService userService;

  @Autowired
  private SecurityManager securityManager;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
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

}
