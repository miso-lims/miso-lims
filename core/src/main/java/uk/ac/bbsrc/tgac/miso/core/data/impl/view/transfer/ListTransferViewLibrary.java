package uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Transfer_Library")
@Immutable
public class ListTransferViewLibrary extends ListTransferViewItem {

  public static class ListTransferViewLibraryId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Transfer transfer;
    private long libraryId;

    public Transfer getTransfer() {
      return transfer;
    }

    public void setTransfer(Transfer transfer) {
      this.transfer = transfer;
    }

    public long getLibraryId() {
      return libraryId;
    }

    public void setLibraryId(long libraryId) {
      this.libraryId = libraryId;
    }

    @Override
    public int hashCode() {
      return Objects.hash(transfer, libraryId);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          ListTransferViewLibraryId::getTransfer,
          ListTransferViewLibraryId::getLibraryId);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "transferId")
  private Transfer transfer;

  @Id
  private long libraryId;

  @ManyToOne
  @JoinColumn(name = "libraryId")
  private ListTransferViewLibraryParent library;

  @Override
  public Transfer getTransfer() {
    return transfer;
  }

  @Override
  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }

  @Override
  public long getItemId() {
    return libraryId;
  }

  @Override
  public void setItemId(long id) {
    this.libraryId = id;
  }

  public ListTransferViewLibraryParent getLibrary() {
    return library;
  }

  public void setLibrary(ListTransferViewLibraryParent library) {
    this.library = library;
  }

  @Override
  public ListTransferViewProject getProject() {
    return getLibrary().getSample().getProject();
  }

}
