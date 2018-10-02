package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;

public interface AttachmentCategoryService extends DeleterService<AttachmentCategory> {

  public List<AttachmentCategory> list() throws IOException;

  public long save(AttachmentCategory category) throws IOException;

}
