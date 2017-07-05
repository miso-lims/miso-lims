package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleValidRelationshipDao;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleClassService implements SampleClassService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleClassService.class);

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private SampleValidRelationshipDao sampleValidRelationshipDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setSampleClassDao(SampleClassDao sampleClassDao) {
    this.sampleClassDao = sampleClassDao;
  }

  @Override
  public SampleClass get(Long sampleClassId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleClassDao.getSampleClass(sampleClassId);
  }

  @Override
  public Long create(SampleClass sampleClass) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    sampleClass.setCreatedBy(user);
    sampleClass.setUpdatedBy(user);
    return sampleClassDao.addSampleClass(sampleClass);
  }

  @Override
  public void update(SampleClass sampleClass) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleClass updatedSampleClass = get(sampleClass.getId());
    updatedSampleClass.setAlias(sampleClass.getAlias());
    updatedSampleClass.setSampleCategory(sampleClass.getSampleCategory());
    updatedSampleClass.setSuffix(sampleClass.getSuffix());
    updatedSampleClass.setDNAseTreatable(sampleClass.getDNAseTreatable());
    User user = authorizationManager.getCurrentUser();
    updatedSampleClass.setUpdatedBy(user);
    sampleClassDao.update(updatedSampleClass);
  }

  @Override
  public Set<SampleClass> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(sampleClassDao.getSampleClass());
  }

  @Override
  public List<SampleClass> listByCategory(String sampleCategory) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleClassDao.listByCategory(sampleCategory);
  }

  @Override
  public void delete(Long sampleClassId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleClass sampleClass = get(sampleClassId);
    sampleClassDao.deleteSampleClass(sampleClass);
  }

  @Override
  public SampleClass inferStockFromAliquot(SampleClass sampleClass) {
    SampleClass aliquotClass = sampleClassDao.getSampleClass(sampleClass.getId());
    if (aliquotClass == null || !aliquotClass.getSampleCategory().equals(SampleAliquot.CATEGORY_NAME)) {
      throw new IllegalArgumentException("Sample class " + sampleClass.getId() + " is not a valid aliquot class.");
    }
    SampleClass stockClass = null;
    for (SampleValidRelationship relationship : sampleValidRelationshipDao.getSampleValidRelationship()) {
      if (!relationship.getArchived() && relationship.getChild().getId() == aliquotClass.getId()
          && relationship.getParent().getSampleCategory().equals(SampleStock.CATEGORY_NAME)) {
        if (stockClass == null) {
          stockClass = relationship.getParent();
        } else {
          throw new IllegalStateException("Aliquot class " + sampleClass.getId() + " has multiple stock class parents.");
        }
      }
    }
    if (stockClass == null) throw new IllegalStateException("Aliquot class " + sampleClass.getId() + " has no stock class.");
    return stockClass;
  }

  @Override
  public SampleClass inferTissueFromStock(SampleClass sampleClass) {
    SampleClass stockClass = sampleClassDao.getSampleClass(sampleClass.getId());
    if (stockClass == null || !stockClass.getSampleCategory().equals(SampleStock.CATEGORY_NAME)) {
      throw new IllegalArgumentException("Sample class " + sampleClass.getId() + " is not a valid stock class.");
    }
    SampleClass tissueClass = null;
    for (SampleValidRelationship relationship : sampleValidRelationshipDao.getSampleValidRelationship()) {
      if (!relationship.getArchived() && relationship.getChild().getId() == stockClass.getId()
          && relationship.getParent().getSampleCategory().equals(SampleTissue.CATEGORY_NAME)) {
        if (tissueClass == null) {
          tissueClass = relationship.getParent();
        } else {
          throw new IllegalStateException("Stock class " + sampleClass.getId() + " has multiple tissue class parents.");
        }
      }
    }
    if (tissueClass == null) throw new IllegalStateException("Stock class" + sampleClass.getId() + " has no stock class.");
    return tissueClass;
  }

}
