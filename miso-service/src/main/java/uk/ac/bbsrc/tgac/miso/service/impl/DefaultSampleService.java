package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl.IdentityBuilder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.IdentityView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableReferenceService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.core.service.LabService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.core.service.ScientificNameService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingControlTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.service.StainService;
import uk.ac.bbsrc.tgac.miso.core.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.core.service.TransferService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissuePieceTypeDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleService implements SampleService {

  private static final Logger log = LoggerFactory.getLogger(DefaultSampleService.class);

  private static final String ERR_MISSING_PARENT_ID = "Detailed sample is missing parent identifier";

  @Autowired
  private SampleStore sampleStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;
  @Autowired
  private ProjectStore projectStore;
  @Autowired
  private TissueOriginDao tissueOriginDao;
  @Autowired
  private TissueTypeDao tissueTypeDao;
  @Autowired
  private DetailedQcStatusService detailedQcStatusService;
  @Autowired
  private SamplePurposeDao samplePurposeDao;
  @Autowired
  private TissueMaterialDao tissueMaterialDao;
  @Autowired
  private TissuePieceTypeDao tissuePieceTypeDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private LabService labService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private StainService stainService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private WorksetService worksetService;
  @Autowired
  private SequencingControlTypeService sequencingControlTypeService;
  @Autowired
  private ScientificNameService scientificNameService;
  @Autowired
  private FileAttachmentService fileAttachmentService;
  @Autowired
  private TransferService transferService;
  @Autowired
  private SubprojectService subprojectService;
  @Autowired
  private SopService sopService;
  @Autowired
  private RequisitionService requisitionService;
  @Autowired
  private BarcodableReferenceService barcodableReferenceService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private NamingSchemeHolder namingSchemeHolder;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  private Boolean uniqueExternalNameWithinProjectRequired = true;

  public void setSampleStore(SampleStore sampleStore) {
    this.sampleStore = sampleStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setSampleClassService(SampleClassService sampleClassService) {
    this.sampleClassService = sampleClassService;
  }

  public void setSampleValidRelationshipService(SampleValidRelationshipService sampleValidRelationshipService) {
    this.sampleValidRelationshipService = sampleValidRelationshipService;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public void setTissueOriginDao(TissueOriginDao tissueOriginDao) {
    this.tissueOriginDao = tissueOriginDao;
  }

  public void setTissueTypeDao(TissueTypeDao tissueTypeDao) {
    this.tissueTypeDao = tissueTypeDao;
  }

  public void setDetailedQcStatusService(DetailedQcStatusService detailedQcStatusService) {
    this.detailedQcStatusService = detailedQcStatusService;
  }

  public void setSubprojectService(SubprojectService subprojectService) {
    this.subprojectService = subprojectService;
  }

  public void setSamplePurposeDao(SamplePurposeDao samplePurposeDao) {
    this.samplePurposeDao = samplePurposeDao;
  }

  public void setTissueMaterialDao(TissueMaterialDao tissueMaterialDao) {
    this.tissueMaterialDao = tissueMaterialDao;
  }

  public void setLabService(LabService labService) {
    this.labService = labService;
  }

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  public void setWorksetService(WorksetService worksetService) {
    this.worksetService = worksetService;
  }

  public void setFileAttachmentService(FileAttachmentService fileAttachmentService) {
    this.fileAttachmentService = fileAttachmentService;
  }

  public void setNamingSchemeHolder(NamingSchemeHolder namingSchemeHolder) {
    this.namingSchemeHolder = namingSchemeHolder;
  }

  @CoverageIgnore
  public Boolean getAutoGenerateIdBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @CoverageIgnore
  public void setAutoGenerateIdBarcodes(Boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

  @CoverageIgnore
  public Boolean isUniqueExternalNameWithinProjectRequired() {
    return uniqueExternalNameWithinProjectRequired;
  }

  @CoverageIgnore
  public void setUniqueExternalNameWithinProjectRequired(Boolean uniqueExternalNameWithinProjectRequired) {
    this.uniqueExternalNameWithinProjectRequired = uniqueExternalNameWithinProjectRequired;
  }

  @Override
  public Sample get(long sampleId) throws IOException {
    return sampleStore.getSample(sampleId);
  }

  @Override
  public long create(Sample sample) throws IOException {
    findOrCreateRequisition(sample);
    loadChildEntities(sample);
    boxService.throwIfBoxPositionIsFilled(sample);
    User changeUser = authorizationManager.getCurrentUser();
    sample.setChangeDetails(changeUser);
    if (isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      if (!isIdentitySample(sample)) {
        // Create a copy of the incoming sample's identity
        SampleIdentity identityCopy = getIdentity(detailed);
        if (detailed.getParent() == null) {
          throw new IllegalArgumentException(ERR_MISSING_PARENT_ID);
        }
        if (detailed.getParent().isSaved()) {
          detailed.setParent((DetailedSample) get(detailed.getParent().getId()));
        } else {
          try {
            setIdentity(detailed);
            if (!isIdentitySample(detailed.getParent())) {
              detailed.setParent(findOrCreateParent(detailed));
            }
          } catch (MisoNamingException e) {
            throw new ValidationException(e.getMessage());
          }
        }
        addExternalNames(detailed, identityCopy);
        validateHierarchy(detailed);
      } else {
        if (isUniqueExternalNameWithinProjectRequired() && isExternalNameDuplicatedInProject(sample)) {
          throw makeDuplicateExternalNameError(((SampleIdentity) sample).getExternalName());
        }
      }
    }
    // pre-save field generation
    sample.setName(generateTemporaryName());
    if (isStringEmptyOrNull(sample.getAlias()) && getNamingScheme(sample).hasSampleAliasGenerator()) {
      sample.setAlias(generateTemporaryName());
    }
    if (sample.getConcentration() == null) {
      sample.setConcentrationUnits(null);
    }
    if (sample.getVolume() == null) {
      sample.setVolumeUnits(null);
    } else {
      sample.setInitialVolume(sample.getVolume());
    }
    if (isSampleSlide(sample)) {
      ((SampleSlide) sample).setInitialSlides(((SampleSlide) sample).getSlides());
    }
    LimsUtils.updateParentVolume(sample, null, changeUser);
    updateParentSlides(sample, null, changeUser);
    validateChange(sample, null);
    long savedId = save(sample, true).getId();
    if (sample.getParent() != null) {
      sampleStore.update(sample.getParent());
    }
    boxService.updateBoxableLocation(sample);
    if (sample.getCreationReceiptInfo() != null) {
      TransferSample transferSample = sample.getCreationReceiptInfo();
      Transfer transfer = transferSample.getTransfer();
      Transfer existingTransfer = transferService
          .listByProperties(transfer.getSenderLab(), transfer.getRecipientGroup(),
              sample.getProject(), transfer.getTransferTime())
          .stream()
          .max(Comparator.comparing(Transfer::getCreationTime)).orElse(null);
      if (existingTransfer != null) {
        existingTransfer.getSampleTransfers().add(transferSample);
        transferSample.setTransfer(existingTransfer);
        transferService.addTransferSample(transferSample);
      } else {
        transferService.create(transfer);
      }
    }
    // Don't log initial additions to requisition, as that's part of the requisition creation
    if (sample.getRequisition() != null
        && sample.getRequisition().getCreationTime().toInstant().isBefore(Instant.now().minus(1, ChronoUnit.HOURS))) {
      addRequisitionSampleChange(sample.getRequisition(), sample, true);
    }
    return savedId;
  }

  private void findOrCreateRequisition(Sample sample) throws IOException {
    if (sample.getRequisition() != null && !sample.getRequisition().isSaved()) {
      Requisition existing = requisitionService.getByAlias(sample.getRequisition().getAlias());
      if (existing == null) {
        long requisitionId = requisitionService.create(sample.getRequisition());
        sample.getRequisition().setId(requisitionId);
      } else {
        sample.setRequisition(existing);
      }
    }
  }

  private ValidationException makeDuplicateExternalNameError(String externalName) {
    return new ValidationException(new ValidationError("externalName",
        String.format("Sample with external name or alias '%s' already exists in this project", externalName)));
  }

  private NamingScheme getNamingScheme(Sample sample) {
    return namingSchemeHolder.get(sample.getProject().isSecondaryNaming());
  }

  private void updateParentSlides(Sample child, Sample beforeChange, User changeUser) {
    if (child.getParent() == null || !isSampleSlide(child.getParent())) {
      return;
    }
    SampleSlide parent = (SampleSlide) deproxify(child.getParent());
    Integer slidesConsumed = null;
    if (isTissuePieceSample(child)) {
      slidesConsumed = ((SampleTissuePiece) child).getSlidesConsumed();
    } else if (isStockSample(child)) {
      slidesConsumed = ((SampleStock) child).getSlidesConsumed();
    }
    if (slidesConsumed == null) {
      return;
    }
    if (beforeChange == null) {
      updateParentSlides(parent, parent.getSlides() - slidesConsumed, changeUser);
    } else {
      Integer beforeSlidesConsumed = null;
      if (isTissuePieceSample(beforeChange)) {
        beforeSlidesConsumed = ((SampleTissuePiece) beforeChange).getSlidesConsumed();
      } else if (isStockSample(beforeChange)) {
        beforeSlidesConsumed = ((SampleStock) beforeChange).getSlidesConsumed();
      }
      if (!slidesConsumed.equals(beforeSlidesConsumed)) {
        updateParentSlides(parent, parent.getSlides() + beforeSlidesConsumed - slidesConsumed, changeUser);
      }
    }
  }

  private void updateParentSlides(SampleSlide parent, Integer value, User changeUser) {
    parent.setChangeDetails(changeUser);
    parent.setSlides(value);
  }

  /**
   * Saves the Sample as is to the database. Fields which should be autogenerated but first require
   * the primary key are then generated, and the changes are saved
   * 
   * @param sample the Sample to save
   * @return the same Sample provided, including fields that were generated by this method
   * @throws IOException
   */
  private Sample save(Sample sample, boolean validateAliasUniqueness) throws IOException {
    NamingScheme namingScheme = getNamingScheme(sample);
    try {
      Long newId = sample.getId();
      if (!hasTemporaryAlias(sample)) {
        validateAlias(sample, namingScheme);
      }
      if (!sample.isSaved()) {
        newId = sampleStore.create(sample);
      } else {
        sampleStore.update(sample);
      }
      Sample created = sampleStore.getSample(newId);

      // post-save field generation
      boolean needsUpdate = false;
      if (hasTemporaryName(sample)) {
        try {
          created.setName(namingScheme.generateNameFor(created));
        } catch (MisoNamingException e) {
          throw new ValidationException(new ValidationError("name", e.getMessage()));
        }
        validateNameOrThrow(created, namingScheme);
        needsUpdate = true;
      }
      if (hasTemporaryAlias(sample)) {
        String generatedAlias;
        try {
          generatedAlias = namingScheme.generateSampleAlias(created);
        } catch (MisoNamingException e) {
          throw new ValidationException(new ValidationError("alias", e.getMessage()));
        }
        created.setAlias(generatedAlias);
        if (isDetailedSample(created)) {
          // generation of non-standard aliases is allowed
          ((DetailedSample) created).setNonStandardAlias(!namingScheme.validateSampleAlias(generatedAlias).isValid());
        } else {
          validateAlias(created, namingScheme);
        }
        needsUpdate = true;
      }
      if (autoGenerateIdBarcodes && isStringEmptyOrNull(created.getIdentificationBarcode())) {
        // if !autoGenerateIdBarcodes then the identificationBarcode is set by the user
        generateAndSetIdBarcode(created);
        needsUpdate = true;
      }
      if (needsUpdate) {
        sampleStore.update(created);
      }

      if (validateAliasUniqueness) {
        validateAliasUniqueness(created, namingScheme);
      }
      return created;
    } catch (ConstraintViolationException e) {
      // Send the nested root cause message to the user, since it contains the actual error.
      throw new ConstraintViolationException(e.getMessage() + " " + ExceptionUtils.getRootCauseMessage(e),
          e.getSQLException(),
          e.getConstraintName());
    }
  }

  /**
   * Checks whether sample's alias conforms to the naming scheme. Validation is skipped for
   * DetailedSamples {@code if (sample.hasNonStandardAlias())}
   * 
   * @param sample
   */
  private void validateAlias(Sample sample, NamingScheme namingScheme) {
    if (!isDetailedSample(sample) || !((DetailedSample) sample).hasNonStandardAlias()) {
      uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult aliasValidation =
          namingScheme.validateSampleAlias(sample
              .getAlias());
      if (!aliasValidation.isValid()) {
        throw new ValidationException(new ValidationError("alias", aliasValidation.getMessage()));
      }
    }
  }

  /**
   * Checks whether the configured naming scheme allows duplicate alias. If not, checks whether an
   * alias is used by multiple samples. This method should be called <b>after</b> saving a new Sample.
   * DetailedSamples marked as having non-standard alias are also considered valid
   * 
   * @param alias the alias to validate
   * @throws ConstraintViolationException if duplicate alias are <b>not</b> allowed, <b>and</b> the
   *         Sample does not have nonStandardAlias <b>and</b> the alias is used by multiple Samples
   * @throws IOException
   */
  private void validateAliasUniqueness(Sample sample, NamingScheme namingScheme) throws IOException {
    if (isDetailedSample(sample) && ((DetailedSample) sample).hasNonStandardAlias()) {
      return;
    }
    if (!namingScheme.duplicateSampleAliasAllowed() && sampleStore.listByAlias(sample.getAlias()).size() > 1) {
      throw new ValidationException(
          new ValidationError("alias", String.format("A sample with alias '%s' already exists",
              sample.getAlias())));
    }
  }

  /**
   * Adds any external names in the sample's attached identity to the identity stored in the database
   * 
   * @param sample
   * @throws IOException
   */

  private void addExternalNames(DetailedSample sample, SampleIdentity identityCopy) throws IOException {
    if (identityCopy == null || identityCopy.getExternalName() == null)
      return;
    SampleIdentity identity = (SampleIdentity) LimsUtils.deproxify(get(getIdentity(sample).getId()));
    Set<String> identityExternalNames = SampleIdentityImpl.getSetFromString(identity.getExternalName());
    Set<String> tempExternalNames = SampleIdentityImpl.getSetFromString(identityCopy.getExternalName());
    Set<String> lowerCaseIdentityExternalNames =
        identityExternalNames.stream().map(String::toLowerCase).collect(Collectors.toSet());

    for (String name : tempExternalNames) {
      if (!lowerCaseIdentityExternalNames.contains(name.toLowerCase()) && !(isUniqueExternalNameWithinProjectRequired()
          &&
          (identity.getProject() == null
              || getIdentitiesByExternalNameOrAliasAndProject(name, identity.getProject().getId(), true).size() > 0))) {
        identityExternalNames.add(name);
      }
    }

    if (identityExternalNames.size() > lowerCaseIdentityExternalNames.size()) {
      identity.setExternalName(String.join(",", identityExternalNames));
      identity.setChangeDetails(authorizationManager.getCurrentUser());
      sampleStore.update(identity);
    }
  }

  /**
   * Checks whether the given external name(s) (may be multiple comma-separated names) is required to
   * be unique within a project, then if it actually is unique within a project. This method should be
   * called <b>before</b> saving an Identity.
   * 
   * @param newExternalName the String to validate
   * @param project the project that will be associated with the Identity
   * @throws ConstraintViolationException if the external name is already used in this project
   * @throws IOException
   */
  @Override
  public void confirmExternalNameUniqueForProjectIfRequired(String newExternalName, Sample sample)
      throws IOException, ConstraintViolationException {
    if (!isUniqueExternalNameWithinProjectRequired())
      return;
    Collection<IdentityView> matches = getIdentitiesByExternalNameOrAliasAndProject(newExternalName,
        sample.getProject().getId(), true);
    if (!matches.isEmpty()) {
      for (IdentityView match : matches) {
        if (match.getId() != sample.getId()) {
          throw makeDuplicateExternalNameError(newExternalName);
        }
      }
    }
  }

  /**
   * Finds an existing parent Sample or creates a new one if necessary
   * 
   * @param sample must contain parent (via {@link DetailedSample#getParent() getParent}), including
   *        externalName if a new parent is to be created. An existing parent may be specified by
   *        including its sampleId or externalName
   * 
   * @return
   * @throws IOException
   * @throws SQLException
   * @throws MisoNamingException
   */
  private DetailedSample findOrCreateParent(DetailedSample sample)
      throws IOException, MisoNamingException, ConstraintViolationException {
    if (sample.getParent() == null) {
      throw new IllegalArgumentException(ERR_MISSING_PARENT_ID);
    }
    DetailedSample tempParent = sample.getParent();
    if (tempParent.isSaved()) {
      Sample parent = sampleStore.getSample(tempParent.getId());
      if (parent == null)
        throw new IllegalArgumentException("Parent sample does not exist");
      else
        return (DetailedSample) parent;
    } else {
      if (isTissueSample(tempParent) && isIdentitySample(tempParent.getParent())) {
        DetailedSample tissueParent = sampleStore.getMatchingGhostTissue((SampleTissue) tempParent);
        if (tissueParent != null) {
          return tissueParent;
        }
      }
      return createGhostParent(tempParent, sample);
    }
  }

  private DetailedSample createGhostParent(DetailedSample parent, DetailedSample child) throws IOException {
    parent.setProject(child.getProject());
    parent.setSubproject(child.getSubproject());
    parent.setSampleType(child.getSampleType());
    parent.setScientificName(child.getScientificName());
    parent.setVolume(BigDecimal.ZERO);
    parent.setVolumeUnits(VolumeUnit.MICROLITRES);
    parent.setSynthetic(true);
    if (child.getIdentityId() != null)
      parent.setIdentityId(child.getIdentityId());
    if (isTissuePieceSample(child) && isSampleSlide(parent)) {
      SampleSlide parentSlides = (SampleSlide) parent;
      Integer slides = parentSlides.getSlides() == null ? 0 : parentSlides.getSlides();
      slides += ((SampleTissuePiece) child).getSlidesConsumed();
      parentSlides.setSlides(slides);
      if (parentSlides.isSaved()) {
        update(parentSlides);
      }
    }
    create(parent);
    return parent;
  }

  private void setIdentity(DetailedSample descendant) throws IOException, MisoNamingException {
    if (descendant.getParent() == null) {
      throw new IllegalArgumentException(ERR_MISSING_PARENT_ID);
    }
    DetailedSample child = descendant;
    DetailedSample parent = descendant.getParent();
    while (parent.getParent() != null) {
      child = parent;
      parent = child.getParent();
    }
    if (!isIdentitySample(parent)) {
      throw new IllegalStateException("Missing Identity at root of hierarchy");
    }
    if (descendant.getIdentityId() != null) {
      parent.setId(descendant.getIdentityId());
    }
    SampleIdentity identity = findOrCreateIdentity(descendant, (SampleIdentity) parent);
    child.setParent(identity);
  }

  /**
   * Finds the identity of a sample by climbing the hierarchy
   * 
   * @param sample
   * @return identity attached to sample or null if no identity is attached
   * @throws IOException
   */
  private SampleIdentity getIdentity(DetailedSample sample) throws IOException {
    if (sample.getParent() == null) {
      throw new IllegalArgumentException(ERR_MISSING_PARENT_ID);
    }
    SampleIdentity identity = LimsUtils.getParent(SampleIdentity.class, sample);
    return identity;
  }

  private SampleIdentity findOrCreateIdentity(DetailedSample descendant, SampleIdentity identity)
      throws IOException, MisoNamingException {
    if (identity.isSaved()) {
      Sample managedIdentity = sampleStore.getSample(identity.getId());
      return (SampleIdentity) LimsUtils.deproxify(managedIdentity);
    } else {
      // If samples are being bulk received for the same new donor, they will all have a null parentId.
      // After the new donor's Identity is created, the following samples need to be parented to that
      // now-existing Identity.
      Collection<IdentityView> newlyCreated = getIdentitiesByExternalNameOrAliasAndProject(identity.getExternalName(),
          descendant.getProject().getId(), true);
      if (newlyCreated.size() > 1) {
        throw new IllegalArgumentException(
            "IdentityId is required since there are multiple identities with external name "
                + identity.getExternalName()
                + " in project " + descendant.getProject().getId());
      } else if (newlyCreated.size() == 1) {
        IdentityView parent = newlyCreated.iterator().next();
        if (parent == null) {
          throw new IllegalArgumentException("Parent sample does not exist");
        } else {
          return (SampleIdentity) get(parent.getId());
        }
      } else {
        try {
          return createParentIdentity(descendant, identity);
        } catch (SQLException e) {
          throw new IOException(e);
        }
      }
    }
  }

  private SampleIdentity createParentIdentity(DetailedSample sample, SampleIdentity identity)
      throws IOException, MisoNamingException,
      SQLException {
    log.debug("Creating a new Identity to use as a parent.");
    List<SampleClass> identityClasses = sampleClassService.listByCategory(SampleIdentity.CATEGORY_NAME);
    if (identityClasses.size() != 1) {
      throw new IllegalStateException(
          "Found more or less than one SampleClass of category " + SampleIdentity.CATEGORY_NAME
              + ". Cannot choose which to use as root sample class.");
    }
    SampleClass rootSampleClass = identityClasses.get(0);
    confirmExternalNameUniqueForProjectIfRequired(identity.getExternalName(), sample);

    Sample identitySample = new IdentityBuilder().project(sample.getProject())
        .sampleType(sample.getSampleType()).scientificName(sample.getScientificName()).name(generateTemporaryName())
        .rootSampleClass(rootSampleClass).volume(BigDecimal.ZERO).externalName(identity.getExternalName())
        .donorSex(identity.getDonorSex()).consentLevel(identity.getConsentLevel()).build();
    identitySample.setAlias(getNamingScheme(sample).generateSampleAlias(identitySample));

    identitySample.setChangeDetails(authorizationManager.getCurrentUser());
    return (SampleIdentity) save(identitySample, true);
  }

  /**
   * Loads persisted objects into sample fields. Should be called before saving new samples. Loads all
   * member objects <b>except</b>
   * <ul>
   * <li>parent sample for detailed samples</li>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param sample the Sample to load entities into. Must contain at least the IDs of objects to load
   *        (e.g. to load the persisted Project into sample.project, sample.project.id must be set)
   * @throws IOException
   */
  private void loadChildEntities(Sample sample) throws IOException {
    if (sample.getProject() != null) {
      sample.setProject(projectStore.get(sample.getProject().getId()));
    }
    loadChildEntity(sample::setScientificName, sample.getScientificName(), scientificNameService, "scientificNameId");
    loadChildEntity(sample::setSequencingControlType, sample.getSequencingControlType(), sequencingControlTypeService,
        "sequencingControlTypeId");
    loadChildEntity(sample::setSop, sample.getSop(), sopService, "sopId");
    loadChildEntity(sample::setDetailedQcStatus, sample.getDetailedQcStatus(), detailedQcStatusService,
        "detailedQcStatusId");
    loadChildEntity(sample::setRequisition, sample.getRequisition(), requisitionService, "requisitionId");
    if (isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      if (detailed.getSampleClass() != null && detailed.getSampleClass().isSaved()) {
        detailed.setSampleClass(sampleClassService.get(detailed.getSampleClass().getId()));
      }
      if (detailed.getSubproject() != null && detailed.getSubproject().isSaved()) {
        detailed.setSubproject(subprojectService.get(detailed.getSubproject().getId()));
      }
      if (isTissueProcessingSample(detailed)) {
        if (detailed instanceof SampleSlide) {
          Stain originalStain = ((SampleSlide) detailed).getStain();
          Stain stain;
          if (originalStain == null) {
            stain = null;
          } else {
            stain = stainService.get(originalStain.getId());
          }
          ((SampleSlide) detailed).setStain(stain);
        } else if (detailed instanceof SampleTissuePiece) {
          SampleTissuePiece tissuePiece = (SampleTissuePiece) detailed;
          tissuePiece.setTissuePieceType(tissuePieceTypeDao.get(tissuePiece.getTissuePieceType().getId()));
          if (tissuePiece.getReferenceSlide() != null) {
            Sample ref = deproxify(get(tissuePiece.getReferenceSlide().getId()));
            tissuePiece.setReferenceSlide((SampleSlide) ref);
          }
        }
      }
      if (isAliquotSample(detailed)) {
        SampleAliquot sa = (SampleAliquot) detailed;
        if (sa.getSamplePurpose() != null && sa.getSamplePurpose().isSaved()) {
          sa.setSamplePurpose(samplePurposeDao.get(sa.getSamplePurpose().getId()));
        }
      }
      if (isStockSample(detailed)) {
        SampleStock stock = (SampleStock) deproxify(detailed);
        if (stock.getReferenceSlide() != null) {
          Sample ref = deproxify(get(stock.getReferenceSlide().getId()));
          stock.setReferenceSlide((SampleSlide) ref);
        }
      }
      if (isTissueSample(detailed)) {
        SampleTissue st = (SampleTissue) detailed;
        if (st.getTissueMaterial() != null && st.getTissueMaterial().isSaved()) {
          st.setTissueMaterial(tissueMaterialDao.get(st.getTissueMaterial().getId()));
        }
        if (st.getTissueOrigin() != null && st.getTissueOrigin().isSaved()) {
          st.setTissueOrigin(tissueOriginDao.get(st.getTissueOrigin().getId()));
        }
        if (st.getTissueType() != null && st.getTissueType().isSaved()) {
          st.setTissueType(tissueTypeDao.get(st.getTissueType().getId()));
        }
        if (st.getLab() != null && st.getLab().isSaved()) {
          st.setLab(labService.get(st.getLab().getId()));
        }
      }
    }
  }

  private void validateHierarchy(DetailedSample sample) throws IOException {
    if (!isValidRelationship(sample.getParent(), sample)) {
      throw new ValidationException("Parent " + sample.getParent().getSampleClass().getAlias()
          + " not permitted to have a child of type " + sample.getSampleClass().getAlias());
    }
  }

  public boolean isValidRelationship(Sample parent, Sample child) throws IOException {
    if (parent == null && !isDetailedSample(child)) {
      return true; // Simple sample has no relationships.
    }
    if (!isDetailedSample(child) || !isDetailedSample(parent)) {
      return false;
    }

    SampleClass parentClass = ((DetailedSample) parent).getSampleClass();
    SampleClass childClass = ((DetailedSample) child).getSampleClass();
    return sampleValidRelationshipService.getByClasses(parentClass, childClass) != null;
  }

  @Override
  public long update(Sample sample) throws IOException {
    Sample managed = get(sample.getId());
    User changeUser = authorizationManager.getCurrentUser();
    managed.setChangeDetails(changeUser);
    boolean validateAliasUniqueness = !managed.getAlias().equals(sample.getAlias());
    maybeRemoveFromBox(sample, managed);
    boxService.throwIfBoxPositionIsFilled(sample);
    if (sample.getParent() != null) {
      ((DetailedSample) sample).setParent((DetailedSample) get(sample.getParent().getId()));
    }
    LimsUtils.updateParentVolume(sample, managed, changeUser);
    updateParentSlides(sample, managed, changeUser);
    loadChildEntities(sample);
    writeRequisitionChangelogs(sample, managed);
    validateChange(sample, managed);
    applyChanges(managed, sample);
    if (isDetailedSample(managed)) {
      DetailedSample detailedUpdated = (DetailedSample) managed;
      if (detailedUpdated.getParent() != null) {
        detailedUpdated.setParent((DetailedSample) get(detailedUpdated.getParent().getId()));
        validateHierarchy(detailedUpdated);
      }
    }

    save(managed, validateAliasUniqueness);
    if (sample.getParent() != null) {
      sampleStore.update(sample.getParent());
    }
    boxService.updateBoxableLocation(sample);
    return sample.getId();
  }

  private void writeRequisitionChangelogs(Sample sample, Sample beforeChange) throws IOException {
    if (!isChanged(Sample::getRequisition, sample, beforeChange)) {
      return;
    }
    if (beforeChange.getRequisition() != null) {
      addRequisitionSampleChange(beforeChange.getRequisition(), beforeChange, false);
    }
    if (sample.getRequisition() != null) {
      addRequisitionSampleChange(sample.getRequisition(), sample, true);
    }
  }

  private void addRequisitionSampleChange(Requisition requisition, Sample sample, boolean addition) throws IOException {
    String message =
        String.format("%s sample %s (%s)", addition ? "Added" : "Removed", sample.getName(), sample.getAlias());
    ChangeLog change = requisition.createChangeLog(message, "samples", authorizationManager.getCurrentUser());
    changeLogService.create(change);
  }

  private void maybeRemoveFromBox(Sample sample, Sample managed) {
    if (sample.isDiscarded() || sample.getDistributionTransfer() != null || managed.getDistributionTransfer() != null) {
      sample.setBoxPosition(null);
      sample.setVolume(BigDecimal.ZERO);
    }
  }

  private void validateChange(Sample sample, Sample beforeChange) throws IOException {
    updateDetailedQcStatusDetails(sample, beforeChange, authorizationManager);

    List<ValidationError> errors = new ArrayList<>();
    validateConcentrationUnits(sample.getConcentration(), sample.getConcentrationUnits(), errors);
    validateVolume(sample.getInitialVolume(), sample.getVolume(), errors);
    validateVolumeUnits(sample.getVolume(), sample.getVolumeUnits(), errors);
    validateBarcodeUniqueness(sample, beforeChange, barcodableReferenceService, errors);
    validateUnboxableFields(sample, errors);
    validateDetailedQcStatus(sample, errors);

    if (isDetailedSample(sample)) {
      validateSubproject(sample, beforeChange, errors);
      validateReferenceSlide((DetailedSample) sample, errors);
      validateGroupDescription((DetailedSample) sample, errors);
      if (sample.getRequisition() != null && ((DetailedSample) sample).isSynthetic()) {
        errors.add(new ValidationError("requisitionId", "Ghost samples cannot be added to requisitions"));
      }
    }

    if (sample.getCreationReceiptInfo() != null) {
      validateReceiptTransfer(sample.getCreationReceiptInfo(), errors);
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void validateSubproject(Sample sample, Sample beforeChange, List<ValidationError> errors) throws IOException {
    DetailedSample detailed = (DetailedSample) sample;
    if (detailed.getSubproject() != null
        && detailed.getSubproject().getParentProject().getId() != detailed.getProject().getId()) {
      errors.add(new ValidationError("subprojectId", "Subproject does not belong to the selected project"));
    }
  }

  private void validateReceiptTransfer(TransferSample transferSample, List<ValidationError> errors) {
    if (transferSample.getTransfer().getSenderLab() == null) {
      errors.add(new ValidationError("senderLabId", "Receipt transfer must specify a lab"));
    }
    if (transferSample.getTransfer().getRecipientGroup() == null) {
      errors.add(new ValidationError("recipientGroupId", "Receipt transfer must specify a recipient group"));
    }
    if (Boolean.FALSE.equals(transferSample.isQcPassed()) && transferSample.getQcNote() == null) {
      errors.add(new ValidationError("reciptQcNote", "A receipt QC note is required when receipt QC is failed"));
    }
  }

  private void validateReferenceSlide(DetailedSample sample, List<ValidationError> errors) {
    SampleSlide reference = null;
    if (isTissuePieceSample(sample)) {
      reference = ((SampleTissuePiece) sample).getReferenceSlide();
    } else if (isStockSample(sample)) {
      reference = ((SampleStock) deproxify(sample)).getReferenceSlide();
    } else {
      return;
    }
    if (reference != null) {
      Sample tissue = getParent(SampleTissue.class, sample);
      Sample referenceTissue = getParent(SampleTissue.class, reference);
      if (tissue.getId() != referenceTissue.getId()) {
        errors.add(new ValidationError("referenceSlideId", "Reference slide must be derived from the same tissue"));
      }
    }
  }

  /**
   * Copies modifiable fields from the source Sample into the target Sample to be persisted
   * 
   * @param target the persisted Sample to modify
   * @param source the modified Sample to copy modifiable fields from
   * @throws IOException
   */
  private void applyChanges(Sample target, Sample source) throws IOException {
    target.setDescription(source.getDescription());
    target.setSampleType(source.getSampleType());
    target.setScientificName(source.getScientificName());
    target.setTaxonIdentifier(source.getTaxonIdentifier());

    target.setAlias(source.getAlias());
    target.setDescription(source.getDescription());
    target.setDiscarded(source.isDiscarded());
    target.setInitialVolume(source.getInitialVolume());
    target.setVolume(source.getVolume());
    if (target.getVolume() == null) {
      target.setVolumeUnits(null);
    } else if (!target.getVolume().equals(BigDecimal.ZERO) || target.getVolumeUnits() != null) {
      target.setVolumeUnits(source.getVolumeUnits());
    }
    target.setConcentration(source.getConcentration());
    if (target.getConcentration() == null) {
      target.setConcentrationUnits(null);
    } else if (!target.getConcentration().equals(BigDecimal.ZERO) || target.getConcentrationUnits() != null) {
      target.setConcentrationUnits(source.getConcentrationUnits());
    }
    target.setLocationBarcode(source.getLocationBarcode());
    target.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(source.getIdentificationBarcode()));
    target.setLocationBarcode(source.getLocationBarcode());
    target.setRequisition(source.getRequisition());
    target.setSequencingControlType(source.getSequencingControlType());
    target.setSop(source.getSop());
    target.setDetailedQcStatus(source.getDetailedQcStatus());
    target.setDetailedQcStatusNote(nullifyStringIfBlank(source.getDetailedQcStatusNote()));
    target.setQcUser(source.getQcUser());
    target.setQcDate(source.getQcDate());

    if (isDetailedSample(target)) {
      DetailedSample dTarget = (DetailedSample) target;
      DetailedSample dSource = (DetailedSample) source;
      if (source.getProject().isSecondaryNaming() != target.getProject().isSecondaryNaming()) {
        dTarget.setNonStandardAlias(true);
      }
      dTarget.setArchived(dSource.getArchived());
      dTarget.setGroupDescription(dSource.getGroupDescription());
      dTarget.setGroupId(dSource.getGroupId());
      dTarget.setCreationDate(dSource.getCreationDate());

      dTarget.setSubproject(dSource.getSubproject());
      dTarget.setVolumeUsed(dSource.getVolumeUsed());
      dTarget.setNgUsed(dSource.getNgUsed());
      source = deproxify(source);
      target = deproxify(target);
      if (isIdentitySample(target)) {
        applyIdentityChanges((SampleIdentity) target, (SampleIdentity) source);
      } else if (isTissueSample(target)) {
        applyTissueChanges((SampleTissue) target, (SampleTissue) source);
      } else if (isTissueProcessingSample(target)) {
        applyTissueProcessingChanges((SampleTissueProcessing) target, (SampleTissueProcessing) source);
      } else if (isAliquotSample(target)) {
        applyAliquotChanges((SampleAliquot) target, (SampleAliquot) source);
      } else if (isStockSample(target)) {
        applyStockChanges((SampleStock) target, (SampleStock) source);
      }
    }
    target.setProject(source.getProject());
  }

  private void applyIdentityChanges(SampleIdentity target, SampleIdentity source) throws IOException {
    if (!source.getExternalName().equals(target.getExternalName())) {
      confirmExternalNameUniqueForProjectIfRequired(source.getExternalName(), target);
      Set<String> sourceExternalNames = SampleIdentityImpl.getSetFromString(source.getExternalName());
      Set<String> targetExternalNames = SampleIdentityImpl.getSetFromString(target.getExternalName());
      if (!sourceExternalNames.containsAll(targetExternalNames)
          || !targetExternalNames.containsAll(sourceExternalNames)) {
        target.setExternalName(source.getExternalName());
      }
    }
    target.setDonorSex(source.getDonorSex());
    target.setConsentLevel(source.getConsentLevel());
  }

  private void applyAliquotChanges(SampleAliquot target, SampleAliquot source) {
    if (source instanceof SampleAliquotSingleCell) {
      ((SampleAliquotSingleCell) target).setInputIntoLibrary(((SampleAliquotSingleCell) source).getInputIntoLibrary());
    }
    target.setSamplePurpose(source.getSamplePurpose());
  }

  private void applyStockChanges(SampleStock target, SampleStock source) {
    if (source instanceof SampleStockSingleCell) {
      ((SampleStockSingleCell) target).setTargetCellRecovery(((SampleStockSingleCell) source).getTargetCellRecovery());
      ((SampleStockSingleCell) target).setCellViability(((SampleStockSingleCell) source).getCellViability());
      ((SampleStockSingleCell) target)
          .setLoadingCellConcentration(((SampleStockSingleCell) source).getLoadingCellConcentration());
    }
    target.setStrStatus(source.getStrStatus());
    target.setSlidesConsumed(source.getSlidesConsumed());
    target.setReferenceSlide(source.getReferenceSlide());
  }

  private void applyTissueChanges(SampleTissue target, SampleTissue source) {
    target.setPassageNumber(source.getPassageNumber());
    target.setTimesReceived(source.getTimesReceived());
    target.setTubeNumber(source.getTubeNumber());
    target.setSecondaryIdentifier(source.getSecondaryIdentifier());
    target.setRegion(source.getRegion());
    target.setTissueMaterial(source.getTissueMaterial());
    target.setTissueOrigin(source.getTissueOrigin());
    target.setTissueType(source.getTissueType());
    target.setLab(source.getLab());
    target.setTimepoint(source.getTimepoint());
  }

  private void applyTissueProcessingChanges(SampleTissueProcessing target, SampleTissueProcessing source) {
    if (source instanceof SampleSlide) {
      ((SampleSlide) target).setInitialSlides(((SampleSlide) source).getInitialSlides());
      ((SampleSlide) target).setSlides(((SampleSlide) source).getSlides());
      ((SampleSlide) target).setThickness(((SampleSlide) source).getThickness());
      ((SampleSlide) target).setStain(((SampleSlide) source).getStain());
      ((SampleSlide) target).setPercentTumour(((SampleSlide) source).getPercentTumour());
      ((SampleSlide) target).setPercentNecrosis(((SampleSlide) source).getPercentNecrosis());
      ((SampleSlide) target).setMarkedArea(((SampleSlide) source).getMarkedArea());
      ((SampleSlide) target).setMarkedAreaPercentTumour(((SampleSlide) source).getMarkedAreaPercentTumour());
    } else if (source instanceof SampleTissuePiece) {
      ((SampleTissuePiece) target).setSlidesConsumed(((SampleTissuePiece) source).getSlidesConsumed());
      ((SampleTissuePiece) target).setTissuePieceType(((SampleTissuePiece) source).getTissuePieceType());
      ((SampleTissuePiece) target).setReferenceSlide(((SampleTissuePiece) source).getReferenceSlide());
    } else if (source instanceof SampleSingleCell) {
      ((SampleSingleCell) target)
          .setInitialCellConcentration(((SampleSingleCell) source).getInitialCellConcentration());
      ((SampleSingleCell) target).setDigestion(((SampleSingleCell) source).getDigestion());
    }
  }

  /**
   * Returns true if another sample with same external name and project exists in database
   * 
   * @param sample
   * @return boolean
   * @throws IOException
   */
  private boolean isExternalNameDuplicatedInProject(Sample sample) throws IOException {
    SampleIdentity identity = (SampleIdentity) sample;
    return getIdentitiesByExternalNameOrAliasAndProject(identity.getExternalName(), identity.getProject().getId(), true)
        .size() > 0;
  }

  @Override
  public List<Sample> list() throws IOException {
    return sampleStore.list();
  }

  @Override
  public List<Sample> listByIdList(List<Long> idList) throws IOException {
    return sampleStore.listByIdList(idList);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public List<IdentityView> getIdentitiesByExternalNameOrAliasAndProject(String externalName, Long projectId,
      boolean exactMatch)
      throws IOException {
    return sampleStore.getIdentitiesByExternalNameOrAliasAndProject(externalName, projectId, exactMatch);
  }

  @Override
  public List<IdentityView> getIdentities(Collection<String> externalNames, boolean exactMatch, Project project)
      throws IOException {
    return sampleStore.getIdentities(externalNames, exactMatch, project);
  }

  @Override
  public Sample getByBarcode(String barcode) throws IOException {
    return sampleStore.getByBarcode(barcode);
  }

  @Override
  public Sample getByLibraryAliquotId(long aliquotId) throws IOException {
    return sampleStore.getByLibraryAliquotId(aliquotId);
  }

  @Override
  public void addNote(Sample sample, Note note) throws IOException {
    Sample managed = sampleStore.get(sample.getId());
    note.setCreationDate(LocalDate.now(ZoneId.systemDefault()));
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    sampleStore.update(managed);
  }

  @Override
  public void deleteNote(Sample sample, Long noteId) throws IOException {
    if (noteId == null) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Sample managed = sampleStore.get(sample.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getId() == noteId.longValue()) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Sample " + sample.getId());
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    sampleStore.update(managed);
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  public void setDeletionStore(DeletionStore deletionStore) {
    this.deletionStore = deletionStore;
  }

  @Override
  public void authorizeDeletion(Sample object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public ValidationResult validateDeletion(Sample object) throws IOException {
    ValidationResult result = new ValidationResult();

    if (isDetailedSample(object)) {
      long childCount = sampleStore.getChildSampleCount(object);
      if (childCount > 0L) {
        result.addError(new ValidationError(
            object.getName() + " has " + childCount + " child sample" + (childCount > 1 ? "s" : "")));
      }
    }
    final int libraries = libraryService.listBySampleId(object.getId()).size();
    if (libraries > 0) {
      result.addError(ValidationError.forDeletionUsage(object, libraries, Pluralizer.libraries(libraries)));
    }
    return result;
  }

  @Override
  public void beforeDelete(Sample object) throws IOException {
    List<Workset> worksets = worksetService.listBySample(object.getId());
    for (Workset workset : worksets) {
      worksetService.removeSamples(workset, Collections.singleton(object));
    }
    Box box = object.getBox();
    if (box != null) {
      box.getBoxPositions().remove(object.getBoxPosition());
      boxService.save(box);
    }
    fileAttachmentService.beforeDelete(object);
  }

  @Override
  public void afterDelete(Sample object) throws IOException {
    fileAttachmentService.afterDelete(object);
  }

  @Override
  public Sample save(Sample sample) throws IOException {
    if (!sample.isSaved()) {
      return get(create(sample));
    } else {
      update(sample);
      return get(sample.getId());
    }
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return sampleStore.count(errorHandler, filter);
  }

  @Override
  public List<Sample> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return sampleStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public EntityReference getNextInProject(Sample sample) {
    return sampleStore.getNextInProject(sample);
  }

  @Override
  public EntityReference getPreviousInProject(Sample sample) {
    return sampleStore.getPreviousInProject(sample);
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<Sample> getChildren(Collection<Long> parentIds, String targetSampleCategory) throws IOException {
    return sampleStore.getChildren(parentIds, targetSampleCategory);
  }

  @Override
  public Long getLockProjectId(Sample item) throws IOException {
    return item.getProject() == null ? null : item.getProject().getId();
  }

}
