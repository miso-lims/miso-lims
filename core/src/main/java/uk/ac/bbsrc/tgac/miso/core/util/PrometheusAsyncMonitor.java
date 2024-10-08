package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;

import io.prometheus.metrics.core.metrics.Histogram;

public class PrometheusAsyncMonitor {

  private static final Histogram times = Histogram.builder().name("miso_async_save_time")
      .labelNames("javaclass", "method", "success")
      .help("The time, in milliseconds, this method takes to run.")
      .classicUpperBounds(100, 500, 1000, 2000, 3000, 5000, 8000, 15000, 30000)
      .register();

  private PrometheusAsyncMonitor() {
    throw new IllegalStateException("Static util class not intended for instantiation");
  }

  public static <T, R> R monitor(String className, String methodName, ThrowingFunction<T, R, IOException> method,
      T object) throws IOException {
    long startTime = System.currentTimeMillis();
    boolean success = true;
    try {
      return method.apply(object);
    } catch (IOException | RuntimeException e) {
      success = false;
      throw e;
    } finally {
      long duration = System.currentTimeMillis() - startTime;
      times.labelValues(className, methodName, Boolean.toString(success)).observe(duration);
    }
  }

}
