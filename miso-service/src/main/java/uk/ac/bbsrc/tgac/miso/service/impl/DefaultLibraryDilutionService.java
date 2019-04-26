package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.annotations.VisibleForTesting;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryDilutionService
    implements LibraryDilutionService, PaginatedDataSource<LibraryDilution> {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleService.class);

  @Autowired
  private LibraryDilutionStore dilutionDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private TargetedSequencingService targetedSequencingService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private WorksetService worksetService;
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Override
  public LibraryDilution get(long dilutionId) throws IOException {
    LibraryDilution dilution = dilutionDao.get(dilutionId);
    return dilution;
  }

  private LibraryDilution save(LibraryDilution dilution) throws IOException {
    dilution.setLastModifier(authorizationManager.getCurrentUser());
    try {
      Long newId = dilutionDao.save(dilution);
      LibraryDilution managed = dilutionDao.get(newId);

      // post-save field generation
      boolean needsUpdate = false;
      if (hasTemporaryName(dilution)) {
        managed.setName(namingScheme.generateNameFor(managed));
        validateNameOrThrow(managed, namingScheme);
        needsUpdate = true;
      }
      if (autoGenerateIdBarcodes && isStringEmptyOrNull(managed.getIdentificationBarcode())) {
        // if !autoGenerateIdBarcodes then the identificationBarcode is set by the user
        generateAndSetIdBarcode(managed);
        needsUpdate = true;
      }
      if (needsUpdate) dilutionDao.save(managed);
      return managed;
    } catch (MisoNamingException e) {
      throw new IllegalArgumentException("Name generator failed to generate valid name for library");
    } catch (ConstraintViolationException e) {
      // Send the nested root cause message to the user, since it contains the actual error.
      throw new ConstraintViolationException(e.getMessage() + " " + ExceptionUtils.getRootCauseMessage(e), e.getSQLException(),
          e.getConstraintName());
    }
  }

  @Override
  public Long create(LibraryDilution dilution) throws IOException {
    loadChildEntities(dilution);
    dilution.setCreator(authorizationManager.getCurrentUser());
    boxService.throwIfBoxPositionIsFilled(dilution);

    if (dilution.getConcentration() == null) {
      dilution.setConcentrationUnits(null);
    }
    if (dilution.getVolume() == null) {
      dilution.setVolumeUnits(null);
    }

    Library library = dilution.getLibrary();
    if (dilution.getVolumeUsed() != null && library.getVolume() != null) {
      library.setVolume(library.getVolume() - dilution.getVolumeUsed());
    }

    dilution.setChangeDetails(authorizationManager.getCurrentUser());

    // pre-save field generation
    dilution.setName(generateTemporaryName());
    validateChange(dilution, null);
    long savedId = save(dilution).getId();
    updateLibrary(library);
    boxService.updateBoxableLocation(dilution);
    return savedId;
  }

  private void updateLibrary(Library library) throws IOException {
    try {
      libraryService.update(library);
    } catch (ValidationException e) {
      List<ValidationError> newErrors = new ArrayList<>();
      for (ValidationError error : e.getErrors()) {
        newErrors.add(new ValidationError(String.format("Library %s: %s", error.getProperty(), error.getMessage())));
      }
      throw new ValidationException(newErrors);
    }
  }

  @Override
  public void update(LibraryDilution dilution) throws IOException {
    LibraryDilution managed = get(dilution.getId());
    boxService.throwIfBoxPositionIsFilled(dilution);

    loadChildEntities(dilution);
    Library library = dilution.getLibrary();
    if (library.getVolume() != null) {
      if (dilution.getVolumeUsed() != null && managed.getVolumeUsed() != null) {
        library.setVolume(library.getVolume() + managed.getVolumeUsed() - dilution.getVolumeUsed());
      } else if (managed.getVolumeUsed() != null) {
        library.setVolume(library.getVolume() + managed.getVolumeUsed());
      } else if (dilution.getVolumeUsed() != null) {
        library.setVolume(library.getVolume() - dilution.getVolumeUsed());
      }
    }
    validateChange(dilution, managed);
    applyChanges(managed, dilution);
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    save(managed);
    updateLibrary(library);
    boxService.updateBoxableLocation(dilution);
  }

  @Override
  public int count() throws IOException {
    return dilutionDao.count();
  }

  @Override
  public List<LibraryDilution> list() throws IOException {
    return dilutionDao.listAll();
  }

  @Override
  public List<LibraryDilution> listByLibraryId(Long libraryId) throws IOException {
    return dilutionDao.listByLibraryId(libraryId);
  }

  @Override
  public LibraryDilution getByBarcode(String barcode) throws IOException {
    return dilutionDao.getByBarcode(barcode);
  }

  /**
   * Loads persisted objects into LibraryDilution fields. Should be called before saving LibraryDilutions. Loads all member objects
   * <b>except</b>
   * <ul>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param libraryDilution the LibraryDilution to load entities into. Must contain at least the IDs of objects to load (e.g. to load the
   *          persisted Library
   *          into dilution.library, dilution.library.id must be set)
   * @throws IOException
   */
  private void loadChildEntities(LibraryDilution dilution) throws IOException {
    if (dilution.getLibrary() != null) {
      dilution.setLibrary(libraryService.get(dilution.getLibrary().getId()));
    }
    if (dilution.getTargetedSequencing() != null) {
      dilution.setTargetedSequencing(targetedSequencingService.get(dilution.getTargetedSequencing().getId()));
    }
  }

  /**
   * Copies modifiable fields from the source LibraryDilution into the target LibraryDilution to be persisted
   * 
   * @param target the persisted LibraryDilution to modify
   * @param source the modified LibraryDilution to copy modifiable fields from
   * @throws IOException
   */
  private void applyChanges(LibraryDilution target, LibraryDilution source) {
    target.setTargetedSequencing(source.getTargetedSequencing());
    target.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(source.getIdentificationBarcode()));
    target.setVolume(source.getVolume());
    target.setVolumeUnits(target.getVolume() == null ? null : source.getVolumeUnits());
    target.setConcentration(source.getConcentration());
    target.setConcentrationUnits(target.getConcentration() == null ? null : source.getConcentrationUnits());
    target.setNgUsed(source.getNgUsed());
    target.setVolumeUsed(source.getVolumeUsed());
  }

  private void validateChange(LibraryDilution dilution, LibraryDilution beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    validateConcentrationUnits(dilution.getConcentration(), dilution.getConcentrationUnits(), errors);
    validateVolumeUnits(dilution.getVolume(), dilution.getVolumeUnits(), errors);
    validateBarcodeUniqueness(dilution, beforeChange, dilutionDao::getByBarcode, errors, "dilution");
    validateTargetedSequencing(dilution, errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void validateTargetedSequencing(LibraryDilution dilution, List<ValidationError> errors) {
    TargetedSequencing ts = dilution.getTargetedSequencing();
    Library library = dilution.getLibrary();

    if (ts == null) {
      if (isTargetedSequencingRequired(library)) {
        errors.add(new ValidationError("targetedSequencingId", "Value is required (based on library design code)"));
      }
    } else {
      if (!isTargetedSequencingCompatible(ts, library)) {
        errors.add(new ValidationError("targetedSequencingId", "Selected value not compatible with the library kit"));
      }
    }
  }

  private boolean isTargetedSequencingRequired(Library library) {
    return LimsUtils.isDetailedLibrary(library)
        && ((DetailedLibrary) library).getLibraryDesignCode().isTargetedSequencingRequired();
  }

  @VisibleForTesting
  protected boolean isTargetedSequencingCompatible(TargetedSequencing ts, Library library) {
    return library.getKitDescriptor().getTargetedSequencing().contains(ts);
  }

  public void setDilutionDao(LibraryDilutionStore dilutionDao) {
    this.dilutionDao = dilutionDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
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
  public List<LibraryDilution> listByIdList(List<Long> idList) throws IOException {
    return dilutionDao.listByIdList(idList);

  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void authorizeDeletion(LibraryDilution object) throws IOException {
    User creator = object.getCreator();
    authorizationManager.throwIfNonAdminOrMatchingOwner(creator);
  }

  @Override
  public ValidationResult validateDeletion(LibraryDilution object) {
    ValidationResult result = new ValidationResult();

    if (object.getPools() != null && !object.getPools().isEmpty()) {
      result.addError(new ValidationError(object.getName() + " is included in " + object.getPools().size() + " pool"
          + (object.getPools().size() > 1 ? "s" : "")));
    }

    return result;
  }

  @Override
  public void beforeDelete(LibraryDilution object) throws IOException {
    List<Workset> worksets = worksetService.listByDilution(object.getId());
    for (Workset workset : worksets) {
      workset.getDilutions().removeIf(ldi -> ldi.getId() == object.getId());
      worksetService.save(workset);
    }
    Box box = object.getBox();
    if (box != null) {
      box.getBoxPositions().remove(object.getBoxPosition());
      boxService.save(box);
    }
  }

  @Override
  public void afterDelete(LibraryDilution object) throws IOException {
    LibraryChangeLog changeLog = new LibraryChangeLog();
    changeLog.setLibrary(object.getLibrary());
    changeLog.setColumnsChanged(object.getName());
    changeLog.setSummary("Deleted dilution " + object.getName() + ".");
    changeLog.setTime(new Date());
    changeLog.setUser(authorizationManager.getCurrentUser());
    changeLogService.create(changeLog);
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return dilutionDao.count(errorHandler, filter);
  }

  @Override
  public List<LibraryDilution> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return dilutionDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}
