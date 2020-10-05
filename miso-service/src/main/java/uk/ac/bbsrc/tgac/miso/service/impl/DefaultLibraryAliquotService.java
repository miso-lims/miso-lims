package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.eaglegenomics.simlims.core.User;
import com.google.common.annotations.VisibleForTesting;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchyEntity;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAliquotStore;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryAliquotService implements LibraryAliquotService, PaginatedDataSource<LibraryAliquot> {

  @Autowired
  private LibraryAliquotStore libraryAliquotDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private NamingSchemeHolder namingSchemeHolder;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;
  @Autowired
  private TargetedSequencingService targetedSequencingService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private WorksetService worksetService;
  @Autowired
  private LibraryStore libraryStore;
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public LibraryAliquot get(long id) throws IOException {
    LibraryAliquot aliquot = libraryAliquotDao.get(id);
    return aliquot;
  }

  private LibraryAliquot save(LibraryAliquot aliquot) throws IOException {
    try {
      NamingScheme namingScheme = getNamingScheme(aliquot);
      if (!hasTemporaryAlias(aliquot)) {
        validateAlias(aliquot, namingScheme);
      }
      Long newId = libraryAliquotDao.save(aliquot);
      LibraryAliquot managed = libraryAliquotDao.get(newId);

      // post-save field generation
      boolean needsUpdate = false;
      if (hasTemporaryName(managed)) {
        try {
          managed.setName(namingScheme.generateNameFor(managed));
        } catch (MisoNamingException e) {
          throw new ValidationException(new ValidationError("name", e.getMessage()));
        }
        validateNameOrThrow(managed, namingScheme);
        needsUpdate = true;
      }
      if (hasTemporaryAlias(managed)) {
        try {
          managed.setAlias(namingScheme.generateLibraryAliquotAlias(managed));
        } catch (MisoNamingException e) {
          throw new ValidationException(new ValidationError("name", e.getMessage()));
        }
        if (isDetailedLibraryAliquot(managed)) {
          // generation of non-standard aliases is allowed
          ((DetailedLibraryAliquot) managed).setNonStandardAlias(!namingScheme.validateLibraryAliquotAlias(managed.getAlias()).isValid());
        } else {
          validateAlias(managed, namingScheme);
        }
        needsUpdate = true;
      }
      if (autoGenerateIdBarcodes && isStringEmptyOrNull(managed.getIdentificationBarcode())) {
        // if !autoGenerateIdBarcodes then the identificationBarcode is set by the user
        generateAndSetIdBarcode(managed);
        needsUpdate = true;
      }
      if (needsUpdate) libraryAliquotDao.save(managed);
      return managed;
    } catch (ConstraintViolationException e) {
      // Send the nested root cause message to the user, since it contains the actual error.
      throw new ConstraintViolationException(e.getMessage() + " " + ExceptionUtils.getRootCauseMessage(e), e.getSQLException(),
          e.getConstraintName());
    }
  }

  private void validateAlias(LibraryAliquot aliquot, NamingScheme namingScheme) {
    if (!isDetailedLibraryAliquot(aliquot) || !((DetailedLibraryAliquot) aliquot).isNonStandardAlias()) {
      uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult aliasValidation = namingScheme
          .validateLibraryAliquotAlias(aliquot.getAlias());
      if (!aliasValidation.isValid()) {
        throw new ValidationException(new ValidationError("alias", aliasValidation.getMessage()));
      }
    }
  }

  @Override
  public long create(LibraryAliquot aliquot) throws IOException {
    loadChildEntities(aliquot);
    User changeUser = authorizationManager.getCurrentUser();
    aliquot.setCreator(changeUser);
    boxService.throwIfBoxPositionIsFilled(aliquot);

    if (aliquot.getConcentration() == null) {
      aliquot.setConcentrationUnits(null);
    }
    if (aliquot.getVolume() == null) {
      aliquot.setVolumeUnits(null);
    }

    LimsUtils.updateParentVolume(aliquot, null, changeUser);

    aliquot.setChangeDetails(authorizationManager.getCurrentUser());

    // pre-save field generation
    aliquot.setName(generateTemporaryName());
    NamingScheme namingScheme = getNamingScheme(aliquot);
    if (isStringEmptyOrNull(aliquot.getAlias()) && namingScheme.hasLibraryAliquotAliasGenerator()) {
      aliquot.setAlias(generateTemporaryName());
    }
    validateChange(aliquot, null);
    long savedId = save(aliquot).getId();
    updateParent(aliquot.getParent());
    boxService.updateBoxableLocation(aliquot);
    return savedId;
  }

  private NamingScheme getNamingScheme(LibraryAliquot aliquot) {
    return namingSchemeHolder.get(aliquot.getLibrary().getSample().getProject().isSecondaryNaming());
  }

  private void updateParent(HierarchyEntity parent) throws IOException {
    try {
      if (parent instanceof LibraryAliquot) {
        libraryAliquotDao.save((LibraryAliquot) parent);
      } else {
        libraryStore.save((Library) parent);
      }
    } catch (ValidationException e) {
      throw ValidationUtils.rewriteParentErrors(e);
    }
  }

  @Override
  public long update(LibraryAliquot aliquot) throws IOException {
    LibraryAliquot managed = get(aliquot.getId());
    User changeUser = authorizationManager.getCurrentUser();
    managed.setChangeDetails(changeUser);
    maybeRemoveFromBox(aliquot, managed);
    boxService.throwIfBoxPositionIsFilled(aliquot);

    loadChildEntities(aliquot);
    LimsUtils.updateParentVolume(aliquot, managed, changeUser);
    validateChange(aliquot, managed);
    applyChanges(managed, aliquot);
    LibraryAliquot saved = save(managed);
    updateParent(aliquot.getParent());
    boxService.updateBoxableLocation(aliquot);
    return saved.getId();
  }

  @Override
  public int count() throws IOException {
    return libraryAliquotDao.count();
  }

  @Override
  public List<LibraryAliquot> list() throws IOException {
    return libraryAliquotDao.listAll();
  }

  @Override
  public List<LibraryAliquot> listByLibraryId(Long libraryId) throws IOException {
    return libraryAliquotDao.listByLibraryId(libraryId);
  }

  @Override
  public LibraryAliquot getByBarcode(String barcode) throws IOException {
    return libraryAliquotDao.getByBarcode(barcode);
  }

  /**
   * Loads persisted objects into LibraryAliquot fields. Should be called before saving LibraryAliquots. Loads all member objects
   * <b>except</b>
   * <ul>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param aliquot the LibraryAliquot to load entities into. Must contain at least the IDs of objects to load (e.g. to load the
   *          persisted Library into aliquot.library, aliquot.library.id must be set)
   * @throws IOException
   */
  private void loadChildEntities(LibraryAliquot aliquot) throws IOException {
    if (aliquot.getLibrary() != null) {
      aliquot.setLibrary(libraryService.get(aliquot.getLibrary().getId()));
    }
    if (aliquot.getParentAliquot() != null) {
      aliquot.setParentAliquot(get(aliquot.getParentAliquot().getId()));
    }
    if (aliquot.getTargetedSequencing() != null) {
      aliquot.setTargetedSequencing(targetedSequencingService.get(aliquot.getTargetedSequencing().getId()));
    }

    if (isDetailedLibraryAliquot(aliquot)) {
      DetailedLibraryAliquot detailed = (DetailedLibraryAliquot) aliquot;
      if (detailed.getLibraryDesignCode() != null) {
        detailed.setLibraryDesignCode(libraryDesignCodeService.get(detailed.getLibraryDesignCode().getId()));
      }
    }
  }

  private void maybeRemoveFromBox(LibraryAliquot aliquot, LibraryAliquot managed) {
    if (aliquot.isDiscarded() || aliquot.getDistributionTransfer() != null || managed.getDistributionTransfer() != null) {
      aliquot.setBoxPosition(null);
      aliquot.setVolume(BigDecimal.ZERO);
    }
  }

  /**
   * Copies modifiable fields from the source LibraryAliquot into the target LibraryAliquot to be persisted
   * 
   * @param target the persisted LibraryAliquot to modify
   * @param source the modified LibraryAliquot to copy modifiable fields from
   * @throws IOException
   */
  private void applyChanges(LibraryAliquot target, LibraryAliquot source) {
    target.setAlias(source.getAlias());
    target.setTargetedSequencing(source.getTargetedSequencing());
    target.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(source.getIdentificationBarcode()));
    target.setDiscarded(source.isDiscarded());
    target.setVolume(source.getVolume());
    target.setVolumeUnits(source.getVolume() == null ? null : source.getVolumeUnits());
    target.setDnaSize(source.getDnaSize());
    target.setConcentration(source.getConcentration());
    target.setConcentrationUnits(target.getConcentration() == null ? null : source.getConcentrationUnits());
    target.setNgUsed(source.getNgUsed());
    target.setVolumeUsed(source.getVolumeUsed());
    target.setCreationDate(source.getCreationDate());
    target.setDetailedQcStatus(source.getDetailedQcStatus());
    target.setDetailedQcStatusNote(source.getDetailedQcStatusNote());

    if (isDetailedLibraryAliquot(target)) {
      DetailedLibraryAliquot dTarget = (DetailedLibraryAliquot) target;
      DetailedLibraryAliquot dSource = (DetailedLibraryAliquot) source;
      dTarget.setLibraryDesignCode(dSource.getLibraryDesignCode());
      dTarget.setGroupId(dSource.getGroupId());
      dTarget.setGroupDescription(dSource.getGroupDescription());
    }
  }

  private void validateChange(LibraryAliquot aliquot, LibraryAliquot beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    validateConcentrationUnits(aliquot.getConcentration(), aliquot.getConcentrationUnits(), errors);
    validateVolumeUnits(aliquot.getVolume(), aliquot.getVolumeUnits(), errors);
    validateBarcodeUniqueness(aliquot, beforeChange, libraryAliquotDao::getByBarcode, errors, "library aliquot");
    validateTargetedSequencing(aliquot, errors);
    validateUnboxableFields(aliquot, errors);
    validateDetailedQcStatus(aliquot, errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void validateTargetedSequencing(LibraryAliquot aliquot, List<ValidationError> errors) {
    TargetedSequencing ts = aliquot.getTargetedSequencing();
    Library library = aliquot.getLibrary();

    if (ts == null) {
      if (isTargetedSequencingRequired(library)) {
        errors.add(new ValidationError("targetedSequencingId", "Value is required (based on library design code)"));
      }
    } else if (!isTargetedSequencingCompatible(ts, library)) {
      errors.add(new ValidationError("targetedSequencingId", "Selected value not compatible with the library kit"));
    }
  }

  private boolean isTargetedSequencingRequired(Library library) {
    return LimsUtils.isDetailedLibrary(library) && ((DetailedLibrary) library).getLibraryDesignCode().isTargetedSequencingRequired();
  }

  @VisibleForTesting
  protected boolean isTargetedSequencingCompatible(TargetedSequencing ts, Library library) {
    return library.getKitDescriptor().getTargetedSequencing().contains(ts);
  }

  public void setLibraryAliquotDao(LibraryAliquotStore libraryAliquotDao) {
    this.libraryAliquotDao = libraryAliquotDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setNamingSchemeHolder(NamingSchemeHolder namingSchemeHolder) {
    this.namingSchemeHolder = namingSchemeHolder;
  }

  public void setTargetedSequencingService(TargetedSequencingService targetedSequencingService) {
    this.targetedSequencingService = targetedSequencingService;
  }

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  public void setAutoGenerateIdBarcodes(Boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setWorksetService(WorksetService worksetService) {
    this.worksetService = worksetService;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<LibraryAliquot> listByIdList(List<Long> idList) throws IOException {
    return libraryAliquotDao.listByIdList(idList);

  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void authorizeDeletion(LibraryAliquot object) throws IOException {
    User creator = object.getCreator();
    authorizationManager.throwIfNonAdminOrMatchingOwner(creator);
  }

  @Override
  public ValidationResult validateDeletion(LibraryAliquot object) throws IOException {
    ValidationResult result = new ValidationResult();

    int poolCount = poolService.listByLibraryAliquotId(object.getId()).size();
    if (poolCount > 0) {
      result.addError(
          new ValidationError(String.format("%s is included in %d %s", object.getName(), poolCount, Pluralizer.pools(poolCount))));
    }

    return result;
  }

  @Override
  public void beforeDelete(LibraryAliquot object) throws IOException {
    List<Workset> worksets = worksetService.listByLibraryAliquot(object.getId());
    for (Workset workset : worksets) {
      worksetService.removeLibraryAliquots(workset, Collections.singleton(object));
    }
    Box box = object.getBox();
    if (box != null) {
      box.getBoxPositions().remove(object.getBoxPosition());
      boxService.save(box);
    }
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return libraryAliquotDao.count(errorHandler, filter);
  }

  @Override
  public List<LibraryAliquot> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return libraryAliquotDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

}
