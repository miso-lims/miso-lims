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

package uk.ac.bbsrc.tgac.miso.core.service.printing.strategy.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.service.printing.context.impl.BradyFtpPrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.strategy.PrintStrategy;
import uk.ac.bbsrc.tgac.miso.core.util.TransmissionUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.printing.strategy.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 30-Jun-2011
 * @since 0.0.3
 */
public class BradyFtpPrintStrategy implements PrintStrategy<File, BradyFtpPrintContext> {
  protected static final Logger log = LoggerFactory.getLogger(BradyFtpPrintStrategy.class);

  @Override
  public boolean print(File content, BradyFtpPrintContext context) throws IOException {
    if (context.getHost() != null && context.getUsername() != null && context.getPassword() != null) {
      FTPClient ftp = TransmissionUtils.ftpConnect(context.getHost(), context.getUsername(), context.getPassword());
      List<File> files = new ArrayList<File>();
      files.add(content);
      if (TransmissionUtils.ftpPut(ftp, "/execute", files, true, false)) {
        log.info("Printing successful");
        return true;
      } else {
        log.error("Printing unsuccessful");
      }
    } else {
      throw new IOException("Invalid parameters supplied for FTP connection");
    }
    return false;
  }
}
