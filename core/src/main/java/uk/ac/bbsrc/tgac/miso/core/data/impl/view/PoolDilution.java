package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution.PoolDilutionId;

@Entity
@Table(name = "Pool_Dilution")
@IdClass(PoolDilutionId.class)
public class PoolDilution implements Serializable {

  public static class PoolDilutionId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Pool pool;
    private PoolableElementView poolableElementView;

    public Pool getPool() {
      return pool;
    }

    public void setPool(Pool pool) {
      this.pool = pool;
    }

    public PoolableElementView getPoolableElementView() {
      return poolableElementView;
    }

    public void setPoolableElementView(PoolableElementView poolableElementView) {
      this.poolableElementView = poolableElementView;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((pool == null) ? 0 : pool.hashCode());
      result = prime * result + ((poolableElementView == null) ? 0 : poolableElementView.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      PoolDilutionId other = (PoolDilutionId) obj;
      if (pool == null) {
        if (other.pool != null) return false;
      } else if (!pool.equals(other.pool)) return false;
      if (poolableElementView == null) {
        if (other.poolableElementView != null) return false;
      } else if (!poolableElementView.equals(other.poolableElementView)) return false;
      return true;
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "pool_poolId")
  private Pool pool;

  @Id
  @ManyToOne
  @JoinColumn(name = "dilution_dilutionId")
  private PoolableElementView poolableElementView;

  private int proportion = 1;

  public PoolDilution() {
    // Default constructor
  }

  public PoolDilution(Pool pool, PoolableElementView poolableElementView) {
    this.pool = pool;
    this.poolableElementView = poolableElementView;
  }

  public PoolDilution(Pool pool, PoolableElementView poolableElementView, int proportion) {
    this(pool, poolableElementView);
    this.proportion = proportion;
  }

  public Pool getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

  public PoolableElementView getPoolableElementView() {
    return poolableElementView;
  }

  public void setPoolableElementView(PoolableElementView poolableElementView) {
    this.poolableElementView = poolableElementView;
  }

  public int getProportion() {
    return proportion;
  }

  public void setProportion(int proportion) {
    this.proportion = proportion;
  }

}
