package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification_;
import uk.ac.bbsrc.tgac.miso.persistence.TransferNotificationStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTransferNotificationDao extends HibernateSaveDao<TransferNotification>
    implements TransferNotificationStore {

  private static final long ONE_MINUTE = 60000;

  public HibernateTransferNotificationDao() {
    super(TransferNotification.class);
  }

  @Override
  public List<TransferNotification> listByTransfer(Transfer transfer) throws IOException {
    QueryBuilder<TransferNotification, TransferNotification> builder =
        new QueryBuilder<>(currentSession(), TransferNotification.class, TransferNotification.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(TransferNotification_.transfer), transfer));
    return builder.getResultList();
  }

  @Override
  public List<TransferNotification> listPending(int holdMinutes, int limit) throws IOException {
    if (limit < 1) {
      return Collections.emptyList();
    }
    Date time = new Date(Calendar.getInstance().getTimeInMillis() - holdMinutes * ONE_MINUTE);

    QueryBuilder<TransferNotification, TransferNotification> builder =
        new QueryBuilder<>(currentSession(), TransferNotification.class, TransferNotification.class);
    builder.addPredicate(builder.getCriteriaBuilder().isNull(builder.getRoot().get(TransferNotification_.sentTime)));
    builder.addPredicate(
        builder.getCriteriaBuilder().lessThanOrEqualTo(builder.getRoot().get(TransferNotification_.created), time));
    builder.addSort(builder.getRoot().get(TransferNotification_.created), true);
    return builder.getResultList(limit, 0);
  }

  @Override
  public List<TransferNotification> listFailurePending(int limit) throws IOException {
    if (limit < 1) {
      return Collections.emptyList();
    }

    QueryBuilder<TransferNotification, TransferNotification> builder =
        new QueryBuilder<>(currentSession(), TransferNotification.class, TransferNotification.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(TransferNotification_.sendSuccess), false));
    builder.addPredicate(
        builder.getCriteriaBuilder().isNull(builder.getRoot().get(TransferNotification_.failureSentTime)));
    Join<TransferNotification, UserImpl> creator = builder.getJoin(builder.getRoot(), TransferNotification_.creator);
    builder.addPredicate(builder.getCriteriaBuilder().isNotNull(creator.get(UserImpl_.email)));
    builder.addSort(builder.getRoot().get(TransferNotification_.sentTime), true);
    return builder.getResultList(limit, 0);
  }

  @Override
  public void delete(TransferNotification notification) throws IOException {
    currentSession().remove(notification);
  }

}
