package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification;
import uk.ac.bbsrc.tgac.miso.persistence.TransferNotificationStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTransferNotificationDao extends HibernateSaveDao<TransferNotification> implements TransferNotificationStore {

  private static final long ONE_MINUTE = 60000;

  public HibernateTransferNotificationDao() {
    super(TransferNotification.class);
  }

  @Override
  public List<TransferNotification> listByTransfer(Transfer transfer) throws IOException {
    @SuppressWarnings("unchecked")
    List<TransferNotification> results = currentSession().createCriteria(TransferNotification.class)
        .add(Restrictions.eq("transfer", transfer))
        .list();
    return results;
  }

  @Override
  public List<TransferNotification> listPending(int holdMinutes, int limit) throws IOException {
    if (limit < 1) {
      return Collections.emptyList();
    }
    Date time = new Date(Calendar.getInstance().getTimeInMillis() - holdMinutes * ONE_MINUTE);

    @SuppressWarnings("unchecked")
    List<TransferNotification> results = currentSession().createCriteria(TransferNotification.class)
        .add(Restrictions.isNull("sentTime"))
        .add(Restrictions.le("created", time))
        .addOrder(Order.asc("created"))
        .setFetchSize(limit)
        .list();
    return results;
  }

  @Override
  public List<TransferNotification> listFailurePending(int limit) throws IOException {
    if (limit < 1) {
      return Collections.emptyList();
    }
    @SuppressWarnings("unchecked")
    List<TransferNotification> results = currentSession().createCriteria(TransferNotification.class)
        .add(Restrictions.eq("sendSuccess", false))
        .add(Restrictions.isNull("failureSentTime"))
        .createAlias("creator", "creator")
        .add(Restrictions.isNotNull("creator.email"))
        .addOrder(Order.asc("sentTime"))
        .setFetchSize(limit)
        .list();
    return results;
  }

  @Override
  public void delete(TransferNotification notification) throws IOException {
    currentSession().delete(notification);
  }

}
