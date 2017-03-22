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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RunChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.util.UnicodeReader;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.1.0
 */
@Entity
@Table(name = "Run")
public class RunImpl extends AbstractRun implements Serializable {

  private static final long serialVersionUID = 1L;

  protected static final Logger log = LoggerFactory.getLogger(RunImpl.class);

  @ManyToMany(targetEntity = SequencerPartitionContainerImpl.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Run_SequencerPartitionContainer", joinColumns = {
      @JoinColumn(name = "Run_runId") }, inverseJoinColumns = {
          @JoinColumn(name = "containers_containerId") })
  private List<SequencerPartitionContainer> containers = new ArrayList<>();

  /**
   * Construct a new Run with a default empty SecurityProfile
   */
  public RunImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new Run with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public RunImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  public RunImpl(Experiment experiment, User user) {
    if (experiment.userCanRead(user)) {
      setSecurityProfile(experiment.getSecurityProfile());
    } else {
      setSecurityProfile(new SecurityProfile(user));
    }
  }

  @Override
  public List<SequencerPartitionContainer> getSequencerPartitionContainers() {
    if (this.containers != null) Collections.sort(this.containers);
    return containers;
  }

  @Override
  public void setSequencerPartitionContainers(List<SequencerPartitionContainer> containers) {
    this.containers = containers;
  }

  @Override
  public void setSequencerPartitionContainer(SequencerPartitionContainer container) {
    if (this.containers != null && this.containers.size() > 1) {
      throw new IllegalArgumentException("Cannot set single container on a run with multiple containers already linked!");
    } else {
      List<SequencerPartitionContainer> list = new ArrayList<>();
      list.add(container);
      this.containers = list;
    }
  }

  @Override
  public void addSequencerPartitionContainer(SequencerPartitionContainer f) {
    f.setSecurityProfile(getSecurityProfile());
    if (f.getId() == 0L && f.getIdentificationBarcode() == null) {
      // can't validate it so add it anyway. this will only usually be the case for new run population.
      this.containers.add(f);
    } else {
      if (!this.containers.contains(f)) {
        this.containers.add(f);
      }
    }
  }

  /**
   * Method buildReport ...
   */
  @Override
  public void buildReport() {
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    RunChangeLog changeLog = new RunChangeLog();
    changeLog.setRun(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  // https://jira.oicr.on.ca/browse/GLT-1611
  // Copied from IlluminaRun. Can be removed in the future.
  public void illuminaRunConfig(String statusXml, User user) {
    try {
      setPlatformType(PlatformType.ILLUMINA);
      Document statusDoc = SubmissionUtils.emptyDocument();
      if (!isStringEmptyOrNull(statusXml)) {
        SubmissionUtils.transform(new UnicodeReader(statusXml), statusDoc);

        String runName = (statusDoc.getElementsByTagName("RunName").item(0).getTextContent());
        setPairedEnd(false);

        if (!statusDoc.getDocumentElement().getTagName().equals("error")) {
          if (statusDoc.getElementsByTagName("IsPairedEndRun").getLength() > 0) {
            boolean paired = Boolean.parseBoolean(statusDoc.getElementsByTagName("IsPairedEndRun").item(0).getTextContent().toLowerCase());
            setPairedEnd(paired);
          }
        }

        String runDirRegex = "[\\d]+_([A-z0-9\\-])+_([\\d])+_([A-z0-9_\\+\\-]*)";
        Matcher m = Pattern.compile(runDirRegex).matcher(runName);
        if (m.matches()) {
          setPlatformRunId(Integer.parseInt(m.group(2)));
        }

        setAlias(runName);
        setFilePath(runName);
        setDescription(m.group(3));
        setPlatformType(PlatformType.ILLUMINA);

        StatusImpl status = new StatusImpl();
        status.parseIlluminaStatusXml(statusXml);
        setStatus(status);
        if (user != null) {
          setSecurityProfile(new SecurityProfile(user));
        } else {
          setSecurityProfile(new SecurityProfile());
        }
      } else {
        log.error("No status XML for this run");
      }
    } catch (ParserConfigurationException e) {
      log.error("Cannot parse status", e);
    } catch (TransformerException e) {
      log.error("Cannot parse status: " + statusXml, e);
    }
  }
}
