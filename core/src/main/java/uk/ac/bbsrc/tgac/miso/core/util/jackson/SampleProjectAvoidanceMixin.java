package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import org.codehaus.jackson.annotate.JsonIgnore;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;

import java.util.Collection;

/**
 * Jackson Mixin class to avoid recursion when grabbing samples and any child libraries.
 *
 * @author Rob Davey
 * @date 03/01/13
 * @since 0.1.9
 */
public abstract class SampleProjectAvoidanceMixin {
  /**
   * Ignore Sample.getProject() method by applying this mixin to a Jackson ObjectMapper
   *
   * @return Project
   */
  @JsonIgnore()
  abstract Project getProject();
}