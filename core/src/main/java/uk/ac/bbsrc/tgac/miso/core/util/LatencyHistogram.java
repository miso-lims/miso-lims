package uk.ac.bbsrc.tgac.miso.core.util;

import io.prometheus.metrics.core.metrics.Histogram;

public class LatencyHistogram {

  private final Histogram histogram;

  public LatencyHistogram(String name, String help, String... labels) {
    histogram = Histogram.builder().classicUpperBounds(1, 5, 10, 30, 60, 300, 600, 3600)
        .name(name).help(help).labelNames(labels).register();
  }

  public AutoCloseable start(final String... labels) {
    long startTime = System.nanoTime();

    return () -> histogram.labelValues(labels).observe((System.nanoTime() - startTime) / 1e9);
  }

}
