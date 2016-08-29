package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(name = "SampleAdditionalInfo")
@Inheritance(strategy = InheritanceType.JOINED)
public class SampleAdditionalInfoImpl extends SampleImpl implements SampleAdditionalInfo {

  private static final long serialVersionUID = 1L;

  @ManyToOne(optional = true, targetEntity = SampleAdditionalInfoImpl.class)
  @JoinColumn(name = "parentId", nullable = true)
  private SampleAdditionalInfo parent;

  @Transient
  private Set<SampleAdditionalInfo> children = new HashSet<>();

  @OneToOne(targetEntity = SampleClassImpl.class)
  @JoinColumn(name = "sampleClassId", nullable = false)
  private SampleClass sampleClass;

  @OneToOne(targetEntity = QcPassedDetailImpl.class)
  @JoinColumn(name = "qcPassedDetailId")
  private QcPassedDetail qcPassedDetail;

  @OneToOne(targetEntity = SubprojectImpl.class)
  @JoinColumn(name = "subprojectId")
  private Subproject subproject;

  private Long kitDescriptorId;

  @Transient
  private KitDescriptor prepKit;

  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;

  private Integer siblingNumber;

  private String groupId;
  private String groupDescription;
  private boolean isSynthetic = false;

  @Column(nullable = false)
  private boolean nonStandardAlias = false;

  @Override
  public SampleAdditionalInfo getParent() {
    return parent;
  }

  @Override
  public void setParent(SampleAdditionalInfo parent) {
    this.parent = parent;
  }

  @Override
  public Set<SampleAdditionalInfo> getChildren() {
    return children;
  }

  @Override
  public void setChildren(Set<SampleAdditionalInfo> children) {
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
  public QcPassedDetail getQcPassedDetail() {
    return qcPassedDetail;
  }

  @Override
  public void setQcPassedDetail(QcPassedDetail qcPassedDetail) {
    this.qcPassedDetail = qcPassedDetail;
  }

  @Override
  public KitDescriptor getPrepKit() {
    return prepKit;
  }

  @Override
  public void setPrepKit(KitDescriptor prepKit) {
    this.prepKit = prepKit;

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
}
