/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.store.SecurityStore;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TagBarcodeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryQcException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLLibraryDAO implements LibraryStore {
  private static String TABLE_NAME = "Library";

  public static final String LIBRARIES_SELECT = "SELECT libraryId, name, description, alias, accession, securityProfile_profileId, sample_sampleId, identificationBarcode, "
      + "locationBarcode, paired, libraryType, librarySelectionType, libraryStrategyType, platformName, concentration, creationDate, qcPassed, lastModifier, lowQuality, boxPositionId, volume, emptied, "
      + "(SELECT boxId FROM BoxPosition WHERE BoxPosition.boxPositionId = " + TABLE_NAME
      + ".boxPositionId) AS boxId, (SELECT alias FROM Box, BoxPosition WHERE Box.boxId = BoxPosition.boxId AND BoxPosition.boxPositionId = "
      + TABLE_NAME + ".boxPositionId) AS boxAlias, (SELECT row FROM BoxPosition WHERE BoxPosition.boxPositionId = " + TABLE_NAME
      + ".boxPositionId) AS boxRow, (SELECT `column` FROM BoxPosition WHERE BoxPosition.boxPositionId = " + TABLE_NAME
      + ".boxPositionId) AS boxColumn " + "FROM " + TABLE_NAME;

  public static final String LIBRARIES_SELECT_LIMIT = LIBRARIES_SELECT + " ORDER BY libraryId DESC LIMIT ?";

  public static final String LIBRARY_SELECT_BY_ID = LIBRARIES_SELECT + " WHERE libraryId = ?";

  public static final String LIBRARY_SELECT_BY_ALIAS = LIBRARIES_SELECT + " WHERE alias = ?";

  public static final String LIBRARIES_SELECT_BY_SEARCH = LIBRARIES_SELECT + " WHERE " + "identificationBarcode LIKE ? OR "
      + "name LIKE ? OR " + "alias LIKE ? OR " + "description LIKE ? ";

  public static final String LIBRARY_SELECT_BY_IDENTIFICATION_BARCODE = LIBRARIES_SELECT + " WHERE identificationBarcode = ?";

  public static final String LIBRARIES_SELECT_FROM_BARCODE_LIST = LIBRARIES_SELECT + " WHERE identificationBarcode IN (";

  public static final String LIBRARY_SELECT_BY_BOX_POSITION_ID = LIBRARIES_SELECT + " WHERE boxPositionId = ?";

  public static final String LIBRARY_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name=:name, description=:description, alias=:alias, accession=:accession, securityProfile_profileId=:securityProfile_profileId, "
      + "sample_sampleId=:sample_sampleId, identificationBarcode=:identificationBarcode,  locationBarcode=:locationBarcode, "
      + "paired=:paired, libraryType=:libraryType, librarySelectionType=:librarySelectionType, libraryStrategyType=:libraryStrategyType, "
      + "platformName=:platformName, concentration=:concentration, creationDate=:creationDate, qcPassed=:qcPassed, lastModifier=:lastModifier, lowQuality=:lowQuality, emptied=:emptied, volume=:volume "
      + "WHERE libraryId=:libraryId";

  public static final String LIBRARY_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE libraryId=:libraryId";

  public static final String LIBRARIES_SELECT_BY_SAMPLE_ID = LIBRARIES_SELECT + " WHERE sample_sampleId=?";

  public static String LIBRARIES_SELECT_BY_PROJECT_ID = LIBRARIES_SELECT
      + " WHERE sample_sampleId IN (SELECT sampleId FROM Sample WHERE project_projectId = ?)";

  public static final String LIBRARY_TYPES_SELECT = "SELECT libraryTypeId, description, platformType " + "FROM LibraryType";

  public static final String LIBRARY_TYPE_SELECT_BY_ID = LIBRARY_TYPES_SELECT + " WHERE libraryTypeId = ?";

  public static final String LIBRARY_TYPE_SELECT_BY_DESCRIPTION = LIBRARY_TYPES_SELECT + " WHERE description = ?";

  public static final String LIBRARY_TYPE_SELECT_BY_DESCRIPTION_AND_PLATFORM = LIBRARY_TYPES_SELECT
      + " WHERE description = ? AND platformType = ?";

  public static final String LIBRARY_TYPES_SELECT_BY_PLATFORM = "SELECT libraryTypeId, description, platformType " + "FROM LibraryType "
      + "WHERE platformType=?";

  public static final String LIBRARY_SELECTION_TYPES_SELECT = "SELECT librarySelectionTypeId, name, description "
      + "FROM LibrarySelectionType";

  public static final String LIBRARY_SELECTION_TYPE_SELECT_BY_ID = LIBRARY_SELECTION_TYPES_SELECT + " WHERE librarySelectionTypeId = ?";

  public static final String LIBRARY_SELECTION_TYPE_SELECT_BY_NAME = LIBRARY_SELECTION_TYPES_SELECT + " WHERE name = ?";

  public static final String LIBRARY_STRATEGY_TYPES_SELECT = "SELECT libraryStrategyTypeId, name, description "
      + "FROM LibraryStrategyType";

  public static final String LIBRARY_STRATEGY_TYPE_SELECT_BY_ID = LIBRARY_STRATEGY_TYPES_SELECT + " WHERE libraryStrategyTypeId = ?";

  public static final String LIBRARY_STRATEGY_TYPE_SELECT_BY_NAME = LIBRARY_STRATEGY_TYPES_SELECT + " WHERE name = ?";

  public static final String LIBRARIES_BY_RELATED_DILUTION_ID = LIBRARIES_SELECT
      + " WHERE libraryId IN (SELECT library_libraryId FROM LibraryDilution WHERE dilutionId=?)";

  public static final String TAG_BARCODES_SELECT = "SELECT tagId, name, sequence, platformName, strategyName " + "FROM TagBarcodes";

  public static final String TAG_BARCODE_SELECT_BY_NAME = TAG_BARCODES_SELECT + " WHERE name = ? ORDER by tagId";

  public static final String TAG_BARCODE_SELECT_BY_LIBRARY_ID = "SELECT tb.tagId, tb.name, tb.sequence, tb.platformName, tb.strategyName "
      + "FROM TagBarcodes tb, Library_TagBarcode lt " + "WHERE tb.tagId = lt.barcode_barcodeId " + "AND lt.library_libraryId = ? ";

  public static final String TAG_BARCODES_SELECT_BY_PLATFORM = TAG_BARCODES_SELECT + " WHERE platformName = ? ORDER by tagId";

  public static final String TAG_BARCODES_SELECT_BY_STRATEGY_NAME = TAG_BARCODES_SELECT + " WHERE strategyName = ? ORDER by tagId";

  public static final String TAG_BARCODE_SELECT_BY_ID = TAG_BARCODES_SELECT + " WHERE tagId = ?";

  public static final String LIBRARY_TAGBARCODE_DELETE_BY_LIBRARY_ID = "DELETE FROM Library_TagBarcode "
      + "WHERE library_libraryId=:library_libraryId";

  protected static final Logger log = LoggerFactory.getLogger(SQLLibraryDAO.class);
  private JdbcTemplate template;
  private Store<SecurityProfile> securityProfileDAO;
  private SampleStore sampleDAO;
  private PoolStore poolDAO;
  private LibraryQcStore libraryQcDAO;
  private LibraryDilutionStore dilutionDAO;
  private NoteStore noteDAO;
  private CascadeType cascadeType;
  private boolean autoGenerateIdentificationBarcodes;
  private ChangeLogStore changeLogDAO;
  private SecurityStore securityDAO;

  @Autowired
  private MisoNamingScheme<Library> libraryNamingScheme;

  public MisoNamingScheme<Library> getLibraryNamingScheme() {
    return libraryNamingScheme;
  }

  public void setLibraryNamingScheme(MisoNamingScheme<Library> libraryNamingScheme) {
    this.libraryNamingScheme = libraryNamingScheme;
  }

  @Autowired
  private MisoNamingScheme<Library> namingScheme;

  @Override
  public MisoNamingScheme<Library> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Library> namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
  }

  public void setPoolDAO(PoolStore poolDAO) {
    this.poolDAO = poolDAO;
  }

  public void setLibraryQcDAO(LibraryQcStore libraryQcDAO) {
    this.libraryQcDAO = libraryQcDAO;
  }

  public void setDilutionDAO(LibraryDilutionStore dilutionDAO) {
    this.dilutionDAO = dilutionDAO;
  }

  public void setNoteDAO(NoteStore noteDAO) {
    this.noteDAO = noteDAO;
  }

  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  public void setAutoGenerateIdentificationBarcodes(boolean autoGenerateIdentificationBarcodes) {
    this.autoGenerateIdentificationBarcodes = autoGenerateIdentificationBarcodes;
  }

  public boolean getAutoGenerateIdentificationBarcodes() {
    return autoGenerateIdentificationBarcodes;
  }

  /**
   * Generates a unique barcode. Note that the barcode will change when the alias is changed.
   * 
   * @param library
   */
  public void autoGenerateIdBarcode(Library library) {
    String barcode = library.getName() + "::" + library.getAlias();
    library.setIdentificationBarcode(barcode);
  }

  private void purgeListCache(Library l, boolean replace) {
    Cache cache = cacheManager.getCache("libraryListCache");
    DbUtils.updateListCache(cache, replace, l, Library.class);
  }

  private void purgeListCache(Library l) {
    purgeListCache(l, true);
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = Exception.class)
  @TriggersRemove(cacheName = { "libraryCache",
      "lazyLibraryCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long save(Library library) throws IOException {
    Long securityProfileId = library.getSecurityProfile().getProfileId();
    if (this.cascadeType != null) {
      securityProfileId = securityProfileDAO.save(library.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", library.getAlias());
    params.addValue("accession", library.getAccession());
    params.addValue("description", library.getDescription());
    params.addValue("locationBarcode", library.getLocationBarcode());
    params.addValue("paired", library.getPaired());
    params.addValue("sample_sampleId", library.getSample().getId());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("libraryType", library.getLibraryType().getLibraryTypeId());
    params.addValue("librarySelectionType", library.getLibrarySelectionType().getLibrarySelectionTypeId());
    params.addValue("libraryStrategyType", library.getLibraryStrategyType().getLibraryStrategyTypeId());
    params.addValue("platformName", library.getPlatformName());
    params.addValue("concentration", library.getInitialConcentration());
    params.addValue("creationDate", library.getCreationDate());
    params.addValue("lastModifier", library.getLastModifier().getUserId());
    params.addValue("lowQuality", library.isLowQuality());
    params.addValue("volume", library.getVolume());
    params.addValue("emptied", library.isEmpty());

    if (library.getQcPassed() != null) {
      params.addValue("qcPassed", library.getQcPassed().toString());
    } else {
      params.addValue("qcPassed", library.getQcPassed());
    }

    if (library.getId() == AbstractLibrary.UNSAVED_ID) {
      if (!libraryNamingScheme.allowDuplicateEntityNameFor("alias") && getByAlias(library.getAlias()) != null) {
        throw new IOException("NEW: A library with this alias already exists in the database");
      } else {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("libraryId");
        try {
          library.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

          String name = libraryNamingScheme.generateNameFor("name", library);
          library.setName(name);
          if (libraryNamingScheme.validateField("name", library.getName())
              && libraryNamingScheme.validateField("alias", library.getAlias())) {
            if (autoGenerateIdentificationBarcodes) {
              autoGenerateIdBarcode(library);
            } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user
            params.addValue("name", name);

            params.addValue("identificationBarcode", library.getIdentificationBarcode());

            Number newId = insert.executeAndReturnKey(params);
            if (newId.longValue() != library.getId()) {
              log.error("Expected library ID doesn't match returned value from database insert: rolling back...");
              new NamedParameterJdbcTemplate(template).update(LIBRARY_DELETE,
                  new MapSqlParameterSource().addValue("libraryId", newId.longValue()));
              throw new IOException("Something bad happened. Expected library ID doesn't match returned value from DB insert");
            }
          } else {
            throw new IOException("Cannot save library - invalid field:" + library.toString());
          }
        } catch (MisoNamingException e) {
          throw new IOException("Cannot save library - issue with naming scheme", e);
        }
      }
    } else {
      try {
        if (libraryNamingScheme.validateField("name", library.getName())
            && libraryNamingScheme.validateField("alias", library.getAlias())) {
          params.addValue("libraryId", library.getId());
          params.addValue("name", library.getName());
          params.addValue("alias", library.getAlias());
          params.addValue("description", library.getDescription());
          if (autoGenerateIdentificationBarcodes) {
            autoGenerateIdBarcode(library);
          } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user
          params.addValue("identificationBarcode", library.getIdentificationBarcode()).addValue("locationBarcode",
              library.getLocationBarcode());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(LIBRARY_UPDATE, params);
        } else {
          throw new IOException("Cannot save library - invalid field:" + library.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save library - issue with naming scheme", e);
      }
    }

    MapSqlParameterSource libparams = new MapSqlParameterSource();
    libparams.addValue("library_libraryId", library.getId());
    NamedParameterJdbcTemplate libNamedTemplate = new NamedParameterJdbcTemplate(template);
    libNamedTemplate.update(LIBRARY_TAGBARCODE_DELETE_BY_LIBRARY_ID, libparams);

    if (library.getTagBarcodes() != null && !library.getTagBarcodes().isEmpty()) {
      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template).withTableName("Library_TagBarcode");

      for (TagBarcode t : library.getTagBarcodes().values()) {
        MapSqlParameterSource ltParams = new MapSqlParameterSource();
        ltParams.addValue("library_libraryId", library.getId());
        ltParams.addValue("barcode_barcodeId", t.getId());
        eInsert.execute(ltParams);
      }
    }

    if (this.cascadeType != null) {
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        // total fudge to clear out the pool cache if this library is used in any pool by way of a dilution
        for (Pool p : poolDAO.listByLibraryId(library.getId())) {
          DbUtils.updateCaches(cacheManager, p, Pool.class);
        }

        sampleDAO.save(library.getSample());
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        for (Pool p : poolDAO.listByLibraryId(library.getId())) {
          DbUtils.updateCaches(cacheManager, p, Pool.class);
        }

        if (library.getSample() != null) {
          DbUtils.updateCaches(cacheManager, library.getSample(), Sample.class);
        }
      }

      if (!library.getNotes().isEmpty()) {
        for (Note n : library.getNotes()) {
          noteDAO.saveLibraryNote(library, n);
        }
      }

      purgeListCache(library);
    }

    return library.getId();
  }

  @Override
  @Cacheable(cacheName = "libraryCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public Library get(long libraryId) throws IOException {
    List eResults = template.query(LIBRARY_SELECT_BY_ID, new Object[] { libraryId }, new LibraryMapper());
    Library e = eResults.size() > 0 ? (Library) eResults.get(0) : null;
    return e;
  }

  @Override
  public Library getByBarcode(String barcode) throws IOException {
    List eResults = template.query(LIBRARY_SELECT_BY_IDENTIFICATION_BARCODE, new Object[] { barcode }, new LibraryMapper());
    Library e = eResults.size() > 0 ? (Library) eResults.get(0) : null;
    return e;
  }

  @Override
  public List<Library> getByBarcodeList(List<String> barcodeList) {
    return DbUtils.getByBarcodeList(template, barcodeList, LIBRARIES_SELECT_FROM_BARCODE_LIST, new LibraryMapper(true));
  }

  @Override
  public Library getByAlias(String alias) throws IOException {
    List eResults = template.query(LIBRARY_SELECT_BY_ALIAS, new Object[] { alias }, new LibraryMapper());
    Library e = eResults.size() > 0 ? (Library) eResults.get(0) : null;
    return e;
  }

  @Override
  public Boxable getByPositionId(long positionId) {
    List<Library> eResults = template.query(LIBRARY_SELECT_BY_BOX_POSITION_ID, new Object[] { positionId }, new LibraryMapper());
    Library e = eResults.size() > 0 ? eResults.get(0) : null;
    return e;
  }

  @Override
  public Library lazyGet(long libraryId) throws IOException {
    List<Library> eResults = template.query(LIBRARY_SELECT_BY_ID, new Object[] { libraryId }, new LibraryMapper(true));
    Library e = eResults.size() > 0 ? (Library) eResults.get(0) : null;
    return e;
  }

  public Library getByIdentificationBarcode(String barcode) throws IOException {
    List<Library> eResults = template.query(LIBRARY_SELECT_BY_IDENTIFICATION_BARCODE, new Object[] { barcode }, new LibraryMapper());
    Library e = eResults.size() > 0 ? (Library) eResults.get(0) : null;
    return e;
  }

  @Override
  public List<Library> listByLibraryDilutionId(long dilutionId) throws IOException {
    return template.query(LIBRARIES_BY_RELATED_DILUTION_ID, new Object[] { dilutionId }, new LibraryMapper(true));
  }

  @Override
  public List<Library> listBySampleId(long sampleId) throws IOException {
    return template.query(LIBRARIES_SELECT_BY_SAMPLE_ID, new Object[] { sampleId }, new LibraryMapper(true));
  }

  @Override
  public List<Library> listByProjectId(long projectId) throws IOException {
    return template.query(LIBRARIES_SELECT_BY_PROJECT_ID, new Object[] { projectId }, new LibraryMapper(true));
  }

  @Override
  @Cacheable(cacheName = "libraryListCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public List<Library> listAll() throws IOException {
    return template.query(LIBRARIES_SELECT, new LibraryMapper(true));
  }

  @Override
  public List<Library> listAllWithLimit(long limit) throws IOException {
    return template.query(LIBRARIES_SELECT_LIMIT, new Object[] { limit }, new LibraryMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<Library> listBySearch(String query) {
    String mySQLQuery = "%" + query.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
    return template.query(LIBRARIES_SELECT_BY_SEARCH, new Object[] { mySQLQuery, mySQLQuery, mySQLQuery, mySQLQuery },
        new LibraryMapper(true));
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "libraryCache",
      "lazyLibraryCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean remove(Library library) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (library.isDeletable()
        && (namedTemplate.update(LIBRARY_DELETE, new MapSqlParameterSource().addValue("libraryId", library.getId())) == 1)) {
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        for (Pool p : poolDAO.listByLibraryId(library.getId())) {
          DbUtils.updateCaches(cacheManager, p, Pool.class);
        }
        sampleDAO.save(library.getSample());
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        for (Pool p : poolDAO.listByLibraryId(library.getId())) {
          DbUtils.updateCaches(cacheManager, p, Pool.class);
        }

        if (library.getSample() != null) {
          DbUtils.updateCaches(cacheManager, library.getSample(), Sample.class);
        }
      }

      // remove any child library QCs
      for (LibraryQC lqc : library.getLibraryQCs()) {
        libraryQcDAO.remove(lqc);
      }

      purgeListCache(library, false);

      return true;
    }
    return false;
  }

  @Override
  public LibraryType getLibraryTypeById(long libraryTypeId) throws IOException {
    List eResults = template.query(LIBRARY_TYPE_SELECT_BY_ID, new Object[] { libraryTypeId }, new LibraryTypeMapper());
    LibraryType e = eResults.size() > 0 ? (LibraryType) eResults.get(0) : null;
    return e;
  }

  @Override
  public LibraryType getLibraryTypeByDescription(String description) throws IOException {
    List eResults = template.query(LIBRARY_TYPE_SELECT_BY_DESCRIPTION, new Object[] { description }, new LibraryTypeMapper());
    LibraryType e = eResults.size() > 0 ? (LibraryType) eResults.get(0) : null;
    return e;
  }

  @Override
  public LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException {
    List eResults = template.query(LIBRARY_TYPE_SELECT_BY_DESCRIPTION_AND_PLATFORM, new Object[] { description, platformType.getKey() },
        new LibraryTypeMapper());
    LibraryType e = eResults.size() > 0 ? (LibraryType) eResults.get(0) : null;
    return e;
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeById(long librarySelectionTypeId) throws IOException {
    List eResults = template.query(LIBRARY_SELECTION_TYPE_SELECT_BY_ID, new Object[] { librarySelectionTypeId },
        new LibrarySelectionTypeMapper());
    LibrarySelectionType e = eResults.size() > 0 ? (LibrarySelectionType) eResults.get(0) : null;
    return e;
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException {
    List eResults = template.query(LIBRARY_SELECTION_TYPE_SELECT_BY_NAME, new Object[] { name }, new LibrarySelectionTypeMapper());
    LibrarySelectionType e = eResults.size() > 0 ? (LibrarySelectionType) eResults.get(0) : null;
    return e;
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException {
    List eResults = template.query(LIBRARY_STRATEGY_TYPE_SELECT_BY_ID, new Object[] { libraryStrategyTypeId },
        new LibraryStrategyTypeMapper());
    LibraryStrategyType e = eResults.size() > 0 ? (LibraryStrategyType) eResults.get(0) : null;
    return e;
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException {
    List eResults = template.query(LIBRARY_STRATEGY_TYPE_SELECT_BY_NAME, new Object[] { name }, new LibraryStrategyTypeMapper());
    LibraryStrategyType e = eResults.size() > 0 ? (LibraryStrategyType) eResults.get(0) : null;
    return e;
  }

  @Override
  public List<LibraryType> listLibraryTypesByPlatform(String platformType) throws IOException {
    return template.query(LIBRARY_TYPES_SELECT_BY_PLATFORM, new Object[] { platformType }, new LibraryTypeMapper());
  }

  @Override
  public List<LibraryType> listAllLibraryTypes() throws IOException {
    return template.query(LIBRARY_TYPES_SELECT, new LibraryTypeMapper());
  }

  @Override
  public List<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException {
    return template.query(LIBRARY_SELECTION_TYPES_SELECT, new LibrarySelectionTypeMapper());
  }

  @Override
  public List<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException {
    return template.query(LIBRARY_STRATEGY_TYPES_SELECT, new LibraryStrategyTypeMapper());
  }

  @Override
  public TagBarcode getTagBarcodeById(long tagBarcodeId) throws IOException {
    List eResults = template.query(TAG_BARCODE_SELECT_BY_ID, new Object[] { tagBarcodeId }, new TagBarcodeMapper());
    TagBarcode e = eResults.size() > 0 ? (TagBarcode) eResults.get(0) : null;
    return e;
  }

  public TagBarcode getTagBarcodeByName(String name) throws IOException {
    List eResults = template.query(TAG_BARCODE_SELECT_BY_NAME, new Object[] { name }, new TagBarcodeMapper());
    TagBarcode e = eResults.size() > 0 ? (TagBarcode) eResults.get(0) : null;
    return e;
  }

  public TagBarcode getTagBarcodeByLibraryId(long libraryId) throws IOException {
    List eResults = template.query(TAG_BARCODE_SELECT_BY_LIBRARY_ID, new Object[] { libraryId }, new TagBarcodeMapper());
    TagBarcode e = eResults.size() > 0 ? (TagBarcode) eResults.get(0) : null;
    return e;
  }

  public HashMap<Integer, TagBarcode> getTagBarcodesByLibraryId(long libraryId) throws IOException {
    List<TagBarcode> eResults = template.query(TAG_BARCODE_SELECT_BY_LIBRARY_ID, new Object[] { libraryId }, new TagBarcodeMapper());
    if (!eResults.isEmpty()) {
      HashMap<Integer, TagBarcode> map = new HashMap<Integer, TagBarcode>();
      int count = 1;
      for (TagBarcode t : eResults) {
        map.put(count, t);
        count++;
      }
      return map;
    }
    return new HashMap<Integer, TagBarcode>();
  }

  @Override
  public List<TagBarcode> listTagBarcodesByPlatform(String platformType) throws IOException {
    return template.query(TAG_BARCODES_SELECT_BY_PLATFORM, new Object[] { platformType }, new TagBarcodeMapper());
  }

  @Override
  public List<TagBarcode> listTagBarcodesByStrategyName(String strategyName) throws IOException {
    return template.query(TAG_BARCODES_SELECT_BY_STRATEGY_NAME, new Object[] { strategyName }, new TagBarcodeMapper());
  }

  @Override
  public List<TagBarcode> listAllTagBarcodes() throws IOException {
    return template.query(TAG_BARCODES_SELECT, new TagBarcodeMapper());
  }

  public ChangeLogStore getChangeLogDAO() {
    return changeLogDAO;
  }

  public void setChangeLogDAO(ChangeLogStore changeLogDAO) {
    this.changeLogDAO = changeLogDAO;
  }

  public SecurityStore getSecurityDAO() {
    return securityDAO;
  }

  public void setSecurityDAO(SecurityStore securityDAO) {
    this.securityDAO = securityDAO;
  }

  public class LibraryMapper extends CacheAwareRowMapper<Library> {
    public LibraryMapper() {
      super(Library.class);
    }

    public LibraryMapper(boolean lazy) {
      super(Library.class, lazy);
    }

    @Override
    public Library mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("libraryId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
               log.info("Cache hit on map for library " + id);
               log.info("Cache hit on map for sample with library " + element);
               Library library = (Library) element.getObjectValue();
               if (library == null) throw new NullPointerException("The cache is full of lies!!!");
               if (library.getId() == 0) {
                  DbUtils.updateCaches(lookupCache(cacheManager), id);
               } else {
                  return (Library) element.getObjectValue();
               }
        }
      }

      Library library = dataObjectFactory.getLibrary();
      library.setId(id);
      library.setName(rs.getString("name"));
      library.setDescription(rs.getString("description"));
      library.setAlias(rs.getString("alias"));
      library.setAccession(rs.getString("accession"));
      library.setCreationDate(rs.getDate("creationDate"));
      library.setIdentificationBarcode(rs.getString("identificationBarcode"));
      library.setLocationBarcode(rs.getString("locationBarcode"));
      library.setPaired(rs.getBoolean("paired"));
      library.setInitialConcentration(rs.getDouble("concentration"));
      library.setPlatformName(rs.getString("platformName"));
      library.setLowQuality(rs.getBoolean("lowQuality"));
      library.setVolume(rs.getInt("volume"));
      library.setEmpty(rs.getBoolean("emptied"));
      library.setBoxPositionId(rs.getLong("boxPositionId"));
      library.setBoxAlias(rs.getString("boxAlias"));
      library.setBoxId(rs.getLong("boxId"));
      int row = rs.getInt("boxRow");
      if (!rs.wasNull()) library.setBoxPosition(BoxUtils.getPositionString(row, rs.getInt("boxColumn")));
      if (rs.getString("qcPassed") != null) {
        library.setQcPassed(Boolean.parseBoolean(rs.getString("qcPassed")));
      } else {
        library.setQcPassed(null);
      }

      try {
        library.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
        library.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));

        library.setLibraryType(getLibraryTypeById(rs.getLong("libraryType")));
        library.setLibrarySelectionType(getLibrarySelectionTypeById(rs.getLong("librarySelectionType")));
        library.setLibraryStrategyType(getLibraryStrategyTypeById(rs.getLong("libraryStrategyType")));

        library.setTagBarcodes(getTagBarcodesByLibraryId(id));

        if (!isLazy()) {
          library.setSample(sampleDAO.get(rs.getLong("sample_sampleId")));

          for (LibraryDilution dil : dilutionDAO.listByLibraryId(id)) {
            library.addDilution(dil);
          }

          for (LibraryQC qc : libraryQcDAO.listByLibraryId(id)) {
            library.addQc(qc);
          }

          library.setNotes(noteDAO.listByLibrary(id));
        } else {
          library.setSample(sampleDAO.lazyGet(rs.getLong("sample_sampleId")));
        }
        library.getChangeLog().addAll(getChangeLogDAO().listAllById(TABLE_NAME, id));
      } catch (IOException e1) {
        log.error("library row mapper", e1);
      } catch (MalformedLibraryQcException e) {
        log.error("library row mapper", e);
      } catch (MalformedDilutionException e) {
        log.error("library row mapper", e);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), library));
      }

      return library;
    }
  }

  public class LibraryTypeMapper implements RowMapper<LibraryType> {
    @Override
    public LibraryType mapRow(ResultSet rs, int rowNum) throws SQLException {
      LibraryType lt = new LibraryType();
      lt.setLibraryTypeId(rs.getLong("libraryTypeId"));
      lt.setDescription(rs.getString("description"));
      lt.setPlatformType(rs.getString("platformType"));
      return lt;
    }
  }

  public class LibrarySelectionTypeMapper implements RowMapper<LibrarySelectionType> {
    @Override
    public LibrarySelectionType mapRow(ResultSet rs, int rowNum) throws SQLException {
      LibrarySelectionType lst = new LibrarySelectionType();
      lst.setLibrarySelectionTypeId(rs.getLong("librarySelectionTypeId"));
      lst.setName(rs.getString("name"));
      lst.setDescription(rs.getString("description"));
      return lst;
    }
  }

  public class LibraryStrategyTypeMapper implements RowMapper<LibraryStrategyType> {
    @Override
    public LibraryStrategyType mapRow(ResultSet rs, int rowNum) throws SQLException {
      LibraryStrategyType lst = new LibraryStrategyType();
      lst.setLibraryStrategyTypeId(rs.getLong("libraryStrategyTypeId"));
      lst.setName(rs.getString("name"));
      lst.setDescription(rs.getString("description"));
      return lst;
    }
  }

  public class TagBarcodeMapper implements RowMapper<TagBarcode> {
    @Override
    public TagBarcode mapRow(ResultSet rs, int rowNum) throws SQLException {
      TagBarcode tb = new TagBarcodeImpl();
      tb.setId(rs.getLong("tagId"));
      tb.setName(rs.getString("name"));
      tb.setSequence(rs.getString("sequence"));
      tb.setPlatformType(PlatformType.get(rs.getString("platformName")));
      tb.setStrategyName(rs.getString("strategyName"));
      return tb;
    }
  }
}
