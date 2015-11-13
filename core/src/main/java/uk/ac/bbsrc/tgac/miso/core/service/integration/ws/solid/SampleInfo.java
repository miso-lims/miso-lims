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
 * Java class for sampleInfo complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sampleInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="libraryInfoList" type="{http://solid.aga.appliedbiosystems.com}libraryInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primaryAnalysisStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="secondaryAnalysisStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "sampleInfo", propOrder = { "libraryInfoList", "name", "primaryAnalysisStatus", "secondaryAnalysisStatus", "xml" })
public class SampleInfo {

  @XmlElement(nillable = true)
  protected List<LibraryInfo> libraryInfoList;
  protected String name;
  protected String primaryAnalysisStatus;
  protected String secondaryAnalysisStatus;
  protected String xml;

  /**
   * Gets the value of the libraryInfoList property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
   * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the libraryInfoList property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getLibraryInfoList().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link LibraryInfo }
   * 
   * 
   */
  public List<LibraryInfo> getLibraryInfoList() {
    if (libraryInfoList == null) {
      libraryInfoList = new ArrayList<LibraryInfo>();
    }
    return this.libraryInfoList;
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
   * Gets the value of the primaryAnalysisStatus property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getPrimaryAnalysisStatus() {
    return primaryAnalysisStatus;
  }

  /**
   * Sets the value of the primaryAnalysisStatus property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setPrimaryAnalysisStatus(String value) {
    this.primaryAnalysisStatus = value;
  }

  /**
   * Gets the value of the secondaryAnalysisStatus property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getSecondaryAnalysisStatus() {
    return secondaryAnalysisStatus;
  }

  /**
   * Sets the value of the secondaryAnalysisStatus property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setSecondaryAnalysisStatus(String value) {
    this.secondaryAnalysisStatus = value;
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
