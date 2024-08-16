package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor_;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType_;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.KitStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateKitDao implements KitStore, JpaCriteriaPaginatedDataSource<KitDescriptor, KitDescriptor> {

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Kit get(long id) throws IOException {
    return (Kit) currentSession().get(KitImpl.class, id);
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    QueryBuilder<Kit, KitImpl> builder = new QueryBuilder<>(currentSession(), KitImpl.class, Kit.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(KitImpl_.lotNumber), lotNumber));
    return builder.getSingleResultOrNull();
  }

  @Override
  public List<Kit> listAll() throws IOException {
    return new QueryBuilder<>(currentSession(), KitImpl.class, Kit.class).getResultList();
  }

  @Override
  public long save(Kit kit) throws IOException {
    long id;
    if (!kit.isSaved()) {
      currentSession().persist(kit);
      id = kit.getId();
    } else {
      id = currentSession().merge(kit).getId();
    }
    return id;
  }

  @Override
  public KitDescriptor getKitDescriptorById(long id) throws IOException {
    return (KitDescriptor) currentSession().get(KitDescriptor.class, id);
  }

  @Override
  public KitDescriptor getKitDescriptorByName(String name) throws IOException {
    QueryBuilder<KitDescriptor, KitDescriptor> builder = getQueryBuilder();
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(KitDescriptor_.name), name));
    return builder.getSingleResultOrNull();
  }

  @Override
  public KitDescriptor getKitDescriptorByPartNumber(String partNumber, KitType kitType, PlatformType platformType)
      throws IOException {
    QueryBuilder<KitDescriptor, KitDescriptor> builder = getQueryBuilder();
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(KitDescriptor_.partNumber), partNumber));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(KitDescriptor_.kitType), kitType));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(KitDescriptor_.platformType), platformType));
    return builder.getSingleResultOrNull();
  }

  @Override
  public List<KitDescriptor> listAllKitDescriptors() throws IOException {
    return getQueryBuilder().getResultList();
  }

  @Override
  public long saveKitDescriptor(KitDescriptor kd) throws IOException {
    long id;
    if (!kd.isSaved()) {
      currentSession().persist(kd);
      id = kd.getId();
    } else {
      id = currentSession().merge(kd).getId();
    }
    return id;
  }

  @Override
  public void restrictPaginationByKitType(QueryBuilder<?, KitDescriptor> builder, KitType type,
      Consumer<String> errorHandler) {
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(KitDescriptor_.kitType), type));
  }

  @Override
  public void restrictPaginationByKitName(QueryBuilder<?, KitDescriptor> builder, String query,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot().get(KitDescriptor_.name), query);
  }

  @Override
  public String getFriendlyName() {
    return "Kit";
  }

  @Override
  public Class<KitDescriptor> getEntityClass() {
    return KitDescriptor.class;
  }

  @Override
  public Class<KitDescriptor> getResultClass() {
    return KitDescriptor.class;
  }

  @Override
  public SingularAttribute<KitDescriptor, ?> getIdProperty() {
    return KitDescriptor_.kitDescriptorId;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<KitDescriptor> root) {
    return Arrays.asList(root.get(KitDescriptor_.name), root.get(KitDescriptor_.manufacturer),
        root.get(KitDescriptor_.partNumber), root.get(KitDescriptor_.description));
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, KitDescriptor> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(KitDescriptor_.kitDescriptorId);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, KitDescriptor> builder, DateType type) {
    return null;
  }

  @Override
  public SingularAttribute<KitDescriptor, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? KitDescriptor_.creator : KitDescriptor_.lastModifier;
  }

  @Override
  public List<LibraryAliquot> getLibraryAliquotsForKdTsRelationship(KitDescriptor kd, TargetedSequencing ts) {
    QueryBuilder<LibraryAliquot, LibraryAliquot> builder =
        new QueryBuilder<>(currentSession(), LibraryAliquot.class, LibraryAliquot.class);
    Root<LibraryAliquot> root = builder.getRoot();
    Join<LibraryAliquot, KitDescriptor> kitJoin = builder.getJoin(root, LibraryAliquot_.kitDescriptor);
    Join<LibraryAliquot, TargetedSequencing> tarseqJoin = builder.getJoin(root, LibraryAliquot_.targetedSequencing);
    builder.addPredicate(builder.getCriteriaBuilder().equal(kitJoin.get(KitDescriptor_.kitDescriptorId), kd.getId()));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(tarseqJoin.get(TargetedSequencing_.targetedSequencingId), ts.getId()));
    return builder.getResultList();
  }

  @Override
  public long getUsageByLibraries(KitDescriptor kitDescriptor) throws IOException {
    LongQueryBuilder<LibraryImpl> builder = new LongQueryBuilder<>(currentSession(), LibraryImpl.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryImpl_.kitDescriptor), kitDescriptor));
    return builder.getCount();
  }

  @Override
  public long getUsageByLibraryAliquots(KitDescriptor kitDescriptor) throws IOException {
    LongQueryBuilder<LibraryAliquot> builder = new LongQueryBuilder<>(currentSession(), LibraryAliquot.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryAliquot_.kitDescriptor), kitDescriptor));
    return builder.getCount();
  }

  @Override
  public long getUsageByContainers(KitDescriptor kitDescriptor) throws IOException {
    LongQueryBuilder<SequencerPartitionContainerImpl> builder =
        new LongQueryBuilder<>(currentSession(), SequencerPartitionContainerImpl.class);
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencerPartitionContainerImpl_.clusteringKit),
            kitDescriptor),
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencerPartitionContainerImpl_.multiplexingKit),
            kitDescriptor)));
    return builder.getCount();
  }

  @Override
  public long getUsageByRuns(KitDescriptor kitDescriptor) throws IOException {
    LongQueryBuilder<Run> builder = new LongQueryBuilder<>(currentSession(), Run.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(Run_.sequencingKit), kitDescriptor));
    return builder.getCount();
  }

  @Override
  public long getUsageByQcTypes(KitDescriptor kitDescriptor) throws IOException {
    LongQueryBuilder<QcType> builder = new LongQueryBuilder<>(currentSession(), QcType.class);
    Join<QcType, KitDescriptor> kitJoin = builder.getJoin(builder.getRoot(), QcType_.kitDescriptors);
    builder.addPredicate(builder.getCriteriaBuilder().equal(kitJoin, kitDescriptor));
    return builder.getCount();
  }

  @Override
  public List<KitDescriptor> search(KitType type, String search) throws IOException {
    QueryBuilder<KitDescriptor, KitDescriptor> builder = getQueryBuilder();
    Root<KitDescriptor> root = builder.getRoot();
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(KitDescriptor_.kitType), type));
    builder.addPredicate(
        builder.getCriteriaBuilder().or(builder.getCriteriaBuilder().like(root.get(KitDescriptor_.name), search + "%"),
            builder.getCriteriaBuilder().equal(root.get(KitDescriptor_.partNumber), search)));
    return builder.getResultList();
  }

}
