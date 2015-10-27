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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for logMessage complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="logMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="component" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="severity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "logMessage", propOrder = { "component", "dateCreated", "details", "message", "severity", "xml" })
public class LogMessage {

  protected String component;
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar dateCreated;
  protected String details;
  protected String message;
  protected String severity;
  protected String xml;

  /**
   * Gets the value of the component property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getComponent() {
    return component;
  }

  /**
   * Sets the value of the component property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setComponent(String value) {
    this.component = value;
  }

  /**
   * Gets the value of the dateCreated property.
   * 
   * @return possible object is {@link XMLGregorianCalendar }
   * 
   */
  public XMLGregorianCalendar getDateCreated() {
    return dateCreated;
  }

  /**
   * Sets the value of the dateCreated property.
   * 
   * @param value
   *          allowed object is {@link XMLGregorianCalendar }
   * 
   */
  public void setDateCreated(XMLGregorianCalendar value) {
    this.dateCreated = value;
  }

  /**
   * Gets the value of the details property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getDetails() {
    return details;
  }

  /**
   * Sets the value of the details property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setDetails(String value) {
    this.details = value;
  }

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
   * Gets the value of the severity property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getSeverity() {
    return severity;
  }

  /**
   * Sets the value of the severity property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setSeverity(String value) {
    this.severity = value;
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
