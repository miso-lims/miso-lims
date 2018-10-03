package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
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
import uk.ac.bbsrc.tgac.miso.core.store.AttachmentCategoryStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateAttachmentCategoryDao implements AttachmentCategoryStore {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public AttachmentCategory get(long id) throws IOException {
    return (AttachmentCategory) currentSession().get(AttachmentCategory.class, id);
  }

  @Override
  public AttachmentCategory getByAlias(String alias) throws IOException {
    return (AttachmentCategory) currentSession().createCriteria(AttachmentCategory.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public List<AttachmentCategory> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<AttachmentCategory> list = currentSession().createCriteria(AttachmentCategory.class).list();
    return list;
  }

  @Override
  public long save(AttachmentCategory category) throws IOException {
    if (category.isSaved()) {
      currentSession().update(category);
      return category.getId();
    } else {
      return (long) currentSession().save(category);
    }
  }

  @Override
  public long getUsage(AttachmentCategory category) {
    return (long) currentSession().createCriteria(FileAttachment.class)
        .add(Restrictions.eq("category", category))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
