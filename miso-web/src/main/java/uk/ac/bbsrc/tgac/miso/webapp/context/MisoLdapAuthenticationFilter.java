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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;

/**
 * A Spring filter that checks at a given point in the login filter chain whether an authenticated LDAP user exists in the underlying MISO
 * database.
 * 
 * @author Rob Davey
 * @date 08-Sep-2010
 * @since 0.0.2
 */
public class MisoLdapAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private static final String POST = "POST";

  private SessionAuthenticationStrategy strategy;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  /**
   * Sets the securityManager of this MisoLdapAuthenticationFilter object.
   * 
   * @param securityManager
   *          securityManager.
   */
  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  /**
   * Creates a new MisoLdapAuthenticationFilter instance.
   */
  public MisoLdapAuthenticationFilter() {
    super();
  }

  @Override
  public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy strategy) {
    // forcing this filter to expose the super session auth strategy to this class' doFilter
    // the parent sessionStrategy is private! :(
    this.strategy = strategy;
    super.setSessionAuthenticationStrategy(strategy);
  }

  /**
   * Does the filtering at the given point in the filter chain.
   * 
   * @param req
   *          of type ServletRequest
   * @param res
   *          of type ServletResponse
   * @param chain
   *          of type FilterChain
   * @throws IOException
   *           when
   * @throws ServletException
   *           when
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    final HttpServletRequest request = (HttpServletRequest) req;
    final HttpServletResponse response = (HttpServletResponse) res;

    if (request.getMethod().equals(POST)) {
      if (!requiresAuthentication(request, response)) {
        chain.doFilter(request, response);
        return;
      }

      if (logger.isDebugEnabled()) {
        logger.debug("Request is to process authentication");
      }

      Authentication authResult;

      try {
        authResult = attemptAuthentication(request, response);
        if (authResult == null) {
          // return immediately as subclass has indicated that it hasn't completed authentication
          return;
        }
        strategy.onAuthentication(authResult, request, response);
      } catch (AuthenticationException failed) {
        // Authentication failed
        unsuccessfulAuthentication(request, response, failed);
        return;
      }

      successfulAuthentication(request, response, chain, authResult);

      // this will verify that the LDAP user is mirrored into the LIMS DB
      Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      if (p instanceof InetOrgPerson) {
        // map the LDAP user details to a MISO User
        User u = LimsSecurityUtils.fromLdapUser((InetOrgPerson) p);
        // check if a user exists in the database with this username
        User dbu = securityManager.getUserByLoginName(u.getLoginName());
        if (dbu == null || (dbu != null && !dbu.equals(u))) {
          long userId = securityManager.saveUser(u);
        } else if (dbu != null) {
          // check if the user is same with the ldap user (skipped the password field)
          dbu.setPassword(u.getPassword());
          if (dbu.equals(u)) {
            // save the user with ldap password, this is for when ldap password changed.
            securityManager.saveUser(dbu);
          }
        }
      }
    } else {
      // If it's a GET, we ignore this request and send it
      // to the next filter in the chain. In this case, that
      // pretty much means the request will hit the /login
      // controller which will process the request to show the
      // login page.
      logger.debug("Chaining: " + chain.toString());
      chain.doFilter(request, response);
      return;
    }
  }
}
