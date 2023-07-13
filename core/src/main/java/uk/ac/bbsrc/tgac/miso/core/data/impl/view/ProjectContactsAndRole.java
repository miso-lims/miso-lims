package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ContactRole;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ProjectContactsAndRole.ProjectContactsAndRoleId;

@Entity
@Table(name = "Project_Contact_and_Role")
@IdClass(ProjectContactsAndRoleId.class)
public class ProjectContactsAndRole implements Serializable {

  public static class ProjectContactsAndRoleId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Project project;
    private Contact contact;

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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((project == null) ? 0 : project.hashCode());
      result = prime * result + ((contact == null) ? 0 : contact.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ProjectContactsAndRoleId other = (ProjectContactsAndRoleId) obj;
      if (project == null) {
        if (other.project != null)
          return false;
      } else if (!project.equals(other.project))
        return false;
      if (contact == null) {
        if (other.contact != null)
          return false;
      } else if (!contact.equals(other.contact))
        return false;
      return true;
    }
  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne(targetEntity = ProjectImpl.class)
  @JoinColumn(name = "projectId")
  private Project project;

  @Id
  @ManyToOne(targetEntity = Contact.class)
  @JoinColumn(name = "contactId")
  private Contact contact;

  @ManyToOne(targetEntity = ContactRole.class)
  @JoinColumn(name = "contactRoleId")
  private ContactRole contactRole;

  public ProjectContactsAndRole() {
    // Default constructor
  }

  public ProjectContactsAndRole(Project project, Contact contact) {
    this.project = project;
    this.contact = contact;
  }

  public ProjectContactsAndRole(Project project, Contact contact, ContactRole contactRole) {
    this(project, contact);
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
