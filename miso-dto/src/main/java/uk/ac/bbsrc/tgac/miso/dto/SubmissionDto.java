package uk.ac.bbsrc.tgac.miso.dto;

public class SubmissionDto {
  private String accession;
  private String alias;
  private boolean completed;
  private String creationDate;
  private String description;
  private long id;
  private String submittedDate;
  private String title;
  private boolean verified;

  public String getAccession() {
    return accession;
  }

  public String getAlias() {
    return alias;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public String getDescription() {
    return description;
  }

  public long getId() {
    return id;
  }

  public String getSubmittedDate() {
    return submittedDate;
  }

  public String getTitle() {
    return title;
  }

  public boolean isCompleted() {
    return completed;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setSubmittedDate(String submittedDate) {
    this.submittedDate = submittedDate;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

}
