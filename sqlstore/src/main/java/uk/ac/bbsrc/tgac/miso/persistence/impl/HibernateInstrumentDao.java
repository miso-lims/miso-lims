package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun_;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel_;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord_;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC_;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentDao extends HibernateSaveDao<Instrument>
    implements InstrumentStore, JpaCriteriaPaginatedDataSource<Instrument, InstrumentImpl> {

  public HibernateInstrumentDao() {
    super(Instrument.class, InstrumentImpl.class);
  }

  @Override
  public List<Instrument> listByType(InstrumentType type) throws IOException {
    QueryBuilder<Instrument, InstrumentImpl> builder = getQueryBuilder();
    Join<InstrumentImpl, InstrumentModel> insJoin = builder.getJoin(builder.getRoot(), InstrumentImpl_.instrumentModel);
    builder.addPredicate(builder.getCriteriaBuilder().equal(insJoin.get(InstrumentModel_.instrumentType), type));
    return builder.getResultList();
  }

  @Override
  public Instrument getByName(String name) throws IOException {
    return getBy(InstrumentImpl_.NAME, name);
  }

  @Override
  public Instrument getByBarcode(String barcode) throws IOException {
    return getBy(InstrumentImpl_.IDENTIFICATION_BARCODE, barcode);
  }

  @Override
  public Instrument getByUpgradedInstrument(long id) {
    QueryBuilder<Instrument, InstrumentImpl> builder = getQueryBuilder();
    Join<InstrumentImpl, InstrumentImpl> insJoin =
        builder.getJoin(builder.getRoot(), InstrumentImpl_.upgradedInstrument);
    builder.addPredicate(builder.getCriteriaBuilder().equal(insJoin.get(InstrumentImpl_.id), id));
    return builder.getSingleResultOrNull();
  }

  @Override
  public String getFriendlyName() {
    return "Instrument";
  }

  @Override
  public SingularAttribute<InstrumentImpl, ?> getIdProperty() {
    return InstrumentImpl_.id;
  }

  @Override
  public Class<InstrumentImpl> getEntityClass() {
    return InstrumentImpl.class;
  }

  @Override
  public Class<Instrument> getResultClass() {
    return Instrument.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<InstrumentImpl> root) {
    return Arrays.asList(root.get(InstrumentImpl_.name), root.get(InstrumentImpl_.serialNumber),
        root.get(InstrumentImpl_.identificationBarcode));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, InstrumentImpl> builder, DateType type) {
    return type == DateType.CREATE ? builder.getRoot().get(InstrumentImpl_.dateCommissioned) : null;
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, InstrumentImpl> builder, String original) {
    switch (original) {
      case "platformType":
        Join<InstrumentImpl, InstrumentModel> modelPT =
            builder.getJoin(builder.getRoot(), InstrumentImpl_.instrumentModel);
        return modelPT.get(InstrumentModel_.platformType);
      case "instrumentModelAlias":
        Join<InstrumentImpl, InstrumentModel> modelAlias =
            builder.getJoin(builder.getRoot(), InstrumentImpl_.instrumentModel);
        return modelAlias.get(InstrumentModel_.alias);
      case "workstationAlias":
        Join<InstrumentImpl, Workstation> workstation = builder.getJoin(builder.getRoot(), InstrumentImpl_.workstation);
        return workstation.get(Workstation_.alias);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<InstrumentImpl, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByDate(QueryBuilder<?, InstrumentImpl> builder, Date start, Date end, DateType type,
      Consumer<String> errorHandler) {
    if (type == DateType.CREATE) {
      JpaCriteriaPaginatedDataSource.super.restrictPaginationByLocalDate(builder, start, end, type,
          errorHandler);
    } else {
      JpaCriteriaPaginatedDataSource.super.restrictPaginationByDate(builder, start, end, type,
          errorHandler);
    }
  }

  @Override
  public void restrictPaginationByWorkstationId(QueryBuilder<?, InstrumentImpl> builder, long id,
      Consumer<String> errorHandler) {
    Join<InstrumentImpl, Workstation> join = builder.getJoin(builder.getRoot(), InstrumentImpl_.workstation);
    builder.addPredicate(builder.getCriteriaBuilder().equal(join.get(Workstation_.workstationId), id));
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, InstrumentImpl> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    Join<InstrumentImpl, InstrumentModel> join = builder.getJoin(builder.getRoot(), InstrumentImpl_.instrumentModel);
    builder.addPredicate(builder.getCriteriaBuilder().equal(join.get(InstrumentModel_.platformType), platformType));
  }

  @Override
  public void restrictPaginationByArchived(QueryBuilder<?, InstrumentImpl> builder, boolean isArchived,
      Consumer<String> errorHandler) {
    if (isArchived) {
      builder.addPredicate(
          builder.getCriteriaBuilder().isNotNull(builder.getRoot().get(InstrumentImpl_.dateDecommissioned)));
    } else {
      builder
          .addPredicate(builder.getCriteriaBuilder().isNull(builder.getRoot().get(InstrumentImpl_.dateDecommissioned)));
    }
  }

  @Override
  public void restrictPaginationByInstrumentType(QueryBuilder<?, InstrumentImpl> builder, InstrumentType type,
      Consumer<String> errorHandler) {
    Join<InstrumentImpl, InstrumentModel> join = builder.getJoin(builder.getRoot(), InstrumentImpl_.instrumentModel);
    builder.addPredicate(builder.getCriteriaBuilder().equal(join.get(InstrumentModel_.instrumentType), type));
  }

  @Override
  public void restrictPaginationByModel(QueryBuilder<?, InstrumentImpl> builder, String query,
      Consumer<String> errorHandler) {
    builder
        .addTextRestriction(builder.getRoot().get(InstrumentImpl_.instrumentModel).get(InstrumentModel_.alias), query);
  }

  @Override
  public void restrictPaginationByWorkstation(QueryBuilder<?, InstrumentImpl> builder, String query,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(Arrays.asList(builder.getRoot().get(InstrumentImpl_.workstation).get(Workstation_.alias),
        builder.getRoot().get(InstrumentImpl_.workstation).get(Workstation_.identificationBarcode)), query);
  }

  @Override
  public long getUsageByRuns(Instrument instrument) throws IOException {
    return getUsageInCollection(Run.class, Run_.SEQUENCER, instrument);
  }

  @Override
  public long getUsageByArrayRuns(Instrument instrument) throws IOException {
    return getUsageInCollection(ArrayRun.class, ArrayRun_.INSTRUMENT, instrument);
  }

  @Override
  public long getUsageByQcs(Instrument instrument) throws IOException {
    List<Long> counts = new ArrayList<>();
    for (QcTarget qc : QcTarget.values()) {
      Class<? extends QC> entityClass = qc.getEntityClass();
      LongQueryBuilder<?> builder = new LongQueryBuilder<>(currentSession(), entityClass);
      builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(QC_.INSTRUMENT), instrument));
      counts.add(builder.getCount());
    }
    return counts.stream().mapToLong(Long::longValue).sum();
  }

  @Override
  public Instrument getByServiceRecord(ServiceRecord record) throws IOException {
    QueryBuilder<Instrument, InstrumentImpl> builder = getQueryBuilder();
    Join<InstrumentImpl, ServiceRecord> recordJoin = builder.getJoin(builder.getRoot(), InstrumentImpl_.serviceRecords);
    builder.addPredicate(builder.getCriteriaBuilder().equal(recordJoin.get(ServiceRecord_.recordId), record.getId()));
    return builder.getSingleResultOrNull();
  }

}
