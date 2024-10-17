package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement.PoolElementId;

@Entity
@Table(name = "Pool_LibraryAliquot")
@IdClass(PoolElementId.class)
public class PoolElement implements Serializable {

  public static class PoolElementId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Pool pool;
    private ListLibraryAliquotView aliquot;

    public Pool getPool() {
      return pool;
    }

    public void setPool(Pool pool) {
      this.pool = pool;
    }

    public ListLibraryAliquotView getAliquot() {
      return aliquot;
    }

    public void setAliquot(ListLibraryAliquotView aliquot) {
      this.aliquot = aliquot;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((pool == null) ? 0 : pool.hashCode());
      result = prime * result + ((aliquot == null) ? 0 : aliquot.hashCode());
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
      PoolElementId other = (PoolElementId) obj;
      if (pool == null) {
        if (other.pool != null)
          return false;
      } else if (!pool.equals(other.pool))
        return false;
      if (aliquot == null) {
        if (other.aliquot != null)
          return false;
      } else if (!aliquot.equals(other.aliquot))
        return false;
      return true;
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "poolId")
  private Pool pool;

  @Id
  @ManyToOne
  @JoinColumn(name = "aliquotId")
  private ListLibraryAliquotView aliquot;

  private int proportion = 1;

  public PoolElement() {
    // Default constructor
  }

  public PoolElement(Pool pool, ListLibraryAliquotView aliquot) {
    this.pool = pool;
    this.aliquot = aliquot;
  }

  public PoolElement(Pool pool, ListLibraryAliquotView aliquot, int proportion) {
    this(pool, aliquot);
    this.proportion = proportion;
  }

  public Pool getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

  public ListLibraryAliquotView getAliquot() {
    return aliquot;
  }

  public void setAliquot(ListLibraryAliquotView aliquot) {
    this.aliquot = aliquot;
  }

  public int getProportion() {
    return proportion;
  }

  public void setProportion(int proportion) {
    this.proportion = proportion;
  }

}
