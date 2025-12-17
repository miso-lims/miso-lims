package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
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

  @Column(nullable = false, length = 255)
  private String name;

  @Column(length = 50)
  private String units;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private FieldType fieldType = FieldType.TEXT;

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
    this.name = name;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public FieldType getFieldType() {
    return fieldType;
  }

  public void setFieldType(FieldType fieldType) {
    this.fieldType = fieldType;
  }

  /**
   * Validates a value against this field's type
   */
  public boolean isValidValue(String value) {
    if (value == null || value.trim().isEmpty()) {
      return true;
    }

    switch (fieldType) {
      case TEXT:
        return true;
      case NUMBER:
        try {
          Double.parseDouble(value);
          return true;
        } catch (NumberFormatException e) {
          return false;
        }
      case PERCENTAGE:
        try {
          double percentage = Double.parseDouble(value);
          return percentage >= 0 && percentage <= 100;
        } catch (NumberFormatException e) {
          return false;
        }
      default:
        return false;
    }
  }

  @Override
  public int hashCode() {
    // Use business key (name + fieldType) instead of id for unsaved entities
    if (id != null) {
      return Objects.hash(id);
    }
    return Objects.hash(name, fieldType);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    SopField other = (SopField) obj;

    // If both have IDs, compare by ID
    if (id != null && other.id != null) {
      return Objects.equals(id, other.id);
    }

    // For unsaved entities, use business key (name + fieldType)
    return Objects.equals(name, other.name) &&
        Objects.equals(fieldType, other.fieldType);
  }

  @Override
  public String toString() {
    return "SopField [id=" + id + ", name=" + name + ", fieldType=" + fieldType + "]";
  }
}
