package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;

public interface SequencingOrder extends Deletable {

  public Pool getPool();

  public void setPool(Pool pool);

  public Integer getPartitions();

  public void setPartitions(Integer partitions);

  public SequencingParameters getSequencingParameter();

  public void setSequencingParameters(SequencingParameters parameter);

  public User getCreatedBy();

  public void setCreatedBy(User createdBy);

  public Date getCreationDate();

  public void setCreationDate(Date creationDate);

  public User getUpdatedBy();

  public void setUpdatedBy(User updatedBy);

  public Date getLastUpdated();

  public void setLastUpdated(Date lastUpdated);

  public String getDescription();

  public void setDescription(String description);

  public RunPurpose getPurpose();

  public void setPurpose(RunPurpose purpose);

  public SequencingContainerModel getContainerModel();

  public void setContainerModel(SequencingContainerModel containerModel);

}
