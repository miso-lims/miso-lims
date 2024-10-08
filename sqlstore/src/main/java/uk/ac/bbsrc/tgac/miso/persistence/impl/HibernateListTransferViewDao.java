package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSubproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSubproject_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibraryAliquotParent;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibraryAliquotParent_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibraryParent;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibraryParent_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewPool_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewProject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewProject_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewSampleParent;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewSampleParent_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView_;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.TransferType;
import uk.ac.bbsrc.tgac.miso.persistence.ListTransferViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListTransferViewDao
    implements ListTransferViewDao, JpaCriteriaPaginatedDataSource<ListTransferView, ListTransferView> {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public String getFriendlyName() {
    return "Transfer";
  }

  @Override
  public SingularAttribute<ListTransferView, ?> getIdProperty() {
    return ListTransferView_.transferId;
  }

  @Override
  public Class<ListTransferView> getEntityClass() {
    return ListTransferView.class;
  }

  @Override
  public Class<ListTransferView> getResultClass() {
    return ListTransferView.class;
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, ListTransferView> builder, DateType type) {
    switch (type) {
      case ENTERED:
        return builder.getRoot().get(ListTransferView_.created);
      case CREATE:
      case RECEIVE:
        return builder.getRoot().get(ListTransferView_.transferTime);
      case UPDATE:
        return builder.getRoot().get(ListTransferView_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, ListTransferView> builder, String original) {
    if ("id".equals(original)) {
      return builder.getRoot().get(ListTransferView_.transferId);
    } else {
      return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<ListTransferView, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? ListTransferView_.creator : ListTransferView_.lastModifier;
  }

  @Override
  public void restrictPaginationByPending(QueryBuilder<?, ListTransferView> builder, Consumer<String> errorHandler) {
    Join<ListTransferView, ListTransferViewSample> sampleJoin =
        builder.getJoin(builder.getRoot(), ListTransferView_.samples);
    Join<ListTransferView, ListTransferViewLibrary> libraryJoin =
        builder.getJoin(builder.getRoot(), ListTransferView_.libraries);
    Join<ListTransferView, ListTransferViewLibraryAliquot> aliquotJoin =
        builder.getJoin(builder.getRoot(), ListTransferView_.libraryAliquots);
    Join<ListTransferView, ListTransferViewPool> poolJoin =
        builder.getJoin(builder.getRoot(), ListTransferView_.pools);

    CriteriaBuilder criteriaBuilder = builder.getCriteriaBuilder();

    builder.addPredicate(criteriaBuilder.or(
        criteriaBuilder.and(criteriaBuilder.isNotEmpty(builder.getRoot().get(ListTransferView_.samples)),
            criteriaBuilder.or(
                criteriaBuilder.isNull(sampleJoin.get(ListTransferViewSample_.received)),
                criteriaBuilder.isNull(sampleJoin.get(ListTransferViewSample_.qcPassed)))),
        criteriaBuilder.and(criteriaBuilder.isNotEmpty(builder.getRoot().get(ListTransferView_.libraries)),
            criteriaBuilder.or(
                criteriaBuilder.isNull(libraryJoin.get(ListTransferViewLibrary_.received)),
                criteriaBuilder.isNull(libraryJoin.get(ListTransferViewLibrary_.qcPassed)))),
        criteriaBuilder.and(criteriaBuilder.isNotEmpty(builder.getRoot().get(ListTransferView_.libraryAliquots)),
            criteriaBuilder.or(
                criteriaBuilder.isNull(aliquotJoin.get(ListTransferViewLibraryAliquot_.received)),
                criteriaBuilder.isNull(aliquotJoin.get(ListTransferViewLibraryAliquot_.qcPassed)))),
        criteriaBuilder.and(criteriaBuilder.isNotEmpty(builder.getRoot().get(ListTransferView_.pools)),
            criteriaBuilder.or(
                criteriaBuilder.isNull(poolJoin.get(ListTransferViewPool_.received)),
                criteriaBuilder.isNull(poolJoin.get(ListTransferViewPool_.qcPassed))))));
  }

  @Override
  public void restrictPaginationByRecipientGroups(QueryBuilder<?, ListTransferView> builder, Collection<Group> groups,
      Consumer<String> errorHandler) {
    In<Group> inClause = builder.getCriteriaBuilder().in(builder.getRoot().get(ListTransferView_.recipientGroup));
    for (Group group : groups) {
      inClause.value(group);
    }
    builder.addPredicate(inClause);
  }

  @Override
  public void restrictPaginationByTransferType(QueryBuilder<?, ListTransferView> builder, TransferType transferType,
      Consumer<String> errorHandler) {
    switch (transferType) {
      case DISTRIBUTION:
        builder
            .addPredicate(builder.getCriteriaBuilder().isNotNull(builder.getRoot().get(ListTransferView_.recipient)));
        break;
      case INTERNAL:
        builder.addPredicate(
            builder.getCriteriaBuilder().isNotNull(builder.getRoot().get(ListTransferView_.senderGroup)));
        builder.addPredicate(
            builder.getCriteriaBuilder().isNotNull(builder.getRoot().get(ListTransferView_.recipientGroup)));
        break;
      case RECEIPT:
        builder.addPredicate(
            builder.getCriteriaBuilder().isNotNull(builder.getRoot().get(ListTransferView_.senderLab)));
        break;
      default:
        throw new IllegalArgumentException("Unhandled transfer type: " + transferType);
    }
  }

  @Override
  public void restrictPaginationByProject(QueryBuilder<?, ListTransferView> builder, String project,
      Consumer<String> errorHandler) {

    Join<ListTransferView, ListTransferViewSample> sample =
        builder.getJoin(builder.getRoot(), ListTransferView_.samples);
    Join<ListTransferViewSample, ListTransferViewSampleParent> parentSample =
        builder.getJoin(sample, ListTransferViewSample_.sample);
    Join<ListTransferViewSampleParent, ListTransferViewProject> sampleProject =
        builder.getJoin(parentSample, ListTransferViewSampleParent_.project);
    Join<ListTransferView, ListTransferViewLibrary> library =
        builder.getJoin(builder.getRoot(), ListTransferView_.libraries);
    Join<ListTransferViewLibrary, ListTransferViewLibraryParent> parentLibrary =
        builder.getJoin(library, ListTransferViewLibrary_.library);
    Join<ListTransferViewLibraryParent, ListTransferViewSampleParent> librarySample =
        builder.getJoin(parentLibrary, ListTransferViewLibraryParent_.sample);
    Join<ListTransferViewSampleParent, ListTransferViewProject> libraryProject =
        builder.getJoin(librarySample, ListTransferViewSampleParent_.project);
    Join<ListTransferView, ListTransferViewLibraryAliquot> aliquot =
        builder.getJoin(builder.getRoot(), ListTransferView_.libraryAliquots);
    Join<ListTransferViewLibraryAliquot, ListTransferViewLibraryAliquotParent> parentAliquot =
        builder.getJoin(aliquot, ListTransferViewLibraryAliquot_.aliquot);
    Join<ListTransferViewLibraryAliquotParent, ListTransferViewLibraryParent> aliquotLibrary =
        builder.getJoin(parentAliquot, ListTransferViewLibraryAliquotParent_.library);
    Join<ListTransferViewLibraryParent, ListTransferViewSampleParent> aliquotSample =
        builder.getJoin(aliquotLibrary, ListTransferViewLibraryParent_.sample);
    Join<ListTransferViewSampleParent, ListTransferViewProject> aliquotProject =
        builder.getJoin(aliquotSample, ListTransferViewSampleParent_.project);

    builder.addTextRestriction(Arrays.asList(
        sampleProject.get(ListTransferViewProject_.name), sampleProject.get(ListTransferViewProject_.code),
        libraryProject.get(ListTransferViewProject_.name), libraryProject.get(ListTransferViewProject_.code),
        aliquotProject.get(ListTransferViewProject_.name), aliquotProject.get(ListTransferViewProject_.code)), project);
  }

  @Override
  public void restrictPaginationBySubproject(QueryBuilder<?, ListTransferView> builder, String subproject,
      Consumer<String> errorHandler) {

    Join<ListTransferView, ListTransferViewSample> sample =
        builder.getJoin(builder.getRoot(), ListTransferView_.samples);
    Join<ListTransferViewSample, ListTransferViewSampleParent> parentSample =
        builder.getJoin(sample, ListTransferViewSample_.sample);
    Join<ListTransferViewSampleParent, ParentSubproject> sampleSubproject =
        builder.getJoin(parentSample, ListTransferViewSampleParent_.subproject);
    Join<ListTransferView, ListTransferViewLibrary> library =
        builder.getJoin(builder.getRoot(), ListTransferView_.libraries);
    Join<ListTransferViewLibrary, ListTransferViewLibraryParent> parentLibrary =
        builder.getJoin(library, ListTransferViewLibrary_.library);
    Join<ListTransferViewLibraryParent, ListTransferViewSampleParent> librarySample =
        builder.getJoin(parentLibrary, ListTransferViewLibraryParent_.sample);
    Join<ListTransferViewSampleParent, ParentSubproject> librarySubproject =
        builder.getJoin(librarySample, ListTransferViewSampleParent_.subproject);
    Join<ListTransferView, ListTransferViewLibraryAliquot> aliquot =
        builder.getJoin(builder.getRoot(), ListTransferView_.libraryAliquots);
    Join<ListTransferViewLibraryAliquot, ListTransferViewLibraryAliquotParent> parentAliquot =
        builder.getJoin(aliquot, ListTransferViewLibraryAliquot_.aliquot);
    Join<ListTransferViewLibraryAliquotParent, ListTransferViewLibraryParent> aliquotLibrary =
        builder.getJoin(parentAliquot, ListTransferViewLibraryAliquotParent_.library);
    Join<ListTransferViewLibraryParent, ListTransferViewSampleParent> aliquotSample =
        builder.getJoin(aliquotLibrary, ListTransferViewLibraryParent_.sample);
    Join<ListTransferViewSampleParent, ParentSubproject> aliquotSubproject =
        builder.getJoin(aliquotSample, ListTransferViewSampleParent_.subproject);

    builder.addTextRestriction(Arrays.asList(
        sampleSubproject.get(ParentSubproject_.alias),
        librarySubproject.get(ParentSubproject_.alias),
        aliquotSubproject.get(ParentSubproject_.alias)), subproject);
  }

}
