package uk.ac.bbsrc.tgac.miso.core.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;

@JsonIgnoreProperties({ "sample" })
public interface SampleStock extends SampleAdditionalInfo {

  public static String CATEGORY_NAME = "Stock";

  Double getConcentration();

  void setConcentration(Double concentration);

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