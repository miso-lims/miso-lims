package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;

@Entity
public class StorageLocationChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long storageLocationChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "locationId", nullable = false, updatable = false)
  private StorageLocation storageLocation;

  @Override
  public Long getId() {
    return storageLocation.getId();
  }

  @Override
  public void setId(Long id) {
    storageLocation.setId(id);
  }

  public Long getStorageLocationChangeLogId() {
    return storageLocationChangeLogId;
  }

  public StorageLocation getStorageLocation() {
    return storageLocation;
  }

  public void setStorageLocation(StorageLocation location) {
    this.storageLocation = location;
  }

}
