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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Study is a subset of work carried out for a {@link Project}, comprising one or more {@link Experiment}s.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile" })
public interface Study extends SecurableByProfile, Submittable<Document>, Comparable, Deletable, Nameable {

  /** Field PREFIX */
  public static final String PREFIX = "STU";

  /**
   * Returns the studyId of this Study object.
   * 
   * @return Long studyId.
   */
  @Deprecated
  public Long getStudyId();

  /**
   * Sets the studyId of this Study object.
   * 
   * @param studyId
   *          studyId.
   */
  @Deprecated
  public void setStudyId(Long studyId);

  public void setId(long id);

  /**
   * Returns the project of this Study object.
   * 
   * @return Project project.
   */
  @JsonBackReference(value = "project")
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
  public String getAlias();

  /**
   * Sets the alias of this Study object.
   * 
   * @param alias
   *          alias.
   */
  public void setAlias(String alias);

  /**
   * Returns the abstract of this Study object.
   * 
   * @return String abstract.
   */
  public String getAbstract();

  /**
   * Sets the abstract of this Study object.
   * 
   * @param abs
   *          abs.
   */
  public void setAbstract(String abs);

  /**
   * Returns the studyType of this Study object.
   * 
   * @return String studyType.
   */
  public String getStudyType();

  /**
   * Sets the studyType of this Study object.
   * 
   * @param studyType
   *          studyType.
   */
  public void setStudyType(String studyType);

  /**
   * Registers an Experiment that will be undertaken as part of this Study
   * 
   * @param e
   *          of type Experiment
   * @throws MalformedExperimentException
   *           when the Experiment being registered is not valid
   */
  public void addExperiment(Experiment e) throws MalformedExperimentException;

  /**
   * Returns the registered Experiments of this Study object.
   * 
   * @return Collection<Experiment> experiments.
   */
  // @JsonManagedReference(value="experiments")
  public Collection<Experiment> getExperiments();

  /**
   * Sets the experiments of this Study object.
   * 
   * @param experiments
   *          experiments.
   */
  public void setExperiments(Collection<Experiment> experiments);

  public Collection<ChangeLog> getChangeLog();

  /**
   * Returns the user who last modified this item.
   */
  public User getLastModifier();

  /**
   * Sets the user who last modified this item. It should always be set to the current user on save.
   */
  public void setLastModifier(User user);
}
