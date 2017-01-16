package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(name = "DetailedLibrary")
@Inheritance(strategy = InheritanceType.JOINED)
public class DetailedLibraryImpl extends LibraryImpl implements DetailedLibrary {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "kitDescriptorId")
  private KitDescriptor kitDescriptor;

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

  private Long preMigrationId;

  @Override
  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  @Override
  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

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
  public int hashCode() {
    return new HashCodeBuilder(15, 45)
        .append(archived)
        .append(libraryDesign)
        .append(nonStandardAlias)
        .append(kitDescriptor)
        .append(preMigrationId)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DetailedLibraryImpl other = (DetailedLibraryImpl) obj;
    return new EqualsBuilder()
        .append(archived, other.archived)
        .append(libraryDesign, other.libraryDesign)
        .append(libraryDesignCode, other.libraryDesignCode)
        .append(nonStandardAlias, other.nonStandardAlias)
        .append(kitDescriptor, other.kitDescriptor)
        .append(preMigrationId, other.preMigrationId)
        .isEquals();
  }

}
