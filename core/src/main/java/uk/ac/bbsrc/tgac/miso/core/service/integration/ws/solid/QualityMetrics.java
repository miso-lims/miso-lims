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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for qualityMetrics complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="qualityMetrics">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cycle" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="filePath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primer" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="primerSet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qualityMetricsDetails" type="{http://solid.aga.appliedbiosystems.com}qualityMetricsDetails" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sample" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeStamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "qualityMetrics", propOrder = { "cycle", "filePath", "name", "primer", "primerSet", "qualityMetricsDetails", "sample",
    "timeStamp", "version", "xml" })
public class QualityMetrics {

  protected int cycle;
  protected String filePath;
  protected String name;
  protected int primer;
  protected String primerSet;
  @XmlElement(nillable = true)
  protected List<QualityMetricsDetails> qualityMetricsDetails;
  protected String sample;
  protected String timeStamp;
  protected int version;
  protected String xml;

  /**
   * Gets the value of the cycle property.
   * 
   */
  public int getCycle() {
    return cycle;
  }

  /**
   * Sets the value of the cycle property.
   * 
   */
  public void setCycle(int value) {
    this.cycle = value;
  }

  /**
   * Gets the value of the filePath property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getFilePath() {
    return filePath;
  }

  /**
   * Sets the value of the filePath property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setFilePath(String value) {
    this.filePath = value;
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
   * Gets the value of the primer property.
   * 
   */
  public int getPrimer() {
    return primer;
  }

  /**
   * Sets the value of the primer property.
   * 
   */
  public void setPrimer(int value) {
    this.primer = value;
  }

  /**
   * Gets the value of the primerSet property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getPrimerSet() {
    return primerSet;
  }

  /**
   * Sets the value of the primerSet property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setPrimerSet(String value) {
    this.primerSet = value;
  }

  /**
   * Gets the value of the qualityMetricsDetails property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
   * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the qualityMetricsDetails property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getQualityMetricsDetails().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link QualityMetricsDetails }
   * 
   * 
   */
  public List<QualityMetricsDetails> getQualityMetricsDetails() {
    if (qualityMetricsDetails == null) {
      qualityMetricsDetails = new ArrayList<QualityMetricsDetails>();
    }
    return this.qualityMetricsDetails;
  }

  /**
   * Gets the value of the sample property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getSample() {
    return sample;
  }

  /**
   * Sets the value of the sample property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setSample(String value) {
    this.sample = value;
  }

  /**
   * Gets the value of the timeStamp property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getTimeStamp() {
    return timeStamp;
  }

  /**
   * Sets the value of the timeStamp property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setTimeStamp(String value) {
    this.timeStamp = value;
  }

  /**
   * Gets the value of the version property.
   * 
   */
  public int getVersion() {
    return version;
  }

  /**
   * Sets the value of the version property.
   * 
   */
  public void setVersion(int value) {
    this.version = value;
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
