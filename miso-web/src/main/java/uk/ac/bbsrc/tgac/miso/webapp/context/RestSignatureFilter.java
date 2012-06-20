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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
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

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    //String url = SignatureHelper.createSortedUrl(request);
    String url = request.getHeader(SignatureHelper.URL_X_HEADER);

    String signature = request.getHeader(SignatureHelper.SIGNATURE_HEADER);
    if (signature == null) {
      signature = request.getHeader(SignatureHelper.SIGNATURE_X_HEADER);
    }

    String apiKey = request.getHeader(SignatureHelper.APIKEY_HEADER);
    if (apiKey == null) {
      apiKey = request.getHeader(SignatureHelper.APIKEY_X_HEADER);
    }

    try {
      if (!SignatureHelper.validateSignature(url, signature, apiKey)) {
        logger.info("REST KEY INVALID");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "REST signature failed validation.");
      }
    } catch (Exception e) {
      logger.info("UNABLE TO UNDERTAKE SIGNATURE VALIDATION");
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The REST Security Server experienced an internal error.");
    }

    logger.info("REST KEY OK. Security!");

    User user = new User("notification", "none", true, true, true, true, AuthorityUtils.createAuthorityList("ROLE_INTERNAL"));
    PreAuthenticatedAuthenticationToken newAuthentication = new PreAuthenticatedAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    newAuthentication.setAuthenticated(true);
    newAuthentication.setDetails(user);

    try {
      SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().getContext();
      sc.setAuthentication(newAuthentication);
      SecurityContextHolder.getContextHolderStrategy().setContext(sc);
      logger.info("Set context - chaining");
    }
    catch (AuthenticationException a) {
      a.printStackTrace();
    }
    filterChain.doFilter(request, response);
  }
}
