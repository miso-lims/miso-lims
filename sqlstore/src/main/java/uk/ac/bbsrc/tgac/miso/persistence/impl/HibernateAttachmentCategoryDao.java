package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
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
    return (AttachmentCategory) currentSession().createCriteria(AttachmentCategory.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public long getUsage(AttachmentCategory category) {
    return (long) currentSession().createCriteria(FileAttachment.class)
        .add(Restrictions.eq("category", category))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public List<AttachmentCategory> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("categoryId", idList);
  }
}
