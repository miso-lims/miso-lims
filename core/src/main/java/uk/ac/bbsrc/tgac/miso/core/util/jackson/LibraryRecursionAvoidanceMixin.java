package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import org.codehaus.jackson.annotate.JsonIgnore;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;

/**
 * Jackson Mixin class to avoid recursion when grabbing libraries and any parent samples.
 * 
 * @author Rob Davey
 * @date 09/08/13
 * @since 0.2.0-SNAPSHOT
 */
public abstract class LibraryRecursionAvoidanceMixin {
  /**
   * Ignore Library.getSample() method when applying this mixin to a Jackson ObjectMapper
   * 
   * @return Sample
   */
  @JsonIgnore()
  abstract Sample getSample();
}