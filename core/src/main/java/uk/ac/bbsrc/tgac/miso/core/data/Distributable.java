package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

public interface Distributable {

  public boolean isDistributed();

  public Date getDistributionDate();

  public String getDistributionRecipient();

  public void setDistributed(boolean distributed);

  public void setDistributionDate(Date distributionDate);

  public void setDistributionRecipient(String distributionRecipient);

}
