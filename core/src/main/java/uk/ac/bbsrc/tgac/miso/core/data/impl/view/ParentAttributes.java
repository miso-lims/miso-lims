package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "SampleHierarchy")
@Immutable
public class ParentAttributes implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long sampleId;

  @ManyToOne
  @JoinColumn(name = "identityId")
  private ParentIdentityAttributes identityAttributes;

  @ManyToOne
  @JoinColumn(name = "tissueId")
  private ParentTissueAttributes tissueAttributes;

  public long getSampleId() {
    return sampleId;
  }

  public void setSampleId(long sampleId) {
    this.sampleId = sampleId;
  }

  public ParentIdentityAttributes getIdentityAttributes() {
    return identityAttributes;
  }

  public void setIdentityAttributes(ParentIdentityAttributes identityAttributes) {
    this.identityAttributes = identityAttributes;
  }

  public ParentTissueAttributes getTissueAttributes() {
    return tissueAttributes;
  }

  public void setTissueAttributes(ParentTissueAttributes tissueAttributes) {
    this.tissueAttributes = tissueAttributes;
  }

}
