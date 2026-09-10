package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingControlType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;

@Entity
@Immutable
@Table(name = "Sample")
public class ParentSample implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long sampleId;

  private String name;
  private String alias;
  private String accession;

  @ManyToOne
  @JoinColumn(name = "sequencingControlTypeId")
  private SequencingControlType sequencingControlType;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;

  @ManyToOne
  @JoinColumn(name = "project_projectId")
  private ParentProject parentProject;

  @ManyToOne
  @JoinColumn(name = "subprojectId")
  private ParentSubproject parentSubproject;

  @ManyToOne
  @JoinColumn(name = "sampleClassId")
  private ParentSampleClass parentSampleClass;

  @OneToOne
  @PrimaryKeyJoinColumn
  @NotFound(action = NotFoundAction.IGNORE) // For plain samples
  private ParentAttributes parentAttributes;

  @ManyToOne
  @JoinColumn(name = "parentId")
  private GrandparentSample parentSample;

  public long getId() {
    return sampleId;
  }

  public void setId(long id) {
    this.sampleId = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public SequencingControlType getSequencingControlType() {
    return sequencingControlType;
  }

  public void setSequencingControlType(SequencingControlType sequencingControlType) {
    this.sequencingControlType = sequencingControlType;
  }

  public DetailedQcStatus getDetailedQcStatus() {
    return detailedQcStatus;
  }

  public void setDetailedQcStatus(DetailedQcStatus detailedQcStatus) {
    this.detailedQcStatus = detailedQcStatus;
  }

  public ParentProject getParentProject() {
    return parentProject;
  }

  public void setParentProject(ParentProject parentProject) {
    this.parentProject = parentProject;
  }

  public ParentSubproject getParentSubproject() {
    return parentSubproject;
  }

  public void setParentSubproject(ParentSubproject parentSubproject) {
    this.parentSubproject = parentSubproject;
  }

  public ParentSampleClass getParentSampleClass() {
    return parentSampleClass;
  }

  public void setParentSampleClass(ParentSampleClass parentSampleClass) {
    this.parentSampleClass = parentSampleClass;
  }

  public ParentAttributes getParentAttributes() {
    return parentAttributes;
  }

  public void setParentAttributes(ParentAttributes parentAttributes) {
    this.parentAttributes = parentAttributes;
  }

  public ParentIdentityAttributes getIdentityAttributes() {
    return parentAttributes == null ? null : parentAttributes.getIdentityAttributes();
  }

  public ParentTissueAttributes getTissueAttributes() {
    return parentAttributes == null ? null : parentAttributes.getTissueAttributes();
  }

  public GrandparentSample getParentSample() {
    return parentSample;
  }

  public void setParentSample(GrandparentSample parentSample) {
    this.parentSample = parentSample;
  }

}
