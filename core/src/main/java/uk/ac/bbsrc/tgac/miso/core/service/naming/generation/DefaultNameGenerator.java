package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultMisoEntityPrefix;

public class DefaultNameGenerator implements NameGenerator<Nameable> {

  @Override
  public String generate(Nameable nameable) throws MisoNamingException {
    DefaultMisoEntityPrefix selected = null;
    for (DefaultMisoEntityPrefix prefix : DefaultMisoEntityPrefix.values()) {
      if (prefix.getClass().isAssignableFrom(nameable.getClass())) {
        if (selected != null) {
          throw new MisoNamingException("Multiple prefixes are available for object of type: " + nameable.getClass().getSimpleName());
        }
        selected = prefix;
      }
    }
    if (selected == null) {
      throw new MisoNamingException("Cannot generate a MISO name from an object of type: " + nameable.getClass().getSimpleName());
    }
    return selected.name() + nameable.getId();
  }
}
