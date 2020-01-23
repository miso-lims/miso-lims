package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.TransferType;
import uk.ac.bbsrc.tgac.miso.persistence.ListTransferViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListTransferViewDao implements ListTransferViewDao, HibernatePaginatedDataSource<ListTransferView> {

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
  public String propertyForId() {
    return "transferId";
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
    criteria.add(Restrictions.or(Restrictions.gt("receiptPending", 0), Restrictions.gt("qcPending", 0)));
  }

  @Override
  public void restrictPaginationByRecipientGroups(Criteria criteria, Collection<Group> groups, Consumer<String> errorHandler) {
    criteria.add(Restrictions.in("recipientGroup", groups));
  }

  @Override
  public void restrictPaginationByTransferType(Criteria criteria, TransferType transferType, Consumer<String> errorHandler) {
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

}
