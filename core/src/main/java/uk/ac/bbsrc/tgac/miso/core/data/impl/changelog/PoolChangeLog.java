package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;

@Entity
@Table(appliesTo = "PoolChangeLog", indexes = {
    @Index(name = "PoolChangeLog_poolId_changeTime", columnNames = { "poolId", "changeTime" }) })
public class PoolChangeLog extends AbstractChangeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long poolChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = PoolImpl.class)
  @JoinColumn(name = "poolId", nullable = false, updatable = false)
  private Pool pool;

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

  public Pool getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

}
