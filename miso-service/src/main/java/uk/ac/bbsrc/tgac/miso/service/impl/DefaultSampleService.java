package uk.ac.bbsrc.tgac.miso.service.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample.SampleFactoryBuilder;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAdditionalInfoDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
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

  @Override
  public Sample get(Long sampleId) throws IOException {
    Sample sample = sampleDao.getSample(sampleId);
    authorizationManager.throwIfNotReadable(sample);
    return sample;
  }

  @Override
  public Long create(SampleDto sampleDto) throws IOException {
    // Construct a Sample from the SampleDto.
    Sample sample = to(sampleDto);
    authorizationManager.throwIfNotWritable(sample);
    User user = authorizationManager.getCurrentUser();
    if (sampleDto.getParentId() == null) {
      log.debug("No parent has been provided.");
      if (sampleDto.getSampleIdentity() != null && !LimsUtils.isStringEmptyOrNull(sampleDto.getSampleIdentity().getExternalName())) {
        log.debug("Obtaining parent based on external name.");
        Sample identitySample = null;
        Identity existingIdentity = identityService.get(sampleDto.getSampleIdentity().getExternalName());
        if (existingIdentity != null) {
          log.debug("Parent with existing external name already exists. Using that parent.");
          sample.setParent(existingIdentity.getSample());
        } else {
          log.debug("Creating a new Identity to use as a parent.");
          String number = sampleNumberPerProjectService.nextNumber(sample.getProject());
          String internalName = sample.getProject().getAlias() + "_" + number;
          sample.getIdentity().setInternalName(internalName);

          SampleClass rootSampleClass = sampleClassDao.getSampleClass(sampleDto.getRootSampleClassId());
          ServiceUtils.throwIfNull(rootSampleClass, "rootSampleClassId", sampleDto.getRootSampleClassId());
          SampleAdditionalInfoDto sampleAdditionalInfoDto = new SampleAdditionalInfoDto();
          sampleAdditionalInfoDto.setSampleClassId(sampleDto.getRootSampleClassId());
          SampleAdditionalInfo sampleAdditionalInfo = sampleAdditionalInfoService.to(sampleAdditionalInfoDto);

          identitySample = new SampleFactoryBuilder().user(user).project(sample.getProject()).description("Identity").sampleType("Identity")
              .scientificName("Identity").name(internalName).rootSampleClass(rootSampleClass).identity(sample.getIdentity())
              .sampleAdditionalInfo(sampleAdditionalInfo).sampleTissue(sample.getSampleTissue()).build();

          sample.setParent(identitySample);
        }
      } else {
        throw new IllegalArgumentException(
            "Unable to create parent Identity for sample. Must provide either a parentId or a sampleIdentity.externalName.");
      }
    }

    Sample newSample = new SampleFactoryBuilder().description(sample.getDescription()).sampleType(sample.getSampleType())
        .scientificName(sample.getScientificName()).user(user).project(sample.getProject())
        .sampleAdditionalInfo(sample.getSampleAdditionalInfo()).sampleAnalyte(sample.getSampleAnalyte()).accession(sample.getAccession())
        .name(sample.getName()).identificationBarcode(sample.getIdentificationBarcode()).locationBarcode(sample.getLocationBarcode())
        .receivedDate(sample.getReceivedDate()).qcPassed(sample.getQcPassed()).alias(sample.getAlias())
        .taxonIdentifier(sample.getTaxonIdentifier()).parent(sample.getParent()).sampleTissue(sample.getSampleTissue()).build();

    if (!LimsUtils.isValidRelationship(sampleValidRelationshipService.getAll(), newSample.getParent(), newSample)) {
      throw new IllegalArgumentException("Parent " + newSample.getParent().getSampleAdditionalInfo().getSampleClass().getAlias()
          + " not permitted to have a child of type " + newSample.getSampleAdditionalInfo().getSampleClass().getAlias());
    }

    try {
      return sampleDao.addSample(newSample);
    } catch (ConstraintViolationException e) {
      // Send the nested root cause message to the user, since it contains the actual error.
      throw new ConstraintViolationException(e.getMessage() + " " + ExceptionUtils.getRootCauseMessage(e), e.getSQLException(),
          e.getConstraintName());
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  Sample to(SampleDto sampleDto) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    checkArgument(sampleDto.getProjectId() != null, "A Project id must be provided to construct Sample.");
    Sample sample = Dtos.to(sampleDto);

    Project project = sqlProjectDAO.get(sampleDto.getProjectId());
    ServiceUtils.throwIfNull(project, "projectId", sampleDto.getProjectId());
    sample.setProject(project);

    if (sampleDto.getParentId() != null) {
      Sample parent = sampleDao.getSample(sampleDto.getParentId());
      ServiceUtils.throwIfNull(parent, "parentId", sampleDto.getParentId());
      sample.setParent(parent);
    }

    if (sampleDto.getSampleIdentity() != null) {
      sample.setIdentity(identityService.to(sampleDto.getSampleIdentity()));
    }
    if (sampleDto.getSampleAnalyte() != null) {
      sample.setSampleAnalyte(sampleAnalyteService.to(sampleDto.getSampleAnalyte()));
    }
    if (sampleDto.getSampleAdditionalInfo() != null) {
      sample.setSampleAdditionalInfo(sampleAdditionalInfoService.to(sampleDto.getSampleAdditionalInfo()));
    }
    if (sampleDto.getSampleTissue() != null) {
      sample.setSampleTissue(sampleTissueService.to(sampleDto.getSampleTissue()));
    }
    return sample;
  }

  @Override
  public void update(Sample sample) throws IOException {
    Sample updatedSample = get(sample.getId());
    authorizationManager.throwIfNotWritable(updatedSample);
    updatedSample.setDescription(sample.getDescription());
    updatedSample.setSampleType(sample.getSampleType());
    updatedSample.setReceivedDate(sample.getReceivedDate());
    updatedSample.setQcPassed(sample.getQcPassed());
    updatedSample.setScientificName(sample.getScientificName());
    updatedSample.setTaxonIdentifier(sample.getTaxonIdentifier());
    updatedSample.setAlias(sample.getAlias());
    updatedSample.setDescription(sample.getDescription());
    updatedSample.setSampleAdditionalInfo(sample.getSampleAdditionalInfo());
    updatedSample.setSampleAnalyte(sample.getSampleAnalyte());

    // Check for parent and valid relationship.

    sampleDao.update(updatedSample);
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

}
