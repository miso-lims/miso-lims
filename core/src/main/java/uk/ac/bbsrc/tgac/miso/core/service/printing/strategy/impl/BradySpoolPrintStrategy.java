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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.SimpleDoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.service.printing.context.impl.BradySpoolPrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.strategy.PrintStrategy;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.printing.strategy.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 30-Jun-2011
 * @since 0.0.3
 */
public class BradySpoolPrintStrategy implements PrintStrategy<String, BradySpoolPrintContext> {
  protected static final Logger log = LoggerFactory.getLogger(BradyFtpPrintStrategy.class);

  @Override
  public boolean print(String content, BradySpoolPrintContext context) throws IOException {
    try {
      byte[] printdata = content.getBytes("US-ASCII");
      DocFlavor flavor = new DocFlavor("application/vnd.cups-raster", "[B");
      if (context != null) {
        DocPrintJob pjob = context.getPrintService().createPrintJob();
        Doc doc = new SimpleDoc(printdata, flavor, null);
        pjob.print(doc, null);
        return true;
      }
    } catch (UnsupportedEncodingException e) {
      log.error("print", e);
    } catch (PrintException e) {
      log.error("print", e);
    }
    return false;
  }
}
