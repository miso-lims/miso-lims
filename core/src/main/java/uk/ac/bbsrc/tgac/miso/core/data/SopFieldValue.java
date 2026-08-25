package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@MappedSuperclass
public abstract class SopFieldValue implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "sopFieldId")
  private SopField sopField;

  @Column(length = 255)
  private String value;

  public SopField getSopField() {
    return sopField;
  }

  public void setSopField(SopField sopField) {
    this.sopField = sopField;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj, SopFieldValue::getSopField, SopFieldValue::getValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sopField, value);
  }

}
