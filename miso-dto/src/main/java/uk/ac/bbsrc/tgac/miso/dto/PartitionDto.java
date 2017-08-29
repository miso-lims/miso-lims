package uk.ac.bbsrc.tgac.miso.dto;

public class PartitionDto {
  private String containerName;
  private Long id;
  private int partitionNumber;
  private PoolDto pool;
  private String qcNotes;
  private long qcType;

  public String getContainerName() {
    return containerName;
  }

  public Long getId() {
    return id;
  }

  public int getPartitionNumber() {
    return partitionNumber;
  }

  public PoolDto getPool() {
    return pool;
  }

  public String getQcNotes() {
    return qcNotes;
  }

  public long getQcType() {
    return qcType;
  }

  public void setContainerName(String containerName) {
    this.containerName = containerName;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setPartitionNumber(int partitionNumber) {
    this.partitionNumber = partitionNumber;
  }

  public void setPool(PoolDto pool) {
    this.pool = pool;
  }

  public void setQcNotes(String qcNotes) {
    this.qcNotes = qcNotes;
  }

  public void setQcType(long qcType) {
    this.qcType = qcType;
  }
}
