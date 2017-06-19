package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface KitService extends PaginatedDataSource<KitDescriptor> {

  public Collection<Kit> listKits() throws IOException;

  public Collection<KitDescriptor> listKitDescriptors() throws IOException;

  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException;

  public void deleteKitNote(Kit kit, Long noteId) throws IOException;

  public void saveKitNote(Kit kit, Note note) throws IOException;

  public long saveKit(Kit kit) throws IOException;

  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException;

  public Kit getKitById(long kitId) throws IOException;

  public Kit getKitByIdentificationBarcode(String barcode) throws IOException;

  public Kit getKitByLotNumber(String lotNumber) throws IOException;

  public KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException;

  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException;

  public Map<String, Integer> getKitDescriptorColumnSizes() throws IOException;

}
