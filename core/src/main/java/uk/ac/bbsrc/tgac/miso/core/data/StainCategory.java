package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "StainCategory")
public class StainCategory implements Deletable, Identifiable, Serializable {

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
