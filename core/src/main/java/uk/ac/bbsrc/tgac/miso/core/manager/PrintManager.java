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

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * This interface describes a manager that can discover and print to javax.print PrintServices
 * 
 * @author Rob Davey
 * @date 04-May-2011
 * @since 0.0.3
 */
public interface PrintManager<T, C> {
  public Set<Class<? extends Barcodable>> getBarcodableEntities();

  public Collection<PrintContext> getPrintContexts();

  public PrintContext getPrintContext(String contextName);

  public long storePrintService(MisoPrintService service) throws IOException;

  public MisoPrintService getPrintService(long serviceId) throws IOException;

  public MisoPrintService getPrintService(String serviceName) throws IOException;

  public Collection<MisoPrintService> listAllPrintServices() throws IOException;

  public Collection<MisoPrintService> listPrintServicesByBarcodeableClass(Class barcodableClass) throws IOException;

  public PrintJob getPrintJob(long jobId) throws IOException;

  public long storePrintJob(PrintJob job) throws IOException;

  public Collection<? extends PrintJob> listAllPrintJobs() throws IOException;

  public Collection<? extends PrintJob> listPrintJobsByPrintService(T printService) throws IOException;

  public Collection<? extends PrintJob> listPrintJobsByUser(User user) throws IOException;

  public PrintJob print(C content, String printServiceName, User user) throws MisoPrintException;

  public Collection<BarcodableSchema> getBarcodableSchemas();

  public BarcodableSchema getBarcodableSchema(String barcodableStateName) throws IOException;

}
