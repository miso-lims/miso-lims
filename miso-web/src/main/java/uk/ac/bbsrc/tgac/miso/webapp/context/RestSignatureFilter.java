/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.integration.util.SignatureHelper;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.context
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 09/02/12
 * @since 0.1.6
 */

public class RestSignatureFilter extends OncePerRequestFilter {
  @Autowired
  AuthenticationManager authenticationManager;
  @Autowired
  SecurityManager securityManager;

  /** Used during development only. Set this to true to use REST resources without authentication. Good for manual testing/exploration. */
  private static boolean UNAUTHENTICATED_MODE = false;
  /** Resources created (POST) and modified (PUT) will be associated with this user in UNAUTHENTICATED_MODE. This user must exist. */
  private static String UNAUTHENTICATED_MODE_USER = "admin";

  private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

  /**
   * Creates a new RestSignatureFilter instance with a default HttpSessionSecurityContextRepository set
   */
  public RestSignatureFilter() {
    super();
  }

  /**
   * Creates a new RestSignatureFilter instance with a defined SecurityContextRepository
   * 
   * @param securityContextRepository
   *          of type SecurityContextRepository
   */
  public RestSignatureFilter(SecurityContextRepository securityContextRepository) {
    super();
    this.securityContextRepository = securityContextRepository;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    User userdetails = null;
    com.eaglegenomics.simlims.core.User user = null;
    logger.debug("HEADERS: ");
    Enumeration es = request.getHeaderNames();
    while (es.hasMoreElements()) {
      String key = (String) es.nextElement();
      logger.info(key + " -> " + request.getHeader(key));
    }

    if (UNAUTHENTICATED_MODE) {
      doFilterUnauthenticatedMode(request, response, filterChain);
    } else {

      String loginName = request.getHeader(SignatureHelper.USER_HEADER);
      if (loginName == null) {
        SecurityContext sc = securityContextRepository.loadContext(new HttpRequestResponseHolder(request, response));
        if (sc != null && sc.getAuthentication() != null) {
          logger.debug("User already logged in - chaining");
          SecurityContextHolder.getContextHolderStrategy().setContext(sc);
          filterChain.doFilter(request, response);
        }
        throw new BadCredentialsException("Cannot enact RESTful request without a user specified!");
      }

      if (loginName.equals("notification")) {
        logger.info("Incoming notification request");
        userdetails = new User("notification", "none", true, true, true, true, AuthorityUtils.createAuthorityList("ROLE_INTERNAL"));
      } else {
        logger.debug("Incoming user REST API request");
        user = securityManager.getUserByLoginName(loginName);
        if (user != null) {
          userdetails = LimsSecurityUtils.toUserDetails(user);
        }
      }

      String signature = request.getHeader(SignatureHelper.SIGNATURE_HEADER);
      if (signature == null) {
        throw new BadCredentialsException("Cannot enact RESTful request without a signature!");
      }

      boolean validSignature = false;
      try {
        if (loginName.equals("notification")) {
          validSignature = SignatureHelper.validateSignature(request, SignatureHelper.PUBLIC_KEY, signature);
        } else {
          validSignature = SignatureHelper.validateSignature(request,
              SignatureHelper.generatePrivateUserKey((user.getLoginName() + "::" + user.getPassword()).getBytes("UTF-8")), signature);
        }
      } catch (InvalidKeyException e) {
        logger.error("filter", e);
      } catch (Exception e) {
        logger.error("filter", e);
      }

      try {
        if (!validSignature) {
          logger.error("REST KEY INVALID");
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "REST signature failed validation.");
        }
      } catch (Exception e) {
        logger.error("UNABLE TO UNDERTAKE SIGNATURE VALIDATION", e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The REST Security Server experienced an internal error.");
      }

      logger.debug("REST KEY OK. Security!");

      if (userdetails != null) {
        PreAuthenticatedAuthenticationToken newAuthentication = new PreAuthenticatedAuthenticationToken(userdetails,
            userdetails.getPassword(), userdetails.getAuthorities());
        newAuthentication.setAuthenticated(true);
        newAuthentication.setDetails(userdetails);

        try {
          SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().getContext();
          sc.setAuthentication(newAuthentication);
          SecurityContextHolder.getContextHolderStrategy().setContext(sc);
          logger.debug("Set context - chaining");
        } catch (AuthenticationException a) {
          logger.error("filter", a);
        }
        filterChain.doFilter(request, response);
      } else {
        throw new AuthenticationCredentialsNotFoundException("No valid user found to authenticate");
      }
    }
  }

  private void doFilterUnauthenticatedMode(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    StringBuilder sb = new StringBuilder();
    sb.append("**************************************************************************************\n");
    sb.append("**  DANGER!! REST requests in MISO are currently unauthenticated. This is suitable  **\n");
    sb.append("**  during development only. Adjust this setting in the RestSignatureFilter class.  **\n");
    sb.append("**************************************************************************************\n");
    logger.error(sb.toString());

    User userdetails = null;
    com.eaglegenomics.simlims.core.User user;
    user = securityManager.getUserByLoginName(UNAUTHENTICATED_MODE_USER);
    if (user != null) {
      userdetails = LimsSecurityUtils.toUserDetails(user);
    }

    if (userdetails != null) {
      PreAuthenticatedAuthenticationToken newAuthentication = new PreAuthenticatedAuthenticationToken(userdetails,
          userdetails.getPassword(), userdetails.getAuthorities());
      newAuthentication.setAuthenticated(true);
      newAuthentication.setDetails(userdetails);

      try {
        SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().getContext();
        sc.setAuthentication(newAuthentication);
        SecurityContextHolder.getContextHolderStrategy().setContext(sc);
        logger.debug("Set context - chaining");
      } catch (AuthenticationException a) {
        logger.error("filter", a);
      }
      filterChain.doFilter(request, response);
    } else {
      throw new AuthenticationCredentialsNotFoundException("No valid user found to authenticate");
    }
  }
}
