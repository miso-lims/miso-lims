package uk.ac.bbsrc.tgac.miso.core.util;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Exception> {

  public R apply(T t, U u) throws E;

}
