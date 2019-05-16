package uk.ac.bbsrc.tgac.miso.dto;

public class StudyDto {
  private String accession;

  private String alias;
  private String description;
  private Long id;
  private String name;
  private Long projectId;
  private Long studyTypeId;

  public String getAccession() {
    return accession;
  }

  public String getAlias() {
    return alias;
  }

  public String getDescription() {
    return description;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Long getProjectId() {
    return projectId;
  }

  public Long getStudyTypeId() {
    return studyTypeId;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public void setStudyTypeId(Long studyTypeId) {
    this.studyTypeId = studyTypeId;
  }

}
