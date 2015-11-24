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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.data.PrintableBarcode;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.service.printing.BarcodableSchemaResolverService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.PrintContextResolverService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;
import uk.ac.bbsrc.tgac.miso.core.store.PrintJobStore;
import uk.ac.bbsrc.tgac.miso.core.store.PrintServiceStore;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 04-May-2011
 * @since 0.0.3
 */
public abstract class AbstractPrintManager<C> implements PrintManager<MisoPrintService, C> {
  protected static final Logger log = LoggerFactory.getLogger(AbstractPrintManager.class);

  private Reflections reflections = new Reflections("uk.ac.bbsrc.tgac.miso.core.data");

  @Autowired
  private PrintContextResolverService printContextResolverService;
  @Autowired
  private BarcodableSchemaResolverService barcodableSchemaResolverService;

  private Map<String, Class<? extends Barcodable>> barcodableMap;

  @Autowired
  private PrintServiceStore printServiceStore;
  @Autowired
  private PrintJobStore printJobStore;

  public void setPrintServiceStore(PrintServiceStore printServiceStore) {
    this.printServiceStore = printServiceStore;
  }

  public void setPrintJobStore(PrintJobStore printJobStore) {
    this.printJobStore = printJobStore;
  }

  public void setPrintContextResolverService(PrintContextResolverService printContextResolverService) {
    this.printContextResolverService = printContextResolverService;
  }

  public void setBarcodableSchemaResolverService(BarcodableSchemaResolverService barcodableSchemaResolverService) {
    this.barcodableSchemaResolverService = barcodableSchemaResolverService;
  }

  @Override
  public Collection<PrintContext> getPrintContexts() {
    return printContextResolverService.getPrintContexts();
  }

  @Override
  public PrintContext getPrintContext(String name) {
    return printContextResolverService.getPrintContext(name);
  }

  @Override
  public Collection<BarcodableSchema> getBarcodableSchemas() {
    return barcodableSchemaResolverService.getBarcodableSchemas();
  }

  @Override
  public BarcodableSchema getBarcodableSchema(String barcodableStateName) {
    return barcodableSchemaResolverService.getBarcodableSchema(barcodableStateName);
  }

  @Override
  public abstract PrintJob print(C content, String printServiceName, User user) throws MisoPrintException;

  @Override
  public Set<Class<? extends Barcodable>> getBarcodableEntities() {
    if (barcodableMap == null) {
      Set<Class<?>> subTypes = reflections.getTypesAnnotatedWith(PrintableBarcode.class);
      barcodableMap = new HashMap<String, Class<? extends Barcodable>>();
      for (Class c : subTypes) {
        if (!barcodableMap.containsKey(c.getSimpleName())) {
          barcodableMap.put(c.getSimpleName(), c);
        } else {
          if (barcodableMap.get(c.getSimpleName()) != c) {
            String msg = "Multiple different Barcodables with the same name " + "('" + c.getName()
                + "') are present on the classpath. Barcodable names must be unique.";
            throw new ServiceConfigurationError(msg);
          }
        }
      }
    }
    return new HashSet<Class<? extends Barcodable>>(barcodableMap.values());
  }

  @Override
  public long storePrintService(MisoPrintService service) throws IOException {
    if (printServiceStore != null) {
      return printServiceStore.save(service);
    } else {
      throw new IOException("No printServiceStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }

  @Override
  public MisoPrintService getPrintService(long serviceId) throws IOException {
    if (printServiceStore != null) {
      return printServiceStore.get(serviceId);
    } else {
      throw new IOException("No printServiceStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }

  @Override
  public MisoPrintService getPrintService(String serviceName) throws IOException {
    if (printServiceStore != null) {
      return printServiceStore.getByName(serviceName);
    } else {
      throw new IOException("No printServiceStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }

  @Override
  public Collection<MisoPrintService> listAllPrintServices() throws IOException {
    if (printServiceStore != null) {
      return printServiceStore.listAll();
    } else {
      throw new IOException("No printServiceStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }

  @Override
  public Collection<MisoPrintService> listPrintServicesByBarcodeableClass(Class barcodableClass) throws IOException {
    if (printServiceStore != null) {
      Collection<MisoPrintService> services = listAllPrintServices();
      Set<MisoPrintService> matchedServices = new HashSet<MisoPrintService>();
      for (MisoPrintService mps : services) {
        if (mps.getPrintServiceFor().equals(barcodableClass)) {
          log.info(
              "Matched service for " + barcodableClass.getName() + ": " + mps.getName() + " (" + mps.getPrintServiceFor().getName() + ")");
          matchedServices.add(mps);
        }
      }
      return matchedServices;
    } else {
      throw new IOException("No printServiceStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }

  @Override
  public long storePrintJob(PrintJob job) throws IOException {
    if (printJobStore != null) {
      return printJobStore.save(job);
    } else {
      throw new IOException("No printJobStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }

  @Override
  public PrintJob getPrintJob(long jobId) throws IOException {
    if (printJobStore != null) {
      return printJobStore.get(jobId);
    } else {
      throw new IOException("No printJobStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }

  @Override
  public Collection<PrintJob> listAllPrintJobs() throws IOException {
    if (printJobStore != null) {
      return printJobStore.listAll();
    } else {
      throw new IOException("No printJobStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }

  @Override
  public Collection<PrintJob> listPrintJobsByUser(User user) throws IOException {
    if (printJobStore != null) {
      return printJobStore.listByUser(user);
    } else {
      throw new IOException("No printJobStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }

  @Override
  public Collection<PrintJob> listPrintJobsByPrintService(MisoPrintService printService) throws IOException {
    if (printJobStore != null) {
      return printJobStore.listByPrintService(printService);
    } else {
      throw new IOException("No printJobStore defined. Ensure one is declared in the Spring XML, or set manually");
    }
  }
}
