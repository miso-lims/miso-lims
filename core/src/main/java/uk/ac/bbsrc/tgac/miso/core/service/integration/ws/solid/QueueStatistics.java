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

package uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for queueStatistics complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="queueStatistics">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="activeJobs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="activeNodeString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="blockedJobs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idleJobs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totalJobs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="xml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queueStatistics", propOrder = { "activeJobs", "activeNodeString", "blockedJobs", "idleJobs", "totalJobs", "xml" })
public class QueueStatistics {

  protected int activeJobs;
  protected String activeNodeString;
  protected int blockedJobs;
  protected int idleJobs;
  protected int totalJobs;
  protected String xml;

  /**
   * Gets the value of the activeJobs property.
   * 
   */
  public int getActiveJobs() {
    return activeJobs;
  }

  /**
   * Sets the value of the activeJobs property.
   * 
   */
  public void setActiveJobs(int value) {
    this.activeJobs = value;
  }

  /**
   * Gets the value of the activeNodeString property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getActiveNodeString() {
    return activeNodeString;
  }

  /**
   * Sets the value of the activeNodeString property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setActiveNodeString(String value) {
    this.activeNodeString = value;
  }

  /**
   * Gets the value of the blockedJobs property.
   * 
   */
  public int getBlockedJobs() {
    return blockedJobs;
  }

  /**
   * Sets the value of the blockedJobs property.
   * 
   */
  public void setBlockedJobs(int value) {
    this.blockedJobs = value;
  }

  /**
   * Gets the value of the idleJobs property.
   * 
   */
  public int getIdleJobs() {
    return idleJobs;
  }

  /**
   * Sets the value of the idleJobs property.
   * 
   */
  public void setIdleJobs(int value) {
    this.idleJobs = value;
  }

  /**
   * Gets the value of the totalJobs property.
   * 
   */
  public int getTotalJobs() {
    return totalJobs;
  }

  /**
   * Sets the value of the totalJobs property.
   * 
   */
  public void setTotalJobs(int value) {
    this.totalJobs = value;
  }

  /**
   * Gets the value of the xml property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getXml() {
    return xml;
  }

  /**
   * Sets the value of the xml property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setXml(String value) {
    this.xml = value;
  }

}
