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
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.PoolBoxableView;

@Entity
@Table(appliesTo = "PoolChangeLog", indexes = {
    @Index(name = "PoolChangeLog_poolId_changeTime", columnNames = {"poolId", "changeTime"})})
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
