package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Stain")

public class Stain implements Serializable {
  private static final long serialVersionUID = 1L;
  @ManyToOne
  @JoinColumn(name = "stainCategoryId")
  private StainCategory category;
  private String name;
  @Id
  private Long stainId;

  public StainCategory getCategory() {
    return category;
  }

  public Long getId() {
    return stainId;
  }

  public String getName() {
    return name;
  }

  public void setCategory(StainCategory category) {
    this.category = category;
  }

  public void setId(Long id) {
    this.stainId = id;
  }

  public void setName(String name) {
    this.name = name;
  }
}
