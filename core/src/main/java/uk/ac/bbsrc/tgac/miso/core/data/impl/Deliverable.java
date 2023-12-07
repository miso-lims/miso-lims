package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Deliverable implements Deletable, Serializable {
  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @Column(name = "deliverableId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long deliverableId = UNSAVED_ID;

  private String name;

  @Override
  public long getId() {
    return deliverableId;
  }

  @Override
  public void setId(long id) {
    this.deliverableId = id;
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
    return "Deliverable";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

  @Override
  public int hashCode() {
    return LimsUtils.hashCodeByIdFirst(this, name);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equalsByIdFirst(this, obj,
        Deliverable::getName);
  }
}
