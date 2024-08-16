package uk.ac.bbsrc.tgac.miso.core.data.impl.view.box;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "PoolBoxPosition")
public class PoolBoxablePositionView extends BoxablePositionView {

  private static final long serialVersionUID = 1L;

  @Id
  private long poolId;

  @Override
  public long getId() {
    return poolId;
  }

  @Override
  public void setId(long id) {
    this.poolId = id;
  }

}
