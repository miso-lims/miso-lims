package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;

@Entity
public class SampleIndexFamily implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long indexFamilyId = UNSAVED_ID;

  private String name;

  @OneToMany(targetEntity = SampleIndex.class, mappedBy = "family", cascade = CascadeType.REMOVE)
  @OrderBy("name")
  private List<SampleIndex> indices;

  @Override
  public long getId() {
    return indexFamilyId;
  }

  @Override
  public void setId(long id) {
    this.indexFamilyId = id;
  }

  @Override
  public boolean isSaved() {
    return indexFamilyId != UNSAVED_ID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<SampleIndex> getIndices() {
    if (indices == null) {
      indices = new ArrayList<>();
    }
    return indices;
  }

  public void setIndices(List<SampleIndex> indices) {
    this.indices = indices;
  }

  @Override
  public String getDeleteType() {
    return "Sample Index Family";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

}
