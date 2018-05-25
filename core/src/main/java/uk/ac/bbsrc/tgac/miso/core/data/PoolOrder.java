package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface PoolOrder extends Deletable {

  public void setId(Long id);

  public Pool getPool();

  public void setPool(Pool pool);

  public Integer getPartitions();

  public void setPartitions(Integer partitions);

  public SequencingParameters getSequencingParameter();

  public void setSequencingParameter(SequencingParameters parameter);

  public User getCreatedBy();

  public void setCreatedBy(User createdBy);

  public Date getCreationDate();

  public void setCreationDate(Date creationDate);

  public User getUpdatedBy();

  public void setUpdatedBy(User updatedBy);

  public Date getLastUpdated();

  public void setLastUpdated(Date lastUpdated);

}
