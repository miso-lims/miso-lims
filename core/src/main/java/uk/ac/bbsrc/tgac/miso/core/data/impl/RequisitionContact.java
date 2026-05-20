package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionContact.RequisitionContactId;

@Entity
@Table(name = "Requisition_Contact")
@IdClass(RequisitionContactId.class)
public class RequisitionContact implements Serializable {

  public static class RequisitionContactId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Requisition requisition;
    private Contact contact;
    private ContactRole contactRole;

    public Requisition getRequisition() {
      return requisition;
    }

    public void setRequisition(Requisition requisition) {
      this.requisition = requisition;
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
      result = prime * result + ((requisition == null) ? 0 : requisition.hashCode());
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
      RequisitionContactId other = (RequisitionContactId) obj;
      if (requisition == null) {
        if (other.requisition != null)
          return false;
      } else if (!requisition.equals(other.requisition))
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
  @ManyToOne(targetEntity = Requisition.class)
  @JoinColumn(name = "requisitionId")
  private Requisition requisition;

  @Id
  @ManyToOne(targetEntity = Contact.class)
  @JoinColumn(name = "contactId")
  private Contact contact;

  @Id
  @ManyToOne(targetEntity = ContactRole.class)
  @JoinColumn(name = "contactRoleId")
  private ContactRole contactRole;

  public RequisitionContact() {
    // Default constructor
  }

  public RequisitionContact(Requisition requisition, Contact contact) {
    this.requisition = requisition;
    this.contact = contact;
  }

  public RequisitionContact(Requisition requisition, Contact contact, ContactRole contactRole) {
    this(requisition, contact);
    this.contactRole = contactRole;
  }

  public Requisition getRequisition() {
    return requisition;
  }

  public void setRequisition(Requisition requisition) {
    this.requisition = requisition;
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
