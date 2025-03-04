package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;
import uk.ac.bbsrc.tgac.miso.core.service.ApiKeyService;

/**
 * This filter should be added before any other authentication filters. It only operates on requests
 * to the API for which API keys are intended and will reject the request if an API key is not
 * provided or is not valid, preventing any other authentication methods.
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final RequestMatcher NEGATED_URI_MATCHER =
      new NegatedRequestMatcher(new AntPathRequestMatcher("/api/**"));
  private static final String HEADER_NAME = "X-API-KEY";

  private ApiKeyService apiKeyService;

  public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String apiKeyString = request.getHeader(HEADER_NAME);
    ApiKey apiKey = apiKeyService.authenticate(apiKeyString);
    if (apiKey == null) {
      // API key Authentication failed
      SecurityContextHolder.getContext().setAuthentication(null);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    } else {
      ApiKeyAuthentication auth = new ApiKeyAuthentication(apiKey);
      SecurityContextHolder.getContext().setAuthentication(auth);
      filterChain.doFilter(request, response);
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return NEGATED_URI_MATCHER.matches(request);
  }

}
