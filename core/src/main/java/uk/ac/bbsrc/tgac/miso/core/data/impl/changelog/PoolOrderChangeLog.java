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
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;

@Entity
@Table(appliesTo = "PoolOrderChangeLog", indexes = {
        @Index(name = "PoolOrderChangeLog_poolOrderId_changeTime", columnNames = { "poolOrderId", "changeTime" }) })
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
