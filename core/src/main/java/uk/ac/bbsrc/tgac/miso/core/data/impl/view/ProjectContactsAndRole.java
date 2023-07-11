package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ContactRole;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;

@Entity
@Table(name = "Project_Contact_and_Role")
public class ProjectContactsAndRole implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne(targetEntity = ProjectImpl.class)
  @JoinColumn(name = "projectId")
  private Project project;

  @Id
  @ManyToOne(targetEntity = Contact.class)
  @JoinColumn(name = "contactId")
  private Contact contact;

  @Id
  @ManyToOne(targetEntity = ContactRole.class)
  @JoinColumn(name = "contactRoleId")
  private ContactRole contactRole;

  public ProjectContactsAndRole() {
    // Default constructor
  }

  public ProjectContactsAndRole(Project project, Contact contact, ContactRole contactRole) {
    this.project = project;
    this.contact = contact;
    this.contactRole = contactRole;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
  }

  public ContactRole getContactRole() {
    return contactRole;
  }

  public void setContactRole(ContactRole contactRole) {
    this.contactRole = contactRole;
  }
}
