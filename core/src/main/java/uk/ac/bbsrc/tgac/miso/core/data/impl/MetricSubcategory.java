package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;

@Entity
public class MetricSubcategory implements Aliasable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long subcategoryId = UNSAVED_ID;

  @Enumerated(EnumType.STRING)
  private MetricCategory category;

  private String alias;

  @ManyToOne
  @JoinColumn(name = "libraryDesignCodeId")
  private LibraryDesignCode libraryDesignCode;

  private Integer sortPriority;

  @Override
  public long getId() {
    return subcategoryId;
  }

  @Override
  public void setId(long id) {
    this.subcategoryId = id;
  }

  @Override
  public boolean isSaved() {
    return subcategoryId != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Metric Subcategory";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  public MetricCategory getCategory() {
    return category;
  }

  public void setCategory(MetricCategory category) {
    this.category = category;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public LibraryDesignCode getLibraryDesignCode() {
    return libraryDesignCode;
  }

  public void setLibraryDesignCode(LibraryDesignCode libraryDesignCode) {
    this.libraryDesignCode = libraryDesignCode;
  }

  public Integer getSortPriority() {
    return sortPriority;
  }

  public void setSortPriority(Integer sortPriority) {
    this.sortPriority = sortPriority;
  }

}
