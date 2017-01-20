package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.store.PrinterStore;
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
  public Collection<Printer> getAll() throws IOException {
    return printerStore.listAll();
  }

  @Override
  public List<Printer> getEnabled() throws IOException {
    List<Printer> enabled = new ArrayList<>();
    for (Printer printer : getAll()) {
      if (printer.isEnabled()) {
        enabled.add(printer);
      }
    }
    return enabled;
  }

  @Override
  public void remove(Printer printer) throws IOException {
    Printer original = printerStore.get(printer.getId());
    if (original == null) {
      return;
    }
    printerStore.remove(printer);
  }

  @Override
  public long update(Printer printer) throws IOException {
    Printer original = printerStore.get(printer.getId());
    original.setBackend(printer.getBackend());
    original.setConfiguration(printer.getConfiguration());
    original.setDriver(printer.getDriver());
    original.setEnabled(printer.isEnabled());
    original.setName(printer.getName());
    return printerStore.save(printer);
  }

}
