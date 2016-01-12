/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.service.printing.factory;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.opensymphony.util.FileUtils;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeLabelFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.printing.factory
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 17/04/12
 * @since 0.1.6
 */
@ServiceProvider
public class FileGeneratingBarcodeLabelFactory<T> implements BarcodeLabelFactory<File, T, BarcodableSchema<File, T>> {
  protected static final Logger log = LoggerFactory.getLogger(FileGeneratingBarcodeLabelFactory.class);
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;
  private MisoFilesManager misoFileManager;

  @Override
  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Override
  public void setFilesManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  @Override
  public File getLabel(BarcodableSchema<File, T> s, T b) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String labelScript = s.getRawState(b);

      File f = misoFileManager.generateTemporaryFile(user.getLoginName() + "_" + b.getClass().getSimpleName().toLowerCase() + "-",
          ".printjob");
      FileUtils.write(f, labelScript);

      return f;
    } catch (IOException e) {
      log.error("get label", e);
    }
    return null;
  }
}
