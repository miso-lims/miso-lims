package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.SopField;

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

  @Column(length = 200)
  private String alias;

  private String version;

  @Enumerated(EnumType.STRING)
  private SopCategory category;

  private String url;

  private boolean archived = false;

  @OneToMany(mappedBy = "sop", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = SopFieldImpl.class)
  @OrderBy("name")
  private final Set<SopField> sopFields = new HashSet<>();

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

  public Set<SopField> getSopFields() {
    return sopFields;
  }

  public void setSopFields(Set<SopField> sopFields) {
    this.sopFields.clear();
    if (sopFields != null) {
      this.sopFields.addAll(sopFields);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Sop))
      return false;
    Sop other = (Sop) obj;

    // MISO convention: only saved entities can be equal
    return sopId != 0L && other.sopId != 0L && sopId == other.sopId;
  }

  @Override
  public int hashCode() {
    return sopId != 0L ? Long.hashCode(sopId) : System.identityHashCode(this);
  }
}
