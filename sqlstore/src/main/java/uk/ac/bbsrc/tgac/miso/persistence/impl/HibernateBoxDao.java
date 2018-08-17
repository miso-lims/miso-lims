package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.DilutionBoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.LibraryBoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolBoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SampleBoxableView;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBoxDao implements BoxStore, HibernatePaginatedDataSource<Box> {

  private static final String FIELD_ALIAS = "alias";

  protected static final String[] SEARCH_PROPERTIES = new String[] { "name", FIELD_ALIAS, "identificationBarcode", "locationBarcode" };

  private static final List<String> STANDARD_ALIASES = Arrays.asList("lastModifier", "creator", "size", "use");

  private static final List<Class<? extends BoxableView>> VIEW_CLASSES = Lists.newArrayList(SampleBoxableView.class,
      LibraryBoxableView.class, DilutionBoxableView.class, PoolBoxableView.class);

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
  public Boxable getBoxable(BoxableId id) {
    Class<?> clazz = id.getTargetType().getPersistClass();
    return (Boxable) currentSession().get(clazz, id.getTargetId());
  }

  @Override
  public void saveBoxable(Boxable boxable) {
    currentSession().update(boxable);
  }

  @Override
  public Box get(long boxId) throws IOException {
    return (Box) currentSession().get(BoxImpl.class, boxId);
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
  public List<Box> getByIdList(List<Long> idList) throws IOException {
    if (idList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.add(Restrictions.in("boxId", idList));
    @SuppressWarnings("unchecked")
    List<Box> records = criteria.list();
    return records;
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
        Restrictions.eq(FIELD_ALIAS, search)));
    @SuppressWarnings("unchecked")
    List<Box> results = criteria.list();
    return results;
  }

  private static String getMostSimilarProperty(Box box, String search) {
    List<String> properties = new ArrayList<>(Arrays.asList(box.getAlias(), box.getName()));
    if (box.getIdentificationBarcode() != null) {
      properties.add(box.getIdentificationBarcode());
    }
    return properties.stream().map(String::toLowerCase).filter(p -> p.indexOf(search) >= 0)
        .min(Comparator.comparingInt(p -> p.indexOf(search))).orElse("");
  }

  @Override
  public List<Box> getByPartialSearch(String search, boolean onlyMatchBeginning) {
    if (search == null) {
      throw new NullPointerException("No search String provided");
    }
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.add(Restrictions.or(
        Restrictions.like("identificationBarcode", search, onlyMatchBeginning ? MatchMode.START : MatchMode.ANYWHERE),
        Restrictions.like("name", search, onlyMatchBeginning ? MatchMode.START : MatchMode.ANYWHERE),
        Restrictions.like(FIELD_ALIAS, search, onlyMatchBeginning ? MatchMode.START : MatchMode.ANYWHERE)));
    @SuppressWarnings("unchecked")
    List<Box> results = criteria.list();
    results.sort((Box b1, Box b2) -> {
      String p1 = getMostSimilarProperty(b1, search.toLowerCase());
      String p2 = getMostSimilarProperty(b2, search.toLowerCase());
      if (p1.indexOf(search.toLowerCase()) == p2.indexOf(search.toLowerCase())) {
        if (p1.length() == p2.length()) {
          return b1.getAlias().compareTo(b2.getAlias());
        }
        return p1.length() - p2.length();
      }
      return p1.indexOf(search.toLowerCase()) - p2.indexOf(search.toLowerCase());
    });
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
    if (boxable.getBox() != null) {
      boxable.getBox().getBoxPositions().remove(boxable.getBoxPosition());
      currentSession().save(boxable.getBox());
    }
  }

  @Override
  public void removeBoxableFromBox(BoxableView boxable) throws IOException {
    if (boxable.getBoxId() == null) {
      return;
    }
    Box box = get(boxable.getBoxId());
    box.getBoxPositions().remove(boxable.getBoxPosition());
    currentSession().update(box);
    // flush required to avoid constraint violation incase item is immediately added to another box or the same one
    currentSession().flush();
  }

  @Override
  public long save(Box box) throws IOException {
    if (!box.isSaved()) {
      return (long) currentSession().save(box);
    } else {
      currentSession().update(box);
      // flush required to avoid constraint violation incase removed items are immediately added to another box or the same one
      currentSession().flush();
      return box.getId();
    }
  }

  @Override
  public BoxableView getBoxableView(BoxableId id) throws IOException {
    List<BoxableView> results = queryBoxables(Restrictions.eq("id", id));
    switch (results.size()) {
    case 0:
      return null;
    case 1:
      return results.get(0);
    default:
      throw new IllegalStateException(String.format("More than one Boxable found with ID %s %d", id.getTargetType(), id.getTargetId()));
    }
  }

  @Override
  public List<BoxableView> getBoxableViewsByBarcodeList(Collection<String> barcodes) throws IOException {
    return queryBoxables(Restrictions.in("identificationBarcode", barcodes));
  }

  @Override
  public List<BoxableView> getBoxableViewsByIdList(Collection<BoxableId> ids) throws IOException {
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    return queryBoxables(Restrictions.in("id", ids));
  }

  @Override
  public List<BoxableView> getBoxContents(long boxId) throws IOException {
    return queryBoxables(Restrictions.eq("boxId", boxId));
  }

  private List<BoxableView> queryBoxables(Criterion criterion) {
    List<BoxableView> results = new ArrayList<>();
    for (Class<? extends BoxableView> clazz : VIEW_CLASSES) {
      @SuppressWarnings("unchecked")
      List<BoxableView> partialResults = currentSession().createCriteria(clazz)
          .add(criterion)
          .list();
      results.addAll(partialResults);
    }
    return results;
  }

  @Override
  public BoxableView getBoxableViewByPreMigrationId(Long preMigrationId) throws IOException {
    if (preMigrationId == null) {
      throw new NullPointerException("Cannot search for null preMigrationId");
    }
    List<BoxableView> results = queryBoxables(Restrictions.eq("preMigrationId", preMigrationId));
    switch (results.size()) {
    case 0:
      return null;
    case 1:
      return results.get(0);
    default:
      throw new IllegalStateException(String.format("More than one Boxable found with preMigrationId %d", preMigrationId));
    }
  }

  @Override
  public List<BoxableView> getBoxableViewsBySearch(String search) {
    if (search == null) {
      throw new NullPointerException("No search String provided");
    }
    return queryBoxables(Restrictions.and(Restrictions.or(
        Restrictions.eq("identificationBarcode", search),
        Restrictions.eq("name", search),
        Restrictions.eq(FIELD_ALIAS, search)), Restrictions.eq("discarded", false)));
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
