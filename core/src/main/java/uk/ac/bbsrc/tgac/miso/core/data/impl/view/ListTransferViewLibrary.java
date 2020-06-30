package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

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

}
