package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolOrderDao extends HibernateSaveDao<PoolOrder> implements PoolOrderDao, HibernatePaginatedDataSource<PoolOrder> {

  private static final String FIELD_POOL = "pool.id";
  private static final String[] SEARCH_PROPERTIES = new String[] { "alias", "description" };
  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("purpose"));

  public HibernatePoolOrderDao() {
    super(PoolOrder.class);
  }

  @Override
  public Session currentSession() {
    return super.currentSession();
  }

  @Override
  public String getFriendlyName() {
    return "Pool Order";
  }

  @Override
  public String getProjectColumn() {
    throw new IllegalArgumentException();
  }

  @Override
  public Class<? extends PoolOrder> getRealClass() {
    return PoolOrder.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case ENTERED:
      return "creationDate";
    case UPDATE:
      return "lastUpdated";
    default:
      return null;
    }
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
    case "purposeAlias":
      return "purpose.alias";
    default:
      return original;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "createdBy" : "updatedBy";
  }

  @Override
  public void restrictPaginationByFulfilled(Criteria criteria, boolean isFulfilled, Consumer<String> errorHandler) {
    if (isFulfilled) {
      criteria.add(Restrictions.isNotNull("pool"));
      criteria.add(Restrictions.or(Restrictions.isNull("partitions"), Restrictions.isNotNull("sequencingOrder")));
    } else {
      criteria.add(Restrictions.or(Restrictions.isNull("pool"),
          Restrictions.and(Restrictions.isNotNull("partitions"), Restrictions.isNull("sequencingOrder"))));
    }
  }

  @Override
  public void restrictPaginationByDraft(Criteria criteria, boolean isDraft, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("draft", isDraft));
  }

  @Override
  public List<PoolOrder> getAllByPoolId(long poolId) {
    Criteria criteria = currentSession().createCriteria(PoolOrder.class);
    criteria.add(Restrictions.eq(FIELD_POOL, poolId));
    @SuppressWarnings("unchecked")
    List<PoolOrder> list = criteria.list();
    return list;
  }

}
