package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.KitService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.StudyService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.persistence.ExperimentStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultExperimentService implements ExperimentService {
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private ExperimentStore experimentStore;
  @Autowired
  private KitService kitService;
  @Autowired
  private NamingSchemeHolder namingSchemeHolder;
  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private StudyService studyService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private RunService runService;

  @Override
  public Experiment get(long experimentId) throws IOException {
    return experimentStore.get(experimentId);
  }

  @Override
  public List<Experiment> list() throws IOException {
    return experimentStore.list();
  }

  @Override
  public Collection<Experiment> listAllByStudyId(long studyId) throws IOException {
    return studyService.get(studyId).getExperiments();
  }

  @Override
  public long create(Experiment experiment) throws IOException {
    loadRunPartitions(experiment);
    experiment.setName(LimsUtils.generateTemporaryName());
    experiment.setInstrumentModel(instrumentModelService.get(experiment.getInstrumentModel().getId()));
    experiment.setLibrary(libraryService.get(experiment.getLibrary().getId()));
    experiment.setStudy(studyService.get(experiment.getStudy().getId()));
    experiment.setChangeDetails(authorizationManager.getCurrentUser());

    experimentStore.create(experiment);
    String name;
    try {
      name = namingSchemeHolder.getPrimary().generateNameFor(experiment);
    } catch (MisoNamingException e) {
      throw new IOException("Cannot save Experiment - failed to generate a valid name", e);
    }
    experiment.setName(name);

    ValidationResult nameValidation = namingSchemeHolder.getPrimary().validateName(experiment.getName());
    if (!nameValidation.isValid()) {
      throw new IOException("Cannot save Experiment - invalid name:" + nameValidation.getMessage());
    }
    return experimentStore.update(experiment);
  }

  @Override
  public long update(Experiment experiment) throws IOException {
    loadRunPartitions(experiment);
    ValidationResult nameValidation = namingSchemeHolder.getPrimary().validateName(experiment.getName());
    if (!nameValidation.isValid()) {
      throw new IOException("Cannot save Experiment - invalid name:" + nameValidation.getMessage());
    }

    Experiment original = experimentStore.get(experiment.getId());
    original.setAccession(experiment.getAccession());
    original.setAlias(experiment.getAlias());
    original.setDescription(experiment.getDescription());
    original.setName(experiment.getName());
    original.setInstrumentModel(instrumentModelService.get(experiment.getInstrumentModel().getId()));
    original.setLibrary(libraryService.get(experiment.getLibrary().getId()));
    original.setStudy(studyService.get(experiment.getStudy().getId()));
    original.setTitle(experiment.getTitle());
    original.setRunPartitions(experiment.getRunPartitions());// These have been already reloaded.
    original.getRunPartitions().forEach(rp -> rp.setExperiment(original));
    Set<Kit> kits = new HashSet<>();
    for (Kit k : experiment.getKits()) {
      kits.add(kitService.get(k.getId()));
    }
    original.setKits(kits);
    original.setChangeDetails(authorizationManager.getCurrentUser());
    return experimentStore.update(original);
  }

  public void loadRunPartitions(Experiment experiment) {
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
  }

  public void setNamingSchemeHolder(NamingSchemeHolder namingSchemeHolder) {
    this.namingSchemeHolder = namingSchemeHolder;
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

  public void setInstrumentModelService(InstrumentModelService instrumentModelService) {
    this.instrumentModelService = instrumentModelService;
  }

  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @Override
  public Collection<Experiment> listAllByLibraryId(long id) throws AuthorizationException, IOException {
    return experimentStore.listByLibrary(id);
  }

  @Override
  public List<Experiment> listAllByRunId(long runId) throws IOException {
    return experimentStore.listByRun(runId);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public void authorizeDeletion(Experiment object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult validateDeletion(Experiment object)
      throws IOException {
    uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult result =
        new uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult();
    long usage = experimentStore.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.submissions(usage)));
    }
    return result;
  }

}
