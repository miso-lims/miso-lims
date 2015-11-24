package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnore;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;

/**
 * Jackson Mixin class to avoid recursion when grabbing samples and any child libraries.
 * 
 * @author Rob Davey
 * @date 03/01/13
 * @since 0.1.9
 */
public abstract class ProjectSampleRecursionAvoidanceMixin {
  /**
   * Ignore Sample.getLibraries() method by applying this mixin to a Jackson ObjectMapper
   * 
   * @return Sample
   */
  @JsonIgnore()
  abstract Collection<Sample> getSamples();
}