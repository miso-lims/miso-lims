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
 * Java class for jobQueue complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="jobQueue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="queueName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="queuedJobs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="runJobs" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "jobQueue", propOrder = { "queueName", "queuedJobs", "runJobs", "xml" })
public class JobQueue {

  protected String queueName;
  protected int queuedJobs;
  protected int runJobs;
  protected String xml;

  /**
   * Gets the value of the queueName property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getQueueName() {
    return queueName;
  }

  /**
   * Sets the value of the queueName property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setQueueName(String value) {
    this.queueName = value;
  }

  /**
   * Gets the value of the queuedJobs property.
   * 
   */
  public int getQueuedJobs() {
    return queuedJobs;
  }

  /**
   * Sets the value of the queuedJobs property.
   * 
   */
  public void setQueuedJobs(int value) {
    this.queuedJobs = value;
  }

  /**
   * Gets the value of the runJobs property.
   * 
   */
  public int getRunJobs() {
    return runJobs;
  }

  /**
   * Sets the value of the runJobs property.
   * 
   */
  public void setRunJobs(int value) {
    this.runJobs = value;
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
