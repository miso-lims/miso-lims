package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;

public interface AttachmentCategoryService extends BulkSaveService<AttachmentCategory>,
    DeleterService<AttachmentCategory>, ListService<AttachmentCategory> {

}
