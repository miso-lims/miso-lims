package uk.ac.bbsrc.tgac.miso.core.service.naming;

import net.sourceforge.fluxion.spi.Spi;
import uk.ac.bbsrc.tgac.miso.core.service.RequestManagerAware;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.naming
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 30/08/12
 * @since 0.1.7
 */
@Spi
public interface RequestManagerAwareNamingScheme<T> extends MisoNamingScheme<T>, RequestManagerAware {
}
