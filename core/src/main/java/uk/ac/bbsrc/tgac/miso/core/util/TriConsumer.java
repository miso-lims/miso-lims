package uk.ac.bbsrc.tgac.miso.core.util;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

  public void accept(A a, B b, C c);

}
