package uk.ac.bbsrc.tgac.miso.runscanner;

import java.util.function.Consumer;
import java.util.stream.Stream;

import io.prometheus.client.Gauge;

/**
 * Count the number of items in a stream and report the result to Prometheus.
 * 
 * This is meant to be used with {@link Stream#peek}
 */
public class StreamCountSpy<T> implements Consumer<T>, AutoCloseable {
  private long count = 0;
  private final Gauge destination;

  public StreamCountSpy(Gauge destination) {
    this.destination = destination;
  }

  @Override
  public void accept(T item) {
    count++;
  }

  @Override
  public void close() throws Exception {
    destination.set(count);
  }

}
