package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isAliquotSample;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isDetailedSample;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isIdentitySample;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStockSample;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringBlankOrNull;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isTissueProcessingSample;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isTissueSample;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleCVSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl.IdentityBuilder;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.QcPassedDetailDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.IdentityService;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.service.SampleAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.SampleTissueService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleService implements SampleService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleService.class);

  @Autowired
  private SampleDao sampleDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private SampleAdditionalInfoService sampleAdditionalInfoService;

  @Autowired
  private IdentityService identityService;

  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;

  @Autowired
  private SampleNumberPerProjectService sampleNumberPerProjectService;

  @Autowired
  private SampleTissueService sampleTissueService;

  @Autowired
  private ProjectStore projectStore;

  @Autowired
  private TissueOriginDao tissueOriginDao;

  @Autowired
  private TissueTypeDao tissueTypeDao;

  @Autowired
  private QcPassedDetailDao qcPassedDetailDao;

  @Autowired
  private SubprojectDao subProjectDao;

  @Autowired
  private KitStore kitStore;

  @Autowired
  private SamplePurposeDao samplePurposeDao;

  @Autowired
  private TissueMaterialDao tissueMaterialDao;

  @Autowired
  private LabService labService;

  @Autowired
  private MisoNamingScheme<Sample> sampleNamingScheme;

  @Autowired
  private MisoNamingScheme<Sample> namingScheme;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  public void setSampleDao(SampleDao sampleDao) {
    this.sampleDao = sampleDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setSampleClassDao(SampleClassDao sampleClassDao) {
    this.sampleClassDao = sampleClassDao;
  }

  public void setSampleAdditionalInfoService(SampleAdditionalInfoService sampleAdditionalInfoService) {
    this.sampleAdditionalInfoService = sampleAdditionalInfoService;
  }

  public void setIdentityService(IdentityService identityService) {
    this.identityService = identityService;
  }

  public void setSampleValidRelationshipService(SampleValidRelationshipService sampleValidRelationshipService) {
    this.sampleValidRelationshipService = sampleValidRelationshipService;
  }

  public void setSampleNumberPerProjectService(SampleNumberPerProjectService sampleNumberPerProjectService) {
    this.sampleNumberPerProjectService = sampleNumberPerProjectService;
  }

  public void setSampleTissueService(SampleTissueService sampleTissueService) {
    this.sampleTissueService = sampleTissueService;
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

  public void setQcPassedDetailDao(QcPassedDetailDao qcPassedDetailDao) {
    this.qcPassedDetailDao = qcPassedDetailDao;
  }

  public void setSubProjectDao(SubprojectDao subProjectDao) {
    this.subProjectDao = subProjectDao;
  }

  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
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

  public void setSampleNamingScheme(MisoNamingScheme<Sample> sampleNamingScheme) {
    this.sampleNamingScheme = sampleNamingScheme;
  }

  public void setNamingScheme(MisoNamingScheme<Sample> namingScheme) {
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
        SampleAdditionalInfo detailed = (SampleAdditionalInfo) sample;
        try {
          detailed.setParent(findOrCreateParent(detailed));
          detailed.setNonStandardAlias(detailed.getParent().hasNonStandardAlias());
        } catch (MisoNamingException e) {
          throw new IOException(e.getMessage(), e);
        }
        validateHierarchy(detailed);
      }
    }

    // pre-save field generation
    sample.setName(generateTemporaryName());
    if (isDetailedSample(sample) && ((SampleAdditionalInfo) sample).hasNonStandardAlias()) {
      // do not validate alias
    } else if (isStringEmptyOrNull(sample.getAlias()) && sampleNamingScheme.hasGeneratorFor("alias")) {
      sample.setAlias(generateTemporaryName());
    } else {
      validateAliasUniqueness(sample.getAlias());
    }
    if (isStockSample(sample) || isAliquotSample(sample) || isTissueProcessingSample(sample)) {
      SampleAdditionalInfo detailed = (SampleAdditionalInfo) sample;
      if (detailed.getParent() != null && detailed.getSiblingNumber() == null) {
        int siblingNumber = sampleDao.getNextSiblingNumber(detailed.getParent(), detailed.getSampleClass());
        detailed.setSiblingNumber(siblingNumber);
      }
    }
    return save(sample).getId();
  }

  /**
   * Saves the Sample as is to the database. Fields which should be autogenerated but first require the primary key are then generated, and
   * the changes are saved
   * 
   * @param sample
   *          the Sample to save
   * @return the same Sample provided, including fields that were generated by this method
   * @throws IOException
   */
  private Sample save(Sample sample) throws IOException {
    try {
      Long newId = sample.getId();
      if (newId == Sample.UNSAVED_ID) {
        newId = sampleDao.addSample(sample);
      } else {
        sampleDao.update(sample);
      }
      Sample created = sampleDao.getSample(newId);

      // post-save field generation
      boolean needsUpdate = false;
      if (hasTemporaryName(sample)) {
        created.setName(namingScheme.generateNameFor("name", created));
        needsUpdate = true;
      }
      if (hasTemporaryAlias(sample)) {
        String generatedAlias = sampleNamingScheme.generateNameFor("alias", created);
        validateAliasUniqueness(generatedAlias);
        created.setAlias(generatedAlias);
        needsUpdate = true;
      }
      if (isStringBlankOrNull(sample.getAlias())) {
        sample.setAlias(sample.getName());
        needsUpdate = true;
      }
      if (autoGenerateIdBarcodes) {
        autoGenerateIdBarcode(sample);
        needsUpdate = true;
      } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user
      if (needsUpdate) sampleDao.update(created);

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
   * Checks whether the configured naming scheme allows duplicate alias. If not, checks to see whether an alias is already in use, in order
   * to prevent duplicates. This method should be called <b>before</b> saving a new Sample
   * 
   * @param alias
   *          the alias to validate
   * @throws ConstraintViolationException
   *           if duplicate alias are <b>not</b> allowed <b>and</b> the alias is already in use
   * @throws IOException
   */
  private void validateAliasUniqueness(String alias) throws ConstraintViolationException, IOException {
    if (!sampleNamingScheme.allowDuplicateEntityNameFor("alias") && sampleDao.aliasExists(alias)) {
      throw new ConstraintViolationException(String.format("A sample with this alias '%s' already exists in the database", alias), null,
          "alias");
    }
  }

  /**
   * Finds an existing parent Sample or creates a new one if necessary
   * 
   * @param sample
   *          must contain parent (via {@link SampleAdditionalInfo#getParent() getParent}), including externalName if a new parent is to be
   *          created. An existing parent may be specified by including its sampleId or externalName
   * 
   * @return
   * @throws IOException
   * @throws SQLException
   * @throws MisoNamingException
   */
  private SampleAdditionalInfo findOrCreateParent(SampleAdditionalInfo sample) throws IOException, MisoNamingException {
    if (sample.getParent() == null) {
      throw new IllegalArgumentException("Detailed sample is missing parent identifier");
    }
    Sample tempParent = sample.getParent();
    if (tempParent.getId() != Sample.UNSAVED_ID) {
      Sample parent = sampleDao.getSample(tempParent.getId());
      if (parent == null)
        throw new IllegalArgumentException("Parent sample does not exist");
      else
        return (SampleAdditionalInfo) parent;
    } else if (isIdentitySample(tempParent) && !isStringEmptyOrNull(((Identity) tempParent).getExternalName())) {
      Identity parentIdentity = identityService.get(((Identity) tempParent).getExternalName());
      if (parentIdentity != null) return parentIdentity;
      try {
        return createParentIdentity(sample);
      } catch (SQLException e) {
        throw new IOException(e);
      }
    } else if (isTissueSample(tempParent)) {
      return createParentTissue((SampleTissue) tempParent, sample);
    }
    throw new IllegalArgumentException("Could not resolve parent sample");
  }

  private SampleTissue createParentTissue(SampleTissue tissue, SampleAdditionalInfo child) throws IOException {
    log.debug("Creating a new Tissue to use as a parent.");
    tissue.setProject(child.getProject());
    tissue.setDescription("Tissue");
    tissue.setSampleType(child.getSampleType());
    tissue.setScientificName(child.getScientificName());
    tissue.setVolume(0D);
    tissue.setSynthetic(true);
    create(tissue);
    return tissue;
  }

  private Identity createParentIdentity(SampleAdditionalInfo sample) throws IOException, MisoNamingException, SQLException {
    log.debug("Creating a new Identity to use as a parent.");
    List<SampleClass> identityClasses = sampleClassDao.listByCategory(Identity.CATEGORY_NAME);
    if (identityClasses.size() != 1) {
      throw new IllegalStateException("Found more or less than one SampleClass of category " + Identity.CATEGORY_NAME
          + ". Cannot choose which to use as root sample class.");
    }
    SampleClass rootSampleClass = identityClasses.get(0);

    String number = sampleNumberPerProjectService.nextNumber(sample.getProject());
    // Cannot generate identity alias via sampleNameGenerator because of dependence on SampleNumberPerProjectService
    String internalName = sample.getProject().getShortName() + "_" + number;
    Identity shellParent = (Identity) sample.getParent();

    Sample identitySample = new IdentityBuilder().user(authorizationManager.getCurrentUser()).project(sample.getProject())
        .description("Identity").sampleType(sample.getSampleType()).scientificName(sample.getScientificName()).name(generateTemporaryName())
        .alias(internalName).rootSampleClass(rootSampleClass).volume(0D).externalName(shellParent.getExternalName())
        .internalName(internalName).donorSex(shellParent.getDonorSex()).build();

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
   * @param sample
   *          the Sample to load entities into. Must contain at least the IDs of objects to load (e.g. to load the persisted Project into
   *          sample.project, sample.project.id must be set)
   * @throws IOException
   */
  private void loadChildEntities(Sample sample) throws IOException {
    if (sample.getProject() != null) {
      sample.setProject(projectStore.lazyGet(sample.getProject().getId()));
    }
    // TODO: move these to public methods in other DAOs (e.g. sampleAdditionalInfoDao.loadChildEntities(SampleAdditionalInfo))
    if (isDetailedSample(sample)) {
      SampleAdditionalInfo sai = (SampleAdditionalInfo) sample;
      if (sai.getSampleClass() != null && sai.getSampleClass().getId() != null) {
        sai.setSampleClass(sampleClassDao.getSampleClass(sai.getSampleClass().getId()));
      }
      if (sai.getQcPassedDetail() != null && sai.getQcPassedDetail().getId() != null) {
        sai.setQcPassedDetail(qcPassedDetailDao.getQcPassedDetails(sai.getQcPassedDetail().getId()));
      }
      if (sai.getSubproject() != null && sai.getSubproject().getId() != null) {
        sai.setSubproject(subProjectDao.getSubproject(sai.getSubproject().getId()));
      }
      if (sai.getPrepKit() != null && sai.getPrepKit().getId() != null) {
        sai.setPrepKit(kitStore.getKitDescriptorById(sai.getPrepKit().getId()));
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

  private void validateHierarchy(SampleAdditionalInfo sample) throws IOException {
    Set<SampleValidRelationship> sampleValidRelationships = sampleValidRelationshipService.getAll();
    if (!LimsUtils.isValidRelationship(sampleValidRelationships, sample.getParent(), sample)) {
      throw new IllegalArgumentException("Parent " + sample.getParent().getSampleClass().getAlias()
          + " not permitted to have a child of type " + sample.getSampleClass().getAlias());
    }
  }

  /**
   * Updates all timestamps and user data associated with the change
   * 
   * @param sample
   *          the Sample to update
   * @param setCreated
   *          specifies whether this is a newly created Sample requiring creation timestamps and user data
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
      SampleAdditionalInfo detailedUpdated = (SampleAdditionalInfo) updatedSample;
      if (detailedUpdated.getParent() != null) {
        detailedUpdated.setParent((SampleAdditionalInfo) get(detailedUpdated.getParent().getId()));
        validateHierarchy(detailedUpdated);
      }
    }

    save(updatedSample);
  }

  /**
   * Copies modifiable fields from the source Sample into the target Sample to be persisted
   * 
   * @param target
   *          the persisted Sample to modify
   * @param source
   *          the modified Sample to copy modifiable fields from
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
        && (!isDetailedSample(target) || (isDetailedSample(target) && !((SampleAdditionalInfo) target).hasNonStandardAlias()))) {
      validateAliasUniqueness(source.getAlias());
    }
    target.setAlias(source.getAlias());
    target.setDescription(source.getDescription());
    target.setEmpty(source.isEmpty());
    target.setVolume(source.getVolume());
    if (isDetailedSample(target)) {
      sampleAdditionalInfoService.applyChanges((SampleAdditionalInfo) target, (SampleAdditionalInfo) source);
      if (isIdentitySample(target)) {
        identityService.applyChanges((Identity) target, (Identity) source);
      }
      if (isTissueSample(target)) {
        sampleTissueService.applyChanges((SampleTissue) target, (SampleTissue) source);
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
    Collection<Sample> allSamples = sampleDao.getSample();
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

  static final private String TEMPORARY_NAME_PREFIX = "TEMPORARY_S";

  /**
   * Generate a temporary name using a UUID.
   * 
   * @return Temporary name
   */
  static public String generateTemporaryName() {
    return TEMPORARY_NAME_PREFIX + UUID.randomUUID();
  }

  static public boolean hasTemporaryName(Sample sample) {
    return sample != null && sample.getName() != null && sample.getName().startsWith(TEMPORARY_NAME_PREFIX);
  }

  static public boolean hasTemporaryAlias(Sample sample) {
    return sample != null && sample.getAlias() != null && sample.getAlias().startsWith(TEMPORARY_NAME_PREFIX);
  }

  /**
   * Generates a unique barcode. Note that the barcode will change when the alias is changed.
   * 
   * @param sample
   */
  public void autoGenerateIdBarcode(Sample sample) {
    String barcode = sample.getName() + "::" + sample.getAlias();
    sample.setIdentificationBarcode(barcode);
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

}
