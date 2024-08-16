package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

@Entity(name = "Attachment")
public class FileAttachment implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long attachmentId = UNSAVED_ID;
  private String filename;
  private String path;

  @ManyToOne
  @JoinColumn(name = "categoryId", nullable = true)
  private AttachmentCategory category;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false)
  private User creator;

  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @Override
  public long getId() {
    return attachmentId;
  }

  @Override
  public void setId(long attachmentId) {
    this.attachmentId = attachmentId;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public AttachmentCategory getCategory() {
    return this.category;
  }

  public void setCategory(AttachmentCategory category) {
    this.category = category;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Date getCreationTime() {
    return created;
  }

  public void setCreationTime(Date creationTime) {
    this.created = creationTime;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (attachmentId ^ (attachmentId >>> 32));
    result = prime * result + ((created == null) ? 0 : created.hashCode());
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + ((filename == null) ? 0 : filename.hashCode());
    result = prime * result + ((path == null) ? 0 : path.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FileAttachment other = (FileAttachment) obj;
    if (attachmentId != other.attachmentId)
      return false;
    if (created == null) {
      if (other.created != null)
        return false;
    } else if (!created.equals(other.created))
      return false;
    if (creator == null) {
      if (other.creator != null)
        return false;
    } else if (!creator.equals(other.creator))
      return false;
    if (filename == null) {
      if (other.filename != null)
        return false;
    } else if (!filename.equals(other.filename))
      return false;
    if (path == null) {
      if (other.path != null)
        return false;
    } else if (!path.equals(other.path))
      return false;
    return true;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

}
