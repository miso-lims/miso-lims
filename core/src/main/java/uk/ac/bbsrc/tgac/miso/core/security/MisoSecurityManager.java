package uk.ac.bbsrc.tgac.miso.core.security;

import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;

/**
 * uk.ac.bbsrc.tgac.miso.core.security
 * <p/>
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 07/02/14
 * @since version
 */
public interface MisoSecurityManager extends com.eaglegenomics.simlims.core.manager.SecurityManager {
  void changePassword(final String oldPass, final String newPass);
}
