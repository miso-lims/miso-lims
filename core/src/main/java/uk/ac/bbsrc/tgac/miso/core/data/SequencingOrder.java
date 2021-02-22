package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;

public interface SequencingOrder extends Deletable, Timestamped {

  public Pool getPool();

  public void setPool(Pool pool);

  public Integer getPartitions();

  public void setPartitions(Integer partitions);

  public SequencingParameters getSequencingParameter();

  public void setSequencingParameters(SequencingParameters parameter);

  public String getDescription();

  public void setDescription(String description);

  public RunPurpose getPurpose();

  public void setPurpose(RunPurpose purpose);

  public SequencingContainerModel getContainerModel();

  public void setContainerModel(SequencingContainerModel containerModel);

}
