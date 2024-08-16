package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "StainCategory")
public class StainCategory implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long stainCategoryId = UNSAVED_ID;
  private String name;

  @Override
  public long getId() {
    return stainCategoryId;
  }

  @Override
  public void setId(long id) {
    this.stainCategoryId = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Stain Category";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

}
