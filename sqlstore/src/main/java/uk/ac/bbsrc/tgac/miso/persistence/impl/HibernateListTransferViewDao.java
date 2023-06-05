package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.TransferType;
import uk.ac.bbsrc.tgac.miso.persistence.ListTransferViewDao;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListTransferViewDao
    implements ListTransferViewDao, HibernatePaginatedDataSource<ListTransferView> {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public String getFriendlyName() {
    return "Transfer";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends ListTransferView> getRealClass() {
    return ListTransferView.class;
  }

  @Override
  public String[] getSearchProperties() {
    return new String[0];
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
      case CREATE:
      case RECEIVE:
        return "transferTime";
      default:
        return null;
    }
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByPending(Criteria criteria, Consumer<String> errorHandler) {
    criteria.createAlias("samples", "sample", JoinType.LEFT_OUTER_JOIN)
        .createAlias("libraries", "library", JoinType.LEFT_OUTER_JOIN)
        .createAlias("libraryAliquots", "aliquot", JoinType.LEFT_OUTER_JOIN)
        .createAlias("pools", "pool", JoinType.LEFT_OUTER_JOIN)
        .add(Restrictions.or(
            Restrictions.and(Restrictions.isNotEmpty("samples"),
                Restrictions.or(Restrictions.isNull("sample.received"), Restrictions.isNull("sample.qcPassed"))),
            Restrictions.and(Restrictions.isNotEmpty("libraries"),
                Restrictions.or(Restrictions.isNull("library.received"), Restrictions.isNull("library.qcPassed"))),
            Restrictions.and(Restrictions.isNotEmpty("libraryAliquots"),
                Restrictions.or(Restrictions.isNull("aliquot.received"), Restrictions.isNull("aliquot.qcPassed"))),
            Restrictions.and(Restrictions.isNotEmpty("pools"),
                Restrictions.or(Restrictions.isNull("pool.received"), Restrictions.isNull("pool.qcPassed")))));
  }

  @Override
  public void restrictPaginationByRecipientGroups(Criteria criteria, Collection<Group> groups,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.in("recipientGroup", groups));
  }

  @Override
  public void restrictPaginationByTransferType(Criteria criteria, TransferType transferType,
      Consumer<String> errorHandler) {
    switch (transferType) {
      case DISTRIBUTION:
        criteria.add(Restrictions.isNotNull("recipient"));
        break;
      case INTERNAL:
        criteria.add(Restrictions.isNotNull("senderGroup"))
            .add(Restrictions.isNotNull("recipientGroup"));
        break;
      case RECEIPT:
        criteria.add(Restrictions.isNotNull("senderLab"));
        break;
      default:
        throw new IllegalArgumentException("Unhandled transfer type: " + transferType);
    }
  }

  @Override
  public void restrictPaginationByProject(Criteria criteria, String project, Consumer<String> errorHandler) {
    criteria.createAlias("samples", "sample", JoinType.LEFT_OUTER_JOIN)
        .createAlias("sample.sample", "parentSample", JoinType.LEFT_OUTER_JOIN)
        .createAlias("parentSample.project", "sampleProject", JoinType.LEFT_OUTER_JOIN)
        .createAlias("libraries", "library", JoinType.LEFT_OUTER_JOIN)
        .createAlias("library.library", "parentLibrary", JoinType.LEFT_OUTER_JOIN)
        .createAlias("parentLibrary.sample", "librarySample", JoinType.LEFT_OUTER_JOIN)
        .createAlias("librarySample.project", "libraryProject", JoinType.LEFT_OUTER_JOIN)
        .createAlias("libraryAliquots", "aliquot", JoinType.LEFT_OUTER_JOIN)
        .createAlias("aliquot.aliquot", "parentAliquot", JoinType.LEFT_OUTER_JOIN)
        .createAlias("parentAliquot.library", "aliquotLibrary", JoinType.LEFT_OUTER_JOIN)
        .createAlias("aliquotLibrary.sample", "aliquotSample", JoinType.LEFT_OUTER_JOIN)
        .createAlias("aliquotSample.project", "aliquotProject", JoinType.LEFT_OUTER_JOIN)
        .add(DbUtils.textRestriction(project, "sampleProject.name", "sampleProject.code", "libraryProject.name",
            "libraryProject.code", "aliquotProject.name", "aliquotProject.code"));
  }

}
