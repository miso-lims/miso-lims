package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Set;

/**
 * uk.ac.bbsrc.tgac.miso.core.data
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 22/10/13
 * @since 0.2.1-SNAPSHOT
 */
public interface SampleGroup<T extends Nameable> extends Nameable {
  public T getParent();

  public void setParent(T overview);

  public Set<Sample> getSamples();

  public void setSamples(Set<Sample> samples);

  public void addSample(Sample sample);
}