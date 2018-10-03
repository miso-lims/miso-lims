package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;

public interface AttachmentCategoryStore {

  public AttachmentCategory get(long id) throws IOException;

  public AttachmentCategory getByAlias(String alias) throws IOException;

  public List<AttachmentCategory> list() throws IOException;

  public long save(AttachmentCategory category) throws IOException;

  public long getUsage(AttachmentCategory category);

}
