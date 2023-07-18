package uk.ac.bbsrc.tgac.miso.dto;

public class ContactDto {

  private Long id;
  private String name;
  private String email;
  private ContactRoleDto contactRole;

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

  public ContactRoleDto getContactRole() {
    return contactRole;
  }

  public void setContactRole(ContactRoleDto contactRole) {
    this.contactRole = contactRole;
  }

}
