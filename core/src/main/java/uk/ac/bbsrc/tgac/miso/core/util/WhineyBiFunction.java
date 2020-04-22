package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.util.function.BiFunction;

public interface WhineyBiFunction<T, U, R> {

  public static <T, U, R> BiFunction<T, U, R> rethrow(WhineyBiFunction<T, U, R> function) {
    return (arg1, arg2) -> {
      try {
        return function.apply(arg1, arg2);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    };
  }

  public R apply(T arg1, U arg2) throws IOException;

}
