package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface KitDescriptorStore extends Store<KitDescriptor> {
  KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException;

  KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException;

  List<KitDescriptor> listKitDescriptorsByManufacturer(String manufacturer) throws IOException;

  List<KitDescriptor> listKitDescriptorsByPlatform(PlatformType platformType) throws IOException;

  List<KitDescriptor> listKitDescriptorsByUnits(String units) throws IOException;

  List<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException;

  Map<String, Integer> getColumnSizes() throws IOException;

}