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

package uk.ac.bbsrc.tgac.miso.core.manager;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.generateTemporaryName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RunChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.ProjectAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunQcException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.ReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerServiceRecordStore;
import uk.ac.bbsrc.tgac.miso.core.store.StatusStore;
import uk.ac.bbsrc.tgac.miso.core.store.SubmissionStore;
import uk.ac.bbsrc.tgac.miso.core.store.TargetedSequencingStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Implementation of a RequestManager to facilitate persistence operations on MISO model objects
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoRequestManager implements RequestManager {
  protected static final Logger log = LoggerFactory.getLogger(MisoRequestManager.class);

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Autowired
  private LibraryStore libraryStore;
  @Autowired
  private LibraryDilutionStore libraryDilutionStore;
  @Autowired
  private LibraryQcStore libraryQcStore;
  @Autowired
  private PlatformStore platformStore;
  @Autowired
  private ProjectStore projectStore;
  @Autowired
  private PoolStore poolStore;
  @Autowired
  private PoolQcStore poolQcStore;
  @Autowired
  private ReferenceGenomeDao referenceGenomeDao;
  @Autowired
  private RunStore runStore;
  @Autowired
  private RunQcStore runQcStore;
  @Autowired
  private SampleStore sampleStore;
  @Autowired
  private TargetedSequencingStore targetedSequencingStore;
  @Autowired
  private SampleQcStore sampleQcStore;
  @Autowired
  private SequencerPartitionContainerStore sequencerPartitionContainerStore;
  @Autowired
  private SequencerReferenceStore sequencerReferenceStore;
  @Autowired
  private SequencerServiceRecordStore sequencerServiceRecordStore;
  @Autowired
  private StatusStore statusStore;
  @Autowired
  private SubmissionStore submissionStore;
  @Autowired
  private ChangeLogStore changeLogStore;
  @Autowired
  private BoxStore boxStore;
  @Autowired
  private SecurityStore securityStore;
  @Autowired
  private SecurityProfileStore securityProfileStore;
  @Autowired
  private LibraryDesignDao libraryDesignDao;
  @Autowired
  private LibraryDesignCodeDao libraryDesignCodeDao;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private PoolAlertManager poolAlertManager;
  @Autowired
  private RunAlertManager runAlertManager;
  @Autowired
  private ProjectAlertManager projectAlertManager;
  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setSecurityProfileStore(SecurityProfileStore securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
  }

  public void setPoolAlertManager(PoolAlertManager poolAlertManager) {
    this.poolAlertManager = poolAlertManager;
  }

  public void setProjectAlertManager(ProjectAlertManager projectAlertManager) {
    this.projectAlertManager = projectAlertManager;
  }

  public void setRunAlertManager(RunAlertManager runAlertManager) {
    this.runAlertManager = runAlertManager;
  }

  public void setBoxStore(BoxStore boxStore) {
    this.boxStore = boxStore;
  }

  public void setLibraryDesignCodeDao(LibraryDesignCodeDao libraryDesignCodeDao) {
    this.libraryDesignCodeDao = libraryDesignCodeDao;
  }

  public void setLibraryStore(LibraryStore libraryStore) {
    this.libraryStore = libraryStore;
  }

  public void setLibraryQcStore(LibraryQcStore libraryQcStore) {
    this.libraryQcStore = libraryQcStore;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setPlatformStore(PlatformStore platformStore) {
    this.platformStore = platformStore;
  }

  public void setPoolStore(PoolStore poolStore) {
    this.poolStore = poolStore;
  }

  public void setPoolQcStore(PoolQcStore poolQcStore) {
    this.poolQcStore = poolQcStore;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public void setReferenceGenomeStore(ReferenceGenomeDao referenceGenomeStore) {
    this.referenceGenomeDao = referenceGenomeStore;
  }

  public void setRunStore(RunStore runStore) {
    this.runStore = runStore;
  }

  public void setRunQcStore(RunQcStore runQcStore) {
    this.runQcStore = runQcStore;
  }

  public void setSampleStore(SampleStore sampleStore) {
    this.sampleStore = sampleStore;
  }

  public void setSampleQcStore(SampleQcStore sampleQcStore) {
    this.sampleQcStore = sampleQcStore;
  }

  public void setSequencerPartitionContainerStore(SequencerPartitionContainerStore sequencerPartitionContainerStore) {
    this.sequencerPartitionContainerStore = sequencerPartitionContainerStore;
  }

  public void setSequencerReferenceStore(SequencerReferenceStore sequencerReferenceStore) {
    this.sequencerReferenceStore = sequencerReferenceStore;
  }

  public void setSequencerServiceRecordStore(SequencerServiceRecordStore sequencerServiceRecordStore) {
    this.sequencerServiceRecordStore = sequencerServiceRecordStore;
  }

  public void setStatusStore(StatusStore statusStore) {
    this.statusStore = statusStore;
  }

  public void setSubmissionStore(SubmissionStore submissionStore) {
    this.submissionStore = submissionStore;
  }

  public void setTargetedSequencingStore(TargetedSequencingStore targetedSequencingStore) {
    this.targetedSequencingStore = targetedSequencingStore;
  }

  public void setAutoGenerateIdBarcodes(boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

  public void setLibraryDilutionStore(LibraryDilutionStore libraryDilutionStore) {
    this.libraryDilutionStore = libraryDilutionStore;
  }

  @Override
  public Collection<Project> listAllProjects() throws IOException {
    if (projectStore != null) {
      return projectStore.listAll();
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException {
    if (projectStore != null) {
      return projectStore.listAllWithLimit(limit);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Project> listAllProjectsBySearch(String query) throws IOException {
    if (projectStore != null) {
      return projectStore.listBySearch(query);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<ProjectOverview> listAllOverviewsByProjectId(long projectId) throws IOException {
    if (projectStore != null) {
      return projectStore.listOverviewsByProjectId(projectId);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllRuns() throws IOException {
    if (runStore != null) {
      return runStore.listAll();
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllRunsWithLimit(long limit) throws IOException {
    if (runStore != null) {
      return runStore.listAllWithLimit(limit);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllRunsBySearch(String query) throws IOException {
    if (runStore != null) {
      return runStore.listBySearch(query);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllRunsByProjectId(long projectId) throws IOException {
    if (runStore != null) {
      return runStore.listByProjectId(projectId);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listRunsByPoolId(long poolId) throws IOException {
    if (runStore != null) {
      return runStore.listByPoolId(poolId);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listRunsBySequencerPartitionContainerId(long containerId) throws IOException {
    if (runStore != null) {
      return runStore.listBySequencerPartitionContainerId(containerId);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllLS454Runs() throws IOException {
    if (runStore != null) {
      Collection<Run> accessibleRuns = new HashSet<>();
      for (Run run : runStore.listAll()) {
        if (run.getPlatformType() == PlatformType.LS454) {
          accessibleRuns.add(run);
        }
      }
      return accessibleRuns;
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllIlluminaRuns() throws IOException {
    if (runStore != null) {
      Collection<Run> accessibleRuns = new HashSet<>();
      for (Run run : runStore.listAll()) {
        if (run.getPlatformType() == PlatformType.ILLUMINA) {
          accessibleRuns.add(run);
        }
      }
      return accessibleRuns;
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllSolidRuns() throws IOException {
    if (runStore != null) {
      Collection<Run> accessibleRuns = new HashSet<>();
      for (Run run : runStore.listAll()) {
        if (run.getPlatformType() == PlatformType.SOLID) {
          accessibleRuns.add(run);
        }
      }
      return accessibleRuns;
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<RunQC> listAllRunQCsByRunId(long runId) throws IOException {
    if (runQcStore != null) {
      return runQcStore.listByRunId(runId);
    } else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listAllSamples() throws IOException {
    if (sampleStore != null) {
      return sampleStore.listAll();
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listAllSamplesWithLimit(long limit) throws IOException {
    if (sampleStore != null) {
      return sampleStore.listAllWithLimit(limit);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listAllSamplesByReceivedDate(long limit) throws IOException {
    if (sampleStore != null) {
      return sampleStore.listAllByReceivedDate(limit);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listAllSamplesBySearch(String query) throws IOException {
    if (sampleStore != null) {
      return sampleStore.listBySearch(query);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listAllSamplesByProjectId(long projectId) throws IOException {
    if (sampleStore != null) {
      return sampleStore.listByProjectId(projectId);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listSamplesByAlias(String alias) throws IOException {
    if (sampleStore != null) {
      return sampleStore.listByAlias(alias);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> getSamplesByIdList(List<Long> idList) throws IOException {
    if (sampleStore != null) {
      return sampleStore.getByIdList(idList);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<String> listAllSampleTypes() throws IOException {
    if (sampleStore != null) {
      return sampleStore.listAllSampleTypes();
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SampleQC> listAllSampleQCsBySampleId(long sampleId) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.listBySampleId(sampleId);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryQC> listAllLibraryQCsByLibraryId(long libraryId) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.listByLibraryId(libraryId);
    } else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<BoxableView> getBoxableViewsFromBarcodeList(Collection<String> barcodeList) throws IOException {
    return boxStore.getBoxableViewsByBarcodeList(barcodeList);
  }

  @Override
  public Collection<SequencerPartitionContainer> listSequencerPartitionContainersByRunId(long runId)
      throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listAllSequencerPartitionContainersByRunId(runId);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPartitionContainer> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listSequencerPartitionContainersByBarcode(barcode);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPartitionContainer> listAllSequencerPartitionContainers() throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listAll();
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Platform> listAllPlatforms() throws IOException {
    if (platformStore != null) {
      return platformStore.listAll();
    } else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<PlatformType> listActivePlatformTypes() throws IOException {
    Collection<PlatformType> activePlatformTypes = Lists.newArrayList();
    for (PlatformType platformType : PlatformType.values()) {
      for (SequencerReference sequencer : listSequencerReferencesByPlatformType(platformType)) {
        if (sequencer.isActive()) {
          activePlatformTypes.add(platformType);
          break;
        }
      }
    }
    return activePlatformTypes;
  }

  @Override
  public Collection<String> listDistinctPlatformNames() throws IOException {
    if (platformStore != null) {
      List<String> names = new ArrayList<>();
      for (PlatformType type : platformStore.listDistinctPlatformNames()) {
        names.add(type.getKey());
      }
      return names;
    } else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Platform> listPlatformsOfType(PlatformType platformType) throws IOException {
    if (platformStore != null) {
      Collection<Platform> platforms = new TreeSet<>();
      for (Platform platform : platformStore.listAll()) {
        if (platform.getPlatformType().equals(platformType)) {
          platforms.add(platform);
        }
      }
      return platforms;
    } else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Submission> listAllSubmissions() throws IOException {
    if (submissionStore != null) {
      return submissionStore.listAll();
    } else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listRunsBySequencerId(Long sequencerReferenceId) throws IOException {
    if (runStore != null) {
      return runStore.listBySequencerId(sequencerReferenceId);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerReference> listAllSequencerReferences() throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.listAll();
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerReference> listSequencerReferencesByPlatformType(PlatformType platformType) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.listByPlatformType(platformType);
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<QcType> listAllLibraryQcTypes() throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.listAllLibraryQcTypes();
    } else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<QcType> listAllPoolQcTypes() throws IOException {
    if (poolQcStore != null) {
      return poolQcStore.listAllPoolQcTypes();
    } else {
      throw new IOException("No poolQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<QcType> listAllRunQcTypes() throws IOException {
    if (runQcStore != null) {
      return runQcStore.listAllRunQcTypes();
    } else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Status> listAllStatus() throws IOException {
    if (statusStore != null) {
      return statusStore.listAll();
    } else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Status> listAllStatusBySequencerName(String sequencerName) throws IOException {
    if (statusStore != null) {
      return statusStore.listAllBySequencerName(sequencerName);
    } else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  // DELETES

  @Override
  public void deleteSample(Sample sample) throws IOException {
    if (sampleStore != null) {
      if (!sampleStore.remove(sample)) {
        throw new IOException("Unable to delete Sample. Make sure the sample has no child entitites.");
      }
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteRun(Run run) throws IOException {
    if (runStore != null) {
      if (!runStore.remove(run)) {
        throw new IOException("Unable to delete Run. Make sure the run has no child entitites.");
      }
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteRunQC(RunQC runQC) throws IOException {
    if (runQcStore != null) {
      if (!runQcStore.remove(runQC)) {
        throw new IOException("Unable to delete RunQC.");
      }
    } else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteSampleQC(SampleQC sampleQc) throws IOException {
    if (sampleQcStore != null) {
      if (!sampleQcStore.remove(sampleQc)) {
        throw new IOException("Unable to delete SampleQC.");
      }
    } else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteLibraryQC(LibraryQC libraryQc) throws IOException {
    if (libraryQcStore != null) {
      if (!libraryQcStore.remove(libraryQc)) {
        throw new IOException("Unable to delete LibraryQC.");
      }
    } else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException {
    if (sequencerReferenceStore != null) {
      if (!sequencerReferenceStore.remove(sequencerReference)) {
        throw new IOException("Unable to delete SequencerReference.");
      }
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteSequencerServiceRecord(SequencerServiceRecord serviceRecord) throws IOException {
    if (sequencerServiceRecordStore != null) {
      if (!sequencerServiceRecordStore.remove(serviceRecord)) {
        throw new IOException("Unable to delete Service Record.");
      }
    } else {
      throw new IOException("No sequencerServiceRecordStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteContainer(SequencerPartitionContainer container) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      if (!sequencerPartitionContainerStore.remove(sequencerPartitionContainerStore.get(container.getId()))) {
        throw new IOException("Unable to delete container.");
      }
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteRunNote(Run run, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Run managed = runStore.get(run.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Run " + run.getId());
    }
    managed.getNotes().remove(deleteNote);
    runStore.save(managed);
  }

  @Override
  public void deleteProjectOverviewNote(ProjectOverview projectOverview, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    ProjectOverview managed = projectStore.getProjectOverviewById(projectOverview.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for ProjectOverview " + projectOverview.getId());
    }
    managed.getNotes().remove(deleteNote);
    projectStore.saveOverview(managed);
  }

  // SAVES

  @Override
  public long saveProject(Project project) throws IOException {
    if (projectStore != null) {
      ValidationResult shortNameValidation = namingScheme.validateProjectShortName(project.getShortName());
      if (!shortNameValidation.isValid()) {
        throw new IOException("Cannot save project - invalid shortName: " + shortNameValidation.getMessage());
      }
      if (project.getId() == ProjectImpl.UNSAVED_ID) {
        resolveMembers(project.getSecurityProfile());
        project.setName(generateTemporaryName());
        projectStore.save(project);
        try {
          project.setName(namingScheme.generateNameFor(project));
        } catch (MisoNamingException e) {
          throw new IOException("Cannot save Project - issue with naming scheme", e);
        }
        LimsUtils.validateNameOrThrow(project, namingScheme);
      } else {
        Project original = projectStore.get(project.getId());
        original.setAlias(project.getAlias());
        original.setDescription(project.getDescription());
        original.setIssueKeys(project.getIssueKeys());
        original.setLastUpdated(project.getLastUpdated());
        original.setProgress(project.getProgress());
        original.setReferenceGenome(referenceGenomeDao.getReferenceGenome(project.getReferenceGenome().getId()));
        original.setShortName(project.getShortName());
        for (ProjectOverview po : project.getOverviews()) {
          if (po.getId() == ProjectOverview.UNSAVED_ID) {
            original.getOverviews().add(po);
          }
        }
        updateSecurityProfile(original.getSecurityProfile(), project.getSecurityProfile());
        project = original;
      }
      project.getSecurityProfile().setProfileId(saveSecurityProfile(project.getSecurityProfile()));
      long id = projectStore.save(project);
      if (projectAlertManager != null) projectAlertManager.update(project);
      return id;
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  private void updateSecurityProfile(SecurityProfile target, SecurityProfile source) throws IOException {
    target.setAllowAllInternal(source.isAllowAllInternal());
    target.setOwner(source.getOwner() == null ? null : securityStore.getUserById(source.getOwner().getUserId()));
    target.setReadGroups(loadManagedGroups(source.getReadGroups()));
    target.setWriteGroups(loadManagedGroups(source.getWriteGroups()));
    target.setReadUsers(loadManagedUsers(source.getReadUsers()));
    target.setWriteUsers(loadManagedUsers(source.getWriteUsers()));
  }

  private Collection<Group> loadManagedGroups(Collection<Group> original) throws IOException {
    if (original == null)
      return null;
    List<Group> managed = new ArrayList<>();
    for (Group item : original) {
      managed.add(securityStore.getGroupById(item.getGroupId()));
    }
    return managed;
  }

  private Collection<User> loadManagedUsers(Collection<User> original) throws IOException {
    if (original == null)
      return null;
    List<User> managed = new ArrayList<>();
    for (User item : original) {
      managed.add(securityStore.getUserById(item.getUserId()));
    }
    return managed;
  }

  private long saveSecurityProfile(SecurityProfile sp) throws IOException {
    return securityProfileStore.save(sp);
  }

  private long resolveMembers(SecurityProfile sp) throws IOException {
    if (sp == null) throw new NullPointerException("null SecurityProfile");
    sp.setOwner(securityStore.getUserById(sp.getOwner().getUserId()));
    sp.setReadUsers(resolveUsers(sp.getReadUsers()));
    sp.setReadGroups(resolveGroups(sp.getReadGroups()));
    sp.setWriteUsers(resolveUsers(sp.getWriteUsers()));
    sp.setWriteGroups(resolveGroups(sp.getWriteGroups()));
    return securityProfileStore.save(sp);
  }

  private Collection<User> resolveUsers(Collection<User> users) throws IOException {
    List<User> resolved = Lists.newArrayList();
    if (users != null) {
      for (User user : users) {
        User u = securityStore.getUserById(user.getUserId());
        if (u == null) throw new IllegalArgumentException("User " + user.getUserId() + " does not exist");
        resolved.add(u);
      }
    }
    return resolved;
  }

  private Collection<Group> resolveGroups(Collection<Group> groups) throws IOException {
    List<Group> resolved = Lists.newArrayList();
    if (groups != null) {
      for (Group group : groups) {
        Group g = securityStore.getGroupById(group.getGroupId());
        if (g == null) throw new IllegalArgumentException("Group " + group.getGroupId() + " does not exist");
        resolved.add(g);
      }
    }
    return resolved;
  }

  @Override
  public long saveProjectOverview(ProjectOverview overview) throws IOException {
    if (projectStore != null) {
      if (overview.getId() != ProjectOverview.UNSAVED_ID) {
        ProjectOverview original = projectStore.getProjectOverviewById(overview.getId());
        original.setAllLibrariesQcPassed(overview.getAllLibrariesQcPassed());
        original.setAllPoolsConstructed(overview.getAllPoolsConstructed());
        original.setAllRunsCompleted(overview.getAllRunsCompleted());
        original.setLocked(overview.getLocked());
        original.setPrimaryAnalysisCompleted(overview.getPrimaryAnalysisCompleted());
        original.setPrincipalInvestigator(overview.getPrincipalInvestigator());
        original.setStartDate(overview.getStartDate());
        original.setEndDate(overview.getEndDate());
        original.setNumProposedSamples(overview.getNumProposedSamples());
        original.setAllSampleQcPassed(overview.getAllSampleQcPassed());
        overview = original;
      }
      overview.setLastUpdated(new Date());
      return projectStore.saveOverview(overview);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    ProjectOverview managed = projectStore.getProjectOverviewById(overview.getId());
    note.setCreationDate(new Date());
    note.setOwner(getCurrentUser());
    managed.getNotes().add(note);
    projectStore.saveOverview(managed);
  }

  @Override
  public long saveRun(Run run) throws IOException {
    if (runStore != null) {
      run.getStatus().setInstrumentName(run.getSequencerReference().getName());

      if (run.getId() == AbstractRun.UNSAVED_ID) {
        run.setSecurityProfile(securityProfileStore.get(securityProfileStore.save(run.getSecurityProfile())));
        run.setLastModifier(getCurrentUser());
        run.getStatus().setLastUpdated(new Date());

        run.setName(generateTemporaryName());
        run.getStatus().setRunAlias(run.getName());

        if (!run.getSequencerPartitionContainers().isEmpty()) {
          List<SequencerPartitionContainer> containersToSave = new ArrayList<>();
          for (SequencerPartitionContainer container : run.getSequencerPartitionContainers()) {
            SequencerPartitionContainer managedContainer = getSequencerPartitionContainerById(container.getId());
            if (managedContainer == null) {
              containersToSave.add(container);
            } else {
              updateContainer(container, managedContainer);
              containersToSave.add(managedContainer);
            }
          }
          run.setSequencerPartitionContainers(containersToSave);
        }
        run.setId(runStore.save(run));
        try {
          String name = namingScheme.generateNameFor(run);
          run.setName(name);
          run.getStatus().setRunAlias(run.getAlias());

          validateNameOrThrow(run, namingScheme);
          return runStore.save(run);
        } catch (MisoNamingException e) {
          throw new IOException("Cannot save Run - issue with generating name");
        }
      } else {
        Run managed = getRunById(run.getId());
        log.info("update run: " + managed);
        managed.setLastModifier(getCurrentUser());
        managed.setAlias(run.getAlias());
        managed.setDescription(run.getDescription());
        managed.setPairedEnd(run.getPairedEnd());
        managed.setCycles(run.getCycles());
        managed.setFilePath(run.getFilePath());
        managed.getStatus().setHealth(run.getStatus().getHealth());
        managed.getStatus().setStartDate(run.getStatus().getStartDate());
        managed.getStatus().setCompletionDate(run.getStatus().getCompletionDate());
        managed.getStatus().setInstrumentName(run.getStatus().getInstrumentName());
        managed.getStatus().setRunAlias(managed.getAlias());
        managed.getStatus().setXml(run.getStatus().getXml());
        for (RunQC runQc : run.getRunQCs()) {
          if (!managed.getRunQCs().contains(runQc)) {
            try {
              managed.addQc(runQc);
            } catch (MalformedRunQcException e) {
              log.error("malformed runQC: ", e);
            }
          }
        }
        Set<String> originalContainers = Barcodable.extractLabels(managed.getSequencerPartitionContainers());
        List<SequencerPartitionContainer> saveContainers = new ArrayList<>();
        for (SequencerPartitionContainer container : run.getSequencerPartitionContainers()) {
          SequencerPartitionContainer managedContainer = getSequencerPartitionContainerById(container.getId());
          updateContainer(container, managedContainer);
          saveContainers.add(managedContainer);
        }
        Set<String> updatedContainers = Barcodable.extractLabels(saveContainers);
        managed.setSequencerPartitionContainers(saveContainers);
        managed.setNotes(run.getNotes());
        managed.setSequencingParameters(run.getSequencingParameters());

        Set<String> added = new TreeSet<>(updatedContainers);
        added.removeAll(originalContainers);
        Set<String> removed = new TreeSet<>(originalContainers);
        removed.removeAll(updatedContainers);
        if (!added.isEmpty() || !removed.isEmpty()) {
          StringBuilder message = new StringBuilder();
          message.append("Containers");
          LimsUtils.appendSet(message, added, "added");
          LimsUtils.appendSet(message, removed, "removed");

          RunChangeLog changeLog = new RunChangeLog();
          changeLog.setRun(managed);
          changeLog.setColumnsChanged("containers");
          changeLog.setSummary(message.toString());
          changeLog.setTime(new Date());
          changeLog.setUser(managed.getLastModifier());
          changeLogStore.create(changeLog);
        }
        runStore.save(managed);
        if (runAlertManager != null) runAlertManager.update(managed);
        return run.getId();
      }
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void saveRuns(Collection<Run> runs) throws IOException {
    if (runStore != null) {
      for (Run run : runs) {
        saveRun(run);
      }
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveRunQC(RunQC runQC) throws IOException {
    if (runQcStore != null) {
      Long runQcId = runQcStore.save(runQC);
      if (runAlertManager != null) runAlertManager.update(runQC.getRun());
      return runQcId;
    } else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void saveRunNote(Run run, Note note) throws IOException {
    Run managed = runStore.get(run.getId());
    note.setCreationDate(new Date());
    note.setOwner(getCurrentUser());
    managed.addNote(note);
    runStore.save(managed);
  }

  @Override
  public long saveSampleQC(SampleQC sampleQc) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.save(sampleQc);
    } else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  public User getCurrentUser() throws IOException {
    Authentication auth = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
    if (auth == null) {
      return null;
    }
    User user = securityManager.getUserByLoginName(auth.getName());
    if (user == null && auth.isAuthenticated()) {
      user = new UserImpl();
      user.setAdmin(true);
      user.setActive(true);
    }
    return user;
  }

  @Override
  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      if (container.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
        container.setSecurityProfile(securityProfileStore.get(securityProfileStore.save(container.getSecurityProfile())));
        container.setPlatform(platformStore.get(container.getPlatform().getId()));
        return sequencerPartitionContainerStore.save(container);
      } else {
        SequencerPartitionContainer managed = getSequencerPartitionContainerById(container.getId());
        updateContainer(container, managed);
        return sequencerPartitionContainerStore.save(managed);
      }
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  private void updateContainer(SequencerPartitionContainer source, SequencerPartitionContainer managed) throws IOException {
    managed.setIdentificationBarcode(source.getIdentificationBarcode());
    managed.setLocationBarcode(source.getLocationBarcode());
    managed.setValidationBarcode(source.getValidationBarcode());

    for (Partition sourcePartition : source.getPartitions()) {
      for (Partition managedPartition : managed.getPartitions()) {
        if (sourcePartition == null || managedPartition == null) {
          throw new IOException("Partition from " + (sourcePartition == null ? "client" : "database") + " is null.");
        }
        if (sourcePartition.getId() == managedPartition.getId()) {
          Pool sourcePool = sourcePartition.getPool();
          Pool managedPool = managedPartition.getPool();
          if (sourcePool == null && managedPool == null) continue;
          if (sourcePool == null && managedPool != null) {
            managedPartition.setPool(null);
          } else if (sourcePool != null && managedPool == null) {
            managedPartition.setPool(poolStore.get(sourcePool.getId()));
          } else if (sourcePool.getId() != managedPool.getId()) {
            managedPartition.setPool(poolStore.get(sourcePool.getId()));
          }
          break;
        }
      }
    }
  }

  @Override
  public long saveStatus(Status status) throws IOException {
    if (statusStore != null) {
      if (status.getId() != StatusImpl.UNSAVED_ID) {
        Status original = getStatusById(status.getId());
        original.setHealth(status.getHealth());
        original.setCompletionDate(status.getCompletionDate());
        original.setXml(status.getXml());
        original.setRunAlias(status.getRunAlias());
        status = original;
      }
      return statusStore.save(status);
    } else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSubmission(Submission submission) throws IOException {
    if (submissionStore != null) {
      return submissionStore.save(submission);
    } else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSequencerReference(SequencerReference sequencerReference) throws IOException {
    if (sequencerReferenceStore != null) {
      if (sequencerReference.getId() != AbstractSequencerReference.UNSAVED_ID) {
        SequencerReference original = getSequencerReferenceById(sequencerReference.getId());
        original.setPlatform(sequencerReference.getPlatform());
        original.setIpAddress(sequencerReference.getIpAddress());
        original.setSerialNumber(sequencerReference.getSerialNumber());
        original.setDateCommissioned(sequencerReference.getDateCommissioned());
        original.setDateDecommissioned(sequencerReference.getDateDecommissioned());
        original.setUpgradedSequencerReference(sequencerReference.getUpgradedSequencerReference() == null ? null
            : getSequencerReferenceById(sequencerReference.getUpgradedSequencerReference().getId()));
        sequencerReference = original;
      }
      return sequencerReferenceStore.save(sequencerReference);
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  // GETS
  @Override
  public Project getProjectById(long projectId) throws IOException {
    if (projectStore != null) {
      return projectStore.get(projectId);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Project getProjectByAlias(String projectAlias) throws IOException {
    if (projectStore != null) {
      return projectStore.getByAlias(projectAlias);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    if (projectStore != null) {
      return projectStore.getProjectOverviewById(overviewId);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Run getRunById(long runId) throws IOException {
    if (runStore != null) {
      return runStore.get(runId);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Run getRunByAlias(String alias) throws IOException {
    if (runStore != null) {
      return runStore.getByAlias(alias);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public RunQC getRunQCById(long runQcId) throws IOException {
    if (runQcStore != null) {
      return runQcStore.get(runQcId);
    } else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Sample getSampleById(long sampleId) throws IOException {
    if (sampleStore != null) {
      return sampleStore.get(sampleId);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Sample getSampleByBarcode(String barcode) throws IOException {
    if (sampleStore != null) {
      return sampleStore.getByBarcode(barcode);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SampleQC getSampleQCById(long sampleQcId) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.get(sampleQcId);
    } else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryQC getLibraryQCById(long libraryQcId) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.get(libraryQcId);
    } else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Partition getPartitionById(long partitionId) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.getPartitionById(partitionId);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerPartitionContainer getSequencerPartitionContainerById(long containerId) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.get(containerId);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Platform getPlatformById(long platformId) throws IOException {
    if (platformStore != null) {
      return platformStore.get(platformId);
    } else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Status getStatusById(long statusId) throws IOException {
    if (statusStore != null) {
      return statusStore.get(statusId);
    } else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Status getStatusByRunName(String runName) throws IOException {
    if (statusStore != null) {
      return statusStore.getByRunAlias(runName);
    } else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Submission getSubmissionById(long submissionId) throws IOException {
    if (submissionStore != null) {
      return submissionStore.get(submissionId);
    } else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerReference getSequencerReferenceById(long referenceId) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.get(referenceId);
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.getByName(referenceName);
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerReference getSequencerReferenceByUpgradedReferenceId(long upgradedReferenceId) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.getByUpgradedReference(upgradedReferenceId);
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getSampleQcTypeById(long qcTypeId) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.getSampleQcTypeById(qcTypeId);
    } else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getSampleQcTypeByName(String qcName) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.getSampleQcTypeByName(qcName);
    } else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getLibraryQcTypeById(long qcTypeId) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.getLibraryQcTypeById(qcTypeId);
    } else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getLibraryQcTypeByName(String qcName) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.getLibraryQcTypeByName(qcName);
    } else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getRunQcTypeById(long qcTypeId) throws IOException {
    if (runQcStore != null) {
      return runQcStore.getRunQcTypeById(qcTypeId);
    } else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getRunQcTypeByName(String qcName) throws IOException {
    if (runQcStore != null) {
      return runQcStore.getRunQcTypeByName(qcName);
    } else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getPoolQcTypeById(long qcTypeId) throws IOException {
    if (poolQcStore != null) {
      return poolQcStore.getPoolQcTypeById(qcTypeId);
    } else {
      throw new IOException("No poolQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getPoolQcTypeByName(String qcName) throws IOException {
    if (poolQcStore != null) {
      return poolQcStore.getPoolQcTypeByName(qcName);
    } else {
      throw new IOException("No poolQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  public ChangeLogStore getChangeLogStore() {
    return changeLogStore;
  }

  public void setChangeLogStore(ChangeLogStore changeLogStore) {
    this.changeLogStore = changeLogStore;
  }

  @Override
  public BoxableView getBoxableViewByBarcode(String barcode) throws IOException {
    return boxStore.getBoxableViewByBarcode(barcode);
  }

  @Override
  public long saveBox(Box box) throws IOException {
    if (box.getId() == AbstractBox.UNSAVED_ID) {
      return saveNewBox(box);
    } else {
      Box original = boxStore.get(box.getId());
      applyChanges(box, original);
      StringBuilder message = new StringBuilder();

      // Process additions/moves
      Set<BoxableId> handled = Sets.newHashSet();
      for (Map.Entry<String, BoxableView> entry : box.getBoxables().entrySet()) {
        BoxableView previousOccupant = original.getBoxable(entry.getKey());
        BoxableView newOccupant = entry.getValue();
        handled.add(newOccupant.getId());

        if (previousOccupant != null && newOccupant.getId().equals(previousOccupant.getId())) {
          // Unchanged
          continue;
        }
        if (message.length() > 0) {
          message.append("\n");
        }
        
        BoxableView oldOccupant = boxStore.getBoxableView(newOccupant.getId());
        if (oldOccupant.getBoxId() != null) {
          if (oldOccupant.getBoxId().longValue() == box.getId()) {
            // Moved within same box
            message.append(String.format("Relocated %s (%s) from %s to %s", oldOccupant.getAlias(), oldOccupant.getName(),
                oldOccupant.getBoxPosition(), entry.getKey()));
          } else {
            // Moved from a different box
            message.append(String.format("Moved %s (%s) from %s (%s) to %s", oldOccupant.getAlias(), oldOccupant.getName(),
                oldOccupant.getBoxAlias(), oldOccupant.getBoxName(), entry.getKey()));

            Box oldHome = boxStore.get(oldOccupant.getBoxId());
            String oldHomeMessage = String.format("Moved %s (%s) to %s (%s)", oldOccupant.getAlias(), oldOccupant.getName(),
                original.getAlias(), original.getName());
            addBoxContentsChangeLog(oldHome, oldHomeMessage);
          }
          boxStore.removeBoxableFromBox(oldOccupant);
        } else {
          message.append(String.format("Added %s (%s) to %s", oldOccupant.getAlias(), oldOccupant.getName(), entry.getKey()));
        }
      }

      // Process removals
      for (Map.Entry<String, BoxableView> entry : original.getBoxables().entrySet()) {
        if (box.getBoxables().keySet().contains(entry.getKey()) || handled.contains(entry.getValue().getId())) {
          // Already handled. Only checking for removals at this point
          continue;
        }
        if (message.length() > 0) {
          message.append("\n");
        }
        BoxableView oldItem = entry.getValue();
        message.append(String.format("Removed %s (%s)", oldItem.getAlias(), oldItem.getName()));
      }

      original.setBoxables(box.getBoxables());

      if (message.length() > 0) {
        addBoxContentsChangeLog(original, message.toString());
      }
      return boxStore.save(box);
    }
  }

  private void addBoxContentsChangeLog(Box box, String message) throws IOException {
    BoxChangeLog changeLog = new BoxChangeLog();
    changeLog.setBox(box);
    changeLog.setTime(new Date());
    changeLog.setColumnsChanged("contents");
    changeLog.setUser(getCurrentUser());
    changeLog.setSummary(message);
    changeLogStore.create(changeLog);
  }

  private long saveNewBox(Box box) throws IOException {
    try {
      box.setName(generateTemporaryName());
      box.setSecurityProfile(securityProfileStore.get(securityProfileStore.save(box.getSecurityProfile())));
      boxStore.save(box);

      if (autoGenerateIdBarcodes) {
        box.setIdentificationBarcode(box.getName() + "::" + box.getAlias());
      }
      box.setName(namingScheme.generateNameFor(box));
      validateNameOrThrow(box, namingScheme);
      return boxStore.save(box);
    } catch (MisoNamingException e) {
      throw new IOException("Invalid name for box", e);
    }
  }

  private void applyChanges(Box from, Box to) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    to.setUse(boxStore.getUseById(from.getUse().getId()));
  }

  @Override
  public Box getBoxById(long boxId) throws IOException {
    if (boxStore != null) {
      return boxStore.get(boxId);
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Box getBoxByBarcode(String barcode) throws IOException {
    if (boxStore != null) {
      return boxStore.getByBarcode(barcode);
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
    if (boxStore != null) {
      return boxStore.getBoxByAlias(alias);
    } else {
      throw new IOException("No boxStore available. Check that is has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Box> listAllBoxes() throws IOException {
    if (boxStore != null) {
      return boxStore.listAll();
    } else {
      throw new IOException("No boxStore available. Check that is has been declared in the Spring config");
    }
  }

  @Override
  public Collection<Box> listAllBoxesWithLimit(long limit) throws IOException {
    if (boxStore != null) {
      return boxStore.listWithLimit(limit);
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<BoxSize> listAllBoxSizes() throws IOException {
    if (boxStore != null) {
      return boxStore.listAllBoxSizes();
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<BoxUse> listAllBoxUses() throws IOException {
    if (boxStore != null) {
      return boxStore.listAllBoxUses();
    } else {
      throw new IOException("No boxStore available. Check that is has been declared in the Spring config.");
    }
  }

  @Override
  public void discardSingleTube(Box box, String position) throws IOException {
    if (boxStore != null) {
      boxStore.discardSingleTube(box, position);
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void discardAllTubes(Box box) throws IOException {
    if (boxStore != null) {
      boxStore.discardAllTubes(box);
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteBox(Box box) throws IOException {
    if (boxStore != null) {
      if (!boxStore.remove(box)) {
        throw new IOException("Unable to delete box.");
      }
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSequencerServiceRecord(SequencerServiceRecord record) throws IOException {
    if (sequencerServiceRecordStore != null) {
      return sequencerServiceRecordStore.save(record);
    } else {
      throw new IOException("No sequencerServiceRecordStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getBoxColumnSizes() throws IOException {
    if (boxStore != null) {
      return boxStore.getBoxColumnSizes();
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerServiceRecord getSequencerServiceRecordById(long id) throws IOException {
    if (sequencerServiceRecordStore != null) {
      return sequencerServiceRecordStore.get(id);
    } else {
      throw new IOException("No sequencerServiceRecordStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerServiceRecord> listAllSequencerServiceRecords() throws IOException {
    if (sequencerServiceRecordStore != null) {
      return sequencerServiceRecordStore.listAll();
    } else {
      throw new IOException("No sequencerServiceRecordStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerServiceRecord> listSequencerServiceRecordsBySequencerId(long referenceId) throws IOException {
    if (sequencerServiceRecordStore != null) {
      return sequencerServiceRecordStore.listBySequencerId(referenceId);
    } else {
      throw new IOException("No sequencerServiceRecordStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException {
    if (sequencerServiceRecordStore != null) {
      return sequencerServiceRecordStore.getServiceRecordColumnSizes();
    } else {
      throw new IOException("No sequencerServiceRecordStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getProjectColumnSizes() throws IOException {
    if (projectStore != null) {
      return projectStore.getProjectColumnSizes();
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getRunColumnSizes() throws IOException {
    if (runStore != null) {
      return runStore.getRunColumnSizes();
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getSampleColumnSizes() throws IOException {
    if (sampleStore != null) {
      return sampleStore.getSampleColumnSizes();
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getSequencerReferenceColumnSizes() throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.getSequencerReferenceColumnSizes();
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getSubmissionColumnSizes() throws IOException {
    if (submissionStore != null) {
      return submissionStore.getSubmissionColumnSizes();
    } else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getUserColumnSizes() throws IOException {
    if (securityStore != null) {
      return securityStore.getUserColumnSizes();
    } else {
      throw new IOException("No securityStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getGroupColumnSizes() throws IOException {
    if (securityStore != null) {
      return securityStore.getGroupColumnSizes();
    } else {
      throw new IOException("No securityStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDesign> listLibraryDesignByClass(SampleClass sampleClass) throws IOException {
    return libraryDesignDao.getLibraryDesignByClass(sampleClass);
  }

  @Override
  public Collection<LibraryDesignCode> listLibraryDesignCodes() throws IOException {
    return libraryDesignCodeDao.getLibraryDesignCodes();
  }

  @Override
  public Collection<TargetedSequencing> listAllTargetedSequencing() throws IOException {
    if (targetedSequencingStore != null) {
      return targetedSequencingStore.listAll();
    } else {
      throw new IOException("No targetedSequencingStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public TargetedSequencing getTargetedSequencingById(long targetedSequencingId) throws IOException {
    if (targetedSequencingStore != null) {
      return targetedSequencingStore.get(targetedSequencingId);
    } else {
      throw new IOException("No targetedSequencingStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Long countRuns() throws IOException {
    if (runStore != null) {
      return runStore.countRuns();
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Long countRunsBySearch(String querystr) throws IOException {
    if (runStore != null) {
      return runStore.countBySearch(querystr);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Run getLatestRunBySequencerPartitionContainerId(Long containerId) throws IOException {
    if (runStore != null) {
      return runStore.getLatestRunIdRunBySequencerPartitionContainerId(containerId);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Long countContainers() throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return Long.valueOf(sequencerPartitionContainerStore.count());
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<Run> getRunsByPool(Pool pool) throws IOException {
    return runStore.listByPoolId(pool.getId());
  }

  public static void validateNameOrThrow(Nameable object, NamingScheme namingScheme) throws IOException {
    ValidationResult val = namingScheme.validateName(object.getName());
    if (!val.isValid()) throw new IOException("Save failed - invalid name:" + val.getMessage());
  }

  @Override
  public void addRunWatcher(Run run, User watcher) throws IOException {
    runStore.addWatcher(run, watcher);
    if (runAlertManager != null) runAlertManager.addWatcher(run, watcher);
  }

  @Override
  public void removeRunWatcher(Run run, User watcher) throws IOException {
    runStore.removeWatcher(run, watcher);
    if (runAlertManager != null) runAlertManager.addWatcher(run, watcher);
  }

  @Override
  public void addProjectWatcher(Project project, User watcher) throws IOException {
    projectStore.addWatcher(project, watcher);
    if (projectAlertManager != null) projectAlertManager.addWatcher(project, watcher);
  }

  @Override
  public void removeProjectWatcher(Project project, User watcher) throws IOException {
    projectStore.removeWatcher(project, watcher);
    if (projectAlertManager != null) projectAlertManager.removeWatcher(project, watcher);
  }

  public void autoGenerateIdBarcode(Pool pool) {
    String barcode = pool.getName() + "::" + pool.getAlias();
    pool.setIdentificationBarcode(barcode);
  }

  @Override
  public Collection<LibraryDesign> listLibraryDesigns() throws IOException {
    return libraryDesignDao.getLibraryDesigns();
  }
}
