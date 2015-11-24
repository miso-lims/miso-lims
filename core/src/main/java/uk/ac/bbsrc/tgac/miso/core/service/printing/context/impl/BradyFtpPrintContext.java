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

package uk.ac.bbsrc.tgac.miso.core.service.printing.context.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.strategy.impl.BradyFtpPrintStrategy;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.printing.context.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 30-Jun-2011
 * @since 0.0.3
 */
@ServiceProvider
public class BradyFtpPrintContext implements PrintContext<File> {
  protected static final Logger log = LoggerFactory.getLogger(BradyFtpPrintContext.class);
  private BradyFtpPrintStrategy ps = new BradyFtpPrintStrategy();
  public String host;
  public String username;
  public String password;

  @Override
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPrintStrategy(BradyFtpPrintStrategy ps) {
    this.ps = ps;
  }

  @Override
  public String getName() {
    return "mach4-type-ftp-printer";
  }

  @Override
  public String getDescription() {
    return "Prints to a Mach4-type printer via the FTP protocol";
  }

  @Override
  public boolean print(File content) throws IOException {
    return ps.print(content, this);
  }
}
