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
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog;

@Entity
@Immutable
@Table(name = "Library")
public class LibraryBoxableView extends BoxableView {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  private long libraryId;

  private String locationBarcode;

  @OneToOne
  @PrimaryKeyJoinColumn
  private LibraryBoxablePositionView boxPosition;

  @ManyToMany
  @JoinTable(name = "Transfer_Library", joinColumns = @JoinColumn(name = "libraryId"),
      inverseJoinColumns = @JoinColumn(name = "transferId"))
  private Set<BoxableTransferView> transfers;

  @Override
  public EntityType getEntityType() {
    return EntityType.LIBRARY;
  }

  @Override
  public long getId() {
    return libraryId;
  }

  @Override
  public void setId(long id) {
    this.libraryId = id;
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  @Override
  public BoxablePositionView getBoxablePosition() {
    return boxPosition;
  }

  public void setBoxPosition(LibraryBoxablePositionView boxPosition) {
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
    return libraryId != UNSAVED_ID;
  }

  @Override
  public ChangeLog makeChangeLog() {
    LibraryChangeLog change = new LibraryChangeLog();
    change.setLibrary(this);
    return change;
  }

}
