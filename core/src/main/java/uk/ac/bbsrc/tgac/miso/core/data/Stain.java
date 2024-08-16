package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Stain")

public class Stain implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long stainId = UNSAVED_ID;

  @ManyToOne
  @JoinColumn(name = "stainCategoryId")
  private StainCategory category;

  private String name;

  @Override
  public long getId() {
    return stainId;
  }

  @Override
  public void setId(long id) {
    this.stainId = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public StainCategory getCategory() {
    return category;
  }

  public void setCategory(StainCategory category) {
    this.category = category;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Stain";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }
}
