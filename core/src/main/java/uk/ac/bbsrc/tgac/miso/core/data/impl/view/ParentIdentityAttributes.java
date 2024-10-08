package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;

@Entity
@Table(name = "Sample")
@Immutable
public class ParentIdentityAttributes implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long sampleId;

  private String externalName;

  @Enumerated(EnumType.STRING)
  private ConsentLevel consentLevel;

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

}
