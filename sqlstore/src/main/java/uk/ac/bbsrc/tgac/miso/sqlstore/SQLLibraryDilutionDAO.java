/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractDilution;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.TargetedSequencingStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
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
@Transactional(rollbackFor = Exception.class)
public class SQLLibraryDilutionDAO implements LibraryDilutionStore {
  public static String DILUTION_SELECT_BY_ID_AND_LIBRARY_PLATFORM = "SELECT DISTINCT * " + "FROM Library l "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = l.libraryId "
      + "INNER JOIN emPCRDilution ed ON ed.library_libraryId = l.libraryId " + "WHERE ld.dilutionId = ? OR ed.dilutionId = ? "
      + "AND l.platformName = ?";

  public static String LIBRARY_DILUTION_SELECT = "SELECT dilutionId, name, concentration, preMigrationId, library_libraryId, "
      + "identificationBarcode, creationDate, dilutionUserName, securityProfile_profileId, targetedSequencingId, " + "lastUpdated "
      + "FROM LibraryDilution";

  public static final String LIBRARY_DILUTIONS_SELECT_LIMIT = LIBRARY_DILUTION_SELECT + " ORDER BY dilutionId DESC LIMIT ?";

  public static String LIBRARY_DILUTION_SELECT_BY_LIBRARY_PLATFORM = "SELECT ld.dilutionId, ld.name, ld.concentration, ld.preMigrationId, "
      + "ld.library_libraryId, ld.identificationBarcode, ld.creationDate, ld.dilutionUserName, ld.securityProfile_profileId, "
      + "ld.targetedSequencingId, l.platformName, ld.lastUpdated " + "FROM LibraryDilution ld, Library l "
      + "WHERE ld.library_libraryId = l.libraryId " + "AND l.platformName = ? ";

  public static String LIBRARY_DILUTION_COUNT_BY_LIBRARY_PLATFORM = "SELECT COUNT(*) FROM LibraryDilution ld, Library l "
      + "WHERE ld.library_libraryId = l.libraryId " + "AND l.platformName = ? ";

  public static String LIBRARY_DILUTION_SELECT_BY_PROJECT_AND_LIBRARY_PLATFORM = "SELECT ld.* FROM Project p "
      + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " + "WHERE li.platformName=? " + "AND p.projectId=?";

  public static String LIBRARY_DILUTION_SELECT_BY_PROJECT = "SELECT ld.* FROM Project p "
      + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " + "WHERE p.projectId=?";

  public static final String LIBRARY_DILUTION_SELECT_BY_DILUTION_ID = LIBRARY_DILUTION_SELECT + " WHERE dilutionId=?";

  public static final String LIBRARY_DILUTION_SELECT_BY_LIBRARY_ID = LIBRARY_DILUTION_SELECT + " WHERE library_libraryId=?";

  public static final String LIBRARY_DILUTION_SELECT_BY_IDENTIFICATION_BARCODE = LIBRARY_DILUTION_SELECT + " WHERE identificationBarcode=?";

  public static final String LIBRARY_DILUTION_UPDATE = "UPDATE LibraryDilution "
      + "SET name=:name, concentration=:concentration, preMigrationId=:preMigrationId, library_libraryId=:library_libraryId, "
      + "identificationBarcode=:identificationBarcode, creationDate=:creationDate, "
      + "securityProfile_profileId=:securityProfile_profileId, targetedSequencingId=:targetedSequencingId, "
      + "lastUpdated=:lastUpdated " + "WHERE dilutionId=:dilutionId";

  public static final String LIBRARY_DILUTION_DELETE = "DELETE FROM LibraryDilution WHERE dilutionId=:dilutionId";

  public static String LIBRARY_DILUTION_SELECT_BY_SEARCH_ONLY = "SELECT ld.dilutionId, ld.name, ld.concentration, ld.preMigrationId, "
      + "ld.library_libraryId, ld.identificationBarcode, ld.creationDate, ld.dilutionUserName, ld.securityProfile_profileId, "
      + "ld.targetedSequencingId, ld.lastUpdated " + "FROM LibraryDilution ld JOIN Library l ON l.libraryId = ld.library_libraryId "
      + "WHERE (UPPER(ld.name) LIKE :search OR UPPER(ld.identificationBarcode) LIKE :search OR "
      + "UPPER(l.name) LIKE :search OR UPPER(l.alias) LIKE :search OR UPPER(l.description) LIKE :search)";

