package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferItem;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer_;
import uk.ac.bbsrc.tgac.miso.persistence.TransferStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTransferDao extends HibernateSaveDao<Transfer> implements TransferStore {

  public HibernateTransferDao() {
    super(Transfer.class);
  }

  @Override
  public long countPendingForGroups(Collection<Group> groups) throws IOException {
    if (groups == null || groups.isEmpty()) {
      return 0L;
    }
    BigDecimal pendingGroups = currentSession()
        .createNativeQuery("SELECT COALESCE(SUM(transfers), 0) FROM PendingTransferGroupView WHERE groupId IN (:ids)",
            BigDecimal.class)
        .setParameterList("ids", groups.stream().map(Group::getId).collect(Collectors.toSet()))
        .uniqueResult();
    return pendingGroups.longValueExact();
  }

  @Override
  public <T extends TransferItem<?>> void deleteTransferItem(T item) throws IOException {
    currentSession().remove(item);
  }

  @Override
  public List<Transfer> listByProperties(Lab sender, Group recipient, Project project, Date transferTime)
      throws IOException {
    QueryBuilder<Transfer, Transfer> builder =
        new QueryBuilder<>(currentSession(), Transfer.class, Transfer.class);
    Join<Transfer, TransferSample> sampleTransfer = builder.getJoin(builder.getRoot(), Transfer_.sampleTransfers);
    Join<TransferSample, SampleImpl> sample = builder.getJoin(sampleTransfer, TransferSample_.item);
    Join<Transfer, TransferLibrary> libraryTransfer = builder.getJoin(builder.getRoot(), Transfer_.libraryTransfers);
    Join<TransferLibrary, LibraryImpl> library = builder.getJoin(libraryTransfer, TransferLibrary_.item);
    Join<LibraryImpl, SampleImpl> librarySample = builder.getJoin(library, LibraryImpl_.sample);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Transfer_.senderLab), sender));
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Transfer_.recipientGroup), recipient));
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Transfer_.transferTime), transferTime));
    builder.addPredicate(
        builder.getCriteriaBuilder().or(builder.getCriteriaBuilder().equal(sample.get(SampleImpl_.project), project),
            builder.getCriteriaBuilder().equal(librarySample.get(SampleImpl_.project), project)));
    return builder.getResultList();
  }

  @Override
  public void detachEntity(Boxable object) {
    currentSession().evict(object);
  }

}
