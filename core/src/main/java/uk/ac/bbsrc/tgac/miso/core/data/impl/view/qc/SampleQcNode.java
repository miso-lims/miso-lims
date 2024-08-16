package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;

@Entity
@Table(name = "Sample")
@Immutable
public class SampleQcNode extends DetailedQcNode {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "sampleId")
  private long id;

  private Long parentId;

  @ManyToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "sampleClassId")
  private SampleClass sampleClass;

  private String externalName;

  @Transient
  private List<SampleQcNode> childSamples;

  @Transient
  private List<LibraryQcNode> libraries;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public SampleClass getSampleClass() {
    return sampleClass;
  }

  public void setSampleClass(SampleClass sampleClass) {
    this.sampleClass = sampleClass;
  }

  public String getExternalName() {
    return externalName;
  }

  public void setExternalName(String externalName) {
    this.externalName = externalName;
  }

  @Override
  public String getLabel() {
    return getExternalName() == null ? getAlias() : String.format("%s (%s)", getAlias(), getExternalName());
  }

  public List<SampleQcNode> getChildSamples() {
    if (childSamples == null) {
      childSamples = new ArrayList<>();
    }
    return childSamples;
  }

  public void setChildSamples(List<SampleQcNode> childSamples) {
    this.childSamples = childSamples;
  }

  public List<LibraryQcNode> getLibraries() {
    if (libraries == null) {
      libraries = new ArrayList<>();
    }
    return libraries;
  }

  public void setLibraries(List<LibraryQcNode> libraries) {
    this.libraries = libraries;
  }

  @Override
  public List<? extends DetailedQcNode> getChildren() {
    List<DetailedQcNode> all = new ArrayList<>();
    all.addAll(getChildSamples());
    all.addAll(getLibraries());
    return all;
  }

  @Override
  public QcNodeType getEntityType() {
    return QcNodeType.SAMPLE;
  }

  @Override
  public String getTypeLabel() {
    return getSampleClass() == null ? QcNodeType.SAMPLE.getLabel() : getSampleClass().getAlias();
  }

}