  public static String LIBRARY_DILUTION_COUNT_BY_SEARCH_ONLY = "SELECT COUNT(*) FROM LibraryDilution ld JOIN Library l ON l.libraryId = ld.library_libraryId "
      + "WHERE (UPPER(ld.name) LIKE :search OR UPPER(ld.identificationBarcode) LIKE :search OR "
      + "UPPER(l.name) LIKE :search OR UPPER(l.alias) LIKE :search OR UPPER(l.description) LIKE :search)";

  public static String LIBRARY_DILUTION_SELECT_BY_SEARCH = LIBRARY_DILUTION_SELECT_BY_SEARCH_ONLY + " AND l.platformName = :platformName";
  public static String LIBRARY_DILUTION_COUNT_BY_SEARCH = LIBRARY_DILUTION_COUNT_BY_SEARCH_ONLY + " AND l.platformName = :platformName";

  protected static final Logger log = LoggerFactory.getLogger(SQLLibraryDilutionDAO.class);

  private JdbcTemplate template;
  private LibraryStore libraryDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private CascadeType cascadeType;
  private TargetedSequencingStore targetedSequencingDAO;
  private boolean autoGenerateIdentificationBarcodes;

  @Autowired
  private NamingScheme namingScheme;

  public NamingScheme getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Autowired
  private CacheManager cacheManager;

  @CoverageIgnore
  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @CoverageIgnore
  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  @CoverageIgnore
  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  @CoverageIgnore
  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @CoverageIgnore
  public void setLibraryDAO(LibraryStore libraryDAO) {
    this.libraryDAO = libraryDAO;
  }

