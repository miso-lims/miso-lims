package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

public interface WhineyFunction<T, R> {
  public static <T, R> Function<T, Stream<R>> flatLog(Logger log, WhineyFunction<? super T, Collection<R>> function) {
    return input -> {
      try {
        return function.apply(input).stream();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  public static <T, R> Function<T, R> log(Logger logger, WhineyFunction<? super T, R> function) {
    return arg -> {
      try {
        return function.apply(arg);
      } catch (IOException e) {
        logger.error("Ignoring error", e);
        return null;
      }
    };
  }

  R apply(T arg) throws IOException;
}