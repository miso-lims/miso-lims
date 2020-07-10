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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;

/**
 * A Project represents the top level object in the MISO data model. A Project couples together {@link Study} and {@link Sample} objects to
 * record information about a given sequencing project.
 * <p/>
 * A Project's status is tracked by its {@link StatusType} enumeration.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Project extends Comparable<Project>, Deletable, Nameable, Serializable, Attachable, ChangeLoggable {

  /** Field PREFIX */
  public static final String PREFIX = "PRO";

  String getDescription();

  void setDescription(String description);

  void setName(String name);

  /**
   * Returns the alias of this Project object.
   * 
   * @return String alias.
   */
  String getAlias();

  /**
   * Sets the alias of this Project object.
   * 
   * @param alias
   *          alias.
   */
  void setAlias(String alias);

  /**
   * Returns the short name, used as a prefix for generating sample names, or the alias if not specified.
   */
  String getShortName();

  /**
   * Sets the short name, used as a prefix for generating sample names.
   */
  void setShortName(String shortName);

  /**
   * @return the status of this Project
   */
  StatusType getStatus();

  /**
   * Sets the status of this Project
   * 
   * @param status
   */
  void setStatus(StatusType status);

  /**
   * Returns the registered samples of this Project object.
   * 
   * @return Collection<Sample> samples.
   */
  Collection<Sample> getSamples();

  /**
   * Returns the registered studies of this Project object.
   * 
   * @return Collection<Study> studies.
   */
  Collection<Study> getStudies();

  /**
   * Registers a collection of samples to this Project object.
   * 
   * @param samples
   *          samples.
   */
  void setSamples(Collection<Sample> samples);

  /**
   * Register that a Sample has been recieved in relation to this Project
   * 
   * @param sample
   *          of type Sample
   */
  void addSample(Sample sample);

  /**
   * Registers a collection of studies to this Project object.
   * 
   * @param studies
   *          studies.
   */
  void setStudies(Collection<Study> studies);

  public ReferenceGenome getReferenceGenome();

  public void setReferenceGenome(ReferenceGenome referenceGenome);

  public TargetedSequencing getDefaultTargetedSequencing();

  public void setDefaultTargetedSequencing(TargetedSequencing defaultTargetedSequencing);

  @Override
  public boolean isSaved();

  public boolean isClinical();

  public void setClinical(boolean clinical);

  public boolean isSecondaryNaming();

  public void setSecondaryNaming(boolean secondaryNaming);

  public String getRebNumber();

  public void setRebNumber(String rebNumber);

  public Date getRebExpiry();

  public void setRebExpiry(Date rebExpiry);

}
