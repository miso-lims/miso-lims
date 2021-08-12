package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;

public class MetricSubcategoryDto {

  private Long id;
  private String category;
  private String alias;
  private Long libraryDesignCodeId;
  private Integer sortPriority;

  public static MetricSubcategoryDto from(MetricSubcategory from) {
    MetricSubcategoryDto to = new MetricSubcategoryDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setCategory, maybeGetProperty(from.getCategory(), MetricCategory::name));
    setString(to::setAlias, from.getAlias());
    Dtos.setId(to::setLibraryDesignCodeId, from.getLibraryDesignCode());
    setInteger(to::setSortPriority, from.getSortPriority(), true);
    return to;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Long getLibraryDesignCodeId() {
    return libraryDesignCodeId;
  }

  public void setLibraryDesignCodeId(Long libraryDesignCodeId) {
    this.libraryDesignCodeId = libraryDesignCodeId;
  }

  public Integer getSortPriority() {
    return sortPriority;
  }

  public void setSortPriority(Integer sortPriority) {
    this.sortPriority = sortPriority;
  }

  public MetricSubcategory to() {
    MetricSubcategory to = new MetricSubcategory();
    setLong(to::setId, getId(), false);
    setObject(to::setCategory, getCategory(), MetricCategory::valueOf);
    setString(to::setAlias, getAlias());
    setObject(to::setLibraryDesignCode, LibraryDesignCode::new, getLibraryDesignCodeId());
    setInteger(to::setSortPriority, getSortPriority(), true);
    return to;
  }

}
