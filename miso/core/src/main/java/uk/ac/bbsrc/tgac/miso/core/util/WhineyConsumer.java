package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.util.function.Consumer;

public interface WhineyConsumer<T> {

  public static <T> Consumer<T> rethrow(WhineyConsumer<? super T> function) {
    return arg -> {
      try {
        function.accept(arg);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  void accept(T arg) throws IOException;
}