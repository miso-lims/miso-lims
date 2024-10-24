package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;

@Embeddable
public class BoxableId implements Serializable {
  private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  private EntityType targetType;
  private long targetId;

  public BoxableId() {
    // default constructor
  }

  public BoxableId(EntityType targetType, long targetId) {
    this.targetType = targetType;
    this.targetId = targetId;
  }

  public EntityType getTargetType() {
    return targetType;
  }

  public void setTargetType(EntityType targetType) {
    this.targetType = targetType;
  }

  public long getTargetId() {
    return targetId;
  }

  public void setTargetId(long targetId) {
    this.targetId = targetId;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(71, 49)
        .append(targetId)
        .append(targetType)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BoxableId other = (BoxableId) obj;
    return new EqualsBuilder()
        .append(targetId, other.targetId)
        .append(targetType, other.targetType)
        .isEquals();
  }

}
