/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.manager.AbstractSecurityManager;

/**
 * Extension of the basic LocalSecurityManager, this class adds the ability to save users initially authorised by LDAP into the MISO DB so
 * that the security information can be utilised by the SecurityProfile system
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Transactional(rollbackFor = Exception.class)
public class LDAPSecurityManager extends AbstractSecurityManager {
  /** Field log */
  protected static final Logger log = LoggerFactory.getLogger(LDAPSecurityManager.class);

  @Override
  public boolean canCreateNewUser() {
    return false;
  }

  @Override
  public boolean isPasswordMutable() {
    return false;
  }
}
