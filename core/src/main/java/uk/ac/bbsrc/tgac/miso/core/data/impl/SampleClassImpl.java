package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

@Entity
@Table(name = "SampleClass", uniqueConstraints = @UniqueConstraint(columnNames = { "alias", "sampleCategory" }))
public class SampleClassImpl implements SampleClass {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sampleClassId;

  @Column(nullable = false)
  private String alias;

  @Column(nullable = false)
  private String sampleCategory;

  @Column(nullable = true)
  private String suffix;

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

  private Boolean dnaseTreatable;

  @Override
  public Long getId() {
    return sampleClassId;
  }

  @Override
  public void setId(Long sampleClassId) {
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
  public Boolean getDNAseTreatable() {
    return dnaseTreatable;
  }

  @Override
  public void setDNAseTreatable(Boolean treatable) {
    dnaseTreatable = treatable;
  }

  @Override
  public boolean hasPathToDnaseTreatable(Collection<SampleValidRelationship> relationships) {
    return hasPathToDnaseTreatable(this, new HashSet<>(), relationships);
  }

  private static boolean hasPathToDnaseTreatable(SampleClass from, Set<Long> checked, Collection<SampleValidRelationship> relationships) {
    if (from.getDNAseTreatable()) {
      return true;
    }
    // stop at tissue level, or if circling into a class hierarchy that has already been checked
    if (from.getSampleCategory().equals(SampleTissue.CATEGORY_NAME) || !checked.add(from.getId())) {
      return false;
    }
    return relationships.stream()
        .filter(relationship -> !relationship.getArchived()
            && relationship.getChild().getId().equals(from.getId())
            && !checked.contains(relationship.getParent().getId()))
        .anyMatch(relationship -> hasPathToDnaseTreatable(relationship.getParent(), checked, relationships));
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(19, 49)
        .append(alias)
        .append(dnaseTreatable)
        .append(sampleCategory)
        .append(suffix)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SampleClassImpl other = (SampleClassImpl) obj;
    return new EqualsBuilder()
        .append(alias, other.alias)
        .append(dnaseTreatable, other.dnaseTreatable)
        .append(sampleCategory, other.sampleCategory)
        .append(suffix, other.suffix)
        .isEquals();
  }
}
