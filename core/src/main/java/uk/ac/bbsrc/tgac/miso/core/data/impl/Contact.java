package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

@Entity
public class Contact implements Deletable, Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long contactId = UNSAVED_ID;

  private String name;

  private String email;

  @Override
  public long getId() {
    return contactId;
  }

  @Override
  public void setId(long id) {
    this.contactId = id;
  }

  @Override
  public boolean isSaved() {
    return contactId != UNSAVED_ID;
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

  @Override
  public String getDeleteType() {
    return "Contact";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, email);
  }
}
