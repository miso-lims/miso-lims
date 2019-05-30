package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl.IdentityBuilder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.core.store.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.TaxonomyUtils;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.StainService;
import uk.ac.bbsrc.tgac.miso.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleService implements SampleService, PaginatedDataSource<Sample> {

  private static final Logger log = LoggerFactory.getLogger(DefaultSampleService.class);

  private static final String ERR_MISSING_PARENT_ID = "Detailed sample is missing parent identifier";

  public static boolean isValidRelationship(Iterable<SampleValidRelationship> relations, Sample parent, Sample child) {
    if (parent == null && !isDetailedSample(child)) {
      return true; // Simple sample has no relationships.
    }
    if (!isDetailedSample(child) || !isDetailedSample(parent)) {
      return false;
    }
    return isValidRelationship(
        relations,
        ((DetailedSample) parent).getSampleClass(),
        ((DetailedSample) child).getSampleClass());
  }

  private static boolean isValidRelationship(Iterable<SampleValidRelationship> relations, SampleClass parent, SampleClass child) {
    for (SampleValidRelationship relation : relations) {
      if (relation.getParent().getId() == parent.getId() && relation.getChild().getId() == child.getId()) {
        return true;
      }
    }
    return false;
  }

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
  private DetailedQcStatusDao detailedQcStatusDao;
  @Autowired
  private SubprojectDao subProjectDao;
  @Autowired
  private SamplePurposeDao samplePurposeDao;
  @Autowired
  private TissueMaterialDao tissueMaterialDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private LabService labService;
  @Autowired
  private StainService stainService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private WorksetService worksetService;
  @Autowired
  private FileAttachmentService fileAttachmentService;

  @Autowired
  private NamingScheme namingScheme;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Value("${miso.taxonLookup.enabled:false}")
  private boolean taxonLookupEnabled;

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

  public void setDetailedQcStatusDao(DetailedQcStatusDao detailedQcStatusDao) {
    this.detailedQcStatusDao = detailedQcStatusDao;
  }

  public void setSubProjectDao(SubprojectDao subProjectDao) {
    this.subProjectDao = subProjectDao;
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

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
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
    loadChildEntities(sample);
    boxService.throwIfBoxPositionIsFilled(sample);
    sample.setChangeDetails(authorizationManager.getCurrentUser());
    if (isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      if (!isIdentitySample(sample)) {
        // Create a copy of the incoming sample's identity
        SampleIdentity identityCopy = getIdentity(detailed);
        if (detailed.getParent() == null) {
          throw new IllegalArgumentException(ERR_MISSING_PARENT_ID);
        }
        if (detailed.getParent().getId() != SampleImpl.UNSAVED_ID) {
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
          throw new IllegalArgumentException("Sample with external name '" + ((SampleIdentity) sample).getExternalName()
              + "' already exists in project " + sample.getProject().getShortName());
        }
      }
    }

    // pre-save field generation
    sample.setName(generateTemporaryName());
    if (isStringEmptyOrNull(sample.getAlias()) && namingScheme.hasSampleAliasGenerator()) {
      sample.setAlias(generateTemporaryName());
    }
    if (sample.getConcentration() == null) {
      sample.setConcentrationUnits(null);
    }
    if (sample.getVolume() == null) {
      sample.setVolumeUnits(null);
    }
    validateChange(sample, null);
    long savedId = save(sample, true).getId();
    boxService.updateBoxableLocation(sample);
    return savedId;
  }

  /**
   * Saves the Sample as is to the database. Fields which should be autogenerated but first require the primary key are then generated, and
   * the changes are saved
   * 
   * @param sample the Sample to save
   * @return the same Sample provided, including fields that were generated by this method
   * @throws IOException
   */
  private Sample save(Sample sample, boolean validateAliasUniqueness) throws IOException {
    if (sample instanceof DetailedSample && ((DetailedSample) sample).getDetailedQcStatus() != null) {
      ((DetailedSample) sample).setQcPassed(((DetailedSample) sample).getDetailedQcStatus().getStatus());
    }
    try {
      Long newId = sample.getId();
      if (!hasTemporaryAlias(sample)) {
        validateAlias(sample);
      }
      if (newId == Sample.UNSAVED_ID) {
        newId = sampleStore.addSample(sample);
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
          validateAlias(created);
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
        validateAliasUniqueness(created);
      }
      return created;
    } catch (ConstraintViolationException e) {
      // Send the nested root cause message to the user, since it contains the actual error.
      throw new ConstraintViolationException(e.getMessage() + " " + ExceptionUtils.getRootCauseMessage(e), e.getSQLException(),
          e.getConstraintName());
    }
  }

  /**
   * Checks whether sample's alias conforms to the naming scheme. Validation is skipped for DetailedSamples
   * {@code if (sample.hasNonStandardAlias())}
   * 
   * @param sample
   */
  private void validateAlias(Sample sample) {
    if (!isDetailedSample(sample) || !((DetailedSample) sample).hasNonStandardAlias()) {
      uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult aliasValidation = namingScheme.validateSampleAlias(sample
          .getAlias());
      if (!aliasValidation.isValid()) {
        throw new ValidationException(new ValidationError("alias", aliasValidation.getMessage()));
      }
    }
  }

  /**
   * Checks whether the configured naming scheme allows duplicate alias. If not, checks whether an alias is used by multiple samples.
   * This method should be called <b>after</b> saving a new Sample. DetailedSamples marked as having non-standard alias are also
   * considered valid
   * 
   * @param alias the alias to validate
   * @throws ConstraintViolationException if duplicate alias are <b>not</b> allowed, <b>and</b> the Sample does not have nonStandardAlias
   *           <b>and</b> the alias is used by multiple Samples
   * @throws IOException
   */
  private void validateAliasUniqueness(Sample sample) throws IOException {
    if (isDetailedSample(sample) && ((DetailedSample) sample).hasNonStandardAlias()) {
      return;
    }
    if (!namingScheme.duplicateSampleAliasAllowed() && sampleStore.listByAlias(sample.getAlias()).size() > 1) {
      throw new ValidationException(new ValidationError("alias", String.format("A sample with alias '%s' already exists",
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
    if (identityCopy == null || identityCopy.getExternalName() == null) return;
    SampleIdentity identity = (SampleIdentity) get(getIdentity(sample).getId());
    Set<String> identityExternalNames = SampleIdentityImpl.getSetFromString(identity.getExternalName());
    Set<String> tempExternalNames = SampleIdentityImpl.getSetFromString(identityCopy.getExternalName());
    Set<String> lowerCaseIdentityExternalNames = identityExternalNames.stream().map(String::toLowerCase).collect(Collectors.toSet());

    for (String name : tempExternalNames) {
      if (!lowerCaseIdentityExternalNames.contains(name.toLowerCase()) && !(isUniqueExternalNameWithinProjectRequired() &&
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
   * Checks whether the given external name(s) (may be multiple comma-separated names) is required to be unique within a project,
   * then if it actually is unique within a project.
   * This method should be called <b>before</b> saving an Identity.
   * 
   * @param newExternalName
   *          the String to validate
   * @param project
   *          the project that will be associated with the Identity
   * @throws ConstraintViolationException
   *           if the external name is already used in this project
   * @throws IOException
   */
  @Override
  public void confirmExternalNameUniqueForProjectIfRequired(String newExternalName, Sample sample)
      throws IOException, ConstraintViolationException {
    if (!isUniqueExternalNameWithinProjectRequired()) return;
    Collection<SampleIdentity> matches = getIdentitiesByExternalNameOrAliasAndProject(newExternalName,
        sample.getProject().getId(), true);
    if (!matches.isEmpty()) {
      for (SampleIdentity match : matches) {
        if (match.getId() != sample.getId()) {
          Set<String> matchExtNames = SampleIdentityImpl.getSetFromString(match.getExternalName());
          Set<String> newExtNames = SampleIdentityImpl.getSetFromString(newExternalName);
          newExtNames.retainAll(matchExtNames);
          throw new ConstraintViolationException(
              "Duplicate external names not allowed within a project: External name \"" + String.join(",", newExtNames)
                  + "\" is already associated with Identity " + match.getAlias() + " (" + match.getExternalName() + ")",
              null, "externalName");
        }
      }
    }
  }

  /**
   * Finds an existing parent Sample or creates a new one if necessary
   * 
   * @param sample must contain parent (via {@link DetailedSample#getParent() getParent}), including externalName if a new parent is
   *          to be created. An existing parent may be specified by including its sampleId or externalName
   * 
   * @return
   * @throws IOException
   * @throws SQLException
   * @throws MisoNamingException
   */
  private DetailedSample findOrCreateParent(DetailedSample sample) throws IOException, MisoNamingException, ConstraintViolationException {
    if (sample.getParent() == null) {
      throw new IllegalArgumentException(ERR_MISSING_PARENT_ID);
    }
    DetailedSample tempParent = sample.getParent();
    if (tempParent.getId() != Sample.UNSAVED_ID) {
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
    parent.setSampleType(child.getSampleType());
    parent.setScientificName(child.getScientificName());
    parent.setVolume(0D);
    parent.setVolumeUnits(VolumeUnit.MICROLITRES);
    parent.setSynthetic(true);
    if (child.getIdentityId() != null) parent.setIdentityId(child.getIdentityId());
    if (isLcmTubeSample(child)) {
      SampleSlide parentSlides = (SampleSlide) parent;
      Integer slides = parentSlides.getSlides() == null ? 0 : parentSlides.getSlides();
      slides += ((SampleLCMTube) child).getSlidesConsumed();
      parentSlides.setSlides(slides);
      parentSlides.setDiscards(0);
      if (parentSlides.getId() != SampleImpl.UNSAVED_ID) {
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

  private SampleIdentity findOrCreateIdentity(DetailedSample descendant, SampleIdentity identity) throws IOException, MisoNamingException {
    if (identity.getId() != SampleImpl.UNSAVED_ID) {
      return (SampleIdentity) sampleStore.getSample(identity.getId());
    } else {
      // If samples are being bulk received for the same new donor, they will all have a null parentId.
      // After the new donor's Identity is created, the following samples need to be parented to that now-existing Identity.
      Collection<SampleIdentity> newlyCreated = getIdentitiesByExternalNameOrAliasAndProject(identity.getExternalName(),
          descendant.getProject().getId(), true);
      if (newlyCreated.size() > 1) {
        throw new IllegalArgumentException(
            "IdentityId is required since there are multiple identities with external name "
                + identity.getExternalName()
                + " in project " + descendant.getProject().getId());
      } else if (newlyCreated.size() == 1) {
        Sample parent = newlyCreated.iterator().next();
        if (parent == null) throw new IllegalArgumentException("Parent sample does not exist");
        else return (SampleIdentity) parent;
      } else {
        try {
          return createParentIdentity(descendant, identity);
        } catch (SQLException e) {
          throw new IOException(e);
        }
      }
    }
  }

  private SampleIdentity createParentIdentity(DetailedSample sample, SampleIdentity identity) throws IOException, MisoNamingException,
      SQLException {
    log.debug("Creating a new Identity to use as a parent.");
    List<SampleClass> identityClasses = sampleClassService.listByCategory(SampleIdentity.CATEGORY_NAME);
    if (identityClasses.size() != 1) {
      throw new IllegalStateException("Found more or less than one SampleClass of category " + SampleIdentity.CATEGORY_NAME
          + ". Cannot choose which to use as root sample class.");
    }
    SampleClass rootSampleClass = identityClasses.get(0);
    confirmExternalNameUniqueForProjectIfRequired(identity.getExternalName(), sample);

    Sample identitySample = new IdentityBuilder().project(sample.getProject())
        .sampleType(sample.getSampleType()).scientificName(sample.getScientificName()).name(generateTemporaryName())
        .rootSampleClass(rootSampleClass).volume(0D).externalName(identity.getExternalName())
        .donorSex(identity.getDonorSex()).consentLevel(identity.getConsentLevel()).build();
    identitySample.setAlias(namingScheme.generateSampleAlias(identitySample));

    identitySample.setChangeDetails(authorizationManager.getCurrentUser());
    return (SampleIdentity) save(identitySample, true);
  }

  /**
   * Loads persisted objects into sample fields. Should be called before saving new samples. Loads all member objects <b>except</b>
   * <ul>
   * <li>parent sample for detailed samples</li>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param sample the Sample to load entities into. Must contain at least the IDs of objects to load (e.g. to load the persisted Project
   *          into sample.project, sample.project.id must be set)
   * @throws IOException
   */
  private void loadChildEntities(Sample sample) throws IOException {
    if (sample.getProject() != null) {
      sample.setProject(projectStore.get(sample.getProject().getId()));
    }
    if (isDetailedSample(sample)) {
      DetailedSample sai = (DetailedSample) sample;
      if (sai.getSampleClass() != null && sai.getSampleClass().getId() != null) {
        sai.setSampleClass(sampleClassService.get(sai.getSampleClass().getId()));
      }
      if (sai.getDetailedQcStatus() != null && sai.getDetailedQcStatus().isSaved()) {
        sai.setDetailedQcStatus(detailedQcStatusDao.get(sai.getDetailedQcStatus().getId()));
      }
      if (sai.getSubproject() != null && sai.getSubproject().isSaved()) {
        sai.setSubproject(subProjectDao.getSubproject(sai.getSubproject().getId()));
      }
      if (isTissueProcessingSample(sai) && sai instanceof SampleSlide) {
        Stain originalStain = ((SampleSlide) sai).getStain();
        Stain stain;
        if (originalStain == null) {
          stain = null;
        } else {
          stain = stainService.get(originalStain.getId());
        }
        ((SampleSlide) sai).setStain(stain);
      }
      if (isAliquotSample(sai)) {
        SampleAliquot sa = (SampleAliquot) sai;
        if (sa.getSamplePurpose() != null && sa.getSamplePurpose().isSaved()) {
          sa.setSamplePurpose(samplePurposeDao.getSamplePurpose(sa.getSamplePurpose().getId()));
        }
      }
      if (isTissueSample(sai)) {
        SampleTissue st = (SampleTissue) sai;
        if (st.getTissueMaterial() != null && st.getTissueMaterial().isSaved()) {
          st.setTissueMaterial(tissueMaterialDao.getTissueMaterial(st.getTissueMaterial().getId()));
        }
        if (st.getTissueOrigin() != null && st.getTissueOrigin().isSaved()) {
          st.setTissueOrigin(tissueOriginDao.getTissueOrigin(st.getTissueOrigin().getId()));
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
    Set<SampleValidRelationship> sampleValidRelationships = sampleValidRelationshipService.getAll();
    if (!isValidRelationship(sampleValidRelationships, sample.getParent(), sample)) {
      throw new IllegalArgumentException("Parent " + sample.getParent().getSampleClass().getAlias()
          + " not permitted to have a child of type " + sample.getSampleClass().getAlias());
    }
  }

  @Override
  public long update(Sample sample) throws IOException {
    Sample managed = get(sample.getId());
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    boolean validateAliasUniqueness = !managed.getAlias().equals(sample.getAlias());
    maybeRemoveFromBox(sample);
    boxService.throwIfBoxPositionIsFilled(sample);
    validateChange(sample, managed);
    applyChanges(managed, sample);
    loadChildEntities(managed);
    if (isDetailedSample(managed)) {
      DetailedSample detailedUpdated = (DetailedSample) managed;
      if (detailedUpdated.getParent() != null) {
        detailedUpdated.setParent((DetailedSample) get(detailedUpdated.getParent().getId()));
        validateHierarchy(detailedUpdated);
      }
    }

    save(managed, validateAliasUniqueness);
    boxService.updateBoxableLocation(sample);
    return sample.getId();
  }

  private void maybeRemoveFromBox(Sample sample) {
    if (sample.isDiscarded() || sample.isDistributed()) {
      sample.setBoxPosition(null);
    }
  }

  private void validateChange(Sample sample, Sample beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    validateConcentrationUnits(sample.getConcentration(), sample.getConcentrationUnits(), errors);
    validateVolumeUnits(sample.getVolume(), sample.getVolumeUnits(), errors);
    validateBarcodeUniqueness(sample, beforeChange, sampleStore::getByBarcode, errors, "sample");
    validateDistributionFields(sample.isDistributed(), sample.getDistributionDate(), sample.getDistributionRecipient(), sample.getBox(),
        errors);
    validateUnboxableFields(sample.isDiscarded(), sample.isDistributed(), sample.getBox(), errors);

    if (taxonLookupEnabled && (beforeChange == null || !sample.getScientificName().equals(beforeChange.getScientificName()))
        && (sample.getScientificName() == null || TaxonomyUtils.checkScientificNameAtNCBI(sample.getScientificName()) == null)) {
      errors.add(new ValidationError("scientificName", "This scientific name is not of a known taxonomy"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
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
    target.setReceivedDate(source.getReceivedDate());
    target.setScientificName(source.getScientificName());
    target.setTaxonIdentifier(source.getTaxonIdentifier());

    // validate alias uniqueness only if the alias has changed and the sample does not have a non-standard alias
    target.setAlias(source.getAlias());
    target.setDescription(source.getDescription());
    target.setDiscarded(source.isDiscarded());
    if (source.isDiscarded() || source.isDistributed()) {
      target.setVolume(0.0);
    } else {
      target.setVolume(source.getVolume());
    }
    if (target.getVolume() == null) {
      target.setVolumeUnits(null);
    } else if (!target.getVolume().equals(Double.valueOf(0.0)) || target.getVolumeUnits() != null) {
      target.setVolumeUnits(source.getVolumeUnits());
    }
    target.setConcentration(source.getConcentration());
    if (target.getConcentration() == null) {
      target.setConcentrationUnits(null);
    } else if (!target.getConcentration().equals(Double.valueOf(0.0)) || target.getConcentrationUnits() != null) {
      target.setConcentrationUnits(source.getConcentrationUnits());
    }
    target.setLocationBarcode(source.getLocationBarcode());
    target.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(source.getIdentificationBarcode()));
    target.setDistributed(source.isDistributed());
    target.setDistributionDate(source.getDistributionDate());
    target.setDistributionRecipient(source.getDistributionRecipient());
    target.setLocationBarcode(source.getLocationBarcode());

    if (isDetailedSample(target)) {
      DetailedSample dTarget = (DetailedSample) target;
      DetailedSample dSource = (DetailedSample) source;
      dTarget.setArchived(dSource.getArchived());
      dTarget.setGroupDescription(dSource.getGroupDescription());
      dTarget.setGroupId(dSource.getGroupId());
      dTarget.setCreationDate(dSource.getCreationDate());

      dTarget.setDetailedQcStatus(dSource.getDetailedQcStatus());
      dTarget.setDetailedQcStatusNote(nullifyStringIfBlank(dSource.getDetailedQcStatusNote()));
      dTarget.setQcPassed(dSource.getQcPassed());
      dTarget.setSubproject(dSource.getSubproject());
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
    } else {
      target.setQcPassed(source.getQcPassed());
    }
  }

  private void applyIdentityChanges(SampleIdentity target, SampleIdentity source) throws IOException {
    if (!source.getExternalName().equals(target.getExternalName())) {
      confirmExternalNameUniqueForProjectIfRequired(source.getExternalName(), target);
      Set<String> sourceExternalNames = SampleIdentityImpl.getSetFromString(source.getExternalName());
      Set<String> targetExternalNames = SampleIdentityImpl.getSetFromString(target.getExternalName());
      if (!sourceExternalNames.containsAll(targetExternalNames) || !targetExternalNames.containsAll(sourceExternalNames)) {
        target.setExternalName(source.getExternalName());
      }
    }
    target.setDonorSex(source.getDonorSex());
    target.setConsentLevel(source.getConsentLevel());
  }

  private void applyAliquotChanges(SampleAliquot target, SampleAliquot source) {
    source = deproxify(source);
    if (source instanceof SampleAliquotSingleCell) {
      ((SampleAliquotSingleCell) target).setInputIntoLibrary(((SampleAliquotSingleCell) source).getInputIntoLibrary());
    }
    target.setSamplePurpose(source.getSamplePurpose());
  }

  private void applyStockChanges(SampleStock target, SampleStock source) {
    source = deproxify(source);
    if (source instanceof SampleStockSingleCell) {
      ((SampleStockSingleCell) target).setTargetCellRecovery(((SampleStockSingleCell) source).getTargetCellRecovery());
      ((SampleStockSingleCell) target).setCellViability(((SampleStockSingleCell) source).getCellViability());
      ((SampleStockSingleCell) target).setLoadingCellConcentration(((SampleStockSingleCell) source).getLoadingCellConcentration());
    }
    target.setStrStatus(source.getStrStatus());
    target.setDNAseTreated(source.getDNAseTreated());
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
  }

  private void applyTissueProcessingChanges(SampleTissueProcessing target, SampleTissueProcessing source) {
    source = deproxify(source);
    if (source instanceof SampleSlide) {
      ((SampleSlide) target).setSlides(((SampleSlide) source).getSlides());
      ((SampleSlide) target).setDiscards(((SampleSlide) source).getDiscards());
      ((SampleSlide) target).setThickness(((SampleSlide) source).getThickness());
      ((SampleSlide) target).setStain(((SampleSlide) source).getStain());
    } else if (source instanceof SampleLCMTube) {
      ((SampleLCMTube) target).setSlidesConsumed(((SampleLCMTube) source).getSlidesConsumed());
    } else if (source instanceof SampleSingleCell) {
      ((SampleSingleCell) target).setInitialCellConcentration(((SampleSingleCell) source).getInitialCellConcentration());
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
    return getIdentitiesByExternalNameOrAliasAndProject(identity.getExternalName(), identity.getProject().getId(), true).size() > 0;
  }

  @Override
  public List<Sample> list() throws IOException {
    return sampleStore.list();
  }

  @Override
  public Long countAll() throws IOException {
    return sampleStore.countAll();
  }

  @Override
  public Collection<Sample> listByProjectId(long projectId) throws IOException {
    return sampleStore.listByProjectId(projectId);
  }

  @Override
  public Collection<Sample> listByIdList(List<Long> idList) throws IOException {
    return sampleStore.getByIdList(idList);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Collection<SampleIdentity> getIdentitiesByExternalNameOrAliasAndProject(String externalName, Long projectId, boolean exactMatch)
      throws IOException {
    return sampleStore.getIdentitiesByExternalNameOrAliasAndProject(externalName, projectId, exactMatch);
  }

  @Override
  public List<Sample> getByAlias(String alias) throws IOException {
    return new ArrayList<>(sampleStore.listByAlias(alias));
  }

  @Override
  public Sample getByBarcode(String barcode) throws IOException {
    return sampleStore.getByBarcode(barcode);
  }

  @Override
  public void addNote(Sample sample, Note note) throws IOException {
    Sample managed = sampleStore.get(sample.getId());
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    sampleStore.save(managed);
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
    sampleStore.save(managed);
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
  public ValidationResult validateDeletion(Sample object) {
    ValidationResult result = new ValidationResult();

    if (isDetailedSample(object)) {
      long childCount = sampleStore.getChildSampleCount(object);
      if (childCount > 0L) {
        result.addError(new ValidationError(object.getName() + " has " + childCount + " child sample" + (childCount > 1 ? "s" : "")));
      }
    }
    if (object.getLibraries() != null && !object.getLibraries().isEmpty()) {
      result.addError(new ValidationError(object.getName() + " has " + object.getLibraries().size() + " librar"
          + (object.getLibraries().size() > 1 ? "ies" : "y")));
    }

    return result;
  }

  @Override
  public void beforeDelete(Sample object) throws IOException {
    List<Workset> worksets = worksetService.listBySample(object.getId());
    for (Workset workset : worksets) {
      workset.getSamples().removeIf(sam -> sam.getId() == object.getId());
      worksetService.update(workset);
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
    if (sample.getId() == SampleImpl.UNSAVED_ID) {
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

}
