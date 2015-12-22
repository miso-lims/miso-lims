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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.Request;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;

/**
 * Skeleton implementation of a Project
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
public abstract class AbstractProject implements Project {
  protected static final Logger log = LoggerFactory.getLogger(AbstractProject.class);
  private static final long serialVersionUID = 1L;

  /**
   * Use this ID to indicate that a project has not yet been saved, and therefore does not yet have a unique ID.
   */
  public static final Long UNSAVED_ID = 0L;

  private Date creationDate = new Date();
  private String description = "";
  private String name = "";
  private String alias = "";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long projectId = AbstractProject.UNSAVED_ID;

  @OneToMany(cascade = CascadeType.ALL)
  private Collection<Request> requests = new HashSet<Request>();

  private Collection<Sample> samples = new HashSet<Sample>();
  private Collection<Run> runs = new HashSet<Run>();
  private Collection<Study> studies = new HashSet<Study>();
  private Collection<ProjectOverview> overviews = new HashSet<ProjectOverview>();
  private Collection<String> issueKeys = new HashSet<String>();

  @Enumerated(EnumType.STRING)
  private ProgressType progress;

  @OneToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile = null;
  private final Set<MisoListener> listeners = new HashSet<MisoListener>();
  private Date lastUpdated;
  private Set<User> watchers = new HashSet<User>();

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public long getId() {
    return projectId;
  }

  @Override
  public void setId(long id) {
    this.projectId = id;
  }

  @Override
  @Deprecated
  public Long getProjectId() {
    return projectId;
  }

  @Override
  public Collection<Request> getRequests() {
    return requests;
  }

  @Override
  public Collection<Sample> getSamples() {
    return samples;
  }

  @Override
  public Collection<Run> getRuns() {
    return runs;
  }

  @Override
  public Collection<Study> getStudies() {
    return studies;
  }

  @Override
  public Collection<ProjectOverview> getOverviews() {
    return overviews;
  }

  @Override
  public ProjectOverview getOverviewById(Long overviewId) {
    for (ProjectOverview p : getOverviews()) {
      if (p.getOverviewId().longValue() == overviewId) {
        return p;
      }
    }
    return null;
  }

  @Override
  public void setCreationDate(Date date) {
    this.creationDate = date;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  @Deprecated
  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  @Override
  public void setRequests(Collection<Request> requests) {
    this.requests = requests;
  }

  @Override
  public void setSamples(Collection<Sample> samples) {
    this.samples = samples;
    try {
      Collections.sort(Arrays.asList(this.samples), new AliasComparator(Sample.class));
    } catch (NoSuchMethodException e) {
      log.error("set samples", e);
    }
  }

  @Override
  public void setRuns(Collection<Run> runs) {
    this.runs = runs;
    try {
      Collections.sort(Arrays.asList(this.runs), new AliasComparator(Run.class));
    } catch (NoSuchMethodException e) {
      log.error("set runs", e);
    }
  }

  @Override
  public void addSample(Sample sample) {
    this.samples.add(sample);
    try {
      Collections.sort(Arrays.asList(this.samples), new AliasComparator(Sample.class));
    } catch (NoSuchMethodException e) {
      log.error("set sample", e);
    }
  }

  @Override
  public void setStudies(Collection<Study> studies) {
    this.studies = studies;
    try {
      Collections.sort(Arrays.asList(this.studies), new AliasComparator(Study.class));
    } catch (NoSuchMethodException e) {
      log.error("set studies", e);
    }
  }

  @Override
  public void setOverviews(Collection<ProjectOverview> overviews) {
    this.overviews = overviews;
    for (ProjectOverview po : overviews) {
      po.setProject(this);
    }
  }

  @Override
  public ProgressType getProgress() {
    return progress;
  }

  @Override
  public void setProgress(ProgressType progress) {
    this.progress = progress;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public boolean isDeletable() {
    return getId() != AbstractProject.UNSAVED_ID && getSamples().isEmpty() && getStudies().isEmpty();
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public void setSecurityProfile(SecurityProfile profile) {
    this.securityProfile = profile;
  }

  @Override
  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    // projects have no parents
  }

  @Override
  public boolean userCanRead(User user) {
    try {
      Boolean bool = false;
      if (BooleanUtils.isTrue(securityProfile.userCanRead(user))) {
        bool = true;
      }
      return bool;
    } catch (NullPointerException e) {
      return false;
    }
  }

  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  /**
   * Only those users who can write to the project can create requests on it.
   */
  public Request createRequest(User owner) throws SecurityException {
    if (!userCanWrite(owner)) {
      throw new SecurityException();
    }
    Request request = new Request(this, owner);
    getRequests().add(request);
    return request;
  }

  public void addStudy(Study s) {
    // do study validation
    s.setProject(this);

    // propagate security profiles down the hierarchy
    s.setSecurityProfile(this.securityProfile);

    // add
    this.studies.add(s);
  }

  @Override
  public Collection<String> getIssueKeys() {
    return issueKeys;
  }

  @Override
  public void setIssueKeys(Collection<String> issueKeys) {
    this.issueKeys = issueKeys;
  }

  @Override
  public void addIssueKey(String issueKey) {
    this.issueKeys.add(issueKey);
  }

  @Override
  public abstract void buildReport();

  @Override
  public Set<MisoListener> getListeners() {
    return this.listeners;
  }

  @Override
  public boolean addListener(MisoListener listener) {
    return listeners.add(listener);
  }

  @Override
  public boolean removeListener(MisoListener listener) {
    return listeners.remove(listener);
  }

  @Override
  public Set<User> getWatchers() {
    return watchers;
  }

  @Override
  public void setWatchers(Set<User> watchers) {
    this.watchers = watchers;
  }

  @Override
  public void addWatcher(User user) {
    watchers.add(user);
  }

  @Override
  public void removeWatcher(User user) {
    watchers.remove(user);
  }

  @Override
  public String getWatchableIdentifier() {
    return getName();
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof AbstractProject)) return false;
    AbstractProject them = (AbstractProject) obj;

    if (getId() == AbstractProject.UNSAVED_ID || them.getId() == AbstractProject.UNSAVED_ID) {
      if (getName() != null && them.getName() != null) {
        return getName().equals(them.getName());
      } else {
        return getAlias().equals(them.getAlias());
      }
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != 0L && getId() != AbstractProject.UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
      if (getAlias() != null) hashcode = PRIME * hashcode + getAlias().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Project s = (Project) o;
    if (getId() != 0L && s.getId() != 0L) {
      if (getId() < s.getId()) return -1;
      if (getId() > s.getId()) return 1;
    } else if (getAlias() != null && s.getAlias() != null) {
      return getAlias().compareTo(s.getAlias());
    }
    return 0;
  }

  /**
   * Format is "Date : Name : Description".
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getCreationDate());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getDescription());
    return sb.toString();
  }
}
