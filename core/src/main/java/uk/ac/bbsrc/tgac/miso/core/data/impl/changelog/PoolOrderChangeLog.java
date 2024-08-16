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
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;

@Entity
@Table(name = "PoolOrderChangeLog", indexes = {
        @Index(name = "PoolOrderChangeLog_poolOrderId_changeTime", columnList = "poolOrderId, changeTime")})
public class PoolOrderChangeLog extends AbstractChangeLog {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long poolOrderChangeLogId;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = PoolOrder.class)
    @JoinColumn(name = "poolOrderId", nullable = false, updatable = false)
    private PoolOrder poolOrder;

    @Override
    public Long getId() {
        return poolOrder.getId();
    }

    @Override
    public void setId(Long id) {
        poolOrder.setId(id);
    }

    public Long getPoolOrderChangeLogId() {
        return poolOrderChangeLogId;
    }

    public PoolOrder getPoolOrder() {
        return poolOrder;
    }

    public void setPoolOrder(PoolOrder poolOrder) {
        this.poolOrder = poolOrder;
    }

}
