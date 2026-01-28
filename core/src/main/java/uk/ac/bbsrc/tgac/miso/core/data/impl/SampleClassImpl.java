package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

@Entity
@Table(name = "SampleClass", uniqueConstraints = @UniqueConstraint(columnNames = {"alias", "sampleCategory"}))
public class SampleClassImpl implements SampleClass {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long sampleClassId = UNSAVED_ID;

  @Column(nullable = false)
  private String alias;

  @Column(nullable = false)
  private String sampleCategory;

  private String sampleSubcategory;

  @Column(nullable = true)
  private String suffix;

  private String v2NamingCode;

  private boolean archived = false;
  private boolean directCreationAllowed = true;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @ManyToOne
  @JoinColumn(name = "defaultSampleTypeId")
  private SampleType defaultSampleType;

  @OneToMany(targetEntity = SampleValidRelationshipImpl.class, mappedBy = "child", cascade = CascadeType.ALL)
  private Set<SampleValidRelationship> parentRelationships;

  @OneToMany(targetEntity = SampleValidRelationshipImpl.class, mappedBy = "parent")
  private Set<SampleValidRelationship> childRelationships;

  @Override
  public long getId() {
    return sampleClassId;
  }

  @Override
  public void setId(long sampleClassId) {
    this.sampleClassId = sampleClassId;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public User getCreator() {
    return createdBy;
  }

  @Override
  public void setCreator(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public Date getCreationTime() {
    return creationDate;
  }

  @Override
  public void setCreationTime(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public User getLastModifier() {
    return updatedBy;
  }

  @Override
  public void setLastModifier(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public Date getLastModified() {
    return lastUpdated;
  }

  @Override
  public void setLastModified(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public String getSampleCategory() {
    return sampleCategory;
  }

  @Override
  public void setSampleCategory(String sampleCategory) {
    this.sampleCategory = sampleCategory;
  }

  @Override
  public String getSuffix() {
    return suffix;
  }

  @Override
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  @Override
  public String getV2NamingCode() {
    return v2NamingCode;
  }

  @Override
  public void setV2NamingCode(String v2NamingCode) {
    this.v2NamingCode = v2NamingCode;
  }

  @Override
  public boolean isArchived() {
    return archived;
  }

  @Override
  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  @Override
  public boolean isDirectCreationAllowed() {
    return directCreationAllowed;
  }

  @Override
  public void setDirectCreationAllowed(boolean directCreationAllowed) {
    this.directCreationAllowed = directCreationAllowed;
  }

  @Override
  public String getSampleSubcategory() {
    return sampleSubcategory;
  }

  @Override
  public void setSampleSubcategory(String sampleSubcategory) {
    this.sampleSubcategory = sampleSubcategory;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(19, 49)
        .append(alias)
        .append(sampleCategory)
        .append(sampleSubcategory)
        .append(suffix)
        .append(v2NamingCode)
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
    SampleClassImpl other = (SampleClassImpl) obj;
    return new EqualsBuilder()
        .append(alias, other.alias)
        .append(sampleCategory, other.sampleCategory)
        .append(sampleSubcategory, other.sampleSubcategory)
        .append(suffix, other.suffix)
        .append(v2NamingCode, other.v2NamingCode)
        .isEquals();
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Sample Class";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public Set<SampleValidRelationship> getParentRelationships() {
    if (parentRelationships == null) {
      parentRelationships = new HashSet<>();
    }
    return parentRelationships;
  }

  @Override
  public Set<SampleValidRelationship> getChildRelationships() {
    if (childRelationships == null) {
      childRelationships = new HashSet<>();
    }
    return childRelationships;
  }

  @Override
  public SampleType getDefaultSampleType() {
    return defaultSampleType;
  }

  @Override
  public void setDefaultSampleType(SampleType sampleType) {
    this.defaultSampleType = sampleType;
  }

}
