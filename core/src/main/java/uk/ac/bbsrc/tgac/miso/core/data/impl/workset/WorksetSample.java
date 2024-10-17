package uk.ac.bbsrc.tgac.miso.core.data.impl.workset;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample.WorksetSampleId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Workset_Sample")
@IdClass(WorksetSampleId.class)
public class WorksetSample extends WorksetItem<Sample> {

  public static class WorksetSampleId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Workset workset;
    private Sample item;

    public Workset getWorkset() {
      return workset;
    }

    public void setWorkset(Workset workset) {
      this.workset = workset;
    }

    public Sample getItem() {
      return item;
    }

    public void setItem(Sample item) {
      this.item = item;
    }

    @Override
    public int hashCode() {
      return Objects.hash(workset, item);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          WorksetSampleId::getWorkset,
          WorksetSampleId::getItem);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "worksetId")
  private Workset workset;

  @Id
  @ManyToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sampleId")
  private Sample item;

  @Override
  public Workset getWorkset() {
    return workset;
  }

  @Override
  public void setWorkset(Workset workset) {
    this.workset = workset;
  }

  @Override
  public Sample getItem() {
    return item;
  }

  @Override
  public void setItem(Sample item) {
    this.item = item;
  }

}
