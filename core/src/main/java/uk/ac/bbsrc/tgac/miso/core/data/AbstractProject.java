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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressTypeUserType;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;

/**
 * Skeleton implementation of a Project
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@MappedSuperclass
@TypeDefs({ @TypeDef(name = "progressTypeUserType", typeClass = ProgressTypeUserType.class) })
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
  private String shortName;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long projectId = AbstractProject.UNSAVED_ID;

  @Transient
  private Collection<Sample> samples = new HashSet<>();
  @Transient
  private Collection<Run> runs = new HashSet<>();
  @Transient
  private Collection<Study> studies = new HashSet<>();
  @Transient
  private Collection<ProjectOverview> overviews = new HashSet<>();
  @Transient
  private Collection<String> issueKeys = new HashSet<>();

  @Column
  @Type(type = "progressTypeUserType")
  private ProgressType progress;

  @OneToOne(targetEntity = ReferenceGenomeImpl.class)
  @JoinColumn(name = "referenceGenomeId", referencedColumnName = "referenceGenomeId", nullable = false)
  private ReferenceGenome referenceGenome;

  @Transient
  private SecurityProfile securityProfile = null;
  @Transient
  private final Set<MisoListener> listeners = new HashSet<>();
  private Date lastUpdated;

  // Cascade all operations except delete
  @ManyToMany(targetEntity = UserImpl.class, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
  @JoinTable(name = "Project_Watcher", joinColumns = { @JoinColumn(name = "projectId") },
      inverseJoinColumns = { @JoinColumn(name = "userId") })
  private Set<User> watchUsers = new HashSet<>();

  @Transient
  // not Hibernate-managed
  private Group watchGroup;

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
  public String getShortName() {
    return shortName;
  }

  @Override
  public void setShortName(String shortName) {
    this.shortName = shortName;
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
      if (p.getId() == overviewId) {
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
    if (overviews == null) {
      this.overviews = new HashSet<>();
      log.error("Attempt to set null project overview list.");
    }
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

  public void setWatchUsers(Set<User> watchUsers) {
    this.watchUsers = watchUsers;
  }

  public Set<User> getWatchUsers() {
    return watchUsers;
  }

  public void setWatchGroup(Group watchGroup) {
    this.watchGroup = watchGroup;
  }

  public Group getWatchGroup() {
    return watchGroup;
  }

  @Override
  public Set<User> getWatchers() {
    Set<User> allWatchers = new HashSet<>();
    if (watchGroup != null) allWatchers.addAll(watchGroup.getUsers());
    if (watchUsers != null) allWatchers.addAll(watchUsers);
    return allWatchers;
  }

  @Override
  public void addWatcher(User user) {
    watchUsers.add(user);
  }

  @Override
  public void removeWatcher(User user) {
    watchUsers.remove(user);
  }

  @Override
  public String getWatchableIdentifier() {
    return getName();
  }

  @Override
  public int compareTo(Project o) {
    if (getId() != 0L && o.getId() != 0L) {
      if (getId() < o.getId()) return -1;
      if (getId() > o.getId()) return 1;
    } else if (getAlias() != null && o.getAlias() != null) {
      return getAlias().compareTo(o.getAlias());
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

  @Override
  public ReferenceGenome getReferenceGenome() {
    return referenceGenome;
  }

  @Override
  public void setReferenceGenome(ReferenceGenome referenceGenome) {
    this.referenceGenome = referenceGenome;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(5, 35)
        .append(alias)
        .append(description)
        .append(progress)
        .append(referenceGenome)
        .append(shortName)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractProject other = (AbstractProject) obj;
    return new EqualsBuilder()
        .append(alias, other.alias)
        .append(description, other.description)
        .append(progress, other.progress)
        .append(referenceGenome, other.referenceGenome)
        .append(shortName, other.shortName)
        .isEquals();
  }

}
