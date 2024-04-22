package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition_;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.RequisitionDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateRequisitionDao extends HibernateSaveDao<Requisition>
    implements HibernatePaginatedDataSource<Requisition>, RequisitionDao {

  private final static String[] SEARCH_PROPERTIES = new String[] {"alias"};
  private static final List<AliasDescriptor> STANDARD_ALIASES = Collections.emptyList();

  public HibernateRequisitionDao() {
    super(Requisition.class);
  }

  @Override
  public Requisition getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public List<Requisition> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(Requisition_.REQUISITION_ID, ids);
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
  public RequisitionSupplementalLibrary getSupplementalLibrary(Requisition requisition, Library library)
      throws IOException {
    return (RequisitionSupplementalLibrary) currentSession().createCriteria(RequisitionSupplementalLibrary.class)
        .add(Restrictions.eq("requisitionId", requisition.getId()))
        .add(Restrictions.eq("library", library))
        .uniqueResult();
  }

  @Override
  public void saveSupplementalLibrary(RequisitionSupplementalLibrary library) throws IOException {
    currentSession().save(library);
  }

  @Override
  public void removeSupplementalLibrary(RequisitionSupplementalLibrary library) throws IOException {
    currentSession().delete(library);
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

  @Override
  public void restrictPaginationByStatus(Criteria criteria, String status, Consumer<String> errorHandler) {
    switch (status) {
      case "stopped":
        criteria.add(Restrictions.eq(Requisition_.STOPPED, true));
        break;
      case "paused":
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        criteria.createAlias(Requisition_.PAUSES, "pause")
            .add(Restrictions.le("pause.startDate", today))
            .add(Restrictions.or(Restrictions.isNull("pause.endDate"),
                Restrictions.gt("pause.endDate", today)));
        break;
      case "ongoing":
        criteria.add(Restrictions.eq(Requisition_.STOPPED, false))
            .add(Subqueries.propertyNotIn(Requisition_.REQUISITION_ID, makePausedRequisitionIdsSubquery()));
        break;
      default:
        errorHandler.accept("Unknown requisition status: " + status);
        break;
    }
  }

  private DetachedCriteria makePausedRequisitionIdsSubquery() {
    LocalDate today = LocalDate.now(ZoneId.systemDefault());
    return DetachedCriteria.forClass(Requisition.class)
        .createAlias(Requisition_.PAUSES, "pause")
        .add(Restrictions.le("pause.startDate", today))
        .add(Restrictions.or(Restrictions.isNull("pause.endDate"),
            Restrictions.gt("pause.endDate", today)))
        .setProjection(Projections.id());
  }

}
