package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignService;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySelectionService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySpikeInService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryStrategyService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.TransferService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.core.service.WorkstationService;
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
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;
import uk.ac.bbsrc.tgac.miso.persistence.impl.util.HibernateSessionManager;
import uk.ac.bbsrc.tgac.miso.service.HibernateBulkSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryService implements HibernateBulkSaveService<Library>, LibraryService, PaginatedDataSource<Library> {

  protected static final Logger log = LoggerFactory.getLogger(DefaultLibraryService.class);

  @Autowired
  private LibraryStore libraryDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private NamingSchemeHolder namingSchemeHolder;
  @Autowired
  private LibraryTypeService libraryTypeService;
  @Autowired
  private LibrarySelectionService librarySelectionService;
  @Autowired
  private LibraryStrategyService libraryStrategyService;
  @Autowired
  private LibraryDesignService libraryDesignService;
  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;
  @Autowired
  private LibrarySpikeInService librarySpikeInService;
  @Autowired
  private IndexService indexService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private KitDescriptorService kitService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private WorksetService worksetService;
  @Autowired
  private WorkstationService workstationService;
  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private SampleStore sampleStore;
  @Autowired
  private FileAttachmentService fileAttachmentService;
  @Autowired
  private TransferService transferService;
  @Autowired
  private HibernateSessionManager hibernateSessionManager;
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Override
  public Library get(long libraryId) throws IOException {
    return libraryDao.get(libraryId);
  }

  private Library save(Library library, boolean validateAliasUniqueness) throws IOException {
    NamingScheme namingScheme = getNamingScheme(library);
    try {
      if (!hasTemporaryAlias(library)) {
        validateAliasOrThrow(library, namingScheme);
      }
      long newId = libraryDao.save(library);

      Library managed = libraryDao.get(newId);

      // post-save field generation
      boolean needsUpdate = false;
      if (hasTemporaryName(library)) {
        managed.setName(namingScheme.generateNameFor(managed));
        validateNameOrThrow(managed, namingScheme);
        needsUpdate = true;
      }
      if (hasTemporaryAlias(library)) {
        String generatedAlias = namingScheme.generateLibraryAlias(managed);
        managed.setAlias(generatedAlias);
        if (isDetailedLibrary(managed)) {
          // generation of non-standard aliases is allowed
          ((DetailedLibrary) managed).setNonStandardAlias(!namingScheme.validateLibraryAlias(generatedAlias).isValid());
        } else {
          validateAliasOrThrow(managed, namingScheme);
        }
        needsUpdate = true;
      }
      if (autoGenerateIdBarcodes && isStringEmptyOrNull(managed.getIdentificationBarcode())) {
        // if !autoGenerateIdBarcodes then the identificationBarcode is set by the user
        generateAndSetIdBarcode(managed);
        needsUpdate = true;
      }
      if (needsUpdate) {
        libraryDao.save(managed);
      }
      if (validateAliasUniqueness) {
        validateAliasUniqueness(managed, namingScheme);
      }
      return managed;
    } catch (MisoNamingException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    } catch (ConstraintViolationException e) {
      // Send the nested root cause message to the user, since it contains the actual error.
      throw new ConstraintViolationException(e.getMessage() + " " + ExceptionUtils.getRootCauseMessage(e), e.getSQLException(),
          e.getConstraintName());
    }
  }

  private NamingScheme getNamingScheme(Library library) {
    return namingSchemeHolder.get(library.getSample().getProject().isSecondaryNaming());
  }

  @Override
  public long create(Library library) throws IOException {
    if (library.getSample() != null && !library.getSample().isSaved()) {
      Long sampleId = sampleService.create(library.getSample());
      library.getSample().setId(sampleId);
    }
    loadChildEntities(library);
    boxService.throwIfBoxPositionIsFilled(library);
    User changeUser = authorizationManager.getCurrentUser();
    library.setChangeDetails(changeUser);
    validateParentOrThrow(library);

    // pre-save field generation
    library.setName(generateTemporaryName());
    if (isStringEmptyOrNull(library.getAlias()) && getNamingScheme(library).hasLibraryAliasGenerator()) {
      library.setAlias(generateTemporaryName());
    }
    if (library.getConcentration() == null) {
      library.setConcentrationUnits(null);
    }
    if (library.getVolume() == null) {
      library.setVolumeUnits(null);
    } else {
      library.setInitialVolume(library.getVolume());
    }

    LimsUtils.updateParentVolume(library, null, changeUser);

    validateChange(library, null);
    long savedId = save(library, true).getId();
    sampleStore.update(library.getParent());
    boxService.updateBoxableLocation(library);
    if (library.getCreationReceiptInfo() != null) {
      TransferLibrary transferLibrary = library.getCreationReceiptInfo();
      Transfer transfer = transferLibrary.getTransfer();
      Transfer existingTransfer = transferService.listByProperties(transfer.getSenderLab(), transfer.getRecipientGroup(),
          library.getSample().getProject(), transfer.getTransferTime()).stream()
          .max(Comparator.comparing(Transfer::getCreationTime)).orElse(null);
      if (existingTransfer != null) {
        existingTransfer.getLibraryTransfers().add(transferLibrary);
        transferLibrary.setTransfer(existingTransfer);
        transferService.update(existingTransfer);
      } else {
        transferService.create(transfer);
      }
    }
    return savedId;
  }

  @Override
  public long update(Library library) throws IOException {
    Library managed = get(library.getId());
    User changeUser = authorizationManager.getCurrentUser();
    managed.setChangeDetails(changeUser);
    List<Index> originalIndices = new ArrayList<>(managed.getIndices());
    maybeRemoveFromBox(library, managed);
    boxService.throwIfBoxPositionIsFilled(library);
    library.setSample(sampleService.get(library.getSample().getId()));
    LimsUtils.updateParentVolume(library, managed, changeUser);
    boolean validateAliasUniqueness = !managed.getAlias().equals(library.getAlias());
    validateChange(library, managed);
    applyChanges(managed, library);
    loadChildEntities(managed);
    makeChangeLogForIndices(originalIndices, managed.getIndices(), managed);
    Library saved = save(managed, validateAliasUniqueness);
    sampleStore.update(library.getParent());
    boxService.updateBoxableLocation(library);
    return saved.getId();
  }

  @Override
  public int count() throws IOException {
    return libraryDao.count();
  }

  @Override
  public long countBySearch(String querystr) throws IOException {
    return libraryDao.countLibrariesBySearch(querystr);
  }

  @Override
  public List<Library> list() throws IOException {
    return libraryDao.listAll();
  }

  @Override
  public EntityReference getAdjacentLibrary(Library library, boolean before) throws IOException {
    return libraryDao.getAdjacentLibrary(library, before);
  }

  @Override
  public Library getByBarcode(String barcode) throws IOException {
    return libraryDao.getByBarcode(barcode);
  }

  @Override
  public List<Library> listByBarcodeList(List<String> barcodeList) throws IOException {
    return libraryDao.getByBarcodeList(barcodeList);
  }

  @Override
  public List<Library> listByIdList(List<Long> idList) throws IOException {
    return libraryDao.getByIdList(idList);
  }

  @Override
  public Library getByPositionId(long positionId) throws IOException {
    return (Library) libraryDao.getByPositionId(positionId);
  }

  @Override
  public List<Library> listBySearch(String querystr) throws IOException {
    return libraryDao.listBySearch(querystr);
  }

  @Override
  public List<Library> listByAlias(String alias) throws IOException {
    return libraryDao.listByAlias(alias);
  }

  @Override
  public List<Library> searchByCreationDate(Date from, Date to) throws IOException {
    return libraryDao.searchByCreationDate(from, to);
  }

  @Override
  public List<Library> listBySampleId(long sampleId) throws IOException {
    return libraryDao.listBySampleId(sampleId);
  }

  @Override
  public List<Library> listByProjectId(long projectId) throws IOException {
    return libraryDao.listByProjectId(projectId);
  }

  @Override
  public void addNote(Library library, Note note) throws IOException {
    Library managed = libraryDao.get(library.getId());
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    save(managed, false);
  }

  @Override
  public void deleteNote(Library library, Long noteId) throws IOException {
    if (noteId == null) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Library managed = libraryDao.get(library.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getId() == noteId.longValue()) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Library  " + library.getId());
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    libraryDao.save(managed);
  }

  /**
   * Turns indices into strings for easier comparison and changelog message concatentation.
   * 
   * @param indices
   * @return
   */
  private Set<String> stringifyIndices(List<Index> indices) {
    Set<String> original = new HashSet<>();
    for (Index index : indices) {
      if (index != null && index.isSaved()) {
        original.add(index.getFamily().getName() + " - " + index.getLabel());
      }
    }
    return original;
  }

  /**
   * Create a changelog if the indices have changed.
   * 
   * @param originalIndices
   * @param updatedIndices
   * @param target
   * @throws IOException
   */
  private void makeChangeLogForIndices(List<Index> originalIndices, List<Index> updatedIndices, Library target) throws IOException {

    Set<String> original = stringifyIndices(originalIndices);
    Set<String> updated = stringifyIndices(updatedIndices);
    Set<String> added = new TreeSet<>(updated);
    added.removeAll(original);
    Set<String> removed = new TreeSet<>(original);
    removed.removeAll(updated);

    if (!added.isEmpty() || !removed.isEmpty()) {
      StringBuilder message = new StringBuilder();
      message.append("Indices");
      LimsUtils.appendSet(message, removed, "removed");
      LimsUtils.appendSet(message, added, (removed.isEmpty() ? "" : "; ") + "added");

      LibraryChangeLog changeLog = new LibraryChangeLog();
      changeLog.setLibrary(target);
      changeLog.setColumnsChanged("indices");
      changeLog.setSummary(message.toString());
      changeLog.setTime(new Date());
      changeLog.setUser(authorizationManager.getCurrentUser());
      changeLogService.create(changeLog);
    }
  }

  /**
   * Loads persisted objects into library fields. Should be called before saving libraries. Loads all member objects <b>except</b>
   * <ul>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param library the Library to load entities into. Must contain at least the IDs of objects to load (e.g. to load the persisted Sample
   *          into library.sample, library.sample.id must be set)
   * @throws IOException
   */
  private void loadChildEntities(Library library) throws IOException {
    if (library.getSample() != null) {
      library.setSample(sampleService.get(library.getSample().getId()));
    }
    if (library.getLibraryType() != null) {
      library.setLibraryType(libraryTypeService.get(library.getLibraryType().getId()));
    }
    if (library.getLibrarySelectionType() != null) {
      library.setLibrarySelectionType(librarySelectionService.get(library.getLibrarySelectionType().getId()));
    }
    if (library.getLibraryStrategyType() != null) {
      library.setLibraryStrategyType(libraryStrategyService.get(library.getLibraryStrategyType().getId()));
    }
    List<Index> managedIndices = new ArrayList<>();
    for (Index index : library.getIndices()) {
      if (index != null && index.isSaved()) {
        Index managedIndex = indexService.get(index.getId());
        if (managedIndex != null) managedIndices.add(managedIndex);
      }
    }
    library.setIndices(managedIndices);
    if (library.getKitDescriptor() != null) {
      library.setKitDescriptor(kitService.get(library.getKitDescriptor().getId()));
    }
    if (library.getSpikeIn() != null) {
      library.setSpikeIn(librarySpikeInService.get(library.getSpikeIn().getId()));
    }
    if (library.getWorkstation() != null) {
      library.setWorkstation(workstationService.get(library.getWorkstation().getId()));
    }
    if (library.getThermalCycler() != null) {
      library.setThermalCycler(instrumentService.get(library.getThermalCycler().getId()));
    }
    if (isDetailedLibrary(library)) {
      DetailedLibrary lai = (DetailedLibrary) library;
      if (lai.getLibraryDesignCode() != null) {
        lai.setLibraryDesignCode(libraryDesignCodeService.get(lai.getLibraryDesignCode().getId()));
      }
      if (lai.getLibraryDesign() != null) {
        LibraryDesign design = libraryDesignService.get(lai.getLibraryDesign().getId());
        lai.setLibraryDesign(design);
        lai.setLibrarySelectionType(design.getLibrarySelectionType());
        lai.setLibraryStrategyType(design.getLibraryStrategyType());
        lai.setLibraryDesignCode(design.getLibraryDesignCode());
        SampleClass sampleClass = ((DetailedSample) library.getSample()).getSampleClass();
        if (sampleClass.getId() != design.getSampleClass().getId()) {
          throw new IllegalArgumentException(
              "Cannot use design " + design.getName() + " for a library from a sample of type " + sampleClass.getAlias());
        }
      }
    }
  }

  /**
   * Copies modifiable fields from the source Library into the target Library to be persisted
   * 
   * @param target the persisted Library to modify
   * @param source the modified Library to copy modifiable fields from
   * @throws IOException
   */
  private void applyChanges(Library target, Library source) throws IOException {
    target.setDescription(source.getDescription());
    target.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(source.getIdentificationBarcode()));
    target.setConcentration(source.getConcentration());
    target.setConcentrationUnits(target.getConcentration() == null ? null : source.getConcentrationUnits());
    target.setPlatformType(source.getPlatformType());
    target.setAlias(source.getAlias());
    target.setPaired(source.getPaired());
    target.setLowQuality(source.isLowQuality());
    target.setDiscarded(source.isDiscarded());
    target.setCreationDate(source.getCreationDate());
    target.setInitialVolume(source.getInitialVolume());
    target.setVolume(source.getVolume());
    target.setVolumeUnits(target.getVolume() == null ? null : source.getVolumeUnits());
    target.setVolumeUsed(source.getVolumeUsed());
    target.setNgUsed(source.getNgUsed());
    target.setDnaSize(source.getDnaSize());
    target.setLibraryType(source.getLibraryType());
    target.setLibrarySelectionType(source.getLibrarySelectionType());
    target.setLibraryStrategyType(source.getLibraryStrategyType());
    target.setQcPassed(source.getQcPassed());

    target.setIndices(source.getIndices());
    if (source.getKitDescriptor() != null) {
      target.setKitDescriptor(source.getKitDescriptor());
    } else {
      target.setKitDescriptor(null);
    }
    target.setKitLot(source.getKitLot());
    target.setSpikeIn(source.getSpikeIn());
    if (target.getSpikeIn() == null) {
      target.setSpikeInDilutionFactor(null);
      target.setSpikeInVolume(null);
    } else {
      target.setSpikeInDilutionFactor(source.getSpikeInDilutionFactor());
      target.setSpikeInVolume(source.getSpikeInVolume());
    }
    target.setLocationBarcode(source.getLocationBarcode());
    target.setUmis(source.getUmis());
    target.setWorkstation(source.getWorkstation());
    target.setThermalCycler(source.getThermalCycler());

    if (isDetailedLibrary(target)) {
      DetailedLibrary dSource = (DetailedLibrary) source;
      DetailedLibrary dTarget = (DetailedLibrary) target;
      dTarget.setNonStandardAlias(dSource.hasNonStandardAlias());
      dTarget.setArchived(dSource.getArchived());
      dTarget.setLibraryDesignCode(dSource.getLibraryDesignCode());
      if (dSource.getLibraryDesign() != null) {
        dTarget.setLibraryDesign(dSource.getLibraryDesign());
      } else {
        dTarget.setLibraryDesign(null);
      }
      dTarget.setGroupId(dSource.getGroupId());
      dTarget.setGroupDescription(dSource.getGroupDescription());
    }
  }

  private void maybeRemoveFromBox(Library library, Library managed) {
    if (library.isDiscarded() || library.getDistributionTransfer() != null || managed.getDistributionTransfer() != null) {
      library.setBoxPosition(null);
      library.setVolume(BigDecimal.ZERO);
    }
  }

  private void validateChange(Library library, Library beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    validateConcentrationUnits(library.getConcentration(), library.getConcentrationUnits(), errors);
    validateVolumeUnits(library.getVolume(), library.getVolumeUnits(), errors);
    validateBarcodeUniqueness(library, beforeChange, libraryDao::getByBarcode, errors, "library");
    validateUnboxableFields(library, errors);
    if (isDetailedLibrary(library) && beforeChange != null) {
      validateTargetedSequencing(((DetailedLibrary) library).getLibraryDesignCode(), beforeChange.getLibraryAliquots(), errors);
    }

    if (library.getSpikeIn() != null) {
      if (library.getSpikeInDilutionFactor() == null) {
        errors.add(new ValidationError("spikeInDilutionFactor", "Spike-in dilution factor must be specified"));
      }
      if (library.getSpikeInVolume() == null) {
        errors.add(new ValidationError("spikeInVolume", "Spike-in volume must be specified"));
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void validateTargetedSequencing(LibraryDesignCode libraryDesignCode, Collection<LibraryAliquot> libraryAliquots,
      List<ValidationError> errors) throws IOException {
    if (libraryAliquots == null || libraryAliquots.isEmpty()) {
      return;
    }
    libraryDesignCode = libraryDesignCodeService.get(libraryDesignCode.getId());
    if (libraryDesignCode.isTargetedSequencingRequired()) {
      List<String> badAliquots = libraryAliquots.stream().filter(aliquot -> aliquot.getTargetedSequencing() == null)
          .map(LibraryAliquot::getName).collect(Collectors.toList());
      if (!badAliquots.isEmpty()) {
        errors.add(new ValidationError("libraryDesignCode",
            String.format("Targeted sequencing must be assigned to the affected %s (%s) to use this library design code: %s",
                Pluralizer.libraryAliquots(badAliquots.size()),
                String.join(", ", badAliquots),
                libraryDesignCode.getCode())));
      }
    }
  }

  /**
   * Checks whether library's alias conforms to the naming scheme. Validation is skipped for DetailedLibraries
   * {@code if (library.hasNonStandardAlias())}
   * 
   * @param library
   * @throws IOException
   * @throws IllegalArgumentException
   */
  private void validateAliasOrThrow(Library library, NamingScheme namingScheme) throws IOException {
    validateAliasUniqueness(library, namingScheme);
    if (!isDetailedLibrary(library) || !((DetailedLibrary) library).hasNonStandardAlias()) {
      uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult aliasValidation = namingScheme.validateLibraryAlias(library
          .getAlias());
      if (!aliasValidation.isValid()) {
        throw new ValidationException(new ValidationError("alias", aliasValidation.getMessage()));
      }
    }
  }

  private void validateAliasUniqueness(Library library, NamingScheme namingScheme) throws IOException {
    // duplicate aliases may be allowed via naming scheme, or with nonStandardAlias=true in the case of a DetailedLibrary
    if (namingScheme.duplicateLibraryAliasAllowed()
        || (LimsUtils.isDetailedLibrary(library) && ((DetailedLibrary) library).hasNonStandardAlias())) {
      return;
    }
    List<Library> potentialDupes = listByAlias(library.getAlias());
    for (Library potentialDupe : potentialDupes) {
      if (library.getId() == LibraryImpl.UNSAVED_ID || library.getId() != potentialDupe.getId()) {
        // an existing DIFFERENT library already has this alias
        throw new ValidationException(new ValidationError("alias", "A library with this alias already exists in the database"));
      }
    }
  }

  private void validateParentOrThrow(Library library) {
    if (!isDetailedLibrary(library)) return;

    if (!isAliquotSample(library.getSample())) {
      String sc = null;
      if (isDetailedSample(library.getSample())) {
        DetailedSample sample = (DetailedSample) library.getSample();
        sc = sample.getSampleClass() == null ? "not set" : sample.getSampleClass().getAlias();
      } else {
        sc = "Plain Sample";
      }
      throw new IllegalArgumentException(String.format("Sample Class '%s' is not a valid parent for Libraries. Must be an aliquot", sc));
    }
  }

  public void setLibraryDao(LibraryStore libraryDao) {
    this.libraryDao = libraryDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setNamingSchemeHolder(NamingSchemeHolder namingSchemeHolder) {
    this.namingSchemeHolder = namingSchemeHolder;
  }

  public void setLibraryDesignService(LibraryDesignService libraryDesignService) {
    this.libraryDesignService = libraryDesignService;
  }

  public void setLibraryDesignCodeService(LibraryDesignCodeService libraryDesignCodeService) {
    this.libraryDesignCodeService = libraryDesignCodeService;
  }

  public void setIndexService(IndexService indexService) {
    this.indexService = indexService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setKitDescriptorService(KitDescriptorService kitService) {
    this.kitService = kitService;
  }

  public void setChangeLogService(ChangeLogService changeLogService) {
    this.changeLogService = changeLogService;
  }

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  public void setWorksetService(WorksetService worksetService) {
    this.worksetService = worksetService;
  }

  public void setFileAttachmentService(FileAttachmentService fileAttachmentService) {
    this.fileAttachmentService = fileAttachmentService;
  }

  public void setAutoGenerateIdBarcodes(Boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void authorizeDeletion(Library object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());

  }

  @Override
  public ValidationResult validateDeletion(Library object) {
    ValidationResult result = new ValidationResult();

    if (object.getLibraryAliquots() != null && !object.getLibraryAliquots().isEmpty()) {
      result.addError(new ValidationError(object.getName() + " has " + object.getLibraryAliquots().size() + " "
          + Pluralizer.libraryAliquots(object.getLibraryAliquots().size())));
    }
    return result;
  }

  @Override
  public void beforeDelete(Library object) throws IOException {
    List<Workset> worksets = worksetService.listByLibrary(object.getId());
    for (Workset workset : worksets) {
      workset.getLibraries().removeIf(lib -> lib.getId() == object.getId());
      worksetService.update(workset);
    }
    Box box = object.getBox();
    if (box != null) {
      box.getBoxPositions().remove(object.getBoxPosition());
      boxService.save(box);
    }
    fileAttachmentService.beforeDelete(object);
  }

  @Override
  public void afterDelete(Library object) throws IOException {
    fileAttachmentService.afterDelete(object);
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return libraryDao.count(errorHandler, filter);
  }

  @Override
  public List<Library> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return libraryDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public HibernateSessionManager getHibernateSessionManager() {
    return hibernateSessionManager;
  }

}
