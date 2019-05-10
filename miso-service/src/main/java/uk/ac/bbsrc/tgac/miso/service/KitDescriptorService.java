package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface KitDescriptorService extends PaginatedDataSource<KitDescriptor>, SaveService<KitDescriptor> {

  public Collection<KitDescriptor> list() throws IOException;

  public Collection<KitDescriptor> listByType(KitType kitType) throws IOException;

  public long saveTargetedSequencingRelationships(KitDescriptor kitDescriptor) throws IOException;

  public KitDescriptor getByPartNumber(String partNumber) throws IOException;

}
