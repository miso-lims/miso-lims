/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import java.net.UnknownHostException;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A SequencerReference is a sequencing machine specifically designated with a {@link Platform}
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface SequencerReference extends Nameable, Deletable {
  /**
   * Sets the id of this SequencerReference object.
   * 
   * @param id
   *          id.
   */
  void setId(Long id);

  /**
   * Sets the name of this SequencerReference object.
   * 
   * @param name
   *          name.
   */
  void setName(String name);

  /**
   * Sets the platform of this SequencerReference object.
   * 
   * @param platform
   *          platform.
   */
  void setPlatform(Platform platform);

  /**
   * Returns the platform of this SequencerReference object.
   * 
   * @return Platform platform.
   */
  Platform getPlatform();
  
  /**
   * Sets the serial number of this SequencerReference object.
   * 
   * @param serialNumber of type String
   */
  public void setSerialNumber(String serialNumber);
  
  /**
   * Returns the serial number of this SequencerReference object.
   * 
   * @return String serialNumber
   */
  public String getSerialNumber();

  /**
   * Sets the ipAddress of this SequencerReference object.
   * 
   * @param ip of type String
   */
  void setIpAddress(String ip);

  /**
   * Returns the String ipAddress of this SequencerReference object.
   * 
   * @return String ipAddress.
   */
  String getIpAddress();

  /**
   * Returns the fully qualified domain name (FQDN) of this SequencerReference object.
   * 
   * @return String FQDN.
   * @throws UnknownHostException
   */
  String getFQDN() throws UnknownHostException;

  /**
   * Sets the date when use of this sequencer began
   * 
   * @param date
   */
  public void setDateCommissioned(Date date);
  
  /**
   * @return the date when use of this sequencer began
   */
  public Date getDateCommissioned();
  
  /**
   * Sets the date when use of this sequencer ended
   * 
   * @param date
   */
  public void setDateDecommissioned(Date date);
  
  /**
   * @return the date when use of this sequencer ended
   */
  public Date getDateDecommissioned();
  
  /**
   * Sets the upgraded sequencer reference, which is a new version of this same sequencer, likely renamed during an upgrade
   * 
   * @param sequencer
   */
  public void setUpgradedSequencerReference(SequencerReference sequencer);
  
  /**
   * @return the upgraded sequencer reference, which is a new version of this same sequencer, likely renamed during an upgrade
   */
  @JsonManagedReference
  public SequencerReference getUpgradedSequencerReference();
  
  /**
   * @return true if the sequencer is currently being used in production; false if it is retired
   */
  public boolean isActive();
  
  /**
   * @param date the date when this sequencer was most recently serviced
   */
  public void setLastServicedDate(Date date);
  
  /**
   * @return the service date of the most recent service record for this sequencer, or null if there are no such service records
   */
  public Date getLastServicedDate();
  
}
