package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.DuplicateBarcodes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.store.DuplicateBarcodesStore;
import uk.ac.bbsrc.tgac.miso.service.DuplicateBarcodeService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultDuplicateBarcodeService implements DuplicateBarcodeService {

  @Autowired
  private DuplicateBarcodesStore store;
  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Collection<DuplicateBarcodes> getAll() throws IOException {
    List<DuplicateBarcodes> results = new ArrayList<>();
    for (DuplicateBarcodes item : store.listAll()) {
      boolean allow = true;
      for (Boxable duplicate : item.getItems()) {
        SecurableByProfile check;
        if (duplicate instanceof LibraryDilution) {
          check = ((LibraryDilution) duplicate).getLibrary();
        } else {
          check = (SecurableByProfile) duplicate;
        }
        if (!authorizationManager.readCheck(check)) {
          allow = false;
          break;
        }
      }
      if (allow) {
        results.add(item);
      }
    }
    return results;
  }

}
