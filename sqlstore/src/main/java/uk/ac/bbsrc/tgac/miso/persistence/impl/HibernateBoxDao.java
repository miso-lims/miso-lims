package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBoxDao implements BoxStore {

  private static final Logger log = LoggerFactory.getLogger(HibernateBoxDao.class);

  private static String TABLE_NAME = "Box";

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate template;

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(BoxImpl.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public void discardAllTubes(Box box) throws IOException {
    List<BoxableView> originalContents = new ArrayList<>(box.getBoxables().values());
    try {
      box.removeAllBoxables();
      save(box);
    } catch (IOException e) {
      log.debug("Error discarding box", e);
      throw new IOException("Error discarding box: " + e.getMessage());
    }

    for (BoxableView boxable : originalContents) {
      boxable.setDiscarded(true);
      updateBoxable(boxable);
    }
  }

  @Override
  public void discardSingleTube(Box box, String position) throws IOException {
    BoxableView target = box.getBoxable(position);
    target.setDiscarded(true);
    box.removeBoxable(position);
    save(box);
    updateBoxable(target);
  }

  private void updateBoxable(BoxableView view) throws IOException {
    Boxable boxable = getBoxable(view);
    applyChanges(view, boxable);
    currentSession().update(boxable);
  }

  private Boxable getBoxable(BoxableView view) {
    Class<?> clazz = view.getId().getTargetType().getPersistClass();
    return (Boxable) currentSession().get(clazz, view.getId().getTargetId());
  }

  private void applyChanges(BoxableView from, Boxable to) {
    to.setDiscarded(from.isDiscarded());
    to.setVolume(from.getVolume());
  }

  @Override
  public Box get(long boxId) throws IOException {
    return (Box) currentSession().get(BoxImpl.class, boxId);
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (Box) criteria.uniqueResult();
  }

  @Override
  public Map<String, Integer> getBoxColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }

  @Override
  public Box getByBarcode(String barcode) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (Box) criteria.uniqueResult();
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public BoxSize getSizeById(long id) throws IOException {
    return (BoxSize) currentSession().get(BoxSize.class, id);
  }

  @Override
  public BoxUse getUseById(long id) throws IOException {
    return (BoxUse) currentSession().get(BoxUse.class, id);
  }

  @Override
  public Collection<Box> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    @SuppressWarnings("unchecked")
    List<Box> results = criteria.list();
    return results;
  }

  @Override
  public Collection<BoxSize> listAllBoxSizes() throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxSize.class);
    @SuppressWarnings("unchecked")
    List<BoxSize> results = criteria.list();
    return results;
  }

  @Override
  public Collection<BoxUse> listAllBoxUses() throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxUse.class);
    @SuppressWarnings("unchecked")
    List<BoxUse> results = criteria.list();
    return results;
  }

  @Override
  public List<String> listAllBoxUsesStrings() throws IOException {
    List<String> results = new ArrayList<>();
    for (BoxUse use : listAllBoxUses()) {
      results.add(use.getAlias());
    }
    return results;
  }

  @Override
  public Collection<Box> listWithLimit(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Box> results = criteria.list();
    return results;
  }

  @Override
  public boolean remove(Box box) throws IOException {
    if (box.getId() != AbstractBox.UNSAVED_ID) {
      currentSession().delete(box);
      return true;
    }
    return false;
  }

  @Override
  public void removeBoxableFromBox(Boxable boxable) throws IOException {
    if (boxable.getBox() == null) {
      return;
    }
    removeBoxableFromBox(boxable.getBox().getId(), boxable.getBoxPosition());
  }

  @Override
  public void removeBoxableFromBox(BoxableView boxable) throws IOException {
    removeBoxableFromBox(boxable.getBoxId(), boxable.getBoxPosition());
  }

  private void removeBoxableFromBox(Long boxId, String position) throws IOException {
    if (boxId == null) {
      return;
    }
    Box box = get(boxId);
    box.removeBoxable(position);
    currentSession().save(box);
  }

  @Override
  public long save(Box box) throws IOException {
    if (box.getId() == AbstractBox.UNSAVED_ID) {
      return (long) currentSession().save(box);
    } else {
      currentSession().update(box);
      return box.getId();
    }
  }

  @Override
  public BoxableView getBoxableView(BoxableId id) throws IOException {
    return (BoxableView) currentSession().get(BoxableView.class, id);
  }

  @Override
  public BoxableView getBoxableViewByBarcode(String barcode) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxableView.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (BoxableView) criteria.uniqueResult();
  }

  @Override
  public List<BoxableView> getBoxableViewsByBarcodeList(Collection<String> barcodes) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxableView.class);
    criteria.add(Restrictions.in("identificationBarcode", barcodes));
    @SuppressWarnings("unchecked")
    List<BoxableView> results = criteria.list();
    return results;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
