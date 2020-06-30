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
@Table(name = "Transfer_LibraryAliquot")
@Immutable
public class ListTransferViewLibraryAliquot extends ListTransferViewItem {

  public static class ListTransferViewLibraryAliquotId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Transfer transfer;
    private long aliquotId;

    public Transfer getTransfer() {
      return transfer;
    }

    public void setTransfer(Transfer transfer) {
      this.transfer = transfer;
    }

    public long getAliquotId() {
      return aliquotId;
    }

    public void setAliquotId(long libraryId) {
      this.aliquotId = libraryId;
    }

    @Override
    public int hashCode() {
      return Objects.hash(transfer, aliquotId);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          ListTransferViewLibraryAliquotId::getTransfer,
          ListTransferViewLibraryAliquotId::getAliquotId);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "transferId")
  private Transfer transfer;

  @Id
  private long aliquotId;

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
    return aliquotId;
  }

  @Override
  public void setItemId(long id) {
    this.aliquotId = id;
  }

}
