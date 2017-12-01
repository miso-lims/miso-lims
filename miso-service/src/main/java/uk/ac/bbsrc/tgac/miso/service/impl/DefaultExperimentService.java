package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultExperimentService implements ExperimentService, NamingSchemeAware {
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ExperimentStore experimentStore;
  @Autowired
  private KitService kitService;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private PlatformService platformService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private SecurityStore securityStore;
  @Autowired
  private SecurityProfileStore securityProfileStore;
  @Autowired
  private StudyService studyService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private RunService runService;

  @Override
  public void delete(Experiment experiment) throws IOException {
    authorizationManager.throwIfNonAdmin();
    if (!experimentStore.remove(experiment)) {
      throw new IOException("Unable to delete Experiment. Make sure the experiment has no child entitites.");
    }
  }

  @Override
  public Experiment get(long experimentId) throws IOException {
    Experiment o = experimentStore.get(experimentId);
    authorizationManager.throwIfNotReadable(o);
    return o;
  }

  @Override
  public Map<String, Integer> getColumnSizes() throws IOException {
    return experimentStore.getExperimentColumnSizes();
  }

  public NamingScheme getNamingScheme() {
    return namingScheme;
  }

  @Override
  public Collection<Experiment> listAll() throws IOException {
    return authorizationManager.filterUnreadable(experimentStore.listAll());
  }

  @Override
  public Collection<Experiment> listAllBySearch(String query) throws IOException {
    return authorizationManager.filterUnreadable(experimentStore.listBySearch(query));
  }

  @Override
  public Collection<Experiment> listAllByStudyId(long studyId) throws IOException {
    return authorizationManager.filterUnreadable(studyService.get(studyId).getExperiments());
  }

  @Override
  public Collection<Experiment> listAllWithLimit(long limit) throws IOException {
    return authorizationManager.filterUnreadable(experimentStore.listAllWithLimit(limit));
  }

  @Override
  public long save(Experiment experiment) throws IOException {
    try {
      authorizationManager.throwIfNotWritable(experiment);
      experiment.setLastModifier(authorizationManager.getCurrentUser());
      if (experiment.getRunPartitions() == null) {
        experiment.setRunPartitions(Collections.emptyList());
      } else {
        experiment.setRunPartitions(experiment.getRunPartitions().stream().map(WhineyFunction.rethrow(from -> {
          RunPartition to = new RunPartition();
          to.setExperiment(experiment);
          to.setPartition(containerService.getPartition(from.getPartition().getId()));
          to.setRun(runService.get(from.getRun().getId()));
          return to;
        })).collect(
            Collectors.toList()));
      }
      if (experiment.getId() == Experiment.UNSAVED_ID) {
        experiment.setName(DbUtils.generateTemporaryName());
        experiment.setPlatform(platformService.get(experiment.getPlatform().getId()));
        experiment.setLibrary(libraryService.get(experiment.getLibrary().getId()));
        experiment.setStudy(studyService.get(experiment.getStudy().getId()));
        if (experiment.getSecurityProfile().getProfileId() == SecurityProfile.UNSAVED_ID) {
          securityProfileStore.save(experiment.getSecurityProfile());
        } else {
          experiment.setSecurityProfile(securityProfileStore.get(experiment.getSecurityProfile().getProfileId()));
        }

        experimentStore.save(experiment);
        String name = namingScheme.generateNameFor(experiment);
        experiment.setName(name);

        ValidationResult nameValidation = namingScheme.validateName(experiment.getName());
        if (!nameValidation.isValid()) {
          throw new IOException("Cannot save Experiment - invalid name:" + nameValidation.getMessage());
        }
        return experimentStore.save(experiment);
      }
      ValidationResult nameValidation = namingScheme.validateName(experiment.getName());
      if (!nameValidation.isValid()) {
        throw new IOException("Cannot save Experiment - invalid name:" + nameValidation.getMessage());
      }

      Experiment original = experimentStore.get(experiment.getId());
      authorizationManager.throwIfNotWritable(original);
      original.setAccession(experiment.getAccession());
      original.setAlias(experiment.getAlias());
      original.setDescription(experiment.getDescription());
      original.setName(experiment.getName());
      original.setPlatform(platformService.get(experiment.getPlatform().getId()));
      original.setLibrary(libraryService.get(experiment.getLibrary().getId()));
      original.setStudy(studyService.get(experiment.getStudy().getId()));
      original.setSecurityProfile(securityStore.getSecurityProfileById(experiment.getSecurityProfile().getProfileId()));
      original.setTitle(experiment.getTitle());
      original.setRunPartitions(experiment.getRunPartitions());// These have been already reloaded.
      original.getRunPartitions().forEach(rp -> rp.setExperiment(original));
      Set<Kit> kits = new HashSet<>();
      for (Kit k : experiment.getKits()) {
        kits.add(kitService.getKitById(k.getId()));
      }
      original.setKits(kits);
      original.setLastModifier(authorizationManager.getCurrentUser());
      return experimentStore.save(original);
    } catch (MisoNamingException e) {
      throw new IOException("Cannot save Experiment - issue with naming scheme", e);
    }
  }

  @Override
  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setExperimentStore(ExperimentStore experimentStore) {
    this.experimentStore = experimentStore;
  }

  public void setKitService(KitService kitService) {
    this.kitService = kitService;
  }

  public void setPlatformService(PlatformService platformService) {
    this.platformService = platformService;
  }

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setSecurityProfileStore(SecurityProfileStore securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
  }

  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @Override
  public Collection<Experiment> listAllByLibraryId(long id) throws AuthorizationException, IOException {
    return authorizationManager.filterUnreadable(experimentStore.listByLibrary(id));
  }

  @Override
  public List<Experiment> listAllByRunId(long runId) throws IOException {
    return authorizationManager.filterUnreadable(experimentStore.listByRun(runId));
  }

}
