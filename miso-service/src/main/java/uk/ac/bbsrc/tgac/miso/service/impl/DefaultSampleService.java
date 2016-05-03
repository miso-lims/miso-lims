package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample.SampleFactoryBuilder;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.QcPassedDetailDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleGroupDao;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.IdentityService;
import uk.ac.bbsrc.tgac.miso.service.SampleAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.SampleAnalyteService;
import uk.ac.bbsrc.tgac.miso.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.SampleTissueService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO;

@Transactional
@Service
public class DefaultSampleService implements SampleService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleService.class);

  @Autowired
  private SampleDao sampleDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private SQLProjectDAO sqlProjectDAO;

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private SampleAnalyteService sampleAnalyteService;

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
  private SecurityStore securityStore;
  
  @Autowired
  private SamplePurposeDao samplePurposeDao;
  
  @Autowired
  private SampleGroupDao sampleGroupDao;
  
  @Autowired
  private TissueMaterialDao tissueMaterialDao;
  
  @Autowired
  private MisoNamingScheme<Sample> sampleNamingScheme;
  
  @Autowired
  private MisoNamingScheme<Sample> namingScheme;
  
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  
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
    setChangeDetails(sample, true);
    if (sample.getSampleAdditionalInfo() != null) { 
      if (sample.getSampleAdditionalInfo().getSampleClass() == null 
          || sample.getSampleAdditionalInfo().getSampleClass().getSampleCategory() == null) {
        throw new IllegalArgumentException("Sample class or category missing");
      }
      if (!Identity.CATEGORY_NAME.equals(sample.getSampleAdditionalInfo().getSampleClass().getSampleCategory())) {
        sample.getSampleAdditionalInfo().setParent(findOrCreateParent(sample));
        validateHierarchy(sample);
      }
    }
    
    // pre-save field generation
    sample.setName(generateTemporaryName());
    if (isStringEmptyOrNull(sample.getAlias()) && sampleNamingScheme.hasGeneratorFor("alias")) {
      sample.setAlias(generateTemporaryName());
    } else {
      validateAliasUniqueness(sample.getAlias());
    }
    if (sample.getSampleAdditionalInfo() != null && sample.getParent() != null) {
      int siblingNumber = sampleDao.getNextSiblingNumber(sample.getParent(), sample.getSampleAdditionalInfo().getSampleClass());
      sample.getSampleAdditionalInfo().setSiblingNumber(siblingNumber);
    }
    
    normalizeSample(sample);
    return save(sample).getId();
  }

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
   * Checks whether the configured naming scheme allows duplicate alias. If not, checks to see whether an alias is already 
   * in use, in order to prevent duplicates. This method should be called <b>before</b> saving a new Sample
   * 
   * @param alias the alias to validate
   * @throws ConstraintViolationException if duplicate alias are <b>not</b> allowed <b>and</b> the alias is already in use
   * @throws IOException
   */
  private void validateAliasUniqueness(String alias) throws ConstraintViolationException, IOException {
    if (!sampleNamingScheme.allowDuplicateEntityNameFor("alias") && sampleDao.aliasExists(alias)) {
      throw new ConstraintViolationException(String.format("A sample with this alias '%s' already exists in the database", 
          alias), null, "alias");
    }
  }
  
  /**
   * Finds an existing parent Sample or creates a new one if necessary
   * 
   * @param sample must contain Identity details (via {@link Sample#getIdentity() getIdentity}), 
   * including externalName if a new parent is to be created. And existing parent may be specified by including its sampleId or 
   * externalName
   * @return
   * @throws IOException 
   * @throws SQLException 
   * @throws MisoNamingException 
   */
  private Sample findOrCreateParent(Sample sample) throws IOException {
    Identity parentIdentity = sample.getIdentity();
    if (parentIdentity == null) throw new IllegalArgumentException("Parent identity is required to create a new Sample");
    if (parentIdentity.getSampleId() != null) {
      Sample parent = get(parentIdentity.getSampleId());
      if (parent != null) return parent;
    }
    if (isStringEmptyOrNull(parentIdentity.getExternalName())) {
      throw new IllegalArgumentException("Parent identity must be specified by sample ID or external name");
    }
    Identity parent = identityService.get(parentIdentity.getExternalName());
    if (parent != null) {
      return parent.getSample();
    }
    try {
      return createParentIdentity(sample);
    } catch (MisoNamingException e) {
      throw new IllegalArgumentException("Name generator failed to generate a valid name", e);
    } catch (SQLException e) {
      throw new IOException(e);
    }
  }
  
  private Sample createParentIdentity(Sample sample) throws IOException, MisoNamingException, SQLException {
    log.debug("Creating a new Identity to use as a parent.");
    List<SampleClass> identityClasses = sampleClassDao.listByCategory(Identity.CATEGORY_NAME);
    if (identityClasses.size() != 1) {
      throw new IllegalStateException("Found more than one SampleClass of category " + Identity.CATEGORY_NAME
          + ". Cannot choose which to use as root sample class.");
    }
    SampleClass rootSampleClass = identityClasses.get(0);
    SampleAdditionalInfo parentSai = new SampleAdditionalInfoImpl();
    SampleAdditionalInfo childSai = sample.getSampleAdditionalInfo();
    parentSai.setSampleClass(rootSampleClass);
    parentSai.setTissueOrigin(childSai.getTissueOrigin());
    parentSai.setTissueType(childSai.getTissueType());
    parentSai.setSubproject(childSai.getSubproject());
    parentSai.setLab(childSai.getLab());
    
    Identity parentIdentity = new IdentityImpl();
    String number = sampleNumberPerProjectService.nextNumber(sample.getProject());
    // Cannot generate identity alias via sampleNameGenerator because of dependence on SampleNumberPerProjectService
    String internalName = sample.getProject().getAlias() + "_" + number;
    parentIdentity.setInternalName(internalName);
    parentIdentity.setExternalName(sample.getIdentity().getExternalName());

    Sample identitySample = new SampleFactoryBuilder()
        .user(authorizationManager.getCurrentUser())
        .project(sample.getProject())
        .description("Identity")
        .sampleType(sample.getSampleType())
        .scientificName(sample.getScientificName())
        .name(generateTemporaryName())
        .alias(internalName)
        .rootSampleClass(rootSampleClass)
        .volume(0D)
        .sampleAdditionalInfo(parentSai)
        .identity(parentIdentity)
        .build();
    
    setChangeDetails(identitySample, true);
    return save(identitySample);
  }
  
  /**
   * Loads persisted objects into sample fields. Should be called before saving new samples. 
   * Loads all member objects <b>except</b>
   * <ul><li>parent sample for detailed samples</li>
   * <li>creator/lastModifier User objects</li></ul>
   * 
   * @param sample the Sample to load entities into. Must contain at least the IDs of objects to load (e.g. to 
   * load the persisted Project into sample.project, sample.project.id must be set)
   * @throws IOException 
   */
  private void loadChildEntities(Sample sample) throws IOException {
    if (sample.getProject() != null) {
      sample.setProject(projectStore.get(sample.getProject().getId()));
    }
    // TODO: move these to public methods in other DAOs (e.g. sampleAdditionalInfoDao.loadChildEntities(SampleAdditionalInfo))
    if (sample.getSampleAdditionalInfo() != null) {
      SampleAdditionalInfo sai = sample.getSampleAdditionalInfo();
      if (sai.getSampleClass() != null && sai.getSampleClass().getId() != null) {
        sai.setSampleClass(sampleClassDao.getSampleClass(sai.getSampleClass().getId()));
      }
      if (sai.getTissueOrigin() != null && sai.getTissueOrigin().getId() != null) {
        sai.setTissueOrigin(tissueOriginDao.getTissueOrigin(sai.getTissueOrigin().getId()));
      }
      if (sai.getTissueType() != null && sai.getTissueType().getId() != null) {
        sai.setTissueType(tissueTypeDao.getTissueType(sai.getTissueType().getId()));
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
      if (sample.getSampleAnalyte() != null) {
        SampleAnalyte sa = sample.getSampleAnalyte();
        if (sa.getSamplePurpose() != null && sa.getSamplePurpose().getId() != null) {
          sa.setSamplePurpose(samplePurposeDao.getSamplePurpose(sa.getSamplePurpose().getId()));
        }
        if (sa.getSampleGroup() != null && sa.getSampleGroup().getId() != null) {
          sa.setSampleGroup(sampleGroupDao.getSampleGroup(sa.getSampleGroup().getId()));
        }
        if (sa.getTissueMaterial() != null && sa.getTissueMaterial().getId() != null) {
          sa.setTissueMaterial(tissueMaterialDao.getTissueMaterial(sa.getTissueMaterial().getId()));
        }
      }
    }
  }
  
  /**
   * Cleans up parts of the model which may have been used during the creation process, but which should not be persisted
   * 
   * @param sample the Sample to normalize
   */
  private void normalizeSample(Sample sample) {
    if (sample.getSampleAdditionalInfo() != null) {
      String category = sample.getSampleAdditionalInfo().getSampleClass().getSampleCategory();
      if (!SampleTissue.CATEGORY_NAME.equals(category)) {
        sample.setSampleTissue(null);
      }
      if (!SampleAnalyte.CATEGORY_NAME.equals(category)) {
        sample.setSampleAnalyte(null);
      }
      if (!Identity.CATEGORY_NAME.equals(category)) {
        sample.setIdentity(null);
      }
    }
  }
  
  private void validateHierarchy(Sample sample) throws IOException {
    Set<SampleValidRelationship> sampleValidRelationships = sampleValidRelationshipService.getAll();
    if (!LimsUtils.isValidRelationship(sampleValidRelationships, sample.getParent(), sample)) {
      throw new IllegalArgumentException("Parent " + sample.getParent().getSampleAdditionalInfo().getSampleClass().getAlias()
          + " not permitted to have a child of type " + sample.getSampleAdditionalInfo().getSampleClass().getAlias());
    }
  }
  
  /**
   * Updates all timestamps and user data associated with the change
   * 
   * @param sample the Sample to update
   * @param setCreated specifies whether this is a newly created Sample requiring creation timestamps and user data
   * @throws IOException 
   */
  private void setChangeDetails(Sample sample, boolean setCreated) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Date now = new Date();
    sample.setLastModifier(user);
    if (sample.getSampleAdditionalInfo() != null) {
      if (setCreated) {
        sample.getSampleAdditionalInfo().setCreatedBy(user);
        sample.getSampleAdditionalInfo().setCreationDate(now);
      }
      sample.getSampleAdditionalInfo().setUpdatedBy(user);
      sample.getSampleAdditionalInfo().setLastUpdated(now);
    }
    if (sample.getIdentity() != null) {
      if (setCreated) {
        sample.getIdentity().setCreatedBy(user);
        sample.getIdentity().setCreationDate(now);
      }
      sample.getIdentity().setUpdatedBy(user);
      sample.getIdentity().setLastUpdated(now);
    }
    if (sample.getSampleTissue() != null) {
      if (setCreated) {
        sample.getSampleTissue().setCreatedBy(user);
        sample.getSampleTissue().setCreationDate(now);
      }
      sample.getSampleTissue().setUpdatedBy(user);
      sample.getSampleTissue().setLastUpdated(now);
    }
    if (sample.getSampleAnalyte() != null) {
      if (setCreated) {
        sample.getSampleAnalyte().setCreatedBy(user);
        sample.getSampleAnalyte().setCreationDate(now);
      }
      sample.getSampleAnalyte().setUpdatedBy(user);
      sample.getSampleAnalyte().setLastUpdated(now);
    }
  }

  @Override
  public void update(Sample sample) throws IOException {
    Sample updatedSample = get(sample.getId());
    authorizationManager.throwIfNotWritable(updatedSample);
    applyChanges(updatedSample, sample);
    setChangeDetails(updatedSample, false);
    loadChildEntities(updatedSample);
    if (updatedSample.getSampleAdditionalInfo() != null) {
      updatedSample.getSampleAdditionalInfo().setParent(get(updatedSample.getSampleAdditionalInfo().getParent().getId()));
      validateHierarchy(updatedSample);
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
    if (!target.getAlias().equals(source.getAlias())) {
      validateAliasUniqueness(source.getAlias());
    }
    target.setAlias(source.getAlias());
    target.setDescription(source.getDescription());
    target.setEmpty(source.isEmpty());
    target.setVolume(source.getVolume());
    if (target.getSampleAdditionalInfo() != null) {
      sampleAdditionalInfoService.applyChanges(target.getSampleAdditionalInfo(), source.getSampleAdditionalInfo());
      if (target.getIdentity() != null) {
        identityService.applyChanges(target.getIdentity(), source.getIdentity());
      }
      if (target.getSampleTissue() != null) {
        sampleTissueService.applyChanges(target.getSampleTissue(), source.getSampleTissue());
      }
      if (target.getSampleAnalyte() != null) {
        sampleAnalyteService.applyChanges(target.getSampleAnalyte(), source.getSampleAnalyte());
      }
    }
  }

  @Override
  public Set<Sample> getAll() throws IOException {
    Collection<Sample> allSamples = sampleDao.getSample();
    return authorizationManager.filterUnreadable(allSamples);
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

}
