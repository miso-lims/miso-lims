package uk.ac.bbsrc.tgac.miso.core.data.impl.view.box;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolChangeLog;

@Entity
@Immutable
@Table(name = "Pool")

public class PoolBoxableView extends BoxableView {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  private long poolId;

  @OneToOne
  @PrimaryKeyJoinColumn
  private PoolBoxablePositionView boxPosition;

  @ManyToMany
  @JoinTable(name = "Transfer_Pool", joinColumns = @JoinColumn(name = "poolId"),
      inverseJoinColumns = @JoinColumn(name = "transferId"))
  private Set<BoxableTransferView> transfers;

  @Override
  public EntityType getEntityType() {
    return EntityType.POOL;
  }

  @Override
  public long getId() {
    return poolId;
  }

  @Override
  public void setId(long id) {
    this.poolId = id;
  }

  @Override
  public String getLocationBarcode() {
    return null;
  }

  @Override
  public PoolBoxablePositionView getBoxablePosition() {
    return boxPosition;
  }

  public void setBoxPosition(PoolBoxablePositionView boxPosition) {
    this.boxPosition = boxPosition;
  }

  public Set<BoxableTransferView> getTransfers() {
    if (transfers == null) {
      transfers = new HashSet<>();
    }
    return transfers;
  }

  @Override
  public boolean isDistributed() {
    return getTransfers().stream().anyMatch(x -> x.getRecipient() != null);
  }

  @Override
  public boolean isSaved() {
    return poolId != UNSAVED_ID;
  }

  @Override
  public ChangeLog makeChangeLog() {
    PoolChangeLog change = new PoolChangeLog();
    change.setPool(this);
    return change;
  }

}
