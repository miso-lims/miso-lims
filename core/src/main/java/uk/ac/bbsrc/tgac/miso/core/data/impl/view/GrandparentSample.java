package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;

@Entity
@Immutable
@Table(name = "Sample")
public class GrandparentSample implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long sampleId;

  private String identificationBarcode;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;

  @ManyToOne
  @JoinColumn(name = "sampleClassId")
  private ParentSampleClass parentSampleClass;

  @ManyToOne
  @JoinColumn(name = "parentId")
  private GrandparentSample parentSample;

  @ManyToOne
  @JoinColumn(name = "requisitionId")
  private Requisition requisition;

  @ManyToOne
  @JoinColumn(name = "indexId")
  private SampleIndex index;

  @ManyToOne
  @JoinColumn(name = "scientificNameId")
  private ScientificName scientificName;

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

  public Requisition getRequisition() {
    return requisition;
  }

  public void setRequisition(Requisition requisition) {
    this.requisition = requisition;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public SampleIndex getIndex() {
    return index;
  }

  public void setIndex(SampleIndex index) {
    this.index = index;
  }

  public ScientificName getScientificName() {
    return scientificName;
  }

  public void setScientificName(ScientificName scientificName) {
    this.scientificName = scientificName;
  }

}
