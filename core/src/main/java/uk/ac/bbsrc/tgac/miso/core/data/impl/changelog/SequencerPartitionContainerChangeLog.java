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
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;

@Entity
@Table(name = "SequencerPartitionContainerChangeLog", indexes = {
    @Index(name = "SequencerPartitionContainerChangeLog_sequencerPartitionContainerId_changeTime",
        columnList = "containerId, changeTime")})
public class SequencerPartitionContainerChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long containerChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = SequencerPartitionContainerImpl.class)
  @JoinColumn(name = "containerId", nullable = false, updatable = false)
  private SequencerPartitionContainer sequencerPartitionContainer;

  @Override
  public Long getId() {
    return sequencerPartitionContainer.getId();
  }

  @Override
  public void setId(Long id) {
    sequencerPartitionContainer.setId(id);
  }

  public Long getSequencerPartitionContainerChangeLogId() {
    return containerChangeLogId;
  }

  public void setSequencerPartitionContainer(SequencerPartitionContainer sequencerPartitionContainer) {
    this.sequencerPartitionContainer = sequencerPartitionContainer;
  }

}
