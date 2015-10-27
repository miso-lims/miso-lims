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
 * Java class for clusterStatus complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="clusterStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="avgLoad5" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="clusterName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="diskSpaceImagesString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="diskSpaceImagesTotal" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="diskSpaceImagesUsable" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="diskSpaceResultsString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="diskSpaceResultsTotal" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="diskSpaceResultsUsable" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="numOfCPUs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="queueStats" type="{http://solid.aga.appliedbiosystems.com}queueStatistics" minOccurs="0"/>
 *         &lt;element name="queues" type="{http://solid.aga.appliedbiosystems.com}jobQueue" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "clusterStatus", propOrder = { "avgLoad5", "clusterName", "diskSpaceImagesString", "diskSpaceImagesTotal",
    "diskSpaceImagesUsable", "diskSpaceResultsString", "diskSpaceResultsTotal", "diskSpaceResultsUsable", "numOfCPUs", "queueStats",
    "queues", "xml" })
public class ClusterStatus {

  protected double avgLoad5;
  protected String clusterName;
  protected String diskSpaceImagesString;
  protected long diskSpaceImagesTotal;
  protected long diskSpaceImagesUsable;
  protected String diskSpaceResultsString;
  protected long diskSpaceResultsTotal;
  protected long diskSpaceResultsUsable;
  protected int numOfCPUs;
  protected QueueStatistics queueStats;
  @XmlElement(nillable = true)
  protected List<JobQueue> queues;
  protected String xml;

  /**
   * Gets the value of the avgLoad5 property.
   * 
   */
  public double getAvgLoad5() {
    return avgLoad5;
  }

  /**
   * Sets the value of the avgLoad5 property.
   * 
   */
  public void setAvgLoad5(double value) {
    this.avgLoad5 = value;
  }

  /**
   * Gets the value of the clusterName property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getClusterName() {
    return clusterName;
  }

  /**
   * Sets the value of the clusterName property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setClusterName(String value) {
    this.clusterName = value;
  }

  /**
   * Gets the value of the diskSpaceImagesString property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getDiskSpaceImagesString() {
    return diskSpaceImagesString;
  }

  /**
   * Sets the value of the diskSpaceImagesString property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setDiskSpaceImagesString(String value) {
    this.diskSpaceImagesString = value;
  }

  /**
   * Gets the value of the diskSpaceImagesTotal property.
   * 
   */
  public long getDiskSpaceImagesTotal() {
    return diskSpaceImagesTotal;
  }

  /**
   * Sets the value of the diskSpaceImagesTotal property.
   * 
   */
  public void setDiskSpaceImagesTotal(long value) {
    this.diskSpaceImagesTotal = value;
  }

  /**
   * Gets the value of the diskSpaceImagesUsable property.
   * 
   */
  public long getDiskSpaceImagesUsable() {
    return diskSpaceImagesUsable;
  }

  /**
   * Sets the value of the diskSpaceImagesUsable property.
   * 
   */
  public void setDiskSpaceImagesUsable(long value) {
    this.diskSpaceImagesUsable = value;
  }

  /**
   * Gets the value of the diskSpaceResultsString property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getDiskSpaceResultsString() {
    return diskSpaceResultsString;
  }

  /**
   * Sets the value of the diskSpaceResultsString property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setDiskSpaceResultsString(String value) {
    this.diskSpaceResultsString = value;
  }

  /**
   * Gets the value of the diskSpaceResultsTotal property.
   * 
   */
  public long getDiskSpaceResultsTotal() {
    return diskSpaceResultsTotal;
  }

  /**
   * Sets the value of the diskSpaceResultsTotal property.
   * 
   */
  public void setDiskSpaceResultsTotal(long value) {
    this.diskSpaceResultsTotal = value;
  }

  /**
   * Gets the value of the diskSpaceResultsUsable property.
   * 
   */
  public long getDiskSpaceResultsUsable() {
    return diskSpaceResultsUsable;
  }

  /**
   * Sets the value of the diskSpaceResultsUsable property.
   * 
   */
  public void setDiskSpaceResultsUsable(long value) {
    this.diskSpaceResultsUsable = value;
  }

  /**
   * Gets the value of the numOfCPUs property.
   * 
   */
  public int getNumOfCPUs() {
    return numOfCPUs;
  }

  /**
   * Sets the value of the numOfCPUs property.
   * 
   */
  public void setNumOfCPUs(int value) {
    this.numOfCPUs = value;
  }

  /**
   * Gets the value of the queueStats property.
   * 
   * @return possible object is {@link QueueStatistics }
   * 
   */
  public QueueStatistics getQueueStats() {
    return queueStats;
  }

  /**
   * Sets the value of the queueStats property.
   * 
   * @param value
   *          allowed object is {@link QueueStatistics }
   * 
   */
  public void setQueueStats(QueueStatistics value) {
    this.queueStats = value;
  }

  /**
   * Gets the value of the queues property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
   * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the queues property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getQueues().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link JobQueue }
   * 
   * 
   */
  public List<JobQueue> getQueues() {
    if (queues == null) {
      queues = new ArrayList<JobQueue>();
    }
    return this.queues;
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
