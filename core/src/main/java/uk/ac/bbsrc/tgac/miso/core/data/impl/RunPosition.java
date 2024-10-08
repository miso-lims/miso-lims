package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition.RunPositionId;

@Entity
@Table(name = "Run_SequencerPartitionContainer")
@IdClass(RunPositionId.class)
public class RunPosition implements Serializable {

  private static final long serialVersionUID = 1L;

  public static class RunPositionId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Run run;
    private SequencerPartitionContainer container;

    public Run getRun() {
      return run;
    }

    public void setRun(Run run) {
      this.run = run;
    }

    public SequencerPartitionContainer getContainer() {
      return container;
    }

    public void setContainer(SequencerPartitionContainer container) {
      this.container = container;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((container == null) ? 0 : container.hashCode());
      result = prime * result + ((run == null) ? 0 : run.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      RunPositionId other = (RunPositionId) obj;
      if (container == null) {
        if (other.container != null)
          return false;
      } else if (!container.equals(other.container))
        return false;
      if (run == null) {
        if (other.run != null)
          return false;
      } else if (!run.equals(other.run))
        return false;
      return true;
    }

  }

  @Id
  @ManyToOne(targetEntity = Run.class)
  @JoinColumn(name = "Run_runId")
  private Run run;

  @Id
  @ManyToOne(targetEntity = SequencerPartitionContainerImpl.class)
  @JoinColumn(name = "containers_containerId")
  private SequencerPartitionContainer container;

  @ManyToOne
  @JoinColumn(name = "positionId")
  private InstrumentPosition position;

  public Run getRun() {
    return run;
  }

  public void setRun(Run run) {
    this.run = run;
  }

  public SequencerPartitionContainer getContainer() {
    return container;
  }

  public void setContainer(SequencerPartitionContainer container) {
    this.container = container;
  }

  public InstrumentPosition getPosition() {
    return position;
  }

  public void setPosition(InstrumentPosition position) {
    this.position = position;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((container == null) ? 0 : container.hashCode());
    result = prime * result + ((position == null) ? 0 : position.hashCode());
    result = prime * result + ((run == null) ? 0 : run.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RunPosition other = (RunPosition) obj;
    if (container == null) {
      if (other.container != null)
        return false;
    } else if (!container.equals(other.container))
      return false;
    if (position == null) {
      if (other.position != null)
        return false;
    } else if (!position.equals(other.position))
      return false;
    if (run == null) {
      if (other.run != null)
        return false;
    } else if (!run.equals(other.run))
      return false;
    return true;
  }

}
