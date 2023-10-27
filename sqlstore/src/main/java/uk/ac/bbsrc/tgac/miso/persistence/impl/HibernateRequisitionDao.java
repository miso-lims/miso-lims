package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition_;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.RequisitionDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateRequisitionDao extends HibernateSaveDao<Requisition>
    implements HibernatePaginatedDataSource<Requisition>, RequisitionDao {

  private final static String[] SEARCH_PROPERTIES = new String[] {"alias"};
  private static final List<AliasDescriptor> STANDARD_ALIASES =
      Arrays.asList(new AliasDescriptor("assay", JoinType.LEFT_OUTER_JOIN));

  public HibernateRequisitionDao() {
    super(Requisition.class);
  }

  @Override
  public Requisition getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public RequisitionSupplementalSample getSupplementalSample(Requisition requisition, Sample sample)
      throws IOException {
    return (RequisitionSupplementalSample) currentSession().createCriteria(RequisitionSupplementalSample.class)
        .add(Restrictions.eq("requisitionId", requisition.getId()))
        .add(Restrictions.eq("sample", sample))
        .uniqueResult();
  }

  @Override
  public void saveSupplementalSample(RequisitionSupplementalSample sample) throws IOException {
    currentSession().save(sample);
  }

  @Override
  public void removeSupplementalSample(RequisitionSupplementalSample sample) throws IOException {
    currentSession().delete(sample);
  }

  @Override
  public String getFriendlyName() {
    return "Requisition";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends Requisition> getRealClass() {
    return Requisition.class;
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
        return "created";
      case UPDATE:
        return "lastModified";
      default:
        return null;
    }
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
      case "assayId":
        return "assay.alias";
      case "creationTime":
        return Requisition_.CREATED;
      default:
        return original;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

}
