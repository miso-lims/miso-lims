package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.store.PrinterStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.PrinterService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultPrinterService implements PrinterService {

  @Autowired
  private PrinterStore printerStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public long create(Printer printer) throws IOException {
    authorizationManager.throwIfNonAdmin();
    printer.setId(Printer.UNSAVED_ID);
    return printerStore.save(printer);
  }

  @Override
  public Printer get(long serviceId) throws IOException {
    return printerStore.get(serviceId);
  }

  @Override
  public List<Printer> getEnabled() throws IOException {
    List<Printer> enabled = new ArrayList<>();
    for (Printer printer : list(0, 0, true, "id")) {
      if (printer.isEnabled()) {
        enabled.add(printer);
      }
    }
    return enabled;
  }

  @Override
  public void remove(Printer printer) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Printer original = printerStore.get(printer.getId());
    if (original == null) {
      return;
    }
    printerStore.remove(original);
  }

  @Override
  public long update(Printer printer) throws IOException {
    authorizationManager.throwIfNotInternal();
    Printer original = printerStore.get(printer.getId());
    original.setBackend(printer.getBackend());
    original.setConfiguration(printer.getConfiguration());
    original.setDriver(printer.getDriver());
    original.setEnabled(printer.isEnabled());
    original.setName(printer.getName());
    return printerStore.save(original);
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return printerStore.count(errorHandler, filter);
  }

  @Override
  public List<Printer> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    if (authorizationManager.isInternalUser())  {
    return printerStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
    } else {
      return Collections.emptyList();
    }
  }

}
