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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.SessionManagementFilter;

import net.sf.json.JSONObject;

/**
 * A Spring filter that checks whether a session has expired when doing an AJAX request. Usually, the request would just fail, but this
 * class allows a proper response to be generated, and users can be informed/kicked back to the login page.
 * 
 * @author Rob Davey
 * @date 27-Sep-2010
 * @since 0.0.2
 */
public class AjaxSessionFilter extends SessionManagementFilter {
  protected static final Logger log = LoggerFactory.getLogger(AjaxSessionFilter.class);

  static final String FILTER_APPLIED = "__spring_security_session_mgmt_filter_applied";
  private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

  /**
   * Creates a new AjaxSessionFilter instance with a default HttpSessionSecurityContextRepository set
   */
  public AjaxSessionFilter() {
    super(new HttpSessionSecurityContextRepository());
  }

  /**
   * Creates a new AjaxSessionFilter instance with a defined SecurityContextRepository
   * 
   * @param securityContextRepository
   *          of type SecurityContextRepository
   */
  public AjaxSessionFilter(SecurityContextRepository securityContextRepository) {
    super(securityContextRepository);
    this.securityContextRepository = securityContextRepository;
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
   * @throws AuthenticationException
   *           when
   * @throws IOException
   *           when
   * @throws ServletException
   *           when
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws AuthenticationException, IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    if (request.getAttribute(FILTER_APPLIED) != null) {
      chain.doFilter(request, response);
      return;
    }

    request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

    if (!securityContextRepository.containsContext(request)) {
      // if a session has been created for this user instance, and that session is no longer valid, then do this filter
      if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
        log.debug("Session expired - informing client.");
        request.getSession();
        JSONObject jsonObject = JSONObject.fromObject("{'sessiontimeout':'sessiontimeout'}");
        jsonObject.write(res.getWriter());
        return;
      }
      // else just carry on with the filter chain as normal
      else {
        chain.doFilter(req, res);
        return;
      }
    }

    chain.doFilter(req, res);
    return;
  }
}
