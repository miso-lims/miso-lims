package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;

public interface Attachable extends Identifiable {

  public List<FileAttachment> getAttachments();

  public void setAttachments(List<FileAttachment> attachments);

  /**
   * @return the entity type String used in the URL for downloading attachments
   */
  public String getAttachmentsTarget();

  public List<FileAttachment> getPendingAttachmentDeletions();

  public void setPendingAttachmentDeletions(List<FileAttachment> pendingAttachmentDeletions);

}
