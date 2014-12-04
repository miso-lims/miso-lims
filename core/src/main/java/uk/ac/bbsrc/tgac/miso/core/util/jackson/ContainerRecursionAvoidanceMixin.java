package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import org.codehaus.jackson.annotate.JsonIgnore;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;

import java.util.Collection;

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