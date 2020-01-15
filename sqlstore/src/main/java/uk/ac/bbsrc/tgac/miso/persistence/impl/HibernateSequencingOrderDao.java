package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingOrderDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencingOrderDao implements SequencingOrderDao {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SequencingOrder> list() {
    @SuppressWarnings("unchecked")
    List<SequencingOrder> records = currentSession().createCriteria(SequencingOrderImpl.class).list();
    return records;
  }

  @Override
  public SequencingOrder get(long id) {
    return (SequencingOrder) currentSession().get(SequencingOrderImpl.class, id);
  }

  @Override
  public long create(SequencingOrder seqOrder) {
    Date now = new Date();
    seqOrder.setCreationDate(now);
    seqOrder.setLastUpdated(now);
    return (long) currentSession().save(seqOrder);
  }

  @Override
  public long update(SequencingOrder seqOrder) {
    Date now = new Date();
    seqOrder.setLastUpdated(now);
    currentSession().update(seqOrder);
    return seqOrder.getId();
  }

  @Override
  public List<SequencingOrder> listByPool(Pool pool) {
    @SuppressWarnings("unchecked")
    List<SequencingOrder> records = currentSession().createCriteria(SequencingOrderImpl.class)
        .add(Restrictions.eq("pool", pool))
        .list();
    return records;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<SequencingOrder> listByAttributes(Pool pool, RunPurpose purpose, SequencingParameters parameters, Integer partitions)
      throws IOException {
    @SuppressWarnings("unchecked")
    List<SequencingOrder> records = currentSession().createCriteria(SequencingOrderImpl.class)
        .add(Restrictions.eq("pool", pool))
        .add(Restrictions.eq("purpose", purpose))
        .add(Restrictions.eq("parameters", parameters))
        .add(Restrictions.eq("partitions", partitions))
        .list();
    return records;
  }

}
