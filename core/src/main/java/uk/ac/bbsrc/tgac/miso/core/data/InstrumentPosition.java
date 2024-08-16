package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class InstrumentPosition implements Serializable, Aliasable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long positionId = UNSAVED_ID;

  @ManyToOne
  @JoinColumn(name = "instrumentModelId")
  private InstrumentModel instrumentModel;

  private String alias;

  @Transient
  private boolean outOfService;

  @Override
  public long getId() {
    return positionId;
  }

  @Override
  public void setId(long id) {
    this.positionId = id;
  }

  public InstrumentModel getInstrumentModel() {
    return instrumentModel;
  }

  public void setInstrumentModel(InstrumentModel instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  /**
   * This value is not loaded from the database, and is only intended to be set when included in an
   * InstrumentStatus
   * 
   * @return whether or not the instrument position is out of service
   */
  public boolean isOutOfService() {
    return outOfService;
  }

  public void setOutOfService(boolean outOfService) {
    this.outOfService = outOfService;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alias == null) ? 0 : alias.hashCode());
    result = prime * result + ((instrumentModel == null) ? 0 : instrumentModel.hashCode());
    result = prime * result + (int) (positionId ^ (positionId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    InstrumentPosition other = (InstrumentPosition) obj;
    if (alias == null) {
      if (other.alias != null)
        return false;
    } else if (!alias.equals(other.alias))
      return false;
    if (instrumentModel == null) {
      if (other.instrumentModel != null)
        return false;
    } else if (!instrumentModel.equals(other.instrumentModel))
      return false;
    if (positionId != other.positionId)
      return false;
    return true;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

}
