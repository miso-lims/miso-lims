package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferItem;
import uk.ac.bbsrc.tgac.miso.persistence.TransferStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTransferDao extends HibernateSaveDao<Transfer> implements TransferStore {

  public HibernateTransferDao() {
    super(Transfer.class);
  }

  @Override
  public boolean anyPendingForGroups(Collection<Group> groups) throws IOException {
    if (groups == null || groups.isEmpty()) {
      return false;
    }
    BigInteger pendingGroups = (BigInteger) currentSession()
        .createSQLQuery("SELECT COUNT(groupId) FROM PendingTransferGroupView WHERE groupId IN (:ids)")
        .setParameterList("ids", groups.stream().map(Group::getId).collect(Collectors.toSet()))
        .uniqueResult();
    return pendingGroups.compareTo(BigInteger.ZERO) > 0;
  }

  @Override
  public <T extends TransferItem<?>> void deleteTransferItem(T item) throws IOException {
    currentSession().delete(item);
  }

  @Override
  public List<Transfer> listByProperties(Lab sender, Group recipient, Project project, Date transferDate) throws IOException {
    @SuppressWarnings("unchecked")
    List<Transfer> results = currentSession().createCriteria(Transfer.class)
        .createAlias("sampleTransfers", "sampleTransfer", JoinType.LEFT_OUTER_JOIN)
        .createAlias("sampleTransfer.item", "sample", JoinType.LEFT_OUTER_JOIN)
        .createAlias("libraryTransfers", "libraryTransfer", JoinType.LEFT_OUTER_JOIN)
        .createAlias("libraryTransfer.item", "library", JoinType.LEFT_OUTER_JOIN)
        .createAlias("library.sample", "librarySample", JoinType.LEFT_OUTER_JOIN)
        .add(Restrictions.eq("senderLab", sender))
        .add(Restrictions.eq("recipientGroup", recipient))
        .add(Restrictions.eq("transferDate", transferDate))
        .add(Restrictions.or(Restrictions.eq("sample.project", project), Restrictions.eq("librarySample.project", project)))
        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
        .list();
    return results;
  }

  @Override
  public void detachEntity(Boxable object) {
    currentSession().evict(object);
  }

}
