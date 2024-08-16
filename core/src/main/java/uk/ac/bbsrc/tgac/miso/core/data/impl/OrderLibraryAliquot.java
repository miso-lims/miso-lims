package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderLibraryAliquot.OrderLibraryId;

@Entity
@Table(name = "PoolOrder_LibraryAliquot")
@IdClass(OrderLibraryId.class)
public class OrderLibraryAliquot implements Serializable {

  public static class OrderLibraryId implements Serializable {

    private static final long serialVersionUID = 1L;

    private PoolOrder poolOrder;
    private LibraryAliquot aliquot;

    public PoolOrder getPoolOrder() {
      return poolOrder;
    }

    public void setPoolOrder(PoolOrder poolOrder) {
      this.poolOrder = poolOrder;
    }

    public LibraryAliquot getAliquot() {
      return aliquot;
    }

    public void setAliquot(LibraryAliquot aliquot) {
      this.aliquot = aliquot;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((aliquot == null) ? 0 : aliquot.hashCode());
      result = prime * result + ((poolOrder == null) ? 0 : poolOrder.hashCode());
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
      OrderLibraryId other = (OrderLibraryId) obj;
      if (aliquot == null) {
        if (other.aliquot != null)
          return false;
      } else if (!aliquot.equals(other.aliquot))
        return false;
      if (poolOrder == null) {
        if (other.poolOrder != null)
          return false;
      } else if (!poolOrder.equals(other.poolOrder))
        return false;
      return true;
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "poolOrderId")
  private PoolOrder poolOrder;

  @Id
  @ManyToOne(targetEntity = LibraryAliquot.class)
  @JoinColumn(name = "aliquotId")
  private LibraryAliquot aliquot;

  private int proportion = 1;

  public PoolOrder getPoolOrder() {
    return poolOrder;
  }

  public void setPoolOrder(PoolOrder poolOrder) {
    this.poolOrder = poolOrder;
  }

  public LibraryAliquot getAliquot() {
    return aliquot;
  }

  public void setAliquot(LibraryAliquot aliquot) {
    this.aliquot = aliquot;
  }

  public int getProportion() {
    return proportion;
  }

  public void setProportion(int proportion) {
    this.proportion = proportion;
  }

}
