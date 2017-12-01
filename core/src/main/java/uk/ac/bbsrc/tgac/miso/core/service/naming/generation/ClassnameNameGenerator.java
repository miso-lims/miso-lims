package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public class ClassnameNameGenerator implements NameGenerator<Nameable> {

  @Override
  public String generate(Nameable object) throws MisoNamingException {
    return object.getClass().getSimpleName() + object.getId();
  }

}
