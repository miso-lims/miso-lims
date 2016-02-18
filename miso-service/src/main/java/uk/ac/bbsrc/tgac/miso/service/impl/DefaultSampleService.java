package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample.SampleFactoryBuilder;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
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

  @Override
  public Sample get(Long sampleId) throws IOException {
    Sample sample = sampleDao.getSample(sampleId);
    authorizationManager.throwIfNotReadable(sample);
    return sample;
  }

  @Override
  public Long create(Sample s, Long projectId, Long parentId, Long rootSampleClassId) throws IOException {
    authorizationManager.throwIfNotWritable(s);
    User user = authorizationManager.getCurrentUser();
    Project project = sqlProjectDAO.get(projectId);
    if (parentId == null) {
      // Create Identity.
      SampleClass rootSampleClass = sampleClassDao.getSampleClass(rootSampleClassId);
      Sample identity = new SampleFactoryBuilder().user(user).project(project).description("Identity").sampleType("Identity")
          .scientificName("Identity").name("Identity").rootSampleClass(rootSampleClass).build();
      parentId = sampleDao.addSample(identity);
    }

    Sample parent = sampleDao.getSample(parentId);

    Sample newSample = new SampleFactoryBuilder().description(s.getDescription()).sampleType(s.getSampleType())
        .scientificName(s.getScientificName()).user(user).project(project).sampleAdditionalInfo(s.getSampleAdditionalInfo())
        .identity(s.getIdentity()).sampleAnalyte(s.getSampleAnalyte()).accession(s.getAccession()).name(s.getName())
        .identificationBarcode(s.getIdentificationBarcode()).locationBarcode(s.getLocationBarcode()).receivedDate(s.getReceivedDate())
        .qcPassed(s.getQcPassed()).alias(s.getAlias()).taxonIdentifier(s.getTaxonIdentifier()).parent(parent).build();

    // Check for parent and valid relationship.
    // The relationship needs to be checked in the constructor as well.

    return sampleDao.addSample(newSample);
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
