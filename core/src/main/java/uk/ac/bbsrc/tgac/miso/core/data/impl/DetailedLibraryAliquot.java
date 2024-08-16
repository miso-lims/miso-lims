package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@DiscriminatorValue("DetailedLibraryAliquot")
public class DetailedLibraryAliquot extends LibraryAliquot implements GroupIdentifiable {

  private static final long serialVersionUID = 1L;

  private boolean nonStandardAlias = false;

  @ManyToOne
  @JoinColumn(name = "libraryDesignCodeId", nullable = false)
  private LibraryDesignCode libraryDesignCode;

  private String groupId;
  private String groupDescription;

  public boolean isNonStandardAlias() {
    return nonStandardAlias;
  }

  public void setNonStandardAlias(boolean nonStandardAlias) {
    this.nonStandardAlias = nonStandardAlias;
  }

  public LibraryDesignCode getLibraryDesignCode() {
    return libraryDesignCode;
  }

  public void setLibraryDesignCode(LibraryDesignCode libraryDesignCode) {
    this.libraryDesignCode = libraryDesignCode;
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
  public GroupIdentifiable getGroupIdentifiableParent() {
    return getLibrary() != null && LimsUtils.isDetailedLibrary(getLibrary()) ? (DetailedLibrary) getLibrary() : null;
  }


  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitLibraryAliquotDetailed(this);
  }
}
