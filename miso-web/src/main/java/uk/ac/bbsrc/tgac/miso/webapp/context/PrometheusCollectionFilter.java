package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

/**
 * Collect per-URL performance statistics and export them to Prometheus
 */
public class PrometheusCollectionFilter extends GenericFilterBean {
  private final static Counter hits = Counter.build().name("tomcat_servlet_request").labelNames("uri", "method", "code")
      .help("The number of requests for this endpoint.").register();
  private final static Counter throwCount = Counter.build().name("tomcat_servlet_throws").labelNames("uri", "method")
      .help("The number of exceptions thrown from this endpoint.").register();
  private final static Histogram times = Histogram.build().name("tomcat_servlet_process_time").labelNames("uri", "method", "code")
      .help("The time, in nano seconds, this endpoint takes to process a request.").exponentialBuckets(1000, 10, 9).register();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    String method = "UNKNOWN";
    String uri = "unknown";
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      method = httpRequest.getMethod();
      uri = httpRequest.getRequestURI().replaceAll("[0-9,]", "");
      // Fluxion URLs are useless, so get the target service instead
      if (uri.endsWith("fluxion.ajax")) {
        uri = String.format("fluxion:%s:%s", httpRequest.getParameter("servicename"), httpRequest.getParameter("action"));
      }
    }
    long start = System.nanoTime();
    try {
      chain.doFilter(request, response);
    } catch (IOException | ServletException e) {
      throwCount.labels(uri, method).inc();
      throw e;
    } finally {
      long duration = System.nanoTime() - start;
      String status = Integer.toString(response instanceof HttpServletResponse ? ((HttpServletResponse) response).getStatus() : 0);
      times.labels(uri, method, status).observe(duration);
      hits.labels(uri, method, status).inc();
    }
  }

}
