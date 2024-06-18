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
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryAliquotChangeLog;

@Entity
@Immutable
@Table(name = "LibraryAliquot")
public class LibraryAliquotBoxableView extends BoxableView {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  private long aliquotId;

  @OneToOne
  @PrimaryKeyJoinColumn
  private LibraryAliquotBoxablePositionView boxPosition;

  @ManyToMany
  @JoinTable(name = "Transfer_LibraryAliquot", joinColumns = @JoinColumn(name = "aliquotId"),
      inverseJoinColumns = @JoinColumn(name = "transferId"))
  private Set<BoxableTransferView> transfers;

  @Override
  public EntityType getEntityType() {
    return EntityType.LIBRARY_ALIQUOT;
  }

  @Override
  public long getId() {
    return aliquotId;
  }

  @Override
  public void setId(long id) {
    this.aliquotId = id;
  }

  @Override
  public String getLocationBarcode() {
    return null;
  }

  @Override
  public LibraryAliquotBoxablePositionView getBoxablePosition() {
    return boxPosition;
  }

  public void setBoxPosition(LibraryAliquotBoxablePositionView boxPosition) {
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
    return aliquotId != UNSAVED_ID;
  }

  @Override
  public ChangeLog makeChangeLog() {
    LibraryAliquotChangeLog change = new LibraryAliquotChangeLog();
    change.setLibraryAliquot(this);
    return change;
  }

}
