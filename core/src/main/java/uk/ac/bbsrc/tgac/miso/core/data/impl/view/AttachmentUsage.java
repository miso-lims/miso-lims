package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Immutable
public class AttachmentUsage {

  @Id
  private long attachmentId;

  private long usage;

  public long getAttachmentId() {
    return attachmentId;
  }

  public void setAttachmentId(long attachmentId) {
    this.attachmentId = attachmentId;
  }

  public long getUsage() {
    return usage;
  }

  public void setUsage(long usage) {
    this.usage = usage;
  }

}
