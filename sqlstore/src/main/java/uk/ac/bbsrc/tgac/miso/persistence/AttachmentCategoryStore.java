package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;

public interface AttachmentCategoryStore extends BulkSaveDao<AttachmentCategory> {

  AttachmentCategory getByAlias(String alias) throws IOException;

  long getUsage(AttachmentCategory category);

}
