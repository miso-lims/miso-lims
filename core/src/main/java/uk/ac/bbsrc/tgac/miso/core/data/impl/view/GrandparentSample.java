package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;

@Entity
@Immutable
@Table(name = "Sample")
public class GrandparentSample implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long sampleId;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;

  @ManyToOne
  @JoinColumn(name = "sampleClassId")
  private ParentSampleClass parentSampleClass;

  @ManyToOne
  @JoinColumn(name = "parentId")
  private GrandparentSample parentSample;

  public long getId() {
    return sampleId;
  }

  public void setId(long id) {
    this.sampleId = id;
  }

  public DetailedQcStatus getDetailedQcStatus() {
    return detailedQcStatus;
  }

  public void setDetailedQcStatus(DetailedQcStatus detailedQcStatus) {
    this.detailedQcStatus = detailedQcStatus;
  }

  public ParentSampleClass getParentSampleClass() {
    return parentSampleClass;
  }

  public void setParentSampleClass(ParentSampleClass parentSampleClass) {
    this.parentSampleClass = parentSampleClass;
  }

  public GrandparentSample getParentSample() {
    return parentSample;
  }

  public void setParentSample(GrandparentSample parentSample) {
    this.parentSample = parentSample;
  }

}
