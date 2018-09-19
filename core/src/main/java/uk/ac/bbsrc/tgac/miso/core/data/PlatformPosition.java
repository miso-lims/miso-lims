package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class PlatformPosition implements Serializable, Aliasable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long positionId = UNSAVED_ID;

  @ManyToOne
  @JoinColumn(name = "platformId")
  private Platform platform;

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

  public Platform getPlatform() {
    return platform;
  }

  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  /**
   * This value is not loaded from the database, and is only intended to be set when included in an InstrumentStatus
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
    result = prime * result + ((platform == null) ? 0 : platform.hashCode());
    result = prime * result + (int) (positionId ^ (positionId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PlatformPosition other = (PlatformPosition) obj;
    if (alias == null) {
      if (other.alias != null) return false;
    } else if (!alias.equals(other.alias)) return false;
    if (platform == null) {
      if (other.platform != null) return false;
    } else if (!platform.equals(other.platform)) return false;
    if (positionId != other.positionId) return false;
    return true;
  }

}
