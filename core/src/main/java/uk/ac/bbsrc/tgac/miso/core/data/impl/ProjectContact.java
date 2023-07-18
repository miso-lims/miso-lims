package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectContact.ProjectContactId;

@Entity
@Table(name = "Project_Contact")
@IdClass(ProjectContactId.class)
public class ProjectContact implements Serializable {

  public static class ProjectContactId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Project project;
    private Contact contact;
    private ContactRole contactRole;

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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((project == null) ? 0 : project.hashCode());
      result = prime * result + ((contact == null) ? 0 : contact.hashCode());
      result = prime * result + ((contact == null) ? 0 : contactRole.hashCode());

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
      ProjectContactId other = (ProjectContactId) obj;
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
      if (contactRole == null) {
        if (other.contactRole != null)
          return false;
      } else if (!contactRole.equals(other.contactRole))
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

  @Id
  @ManyToOne(targetEntity = ContactRole.class)
  @JoinColumn(name = "contactRoleId")
  private ContactRole contactRole;

  public ProjectContact() {
    // Default constructor
  }

  public ProjectContact(Project project, Contact contact) {
    this.project = project;
    this.contact = contact;
  }

  public ProjectContact(Project project, Contact contact, ContactRole contactRole) {
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
