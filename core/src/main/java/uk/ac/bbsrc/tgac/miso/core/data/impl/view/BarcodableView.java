package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;

@Entity
@Immutable
@Table(name = "BarcodableView")
public class BarcodableView implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private BarcodableId id;

  private String name;
  private String alias;
  private String identificationBarcode;

  public BarcodableId getId() {
    return id;
  }

  public void setId(BarcodableId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @Embeddable
  public static class BarcodableId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private EntityType targetType;
    private long targetId;

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
      return new HashCodeBuilder(71, 49).append(targetId).append(targetType).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      BarcodableId other = (BarcodableId) obj;
      return new EqualsBuilder().append(targetId, other.targetId).append(targetType, other.targetType).isEquals();
    }
  }
}
