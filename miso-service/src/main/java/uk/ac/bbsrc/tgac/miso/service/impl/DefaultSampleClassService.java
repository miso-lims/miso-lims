package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleValidRelationshipDao;

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
  public SampleClass get(long sampleClassId) throws IOException {
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
  public SampleClass inferParentFromChild(long childClassId, String childCategory, String parentCategory) {
    SampleClass childClass = getNotNullClass(childClassId);
    if (!childClass.getSampleCategory().equals(childCategory)) {
      throw new IllegalArgumentException(
          String.format("Sample class %s is not a valid %s class.", childClassId, childCategory));
    }
    List<SampleClass> parentClasses = sampleValidRelationshipDao.getSampleValidRelationship().stream()
        .filter(relationship -> !relationship.getArchived() && !relationship.getParent().isArchived()
            && relationship.getChild().getId() == childClass.getId()
            && relationship.getParent().getSampleCategory().equals(parentCategory))
        .map(SampleValidRelationship::getParent).collect(Collectors.toList());
    return singleResult(parentClasses, childClass, parentCategory);
  }

  @Override
  public SampleClass getRequiredTissueProcessingClass(Long childClassId) throws IOException {
    SampleClass stockClass = getNotNullClass(childClassId);
    List<SampleValidRelationship> relationships = sampleValidRelationshipDao.getSampleValidRelationship();
    if (relationships.stream().anyMatch(relationship -> !relationship.getArchived()
        && relationship.getChild().getId() == childClassId
        && relationship.getParent().getSampleCategory().equals(SampleTissue.CATEGORY_NAME))) {
      return null;
    }
    List<SampleClass> parentClasses = relationships.stream().filter(relationship -> !relationship.getArchived()
        && relationship.getChild().getId() == childClassId
        && relationship.getParent().getSampleCategory().equals(SampleTissueProcessing.CATEGORY_NAME))
        .map(SampleValidRelationship::getParent)
        .collect(Collectors.toList());
    return singleResult(parentClasses, stockClass, SampleTissueProcessing.CATEGORY_NAME);
  }

  private SampleClass getNotNullClass(Long sampleClassId) {
    if (sampleClassId == null) {
      throw new NullPointerException("Class ID not provided");
    }
    SampleClass sampleClass = sampleClassDao.getSampleClass(sampleClassId);
    if (sampleClass == null) {
      throw new IllegalArgumentException("Invalid sample class " + sampleClassId);
    }
    return sampleClass;
  }

  private SampleClass singleResult(Collection<SampleClass> classes, SampleClass child, String parentCategory) {
    switch (classes.size()) {
    case 0:
      return null;
    case 1:
      return classes.iterator().next();
    default:
      throw new IllegalStateException(
          String.format("SampleClass %s has multiple %s parents.", child.getAlias(), parentCategory));
    }
  }

}
