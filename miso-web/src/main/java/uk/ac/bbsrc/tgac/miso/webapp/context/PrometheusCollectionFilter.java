package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

/**
 * Collect per-URL performance statistics and export them to Prometheus
 */
public class PrometheusCollectionFilter extends GenericFilterBean {

  // Wrapper for the servlet response in order to collect the status code
  private static class ServletResponseWrapper implements HttpServletResponse {
    private final HttpServletResponse backingResponse;
    private int status = SC_OK;

    public ServletResponseWrapper(HttpServletResponse httpResponse) {
      backingResponse = httpResponse;
    }

    @Override
    public void addCookie(Cookie arg0) {
      backingResponse.addCookie(arg0);
    }

    @Override
    public void addDateHeader(String arg0, long arg1) {
      backingResponse.addDateHeader(arg0, arg1);
    }

    @Override
    public void addHeader(String arg0, String arg1) {
      backingResponse.addHeader(arg0, arg1);
    }

    @Override
    public void addIntHeader(String arg0, int arg1) {
      backingResponse.addIntHeader(arg0, arg1);
    }

    @Override
    public boolean containsHeader(String arg0) {
      return backingResponse.containsHeader(arg0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String encodeRedirectUrl(String arg0) {
      return backingResponse.encodeRedirectUrl(arg0);
    }

    @Override
    public String encodeRedirectURL(String arg0) {
      return backingResponse.encodeRedirectURL(arg0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String encodeUrl(String arg0) {
      return backingResponse.encodeUrl(arg0);
    }

    @Override
    public String encodeURL(String arg0) {
      return backingResponse.encodeURL(arg0);
    }

    @Override
    public void flushBuffer() throws IOException {
      backingResponse.flushBuffer();
    }

    @Override
    public int getBufferSize() {
      return backingResponse.getBufferSize();
    }

    @Override
    public String getCharacterEncoding() {
      return backingResponse.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
      return backingResponse.getContentType();
    }

    @Override
    public Locale getLocale() {
      return backingResponse.getLocale();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      return backingResponse.getOutputStream();
    }

    public int getStatusCode() {
      return status;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
      return backingResponse.getWriter();
    }

    @Override
    public boolean isCommitted() {
      return backingResponse.isCommitted();
    }

    @Override
    public void reset() {
      backingResponse.reset();
    }

    @Override
    public void resetBuffer() {
      backingResponse.resetBuffer();
    }

    @Override
    public void sendError(int arg0) throws IOException {
      status = arg0;
      backingResponse.sendError(arg0);
    }

    @Override
    public void sendError(int arg0, String arg1) throws IOException {
      backingResponse.sendError(arg0, arg1);
    }

    @Override
    @CoverageIgnore
    public void sendRedirect(String arg0) throws IOException {
      status = SC_MOVED_TEMPORARILY;
      backingResponse.sendRedirect(arg0);
    }

    @Override
    public void setBufferSize(int arg0) {
      backingResponse.setBufferSize(arg0);
    }

    @Override
    public void setCharacterEncoding(String arg0) {
      backingResponse.setCharacterEncoding(arg0);
    }

    @Override
    public void setContentLength(int arg0) {
      backingResponse.setContentLength(arg0);
    }

    @Override
    public void setContentType(String arg0) {
      backingResponse.setContentType(arg0);
    }

    @Override
    public void setDateHeader(String arg0, long arg1) {
      backingResponse.setDateHeader(arg0, arg1);
    }

    @Override
    public void setHeader(String arg0, String arg1) {
      backingResponse.setHeader(arg0, arg1);
    }

    @Override
    public void setIntHeader(String arg0, int arg1) {
      backingResponse.setIntHeader(arg0, arg1);
    }

    @Override
    public void setLocale(Locale arg0) {
      backingResponse.setLocale(arg0);
    }

    @Override
    @CoverageIgnore
    public void setStatus(int arg0) {
      status = arg0;
      backingResponse.setStatus(arg0);
    }

    @Override
    @CoverageIgnore
    public void setStatus(int arg0, String arg1) {
      status = arg0;
      backingResponse.setStatus(arg0);
    }

    @Override
    public void setContentLengthLong(long arg0) {
      backingResponse.setContentLengthLong(arg0);

    }

    @Override
    public String getHeader(String arg0) {
      return backingResponse.getHeader(arg0);
    }

    @Override
    public Collection<String> getHeaderNames() {
      return backingResponse.getHeaderNames();
    }

    @Override
    public Collection<String> getHeaders(String arg0) {
      return backingResponse.getHeaders(arg0);
    }

    @Override
    public int getStatus() {
      return backingResponse.getStatus();
    }

  }

  private final static Counter hits = Counter.build().name("tomcat_servlet_request").labelNames("uri", "method", "code")
      .help("The number of requests for this endpoint.").register();
  private final static Counter throwCount = Counter.build().name("tomcat_servlet_throws").labelNames("uri", "method")
      .help("The number of exceptions thrown from this endpoint.").register();
  private final static Histogram times = Histogram.build().name("tomcat_servlet_process_time").labelNames("uri", "method", "code")
      .help("The time, in nano seconds, this endpoint takes to process a request.").exponentialBuckets(1000, 10, 7).register();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    String method = "UNKNOWN";
    String uri = "unknown";
    ServletResponseWrapper proxy = response instanceof HttpServletResponse ? new ServletResponseWrapper(((HttpServletResponse) response))
        : null;
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
      chain.doFilter(request, proxy == null ? response : (ServletResponse) proxy);
    } catch (IOException | ServletException e) {
      throwCount.labels(uri, method).inc();
      throw e;
    } finally {
      long duration = System.nanoTime() - start;
      String status = Integer.toString(proxy == null ? 0 : proxy.getStatusCode());
      times.labels(uri, method, status).observe(duration);
      hits.labels(uri, method, status).inc();
    }
  }

}
