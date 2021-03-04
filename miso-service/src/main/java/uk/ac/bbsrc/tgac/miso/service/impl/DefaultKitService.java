package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.service.KitService;
import uk.ac.bbsrc.tgac.miso.persistence.KitStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultKitService implements KitService {

  @Autowired
  private KitStore kitStore;

  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
  }

  @Override
  public List<Kit> list() throws IOException {
    return kitStore.listAll();
  }

  @Override
  public long saveKit(Kit kit) throws IOException {
    if (kit.isSaved()) {
      Kit original = get(kit.getId());
      original.setIdentificationBarcode(kit.getIdentificationBarcode());
      original.setKitDate(kit.getKitDate());
      original.setKitDescriptor(kitStore.getKitDescriptorById(kit.getKitDescriptor().getId()));
      original.setLocationBarcode(kit.getLocationBarcode());
      original.setLotNumber(kit.getLotNumber());
      kit = original;
    }
    return kitStore.save(kit);
  }

  @Override
  public Kit get(long kitId) throws IOException {
    return kitStore.get(kitId);
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    return kitStore.getKitByLotNumber(lotNumber);
  }

}
