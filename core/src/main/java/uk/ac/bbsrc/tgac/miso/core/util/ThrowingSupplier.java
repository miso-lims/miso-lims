package uk.ac.bbsrc.tgac.miso.core.util;

public interface ThrowingSupplier<T, E extends Exception> {

  T get() throws E;
  
}
