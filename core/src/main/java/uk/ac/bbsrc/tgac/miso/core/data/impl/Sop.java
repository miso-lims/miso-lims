package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Sop implements Aliasable, Deletable, Serializable {

  public enum SopCategory {
    SAMPLE("Sample"), LIBRARY("Library"), RUN("Run");

    private final String label;

    private SopCategory(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
  }

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long sopId = UNSAVED_ID;

  private String alias;
  private String version;

  @Enumerated(EnumType.STRING)
  private SopCategory category;
  private String url;
  private boolean archived = false;

  @Override
  public long getId() {
    return sopId;
  }

  @Override
  public void setId(long id) {
    this.sopId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "SOP";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias() + " (" + getVersion() + ")";
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

  public SopCategory getCategory() {
    return category;
  }

  public void setCategory(SopCategory category) {
    this.category = category;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  @Override
  public int hashCode() {
    return Objects.hash(alias, version, category, url, archived);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        Sop::getAlias,
        Sop::getVersion,
        Sop::getCategory,
        Sop::getUrl,
        Sop::isArchived);
  }

}
