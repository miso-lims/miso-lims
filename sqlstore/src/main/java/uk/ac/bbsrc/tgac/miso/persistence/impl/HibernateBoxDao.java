package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
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

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBoxDao implements BoxStore, HibernatePaginatedDataSource<Box> {

  private static final String FIELD_ALIAS = "alias";

  protected static final String[] SEARCH_PROPERTIES = new String[] { "name", FIELD_ALIAS, "identificationBarcode", "locationBarcode" };

  private static final List<String> STANDARD_ALIASES = Arrays.asList("lastModifier", "creator", "size", "use");

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

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public void discardAllContents(Box box, User currentUser) throws IOException {
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
      updateBoxable(boxable, currentUser);
    }
  }

  @Override
  public void discardSingleItem(Box box, String position, User currentUser) throws IOException {
    BoxableView target = box.getBoxable(position);
    target.setDiscarded(true);
    box.removeBoxable(position);
    save(box);
    updateBoxable(target, currentUser);
  }

  private void updateBoxable(BoxableView view, User currentUser) throws IOException {
    Boxable boxable = getBoxable(view);
    applyChanges(view, boxable, currentUser);
    currentSession().update(boxable);
  }

  private Boxable getBoxable(BoxableView view) {
    Class<?> clazz = view.getId().getTargetType().getPersistClass();
    return (Boxable) currentSession().get(clazz, view.getId().getTargetId());
  }

  private void applyChanges(BoxableView from, Boxable to, User currentUser) {
    to.setVolume(from.getVolume());
    to.setDiscarded(from.isDiscarded());
    to.setLastModified(new Date());
    to.setLastModifier(currentUser);
  }

  @Override
  public Box get(long boxId) throws IOException {
    return (Box) currentSession().get(BoxImpl.class, boxId);
  }

  @Override
  public Box getDetached(long boxId) throws IOException {
    Box box = (Box) currentSession().get(BoxImpl.class, boxId);
    Hibernate.initialize(box.getBoxables());
    currentSession().evict(box);
    return box;
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.add(Restrictions.eq(FIELD_ALIAS, alias));
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

  protected SessionFactory getSessionFactory() {
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
  public List<Box> getBySearch(String search) {
    if (search == null) {
      throw new NullPointerException("No search String provided");
    }
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.add(Restrictions.or(
        Restrictions.eq("identificationBarcode", search),
        Restrictions.eq("name", search),
        Restrictions.eq(FIELD_ALIAS, search)
        ));
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
  public void removeBoxableFromBox(Boxable boxable) throws IOException {
    Long boxId = boxable.getBox() == null ? null : boxable.getBox().getId();
    removeBoxableFromBox(boxId, boxable.getBoxPosition());
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
    Box persisted = (Box) currentSession().merge(box);
    currentSession().update(persisted);
  }

  @Override
  public long save(Box box) throws IOException {
    if (box.getId() == AbstractBox.UNSAVED_ID) {
      return (long) currentSession().save(box);
    } else {
      // Merge required to allow temporary eviction during update (see getDetached)
      Box persisted = (Box) currentSession().merge(box);
      currentSession().update(persisted);
      return box.getId();
    }
  }

  @Override
  public BoxableView getBoxableView(BoxableId id) throws IOException {
    return (BoxableView) currentSession().get(BoxableView.class, id);
  }

  @Override
  public List<BoxableView> getBoxableViewsByBarcodeList(Collection<String> barcodes) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxableView.class);
    criteria.add(Restrictions.in("identificationBarcode", barcodes));
    @SuppressWarnings("unchecked")
    List<BoxableView> results = criteria.list();
    return results;
  }

  @Override
  public List<BoxableView> getBoxableViewsByIdList(Collection<BoxableId> ids) throws IOException {
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(BoxableView.class);
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<BoxableView> results = criteria.list();
    return results;
  }

  @Override
  public BoxableView getBoxableViewByPreMigrationId(Long preMigrationId) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxableView.class);
    criteria.add(Restrictions.eq("preMigrationId", preMigrationId));
    BoxableView result = (BoxableView) criteria.uniqueResult();
    return result;
  }

  @Override
  public List<BoxableView> getBoxableViewsBySearch(String search) {
    if (search == null) {
      throw new NullPointerException("No search String provided");
    }
    Criteria criteria = currentSession().createCriteria(BoxableView.class);
    criteria.add(Restrictions.or(
        Restrictions.eq("identificationBarcode", search),
        Restrictions.eq("name", search),
        Restrictions.eq(FIELD_ALIAS, search)
        ));
    criteria.add(Restrictions.eq("discarded", false));
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

  @Override
  public String getFriendlyName() {
    return "Box";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends Box> getRealClass() {
    return BoxImpl.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<String> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case CREATE:
      return "creationTime";
    case UPDATE:
      return "lastModified";
    default:
      return null;
    }
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
    case "sizeId":
      return "size.id";
    case "useId":
      return "use.id";
    default:
      return original;
    }
  }

  @Override
  public String propertyForUserName(Criteria criteria, boolean creator) {
    return creator ? null : "lastModifier.loginName";
  }

  @Override
  public void restrictPaginationByBoxUse(Criteria criteria, long id, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("use.id", id));
  }

}
