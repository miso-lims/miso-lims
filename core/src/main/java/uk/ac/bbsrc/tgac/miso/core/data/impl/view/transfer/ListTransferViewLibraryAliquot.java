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

  @ManyToOne
  @JoinColumn(name = "aliquotId")
  private ListTransferViewLibraryAliquotParent aliquot;

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

  public ListTransferViewLibraryAliquotParent getAliquot() {
    return aliquot;
  }

  public void setAliquot(ListTransferViewLibraryAliquotParent aliquot) {
    this.aliquot = aliquot;
  }

  @Override
  public ListTransferViewProject getProject() {
    return getAliquot().getLibrary().getSample().getProject();
  }

}
