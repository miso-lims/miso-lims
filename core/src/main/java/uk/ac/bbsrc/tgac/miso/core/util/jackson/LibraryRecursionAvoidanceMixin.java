package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import org.codehaus.jackson.annotate.JsonIgnore;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;

/**
 * uk.ac.bbsrc.tgac.miso.core.util.jackson
 *
 * @author Rob Davey
 * @date 09/08/13
 * @since 0.2.0-SNAPSHOT
 */
public abstract class LibraryRecursionAvoidanceMixin {
  @JsonIgnore()
  abstract Sample getSample();
}