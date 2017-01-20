package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryQcException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
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
    
    // pre-save field generation
    library.setName(generateTemporaryName());
    if (isDetailedLibrary(library) && ((DetailedLibrary) library).hasNonStandardAlias()) {
      // do not validate alias
    } else if (isStringEmptyOrNull(library.getAlias()) && namingScheme.hasLibraryAliasGenerator()) {
      try {
        library.setAlias(namingScheme.generateLibraryAlias(library));
      } catch (MisoNamingException e) {
        throw new IOException("Error generating alias for library", e);
      }
    } else {
      validateAliasOrThrow(library);
    }
    return save(library).getId();
  }

  @Override
  public void update(Library library) throws IOException {
    Library updatedLibrary = get(library.getId());
    authorizationManager.throwIfNotWritable(updatedLibrary);
    applyChanges(updatedLibrary, library);
    validateAliasOrThrow(updatedLibrary);
    setChangeDetails(updatedLibrary);
    loadChildEntities(updatedLibrary);
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
  public List<Library> getAll() throws IOException {
    Collection<Library> allLibraries = libraryDao.listAll();
    return authorizationManager.filterUnreadable(allLibraries);
  }

  @Override
  public List<Library> getAllByPageAndSize(int offset, int size, String sortDir, String sortCol) throws IOException {
    Collection<Library> libraries = libraryDao.listByOffsetAndNumResults(offset, size, sortDir, sortCol);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> getAllByPageSizeAndSearch(int offset, int size, String querystr, String sortDir, String sortCol)
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
  public List<Library> getAllByBarcodeList(List<String> barcodeList) throws IOException {
    Collection<Library> libraries = libraryDao.getByBarcodeList(barcodeList);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> getAllByIdList(List<Long> idList) throws IOException {
    Collection<Library> libraries = libraryDao.getByIdList(idList);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public Library getByPositionId(long positionId) throws IOException {
    Library library = (Library) libraryDao.getByPositionId(positionId);
    return (authorizationManager.readCheck(library) ? library : null);
  }

  @Override
  public List<Library> getAllBySearch(String querystr) throws IOException {
    Collection<Library> libraries = libraryDao.listBySearch(querystr);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> getAllByAlias(String alias) throws IOException {
    Collection<Library> libraries = libraryDao.listByAlias(alias);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> getAllWithLimit(long limit) throws IOException {
    Collection<Library> libraries = libraryDao.listAllWithLimit(limit);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> searchByCreationDate(Date from, Date to) throws IOException {
    Collection<Library> libraries = libraryDao.searchByCreationDate(from, to);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> getAllBySampleId(long sampleId) throws IOException {
    Collection<Library> libraries = libraryDao.listBySampleId(sampleId);
    return authorizationManager.filterUnreadable(libraries);
  }

  @Override
  public List<Library> getAllByProjectId(long projectId) throws IOException {
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
  public Collection<LibraryType> getAllLibraryTypes() throws IOException {
    return libraryDao.listAllLibraryTypes();
  }

  @Override
  public Collection<LibraryType> getAllLibraryTypesByPlatform(PlatformType platform) throws IOException {
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
  public Collection<LibrarySelectionType> getAllLibrarySelectionTypes() throws IOException {
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
  public Collection<LibraryStrategyType> getAllLibraryStrategyTypes() throws IOException {
    return libraryDao.listAllLibraryStrategyTypes();
  }

  @Override
  public Collection<QcType> getAllLibraryQcTypes() throws IOException {
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
    Library managed = libraryDao.get(library.getId());
    authorizationManager.throwIfNotWritable(managed);
    qc.setQcCreator(authorizationManager.getCurrentUsername());
    try {
      qc.setLibrary(managed);
      managed.addQc(qc);
    } catch (MalformedLibraryException e) {
      throw new IOException("Malformed Library " + managed.getName() + " in the database...", e);
    } catch (MalformedLibraryQcException e) {
      throw new IOException("Malformed Library QC");
    }
    libraryQcDao.save(qc);
    libraryDao.save(library);
  }

  @Override
  public void deleteQc(Library library, Long qcId) throws IOException {
    if (qcId == null || qcId.equals(AbstractLibraryQC.UNSAVED_ID)) {
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
    libraryQcDao.remove(deleteQc);
    libraryDao.save(managed);
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
    if (isDetailedLibrary(library)) {
      DetailedLibrary lai = (DetailedLibrary) library;
      if (lai.getKitDescriptor() != null) {
        lai.setKitDescriptor(kitDescriptorDao.getKitDescriptorById(lai.getKitDescriptor().getId()));
      }
      if (lai.getLibraryDesign() != null) {
        lai.setLibraryDesign(libraryDesignDao.getLibraryDesign(lai.getLibraryDesign().getId()));
      }
      if (lai.getLibraryDesignCode() != null) {
        lai.setLibraryDesignCode(libraryDesignCodeDao.getLibraryDesignCode(lai.getLibraryDesignCode().getId()));
      }
      validateLibraryDesignValues(library);
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
    target.setPlatformName(source.getPlatformName());
    target.setAlias(source.getAlias());
    target.setPaired(source.getPaired());
    target.setLowQuality(source.isLowQuality());
    target.setDiscarded(source.isDiscarded());
    if (target.isDiscarded()) {
      target.setVolume(0.0);
    } else {
      target.setVolume(source.getVolume());
    }
    target.getLibraryType().setId(source.getLibraryType().getId());
    target.getLibrarySelectionType().setId(source.getLibrarySelectionType().getId());
    target.getLibraryStrategyType().setId(source.getLibraryStrategyType().getId());
    target.setQcPassed(source.getQcPassed());
    if (isDetailedLibrary(target)) {
      DetailedLibrary dSource = (DetailedLibrary) source;
      DetailedLibrary dTarget = (DetailedLibrary) target;
      dTarget.setPreMigrationId(dSource.getPreMigrationId());
      dTarget.setNonStandardAlias(dSource.hasNonStandardAlias());
      dTarget.setArchived(dSource.getArchived());
      dTarget.getLibraryDesignCode().setId(dSource.getLibraryDesignCode().getId());
      if (dSource.getLibraryDesign() != null) {
        dTarget.getLibraryDesign().setId(dSource.getLibraryDesign().getId());
      } else {
        dTarget.getLibraryDesign().setId(null);
      }
      if (dSource.getKitDescriptor() != null) {
      dTarget.getKitDescriptor().setId(dSource.getKitDescriptor().getId());
      } else {
        dTarget.getKitDescriptor().setId(null);
      }
    }
  }

  /**
   * Confirms that when a Library Design is selected (detailed library only), the corresponding
   * LibraryDesignCode, LibrarySelection and LibraryStrategy all match the values for the selected LibraryDesign.
   *
   * @param library the Library with desired selection, strategy, and libraryDesignCode set.
   * @throws IOException when the library's values don't match the values of the LibraryDesign
   */
  private void validateLibraryDesignValues(Library library) throws IOException {
    if (!isDetailedLibrary(library)) {
      throw new IOException("A library design can only be applied to a detailed sample.");
    }
    LibraryDesign design = ((DetailedLibrary) library).getLibraryDesign();
    if (((DetailedSample) library.getSample()).getSampleClass().getId() != design.getSampleClass().getId()) {
      throw new IOException(
          "This library design is not valid for sample " + library.getSample().getName() + " because the class is not compatible.");
    }
    LibrarySelectionType selection = design.getLibrarySelectionType();
    LibraryStrategyType strategy = design.getLibraryStrategyType();
    if (library.getLibrarySelectionType() != null && library.getLibrarySelectionType().getId() != selection.getId()) {
      throw new IOException("Library selection doesn't match library design.");
    }
    if (library.getLibraryStrategyType() != null && library.getLibraryStrategyType().getId() != strategy.getId()) {
      throw new IOException("Library strategy doesn't match library design.");
    }
    if (((DetailedLibrary) library).getLibraryDesignCode().getId() != null
        && ((DetailedLibrary) library).getLibraryDesign().getId() != null
        && ((DetailedLibrary) library).getLibraryDesignCode().getId() != null
        && ((DetailedLibrary) library).getLibraryDesign().getLibraryDesignCode().getId() != null) {
      throw new IOException("Selected library design code does not match library design code for selected library design.");
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
    if (!namingScheme.duplicateLibraryAliasAllowed() && !getAllByAlias(library.getAlias()).isEmpty()) {
      if (LimsUtils.isDetailedLibrary(library) && ((DetailedLibrary) library).hasNonStandardAlias()) {
        // do nothing; nonstandard alias means duplicates are acceptable
      } else {
        // throw if duplicate aliases are not allowed (and in the case of a DetailedLibrary, if it has a standard alias
        throw new IllegalArgumentException("NEW: A library with this alias already exists in the database");
      }
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

  public void setAutoGenerateIdBarcodes(Boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

}
