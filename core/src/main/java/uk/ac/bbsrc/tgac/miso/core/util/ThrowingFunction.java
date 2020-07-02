package uk.ac.bbsrc.tgac.miso.core.util;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

  public R apply(T t) throws E;

}
