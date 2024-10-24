package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@DiscriminatorValue("DetailedLibrary")
public class DetailedLibraryImpl extends LibraryImpl implements DetailedLibrary {

  private static final long serialVersionUID = 1L;

  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;

  @Column(nullable = false)
  private boolean nonStandardAlias = false;

  @ManyToOne
  @JoinColumn(name = "libraryDesign", nullable = true)
  private LibraryDesign libraryDesign;

  @ManyToOne
  @JoinColumn(name = "libraryDesignCodeId", nullable = false)
  private LibraryDesignCode libraryDesignCode;

  @Column(updatable = false)
  private Long preMigrationId;
  private String groupId;
  private String groupDescription;

  @Override
  public Boolean getArchived() {
    return archived;
  }

  @Override
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  @Override
  public LibraryDesign getLibraryDesign() {
    return libraryDesign;
  }

  @Override
  public void setLibraryDesign(LibraryDesign libraryDesign) {
    this.libraryDesign = libraryDesign;
  }

  @Override
  public LibraryDesignCode getLibraryDesignCode() {
    return libraryDesignCode;
  }

  @Override
  public void setLibraryDesignCode(LibraryDesignCode libraryDesignCode) {
    this.libraryDesignCode = libraryDesignCode;
  }

  @Override
  public boolean hasNonStandardAlias() {
    return nonStandardAlias;
  }

  @Override
  public void setNonStandardAlias(boolean nonStandardAlias) {
    this.nonStandardAlias = nonStandardAlias;
  }

  @Override
  public Long getPreMigrationId() {
    return preMigrationId;
  }

  @Override
  public void setPreMigrationId(Long preMigrationId) {
    this.preMigrationId = preMigrationId;
  }

  @Override
  public String getGroupId() {
    return groupId;
  }

  @Override
  public void setGroupId(String groupId) {
    this.groupId = nullifyStringIfBlank(groupId);
  }

  @Override
  public String getGroupDescription() {
    return groupDescription;
  }

  @Override
  public void setGroupDescription(String groupDescription) {
    this.groupDescription = nullifyStringIfBlank(groupDescription);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(15, 45)
        .append(archived)
        .append(libraryDesign)
        .append(nonStandardAlias)
        .append(preMigrationId)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DetailedLibraryImpl other = (DetailedLibraryImpl) obj;
    return new EqualsBuilder()
        .appendSuper(super.equals(obj))
        .append(archived, other.archived)
        .append(libraryDesign, other.libraryDesign)
        .append(libraryDesignCode, other.libraryDesignCode)
        .append(nonStandardAlias, other.nonStandardAlias)
        .append(preMigrationId, other.preMigrationId)
        .isEquals();
  }

  @Override
  public GroupIdentifiable getGroupIdentifiableParent() {
    return getSample() != null && LimsUtils.isDetailedSample(getSample()) ? (DetailedSample) getSample() : null;
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitLibraryDetailed(this);
  }

}
