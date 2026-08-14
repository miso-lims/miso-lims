package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSopFieldValue.SampleSopFieldValueId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

@Entity
@Table(name = "SampleSopFieldValue")
@IdClass(SampleSopFieldValueId.class)
public class SampleSopFieldValue extends SopFieldValue {

  public static class SampleSopFieldValueId implements Serializable {
    public static final long serialVersionUID = 1L;
    private Sample sample;
    private SopField sopField;

    public Sample getSample() {
      return sample;
    }

    public void setSample(Sample sample) {
      this.sample = sample;
    }

    public SopField getSopField() {
      return sopField;
    }

    public void setSopField(SopField sopField) {
      this.sopField = sopField;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      SampleSopFieldValueId other = (SampleSopFieldValueId) obj;
      return Objects.equals(sample, other.sample) && Objects.equals(sopField, other.sopField);
    }

    @Override
    public int hashCode() {
      return Objects.hash(sample, sopField);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sampleId")
  private Sample sample;

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    SampleSopFieldValue other = (SampleSopFieldValue) obj;
    return Objects.equals(sample, other.sample) && Objects.equals(getSopField(), other.getSopField());
  }

  @Override
  public int hashCode() {
    return Objects.hash(sample, getSopField());
  }

}
