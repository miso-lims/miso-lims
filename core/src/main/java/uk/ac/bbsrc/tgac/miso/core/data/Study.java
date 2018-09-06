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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Collection;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Study is a subset of work carried out for a {@link Project}, comprising one or more {@link Experiment}s.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Study extends SecurableByProfile, Comparable<Study>, Deletable, Nameable, ChangeLoggable, Aliasable, Serializable {

  /** Field PREFIX */
  public static final String PREFIX = "STU";

  /**
   * Returns the project of this Study object.
   * 
   * @return Project project.
   */
  public Project getProject();

  /**
   * Sets the project of this Study object.
   * 
   * @param project
   *          project.
   */
  public void setProject(Project project);

  /**
   * Returns the accession of this Study object.
   * 
   * @return String accession.
   */
  public String getAccession();

  /**
   * Sets the accession of this Study object.
   * 
   * @param accession
   *          accession.
   */
  public void setAccession(String accession);

  /**
   * Sets the name of this Study object.
   * 
   * @param name
   *          name.
   */
  public void setName(String name);

  /**
   * Returns the description of this Study object.
   * 
   * @return String description.
   */
  public String getDescription();

  /**
   * Sets the description of this Study object.
   * 
   * @param description
   *          description.
   */
  public void setDescription(String description);

  /**
   * Returns the alias of this Study object.
   * 
   * @return String alias.
   */
  @Override
  public String getAlias();

  /**
   * Sets the alias of this Study object.
   * 
   * @param alias
   *          alias.
   */
  public void setAlias(String alias);

  /**
   * Returns the studyType of this Study object.
   * 
   * @return StudyType studyType.
   */
  public StudyType getStudyType();

  /**
   * Sets the studyType of this Study object.
   * 
   * @param studyType
   *          studyType.
   */
  public void setStudyType(StudyType studyType);

  /**
   * Registers an Experiment that will be undertaken as part of this Study
   * 
   * @param e
   *          of type Experiment
   */
  public void addExperiment(Experiment e);

  /**
   * Returns the registered Experiments of this Study object.
   * 
   * @return Collection<Experiment> experiments.
   */
  public Collection<Experiment> getExperiments();

  /**
   * Sets the experiments of this Study object.
   * 
   * @param experiments
   *          experiments.
   */
  public void setExperiments(Collection<Experiment> experiments);

  @Override
  public Collection<ChangeLog> getChangeLog();

  /**
   * Returns the user who last modified this item.
   */
  @Override
  public User getLastModifier();

  /**
   * Sets the user who last modified this item. It should always be set to the current user on save.
   */
  @Override
  public void setLastModifier(User user);
}
