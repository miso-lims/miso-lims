package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.util.function.Consumer;

import org.slf4j.Logger;

public interface WhineyConsumer<T> {

  public static <T> Consumer<T> log(Logger logger, WhineyConsumer<? super T> function) {
    return arg -> {
      try {
        function.accept(arg);
      } catch (IOException e) {
        logger.error("Ignoring error", e);
      }
    };
  }

  void accept(T arg) throws IOException;
}