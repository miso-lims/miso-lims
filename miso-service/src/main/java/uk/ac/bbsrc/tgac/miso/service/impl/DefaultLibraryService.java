package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.IndexStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryService implements LibraryService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultLibraryService.class);

  @Autowired
  private LibraryStore libraryDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private LibraryDesignDao libraryDesignDao;
  @Autowired
  private LibraryDesignCodeDao libraryDesignCodeDao;
  @Autowired
  private LibraryQcStore libraryQcDao;
  @Autowired
  private IndexStore indexDao;
  @Autowired
  private SampleDao sampleDao;
  @Autowired
  private KitStore kitDescriptorDao;
  @Autowired
  private ChangeLogStore changeLogDao;
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Override
  public Library get(long libraryId) throws IOException {
    Library library = libraryDao.get(libraryId);
    authorizationManager.throwIfNotReadable(library);
    return library;
  }

  private Library save(Library library) throws IOException {
    try {
      Long newId = libraryDao.save(library);
      
      Library managed = libraryDao.get(newId);
      
      // post-save field generation
      boolean needsUpdate = false;
      if (hasTemporaryName(library)) {
        managed.setName(namingScheme.generateNameFor(managed));
        validateNameOrThrow(managed, namingScheme);
        needsUpdate = true;
      }
      if (autoGenerateIdBarcodes && isStringEmptyOrNull(managed.getIdentificationBarcode())) {
        // if !autoGenerateIdBarcodes then the identificationBarcode is set by the user
        generateAndSetIdBarcode(managed);
        needsUpdate = true;
      }
      if (needsUpdate) libraryDao.save(managed);
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
  public Long create(Library library) throws IOException {
    loadChildEntities(library);
    setChangeDetails(library);
    if (library.getSecurityProfile() == null) {
      library.inheritPermissions(sampleDao.get(library.getSample().getId()));
    }
    authorizationManager.throwIfNotWritable(library);
    validateParentOrThrow(library);

    // pre-save field generation
    library.setName(generateTemporaryName());
    if (isStringEmptyOrNull(library.getAlias()) && namingScheme.hasLibraryAliasGenerator()) {
      try {
        library.setAlias(namingScheme.generateLibraryAlias(library));
      } catch (MisoNamingException e) {
        throw new IOException("Error generating alias for library", e);
      }
    } else if (!isDetailedLibrary(library) || !((DetailedLibrary) library).hasNonStandardAlias()) {
      validateAliasOrThrow(library);
    }
    return save(library).getId();
  }

  @Override
  public void update(Library library) throws IOException {
    Library updatedLibrary = get(library.getId());
    List<Index> originalIndices = new ArrayList<>(updatedLibrary.getIndices());
    authorizationManager.throwIfNotWritable(updatedLibrary);
    applyChanges(updatedLibrary, library);
    validateAliasOrThrow(updatedLibrary);
    setChangeDetails(updatedLibrary);
    loadChildEntities(updatedLibrary);
    makeChangeLogForIndices(originalIndices, updatedLibrary.getIndices(), updatedLibrary);
    save(updatedLibrary);
  }

  @Override
  public boolean delete(Library library) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Library managed = get(library.getId());
    managed.getSample().getLibraries().remove(managed);
    return libraryDao.remove(managed);
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
    Collection<Library> allLibraries = libraryDao.listAll();
    return authorizationManager.filterUnreadable(allLibraries);
  }

  @Override
  public List<Library> listByPageAndSize(int offset, int size, String sortDir, String sortCol) throws IOException {
    Collection<Library> libraries = libraryDao.listByOffsetAndNumResults(offset, size, sortDir, sortCol);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> listByPageSizeAndSearch(int offset, int size, String querystr, String sortDir, String sortCol)
      throws IOException {
    Collection<Library> libraries = libraryDao.listBySearchOffsetAndNumResults(offset, size, querystr, sortDir, sortCol);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public Library getAdjacentLibrary(long libraryId, boolean before) throws IOException {
    Library library = libraryDao.getAdjacentLibrary(libraryId, before);
    // We don't throw because the user has no real control over this.
    return (authorizationManager.readCheck(library) ? library : null);
  }

  @Override
  public Library getByBarcode(String barcode) throws IOException {
    Library library = libraryDao.getByBarcode(barcode);
    return (authorizationManager.readCheck(library) ? library : null);
  }

  @Override
  public List<Library> listByBarcodeList(List<String> barcodeList) throws IOException {
    Collection<Library> libraries = libraryDao.getByBarcodeList(barcodeList);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> listByIdList(List<Long> idList) throws IOException {
    Collection<Library> libraries = libraryDao.getByIdList(idList);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public Library getByPositionId(long positionId) throws IOException {
    Library library = (Library) libraryDao.getByPositionId(positionId);
    return (authorizationManager.readCheck(library) ? library : null);
  }

  @Override
  public List<Library> listBySearch(String querystr) throws IOException {
    Collection<Library> libraries = libraryDao.listBySearch(querystr);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> listByAlias(String alias) throws IOException {
    Collection<Library> libraries = libraryDao.listByAlias(alias);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> listWithLimit(long limit) throws IOException {
    Collection<Library> libraries = libraryDao.listAllWithLimit(limit);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> searchByCreationDate(Date from, Date to) throws IOException {
    Collection<Library> libraries = libraryDao.searchByCreationDate(from, to);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> listBySampleId(long sampleId) throws IOException {
    Collection<Library> libraries = libraryDao.listBySampleId(sampleId);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> listByProjectId(long projectId) throws IOException {
    Collection<Library> libraries = libraryDao.listByProjectId(projectId);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public Map<String, Integer> getLibraryColumnSizes() throws IOException {
    return libraryDao.getLibraryColumnSizes();
  }

  @Override
  public LibraryType getLibraryTypeById(long libraryTypeId) throws IOException {
    return libraryDao.getLibraryTypeById(libraryTypeId);
  }

  @Override
  public LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException {
    return libraryDao.getLibraryTypeByDescriptionAndPlatform(description, platformType);
  }

  @Override
  public Collection<LibraryType> listLibraryTypes() throws IOException {
    return libraryDao.listAllLibraryTypes();
  }

  @Override
  public Collection<LibraryType> listLibraryTypesByPlatform(PlatformType platform) throws IOException {
    return libraryDao.listLibraryTypesByPlatform(platform);
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeById(Long librarySelectionTypeId) throws IOException {
    return libraryDao.getLibrarySelectionTypeById(librarySelectionTypeId);
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException {
    return libraryDao.getLibrarySelectionTypeByName(name);
  }

  @Override
  public Collection<LibrarySelectionType> listLibrarySelectionTypes() throws IOException {
    return libraryDao.listAllLibrarySelectionTypes();
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException {
    return libraryDao.getLibraryStrategyTypeById(libraryStrategyTypeId);
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException {
    return libraryDao.getLibraryStrategyTypeByName(name);
  }

  @Override
  public Collection<LibraryStrategyType> listLibraryStrategyTypes() throws IOException {
    return libraryDao.listAllLibraryStrategyTypes();
  }

  @Override
  public Collection<QcType> listLibraryQcTypes() throws IOException {
    return libraryQcDao.listAllLibraryQcTypes();
  }

  @Override
  public void addNote(Library library, Note note) throws IOException {
    Library managed = libraryDao.get(library.getId());
    authorizationManager.throwIfNotWritable(managed);
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    save(managed);
  }

  @Override
  public void deleteNote(Library library, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Library managed = libraryDao.get(library.getId());
    authorizationManager.throwIfNotWritable(managed);
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
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

  @Override
  public void addQc(Library library, LibraryQC qc) throws IOException {
    if (qc.getQcType() == null || qc.getQcType().getQcTypeId() == null) {
      throw new IllegalArgumentException("QC Type cannot be null");
    }
    QcType managedQcType = libraryQcDao.getLibraryQcTypeById(qc.getQcType().getQcTypeId());
    if (managedQcType == null) {
      throw new IllegalArgumentException("QC Type " + qc.getQcType().getQcTypeId() + " is not applicable for libraries");
    }
    qc.setQcType(managedQcType);
    qc.setQcCreator(authorizationManager.getCurrentUsername());

    Library managed = libraryDao.get(library.getId());
    authorizationManager.throwIfNotWritable(managed);
    // TODO: update concentration if QC is of relevant type
    managed.addQc(qc);
    managed.setLastModifier(authorizationManager.getCurrentUser());
    libraryDao.save(managed);
  }

  @Override
  public void deleteQc(Library library, Long qcId) throws IOException {
    if (qcId == null || qcId.equals(LibraryQCImpl.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Library QC");
    }
    Library managed = libraryDao.get(library.getId());
    authorizationManager.throwIfNotWritable(managed);
    LibraryQC deleteQc = null;
    for (LibraryQC qc : managed.getLibraryQCs()) {
      if (qc.getId() == qcId) {
        deleteQc = qc;
        break;
      }
    }
    if (deleteQc == null) throw new IOException("QC " + qcId + " not found for Library " + library.getId());
    authorizationManager.throwIfNonAdminOrMatchingOwner(securityManager.getUserByLoginName(deleteQc.getQcCreator()));
    managed.getLibraryQCs().remove(deleteQc);
    managed.setLastModifier(authorizationManager.getCurrentUser());
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
      if (index != null && index.getId() != Index.UNSAVED_ID) {
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
      changeLogDao.create(changeLog);
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
      library.setSample(sampleDao.get(library.getSample().getId()));
    }
    if (library.getLibraryType() != null) {
      library.setLibraryType(getLibraryTypeById(library.getLibraryType().getId()));
    }
    if (library.getLibrarySelectionType() != null) {
      library.setLibrarySelectionType(getLibrarySelectionTypeById(library.getLibrarySelectionType().getId()));
    }
    if (library.getLibraryStrategyType() != null) {
      library.setLibraryStrategyType(getLibraryStrategyTypeById(library.getLibraryStrategyType().getId()));
    }
    List<Index> managedIndices = new ArrayList<>();
    for (Index index : library.getIndices()) {
      Index managedIndex = indexDao.getIndexById(index.getId());
      if (managedIndex != null) managedIndices.add(managedIndex);
    }
    library.setIndices(managedIndices);
    if (library.getSecurityProfile() != null && library.getSecurityProfile().getProfileId() != SecurityProfile.UNSAVED_ID) {
      library.setSecurityProfile(securityManager.getSecurityProfileById(library.getSecurityProfile().getProfileId()));
    }
    if (isDetailedLibrary(library)) {
      DetailedLibrary lai = (DetailedLibrary) library;
      if (lai.getKitDescriptor() != null) {
        lai.setKitDescriptor(kitDescriptorDao.getKitDescriptorById(lai.getKitDescriptor().getId()));
      }
      if (lai.getLibraryDesignCode() != null) {
        lai.setLibraryDesignCode(libraryDesignCodeDao.getLibraryDesignCode(lai.getLibraryDesignCode().getId()));
      }
      if (lai.getLibraryDesign() != null) {
        LibraryDesign design = libraryDesignDao.getLibraryDesign(lai.getLibraryDesign().getId());
        lai.setLibraryDesign(design);
        lai.setLibrarySelectionType(design.getLibrarySelectionType());
        lai.setLibraryStrategyType(design.getLibraryStrategyType());
        lai.setLibraryDesignCode(design.getLibraryDesignCode());
        SampleClass sampleClass = ((DetailedSample) library.getSample()).getSampleClass();
        if (!sampleClass.getId().equals(design.getSampleClass().getId())) {
          throw new IllegalArgumentException(
              "Cannot use design " + design.getName() + " for a library from a sample of type " + sampleClass.getAlias());
        }
      }
    }
  }

  /**
   * Updates all timestamps and user data associated with the change
   * 
   * @param library the Library to update
   * @throws IOException
   */
  private void setChangeDetails(Library library) throws IOException {
    User user = authorizationManager.getCurrentUser();
    library.setLastModifier(user);
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
    target.setIdentificationBarcode(source.getIdentificationBarcode());
    target.setLocationBarcode(source.getLocationBarcode());
    target.setInitialConcentration(source.getInitialConcentration());
    target.setPlatformType(source.getPlatformType());
    target.setAlias(source.getAlias());
    target.setPaired(source.getPaired());
    target.setLowQuality(source.isLowQuality());
    target.setDiscarded(source.isDiscarded());
    if (target.isDiscarded()) {
      target.setVolume(0.0);
    } else {
      target.setVolume(source.getVolume());
    }
    target.setDnaSize(source.getDnaSize());
    target.setLibraryType(source.getLibraryType());
    target.setLibrarySelectionType(source.getLibrarySelectionType());
    target.setLibraryStrategyType(source.getLibraryStrategyType());
    target.setQcPassed(source.getQcPassed());

    target.setIndices(source.getIndices());

    if (isDetailedLibrary(target)) {
      DetailedLibrary dSource = (DetailedLibrary) source;
      DetailedLibrary dTarget = (DetailedLibrary) target;
      dTarget.setPreMigrationId(dSource.getPreMigrationId());
      dTarget.setNonStandardAlias(dSource.hasNonStandardAlias());
      dTarget.setArchived(dSource.getArchived());
      dTarget.setLibraryDesignCode(dSource.getLibraryDesignCode());
      if (dSource.getLibraryDesign() != null) {
        dTarget.setLibraryDesign(dSource.getLibraryDesign());
      } else {
        dTarget.setLibraryDesign(null);
      }
      if (dSource.getKitDescriptor() != null) {
        dTarget.setKitDescriptor(dSource.getKitDescriptor());
      } else {
        dTarget.setKitDescriptor(null);
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
  private void validateAliasOrThrow(Library library) throws IOException {
    validateAliasUniqueness(library);
    if (!isDetailedLibrary(library) || !((DetailedLibrary) library).hasNonStandardAlias()) {
      ValidationResult aliasValidation = namingScheme.validateLibraryAlias(library.getAlias());
      if (!aliasValidation.isValid()) {
        throw new IllegalArgumentException("Invalid library alias: '" + library.getAlias() + "' - " + aliasValidation.getMessage());
      }
    }
  }

  private void validateAliasUniqueness(Library library) throws IOException {
    // duplicate aliases may be allowed via naming scheme, or with nonStandardAlias=true in the case of a DetailedLibrary
    if (namingScheme.duplicateLibraryAliasAllowed()
        || (LimsUtils.isDetailedLibrary(library) && ((DetailedLibrary) library).hasNonStandardAlias())) {
      return;
    }
    List<Library> potentialDupes = listByAlias(library.getAlias());
    for (Library potentialDupe : potentialDupes) {
      if (library.getId() == AbstractLibrary.UNSAVED_ID || library.getId() != potentialDupe.getId()) {
        // an existing DIFFERENT library already has this alias
        throw new IllegalArgumentException("NEW: A library with this alias already exists in the database");
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

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setLibraryDesignDao(LibraryDesignDao libraryDesignDao) {
    this.libraryDesignDao = libraryDesignDao;
  }

  public void setLibraryDesignCodeDao(LibraryDesignCodeDao libraryDesignCodeDao) {
    this.libraryDesignCodeDao = libraryDesignCodeDao;
  }

  public void setLibraryQcDao(LibraryQcStore libraryQcStore) {
    this.libraryQcDao = libraryQcStore;
  }

  public void setIndexDao(IndexStore indexDao) {
    this.indexDao = indexDao;
  }

  public void setSampleDao(SampleDao sampleDao) {
    this.sampleDao = sampleDao;
  }

  public void setKitDao(KitStore kitDescriptorDao) {
    this.kitDescriptorDao = kitDescriptorDao;
  }

  public void setChangeLogDao(ChangeLogStore changeLogDao) {
    this.changeLogDao = changeLogDao;
  }

  public void setAutoGenerateIdBarcodes(Boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

}
