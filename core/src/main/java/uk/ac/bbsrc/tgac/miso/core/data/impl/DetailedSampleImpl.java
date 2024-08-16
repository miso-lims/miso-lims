package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Transient;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentIdentityAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class DetailedSampleImpl extends SampleImpl implements DetailedSample {

  private static final long serialVersionUID = 1L;

  @ManyToOne(optional = true, targetEntity = DetailedSampleImpl.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "parentId", nullable = true)
  private DetailedSample parent;

  @OneToMany(targetEntity = DetailedSampleImpl.class, mappedBy = "parent")
  private List<DetailedSample> children = new ArrayList<>();

  @ManyToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "sampleClassId", nullable = false)
  private SampleClass sampleClass;

  @ManyToOne(targetEntity = SubprojectImpl.class)
  @JoinColumn(name = "subprojectId")
  private Subproject subproject;

  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;

  private Integer siblingNumber;

  private String groupId;
  private String groupDescription;
  private boolean isSynthetic = false;

  @Column(nullable = false)
  private boolean nonStandardAlias = false;

  @Column(updatable = false)
  private Long preMigrationId;

  private LocalDate creationDate;

  private BigDecimal volumeUsed;
  private BigDecimal ngUsed;

  @OneToOne
  @PrimaryKeyJoinColumn
  private ParentAttributes parentAttributes;

  @Transient
  private Long identityId;

  @Override
  public DetailedSample getParent() {
    return parent;
  }

  @Override
  public void setParent(DetailedSample parent) {
    this.parent = parent;
  }

  @Override
  public List<DetailedSample> getChildren() {
    return children;
  }

  @Override
  public void setChildren(List<DetailedSample> children) {
    this.children = children;
  }

  @Override
  public SampleClass getSampleClass() {
    return sampleClass;
  }

  @Override
  public void setSampleClass(SampleClass sampleClass) {
    this.sampleClass = sampleClass;
  }

  @Override
  public Subproject getSubproject() {
    return subproject;
  }

  @Override
  public void setSubproject(Subproject subproject) {
    this.subproject = subproject;
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
  public Integer getSiblingNumber() {
    return siblingNumber;
  }

  @Override
  public void setSiblingNumber(Integer siblingNumber) {
    this.siblingNumber = siblingNumber;
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
  public Boolean isSynthetic() {
    return isSynthetic;
  }

  @Override
  public void setSynthetic(Boolean isSynthetic) {
    if (isSynthetic != null) {
      this.isSynthetic = isSynthetic;
    }
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
  public Long getIdentityId() {
    return identityId;
  }

  @Override
  public void setIdentityId(Long identityId) {
    this.identityId = identityId;
  }

  @Override
  public LocalDate getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public BigDecimal getVolumeUsed() {
    return volumeUsed;
  }

  @Override
  public void setVolumeUsed(BigDecimal ngUsed) {
    this.volumeUsed = ngUsed;
  }

  @Override
  public BigDecimal getNgUsed() {
    return ngUsed;
  }

  @Override
  public void setNgUsed(BigDecimal ngUsed) {
    this.ngUsed = ngUsed;
  }

  @Override
  public LocalDate getBarcodeDate() {
    Date receivedDate = getReceivedDate();
    if (receivedDate != null) {
      return LocalDate.ofInstant(receivedDate.toInstant(), ZoneId.systemDefault());
    }
    if (getCreationDate() != null) {
      return getCreationDate();
    }
    if (getCreationTime() != null) {
      return LocalDate.ofInstant(getCreationTime().toInstant(), ZoneId.systemDefault());
    }
    return null;
  }

  private Date getReceivedDate() {
    ListTransferView receipt = getReceiptTransfer();
    return receipt == null ? null : receipt.getTransferTime();
  }

  @Override
  public GroupIdentifiable getGroupIdentifiableParent() {
    return getParent();
  }

  @Override
  public ParentAttributes getParentAttributes() {
    return parentAttributes;
  }

  @Override
  public void setParentAttributes(ParentAttributes parentAttributes) {
    this.parentAttributes = parentAttributes;
  }

  @Override
  public ParentIdentityAttributes getIdentityAttributes() {
    return parentAttributes == null ? null : parentAttributes.getIdentityAttributes();
  }

  @Override
  public ParentTissueAttributes getTissueAttributes() {
    return parentAttributes == null ? null : parentAttributes.getTissueAttributes();
  }

}
