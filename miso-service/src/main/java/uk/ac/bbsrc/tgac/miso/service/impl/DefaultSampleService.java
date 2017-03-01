package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleCVSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl.IdentityBuilder;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleService implements SampleService {

  private static final Logger log = LoggerFactory.getLogger(DefaultSampleService.class);

  @Autowired
  private SampleDao sampleDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;

  @Autowired
  private SampleNumberPerProjectService sampleNumberPerProjectService;

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
  private LabService labService;

  @Autowired
  private NamingScheme namingScheme;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  private Boolean uniqueExternalNameWithinProjectRequired = true;

  public void setSampleDao(SampleDao sampleDao) {
    this.sampleDao = sampleDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setSampleClassDao(SampleClassDao sampleClassDao) {
    this.sampleClassDao = sampleClassDao;
  }

  public void setSampleValidRelationshipService(SampleValidRelationshipService sampleValidRelationshipService) {
    this.sampleValidRelationshipService = sampleValidRelationshipService;
  }

  public void setSampleNumberPerProjectService(SampleNumberPerProjectService sampleNumberPerProjectService) {
    this.sampleNumberPerProjectService = sampleNumberPerProjectService;
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
  public Sample get(Long sampleId) throws IOException {
    Sample sample = sampleDao.getSample(sampleId);
    authorizationManager.throwIfNotReadable(sample);
    return sample;
  }

  @Override
  public Long create(Sample sample) throws IOException {
    loadChildEntities(sample);
    authorizationManager.throwIfNotWritable(sample);
    setChangeDetails(sample);
    if (isDetailedSample(sample)) {
      if (!isIdentitySample(sample)) {
        DetailedSample detailed = (DetailedSample) sample;
        try {
          detailed.setParent(findOrCreateParent(detailed));
          detailed.inheritPermissions(detailed.getParent());
          if (detailed.getParent().hasNonStandardAlias()) {
            detailed.setNonStandardAlias(true);
          }
        } catch (MisoNamingException e) {
          throw new IOException(e.getMessage(), e);
        }
        validateHierarchy(detailed);
      }
    } else {
      sample.setProject(projectStore.get(sample.getProject().getId()));
      sample.inheritPermissions(sample.getProject());
    }

    // pre-save field generation
    sample.setName(generateTemporaryName());
    if (isDetailedSample(sample) && ((DetailedSample) sample).hasNonStandardAlias()) {
      // do not validate alias
    } else if (isStringEmptyOrNull(sample.getAlias()) && namingScheme.hasSampleAliasGenerator()) {
      sample.setAlias(generateTemporaryName());
    } else {
      validateAliasUniqueness(sample.getAlias());
    }
    return save(sample).getId();
  }

  /**
   * Saves the Sample as is to the database. Fields which should be autogenerated but first require the primary key are then generated, and
   * the changes are saved
   * 
   * @param sample the Sample to save
   * @return the same Sample provided, including fields that were generated by this method
   * @throws IOException
   */
  private Sample save(Sample sample) throws IOException {
    if (sample.isDiscarded()) {
      sample.setVolume(0.0);
    }
    try {
      Long newId = sample.getId();
      if (!hasTemporaryAlias(sample)) {
        validateAlias(sample);
      }
      if (newId == Sample.UNSAVED_ID) {
        newId = sampleDao.addSample(sample);
      } else {
        sampleDao.update(sample);
      }
      Sample created = sampleDao.getSample(newId);

      // post-save field generation
      boolean needsUpdate = false;
      if (hasTemporaryName(sample)) {
        created.setName(namingScheme.generateNameFor(created));
        validateNameOrThrow(created, namingScheme);
        needsUpdate = true;
      }
      if (hasTemporaryAlias(sample)) {
        String generatedAlias = namingScheme.generateSampleAlias(created);
        validateAliasUniqueness(generatedAlias);
        created.setAlias(generatedAlias);
        validateAlias(created);
        needsUpdate = true;
      }
      if (autoGenerateIdBarcodes && isStringEmptyOrNull(created.getIdentificationBarcode())) {
        // if !autoGenerateIdBarcodes then the identificationBarcode is set by the user
        generateAndSetIdBarcode(created);
        needsUpdate = true;
      }
      if (needsUpdate) {
        sampleDao.update(created);
      }

      return created;
    } catch (MisoNamingException e) {
      throw new IllegalArgumentException("Name generator failed to generate a valid name", e);
    } catch (ConstraintViolationException e) {
      // Send the nested root cause message to the user, since it contains the actual error.
      throw new ConstraintViolationException(e.getMessage() + " " + ExceptionUtils.getRootCauseMessage(e), e.getSQLException(),
          e.getConstraintName());
    } catch (SQLException e) {
      throw new IOException(e);
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
      ValidationResult aliasValidation = namingScheme.validateSampleAlias(sample.getAlias());
      if (!aliasValidation.isValid()) {
        throw new IllegalArgumentException("Invalid sample alias: '" + sample.getAlias() + "' - " + aliasValidation.getMessage());
      }
    }
  }

  /**
   * Checks whether the configured naming scheme allows duplicate alias. If not, checks to see whether an alias is already in use, in order
   * to prevent duplicates. This method should be called <b>before</b> saving a new Sample
   * 
   * @param alias the alias to validate
   * @throws ConstraintViolationException if duplicate alias are <b>not</b> allowed <b>and</b> the alias is already in use
   * @throws IOException
   */
  private void validateAliasUniqueness(String alias) throws ConstraintViolationException, IOException {
    if (!namingScheme.duplicateSampleAliasAllowed() && sampleDao.aliasExists(alias)) {
      throw new ConstraintViolationException(String.format("A sample with this alias '%s' already exists in the database", alias), null,
          "alias");
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
    for (Identity existingIdentity : getIdentitiesByExternalNameOrAlias(newExternalName)) {
        // not an issue if it matches an identity from another project
      if (existingIdentity.getProject().getId() != sample.getProject().getId()) continue;
      Set<String> intersection = new HashSet<>(IdentityImpl.getSetFromString(newExternalName));
      intersection.retainAll(IdentityImpl.getSetFromString(existingIdentity.getExternalName()));
      if (intersection.size() != 0) {
        throw new ConstraintViolationException("Duplicate external names not allowed within a project: External name " + newExternalName
            + " is already associated with Identity " + existingIdentity.getAlias() + " (" + existingIdentity.getExternalName() + ")",
            null, "externalName");
      }
    }
  }

  /**
   * Finds an existing parent Sample or creates a new one if necessary
   * 
   * @param sample must contain parent (via {@link DetailedSample#getParent() getParent}), including externalName if a new parent is
   * to be created. An existing parent may be specified by including its sampleId or externalName
   * 
   * @return
   * @throws IOException
   * @throws SQLException
   * @throws MisoNamingException
   */
  private DetailedSample findOrCreateParent(DetailedSample sample) throws IOException, MisoNamingException, ConstraintViolationException {
    if (sample.getParent() == null) {
      throw new IllegalArgumentException("Detailed sample is missing parent identifier");
    }
    Sample tempParent = sample.getParent();
    if (tempParent.getId() != Sample.UNSAVED_ID) {
      Sample parent = sampleDao.getSample(tempParent.getId());
      if (parent == null)
        throw new IllegalArgumentException("Parent sample does not exist");
      else
        return (DetailedSample) parent;
    } else if (isIdentitySample(tempParent)) {
      if (sample.getIdentityId() != null) {
        return (DetailedSample) sampleDao.getSample(sample.getIdentityId());
      } else {
        // If samples are being bulk received for the same new donor, they will all have a null parentId.
        // After the new donor's Identity is created, the following samples need to be parented to that now-existing Identity.
        Collection<Identity> newlyCreated = getIdentitiesByExternalNameAndProject(((Identity) tempParent).getExternalName(),
            sample.getProject().getId());
        if (newlyCreated.size() > 1) {
          throw new IllegalArgumentException(
              "IdentityId is required since there are multiple identities with external name " + ((Identity) tempParent).getExternalName()
                  + " in project " + sample.getProject().getId());
        } else if (newlyCreated.size() == 1) {
          Sample parent = newlyCreated.iterator().next();
          if (parent == null)
            throw new IllegalArgumentException("Parent sample does not exist");
          else
            return (DetailedSample) parent;
        } else {
          try {
            return createParentIdentity(sample);
          } catch (SQLException e) {
            throw new IOException(e);
          }
        }
      }
    } else if (isTissueSample(tempParent)) {
      return createParentTissue((SampleTissue) tempParent, sample);
    } else if (isStockSample(tempParent)) {
      return createParentStock((SampleStock) tempParent, sample);
    }
    throw new IllegalArgumentException("Could not resolve parent sample");
  }

  private SampleTissue createParentTissue(SampleTissue tissue, DetailedSample child) throws IOException {
    log.debug("Creating a new Tissue to use as a parent.");
    tissue.setProject(child.getProject());
    tissue.setSampleType(child.getSampleType());
    tissue.setScientificName(child.getScientificName());
    tissue.setVolume(0D);
    tissue.setSynthetic(true);
    if (child.getIdentityId() != null) tissue.setIdentityId(child.getIdentityId());
    create(tissue);
    return tissue;
  }

  private SampleStock createParentStock(SampleStock stock, DetailedSample child) throws IOException {
    log.debug("Creating a new Stock to use as a parent.");
    stock.setProject(child.getProject());
    stock.setSampleType(child.getSampleType());
    stock.setScientificName(child.getScientificName());
    stock.setVolume(0D);
    stock.setSynthetic(true);
    if (child.getIdentityId() != null) stock.setIdentityId(child.getIdentityId());
    create(stock);
    return stock;
  }

  private Identity createParentIdentity(DetailedSample sample) throws IOException, MisoNamingException, SQLException {
    log.debug("Creating a new Identity to use as a parent.");
    List<SampleClass> identityClasses = sampleClassDao.listByCategory(Identity.CATEGORY_NAME);
    if (identityClasses.size() != 1) {
      throw new IllegalStateException("Found more or less than one SampleClass of category " + Identity.CATEGORY_NAME
          + ". Cannot choose which to use as root sample class.");
    }
    SampleClass rootSampleClass = identityClasses.get(0);

    String number = sampleNumberPerProjectService.nextNumber(sample.getProject());
    // Cannot generate identity alias via sampleNameGenerator because of dependence on SampleNumberPerProjectService
    if (sample.getProject().getShortName() == null) {
      throw new NullPointerException("Project shortname required to generate Identity alias");
    }
    String internalName = sample.getProject().getShortName() + "_" + number;
    Identity shellParent = (Identity) sample.getParent();

    confirmExternalNameUniqueForProjectIfRequired(shellParent.getExternalName(), sample);

    Sample identitySample = new IdentityBuilder().project(sample.getProject())
        .sampleType(sample.getSampleType()).scientificName(sample.getScientificName()).name(generateTemporaryName())
        .alias(internalName).rootSampleClass(rootSampleClass).volume(0D).externalName(shellParent.getExternalName())
        .donorSex(shellParent.getDonorSex()).build();

    setChangeDetails(identitySample);
    return (Identity) save(identitySample);
  }

  /**
   * Loads persisted objects into sample fields. Should be called before saving new samples. Loads all member objects <b>except</b>
   * <ul>
   * <li>parent sample for detailed samples</li>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param sample the Sample to load entities into. Must contain at least the IDs of objects to load (e.g. to load the persisted Project
   * into sample.project, sample.project.id must be set)
   * @throws IOException
   */
  private void loadChildEntities(Sample sample) throws IOException {
    if (sample.getProject() != null) {
      sample.setProject(projectStore.get(sample.getProject().getId()));
    }
    if (isDetailedSample(sample)) {
      DetailedSample sai = (DetailedSample) sample;
      if (sai.getSampleClass() != null && sai.getSampleClass().getId() != null) {
        sai.setSampleClass(sampleClassDao.getSampleClass(sai.getSampleClass().getId()));
      }
      if (sai.getDetailedQcStatus() != null && sai.getDetailedQcStatus().getId() != null) {
        sai.setDetailedQcStatus(detailedQcStatusDao.getDetailedQcStatus(sai.getDetailedQcStatus().getId()));
      }
      if (sai.getSubproject() != null && sai.getSubproject().getId() != null) {
        sai.setSubproject(subProjectDao.getSubproject(sai.getSubproject().getId()));
      }
      if (isAliquotSample(sai)) {
        SampleAliquot sa = (SampleAliquot) sai;
        if (sa.getSamplePurpose() != null && sa.getSamplePurpose().getId() != null) {
          sa.setSamplePurpose(samplePurposeDao.getSamplePurpose(sa.getSamplePurpose().getId()));
        }
      }
      if (isTissueSample(sai)) {
        SampleTissue st = (SampleTissue) sai;
        if (st.getTissueMaterial() != null && st.getTissueMaterial().getId() != null) {
          st.setTissueMaterial(tissueMaterialDao.getTissueMaterial(st.getTissueMaterial().getId()));
        }
        if (st.getTissueOrigin() != null && st.getTissueOrigin().getId() != null) {
          st.setTissueOrigin(tissueOriginDao.getTissueOrigin(st.getTissueOrigin().getId()));
        }
        if (st.getTissueType() != null && st.getTissueType().getId() != null) {
          st.setTissueType(tissueTypeDao.getTissueType(st.getTissueType().getId()));
        }
        if (st.getLab() != null && st.getLab().getId() != null) {
          st.setLab(labService.get(st.getLab().getId()));
        }
      }
    }
  }

  private void validateHierarchy(DetailedSample sample) throws IOException {
    Set<SampleValidRelationship> sampleValidRelationships = sampleValidRelationshipService.getAll();
    if (!LimsUtils.isValidRelationship(sampleValidRelationships, sample.getParent(), sample)) {
      throw new IllegalArgumentException("Parent " + sample.getParent().getSampleClass().getAlias()
          + " not permitted to have a child of type " + sample.getSampleClass().getAlias());
    }
  }

  /**
   * Updates all timestamps and user data associated with the change
   * 
   * @param sample the Sample to update
   * @throws IOException
   */
  private void setChangeDetails(Sample sample) throws IOException {
    User user = authorizationManager.getCurrentUser();
    sample.setLastModifier(user);
  }

  @Override
  public void update(Sample sample) throws IOException {
    Sample updatedSample = get(sample.getId());
    authorizationManager.throwIfNotWritable(updatedSample);
    applyChanges(updatedSample, sample);
    setChangeDetails(updatedSample);
    loadChildEntities(updatedSample);
    if (isDetailedSample(updatedSample)) {
      DetailedSample detailedUpdated = (DetailedSample) updatedSample;
      if (detailedUpdated.getParent() != null) {
        detailedUpdated.setParent((DetailedSample) get(detailedUpdated.getParent().getId()));
        validateHierarchy(detailedUpdated);
      }
    }

    save(updatedSample);
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
    target.setQcPassed(source.getQcPassed());
    target.setScientificName(source.getScientificName());
    target.setTaxonIdentifier(source.getTaxonIdentifier());

    // validate alias uniqueness only if the alias has changed and the sample does not have a non-standard alias
    if (!target.getAlias().equals(source.getAlias())
        && (!isDetailedSample(target) || (isDetailedSample(target) && !((DetailedSample) target).hasNonStandardAlias()))) {
      validateAliasUniqueness(source.getAlias());
    }
    target.setAlias(source.getAlias());
    target.setDescription(source.getDescription());
    target.setDiscarded(source.isDiscarded());
    target.setVolume(source.getVolume());
    target.setIdentificationBarcode(source.getIdentificationBarcode());
    if (isDetailedSample(target)) {
      DetailedSample dTarget = (DetailedSample) target;
      DetailedSample dSource = (DetailedSample) source;
      dTarget.setArchived(dSource.getArchived());
      dTarget.setGroupDescription(dSource.getGroupDescription());
      dTarget.setGroupId(dSource.getGroupId());
      dTarget.setDetailedQcStatus(dSource.getDetailedQcStatus());
      dTarget.setDetailedQcStatusNote(dSource.getDetailedQcStatusNote());

      if (isIdentitySample(target)) {
        Identity iTarget = (Identity) target;
        Identity iSource = (Identity) source;
        if (!iSource.getExternalName().equals(iTarget.getExternalName())) {
          confirmExternalNameUniqueForProjectIfRequired(iSource.getExternalName(), iTarget);
        }
        iTarget.setExternalName(iSource.getExternalName());
      }
      if (isTissueSample(target)) {
        applyChanges((SampleTissue) target, (SampleTissue) source);
      }
      if (isTissueProcessingSample(target)) {
        applyChanges((SampleTissueProcessing) target, (SampleTissueProcessing) source);
      }
      if (isAliquotSample(target)) {
        SampleAliquot saTarget = (SampleAliquot) target;
        SampleAliquot saSource = (SampleAliquot) source;
        saTarget.setSamplePurpose(saSource.getSamplePurpose());
      }
      if (isStockSample(target)) {
        SampleStock ssTarget = (SampleStock) target;
        SampleStock ssSource = (SampleStock) source;
        ssTarget.setStrStatus(ssSource.getStrStatus());
        ssTarget.setConcentration(ssSource.getConcentration());
        ssTarget.setDNAseTreated(ssSource.getDNAseTreated());
      }
    }
  }

  public void applyChanges(SampleTissue target, SampleTissue source) {
    target.setPassageNumber(source.getPassageNumber());
    target.setTimesReceived(source.getTimesReceived());
    target.setTubeNumber(source.getTubeNumber());
    target.setExternalInstituteIdentifier(source.getExternalInstituteIdentifier());
    target.setRegion(source.getRegion());
  }

  public void applyChanges(SampleTissueProcessing target, SampleTissueProcessing source) {
    if (source instanceof SampleCVSlide) {
      ((SampleCVSlide) target).setSlides(((SampleCVSlide) source).getSlides());
      ((SampleCVSlide) target).setDiscards(((SampleCVSlide) source).getDiscards());
      ((SampleCVSlide) target).setThickness(((SampleCVSlide) source).getThickness());
    } else if (source instanceof SampleLCMTube) {
      ((SampleLCMTube) target).setSlidesConsumed(((SampleLCMTube) source).getSlidesConsumed());
    }
  }

  @Override
  public List<Sample> getAll() throws IOException {
    Collection<Sample> allSamples = sampleDao.getSamples();
    return authorizationManager.filterUnreadable(allSamples);
  }

  @Override
  public Long countAll() throws IOException {
    return sampleDao.countAll();
  }

  @Override
  public void delete(Long sampleId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Sample sample = get(sampleId);
    sampleDao.deleteSample(sample);
  }

  @CoverageIgnore
  @Override
  public List<Sample> getByPageAndSize(int offset, int size, String sortCol, String sortDir) throws IOException {
    Collection<Sample> samples = sampleDao.listByOffsetAndNumResults(offset, size, sortCol, sortDir);
    return authorizationManager.filterUnreadable(samples);
  }

  @CoverageIgnore
  @Override
  public List<Sample> getByPageAndSizeAndSearch(int offset, int size, String querystr, String sortCol, String sortDir) throws IOException {
    Collection<Sample> samples = sampleDao.listBySearchOffsetAndNumResults(offset, size, querystr, sortCol, sortDir);
    return authorizationManager.filterUnreadable(samples);
  }

  @CoverageIgnore
  @Override
  public List<Sample> getBySearch(String querystr) throws IOException {
    Collection<Sample> samples = sampleDao.listBySearch(querystr);
    return authorizationManager.filterUnreadable(samples);
  }

  @CoverageIgnore
  @Override
  public Long countBySearch(String querystr) throws IOException {
    return sampleDao.countBySearch(querystr);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Collection<Identity> getIdentitiesByExternalNameOrAlias(String externalName) throws IOException {
    return sampleDao.getIdentitiesByExternalNameOrAlias(externalName);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Collection<Identity> getIdentitiesByExternalNameAndProject(String externalName, Long projectId) throws IOException {
    return sampleDao.getIdentitiesByExternalNameAndProject(externalName, projectId);
  }

  @Override
  public List<Sample> getByAlias(String alias) throws IOException {
    return new ArrayList<>(sampleDao.listByAlias(alias));
  }

  @Override
  public Sample getByBarcode(String barcode) throws IOException {
    Sample sample = sampleDao.getByBarcode(barcode);
    return (authorizationManager.readCheck(sample) ? sample : null);
  }

  @Override
  public void addNote(Sample sample, Note note) throws IOException {
    Sample managed = sampleDao.get(sample.getId());
    authorizationManager.throwIfNotWritable(managed);
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    sampleDao.save(managed);
  }

  @Override
  public void deleteNote(Sample sample, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Sample managed = sampleDao.get(sample.getId());
    authorizationManager.throwIfNotWritable(managed);
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Sample " + sample.getId());
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    sampleDao.save(managed);
  }

}
