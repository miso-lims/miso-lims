package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionPause;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionPause_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.RequisitionDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateRequisitionDao extends HibernateSaveDao<Requisition>
    implements JpaCriteriaPaginatedDataSource<Requisition, Requisition>, RequisitionDao {

  public HibernateRequisitionDao() {
    super(Requisition.class);
  }

  @Override
  public Requisition getByAlias(String alias) throws IOException {
    return getBy(Requisition_.ALIAS, alias);
  }

  @Override
  public List<Requisition> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(Requisition_.REQUISITION_ID, ids);
  }

  @Override
  public RequisitionSupplementalSample getSupplementalSample(Requisition requisition, Sample sample)
      throws IOException {
    QueryBuilder<RequisitionSupplementalSample, RequisitionSupplementalSample> builder = new QueryBuilder<>(
        currentSession(), RequisitionSupplementalSample.class, RequisitionSupplementalSample.class);
    builder.addPredicate(builder.getCriteriaBuilder()
        .equal(builder.getRoot().get(RequisitionSupplementalSample_.requisitionId), requisition.getId()));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(RequisitionSupplementalSample_.sample), sample));
    return builder.getSingleResultOrNull();
  }

  @Override
  public void saveSupplementalSample(RequisitionSupplementalSample sample) throws IOException {
    currentSession().persist(sample);
  }

  @Override
  public void removeSupplementalSample(RequisitionSupplementalSample sample) throws IOException {
    currentSession().remove(sample);
  }

  @Override
  public RequisitionSupplementalLibrary getSupplementalLibrary(Requisition requisition, Library library)
      throws IOException {
    QueryBuilder<RequisitionSupplementalLibrary, RequisitionSupplementalLibrary> builder = new QueryBuilder<>(
        currentSession(), RequisitionSupplementalLibrary.class, RequisitionSupplementalLibrary.class);
    builder.addPredicate(builder.getCriteriaBuilder()
        .equal(builder.getRoot().get(RequisitionSupplementalLibrary_.requisitionId), requisition.getId()));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(RequisitionSupplementalLibrary_.library), library));
    return builder.getSingleResultOrNull();
  }

  @Override
  public void saveSupplementalLibrary(RequisitionSupplementalLibrary library) throws IOException {
    currentSession().persist(library);
  }

  @Override
  public void removeSupplementalLibrary(RequisitionSupplementalLibrary library) throws IOException {
    currentSession().remove(library);
  }

  @Override
  public String getFriendlyName() {
    return "Requisition";
  }

  @Override
  public SingularAttribute<Requisition, ?> getIdProperty() {
    return Requisition_.requisitionId;
  }

  @Override
  public Class<Requisition> getEntityClass() {
    return Requisition.class;
  }

  @Override
  public Class<Requisition> getResultClass() {
    return Requisition.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<Requisition> root) {
    return Arrays.asList(root.get(Requisition_.alias));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, Requisition> builder, DateType type) {
    switch (type) {
      case ENTERED:
        return builder.getRoot().get(Requisition_.created);
      case UPDATE:
        return builder.getRoot().get(Requisition_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, Requisition> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(Requisition_.requisitionId);
      case "creationTime":
        return builder.getRoot().get(Requisition_.created);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<Requisition, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? Requisition_.creator : Requisition_.lastModifier;
  }

  @Override
  public void restrictPaginationByStatus(QueryBuilder<?, Requisition> builder, String status,
      Consumer<String> errorHandler) {
    switch (status) {
      case "stopped":
        builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Requisition_.stopped), true));
        break;
      case "paused":
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Join<Requisition, RequisitionPause> pause = builder.getJoin(builder.getRoot(), Requisition_.pauses);
        builder.addPredicate(
            builder.getCriteriaBuilder().lessThanOrEqualTo(pause.get(RequisitionPause_.startDate), today));
        builder.addPredicate(
            builder.getCriteriaBuilder().or(builder.getCriteriaBuilder().isNull(pause.get(RequisitionPause_.endDate)),
                builder.getCriteriaBuilder().greaterThan(pause.get(RequisitionPause_.endDate), today)));
        break;
      case "ongoing":
        builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Requisition_.stopped), false));
        List<Long> ids = makePausedRequisitionIdsSubquery();
        In<Long> inClause = builder.getCriteriaBuilder().in(builder.getRoot().get(Requisition_.requisitionId));
        for (Long id : ids) {
          inClause.value(id);
        }
        builder.addPredicate(builder.getCriteriaBuilder().not(inClause));
        break;
      default:
        errorHandler.accept("Unknown requisition status: " + status);
        break;
    }
  }

  private List<Long> makePausedRequisitionIdsSubquery() {
    LocalDate today = LocalDate.now(ZoneId.systemDefault());
    QueryBuilder<Long, Requisition> builder = new QueryBuilder<>(currentSession(), Requisition.class, Long.class);
    Join<Requisition, RequisitionPause> pause = builder.getJoin(builder.getRoot(), Requisition_.pauses);
    builder.addPredicate(builder.getCriteriaBuilder().lessThanOrEqualTo(pause.get(RequisitionPause_.startDate), today));
    builder.addPredicate(
        builder.getCriteriaBuilder().or(builder.getCriteriaBuilder().isNull(pause.get(RequisitionPause_.endDate)),
            builder.getCriteriaBuilder().greaterThan(pause.get(RequisitionPause_.endDate), today)));
    builder.setColumn(builder.getRoot().get(Requisition_.requisitionId));
    return builder.getResultList();
  }

}
