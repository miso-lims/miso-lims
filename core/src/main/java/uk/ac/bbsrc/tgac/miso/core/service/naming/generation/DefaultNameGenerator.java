package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import org.apache.log4j.Logger;

import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultMisoEntityPrefix;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class DefaultNameGenerator implements NameGenerator<Nameable> {

  private static final Logger log = Logger.getLogger(DefaultNameGenerator.class);

  @Override
  public String generate(Nameable nameable) throws MisoNamingException {
    if (DefaultMisoEntityPrefix.get(nameable.getClass().getSimpleName()) == null) {
      for (Class<?> i : LimsUtils.getAllInterfaces(nameable.getClass())) {
        if (DefaultMisoEntityPrefix.get(i.getSimpleName()) != null) {
          log.info("Generating name based on interface :: " + DefaultMisoEntityPrefix.get(i.getSimpleName()).name() + nameable.getId());
          return DefaultMisoEntityPrefix.get(i.getSimpleName()).name() + nameable.getId();
        }
      }
      throw new MisoNamingException("Cannot generate a MISO name from an object of type: " + nameable.getClass().getSimpleName());
    }
    log.info("Generating name :: " + DefaultMisoEntityPrefix.get(nameable.getClass().getSimpleName()).name() + nameable.getId());
    return DefaultMisoEntityPrefix.get(nameable.getClass().getSimpleName()).name() + nameable.getId();
  }

}
