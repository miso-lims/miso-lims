package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.RunSopFieldValue.RunSopFieldValueId;


@Entity
@Table(name = "RunSopFieldValue")
@IdClass(RunSopFieldValueId.class)
public class RunSopFieldValue extends SopFieldValue {

  public static class RunSopFieldValueId implements Serializable {
    public static final long serialVersionUID = 1L;
    private Run run;
    private SopField sopField;

    public Run getRun() {
      return run;
    }

    public void setRun(Run run) {
      this.run = run;
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
      RunSopFieldValueId other = (RunSopFieldValueId) obj;
      return Objects.equals(run, other.run) && Objects.equals(sopField, other.sopField);
    }

    @Override
    public int hashCode() {
      return Objects.hash(run, sopField);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "runId")
  private Run run;

  public Run getRun() {
    return run;
  }

  public void setRun(Run run) {
    this.run = run;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    RunSopFieldValue other = (RunSopFieldValue) obj;
    return Objects.equals(run, other.run) && Objects.equals(getSopField(), other.getSopField());
  }

  @Override
  public int hashCode() {
    return Objects.hash(run, getSopField());
  }

}
