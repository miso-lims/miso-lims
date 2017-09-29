package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

public interface WhineyFunction<T, R> {
  public static <T, R> Function<T, Stream<R>> flatRethrow(WhineyFunction<? super T, Collection<R>> function) {
    return input -> {
      try {
        return function.apply(input).stream();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  public static <T, R> Function<T, R> rethrow(WhineyFunction<? super T, R> function) {
    return arg -> {
      try {
        return function.apply(arg);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  R apply(T arg) throws IOException;
}