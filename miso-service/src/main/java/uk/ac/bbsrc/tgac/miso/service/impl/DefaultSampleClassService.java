package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleClassService extends AbstractSaveService<SampleClass> implements SampleClassService {

  @Autowired
  private SampleClassDao sampleClassDao;
  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;
  @Autowired
  private SampleTypeService sampleTypeService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setSampleClassDao(SampleClassDao sampleClassDao) {
    this.sampleClassDao = sampleClassDao;
  }

  @Override
  public List<SampleClass> listByCategory(String sampleCategory) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleClassDao.listByCategory(sampleCategory);
  }

  @Override
  public SampleClass inferParentFromChild(long childClassId, String childCategory, String parentCategory)
      throws IOException {
    SampleClass childClass = getNotNullClass(childClassId);
    if (!childClass.getSampleCategory().equals(childCategory)) {
      throw new IllegalArgumentException(
          String.format("Sample class %s is not a valid %s class.", childClassId, childCategory));
    }
    List<SampleClass> parentClasses = sampleValidRelationshipService.getAll().stream()
        .filter(relationship -> !relationship.isArchived() && !relationship.getParent().isArchived()
            && relationship.getChild().getId() == childClass.getId()
            && relationship.getParent().getSampleCategory().equals(parentCategory))
        .map(SampleValidRelationship::getParent).collect(Collectors.toList());
    return singleResult(parentClasses, childClass, parentCategory);
  }

  @Override
  public SampleClass getRequiredTissueProcessingClass(Long childClassId) throws IOException {
    SampleClass stockClass = getNotNullClass(childClassId);
    Set<SampleValidRelationship> relationships = sampleValidRelationshipService.getAll();
    if (relationships.stream().anyMatch(relationship -> !relationship.isArchived()
        && relationship.getChild().getId() == childClassId
        && relationship.getParent().getSampleCategory().equals(SampleTissue.CATEGORY_NAME))) {
      return null;
    }
    List<SampleClass> parentClasses = relationships.stream().filter(relationship -> !relationship.isArchived()
        && relationship.getChild().getId() == childClassId
        && relationship.getParent().getSampleCategory().equals(SampleTissueProcessing.CATEGORY_NAME))
        .map(SampleValidRelationship::getParent)
        .collect(Collectors.toList());
    return singleResult(parentClasses, stockClass, SampleTissueProcessing.CATEGORY_NAME);
  }

  private SampleClass getNotNullClass(Long sampleClassId) throws IOException {
    if (sampleClassId == null) {
      throw new NullPointerException("Class ID not provided");
    }
    SampleClass sampleClass = sampleClassDao.get(sampleClassId);
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

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<SampleClass> list() throws IOException {
    return sampleClassDao.list();
  }

  @Override
  public SaveDao<SampleClass> getDao() {
    return sampleClassDao;
  }

  @Override
  protected void authorizeUpdate(SampleClass object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void loadChildEntities(SampleClass object) throws IOException {
    Set<SampleValidRelationship> parents = new HashSet<>();
    for (SampleValidRelationship svr : object.getParentRelationships()) {
      SampleValidRelationship loaded = null;
      if (svr.isSaved()) {
        loaded = sampleValidRelationshipService.get(svr.getId());
        if (loaded == null) {
          throw new ValidationException(new ValidationError("No parent relationship found with ID: " + svr.getId()));
        }
        loaded.setArchived(svr.isArchived());
      } else {
        loaded = svr;
        loaded.setChild(object);
        SampleClass parent = loaded.getParent() == null ? null : get(loaded.getParent().getId());
        if (parent == null) {
          throw new ValidationException(
              String.format("Parent sample class not found%s%s", loaded.getParent() == null ? "" : " with ID: ",
                  loaded.getParent() == null ? "" : loaded.getParent().getId()));
        }
        loaded.setParent(parent);
      }
      parents.add(loaded);
    }
    object.getParentRelationships().clear();
    object.getParentRelationships().addAll(parents);
    if (object.getDefaultSampleType() != null) {
      object.setDefaultSampleType(sampleTypeService.getByName(object.getDefaultSampleType().getName()));
    }
  }

  @Override
  protected void collectValidationErrors(SampleClass object, SampleClass beforeChange, List<ValidationError> errors)
      throws IOException {
    long usage = beforeChange == null ? 0L : sampleClassDao.getUsage(beforeChange);
    if (beforeChange == null && SampleIdentity.CATEGORY_NAME.equals(object.getSampleCategory())
        && list().stream()
            .anyMatch(sampleClass -> SampleIdentity.CATEGORY_NAME.equals(sampleClass.getSampleCategory()))) {
      errors.add(new ValidationError("sampleCategory", "There can be only one identity class"));
    }
    if (usage > 0L) {
      if (ValidationUtils.isSetAndChanged(SampleClass::getSampleCategory, object, beforeChange)) {
        errors.add(new ValidationError("sampleCategory",
            String.format("Cannot change because this sample class is already used by %d samples", usage)));
      }
    }
    if (!SampleClass.CATEGORIES.contains(object.getSampleCategory())) {
      errors.add(new ValidationError("sampleCategory", "Invalid category"));
    } else if (object.getSampleSubcategory() != null
        && !SampleClass.SUBCATEGORIES.get(object.getSampleCategory()).contains(object.getSampleSubcategory())) {
      errors.add(new ValidationError("sampleSubcategory", "Invalid subcategory"));
    }
    if (beforeChange == null && !object.getChildRelationships().isEmpty()) {
      errors.add(new ValidationError("Cannot specify child relationships for new sample class"));
    }
    validateParentRelationships(object, errors);
    if (object.isArchived()) {
      if (object.getParentRelationships().stream().anyMatch(relationship -> relationship.isArchived())) {
        errors.add(new ValidationError("archived", "Cannot archive class due to unarchived parent relationships"));
      }
      if (object.getChildRelationships().stream().anyMatch(relationship -> relationship.isArchived())) {
        errors.add(new ValidationError("archived", "Cannot archive class due to unarchived child relationships"));
      }
    }
  }

  private void validateParentRelationships(SampleClass sampleClass, List<ValidationError> errors) {
    Set<String> allowedParents = null;
    switch (sampleClass.getSampleCategory()) {
      case SampleIdentity.CATEGORY_NAME:
        allowedParents = Collections.emptySet();
        break;
      case SampleTissue.CATEGORY_NAME:
        allowedParents = Sets.newHashSet(SampleIdentity.CATEGORY_NAME, SampleTissue.CATEGORY_NAME);
        validateSingleParentRequirement(sampleClass, SampleIdentity.CATEGORY_NAME, errors);
        break;
      case SampleTissueProcessing.CATEGORY_NAME:
        allowedParents = Sets.newHashSet(SampleTissue.CATEGORY_NAME, SampleTissueProcessing.CATEGORY_NAME);
        long tissueParents = sampleClass.getParentRelationships().stream()
            .filter(relationship -> SampleTissue.CATEGORY_NAME.equals(relationship.getParent().getSampleCategory())
                && !relationship.isArchived())
            .count();
        if (tissueParents > 1L) {
          errors.add(new ValidationError("parentRelationships",
              String.format("%s classes cannot have multiple %s parents", SampleTissueProcessing.CATEGORY_NAME,
                  SampleTissue.CATEGORY_NAME)));
        } else if (tissueParents == 0L && !hasPathToIdentity(sampleClass)) {
          errors.add(new ValidationError("parentRelationships",
              String.format("Must have a direct or indirect link to the %s class", SampleIdentity.CATEGORY_NAME)));
        }
        break;
      case SampleStock.CATEGORY_NAME:
        allowedParents = Sets.newHashSet(SampleTissue.CATEGORY_NAME, SampleTissueProcessing.CATEGORY_NAME,
            SampleStock.CATEGORY_NAME);
        if (SampleStockSingleCell.SUBCATEGORY_NAME.equals(sampleClass.getSampleSubcategory())) {
          // Single Cell Stocks need to be parented to a Single Cell (tissue processing) class instead of a
          // tissue class
          if (sampleClass.getParentRelationships().stream()
              .filter(relationship -> SampleTissueProcessing.CATEGORY_NAME
                  .equals(relationship.getParent().getSampleCategory())
                  && SampleSingleCell.SUBCATEGORY_NAME.equals(relationship.getParent().getSampleSubcategory())
                  && !relationship.isArchived())
              .count() != 1L) {
            errors.add(makeSingleParentRequirementError(sampleClass.getSampleSubcategory(),
                SampleSingleCell.SUBCATEGORY_NAME));
          }
        } else {
          validateSingleParentRequirement(sampleClass, SampleTissue.CATEGORY_NAME, errors);
        }
        break;
      case SampleAliquot.CATEGORY_NAME:
        allowedParents = Sets.newHashSet(SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME);
        validateSingleParentRequirement(sampleClass, SampleStock.CATEGORY_NAME, errors);
        break;
      default:
        throw new IllegalArgumentException("Unhandled sample category: " + sampleClass.getSampleCategory());
    }
    for (SampleValidRelationship relationship : sampleClass.getParentRelationships()) {
      if (!allowedParents.contains(relationship.getParent().getSampleCategory())) {
        errors.add(new ValidationError("parentRelationships", String.format("%s (%s) cannot be a parent of %s classes",
            relationship.getParent().getAlias(), relationship.getParent().getSampleCategory(),
            sampleClass.getSampleCategory())));
      }
    }
    List<SampleClass> parents = sampleClass.getParentRelationships().stream().map(SampleValidRelationship::getParent)
        .collect(Collectors.toList());
    for (int i = 0; i < parents.size(); i++) {
      for (int j = i + 1; j < parents.size(); j++) {
        if (parents.get(i).getId() == parents.get(j).getId()) {
          errors.add(new ValidationError("parentRelationships",
              String.format("Cannot have multiple parent relationships to the same sample class (%s)",
                  parents.get(i).getAlias())));
          break;
        }
      }
    }
  }

  private boolean hasPathToIdentity(SampleClass sampleClass) {
    return hasPathToIdentity(sampleClass, new HashSet<>());
  }

  private boolean hasPathToIdentity(SampleClass sampleClass, Set<Long> checkedSampleClassIds) {
    checkedSampleClassIds.add(sampleClass.getId());
    if (SampleIdentity.CATEGORY_NAME.equals(sampleClass.getSampleCategory())) {
      return true;
    }
    for (SampleValidRelationship parentRelationship : sampleClass.getParentRelationships()) {
      if (checkedSampleClassIds.contains(parentRelationship.getParent().getId())) {
        continue;
      }
      if (hasPathToIdentity(parentRelationship.getParent())) {
        return true;
      }
    }
    return false;
  }

  private void validateSingleParentRequirement(SampleClass sampleClass, String requiredParentCategory,
      List<ValidationError> errors) {
    if (sampleClass.getParentRelationships().stream()
        .filter(relationship -> requiredParentCategory.equals(relationship.getParent().getSampleCategory())
            && !relationship.isArchived())
        .count() != 1L) {
      errors.add(makeSingleParentRequirementError(sampleClass.getSampleCategory(), requiredParentCategory));
    }
  }

  private ValidationError makeSingleParentRequirementError(String childCategory, String parentCategory) {
    return new ValidationError("parentRelationships",
        String.format("%s classes must be parented to a single %s class", childCategory, parentCategory));
  }

  @Override
  protected void applyChanges(SampleClass to, SampleClass from) throws IOException {
    to.setAlias(from.getAlias());
    to.setDirectCreationAllowed(from.isDirectCreationAllowed());
    to.setArchived(from.isArchived());
    to.setSampleCategory(from.getSampleCategory());
    to.setSampleSubcategory(from.getSampleSubcategory());
    to.setSuffix(from.getSuffix());
    to.setV2NamingCode(from.getV2NamingCode());
    to.setDefaultSampleType(from.getDefaultSampleType());

    User currentUser = authorizationManager.getCurrentUser();
    for (Iterator<SampleValidRelationship> iterator = to.getParentRelationships().iterator(); iterator.hasNext();) {
      final SampleValidRelationship toRelationship = iterator.next();
      SampleValidRelationship fromRelationship = from.getParentRelationships().stream()
          .filter(relationship -> relationship.getId() == toRelationship.getId()).findFirst().orElse(null);
      if (fromRelationship == null) {
        // relationship removed
        iterator.remove();
        sampleValidRelationshipService.delete(toRelationship);
      } else if (fromRelationship.isArchived() != toRelationship.isArchived()) {
        // relationship changed
        toRelationship.setArchived(fromRelationship.isArchived());
        toRelationship.setChangeDetails(currentUser);
      }
    }
    for (SampleValidRelationship fromRelationship : from.getParentRelationships()) {
      if (!fromRelationship.isSaved()) {
        // relationship added
        to.getParentRelationships().add(fromRelationship);
        fromRelationship.setChild(to);
        fromRelationship.setChangeDetails(currentUser);
      }
    }
  }

  @Override
  protected void beforeSave(SampleClass object) throws IOException {
    User user = authorizationManager.getCurrentUser();
    object.setChangeDetails(user);
    object.getParentRelationships().forEach(relationship -> relationship.setChangeDetails(user));
  }

  @Override
  public ValidationResult validateDeletion(SampleClass object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = sampleClassDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }
    return result;
  }

}
