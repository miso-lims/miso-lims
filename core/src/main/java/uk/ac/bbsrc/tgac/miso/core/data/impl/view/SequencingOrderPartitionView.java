package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

@Entity
@Immutable
public class SequencingOrderPartitionView implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long partitionId;

  @Enumerated(EnumType.STRING)
  private HealthType health;

  @Override
  public long getId() {
    return partitionId;
  }

  @Override
  public void setId(long id) {
    this.partitionId = id;
  }

  @Override
  public boolean isSaved() {
    return true;
  }

  public HealthType getHealth() {
    return health;
  }

  public void setHealth(HealthType health) {
    this.health = health;
  }

}
