package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Set;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleOptionsDto {

  private Set<SubprojectDto> subprojects;
  private Set<TissueOriginDto> tissueOrigins;
  private Set<TissueTypeDto> tissueTypes;
  private Set<SampleClassDto> sampleClasses;
  private Set<SamplePurposeDto> samplePurposes;
  private Set<SampleGroupDto> sampleGroups;
  private Set<TissueMaterialDto> tissueMaterials;
  private Set<QcPassedDetailDto> qcPassedDetails;
  private Set<SampleValidRelationshipDto> sampleValidRelationships;
  private Set<InstituteDto> institutes;
  private Set<LabDto> labs;

  public Set<SubprojectDto> getSubprojectsDtos() {
    return subprojects;
  }

  public void setSubprojectsDtos(Set<SubprojectDto> subprojects) {
    this.subprojects = subprojects;
  }

  public Set<TissueOriginDto> getTissueOriginsDtos() {
    return tissueOrigins;
  }

  public void setTissueOriginsDtos(Set<TissueOriginDto> tissueOrigins) {
    this.tissueOrigins = tissueOrigins;
  }

  public Set<TissueTypeDto> getTissueTypesDtos() {
    return tissueTypes;
  }

  public void setTissueTypesDtos(Set<TissueTypeDto> tissueTypes) {
    this.tissueTypes = tissueTypes;
  }

  public Set<SampleClassDto> getSampleClassesDtos() {
    return sampleClasses;
  }

  public void setSampleClassesDtos(Set<SampleClassDto> sampleClasses) {
    this.sampleClasses = sampleClasses;
  }

  public Set<SamplePurposeDto> getSamplePurposesDtos() {
    return samplePurposes;
  }

  public void setSamplePurposesDtos(Set<SamplePurposeDto> samplePurposes) {
    this.samplePurposes = samplePurposes;
  }

  public Set<SampleGroupDto> getSampleGroupsDtos() {
    return sampleGroups;
  }

  public void setSampleGroupsDtos(Set<SampleGroupDto> sampleGroups) {
    this.sampleGroups = sampleGroups;
  }

  public Set<TissueMaterialDto> getTissueMaterialsDtos() {
    return tissueMaterials;
  }

  public void setTissueMaterialsDtos(Set<TissueMaterialDto> tissueMaterials) {
    this.tissueMaterials = tissueMaterials;
  }

  public Set<QcPassedDetailDto> getQcPassedDetailsDtos() {
    return qcPassedDetails;
  }

  public void setQcPassedDetailsDtos(Set<QcPassedDetailDto> qcPassedDetails) {
    this.qcPassedDetails = qcPassedDetails;
  }

  public Set<SampleValidRelationshipDto> getSampleValidRelationshipsDtos() {
    return sampleValidRelationships;
  }

  public void setSampleValidRelationshipsDtos(Set<SampleValidRelationshipDto> sampleValidRelationships) {
    this.sampleValidRelationships = sampleValidRelationships;
  }

  public Set<InstituteDto> getInstitutesDtos() {
    return institutes;
  }

  public void setInstitutesDtos(Set<InstituteDto> institutes) {
    this.institutes = institutes;
  }
  
  public Set<LabDto> getLabsDtos() {
    return labs;
  }
  
  public void setLabsDtos(Set<LabDto> labs) {
    this.labs = labs;
  }

}
