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

package uk.ac.bbsrc.tgac.miso.core.manager;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.ProjectAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunQcException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.AlertStore;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignDao;
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
  private AlertStore alertStore;
  @Autowired
  private KitStore kitStore;
  @Autowired
  private LibraryStore libraryStore;
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
  private SecurityProfileStore securityProfileStore;
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

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setBoxStore(BoxStore boxStore) {
    this.boxStore = boxStore;
  }

  public void setAlertStore(AlertStore alertStore) {
    this.alertStore = alertStore;
  }

  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
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

  public void setSecurityProfileStore(SecurityProfileStore securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
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

  public void setAutoGenerateIdBarcodes(boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
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
  public Collection<Pool> listAllPoolsBySearch(String query) throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByCriteria(null, query, null, false);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool> listAllPoolsWithLimit(int limit) throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByCriteria(null, null, limit, false);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
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
  public Collection<Boxable> getBoxablesFromBarcodeList(List<String> barcodeList) throws IOException {
    List<Boxable> boxables = new ArrayList<>();
    if (sampleStore != null && libraryStore != null) {
      boxables.addAll(sampleStore.getByBarcodeList(barcodeList));
      boxables.addAll(libraryStore.getByBarcodeList(barcodeList));
      boxables.addAll(poolStore.getByBarcodeList(barcodeList));
      return boxables;
    } else {
      throw new IOException(
          "One or more of sampleStore, libraryStore, or poolStore are not available. Check that they have been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool> listAllPools() throws IOException {
    if (poolStore != null) {
      return poolStore.listAll();
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool> listAllPoolsByPlatform(PlatformType platformType) throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByCriteria(platformType, null, null, false);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool> listAllPoolsByPlatformAndSearch(PlatformType platformType, String query)
      throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByCriteria(platformType, query, null, false);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool> listReadyPoolsByPlatform(PlatformType platformType) throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByCriteria(platformType, null, null, true);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query)
      throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByCriteria(platformType, query, null, true);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool> listPoolsByProjectId(long projectId) throws IOException {
    if (poolStore != null) {
      return poolStore.listByProjectId(projectId);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool> listPoolsByLibraryId(long libraryId) throws IOException {
    if (poolStore != null) {
      return poolStore.listByLibraryId(libraryId);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByRunId(long runId)
      throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listAllSequencerPartitionContainersByRunId(runId);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listSequencerPartitionContainersByBarcode(barcode);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainers() throws IOException {
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
      Collection<SequencerReference> sequencers = listSequencerReferencesByPlatformType(platformType);
      if (!sequencers.isEmpty()) {
        activePlatformTypes.add(platformType);
      }
    }
    return activePlatformTypes;
  }

  @Override
  public Collection<String> listDistinctPlatformNames() throws IOException {
    if (platformStore != null) {
      return platformStore.listDistinctPlatformNames();
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
  public Collection<Kit> listAllKits() throws IOException {
    if (kitStore != null) {
      return kitStore.listAll();
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<KitDescriptor> listAllKitDescriptors() throws IOException {
    if (kitStore != null) {
      return kitStore.listAllKitDescriptors();
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException {
    if (kitStore != null) {
      return kitStore.listKitDescriptorsByType(kitType);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<QcType> listAllSampleQcTypes() throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.listAllSampleQcTypes();
    } else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
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
  public void deletePool(Pool pool) throws IOException {
    if (poolStore != null) {
      if (!poolStore.remove(pool)) {
        throw new IOException("Unable to delete Pool.");
      }
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteContainer(SequencerPartitionContainer<SequencerPoolPartition> container) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      if (!sequencerPartitionContainerStore.remove(container)) {
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
  public void deleteKitNote(Kit kit, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Kit managed = kitStore.get(kit.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Kit " + kit.getId());
    }
    managed.getNotes().remove(deleteNote);
    kitStore.save(managed);
  }

  @Override
  public void deletePoolNote(Pool pool, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Pool managed = poolStore.get(pool.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Pool " + pool.getId());
    }
    managed.getNotes().remove(deleteNote);
    poolStore.save(managed);
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
        project = original;
      }
      long id = projectStore.save(project);
      if (projectAlertManager != null) projectAlertManager.update(project);
      return id;
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveProjectOverview(ProjectOverview overview) throws IOException {
    if (projectStore != null) {
      return projectStore.saveOverview(overview);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    ProjectOverview managed = projectStore.getProjectOverviewById(overview.getId());
    note.setCreationDate(new Date());
    // TODO: when moved to Service: note.setOwner(authorizationManager.getCurrentUser());
    managed.getNotes().add(note);
    projectStore.saveOverview(managed);
  }

  @Override
  public long saveRun(Run run) throws IOException {
    if (runStore != null) {
      if (run.getId() == AbstractRun.UNSAVED_ID) {
        run.setName(generateTemporaryName());
        run.setId(runStore.save(run));
        try {
          String name = namingScheme.generateNameFor(run);
          run.setName(name);

          validateNameOrThrow(run, namingScheme);
          return runStore.save(run);
        } catch (MisoNamingException e) {
          throw new IOException("Cannot save Run - issue with generating name");
        }
      } else {
        Run managed = getRunById(run.getId());
        log.info("update run: " + managed);
        managed.setAlias(run.getAlias());
        managed.setDescription(run.getDescription());
        managed.setPairedEnd(run.getPairedEnd());
        managed.setCycles(run.getCycles());
        managed.setFilePath(run.getFilePath());
        managed.getStatus().setHealth(run.getStatus().getHealth());
        managed.getStatus().setStartDate(run.getStatus().getStartDate());
        managed.getStatus().setCompletionDate(run.getStatus().getCompletionDate());
        managed.getStatus().setInstrumentName(run.getStatus().getInstrumentName());
        managed.getStatus().setRunName(run.getStatus().getRunName());
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
        managed.setNotes(run.getNotes());
        managed.setSequencingParameters(run.getSequencingParameters());
        runStore.save(managed);
        if (runAlertManager != null) runAlertManager.update(run);
        return run.getId();
      }
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void saveRuns(Collection<Run> runs) throws IOException {
    if (runStore != null) {
      runStore.saveAll(runs);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveRunQC(RunQC runQC) throws IOException {
    if (runQcStore != null) {
      long id = runQcStore.save(runQC);
      if (runAlertManager != null) runAlertManager.update(runQC.getRun());
      return id;
    } else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void saveRunNote(Run run, Note note) throws IOException {
    Run managed = runStore.get(run.getId());
    note.setCreationDate(new Date());
    // TODO: when moved to Service: note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    runStore.save(managed);
  }

  @Override
  public void saveKitNote(Kit kit, Note note) throws IOException {
    Kit managed = kitStore.get(kit.getId());
    note.setCreationDate(new Date());
    // TODO: when moved to Service: note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    kitStore.save(managed);
  }

  @Override
  public long saveSample(Sample sample) throws IOException {
    if (sampleStore != null) {
      return sampleStore.save(sample);
    } else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSampleQC(SampleQC sampleQc) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.save(sampleQc);
    } else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long savePool(Pool pool) throws IOException {
    if (poolStore != null) {
      if (pool.isDiscarded()) {
        pool.setVolume(0.0);
      }
      if (autoGenerateIdBarcodes && pool.getId() == PoolImpl.UNSAVED_ID) {
        LimsUtils.generateAndSetIdBarcode(pool);
      }
      long id = poolStore.save(pool);
      if (poolAlertManager != null) poolAlertManager.update(pool);
      return id;
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long savePoolQC(PoolQC poolQC) throws IOException {
    if (poolQcStore != null) {
      return poolQcStore.save(poolQC);
    } else {
      throw new IOException("No poolQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void savePoolNote(Pool pool, Note note) throws IOException {
    Pool managed = poolStore.get(pool.getId());
    note.setCreationDate(new Date());
    // TODO: when moved to Service: note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    poolStore.save(managed);
  }

  @Override
  public long saveSequencerPartitionContainer(SequencerPartitionContainer<SequencerPoolPartition> container) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      if (container.getId() == AbstractSequencerPartitionContainer.UNSAVED_ID) {
        return sequencerPartitionContainerStore.save(container);
      } else {
        SequencerPartitionContainer<SequencerPoolPartition> managed = getSequencerPartitionContainerById(container.getId());
        managed.setIdentificationBarcode(container.getIdentificationBarcode());
        managed.setLocationBarcode(container.getLocationBarcode());
        managed.setValidationBarcode(container.getValidationBarcode());
        updatePartitionPools(container, managed);
        return sequencerPartitionContainerStore.save(managed);
      }
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  private void updatePartitionPools(SequencerPartitionContainer<SequencerPoolPartition> source,
      SequencerPartitionContainer<SequencerPoolPartition> managed) throws IOException {
    for (SequencerPoolPartition sourcePartition : source.getPartitions()) {
      for (SequencerPoolPartition managedPartition : source.getPartitions()) {
        if (sourcePartition.getId() == managedPartition.getId()) {
          Pool sourcePool = sourcePartition.getPool();
          Pool managedPool = managedPartition.getPool();
          if (sourcePool == null && managedPool == null) continue;
          if (sourcePool == null && managedPool != null) {
            managedPartition.setPool(null);
          } else if (sourcePool != null && managedPool == null) {
            managedPartition.setPool(getPoolById(sourcePool.getId()));
          } else if (sourcePool.getId() != managedPool.getId()) {
            managedPartition.setPool(getPoolById(sourcePool.getId()));
          }
        }
      }
    }
  }

  @Override
  public long savePlatform(Platform platform) throws IOException {
    if (platformStore != null) {
      return platformStore.save(platform);
    } else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveStatus(Status status) throws IOException {
    if (statusStore != null) {
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
      return sequencerReferenceStore.save(sequencerReference);
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveKit(Kit kit) throws IOException {
    if (kitStore != null) {
      return kitStore.save(kit);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException {
    if (kitStore != null) {
      return kitStore.saveKitDescriptor(kitDescriptor);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
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
  public Pool getPoolById(long poolId) throws IOException {
    if (poolStore != null) {
      return poolStore.get(poolId);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public PoolQC getPoolQCById(long poolQcId) throws IOException {
    if (poolQcStore != null) {
      return poolQcStore.get(poolQcId);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool getPoolByBarcode(String barcode, PlatformType platformType) throws IOException {
    if (poolStore != null) {
      return poolStore.getPoolByBarcode(barcode, platformType);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool getPoolByBarcode(String barcode) throws IOException {
    String[] s = barcode.split("::");
    if (s.length > 1) {
      String platformKey = s[1];
      if (!isStringEmptyOrNull(platformKey)) {
        PlatformType pt = PlatformType.get(platformKey);
        if (pt != null) {
          return getPoolByBarcode(barcode, pt);
        }
      }
    }
    return null;
  }

  @Override
  public Pool getPoolByIdBarcode(String barcode) throws IOException {
    if (poolStore != null) {
      return poolStore.getByBarcode(barcode);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerPoolPartition getSequencerPoolPartitionById(long partitionId) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.getPartitionById(partitionId);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerById(long containerId) throws IOException {
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
      return statusStore.getByRunName(runName);
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
  public Kit getKitById(long kitId) throws IOException {
    if (kitStore != null) {
      return kitStore.get(kitId);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Kit getKitByIdentificationBarcode(String barcode) throws IOException {
    if (kitStore != null) {
      return kitStore.getKitByIdentificationBarcode(barcode);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    if (kitStore != null) {
      return kitStore.getKitByLotNumber(lotNumber);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException {
    if (kitStore != null) {
      return kitStore.getKitDescriptorById(kitDescriptorId);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException {
    if (kitStore != null) {
      return kitStore.getKitDescriptorByPartNumber(partNumber);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
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
  public long saveBox(Box box) throws IOException {
    if (boxStore != null) {
      if (box.getId() == AbstractBox.UNSAVED_ID) {
        try {
          boxStore.save(box);

          if (autoGenerateIdBarcodes) {
            box.setIdentificationBarcode(box.getName() + "::" + box.getAlias());
          }
          namingScheme.generateNameFor(box);
          validateNameOrThrow(box, namingScheme);
          return boxStore.save(box);
        } catch (MisoNamingException e) {
          throw new IOException("Invalid name for box", e);
        }
      } else {
        Box original = boxStore.get(box.getId());
        original.setAlias(box.getAlias());
        original.setDescription(box.getDescription());
        original.setIdentificationBarcode(box.getIdentificationBarcode());
        original.setSize(boxStore.getSizeById(box.getSize().getId()));
        original.setUse(boxStore.getUseById(box.getUse().getId()));
        Map<String, Boxable> contents = new HashMap<>();
        for (Map.Entry<String, Boxable> entry : box.getBoxables().entrySet()) {
          Boxable item;
          if (entry.getValue() instanceof Pool) {
            item = poolStore.get(entry.getValue().getId());
          } else if (entry.getValue() instanceof Sample) {
            item = sampleStore.get(entry.getValue().getId());
          } else if (entry.getValue() instanceof Library) {
            item = libraryStore.get(entry.getValue().getId());
          } else {
            throw new IllegalArgumentException("Unknown boxable: " + entry.getValue().getClass().getName());
          }
          contents.put(entry.getKey(), item);
        }
        original.setBoxables(contents);
        return boxStore.save(box);
      }
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
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
  public Map<String, Integer> getPoolColumnSizes() throws IOException {
    if (poolStore != null) {
      return poolStore.getPoolColumnSizes();
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
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
  public Map<String, Integer> getKitDescriptorColumnSizes() throws IOException {
    if (kitStore != null) {
      return kitStore.getKitDescriptorColumnSizes();
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
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
  public Long countPoolsByPlatform(PlatformType platform) throws IOException {
    if (poolStore != null) {
      return poolStore.countPoolsBySearch(platform, null);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Long getNumPoolsBySearch(PlatformType platform, String querystr) throws IOException {
    if (poolStore != null) {
      return poolStore.countPoolsBySearch(platform, querystr);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  };

  @Override
  public List<Pool> getPoolsByPageSizeSearchPlatform(int offset, int limit, String querystr, String sortDir,
      String sortCol, PlatformType platform) throws IOException {
    if (poolStore != null) {
      return poolStore.listBySearchOffsetAndNumResultsAndPlatform(offset, limit, querystr, sortDir, sortCol, platform);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<Pool> getPoolsByPageAndSize(int offset, int limit, String sortDir, String sortCol,
      PlatformType platform) throws IOException {
    if (poolStore != null) {
      return poolStore.listBySearchOffsetAndNumResultsAndPlatform(offset, limit, null, sortDir, sortCol, platform);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
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
  public List<Run> getRunsByPageSizeSearch(int offset, int limit, String querystr, String sortDir, String sortCol) throws IOException {
    if (runStore != null) {
      return runStore.listBySearchOffsetAndNumResults(offset, limit, querystr, sortDir, sortCol);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<Run> getRunsByPageAndSize(int offset, int limit, String sortDir, String sortCol) throws IOException {
    if (runStore != null) {
      return runStore.listByOffsetAndNumResults(offset, limit, sortDir, sortCol);
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
  public List<SequencerPartitionContainer<SequencerPoolPartition>> getContainersByPageSizeSearch(int offset, int limit, String querystr,
      String sortDir, String sortCol) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listBySearchOffsetAndNumResults(offset, limit, querystr, sortDir, sortCol);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> getContainersByPageAndSize(int offset, int limit, String sortDir,
      String sortCol) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listByOffsetAndNumResults(offset, limit, sortDir, sortCol);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Long countContainersBySearch(String querystr) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.countBySearch(querystr);
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

  @Override
  public void addPoolWatcher(Pool pool, User watcher) throws IOException {
    poolStore.addWatcher(pool, watcher);
    if (poolAlertManager != null)
    poolAlertManager.addWatcher(pool, watcher);
  }

  @Override
  public void removePoolWatcher(Pool pool, User watcher) throws IOException {
    poolStore.removeWatcher(pool, watcher);
    if (poolAlertManager != null) poolAlertManager.removeWatcher(pool, watcher);
  }

  public void autoGenerateIdBarcode(Pool pool) {
    String barcode = pool.getName() + "::" + pool.getAlias();
    pool.setIdentificationBarcode(barcode);
  }
}
