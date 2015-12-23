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

package uk.ac.bbsrc.tgac.miso.core.manager;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Plateable;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.store.AlertStore;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.EmPCRDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.EmPCRStore;
import uk.ac.bbsrc.tgac.miso.core.store.EntityGroupStore;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.PartitionStore;
import uk.ac.bbsrc.tgac.miso.core.store.PlateStore;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.core.store.StatusStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;

/**
 * Implementation of a RequestManager to facilitate persistence operations on MISO model objects
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoRequestManager implements RequestManager {
  protected static final Logger log = LoggerFactory.getLogger(MisoRequestManager.class);

  @Autowired
  private AlertStore alertStore;
  @Autowired
  private EmPCRDilutionStore emPCRDilutionStore;
  @Autowired
  private LibraryDilutionStore libraryDilutionStore;
  @Autowired
  private EmPCRStore emPCRStore;
  @Autowired
  private ExperimentStore experimentStore;
  @Autowired
  private EntityGroupStore entityGroupStore;
  @Autowired
  private KitStore kitStore;
  @Autowired
  private LibraryStore libraryStore;
  @Autowired
  private LibraryQcStore libraryQcStore;
  @Autowired
  private NoteStore noteStore;
  @Autowired
  private PartitionStore partitionStore;
  @Autowired
  private PlateStore plateStore;
  @Autowired
  private PlatformStore platformStore;
  @Autowired
  private ProjectStore projectStore;
  @Autowired
  private PoolStore poolStore;
  @Autowired
  private PoolQcStore poolQcStore;
  @Autowired
  private RunStore runStore;
  @Autowired
  private RunQcStore runQcStore;
  @Autowired
  private SampleStore sampleStore;
  @Autowired
  private SampleQcStore sampleQcStore;
  @Autowired
  private Store<SecurityProfile> securityProfileStore;
  @Autowired
  private SequencerPartitionContainerStore sequencerPartitionContainerStore;
  @Autowired
  private SequencerReferenceStore sequencerReferenceStore;
  @Autowired
  private StatusStore statusStore;
  @Autowired
  private StudyStore studyStore;
  @Autowired
  private Store<Submission> submissionStore;
  @Autowired
  private ChangeLogStore changeLogStore;
  @Autowired
  private BoxStore boxStore;

  public void setBoxStore(BoxStore boxStore) {
    this.boxStore = boxStore;
  }

  public void setAlertStore(AlertStore alertStore) {
    this.alertStore = alertStore;
  }

  public void setEmPCRDilutionStore(EmPCRDilutionStore emPCRDilutionStore) {
    this.emPCRDilutionStore = emPCRDilutionStore;
  }

  public void setLibraryDilutionStore(LibraryDilutionStore libraryDilutionStore) {
    this.libraryDilutionStore = libraryDilutionStore;
  }

  public void setEmPCRStore(EmPCRStore emPCRStore) {
    this.emPCRStore = emPCRStore;
  }

  public void setExperimentStore(ExperimentStore experimentStore) {
    this.experimentStore = experimentStore;
  }

  public void setEntityGroupStore(EntityGroupStore entityGroupStore) {
    this.entityGroupStore = entityGroupStore;
  }

  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
  }

  public void setLibraryStore(LibraryStore libraryStore) {
    this.libraryStore = libraryStore;
  }

  public void setLibraryQcStore(LibraryQcStore libraryQcStore) {
    this.libraryQcStore = libraryQcStore;
  }

  public void setNoteStore(NoteStore noteStore) {
    this.noteStore = noteStore;
  }

  public void setPartitionStore(PartitionStore partitionStore) {
    this.partitionStore = partitionStore;
  }

  public void setPlateStore(PlateStore plateStore) {
    this.plateStore = plateStore;
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

  public void setSecurityProfileStore(Store<SecurityProfile> securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
  }

  public void setSequencerPartitionContainerStore(SequencerPartitionContainerStore sequencerPartitionContainerStore) {
    this.sequencerPartitionContainerStore = sequencerPartitionContainerStore;
  }

  public void setSequencerReferenceStore(SequencerReferenceStore sequencerReferenceStore) {
    this.sequencerReferenceStore = sequencerReferenceStore;
  }

  public void setStatusStore(StatusStore statusStore) {
    this.statusStore = statusStore;
  }

  public void setStudyStore(StudyStore studyStore) {
    this.studyStore = studyStore;
  }

  public void setSubmissionStore(Store<Submission> submissionStore) {
    this.submissionStore = submissionStore;
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
      Collection<Run> accessibleRuns = new HashSet<Run>();
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
      Collection<Run> accessibleRuns = new HashSet<Run>();
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
      Collection<Run> accessibleRuns = new HashSet<Run>();
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
  public Collection<Sample> listAllSamplesByExperimentId(long experimentId) throws IOException {
    if (sampleStore != null) {
      return sampleStore.listByExperimentId(experimentId);
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
  public Collection<Library> listAllLibraries() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAll();
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Library> listAllLibrariesWithLimit(long limit) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAllWithLimit(limit);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Library> listAllLibrariesBySearch(String query) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listBySearch(query);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Library> listAllLibrariesByProjectId(long projectId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listByProjectId(projectId);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Library> listAllLibrariesBySampleId(long sampleId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listBySampleId(sampleId);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
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
    List<Boxable> boxables = new ArrayList<Boxable>();
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
  public Collection<Pool<? extends Poolable>> listAllPools() throws IOException {
    if (poolStore != null) {
      return poolStore.listAll();
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatform(PlatformType platformType) throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByPlatform(platformType);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByPlatformAndSearch(platformType, query);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatform(PlatformType platformType) throws IOException {
    if (poolStore != null) {
      return poolStore.listReadyByPlatform(platformType);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query)
      throws IOException {
    if (poolStore != null) {
      return poolStore.listReadyByPlatformAndSearch(platformType, query);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listPoolsByProjectId(long projectId) throws IOException {
    if (poolStore != null) {
      return poolStore.listByProjectId(projectId);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listPoolsByLibraryId(long libraryId) throws IOException {
    if (poolStore != null) {
      return poolStore.listByLibraryId(libraryId);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listPoolsBySampleId(long sampleId) throws IOException {
    if (poolStore != null) {
      return poolStore.listBySampleId(sampleId);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<PoolQC> listAllPoolQCsByPoolId(long poolId) throws IOException {
    if (poolQcStore != null) {
      return poolQcStore.listByPoolId(poolId);
    } else {
      throw new IOException("No poolQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryType> listAllLibraryTypes() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAllLibraryTypes();
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryType> listLibraryTypesByPlatform(String platformName) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listLibraryTypesByPlatform(platformName);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAllLibrarySelectionTypes();
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAllLibraryStrategyTypes();
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodes() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAllTagBarcodes();
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodesByPlatform(String platformName) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listTagBarcodesByPlatform(platformName);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodesByStrategyName(String strategyName) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listTagBarcodesByStrategyName(strategyName);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Dilution> listDilutionsBySearch(String query, PlatformType platformType) throws IOException {
    List<Dilution> dilutions = new ArrayList<Dilution>();
    for (Dilution d : libraryDilutionStore.listAllLibraryDilutionsBySearch(query, platformType)) {
      dilutions.add(d);
    }

    for (Dilution d : emPCRDilutionStore.listAllEmPcrDilutionsBySearch(query, platformType)) {
      dilutions.add(d);
    }
    return dilutions;
  }

  @Override
  public Collection<Dilution> listAllDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    List<Dilution> dilutions = new ArrayList<Dilution>();
    for (Dilution d : libraryDilutionStore.listAllLibraryDilutionsByProjectAndPlatform(projectId, platformType)) {
      dilutions.add(d);
    }

    for (Dilution d : emPCRDilutionStore.listAllEmPcrDilutionsByProjectAndPlatform(projectId, platformType)) {
      dilutions.add(d);
    }
    return dilutions;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutions() throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.listAll();
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsWithLimit(long limit) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.listAllWithLimit(limit);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByLibraryId(long libraryId) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.listByLibraryId(libraryId);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformType) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.listAllLibraryDilutionsByPlatform(platformType);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.listAllLibraryDilutionsByProjectId(projectId);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearch(String query, PlatformType platformType) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.listAllLibraryDilutionsByPlatformAndSearch(query, platformType);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearchOnly(String query) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.listAllLibraryDilutionsBySearchOnly(query);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectAndPlatform(long projectId, PlatformType platformType)
      throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.listAllLibraryDilutionsByProjectAndPlatform(projectId, platformType);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutions() throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.listAll();
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByPlatform(PlatformType platformType) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.listAllEmPcrDilutionsByPlatform(platformType);
    } else {
      throw new IOException("No emPcrDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.listAllEmPcrDilutionsByPoolAndPlatform(poolId, platformType);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCR> listAllEmPCRs() throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.listAll();
    } else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCR> listAllEmPCRsByDilutionId(long dilutionId) throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.listAllByDilutionId(dilutionId);
    } else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCR> listAllEmPCRsByProjectId(long projectId) throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.listAllByProjectId(projectId);
    } else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByEmPcrId(long pcrId) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.listAllByEmPCRId(pcrId);
    } else {
      throw new IOException("No emPcrDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByProjectId(long projectId) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.listAllEmPcrDilutionsByProjectId(projectId);
    } else {
      throw new IOException("No emPcrDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsBySearch(String query, PlatformType platformType) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.listAllEmPcrDilutionsBySearch(query, platformType);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.listAllEmPcrDilutionsByProjectAndPlatform(projectId, platformType);
    } else {
      throw new IOException("No emPcrDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Experiment> listAllExperiments() throws IOException {
    if (experimentStore != null) {
      return experimentStore.listAll();
    } else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Experiment> listAllExperimentsWithLimit(long limit) throws IOException {
    if (experimentStore != null) {
      return experimentStore.listAllWithLimit(limit);
    } else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Experiment> listAllExperimentsBySearch(String query) throws IOException {
    if (experimentStore != null) {
      return experimentStore.listBySearch(query);
    } else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Experiment> listAllExperimentsByStudyId(long studyId) throws IOException {
    if (experimentStore != null) {
      return experimentStore.listByStudyId(studyId);
    } else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Study> listAllStudies() throws IOException {
    if (studyStore != null) {
      return studyStore.listAll();
    } else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Study> listAllStudiesWithLimit(long limit) throws IOException {
    if (studyStore != null) {
      return studyStore.listAllWithLimit(limit);
    } else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Study> listAllStudiesBySearch(String query) throws IOException {
    if (studyStore != null) {
      return studyStore.listBySearch(query);
    } else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Study> listAllStudiesByLibraryId(long libraryId) throws IOException {
    if (studyStore != null) {
      return studyStore.listByLibraryId(libraryId);
    } else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Study> listAllStudiesByProjectId(long projectId) throws IOException {
    if (studyStore != null) {
      return studyStore.listByProjectId(projectId);
    } else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
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
  public Collection<SequencerPoolPartition> listAllSequencerPoolPartitions() throws IOException {
    if (partitionStore != null) {
      return partitionStore.listAll();
    } else {
      throw new IOException("No partitionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<? extends SequencerPoolPartition> listPartitionsBySequencerPartitionContainerId(long containerId) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listPartitionsByContainerId(containerId);
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
      Collection<Platform> platforms = new TreeSet<Platform>();
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
  public Collection<String> listAllStudyTypes() throws IOException {
    if (studyStore != null) {
      return studyStore.listAllStudyTypes();
    } else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
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
  @Deprecated
  public Collection<Run> listRunsByExperimentId(Long experimentId) throws IOException {
    if (runStore != null) {
      return runStore.listByExperimentId(experimentId);
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
  public Collection<Kit> listKitsByExperimentId(long experimentId) throws IOException {
    if (kitStore != null) {
      return kitStore.listByExperiment(experimentId);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Kit> listKitsByManufacturer(String manufacturer) throws IOException {
    if (kitStore != null) {
      return kitStore.listByManufacturer(manufacturer);
    } else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Kit> listKitsByType(KitType kitType) throws IOException {
    if (kitStore != null) {
      return kitStore.listKitsByType(kitType);
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

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlates() throws IOException {
    if (plateStore != null) {
      return plateStore.listAll();
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlatesByProjectId(long projectId)
      throws IOException {
    if (plateStore != null) {
      return plateStore.listByProjectId(projectId);
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlatesBySearch(String str) throws IOException {
    if (plateStore != null) {
      return plateStore.listBySearch(str);
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Alert> listUnreadAlertsByUserId(long userId) throws IOException {
    if (alertStore != null) {
      return alertStore.listUnreadByUserId(userId);
    } else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Alert> listAlertsByUserId(long userId) throws IOException {
    if (alertStore != null) {
      return alertStore.listByUserId(userId);
    } else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Alert> listAlertsByUserId(long userId, long limit) throws IOException {
    if (alertStore != null) {
      return alertStore.listByUserId(userId, limit);
    } else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }

  // DELETES
  @Override
  public void deleteProject(Project project) throws IOException {
    if (projectStore != null) {
      if (!projectStore.remove(project)) {
        throw new IOException("Unable to delete Project. Make sure the project has no child entitites.");
      }
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteStudy(Study study) throws IOException {
    if (studyStore != null) {
      if (!studyStore.remove(study)) {
        throw new IOException("Unable to delete Study. Make sure the study has no child entitites.");
      }
    } else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteExperiment(Experiment experiment) throws IOException {
    if (experimentStore != null) {
      if (!experimentStore.remove(experiment)) {
        throw new IOException("Unable to delete Experiment. Make sure the experiment has no child entitites.");
      }
    } else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

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
  public void deleteLibrary(Library library) throws IOException {
    if (libraryStore != null) {
      if (!libraryStore.remove(library)) {
        throw new IOException("Unable to delete Library. Make sure the library has no child entitites.");
      }
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteEmPCR(emPCR empcr) throws IOException {
    if (emPCRStore != null) {
      if (!emPCRStore.remove(empcr)) {
        throw new IOException("Unable to delete EmPCR. Make sure the EmPCR has no child entitites.");
      }
    } else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
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
  public void deletePoolQC(PoolQC poolQc) throws IOException {
    if (poolQcStore != null) {
      if (!poolQcStore.remove(poolQc)) {
        throw new IOException("Unable to delete PoolQC.");
      }
    } else {
      throw new IOException("No poolQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteLibraryDilution(LibraryDilution dilution) throws IOException {
    if (libraryDilutionStore != null) {
      if (!libraryDilutionStore.remove(dilution)) {
        throw new IOException("Unable to delete LibraryDilution.");
      }
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteEmPCRDilution(emPCRDilution dilution) throws IOException {
    if (emPCRDilutionStore != null) {
      if (!emPCRDilutionStore.remove(dilution)) {
        throw new IOException("Unable to delete emPCRDilution.");
      }
    } else {
      throw new IOException("No emPcrDilutionStore available. Check that it has been declared in the Spring config.");
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
  public void deletePlate(Plate plate) throws IOException {
    if (plateStore != null) {
      if (!plateStore.remove(plate)) {
        throw new IOException("Unable to delete Plate.");
      }
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
    if (entityGroupStore != null) {
      if (!entityGroupStore.remove(entityGroup)) {
        throw new IOException("Unable to delete EntityGroup.");
      }
    } else {
      throw new IOException("No entityGroupStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteContainer(SequencerPartitionContainer container) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      if (!sequencerPartitionContainerStore.remove(container)) {
        throw new IOException("Unable to delete container.");
      }
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteNote(Note note) throws IOException {
    if (noteStore != null) {
      if (!noteStore.remove(note)) {
        throw new IOException("Unable to delete note.");
      }
    } else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deletePartition(SequencerPoolPartition partition) throws IOException {
    if (partitionStore != null) {
      if (!partitionStore.remove(partition)) {
        throw new IOException("Unable to delete partition.");
      }
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  // SAVES

  @Override
  public long saveProject(Project project) throws IOException {
    if (projectStore != null) {
      return projectStore.save(project);
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
  public long saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    if (noteStore != null) {
      return noteStore.saveProjectOverviewNote(overview, note);
    } else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveRun(Run run) throws IOException {
    if (runStore != null) {
      return runStore.save(run);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public int[] saveRuns(Collection<Run> runs) throws IOException {
    if (runStore != null) {
      return runStore.saveAll(runs);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveRunQC(RunQC runQC) throws IOException {
    if (runQcStore != null) {
      return runQcStore.save(runQC);
    } else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveRunNote(Run run, Note note) throws IOException {
    if (noteStore != null) {
      return noteStore.saveRunNote(run, note);
    } else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
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
  public long saveSampleNote(Sample sample, Note note) throws IOException {
    if (noteStore != null) {
      return noteStore.saveSampleNote(sample, note);
    } else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveEmPcrDilution(emPCRDilution dilution) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.save(dilution);
    } else {
      throw new IOException("No emPcrDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveLibrary(Library library) throws IOException {
    if (libraryStore != null) {
      return libraryStore.save(library);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveLibraryDilution(LibraryDilution libraryDilution) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.save(libraryDilution);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveLibraryNote(Library library, Note note) throws IOException {
    if (noteStore != null) {
      return noteStore.saveLibraryNote(library, note);
    } else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveLibraryQC(LibraryQC libraryQc) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.save(libraryQc);
    } else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveEmPCR(emPCR pcr) throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.save(pcr);
    } else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveEmPCRDilution(emPCRDilution dilution) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.save(dilution);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long savePool(Pool pool) throws IOException {
    if (poolStore != null) {
      return poolStore.save(pool);
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
  public long saveExperiment(Experiment experiment) throws IOException {
    if (experimentStore != null) {
      return experimentStore.save(experiment);
    } else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveStudy(Study study) throws IOException {
    if (studyStore != null) {
      return studyStore.save(study);
    } else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSequencerPoolPartition(SequencerPoolPartition partition) throws IOException {
    if (partitionStore != null) {
      return partitionStore.save(partition);
    } else {
      throw new IOException("No partitionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.save(container);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
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
  public long saveSecurityProfile(SecurityProfile profile) throws IOException {
    if (securityProfileStore != null) {
      return securityProfileStore.save(profile);
    } else {
      throw new IOException("No securityProfileStore available. Check that it has been declared in the Spring config.");
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

  @Override
  public <T extends List<S>, S extends Plateable> long savePlate(Plate<T, S> plate) throws IOException {
    if (plateStore != null) {
      return plateStore.save(plate);
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveAlert(Alert alert) throws IOException {
    if (alertStore != null) {
      return alertStore.save(alert);
    } else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
    if (entityGroupStore != null) {
      return entityGroupStore.save(entityGroup);
    } else {
      throw new IOException("No entityGroupStore available. Check that it has been declared in the Spring config.");
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
  public Library getLibraryById(long libraryId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.get(libraryId);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Library getLibraryByBarcode(String barcode) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getByBarcode(barcode);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Library getLibraryByAlias(String alias) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getByAlias(alias);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Dilution getDilutionByBarcode(String barcode) throws IOException {
    Dilution d = null;
    if (libraryDilutionStore != null) {
      d = libraryDilutionStore.getLibraryDilutionByBarcode(barcode);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }

    if (emPCRDilutionStore != null) {
      if (d == null) {
        d = emPCRDilutionStore.getEmPcrDilutionByBarcode(barcode);
      }
    } else {
      throw new IOException("No emPcrDilutionStore available. Check that it has been declared in the Spring config.");
    }

    return d;
  }

  @Override
  public Dilution getDilutionByIdAndPlatform(long dilutionid, PlatformType platformType) throws IOException {
    Dilution d = null;
    if (libraryDilutionStore != null) {
      d = libraryDilutionStore.getLibraryDilutionByIdAndPlatform(dilutionid, platformType);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }

    if (emPCRDilutionStore != null) {
      if (d == null) {
        d = emPCRDilutionStore.getEmPcrDilutionByIdAndPlatform(dilutionid, platformType);
      }
    } else {
      throw new IOException("No emPcrDilutionStore available. Check that it has been declared in the Spring config.");
    }

    return d;
  }

  @Override
  public Dilution getDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    Dilution d = null;
    if (libraryDilutionStore != null) {
      d = libraryDilutionStore.getLibraryDilutionByBarcodeAndPlatform(barcode, platformType);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }

    if (emPCRDilutionStore != null) {
      if (d == null) {
        d = emPCRDilutionStore.getEmPcrDilutionByBarcodeAndPlatform(barcode, platformType);
      }
    } else {
      throw new IOException("No emPcrDilutionStore available. Check that it has been declared in the Spring config.");
    }

    return d;
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.getLibraryDilutionByBarcodeAndPlatform(barcode, platformType);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public emPCRDilution getEmPCRDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.getEmPcrDilutionByBarcodeAndPlatform(barcode, platformType);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryDilution getLibraryDilutionById(long dilutionId) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.get(dilutionId);
    } else {
      throw new IOException("No libraryDilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException {
    if (libraryDilutionStore != null) {
      return libraryDilutionStore.getLibraryDilutionByBarcode(barcode);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
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
  public LibraryType getLibraryTypeById(long typeId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibraryTypeById(typeId);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryType getLibraryTypeByDescription(String description) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibraryTypeByDescription(description);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibraryTypeByDescriptionAndPlatform(description, platformType);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeById(long typeId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibrarySelectionTypeById(typeId);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibrarySelectionTypeByName(name);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeById(long typeId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibraryStrategyTypeById(typeId);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibraryStrategyTypeByName(name);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public TagBarcode getTagBarcodeById(long tagBarcodeId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getTagBarcodeById(tagBarcodeId);
    } else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public emPCR getEmPCRById(long pcrId) throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.get(pcrId);
    } else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public emPCRDilution getEmPCRDilutionById(long dilutionId) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.get(dilutionId);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public emPCRDilution getEmPCRDilutionByBarcode(String barcode) throws IOException {
    if (emPCRDilutionStore != null) {
      return emPCRDilutionStore.getEmPcrDilutionByBarcode(barcode);
    } else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool<? extends Poolable> getPoolById(long poolId) throws IOException {
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
  public Pool<? extends Poolable> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException {
    if (poolStore != null) {
      return poolStore.getPoolByBarcode(barcode, platformType);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool<? extends Poolable> getPoolByBarcode(String barcode) throws IOException {
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
  public Pool<? extends Poolable> getPoolByIdBarcode(String barcode) throws IOException {
    if (poolStore != null) {
      return poolStore.getByBarcode(barcode);
    } else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Experiment getExperimentById(long experimentId) throws IOException {
    if (experimentStore != null) {
      return experimentStore.get(experimentId);
    } else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Study getStudyById(long studyId) throws IOException {
    if (studyStore != null) {
      return studyStore.get(studyId);
    } else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerPoolPartition getSequencerPoolPartitionById(long partitionId) throws IOException {
    if (partitionStore != null) {
      return partitionStore.get(partitionId);
    } else {
      throw new IOException("No partitionStore available. Check that it has been declared in the Spring config.");
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
  public Note getNoteById(long noteId) throws IOException {
    if (noteStore != null) {
      return noteStore.get(noteId);
    } else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
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
  public SequencerReference getSequencerReferenceByRunId(long runId) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.getByRunId(runId);
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

  @Override
  public Plate<? extends List<? extends Plateable>, ? extends Plateable> getPlateById(long plateId) throws IOException {
    if (plateStore != null) {
      return plateStore.get(plateId);
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public <T extends List<S>, S extends Plateable> Plate<T, S> getPlateByBarcode(String barcode) throws IOException {
    if (plateStore != null) {
      return plateStore.<T, S> getPlateByIdentificationBarcode(barcode);
    } else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Alert getAlertById(long alertId) throws IOException {
    if (alertStore != null) {
      return alertStore.get(alertId);
    } else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public EntityGroup<? extends Nameable, ? extends Nameable> getEntityGroupById(long entityGroupId) throws IOException {
    if (entityGroupStore != null) {
      return entityGroupStore.get(entityGroupId);
    } else {
      throw new IOException("No entityGroupStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<ChangeLog> listAllChanges(String type) throws IOException {
    return changeLogStore.listAll(type);
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
      return boxStore.save(box);
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
  public Collection<Box> listAllBoxesBySearch(String query) throws IOException {
    if (boxStore != null) {
      return boxStore.listBySearch(query);
    } else {
      throw new IOException("No boxStore available. Check that is has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Box> listAllBoxesByAlias(String alias) throws IOException {
    if (boxStore != null) {
      return boxStore.listByAlias(alias);
    } else {
      throw new IOException("No boxStore available. Check that is has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<String> listAllBoxUsesStrings() throws IOException {
    if (boxStore != null) {
      return boxStore.listAllBoxUsesStrings();
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
  public void emptySingleTube(Box box, String position) throws IOException {
    if (boxStore != null) {
      boxStore.emptySingleTube(box, position);
    } else {
      throw new IOException("No boxStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void emptyAllTubes(Box box) throws IOException {
    if (boxStore != null) {
      boxStore.emptyAllTubes(box);
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
}
