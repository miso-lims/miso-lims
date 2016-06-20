package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(name = "LibraryAdditionalInfo")
public class LibraryAdditionalInfoImpl implements LibraryAdditionalInfo {

  @Id
  private Long libraryId;

  @OneToOne(targetEntity = LibraryImpl.class)
  @JoinColumn(name = "libraryId", nullable = false)
  @MapsId
  private Library library;

  private Long groupId;
  private String groupDescription;

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

  @OneToOne
  @JoinColumn(name = "libraryDesign", nullable = true)
  private LibraryDesign libraryDesign;

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
  public Long getGroupId() {
    return groupId;
  }

  @Override
  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }

  @Override
  public String getGroupDescription() {
    return groupDescription;
  }

  @Override
  public void setGroupDescription(String groupDescription) {
    this.groupDescription = groupDescription;
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

}
