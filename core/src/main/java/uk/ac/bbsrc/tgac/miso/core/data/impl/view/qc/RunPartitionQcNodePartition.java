package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "_Partition")
@Immutable
public class RunPartitionQcNodePartition implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "partitionId")
  private long id;

  private int partitionNumber;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getPartitionNumber() {
    return partitionNumber;
  }

  public void setPartitionNumber(int partitionNumber) {
    this.partitionNumber = partitionNumber;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, partitionNumber);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        RunPartitionQcNodePartition::getId,
        RunPartitionQcNodePartition::getPartitionNumber);
  }

}
