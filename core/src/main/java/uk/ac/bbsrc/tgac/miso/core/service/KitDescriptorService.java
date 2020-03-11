package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface KitDescriptorService extends DeleterService<KitDescriptor>, ListService<KitDescriptor>, PaginatedDataSource<KitDescriptor>,
    SaveService<KitDescriptor> {

  public Collection<KitDescriptor> listByType(KitType kitType) throws IOException;

  public long saveTargetedSequencingRelationships(KitDescriptor kitDescriptor) throws IOException;

  public KitDescriptor getByName(String name) throws IOException;

  public KitDescriptor getByPartNumber(String partNumber) throws IOException;

  public List<KitDescriptor> search(KitType type, String search) throws IOException;

}
