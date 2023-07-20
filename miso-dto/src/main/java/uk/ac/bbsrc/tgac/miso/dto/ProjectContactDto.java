package uk.ac.bbsrc.tgac.miso.dto;

public class ProjectContactDto {
  private long projectId;
  private long contactId;
  private long contactRoleId;
  // fields required for the case where a new contact is made
  private String contactName;
  private String contactEmail;
  private String contactRole;

  public long getProjectId() {
    return projectId;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public long getContactId() {
    return contactId;
  }

  public void setContactId(long contactId) {
    this.contactId = contactId;
  }

  public long getContactRoleId() {
    return contactRoleId;
  }

  public void setContactRoleId(long contactRoleId) {
    this.contactRoleId = contactRoleId;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getContactRole() {
    return contactRole;
  }

  public void setContactRole(String contactRole) {
    this.contactRole = contactRole;
  }
}

