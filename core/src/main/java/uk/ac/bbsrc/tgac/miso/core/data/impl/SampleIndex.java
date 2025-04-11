package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;

@Entity
public class SampleIndex implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long indexId = UNSAVED_ID;

  private String name;

  @ManyToOne
  @JoinColumn(name = "indexFamilyId")
  private SampleIndexFamily family;

  @Override
  public long getId() {
    return indexId;
  }

  @Override
  public void setId(long id) {
    this.indexId = id;
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

  public SampleIndexFamily getFamily() {
    return family;
  }

  public void setFamily(SampleIndexFamily family) {
    this.family = family;
  }

  @Override
  public String getDeleteType() {
    return "Sample Index";
  }

  @Override
  public String getDeleteDescription() {
    return getFamily().getName() + " - " + getName();
  }

}
