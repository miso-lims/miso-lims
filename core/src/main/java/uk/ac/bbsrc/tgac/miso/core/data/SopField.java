package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "SopField")
public class SopField implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  public enum FieldType {
    TEXT, NUMBER
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sopFieldId")
  private long id = UNSAVED_ID;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sopId")
  private Sop sop;

  @Column(length = 255)
  private String name;

  @Column(length = 50)
  private String units;

  @Enumerated(EnumType.STRING)
  private FieldType fieldType;

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public boolean isSaved() {
    return id != UNSAVED_ID;
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

  @Override
  public int hashCode() {
    return LimsUtils.hashCodeByIdFirst(this, name, units, fieldType);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equalsByIdFirst(this, obj,
        SopField::getName,
        SopField::getUnits,
        SopField::getFieldType);
  }
}

