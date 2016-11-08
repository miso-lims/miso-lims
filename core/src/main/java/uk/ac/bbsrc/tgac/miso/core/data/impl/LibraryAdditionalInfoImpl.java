package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(name = "LibraryAdditionalInfo")
public class LibraryAdditionalInfoImpl implements LibraryAdditionalInfo {

  @Id
  private Long libraryId;

  // TODO: enable once Library is migrated to Hibernate.
  // @OneToOne(targetEntity = LibraryImpl.class)
  // @JoinColumn(name = "libraryId", nullable = false)
  // @MapsId
  @Transient
  private Library library;

  private Long kitDescriptorId;

  @Transient
  private KitDescriptor prepKit;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  private Date creationDate;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  private Date lastUpdated;

  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;

  @Column(nullable = false)
  private boolean nonStandardAlias = false;

  @OneToOne
  @JoinColumn(name = "libraryDesign", nullable = true)
  private LibraryDesign libraryDesign;
  
  @OneToOne
  @JoinColumn(name = "libraryDesignCodeId", nullable = false)
  private LibraryDesignCode libraryDesignCode;

  private Long preMigrationId;

  @Override
  public Long getLibraryId() {
    return libraryId;
  }

  @Override
  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }

  @Override
  public Library getLibrary() {
    return library;
  }

  @Override
  public void setLibrary(Library library) {
    this.library = library;
  }

  @Override
  public KitDescriptor getPrepKit() {
    return prepKit;
  }

  @Override
  public void setPrepKit(KitDescriptor kitDescriptor) {
    this.prepKit = kitDescriptor;

    // Keep kitDescriptorId field consistent for Hibernate persistence
    if (prepKit == null) {
      this.kitDescriptorId = null;
    } else {
      this.kitDescriptorId = prepKit.getId();
    }
  }

  @Override
  public Long getHibernateKitDescriptorId() {
    return kitDescriptorId;
  }

  @Override
  public User getCreatedBy() {
    return createdBy;
  }

  @Override
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public User getUpdatedBy() {
    return updatedBy;
  }

  @Override
  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
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
        .append(kitDescriptorId)
        .append(libraryDesign)
        .append(nonStandardAlias)
        .append(prepKit)
        .append(preMigrationId)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LibraryAdditionalInfoImpl other = (LibraryAdditionalInfoImpl) obj;
    return new EqualsBuilder()
        .append(archived, other.archived)
        .append(libraryDesign, other.libraryDesign)
        .append(libraryDesignCode, other.libraryDesignCode)
        .append(nonStandardAlias, other.nonStandardAlias)
        .append(prepKit, other.prepKit)
        .append(preMigrationId, other.preMigrationId)
        .isEquals();
  }

}
