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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.SessionManagementFilter;

import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.ProjectAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;

/**
 * A Spring filter that checks whether a session has expired when doing an AJAX request. Usually, the request would just fail, but this
 * class allows a proper response to be generated, and users can be informed/kicked back to the login page.
 * 
 * @author Rob Davey
 * @date 27-Sep-2010
 * @since 0.0.2
 */
public class ExposeRequestUrlFilter extends SessionManagementFilter {
  protected static final Logger log = LoggerFactory.getLogger(ExposeRequestUrlFilter.class);

  static final String FILTER_APPLIED = "__miso_expose_request_url_filter_applied";

  @Autowired
  private ApplicationContextProvider applicationContextProvider;

  public void setApplicationContextProvider(ApplicationContextProvider applicationContextProvider) {
    this.applicationContextProvider = applicationContextProvider;
  }

  public ExposeRequestUrlFilter() {
    super(new HttpSessionSecurityContextRepository());
  }

  public ExposeRequestUrlFilter(SecurityContextRepository securityContextRepository) {
    super(securityContextRepository);
  }

  /**
   * Does the filtering at the given point in the filter chain.
   * 
   * @param request
   *          of type ServletRequest
   * @param response
   *          of type ServletResponse
   * @param chain
   *          of type FilterChain
   * @throws org.springframework.security.core.AuthenticationException
   *           when
   * @throws java.io.IOException
   *           when
   * @throws javax.servlet.ServletException
   *           when
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;

    String url = req.getRequestURL().toString();
    String baseURL = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/miso";

    if (req.getSession(false) != null) {
      if (req.getSession(false).getAttribute(FILTER_APPLIED) != null) {
        chain.doFilter(req, response);
        return;
      }

      // TODO - this whole process isn't great
      // Basically, because it's hard to get the FQDN without an initial request, and then setting it in any beans
      // is a manual process
      applicationContextProvider.setBaseUrl(baseURL);

      AutowireCapableBeanFactory bf = ApplicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory();

      RunAlertManager ram = (RunAlertManager) bf.getBean("runAlertManager");
      ram.getRunListener().setBaseURL(applicationContextProvider.getBaseUrl());

      ProjectAlertManager pam = (ProjectAlertManager) bf.getBean("projectAlertManager");
      pam.getProjectListener().setBaseURL(applicationContextProvider.getBaseUrl());
      pam.getProjectOverviewListener().setBaseURL(applicationContextProvider.getBaseUrl());

      PoolAlertManager poam = (PoolAlertManager) bf.getBean("poolAlertManager");
      poam.getPoolListener().setBaseURL(applicationContextProvider.getBaseUrl());

      req.getSession(false).setAttribute(FILTER_APPLIED, baseURL);

      log.info("Set context provider base url to: " + applicationContextProvider.getBaseUrl());

      chain.doFilter(req, response);
      return;
    } else {
      chain.doFilter(req, response);
      return;
    }
  }
}