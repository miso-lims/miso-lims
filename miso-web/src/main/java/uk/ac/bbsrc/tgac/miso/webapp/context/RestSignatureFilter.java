/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.integration.util.SignatureHelper;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestExceptionHandler;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestExceptionHandler.RestError;

/**
 * Authentication filter for REST requests. If authentication headers are found in the request, signature authentication will be 
 * attempted. Otherwise, forms authentication will be checked. Lastly, if unauthenticated mode is enabled and neither previous 
 * authentication option has been attempted, the request will be authenticated using the unauthenticated mode user. If authentication 
 * fails or any other exception occurs, the error data is returned in JSON (see {@link RestError}) and the request is blocked.
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
    try {
      if (request.getHeader(SignatureHelper.USER_HEADER) == null) {
        if (checkFormLogin(request, response, filterChain)) {
          // Already logged in via form
          return;
        }
        if (UNAUTHENTICATED_MODE) {
          // No header/form authentication has been attempted
          filterUnauthenticated(request, response, filterChain);
          return;
        }
        throw new RestException("Cannot enact RESTful request without a user specified!", Status.UNAUTHORIZED);
      }
      
      // Attempt authentication via username and signature headers
      checkSignature(request, response, filterChain);
    } catch (Exception e) {
      // Return JSON representation of any errors
      RestError error = RestExceptionHandler.handleException(request, response, e);
      response.setContentType("application/json");
      try (PrintWriter writer = response.getWriter()) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writer().writeValue(writer, error);
      }
    }
  }
  
  /**
   * Checks if the user is already authenticated via form and passes the request/response pair through the filter if so
   * 
   * @param request the request being handled
   * @param response the response being served
   * @param filterChain the filter chain containing this filter, which is being executed
   * @return true if the user is authenticated; false otherwise. If true, the filter has already been passed, and no further 
   * action should be taken.
   * @throws IOException
   * @throws ServletException
   */
  private boolean checkFormLogin(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
      throws IOException, ServletException {
    SecurityContext sc = securityContextRepository.loadContext(new HttpRequestResponseHolder(request, response));
    if (sc != null && sc.getAuthentication() != null) {
      logger.debug("User already logged in - chaining");
      SecurityContextHolder.getContextHolderStrategy().setContext(sc);
      filterChain.doFilter(request, response);
      return true;
    }
    return false;
  }
  
  /**
   * Forces authentication as the unauthenticated mode user and passes the request/response pair through the filter. 
   * No further action should be taken after this method has been called, as the filter has already been passed.
   * 
   * @param request the request being handled
   * @param response the response being served
   * @param filterChain the filter chain containing this filter, which is being executed
   * @throws IOException
   * @throws ServletException
   */
  private void filterUnauthenticated(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("**************************************************************************************\n");
    sb.append("**  DANGER!! REST requests in MISO are currently unauthenticated. This is suitable  **\n");
    sb.append("**  during development only. Adjust setting in RestSignatureHeaderFilter class.     **\n");
    sb.append("**************************************************************************************\n");
    logger.error(sb.toString());

    User userdetails = null;
    com.eaglegenomics.simlims.core.User user;
    user = securityManager.getUserByLoginName(UNAUTHENTICATED_MODE_USER);
    if (user != null) {
      userdetails = LimsSecurityUtils.toUserDetails(user);
    }
    
    filterUser(request, response, filterChain, userdetails);
  }

  /**
   * Attempts authentication via username and signature headers in the request. If successful, passes the request/response pair 
   * through the filter. No further action should be taken after this method has been called, as the filter has already been passed. 
   * An Exception of some sort is thrown otherwise
   * 
   * @param request the request being handled
   * @param response the response being served
   * @param filterChain the filter chain containing this filter, which is being executed
   * @throws RestException if the username or signature header is missing, if the user cannot be found, or if the signature is invalid
   * @throws IOException
   * @throws ServletException
   */
  private void checkSignature(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    logger.debug("HEADERS: ");
    Enumeration es = request.getHeaderNames();
    while (es.hasMoreElements()) {
      String key = (String) es.nextElement();
      logger.info(key + " -> " + request.getHeader(key));
    }
    
    // get login name
    String loginName = request.getHeader(SignatureHelper.USER_HEADER);
    if (loginName == null) {
      throw new RestException("Cannot enact RESTful request without a user specified!", Status.UNAUTHORIZED);
    }
    
    // get signature
    String signature = request.getHeader(SignatureHelper.SIGNATURE_HEADER);
    if (signature == null) {
      throw new RestException("Cannot enact RESTful request without a signature!", Status.UNAUTHORIZED);
    }
    
    // get user and validate signature
    User userdetails = null;
    boolean validSignature = false;
    try {
      com.eaglegenomics.simlims.core.User user = securityManager.getUserByLoginName(loginName);
      if (loginName.equals("notification")) {
        logger.info("Incoming notification request");
        validSignature = SignatureHelper.validateSignature(request, SignatureHelper.PUBLIC_KEY, signature);
      } else {
        logger.debug("Incoming user REST API request");
        if (user != null) {
          validSignature = SignatureHelper.validateSignature(request,
              SignatureHelper.generatePrivateUserKey((user.getLoginName() + "::" + user.getPassword()).getBytes("UTF-8")), signature);
        }
      }
      userdetails = LimsSecurityUtils.toUserDetails(user);
    } catch (InvalidKeyException e) {
      logger.error("filter", e);
    } catch (Exception e) {
      logger.error("filter", e);
    }
    
    if (!validSignature) {
      logger.error("REST KEY INVALID");
      throw new RestException("REST signature failed validation.", Status.UNAUTHORIZED);
    }
    
    logger.debug("REST KEY OK. Security!");
    filterUser(request, response, filterChain, userdetails);
  }
  
  /**
   * Sets the security context's authentication and user details and passes the request/response pair through the filter. No further 
   * action should be taken after this method has been called, as the filter has already been passed.
   * 
   * @param request the request being handled
   * @param response the response being served
   * @param filterChain the filter chain containing this filter, which is being executed
   * @throws RestException if the user cannot be found
   * @throws IOException
   * @throws ServletException
   */
  private void filterUser(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, User userdetails) 
      throws IOException, ServletException {
    if (userdetails != null) {
      PreAuthenticatedAuthenticationToken newAuthentication = new PreAuthenticatedAuthenticationToken(userdetails,
          userdetails.getPassword(), userdetails.getAuthorities());
      newAuthentication.setAuthenticated(true);
      newAuthentication.setDetails(userdetails);
      
      SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().getContext();
      sc.setAuthentication(newAuthentication);
      SecurityContextHolder.getContextHolderStrategy().setContext(sc);
      logger.debug("Set context - chaining");
      
      filterChain.doFilter(request, response);
    } else {
      throw new RestException("No valid user found to authenticate", Status.UNAUTHORIZED);
    }
  }
}
