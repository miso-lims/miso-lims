package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;

@Entity
@Table(name = "ContainerQC")
public class ContainerQC extends QC {

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = SequencerPartitionContainerImpl.class)
  @JoinColumn(name = "containerId")
  private SequencerPartitionContainer container;

  @OneToMany(mappedBy = "qc")
  private List<ContainerQcControlRun> controls;

  public SequencerPartitionContainer getContainer() {
    return container;
  }

  public void setContainer(SequencerPartitionContainer container) {
    this.container = container;
  }

  @Override
  public QualityControllable<?> getEntity() {
    return getContainer();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((container == null) ? 0 : container.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ContainerQC other = (ContainerQC) obj;
    if (container == null) {
      if (other.container != null) return false;
    } else if (!container.equals(other.container)) return false;
    return true;
  }

  @Override
  public List<ContainerQcControlRun> getControls() {
    if (controls == null) {
      controls = new ArrayList<>();
    }
    return controls;
  }

}
