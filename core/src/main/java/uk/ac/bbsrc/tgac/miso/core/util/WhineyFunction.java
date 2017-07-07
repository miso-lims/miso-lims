package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.util.Collection;

public interface WhineyFunction<T, R> {
  Collection<R> apply(T input) throws IOException;
}
