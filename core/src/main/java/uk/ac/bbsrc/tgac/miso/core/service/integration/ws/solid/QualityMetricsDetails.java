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
 * Java class for qualityMetricsDetails complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="qualityMetricsDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="statusLight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="threshold" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "qualityMetricsDetails", propOrder = { "message", "name", "statusLight", "threshold", "xml" })
public class QualityMetricsDetails {

  protected String message;
  protected String name;
  protected String statusLight;
  protected String threshold;
  protected String xml;

  /**
   * Gets the value of the message property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the value of the message property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setMessage(String value) {
    this.message = value;
  }

  /**
   * Gets the value of the name property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Gets the value of the statusLight property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getStatusLight() {
    return statusLight;
  }

  /**
   * Sets the value of the statusLight property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setStatusLight(String value) {
    this.statusLight = value;
  }

  /**
   * Gets the value of the threshold property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getThreshold() {
    return threshold;
  }

  /**
   * Sets the value of the threshold property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setThreshold(String value) {
    this.threshold = value;
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
