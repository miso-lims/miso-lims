package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;

public interface Instrument extends Barcodable, Deletable, Nameable, Serializable {

  public void setName(String name);

  public void setInstrumentModel(InstrumentModel instrumentModel);

  public InstrumentModel getInstrumentModel();

  public void setSerialNumber(String serialNumber);

  public String getSerialNumber();

  public Workstation getWorkstation();

  public void setWorkstation(Workstation workstation);

  /**
   * Sets the date when use of this instrument began
   * 
   * @param date
   */
  public void setDateCommissioned(LocalDate date);

  /**
   * @return the date when use of this instrument began
   */
  public LocalDate getDateCommissioned();

  /**
   * Sets the date when use of this instrument ended
   * 
   * @param date
   */
  public void setDateDecommissioned(LocalDate date);

  /**
   * @return the date when use of this instrument ended
   */
  public LocalDate getDateDecommissioned();

  /**
   * Sets the upgraded instrument, which is a new version of this same instrument, likely renamed
   * during an upgrade
   * 
   * @param instrument
   */
  public void setUpgradedInstrument(Instrument instrument);

  /**
   * @return the upgraded instrument, which is a new version of this same instrument, likely renamed
   *         during an upgrade
   */
  public Instrument getUpgradedInstrument();

  /**
   * @return true if the instrument is currently being used in production; false if it is retired
   */
  public boolean isActive();

  /**
   * @param date the date when this instrument was most recently serviced
   */
  public void setLastServicedDate(Date date);

  /**
   * @return the service date of the most recent service record for this instrument, or null if there
   *         are no such service records
   */
  public Date getLastServicedDate();

  public Set<Run> getRuns();

  public void setRuns(Set<Run> runs);

  public Set<ServiceRecord> getServiceRecords();

  public void setServiceRecords(Set<ServiceRecord> serviceRecords);

  public boolean isOutOfService();

  public Set<InstrumentPosition> getOutOfServicePositions();

  public String getOutOfServicePositionsLabel();

  public RunPurpose getDefaultRunPurpose();

  public void setDefaultRunPurpose(RunPurpose defaultRunPurpose);

  public InstrumentPosition findPosition(long id);

  @Override
  int hashCode();

  @Override
  boolean equals(Object obj);

}
