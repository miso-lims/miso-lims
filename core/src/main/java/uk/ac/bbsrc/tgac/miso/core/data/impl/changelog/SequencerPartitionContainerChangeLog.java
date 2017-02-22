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
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;

@Entity
@Table(appliesTo = "SequencerPartitionContainerChangeLog", indexes = {
    @Index(name = "SequencerPartitionContainerChangeLog_sequencerPartitionContainerId_changeTime", columnNames = {
        "containerId", "changeTime" }) })
public class SequencerPartitionContainerChangeLog extends AbstractChangeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sequencerPartitionContainerChangeLogId;

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
    return sequencerPartitionContainerChangeLogId;
  }

  public void setSequencerPartitionContainer(SequencerPartitionContainer sequencerPartitionContainer) {
    this.sequencerPartitionContainer = sequencerPartitionContainer;
  }

}
