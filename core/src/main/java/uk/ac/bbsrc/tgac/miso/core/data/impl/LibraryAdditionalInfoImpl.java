package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(name = "LibraryAdditionalInfo")
public class LibraryAdditionalInfoImpl implements LibraryAdditionalInfo {
  
  @Id
  @Column(name = "libraryAdditionalInfoId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @OneToOne(targetEntity = LibraryImpl.class)
  @JoinColumn(name = "libraryId", nullable = false)
  private Library library;
  
  @OneToOne(targetEntity = TissueOriginImpl.class)
  @JoinColumn(name = "tissueOriginId", nullable = false)
  private TissueOrigin tissueOrigin;
  
  @OneToOne(targetEntity = TissueTypeImpl.class)
  @JoinColumn(name = "tissueTypeId", nullable = false)
  private TissueType tissueType;
  
  @OneToOne(targetEntity = SampleGroupImpl.class)
  @JoinColumn(name = "sampleGroupId", nullable = true)
  private SampleGroupId sampleGroupId;
  
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

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
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
  public TissueOrigin getTissueOrigin() {
    return tissueOrigin;
  }

  @Override
  public void setTissueOrigin(TissueOrigin tissueOrigin) {
    this.tissueOrigin = tissueOrigin;
  }

  @Override
  public TissueType getTissueType() {
    return tissueType;
  }

  @Override
  public void setTissueType(TissueType tissueType) {
    this.tissueType = tissueType;
  }

  @Override
  public SampleGroupId getSampleGroupId() {
    return sampleGroupId;
  }

  @Override
  public void setSampleGroupId(SampleGroupId sampleGroupId) {
    this.sampleGroupId = sampleGroupId;
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
      this.kitDescriptorId = prepKit.getKitDescriptorId();
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

}
