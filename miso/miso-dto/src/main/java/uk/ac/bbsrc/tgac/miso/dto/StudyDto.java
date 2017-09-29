package uk.ac.bbsrc.tgac.miso.dto;

public class StudyDto {
  private String accession;

  private String alias;
  private String description;
  private long id;
  private String name;
  private long projectId;
  private long studyTypeId;

  public String getAccession() {
    return accession;
  }

  public String getAlias() {
    return alias;
  }

  public String getDescription() {
    return description;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public long getProjectId() {
    return projectId;
  }

  public long getStudyTypeId() {
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

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public void setStudyTypeId(long studyTypeId) {
    this.studyTypeId = studyTypeId;
  }

}
