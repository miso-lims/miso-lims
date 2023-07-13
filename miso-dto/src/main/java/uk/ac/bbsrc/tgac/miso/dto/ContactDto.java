package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ContactRole;

public class ContactDto {

  private Long id;
  private String name;
  private String email;
  private ContactRole contactRole;
  private String contactRoleName;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public ContactRole getContactRole() {
    return contactRole;
  }

  public void setContactRole(ContactRole contactRole) {
    this.contactRole = contactRole;
  }

}
