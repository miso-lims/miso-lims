package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;

@Entity
@Table(name = "SopField")
public class SopField implements Serializable {

  private static final long serialVersionUID = 1L;

  public enum FieldType {
    TEXT("Text"), NUMBER("Number"), PERCENTAGE("Percentage");

    private final String label;

    FieldType(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sopFieldId")
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "sopId", nullable = false)
  private Sop sop;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "units", length = 50)
  private String units;

  @Enumerated(EnumType.STRING)
  @Column(name = "fieldType", nullable = false, length = 50)
  private FieldType fieldType;

  public SopField() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Sop getSop() {
    return sop;
  }

  public void setSop(Sop sop) {
    this.sop = sop;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units == null ? null : units.trim();
  }

  public FieldType getFieldType() {
    return fieldType;
  }

  public void setFieldType(FieldType fieldType) {
    this.fieldType = fieldType;
  }

  public boolean isValidValue(String value) {
    if (value == null)
      return true;

    String trimmed = value.trim();
    if (trimmed.isEmpty())
      return true;

    FieldType type = fieldType == null ? FieldType.TEXT : fieldType;

    switch (type) {
      case TEXT:
        return true;

      case NUMBER:
        return isDecimal(trimmed);

      case PERCENTAGE:
        if (!isDecimal(trimmed))
          return false;
        try {
          BigDecimal d = new BigDecimal(trimmed);
          return d.compareTo(BigDecimal.ZERO) >= 0 && d.compareTo(new BigDecimal("100")) <= 0;
        } catch (NumberFormatException e) {
          return false;
        }

      default:
        return true;
    }
  }

  private boolean isDecimal(String s) {
    try {
      new BigDecimal(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof SopField))
      return false;
    SopField other = (SopField) obj;
    return id != null && other.id != null && Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return id != null ? Objects.hash(id) : System.identityHashCode(this);
  }

  @Override
  public String toString() {
    return "SopField [id=" + id + ", name=" + name + ", fieldType=" + fieldType + "]";
  }
}
