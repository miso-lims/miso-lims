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

package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MisoPrintJob;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.PrintContextResolverService;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * Manages specified MisoPrintServices and allows construction of a print job that will be persisted on print()
 * 
 * @author Rob Davey
 * @date 30-Jun-2011
 * @since 0.0.3
 */
public class BarcodePrintManager extends AbstractPrintManager<Queue<File>> {
  protected static final Logger log = LoggerFactory.getLogger(BarcodePrintManager.class);

  public BarcodePrintManager(PrintContextResolverService pcrs) {
    setPrintContextResolverService(pcrs);
  }

  @Override
  public PrintJob print(Queue<File> barcodesToPrint, String printServiceName, User user) throws MisoPrintException {
    try {
      MisoPrintService mps = getPrintService(printServiceName);
      if (mps != null) {
        MisoPrintJob job = new MisoPrintJob();
        job.setPrintDate(new Date());
        job.setPrintService(mps);
        job.setPrintUser(user);
        job.setQueuedElements(barcodesToPrint);
        job.setStatus("QUEUED");
        try {
          long jobId = storePrintJob(job);
          job.setJobId(jobId);
        } catch (IOException e) {
          log.error("Could not store print job", e);
        }

        try {
          boolean jobOK = true;
          for (File barcodeFile : barcodesToPrint) {
            if (!mps.print(barcodeFile)) {
              jobOK = false;
            }
          }

          if (jobOK) {
            job.setStatus("OK");
          } else {
            job.setStatus("FAIL");
          }

          storePrintJob(job);
        } catch (IOException e) {
          log.error("Could not store print barcodes", e);
          throw new MisoPrintException("Could not print barcodes to " + printServiceName + ": " + e.getMessage(), e);
        }
        return job;
      } else {
        throw new MisoPrintException("No such PrintService: " + printServiceName);
      }
    } catch (IOException e) {
      log.error("Could not store print barcodes", e);
      throw new MisoPrintException("Cannot retrieve PrintService: " + printServiceName);
    }
  }
}
