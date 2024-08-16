package uk.ac.bbsrc.tgac.miso.core.data.impl.view.box;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Immutable
@Table(name = "Transfer")
public class BoxableTransferView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long transferId;

  private String recipient;

  public long getId() {
    return transferId;
  }

  public void setId(long id) {
    this.transferId = id;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipient, transferId);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        BoxableTransferView::getId,
        BoxableTransferView::getRecipient);
  }

}
