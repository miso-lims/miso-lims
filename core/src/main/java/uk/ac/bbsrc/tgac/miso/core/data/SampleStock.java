package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;

public interface SampleStock extends DetailedSample {

  public static String CATEGORY_NAME = "Stock";

  /**
   * @return the short tandem repeat QC status for this SampleAnalyte
   */
  public StrStatus getStrStatus();

  /**
   * Sets the short tandem repeat QC status for this SampleAnalyte
   * 
   * @param strStatus
   */
  public void setStrStatus(StrStatus strStatus);

  /**
   * Convenience method for setting the short tandem repeat QC status for this SampleAnalyte
   * 
   * @param strStatus
   *          must match an existing {@link StrStatus} label
   * @throws IllegalArgumentException
   *           if no StrStatus with the requested label exists
   */
  public void setStrStatus(String strStatus);

  public Boolean getDNAseTreated();

  public void setDNAseTreated(Boolean dnaseTreated);

}