package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.SopField;

@Entity
@Table(name = "SopField")
public class SopFieldImpl implements SopField, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sopFieldId")
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sopId", nullable = false)
  private Sop sop;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(length = 50)
  private String units;

  @Column(nullable = false, length = 20)
  private String fieldType;

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public Sop getSop() {
    return sop;
  }

  @Override
  public void setSop(Sop sop) {
    this.sop = sop;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getUnits() {
    return units;
  }

  @Override
  public void setUnits(String units) {
    this.units = units;
  }

  @Override
  public String getFieldType() {
    return fieldType;
  }

  @Override
  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof SopField))
      return false;

    SopField other = (SopField) obj;

    if (getId() != 0L && other.getId() != 0L) {
      return getId() == other.getId();
    }

    return false;
  }

  @Override
  public int hashCode() {
    return getId() != 0L ? Long.hashCode(getId()) : System.identityHashCode(this);
  }
}
