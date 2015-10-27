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
 * Java class for run complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="run">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="creator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateCompleted" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateStarted" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flowcellNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="instrumentName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numberOfSamples" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="qualityMetrics" type="{http://solid.aga.appliedbiosystems.com}qualityMetrics" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sampleInfoList" type="{http://solid.aga.appliedbiosystems.com}sampleInfo" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "run", propOrder = { "creator", "dateCompleted", "dateCreated", "dateStarted", "flowcellNum", "id", "instrumentName",
    "name", "numberOfSamples", "qualityMetrics", "sampleInfoList", "xml" })
public class Run {

  protected String creator;
  protected String dateCompleted;
  protected String dateCreated;
  protected String dateStarted;
  protected String flowcellNum;
  protected String id;
  protected String instrumentName;
  protected String name;
  protected Integer numberOfSamples;
  @XmlElement(nillable = true)
  protected List<QualityMetrics> qualityMetrics;
  @XmlElement(nillable = true)
  protected List<SampleInfo> sampleInfoList;
  protected String xml;

  /**
   * Gets the value of the creator property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getCreator() {
    return creator;
  }

  /**
   * Sets the value of the creator property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setCreator(String value) {
    this.creator = value;
  }

  /**
   * Gets the value of the dateCompleted property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getDateCompleted() {
    return dateCompleted;
  }

  /**
   * Sets the value of the dateCompleted property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setDateCompleted(String value) {
    this.dateCompleted = value;
  }

  /**
   * Gets the value of the dateCreated property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getDateCreated() {
    return dateCreated;
  }

  /**
   * Sets the value of the dateCreated property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setDateCreated(String value) {
    this.dateCreated = value;
  }

  /**
   * Gets the value of the dateStarted property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getDateStarted() {
    return dateStarted;
  }

  /**
   * Sets the value of the dateStarted property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setDateStarted(String value) {
    this.dateStarted = value;
  }

  /**
   * Gets the value of the flowcellNum property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getFlowcellNum() {
    return flowcellNum;
  }

  /**
   * Sets the value of the flowcellNum property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setFlowcellNum(String value) {
    this.flowcellNum = value;
  }

  /**
   * Gets the value of the id property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setId(String value) {
    this.id = value;
  }

  /**
   * Gets the value of the instrumentName property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getInstrumentName() {
    return instrumentName;
  }

  /**
   * Sets the value of the instrumentName property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setInstrumentName(String value) {
    this.instrumentName = value;
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
   * Gets the value of the numberOfSamples property.
   * 
   * @return possible object is {@link Integer }
   * 
   */
  public Integer getNumberOfSamples() {
    return numberOfSamples;
  }

  /**
   * Sets the value of the numberOfSamples property.
   * 
   * @param value
   *          allowed object is {@link Integer }
   * 
   */
  public void setNumberOfSamples(Integer value) {
    this.numberOfSamples = value;
  }

  /**
   * Gets the value of the qualityMetrics property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
   * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the qualityMetrics property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getQualityMetrics().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link QualityMetrics }
   * 
   * 
   */
  public List<QualityMetrics> getQualityMetrics() {
    if (qualityMetrics == null) {
      qualityMetrics = new ArrayList<QualityMetrics>();
    }
    return this.qualityMetrics;
  }

  /**
   * Gets the value of the sampleInfoList property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
   * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the sampleInfoList property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getSampleInfoList().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link SampleInfo }
   * 
   * 
   */
  public List<SampleInfo> getSampleInfoList() {
    if (sampleInfoList == null) {
      sampleInfoList = new ArrayList<SampleInfo>();
    }
    return this.sampleInfoList;
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
