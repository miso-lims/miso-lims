/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

import java.io.IOException;
import java.net.InetAddress;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A HardwareReference represents a piece of network-connected physical hardware that performs a given task, e.g. a sequencer
 * <p/>
 * Usually, the hardware itself is abstracted by a protocol of some kind, managed by a computer node, e.g. the head node that sits on top of
 * a sequencing machine. This computer will have an IP address on which a service will run to accept a
 * {@link uk.ac.bbsrc.tgac.miso.core.service.integration.contract.InterrogationQuery} and give back a
 * {@link uk.ac.bbsrc.tgac.miso.core.service.integration.contract.InterrogationResult}.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface HardwareReference extends Nameable {
  /**
   * Sets the id of this HardwareReference object.
   * 
   * @param id
   *          id.
   * 
   */
  void setId(Long id);

  /**
   * Sets the name of this HardwareReference object.
   * 
   * @param name
   *          name.
   * 
   */
  void setName(String name);

  /**
   * Sets the availability of this HardwareReference object.
   * 
   * @param available
   *          available.
   * 
   */
  void setAvailable(Boolean available);

  /**
   * Returns the availability of this HardwareReference object.
   * 
   * @return Boolean available.
   */
  Boolean getAvailable();

  /**
   * Check the availability of this HardwareReference within a given timeout period
   * 
   * @param timeout
   *          of type int
   * @throws IOException
   *           when the machine cannot be contacted
   */
  void checkAvailability(int timeout) throws IOException;

  /**
   * Sets the ipAddress of this HardwareReference object.
   * 
   * @param ip
   *          ipAddress.
   * 
   */
  void setIpAddress(InetAddress ip);

  /**
   * Returns the ipAddress of this HardwareReference object.
   * 
   * @return InetAddress ipAddress.
   */
  InetAddress getIpAddress();

  /**
   * Returns the fully qualified domain name (FQDN) of this HardwareReference object.
   * 
   * @return String FQDN.
   */
  String getFQDN();
}