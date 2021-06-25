package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Assay implements Aliasable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;
  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long assayId;

  private String alias;

  @Column(updatable = false)
  private String version;

  private String description;
  private boolean archived = false;

  @OneToMany(mappedBy = "assay", cascade = CascadeType.ALL)
  private Set<AssayMetric> assayMetrics;

  @Override
  public long getId() {
    return assayId;
  }

  @Override
  public void setId(long id) {
    this.assayId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Assay";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public Set<AssayMetric> getAssayMetrics() {
    if (assayMetrics == null) {
      assayMetrics = new HashSet<>();
    }
    return assayMetrics;
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, version, description, archived);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        Assay::getId,
        Assay::getVersion,
        Assay::getDescription,
        Assay::isArchived);
  }

}
