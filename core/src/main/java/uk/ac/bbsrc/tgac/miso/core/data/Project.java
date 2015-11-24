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
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Project represents the top level object in the MISO data model. A Project couples together {@link Study} and {@link Sample} objects to
 * record information about a given sequencing project.
 * <p/>
 * A Project's progress status is tracked by its {@link ProgressType} enumeration. A Project describes a collaborator or PI via the
 * {@link ProjectOverview} object, whereby proposed start and end dates, number of expected samples and watchers can be assigned.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
// @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile" })
public interface Project extends com.eaglegenomics.simlims.core.Project, Comparable, SecurableByProfile, Submittable<Document>, Reportable,
    Deletable, Watchable, Nameable, Alertable {
  /** Field PREFIX */
  public static final String PREFIX = "PRO";

  public void setId(long id);

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
   * Returns the progress of this Project object.
   * 
   * @return ProgressType progress.
   */
  ProgressType getProgress();

  /**
   * Sets the progress of this Project object.
   * 
   * @param progress
   *          progress.
   */
  void setProgress(ProgressType progress);

  /**
   * Returns the registered samples of this Project object.
   * 
   * @return Collection<Sample> samples.
   */
  Collection<Sample> getSamples();

  /**
   * Returns the registered samples of this Project object.
   * 
   * @return Collection<Run> runs.
   */
  Collection<Run> getRuns();

  /**
   * Returns the registered studies of this Project object.
   * 
   * @return Collection<Study> studies.
   */
  Collection<Study> getStudies();

  /**
   * Returns the overviews of this Project object.
   * 
   * @return Collection<ProjectOverview> overviews.
   */
  Collection<ProjectOverview> getOverviews();

  /**
   * Returns the overview of this Project object with the given ID
   * 
   * @param overviewId
   *          of type Long
   * @return ProjectOverview overview.
   */
  ProjectOverview getOverviewById(Long overviewId);

  /**
   * Registers a collection of samples to this Project object.
   * 
   * @param samples
   *          samples.
   */
  void setSamples(Collection<Sample> samples);

  /**
   * Registers a collection of samples to this Project object.
   * 
   * @param runs
   *          runs.
   */
  void setRuns(Collection<Run> runs);

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

  /**
   * Registers a collection of project overviews to this Project object.
   * 
   * @param overviews
   *          overviews.
   */
  void setOverviews(Collection<ProjectOverview> overviews);

  /**
   * Returns the associated issue keys of this Project object.
   * 
   * @return Collection<String> issueKeys.
   */
  Collection<String> getIssueKeys();

  /**
   * Registers a collection of issue keys from an issue tracker to this Project object.
   * 
   * @param issueKeys
   *          issueKeys.
   */
  void setIssueKeys(Collection<String> issueKeys);

  /**
   * Registers an issue key from an issue tracker to this Project object.
   * 
   * @param issueKey
   *          issueKey.
   */
  void addIssueKey(String issueKey);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);
}
