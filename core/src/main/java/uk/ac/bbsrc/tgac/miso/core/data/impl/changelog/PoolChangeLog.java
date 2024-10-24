package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.PoolBoxableView;

@Entity
@Table(name = "PoolChangeLog", indexes = {
    @Index(name = "PoolChangeLog_poolId_changeTime", columnList = "poolId, changeTime")})
public class PoolChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long poolChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = PoolImpl.class)
  @JoinColumn(name = "poolId", nullable = false, updatable = false)
  private Identifiable pool;

  @Override
  public Long getId() {
    return pool.getId();
  }

  @Override
  public void setId(Long id) {
    pool.setId(id);
  }

  public Long getPoolChangeLogId() {
    return poolChangeLogId;
  }

  public Identifiable getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

  public void setPool(PoolBoxableView pool) {
    this.pool = pool;
  }

}
