package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment_;
import uk.ac.bbsrc.tgac.miso.persistence.AttachmentCategoryStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateAttachmentCategoryDao extends HibernateSaveDao<AttachmentCategory>
    implements AttachmentCategoryStore {

  public HibernateAttachmentCategoryDao() {
    super(AttachmentCategory.class);
  }

  @Override
  public AttachmentCategory getByAlias(String alias) throws IOException {
    return getBy(AttachmentCategory_.ALIAS, alias);
  }

  @Override
  public long getUsage(AttachmentCategory category) {
    return getUsageBy(FileAttachment.class, FileAttachment_.CATEGORY, category);
  }

  @Override
  public List<AttachmentCategory> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(AttachmentCategory_.CATEGORY_ID, idList);
  }
}
