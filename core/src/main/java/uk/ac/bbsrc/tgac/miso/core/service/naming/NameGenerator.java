package uk.ac.bbsrc.tgac.miso.core.service.naming;

import net.sourceforge.fluxion.spi.Spi;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.naming
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 27/09/12
 * @since 0.1.8
 */
@Spi
public interface NameGenerator<T> {
  public String getGeneratorName();

  public String generateName(T t);

  public Class<T> nameGeneratorFor();
}
