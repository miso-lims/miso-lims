package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "StainCategory")
public class StainCategory implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  private Long stainCategoryId;
  private String name;

  public Long getId() {
    return stainCategoryId;
  }

  public String getName() {
    return name;
  }

  public void setId(Long id) {
    this.stainCategoryId = id;
  }

  public void setName(String name) {
    this.name = name;
  }

}
