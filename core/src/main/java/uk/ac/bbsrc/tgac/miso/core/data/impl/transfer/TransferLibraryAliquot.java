package uk.ac.bbsrc.tgac.miso.core.data.impl.transfer;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibraryAliquot.TransferLibraryAliquotId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Transfer_LibraryAliquot")
@IdClass(TransferLibraryAliquotId.class)
public class TransferLibraryAliquot extends TransferItem<LibraryAliquot> {

  public static class TransferLibraryAliquotId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Transfer transfer;
    private LibraryAliquot item;

    public Transfer getTransfer() {
      return transfer;
    }

    public void setTransfer(Transfer transfer) {
      this.transfer = transfer;
    }

    public LibraryAliquot getItem() {
      return item;
    }

    public void setItem(LibraryAliquot item) {
      this.item = item;
    }

    @Override
    public int hashCode() {
      return Objects.hash(transfer, item);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          TransferLibraryAliquotId::getTransfer,
          TransferLibraryAliquotId::getItem);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "transferId")
  private Transfer transfer;

  @Id
  @ManyToOne(targetEntity = LibraryAliquot.class)
  @JoinColumn(name = "aliquotId")
  private LibraryAliquot item;

  @Override
  public Transfer getTransfer() {
    return transfer;
  }

  @Override
  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }

  @Override
  public LibraryAliquot getItem() {
    return item;
  }

  @Override
  public void setItem(LibraryAliquot aliquot) {
    this.item = aliquot;
  }

}
