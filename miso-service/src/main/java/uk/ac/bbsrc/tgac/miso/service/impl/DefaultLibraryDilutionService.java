package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.TargetedSequencingStore;
import uk.ac.bbsrc.tgac.miso.core.util.DilutionPaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizedPaginatedDataSource;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryDilutionService
    implements LibraryDilutionService, AuthorizedPaginatedDataSource<LibraryDilution, DilutionPaginationFilter> {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleService.class);

  @Autowired
  private LibraryDilutionStore dilutionDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private LibraryStore libraryDao;
  @Autowired
  private TargetedSequencingStore targetedSequencingDao;
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Override
  public LibraryDilution get(long dilutionId) throws IOException {
    LibraryDilution dilution = dilutionDao.get(dilutionId);
    authorizationManager.throwIfNotReadable(dilution);
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
    dilution.setDilutionCreator(authorizationManager.getCurrentUsername());
    if (dilution.getSecurityProfile() == null) {
      dilution.inheritPermissions(libraryDao.get(dilution.getLibrary().getId()));
    }
    authorizationManager.throwIfNotWritable(dilution);
    
    // pre-save field generation
    dilution.setName(generateTemporaryName());
    return save(dilution).getId();
  }

  @Override
  public void update(LibraryDilution dilution) throws IOException {
    LibraryDilution updatedDilution = get(dilution.getId());
    authorizationManager.throwIfNotWritable(updatedDilution);
    applyChanges(updatedDilution, dilution);
    loadChildEntities(updatedDilution);
    save(updatedDilution);
  }
  
  @Override
  public boolean delete(LibraryDilution dilution) throws IOException {
    authorizationManager.throwIfNonAdmin();
    LibraryDilution managed = get(dilution.getId());
    managed.getLibrary().getLibraryDilutions().remove(managed);
    return dilutionDao.remove(managed);
  }

  @Override
  public int count() throws IOException {
    return dilutionDao.count();
  }

  @Override
  public List<LibraryDilution> list() throws IOException {
    Collection<LibraryDilution> allDilutions = dilutionDao.listAll();
    return authorizationManager.filterUnreadable(allDilutions);
  }

  @Override
  public List<LibraryDilution> listByLibraryId(Long libraryId) throws IOException {
    Collection<LibraryDilution> allDilutions = dilutionDao.listByLibraryId(libraryId);
    return authorizationManager.filterUnreadable(allDilutions);
  }

  @Override
  public LibraryDilution getByBarcode(String barcode) throws IOException {
    LibraryDilution dilution = dilutionDao.getLibraryDilutionByBarcode(barcode);
    return (authorizationManager.readCheck(dilution) ? dilution : null);
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
      dilution.setLibrary(libraryDao.get(dilution.getLibrary().getId()));
    }
    if (dilution.getTargetedSequencing() != null) {
      dilution.setTargetedSequencing(targetedSequencingDao.get(dilution.getTargetedSequencing().getId()));
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
    target.setConcentration(source.getConcentration());
    target.setTargetedSequencing(source.getTargetedSequencing());
    target.setIdentificationBarcode(source.getIdentificationBarcode());
    target.setVolume(source.getVolume());
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

  public void setTargetedSequencingDao(TargetedSequencingStore targetedSequencingDao) {
    this.targetedSequencingDao = targetedSequencingDao;
  }

  public void setAutoGenerateIdBarcodes(Boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

  public void setLibraryDao(LibraryStore libraryDao) {
    this.libraryDao = libraryDao;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public PaginatedDataSource<LibraryDilution, DilutionPaginationFilter> getBackingPaginationSource() {
    return dilutionDao;
  }
}