  @CoverageIgnore
  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  @CoverageIgnore
  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  @CoverageIgnore
  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  @CoverageIgnore
  public void setTargetedSequencingDAO(TargetedSequencingStore targetedSequencingDAO) {
    this.targetedSequencingDAO = targetedSequencingDAO;
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
  public void autoGenerateIdBarcode(LibraryDilution dilution) {
    String barcode = dilution.getName() + "::" + dilution.getLibrary().getAlias();
    dilution.setIdentificationBarcode(barcode);
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearchAndPlatform(String query, PlatformType platformType) {
    String squery = DbUtils.convertStringToSearchQuery(query);
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("platformName", platformType.getKey());
    params.addValue("search", squery);
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.query(LIBRARY_DILUTION_SELECT_BY_SEARCH, params, new LibraryDilutionMapper(true));
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearchOnly(String query) {
    String squery = DbUtils.convertStringToSearchQuery(query);
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("search", squery);
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.query(LIBRARY_DILUTION_SELECT_BY_SEARCH_ONLY, params, new LibraryDilutionMapper(true));
  }

  @Override
  public List<LibraryDilution> listByLibraryId(long libraryId) throws IOException {
    return template.query(LIBRARY_DILUTION_SELECT_BY_LIBRARY_ID, new Object[] { libraryId }, new LibraryDilutionMapper(true));
  }

  @Override
  public Collection<LibraryDilution> listAll() throws IOException {
    return template.query(LIBRARY_DILUTION_SELECT, new LibraryDilutionMapper(true));
  }

  @Override
  public Collection<LibraryDilution> listAllWithLimit(long limit) throws IOException {
    return template.query(LIBRARY_DILUTIONS_SELECT_LIMIT, new Object[] { limit }, new LibraryDilutionMapper(true));
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformType) throws IOException {
    return template.query(LIBRARY_DILUTION_SELECT_BY_LIBRARY_PLATFORM, new Object[] { platformType.getKey() },
        new LibraryDilutionMapper(true));
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException {
    return template.query(LIBRARY_DILUTION_SELECT_BY_PROJECT, new Object[] { projectId }, new LibraryDilutionMapper(true));
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectAndPlatform(long projectId, PlatformType platformType)
      throws IOException {
    return template.query(LIBRARY_DILUTION_SELECT_BY_PROJECT_AND_LIBRARY_PLATFORM, new Object[] { platformType.getKey(), projectId },
        new LibraryDilutionMapper(true));
  }

  @Override
  @Cacheable(cacheName = "libraryDilutionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public LibraryDilution get(long dilutionId) throws IOException {
    List<LibraryDilution> eResults = template.query(LIBRARY_DILUTION_SELECT_BY_DILUTION_ID, new Object[] { dilutionId },
        new LibraryDilutionMapper());
    LibraryDilution e = eResults.size() > 0 ? (LibraryDilution) eResults.get(0) : null;
    return e;
  }

  @Override
  @Cacheable(cacheName = "libraryDilutionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))

  public LibraryDilution lazyGet(long id) throws IOException {
    List<LibraryDilution> eResults = template.query(LIBRARY_DILUTION_SELECT_BY_DILUTION_ID, new Object[] { id },
        new LibraryDilutionMapper(true));
    LibraryDilution e = eResults.size() > 0 ? (LibraryDilution) eResults.get(0) : null;
    return e;
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException {
    if (barcode == null) throw new NullPointerException("Cannot search for null barcode");
    List<LibraryDilution> eResults = template.query(LIBRARY_DILUTION_SELECT_BY_IDENTIFICATION_BARCODE, new Object[] { barcode },
        new LibraryDilutionMapper());
    LibraryDilution e = eResults.size() > 0 ? (LibraryDilution) eResults.get(0) : null;
    return e;
  }

  @Override
  @TriggersRemove(cacheName = { "libraryDilutionCache",
      "lazyLibraryDilutionCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public long save(LibraryDilution dilution) throws IOException {
    Long securityProfileId = dilution.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) {
      securityProfileId = securityProfileDAO.save(dilution.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("concentration", dilution.getConcentration());
    params.addValue("preMigrationId", dilution.getPreMigrationId());
    params.addValue("library_libraryId", dilution.getLibrary().getId());
    params.addValue("creationDate", dilution.getCreationDate());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("dilutionUserName", dilution.getDilutionUserName());
    params.addValue("lastUpdated", dilution.getLastModified());
    if (dilution.getTargetedSequencing() != null) {
      params.addValue("targetedSequencingId", dilution.getTargetedSequencing().getId());
    } else {
      params.addValue("targetedSequencingId", null);
    }

    if (dilution.getId() == AbstractDilution.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName("LibraryDilution").usingGeneratedKeyColumns("dilutionId");
      try {
        dilution.setId(DbUtils.getAutoIncrement(template, "LibraryDilution"));

        String name = namingScheme.generateNameFor(dilution);
        dilution.setName(name);

        LimsUtils.validateNameOrThrow(dilution, namingScheme);
        if (autoGenerateIdentificationBarcodes) {
          autoGenerateIdBarcode(dilution);
        } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user
        params.addValue("name", name);

        params.addValue("identificationBarcode", dilution.getIdentificationBarcode());

        Number newId = insert.executeAndReturnKey(params);
        if (newId.longValue() != dilution.getId()) {
          log.error("Expected LibraryDilution ID doesn't match returned value from database insert: rolling back...");
          new NamedParameterJdbcTemplate(template).update(LIBRARY_DILUTION_DELETE,
              new MapSqlParameterSource().addValue("dilutionId", newId.longValue()));
          throw new IOException("Something bad happened. Expected LibraryDilution ID doesn't match returned value from DB insert");
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save LibraryDilution - issue with naming scheme", e);
      }
    } else {
      LimsUtils.validateNameOrThrow(dilution, namingScheme);
      params.addValue("dilutionId", dilution.getId());
      params.addValue("name", dilution.getName());
      if (dilution.getTargetedSequencing() != null) {
        params.addValue("targetedSequencingId", dilution.getTargetedSequencing().getId());
      } else {
        params.addValue("targetedSequencingId", null);
      }
      if (autoGenerateIdentificationBarcodes) {
        autoGenerateIdBarcode(dilution);
      } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user
      params.addValue("identificationBarcode", dilution.getIdentificationBarcode());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(LIBRARY_DILUTION_UPDATE, params);
    }

    if (this.cascadeType != null) {
      Library l = dilution.getLibrary();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (l != null) libraryDAO.save(l);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (l != null) {
          DbUtils.updateCaches(cacheManager, l, Library.class);
        }
      }
    }

    return dilution.getId();
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM LibraryDilution");
  }

  /*
   * To be used in conjunction with listBySearchOffsetAndNumResultsAndPlatform as datatables requires a count of all dilutions available
   * from the query to enable paginating.
   */
  @Override
  public Integer countAllBySearchAndPlatform(String search, PlatformType platform) throws IOException {
    Integer rtn;
    if (LimsUtils.isStringEmptyOrNull(search)) {
      rtn = template.queryForInt(LIBRARY_DILUTION_COUNT_BY_LIBRARY_PLATFORM, platform.getKey());
    } else {
      final String querystr = DbUtils.convertStringToSearchQuery(search);
      final MapSqlParameterSource params = new MapSqlParameterSource();
      final NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      params.addValue("platformName", platform.getKey());
      params.addValue("search", querystr);
      rtn = namedTemplate.queryForInt(LIBRARY_DILUTION_COUNT_BY_SEARCH, params);
    }
    return rtn;
  }

  @Override
  public List<LibraryDilution> listBySearchOffsetAndNumResultsAndPlatform(int offset, int limit, String search, String sortDir,
      String sortCol, PlatformType platform) throws IOException {
    List<LibraryDilution> rtn;
    if (offset < 0 || limit < 0) {
      throw new IOException("Limit and Offset must be greater than -1");
    }
    if (!Arrays.asList("DESC", "ASC").contains(sortDir.toUpperCase())) {
      throw new IOException("Bad value in sortDir");
    }
    if (LimsUtils.isStringEmptyOrNull(search)) {

      final String query = LIBRARY_DILUTION_SELECT_BY_LIBRARY_PLATFORM + " ORDER BY " + sortCol + " " + sortDir + " LIMIT " + limit
          + " OFFSET " + offset;
      rtn = template.query(query, new Object[] { platform.getKey() }, new LibraryDilutionMapper(true));
    } else {
      final String querystr = DbUtils.convertStringToSearchQuery(search);
      final String query = LIBRARY_DILUTION_SELECT_BY_SEARCH + " ORDER BY " + sortCol + " " + sortDir + " LIMIT " + limit + " OFFSET "
          + offset;

      final MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("platformName", platform.getKey());
      params.addValue("search", querystr);
      final NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      rtn = namedTemplate.query(query, params, new LibraryDilutionMapper(true));

    }
    return rtn;
  }

  @Override
  public int countByPlatform(PlatformType platform) throws IOException {
    return template.queryForInt(
        "SELECT COUNT(*) from LibraryDilution ld INNER JOIN Library l ON ld.library_libraryId = l.libraryId WHERE l.platformName LIKE '"
            + platform.getKey() + "';");
  }
  @Override
  @TriggersRemove(cacheName = { "libraryDilutionCache",
      "lazyLibraryDilutionCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public boolean remove(LibraryDilution d) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (d.isDeletable()
        && (namedTemplate.update(LIBRARY_DILUTION_DELETE, new MapSqlParameterSource().addValue("dilutionId", d.getId())) == 1)) {
      Library l = d.getLibrary();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (l != null) libraryDAO.save(l);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (l != null) {
          DbUtils.updateCaches(cacheManager, l, Library.class);
        }
      }
      return true;
    }
    return false;
  }

  public class LibraryDilutionMapper extends CacheAwareRowMapper<LibraryDilution> {
    public LibraryDilutionMapper() {
      super(LibraryDilution.class);
    }

    public LibraryDilutionMapper(boolean lazy) {
      super(LibraryDilution.class, lazy);
    }

    @Override
    public LibraryDilution mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("dilutionId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for LibraryDilution " + id);
          LibraryDilution dilution = (LibraryDilution) element.getObjectValue();
          if (dilution == null) throw new NullPointerException("The LazyLibraryDilutionMapper cache is full of lies!!!");
          if (dilution.getId() == 0) {
            DbUtils.updateCaches(lookupCache(cacheManager), id);
          } else {
            return (LibraryDilution) element.getObjectValue();
          }
        }
      }
      LibraryDilution libraryDilution = dataObjectFactory.getLibraryDilution();
      libraryDilution.setId(id);
      libraryDilution.setName(rs.getString("name"));
      libraryDilution.setConcentration(rs.getDouble("concentration"));
      libraryDilution.setPreMigrationId(rs.getLong("preMigrationId"));
      if (rs.wasNull()) libraryDilution.setPreMigrationId(null);
      libraryDilution.setIdentificationBarcode(rs.getString("identificationBarcode"));
      libraryDilution.setCreationDate(rs.getDate("creationDate"));
      libraryDilution.setDilutionUserName(rs.getString("dilutionUserName"));
      libraryDilution.setLastModified(rs.getTimestamp("lastUpdated"));

      try {
        libraryDilution.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));

        if (!isLazy()) {
          libraryDilution.setTargetedSequencing(targetedSequencingDAO.get(rs.getLong("targetedSequencingId")));
          libraryDilution.setLibrary(libraryDAO.get(rs.getLong("library_libraryId")));
        } else {
          libraryDilution.setTargetedSequencing(targetedSequencingDAO.lazyGet(rs.getLong("targetedSequencingId")));
          libraryDilution.setLibrary(libraryDAO.lazyGet(rs.getLong("library_libraryId")));
        }
      } catch (IOException e1) {
        log.error("library dilution row mapper", e1);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), libraryDilution));
      }

      return libraryDilution;
    }
  }
}
