package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnore;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;

/**
 * Jackson Mixin class to avoid recursion when grabbing samples and any child libraries.
 * 
 * @author Xingdong Bian
 * @date 03/12/14
 * @since 0.2.2
 */
public abstract class ContainerRecursionAvoidanceMixin {
  /**
   * Ignore Sample.getLibraries() method by applying this mixin to a Jackson ObjectMapper
   * 
   * @return Sample
   */
  @JsonIgnore()
  abstract Collection<Partition> getPartitions();
}