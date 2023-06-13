package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.generateTemporaryName;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ContactService;
import uk.ac.bbsrc.tgac.miso.core.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.core.service.PipelineService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultProjectService implements ProjectService {
  protected static final Logger log = LoggerFactory.getLogger(DefaultProjectService.class);

  @Autowired
  private ProjectStore projectStore;
  @Autowired
  private ReferenceGenomeService referenceGenomeService;
  @Autowired
  private TargetedSequencingService targetedSequencingService;
  @Autowired
  private NamingSchemeHolder namingSchemeHolder;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private FileAttachmentService fileAttachmentService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryTemplateService libraryTemplateService;
  @Autowired
  private SampleNumberPerProjectService sampleNumberPerProjectService;
  @Autowired
  private PipelineService pipelineService;
  @Autowired
  private ContactService contactService;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  @Override
  public Project get(long projectId) throws IOException {
    return projectStore.get(projectId);
  }

  @Override
  public Project getProjectByCode(String projectCode) throws IOException {
    return projectStore.getByCode(projectCode);
  }

  @Override
  public List<Project> list() throws IOException {
    return projectStore.list();
  }

  @Override
  public long create(Project project) throws IOException {
    saveNewContact(project.getContact());
    loadChildEntities(project);
    validateChange(project, null);
    project.setChangeDetails(authorizationManager.getCurrentUser());
    project.setName(generateTemporaryName());
    projectStore.create(project);
    NamingScheme namingScheme = namingSchemeHolder.get(project.isSecondaryNaming());
    try {
      project.setName(namingScheme.generateNameFor(project));
    } catch (MisoNamingException e) {
      throw new ValidationException(new ValidationError("name", e.getMessage()));
    }
    validateNameOrThrow(project, namingScheme);
    return projectStore.update(project);
  }

  @Override
  public long update(Project project) throws IOException {
    Project original = projectStore.get(project.getId());
    saveNewContact(project.getContact());
    loadChildEntities(project);
    validateChange(project, original);
    applyChanges(original, project);
    project = original;
    project.setChangeDetails(authorizationManager.getCurrentUser());
    return projectStore.update(project);
  }

  private void saveNewContact(Contact contact) throws IOException {
    if (contact != null && !contact.isSaved()) {
      try {
        long savedId = contactService.create(contact);
        contact.setId(savedId);
      } catch (ValidationException e) {
        List<ValidationError> errors = e.getErrors().stream().map(error -> {
          String message = error.getMessage();
          if (!ValidationError.GENERAL_PROPERTY.equals(error.getProperty())) {
            message = error.getProperty() + ": " + error.getMessage();
          }
          return new ValidationError("contact", message);
        }).collect(Collectors.toList());
        throw new ValidationException(errors);
      }
    }
  }

  private void validateChange(Project project, Project beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    NamingScheme namingScheme = namingSchemeHolder.get(project.isSecondaryNaming());
    if (ValidationUtils.isSetAndChanged(Project::getCode, project, beforeChange)) {
      // assume that if project code is required by the naming scheme, it is used for generating
      // sample titles
      if (beforeChange != null && !namingScheme.nullProjectCodeAllowed() && hasSamples(beforeChange)) {
        errors.add(new ValidationError("code", "Cannot change because there are already samples in the project"));
      }
    }
    if (project.getCode() == null && detailedSample) {
      errors.add(ValidationError.forRequired("code"));
    }
    ValidationResult codeValidation = namingScheme.validateProjectCode(project.getCode());
    if (!codeValidation.isValid()) {
      errors.add(new ValidationError("code", codeValidation.getMessage()));
    }
    if ((beforeChange == null
        || (project.getCode() != null && !project.getCode().equals(beforeChange.getCode())))
        && (project.getCode() != null && getProjectByCode(project.getCode()) != null)) {
      errors.add(new ValidationError("code", "There is already a project with this code"));
    }
    if ((beforeChange == null || !project.getTitle().equals(beforeChange.getTitle()))
        && projectStore.getByTitle(project.getTitle()) != null) {
      errors.add(new ValidationError("title", "There is already a project with this title"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void loadChildEntities(Project project) throws IOException {
    loadChildEntity(project::setReferenceGenome, project.getReferenceGenome(), referenceGenomeService,
        "referenceGenomeId");
    loadChildEntity(project::setDefaultTargetedSequencing, project.getDefaultTargetedSequencing(),
        targetedSequencingService,
        "defaultTargetedSequencingId");
    loadChildEntity(project::setPipeline, project.getPipeline(), pipelineService, "pipelineId");
    loadChildEntity(project::setContact, project.getContact(), contactService, "contactId");

  }

  private void applyChanges(Project original, Project project) {
    original.setTitle(project.getTitle());
    original.setDescription(project.getDescription());
    original.setStatus(project.getStatus());
    original.setReferenceGenome(project.getReferenceGenome());
    original.setDefaultTargetedSequencing(project.getDefaultTargetedSequencing());
    original.setCode(project.getCode());
    original.setPipeline(project.getPipeline());
    original.setRebNumber(project.getRebNumber());
    original.setRebExpiry(project.getRebExpiry());
    original.setSamplesExpected(project.getSamplesExpected());
    original.setContact(project.getContact());
    ValidationUtils.applySetChanges(original.getAssays(), project.getAssays());
  }

  public void setNamingSchemeHolder(NamingSchemeHolder namingSchemeHolder) {
    this.namingSchemeHolder = namingSchemeHolder;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public void setReferenceGenomeService(ReferenceGenomeService referenceGenomeService) {
    this.referenceGenomeService = referenceGenomeService;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
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
  public void authorizeDeletion(Project object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult validateDeletion(Project object)
      throws IOException {
    uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult result =
        new uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult();

    long samples = sampleService.count(PaginationFilter.project(object.getId()));
    if (samples > 0L) {
      result.addError(new ValidationError(String.format("Project %s contains %d samples",
          object.getCode() == null ? object.getTitle() : object.getCode(), samples)));
    }

    return result;
  }

  @Override
  public void beforeDelete(Project object) throws IOException {
    fileAttachmentService.beforeDelete(object);

    List<LibraryTemplate> templates = libraryTemplateService.listByProject(object.getId());
    for (LibraryTemplate template : templates) {
      template.getProjects().removeIf(templateProject -> templateProject.getId() == object.getId());
      libraryTemplateService.update(template);
    }
    SampleNumberPerProject sampleNumberPerProject = sampleNumberPerProjectService.getByProject(object);
    if (sampleNumberPerProject != null) {
      sampleNumberPerProjectService.delete(sampleNumberPerProject);
    }
  }

  @Override
  public void afterDelete(Project object) throws IOException {
    fileAttachmentService.afterDelete(object);
  }

  @Override
  public boolean hasSamples(Project project) throws IOException {
    Project managed = projectStore.get(project.getId());
    return projectStore.getUsage(managed) > 0L;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return projectStore.count(errorHandler, filter);
  }

  @Override
  public List<Project> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return projectStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}
