package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import org.codehaus.jackson.annotate.JsonIgnore;
import uk.ac.bbsrc.tgac.miso.core.data.Library;

import java.util.Collection;

/**
 * uk.ac.bbsrc.tgac.miso.core.util.jackson
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 03/01/13
 * @since 0.1.9
 */
public abstract class SampleRecursionAvoidanceMixin {
  @JsonIgnore()
  abstract Collection<Library> getLibraries();
}