package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;

/**
 * Concrete implementation of a PoolQC
 * 
 * @author Rob Davey
 * @since 0.1.9
 */
@Entity
@Table(name = "PoolQC")
public class PoolQC extends QC {
  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "pool_poolId")
  private Pool pool;

  @OneToMany(mappedBy = "qc", cascade = CascadeType.REMOVE)
  private List<PoolQcControlRun> controls;

  @Override
  public QualityControllable<?> getEntity() {
    return pool;
  }

  public Pool getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

  @Override
  public List<PoolQcControlRun> getControls() {
    if (controls == null) {
      controls = new ArrayList<>();
    }
    return controls;
  }

}
