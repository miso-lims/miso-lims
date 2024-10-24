package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class ContactRole implements Deletable, Serializable {
  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @Column(name = "contactRoleId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long contactRoleId = UNSAVED_ID;

  private String name;

  @Override
  public long getId() {
    return contactRoleId;
  }

  @Override
  public void setId(long id) {
    this.contactRoleId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getDeleteType() {
    return "Contact Role";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj, ContactRole::getName);
  }
}
