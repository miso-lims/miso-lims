package uk.ac.bbsrc.tgac.miso.dto;

public class RequisitionContactDto {
  private long requisitionId;
  private long contactId;
  private long contactRoleId;
  // fields required for the case where a new contact is made
  private String contactName;
  private String contactEmail;
  private String contactRole;

  public long getRequisitionId() {
    return requisitionId;
  }

  public void setRequisitionId(long requisitionId) {
    this.requisitionId = requisitionId;
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


