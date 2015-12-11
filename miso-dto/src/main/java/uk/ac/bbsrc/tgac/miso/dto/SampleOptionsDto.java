package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Set;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleOptionsDto {
  
  private Set<Subproject> subprojects;
  private Set<TissueOrigin> tissueOrigins;
  private Set<TissueType> tissueTypes;
  private Set<SampleClass> sampleClasses;
  private Set<SamplePurpose> samplePurposes;
  private Set<SampleGroupId> sampleGroups;
  private Set<TissueMaterial> tissueMaterials;
  private Set<QcPassedDetail> qcPassedDetails;
  
  public Set<Subproject> getSubprojects() {
    return subprojects;
  }
  
  public void setSubprojects(Set<Subproject> subprojects) {
    this.subprojects = subprojects;
  }
  
  public Set<TissueOrigin> getTissueOrigins() {
    return tissueOrigins;
  }
  
  public void setTissueOrigins(Set<TissueOrigin> tissueOrigins) {
    this.tissueOrigins = tissueOrigins;
  }
  
  public Set<TissueType> getTissueTypes() {
    return tissueTypes;
  }
  
  public void setTissueTypes(Set<TissueType> tissueTypes) {
    this.tissueTypes = tissueTypes;
  }
  
  public Set<SampleClass> getSampleClasses() {
    return sampleClasses;
  }
  
  public void setSampleClasses(Set<SampleClass> sampleClasses) {
    this.sampleClasses = sampleClasses;
  }
  
  public Set<SamplePurpose> getSamplePurposes() {
    return samplePurposes;
  }
  
  public void setSamplePurposes(Set<SamplePurpose> samplePurposes) {
    this.samplePurposes = samplePurposes;
  }
  
  public Set<SampleGroupId> getSampleGroups() {
    return sampleGroups;
  }
  
  public void setSampleGroups(Set<SampleGroupId> sampleGroups) {
    this.sampleGroups = sampleGroups;
  }
  
  public Set<TissueMaterial> getTissueMaterials() {
    return tissueMaterials;
  }
  
  public void setTissueMaterials(Set<TissueMaterial> tissueMaterials) {
    this.tissueMaterials = tissueMaterials;
  }
  
  public Set<QcPassedDetail> getQcPassedDetails() {
    return qcPassedDetails;
  }
  
  public void setQcPassedDetails(Set<QcPassedDetail> qcPassedDetails) {
    this.qcPassedDetails = qcPassedDetails;
  }
}
