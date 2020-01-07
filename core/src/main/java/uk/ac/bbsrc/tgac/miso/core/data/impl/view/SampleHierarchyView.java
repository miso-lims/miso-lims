package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;

@Entity
@Immutable
public class SampleHierarchyView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long sampleId;

  private String externalName;

  @Enumerated(EnumType.STRING)
  private ConsentLevel consentLevel;

  @ManyToOne(targetEntity = TissueOriginImpl.class)
  @JoinColumn(name = "tissueOriginId")
  private TissueOrigin tissueOrigin;

  @ManyToOne(targetEntity = TissueTypeImpl.class)
  @JoinColumn(name = "tissueTypeId")
  private TissueType tissueType;

  public long getId() {
    return sampleId;
  }

  public void setId(long id) {
    this.sampleId = id;
  }

  public String getExternalName() {
    return externalName;
  }

  public void setExternalName(String externalName) {
    this.externalName = externalName;
  }

  public ConsentLevel getConsentLevel() {
    return consentLevel;
  }

  public void setConsentLevel(ConsentLevel consentLevel) {
    this.consentLevel = consentLevel;
  }

  public TissueOrigin getTissueOrigin() {
    return tissueOrigin;
  }

  public void setTissueOrigin(TissueOrigin tissueOrigin) {
    this.tissueOrigin = tissueOrigin;
  }

  public TissueType getTissueType() {
    return tissueType;
  }

  public void setTissueType(TissueType tissueType) {
    this.tissueType = tissueType;
  }

}
