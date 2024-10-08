package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.AttachmentUsage;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.AttachmentUsage_;
import uk.ac.bbsrc.tgac.miso.persistence.AttachableStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateAttachableDao implements AttachableStore {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public Attachable getManaged(Attachable object) {
    return (Attachable) currentSession().get(object.getClass(), object.getId());
  }

  @Override
  public void save(Attachable object) {
    currentSession().persist(object);
  }

  @Override
  public FileAttachment getAttachment(long attachmentId) {
    return (FileAttachment) currentSession().get(FileAttachment.class, attachmentId);
  }

  @Override
  public long getUsage(FileAttachment attachment) {
    QueryBuilder<AttachmentUsage, AttachmentUsage> builder =
        new QueryBuilder<>(currentSession(), AttachmentUsage.class, AttachmentUsage.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(AttachmentUsage_.attachmentId), attachment.getId()));
    AttachmentUsage view = builder.getSingleResultOrNull();

    if (view == null) {
      throw new IllegalArgumentException("No persisted attachment found with ID " + attachment.getId());
    }
    return view.getUsage();
  }

  @Override
  public void delete(FileAttachment attachment) {
    currentSession().remove(attachment);
  }

  @Override
  public void save(FileAttachment attachment) {
    currentSession().persist(attachment);
  }

}
