package uk.ac.bbsrc.tgac.miso.core.data.impl.workset;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetPool.WorksetPoolId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Workset_Pool")
@IdClass(WorksetPoolId.class)
public class WorksetPool extends WorksetItem<Pool>{

    public static class WorksetPoolId implements Serializable{

        private static final long serialVersionUID = 1L;

        private Workset workset;
        private Pool item;

        public Workset getWorkset() {
            return workset;
        }

        public void setWorkset(Workset workset) {
            this.workset = workset;
        }

        public Pool getItem() {
            return item;
        }

        public void setItem(Pool item) {
            this.item = item;
        }

        @Override
        public int hashCode() {
            return Objects.hash(workset, item);
        }

        @Override
        public boolean equals(Object obj) {
            return LimsUtils.equals(this, obj,
                WorksetPoolId::getWorkset,
                WorksetPoolId::getItem);
        }
    }
    
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "worksetId")
    private Workset workset;

    @Id
    @ManyToOne(targetEntity = PoolImpl.class)
    @JoinColumn(name = "poolId")
    private Pool item;

    @Override
    public Workset getWorkset() {
        return workset;
    }

    @Override
    public void setWorkset(Workset workset) {
        this.workset = workset;
    }

    @Override
    public Pool getItem() {
        return item;
    }

    @Override
    public void setItem(Pool item) {
        this.item = item;
    }
}
