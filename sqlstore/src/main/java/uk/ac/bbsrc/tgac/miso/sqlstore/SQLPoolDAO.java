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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;

import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.store.SecurityStore;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractPool;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolQcException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.WatcherStore;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DaoLookup;
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
public class SQLPoolDAO implements PoolStore {
  private static final String TABLE_NAME = "Pool";

  private static final String POOL_CHANGE_LOG_INSERT = "INSERT INTO PoolChangeLog (poolId, columnsChanged, userId, message) VALUES (?, '', ?, ?)";

  private static final String POOL_COUNT = "SELECT COUNT(*) " + "FROM " + TABLE_NAME + " p";

  public static final String POOL_COUNT_BY_PLATFORM = POOL_COUNT + " WHERE p.platformType=?";

  private static final String POOL_SELECT = "SELECT p.poolId, p.concentration, p.identificationBarcode, p.name, p.alias, p.description, p.creationDate, "
      + "p.securityProfile_profileId, p.platformType, p.ready, p.qcPassed, p.lastModifier, pmod.lastModified, p.boxPositionId, p.volume, p.discarded, b.boxId, "
      + "b.alias AS boxAlias, b.locationBarcode AS boxLocation, bp.row AS boxRow, bp.column AS boxColumn " + "FROM " + TABLE_NAME + " p "
      + "LEFT JOIN BoxPosition bp ON bp.boxPositionId = p.boxPositionId " + "LEFT JOIN Box b ON b.boxId = bp.boxId "
      + "LEFT JOIN (SELECT poolId, MAX(changeTime) AS lastModified FROM PoolChangeLog GROUP BY poolId) pmod ON p.poolId = pmod.poolId";

  public static final String POOL_SELECT_BY_POOL_ID = POOL_SELECT + " WHERE p.poolId=?";

  public static final String POOL_SELECT_BY_PLATFORM = POOL_SELECT + " WHERE p.platformType=?";

  public static final String POOL_SELECT_BY_PLATFORM_AND_SEARCH = POOL_SELECT + " WHERE p.platformType=? AND " + "(UPPER(p.name) LIKE ? OR "
      + "UPPER(p.alias) LIKE ? OR UPPER(p.identificationBarcode) LIKE ? OR UPPER(p.description) LIKE ?) ";

  public static final String POOL_SELECT_BY_PLATFORM_AND_READY = POOL_SELECT_BY_PLATFORM + " AND p.ready=1";

  public static final String POOL_SELECT_BY_PLATFORM_AND_READY_AND_SEARCH = POOL_SELECT_BY_PLATFORM_AND_SEARCH + " AND p.ready=1";

  public static final String POOL_SELECT_BY_IDENTIFICATION_BARCODE = POOL_SELECT + " WHERE p.identificationBarcode = ?";

  public static final String POOL_SELECT_FROM_BARCODE_LIST = POOL_SELECT + " WHERE p.identificationBarcode IN (";

  public static final String POOL_SELECT_FROM_ID_LIST = POOL_SELECT + " WHERE p.poolId IN (";

  public static final String POOL_SELECT_BY_BOX_POSITION_ID = POOL_SELECT + " WHERE p.boxPositionId = ?";

  public static final String POOL_COUNT_BY_PLATFORM_AND_SEARCH = POOL_COUNT + " WHERE p.platformType=? AND " + "(UPPER(p.name) LIKE ? OR "
      + "UPPER(p.alias) LIKE ? OR UPPER(p.identificationBarcode) LIKE ? OR UPPER(p.description) LIKE ?) ";

  public static final String POOL_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET alias=:alias, concentration=:concentration, identificationBarcode=:identificationBarcode, creationDate=:creationDate, securityProfile_profileId=:securityProfile_profileId, "
      + "platformType=:platformType, ready=:ready, qcPassed=:qcPassed, lastModifier=:lastModifier, discarded=:discarded, volume=:volume, description=:description "
      + "WHERE poolId=:poolId";

  public static final String POOL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE poolId=:poolId";

  public static final String POOL_ELEMENT_SELECT_BY_POOL_ID = "SELECT pool_poolId, elementType, elementId FROM Pool_Elements WHERE pool_poolId = ?";

  public static final String POOL_EXPERIMENT_DELETE_BY_POOL_ID = "DELETE FROM Pool_Experiment " + "WHERE pool_poolId=:pool_poolId";

  public static final String EMPCR_POOL_SELECT_BY_RELATED_PROJECT = POOL_SELECT
      + " WHERE p.poolId IN (SELECT DISTINCT pool_poolId FROM Project p " + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId "
      + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId "
      + "LEFT JOIN emPCR e ON e.dilution_dilutionId = ld.dilutionId " + "LEFT JOIN emPCRDilution ed ON ed.emPCR_pcrId = e.pcrId "
      + "LEFT JOIN Pool_Elements ple ON ple.elementId = ed.dilutionId "
      + "WHERE p.projectId = ? AND ple.elementType = 'uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution')";

  public static final String DILUTION_POOL_SELECT_BY_RELATED_PROJECT = POOL_SELECT
      + " WHERE p.poolId IN (SELECT DISTINCT pool_poolId FROM Project p "

      + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId "
      + "LEFT JOIN Pool_Elements pld ON pld.elementId = ld.dilutionId "
      + "WHERE p.projectId = ? AND pld.elementType = 'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution')";

  public static final String PLATE_POOL_SELECT_BY_RELATED_PROJECT = POOL_SELECT
      + " WHERE p.poolId IN (SELECT DISTINCT pool_poolId FROM Project p "

      + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN Plate_Elements pe ON li.libraryId = pe.elementId " + "INNER JOIN Plate pl ON pl.plateId = pe.plate_plateId "
      + "LEFT JOIN Pool_Elements pld ON pld.elementId = pl.plateId " + "WHERE p.projectId= ? AND pld.elementType LIKE '%Plate')";

  public static final String POOL_ID_SELECT_BY_RELATED = "SELECT DISTINCT pool_poolId AS poolId FROM Pool_Elements, "
      + "(SELECT dilutionId as elementId, library_libraryId as libraryId, 'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution' as elementType FROM LibraryDilution "
      + "UNION ALL SELECT emPCRDilution.dilutionId as elementId, library_libraryId as libraryId, 'uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution' as elementType "
      + "FROM LibraryDilution JOIN emPCR ON LibraryDilution.dilutionId = emPCR.dilution_dilutionId JOIN emPCRDilution ON emPCR.pcrId = emPCRDilution.emPCR_pcrID"
      + ") AS Contents WHERE Contents.elementType = Pool_Elements.elementType AND Contents.elementId = Pool_Elements.elementId ";

  public static final String POOL_ID_SELECT_BY_RELATED_LIBRARY = POOL_ID_SELECT_BY_RELATED + "AND Contents.libraryId = ?";
  public static final String POOL_ID_SELECT_BY_RELATED_SAMPLE = POOL_ID_SELECT_BY_RELATED
      + "AND Contents.libraryId IN (SELECT libraryId FROM Library WHERE sample_sampleId = ?)";

  public static final String POOL_ELEMENT_DELETE_BY_POOL_ID = "DELETE FROM Pool_Elements " + "WHERE pool_poolId=:pool_poolId";

  // ILLUMINA

  public static final String ILLUMINA_POOL_SELECT = POOL_SELECT + " WHERE p.platformType='Illumina'";

  public static final String ILLUMINA_POOL_SELECT_BY_POOL_ID = ILLUMINA_POOL_SELECT + " AND poolId=?";

  public static final String ILLUMINA_POOL_SELECT_BY_READY = ILLUMINA_POOL_SELECT + " AND ready=1";

  public static final String ILLUMINA_POOL_SELECT_BY_ID_BARCODE = ILLUMINA_POOL_SELECT + " AND identificationBarcode=?";

  public static final String ILLUMINA_POOL_SELECT_BY_EXPERIMENT_ID = ILLUMINA_POOL_SELECT
      + "AND p.poolId IN (SELECT pool_poolId FROM Pool_Experiment WHERE experiments_experimentId=?) ";

  // 454
  public static final String LS454_POOL_SELECT = POOL_SELECT + " WHERE p.platformType='LS454'";

  public static final String LS454_POOL_SELECT_BY_POOL_ID = LS454_POOL_SELECT + " AND poolId=?";

  public static final String LS454_POOL_SELECT_BY_READY = LS454_POOL_SELECT + " AND ready=1";

  public static final String LS454_POOL_SELECT_BY_ID_BARCODE = LS454_POOL_SELECT + " AND identificationBarcode=?";

  public static final String LS454_POOL_SELECT_BY_EXPERIMENT_ID = LS454_POOL_SELECT
      + "AND p.poolId IN (SELECT pool_poolId FROM Pool_Experiment WHERE experiments_experimentId=?)";

  public static final String EMPCR_DILUTIONS_BY_RELATED_LS454_POOL_ID = "SELECT p.dilutions_dilutionId, l.concentration, l.emPCR_pcrId, l.identificationBarcode, l.name, l.alias, l.creationDate, l.securityProfile_profileId "
      + "FROM emPCRDilution l, Pool_emPCRDilution p " + "WHERE l.dilutionId=p.dilutions_dilutionId " + "AND p.pool_poolId=?";

  // SOLiD
  public static final String SOLID_POOL_SELECT = POOL_SELECT + " WHERE p.platformType='Solid'";

  public static final String SOLID_POOL_SELECT_BY_POOL_ID = SOLID_POOL_SELECT + " AND poolId=?";

  public static final String SOLID_POOL_SELECT_BY_READY = SOLID_POOL_SELECT + " AND ready=1";

  public static final String SOLID_POOL_SELECT_BY_ID_BARCODE = SOLID_POOL_SELECT + " AND identificationBarcode=?";

  public static final String SOLID_POOL_SELECT_BY_EXPERIMENT_ID = SOLID_POOL_SELECT
      + " AND p.poolId IN (SELECT pool_poolId FROM Pool_Experiment WHERE p.experiment_experimentId=?)";

  public static final String EMPCR_DILUTIONS_BY_RELATED_SOLID_POOL_ID = "SELECT p.dilutions_dilutionId, l.concentration, l.emPCR_pcrId, l.identificationBarcode, l.name, l.alias, l.creationDate, l.securityProfile_profileId "
      + "FROM emPCRDilution l, Pool_emPCRDilution p " + "WHERE l.dilutionId=p.dilutions_dilutionId " + "AND p.pool_poolId=?";

  // EMPCR
  public static final String EMPCR_POOL_SELECT = POOL_SELECT
      + " WHERE p.platformType='Solid' OR p.platformType='LS454' AND p.name LIKE 'EFO%'";

  public static final String EMPCR_POOL_SELECT_BY_POOL_ID = EMPCR_POOL_SELECT + " AND poolId = ?";

  public static final String POOL_SELECT_BY_SEARCH = POOL_SELECT + " WHERE UPPER(p.name) LIKE ? OR UPPER(p.alias) LIKE ? OR "
      + "UPPER(p.description) LIKE ?";

  public static final String POOL_SELECT_LIMIT = POOL_SELECT + " ORDER BY p.poolId DESC LIMIT ?";

  protected static final Logger log = LoggerFactory.getLogger(SQLPoolDAO.class);

  private JdbcTemplate template;
  private ExperimentStore experimentDAO;
  private PoolQcStore poolQcDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private WatcherStore watcherDAO;
  private CascadeType cascadeType;
  private boolean autoGenerateIdentificationBarcodes;
  private ChangeLogStore changeLogDAO;
  private SecurityStore securityDAO;
  private NoteStore noteDAO;
  private BoxStore boxDAO;

  @CoverageIgnore
  public ChangeLogStore getChangeLogDAO() {
    return changeLogDAO;
  }

  @CoverageIgnore
  public void setChangeLogDAO(ChangeLogStore changeLogDAO) {
    this.changeLogDAO = changeLogDAO;
  }

  @Autowired
  private PoolAlertManager poolAlertManager;

  @CoverageIgnore
  public void setPoolAlertManager(PoolAlertManager poolAlertManager) {
    this.poolAlertManager = poolAlertManager;
  }

  @Autowired
  private DaoLookup daoLookup;

  @CoverageIgnore
  public void setDaoLookup(DaoLookup daoLookup) {
    this.daoLookup = daoLookup;
  }

  @Autowired
  private MisoNamingScheme<Pool<? extends Poolable<?, ?>>> namingScheme;

  @Override
  @CoverageIgnore
  public MisoNamingScheme<Pool<? extends Poolable<?, ?>>> getNamingScheme() {
    return namingScheme;
  }

  @Override
  @CoverageIgnore
  public void setNamingScheme(MisoNamingScheme<Pool<? extends Poolable<?, ?>>> namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @CoverageIgnore
  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
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
  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  @CoverageIgnore
  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  @CoverageIgnore
  public SecurityStore getSecurityDAO() {
    return securityDAO;
  }

  @CoverageIgnore
  public void setSecurityDAO(SecurityStore securityDAO) {
    this.securityDAO = securityDAO;
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
  public void setExperimentDAO(ExperimentStore experimentDAO) {
    this.experimentDAO = experimentDAO;
  }

  @CoverageIgnore
  public void setPoolQcDAO(PoolQcStore poolQcDAO) {
    this.poolQcDAO = poolQcDAO;
  }

  @CoverageIgnore
  public void setWatcherDAO(WatcherStore watcherDAO) {
    this.watcherDAO = watcherDAO;
  }

  @CoverageIgnore
  public BoxStore getBoxDAO() {
    return boxDAO;
  }

  @CoverageIgnore
  public void setBoxDAO(BoxStore boxDAO) {
    this.boxDAO = boxDAO;
  }

  @CoverageIgnore
  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  @CoverageIgnore
  public void setAutoGenerateIdentificationBarcodes(boolean autoGenerateIdentificationBarcodes) {
    this.autoGenerateIdentificationBarcodes = autoGenerateIdentificationBarcodes;
  }

  @CoverageIgnore
  public void setNoteDAO(NoteStore noteDAO) {
    this.noteDAO = noteDAO;
  }

  @CoverageIgnore
  public boolean getAutoGenerateIdentificationBarcodes() {
    return autoGenerateIdentificationBarcodes;
  }

  /**
   * Generates a unique barcode. Note that the barcode will change if the Platform is changed.
   *
   * @param pool
   */
  public void autoGenerateIdBarcode(Pool pool) {
    String barcode = pool.getName() + "::" + pool.getPlatformType().getKey();
    pool.setIdentificationBarcode(barcode);
  }

  private void purgeListCache(Pool p, boolean replace) {
    if (cacheManager != null) {
      Cache cache = cacheManager.getCache("poolListCache");
      DbUtils.updateListCache(cache, replace, p, Pool.class);
    }
  }

  private void purgeListCache(Pool p) {
    purgeListCache(p, true);
  }

  @Override
  public Pool getPoolByExperiment(Experiment e) {
    if (e.getPlatform() != null) {
      if (e.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
        List<Pool<? extends Poolable<?, ?>>> eResults = template.query(ILLUMINA_POOL_SELECT_BY_EXPERIMENT_ID, new Object[] { e.getId() },
            new PoolMapper());
        return eResults.size() > 0 ? eResults.get(0) : null;
      } else if (e.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
        List<Pool<? extends Poolable<?, ?>>> eResults = template.query(LS454_POOL_SELECT_BY_EXPERIMENT_ID, new Object[] { e.getId() },
            new PoolMapper());
        return eResults.size() > 0 ? eResults.get(0) : null;
      } else if (e.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
        List<Pool<? extends Poolable<?, ?>>> eResults = template.query(SOLID_POOL_SELECT_BY_EXPERIMENT_ID, new Object[] { e.getId() },
            new PoolMapper());
        return eResults.size() > 0 ? eResults.get(0) : null;
      }
    }
    return null;
  }

  @Override
  @TriggersRemove(cacheName = { "poolCache",
      "lazyPoolCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public long save(Pool<? extends Poolable<?, ?>> pool) throws IOException {
    Long securityProfileId = pool.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) {
      securityProfileId = securityProfileDAO.save(pool.getSecurityProfile());
    }
    if (pool.isDiscarded()) {
      boxDAO.removeBoxableFromBox(pool);
      pool.setVolume(0D);
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("concentration", pool.getConcentration());
    params.addValue("alias", pool.getAlias());
    params.addValue("description", pool.getDescription());
    params.addValue("creationDate", pool.getCreationDate());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("platformType", pool.getPlatformType().getKey());
    params.addValue("ready", pool.getReadyToRun());
    params.addValue("discarded", pool.isDiscarded());
    params.addValue("volume", pool.getVolume());
    params.addValue("lastModifier", pool.getLastModifier().getUserId());
    params.addValue("qcPassed", pool.getQcPassed());

    if (pool.getId() == AbstractPool.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("poolId");
      try {
        pool.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", pool);
        pool.setName(name);

        if (namingScheme.validateField("name", pool.getName())) {
          if (autoGenerateIdentificationBarcodes) {
            autoGenerateIdBarcode(pool);
          } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user

          params.addValue("name", name);
          params.addValue("identificationBarcode", pool.getIdentificationBarcode());

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != pool.getId()) {
            log.error("Expected Pool ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(POOL_DELETE, new MapSqlParameterSource().addValue("poolId", newId.longValue()));
            throw new IOException("Something bad happened. Expected Pool ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save Pool - invalid field:" + pool.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Pool - issue with naming scheme", e);
      }
    } else {
      try {
        if (namingScheme.validateField("name", pool.getName())) {
          params.addValue("poolId", pool.getId()).addValue("name", pool.getName());

          if (autoGenerateIdentificationBarcodes) {
            autoGenerateIdBarcode(pool);
          } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user
          params.addValue("identificationBarcode", pool.getIdentificationBarcode());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(POOL_UPDATE, params);
        } else {
          throw new IOException("Cannot save Pool - invalid field:" + pool.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Pool - issue with naming scheme", e);
      }
    }

    Set<String> oldIds = new HashSet<>(
        template.query(POOL_ELEMENT_SELECT_BY_POOL_ID, new Object[] { pool.getId() }, new RowMapper<String>() {
          @Override
          public String mapRow(ResultSet rs, int pos) throws SQLException {
            String[] parts = rs.getString("elementType").split("\\.");
            return parts[parts.length - 1] + ":" + rs.getLong("elementId");
          }
        }));
    Set<String> newIds = new HashSet<>();
    MapSqlParameterSource delparams = new MapSqlParameterSource();
    delparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    namedTemplate.update(POOL_ELEMENT_DELETE_BY_POOL_ID, delparams);

    if (pool.getPoolableElements() != null && !pool.getPoolableElements().isEmpty()) {
      String type = pool.getPoolableElements().iterator().next().getClass().getSimpleName();

      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template).withTableName("Pool_Elements");
      String lc = type.substring(0, 1).toLowerCase() + type.substring(1);

      Cache dc = null;
      Cache ldc = null;
      if (cacheManager != null) {
        dc = cacheManager.getCache(lc + "Cache");
        ldc = cacheManager.getCache("lazy" + type + "Cache");
      }

      Set<String> previouslySeenElementIds = new HashSet<>();
      for (Poolable<?, ?> d : pool.getPoolableElements()) {
        String uid = d.getClass().getName() + ":" + d.getId();
        if (previouslySeenElementIds.contains(uid)) {
          continue;
        }
        previouslySeenElementIds.add(uid);
        newIds.add(d.getClass().getSimpleName() + ":" + d.getId());
        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("elementId", d.getId());
        esParams.addValue("pool_poolId", pool.getId());
        esParams.addValue("elementType", d.getClass().getName());

        eInsert.execute(esParams);

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            Store<? super Poolable<?, ?>> dao = daoLookup.lookup(d.getClass());
            if (dao != null) {
              dao.save(d);
            }
          } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            if (d instanceof Plate && cacheManager != null) {
              dc = cacheManager.getCache("plateCache");
              ldc = cacheManager.getCache("lazyPlateCache");
            }
            if (dc != null) DbUtils.updateCaches(cacheManager, d, Poolable.class);
            if (ldc != null) DbUtils.updateCaches(cacheManager, d, Poolable.class);
          }
        }
      }
    }

    Set<String> commonIds = new HashSet<>(oldIds);
    commonIds.retainAll(newIds);
    newIds.removeAll(commonIds);
    oldIds.removeAll(commonIds);
    if (!newIds.isEmpty() || !oldIds.isEmpty()) {
      String message = pool.getLastModifier().getLoginName() + (oldIds.isEmpty() ? "" : (" Removed: " + buildElementString(oldIds)))
          + (newIds.isEmpty() ? "" : (" Added: " + buildElementString(newIds)));
      template.update(POOL_CHANGE_LOG_INSERT, pool.getId(), pool.getLastModifier().getUserId(), message);
    }

    MapSqlParameterSource poolparams = new MapSqlParameterSource();
    poolparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate poolNamedTemplate = new NamedParameterJdbcTemplate(template);
    poolNamedTemplate.update(POOL_EXPERIMENT_DELETE_BY_POOL_ID, poolparams);

    if (pool.getExperiments() != null && !pool.getExperiments().isEmpty()) {
      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template).withTableName("Pool_Experiment");

      for (Experiment e : pool.getExperiments()) {
        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("experiments_experimentId", e.getId());
        esParams.addValue("pool_poolId", pool.getId());

        eInsert.execute(esParams);

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            experimentDAO.save(e);
          } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            DbUtils.updateCaches(cacheManager, e, Experiment.class);
          }
        }
      }
    }

    watcherDAO.removeWatchedEntityByUser(pool, pool.getLastModifier());

    for (User u : pool.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(pool, u);
    }

    if (!pool.getNotes().isEmpty()) {
      for (Note n : pool.getNotes()) {
        noteDAO.savePoolNote(pool, n);
      }
    }

    purgeListCache(pool);

    return pool.getId();
  }

  private String buildElementString(Set<String> ids) {
    StringBuilder names = new StringBuilder();
    for (String id : ids) {
      if (names.length() > 0) {
        names.append(", ");
      }
      String[] parts = id.split(":");
      String idColumn;
      if (parts[0].equals("LibraryDilution")) {
        idColumn = "dilutionId";
      } else if (parts[0].equals("Plate")) {
        idColumn = "plateId";
      } else {
        throw new NotImplementedException("Don't know how to pool: " + parts[0]);
      }
      names.append(template.query("SELECT name FROM " + parts[0] + " WHERE " + idColumn + " = ?",
          new Object[] { Integer.parseInt(parts[1]) }, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
              return rs.next() ? rs.getString("name") : null;
            }

          }));
    }
    return names.toString();
  }

  @Override
  public Pool<? extends Poolable<?, ?>> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException {
    if (barcode == null) throw new NullPointerException("cannot look up null barcode");
    if (platformType == null) {
      return getByBarcode(barcode);
    }
    List<Pool<? extends Poolable<?, ?>>> pools = listAllByPlatformAndSearch(platformType, barcode);
    return pools.size() == 1 ? pools.get(0) : null;
  }

  @Override
  public Pool<? extends Poolable<?, ?>> getByBarcode(String barcode) {
    if (barcode == null) throw new NullPointerException("cannot look up null barcode");
    List<Pool<? extends Poolable<?, ?>>> eResults = template.query(POOL_SELECT_BY_IDENTIFICATION_BARCODE, new Object[] { barcode },
        new PoolMapper(true));
    Pool<? extends Poolable<?, ?>> e = eResults.size() > 0 ? (Pool<? extends Poolable<?, ?>>) eResults.get(0) : null;
    return e;
  }

  @Override
  public List<Pool<? extends Poolable<?, ?>>> getByBarcodeList(List<String> barcodeList) {
    return DbUtils.getByBarcodeList(template, barcodeList, POOL_SELECT_FROM_BARCODE_LIST, new PoolMapper(true));
  }

  @Override
  public Boxable getByPositionId(long positionId) {
    List<Pool<? extends Poolable<?, ?>>> eResults = template.query(POOL_SELECT_BY_BOX_POSITION_ID, new Object[] { positionId },
        new PoolMapper());
    Pool<? extends Poolable<?, ?>> e = eResults.size() > 0 ? eResults.get(0) : null;
    return e;
  }

  private Collection<Pool<? extends Poolable<?, ?>>> listByRelated(String query, long relatedId) throws IOException {
    List<Long> poolIds = template.queryForList(query, Long.class, relatedId);
    return DbUtils.getByGenericList(template, poolIds, Types.BIGINT, POOL_SELECT_FROM_ID_LIST, new PoolMapper(true));
  }

  @Override
  public Collection<Pool<? extends Poolable<?, ?>>> listBySampleId(long sampleId) throws IOException {
    return listByRelated(POOL_ID_SELECT_BY_RELATED_SAMPLE, sampleId);
  }

  @Override
  public Collection<Pool<? extends Poolable<?, ?>>> listByLibraryId(long libraryId) throws IOException {
    return listByRelated(POOL_ID_SELECT_BY_RELATED_LIBRARY, libraryId);
  }

  @Override
  public Collection<Pool<? extends Poolable<?, ?>>> listByProjectId(long projectId) throws IOException {
    List<Pool<? extends Poolable<?, ?>>> lpools = template.query(DILUTION_POOL_SELECT_BY_RELATED_PROJECT, new Object[] { projectId },
        new PoolMapper(true));
    List<Pool<? extends Poolable<?, ?>>> epools = template.query(EMPCR_POOL_SELECT_BY_RELATED_PROJECT, new Object[] { projectId },
        new PoolMapper(true));
    List<Pool<? extends Poolable<?, ?>>> ppools = template.query(PLATE_POOL_SELECT_BY_RELATED_PROJECT, new Object[] { projectId },
        new PoolMapper(true));
    lpools.addAll(epools);
    lpools.addAll(ppools);
    return lpools;
  }

  @Override
  @Cacheable(cacheName = "poolCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public Pool<? extends Poolable<?, ?>> get(long poolId) throws IOException {
    List<Pool<? extends Poolable<?, ?>>> eResults = template.query(POOL_SELECT_BY_POOL_ID, new Object[] { poolId }, new PoolMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Pool<? extends Poolable<?, ?>> lazyGet(long poolId) throws IOException {
    List<Pool<? extends Poolable<?, ?>>> eResults = template.query(POOL_SELECT_BY_POOL_ID, new Object[] { poolId }, new PoolMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  @Cacheable(cacheName = "poolListCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public Collection<Pool<? extends Poolable<?, ?>>> listAll() throws IOException {
    return template.query(POOL_SELECT, new PoolMapper());
  }

  @Override
  public List<Pool<? extends Poolable<?, ?>>> listAllByPlatform(PlatformType platformType) throws IOException {
    return template.query(POOL_SELECT_BY_PLATFORM, new Object[] { platformType.getKey() }, new PoolMapper());
  }

  @Override
  public List<Pool<? extends Poolable<?, ?>>> listAllByPlatformAndSearch(PlatformType platformType, String search) throws IOException {
    String query = DbUtils.convertStringToSearchQuery(search);
    return template.query(POOL_SELECT_BY_PLATFORM_AND_SEARCH, new Object[] { platformType.getKey(), query, query, query, query },
        new PoolMapper());
  }

  @Override
  public List<Pool<? extends Poolable<?, ?>>> listReadyByPlatform(PlatformType platformType) throws IOException {
    return template.query(POOL_SELECT_BY_PLATFORM_AND_READY, new Object[] { platformType.getKey() }, new PoolMapper());
  }

  @Override
  public List<Pool<? extends Poolable<?, ?>>> listReadyByPlatformAndSearch(PlatformType platformType, String search) throws IOException {
    String mySQLQuery = DbUtils.convertStringToSearchQuery(search);
    return template.query(POOL_SELECT_BY_PLATFORM_AND_READY_AND_SEARCH,
        new Object[] { platformType.getKey(), mySQLQuery, mySQLQuery, mySQLQuery, mySQLQuery }, new PoolMapper());
  }

  public Collection<? extends Poolable<?, ?>> listPoolableElementsByPoolId(long poolId) throws IOException {
    return template.query(POOL_ELEMENT_SELECT_BY_POOL_ID, new Object[] { poolId }, new PoolableMapper());
  }

  public Collection<? extends Poolable<?, ?>> lazyListPoolableElementsByPoolId(long poolId) throws IOException {
    return template.query(POOL_ELEMENT_SELECT_BY_POOL_ID, new Object[] { poolId }, new PoolableMapper(true));
  }

  @Override
  @TriggersRemove(cacheName = { "poolCache",
      "lazyPoolCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public boolean remove(Pool<? extends Poolable<?, ?>> pool) throws IOException {
    MapSqlParameterSource poolparams = new MapSqlParameterSource();
    poolparams.addValue("pool_poolId", pool.getId());
    poolparams.addValue("poolId", pool.getId());
    NamedParameterJdbcTemplate poolNamedTemplate = new NamedParameterJdbcTemplate(template);

    boolean ok = true;
    if (pool.isDeletable()) {
      changeLogDAO.deleteAllById(TABLE_NAME, pool.getId());
    }
    if (pool.isDeletable() && poolNamedTemplate.update(POOL_DELETE, poolparams) == 1) {
      if (!pool.getDilutions().isEmpty()) {
        Poolable<?, ?> d = pool.getPoolableElements().iterator().next();
        ok = (poolNamedTemplate.update(POOL_ELEMENT_DELETE_BY_POOL_ID, poolparams) == 1);
        String type = d.getClass().getSimpleName();
        String lc = type.substring(0, 1).toLowerCase() + type.substring(1);
        Cache dc = cacheManager.getCache(lc + "Cache");
        Cache ldc = cacheManager.getCache("lazy" + type + "Cache");

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            Store<? super Poolable<?, ?>> dao = daoLookup.lookup(d.getClass());
            if (dao != null) {
              dao.save(d);
            }
          } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            if (d instanceof Plate) {
              dc = cacheManager.getCache("plateCache");
              ldc = cacheManager.getCache("lazyPlateCache");
            }
            if (dc != null) DbUtils.updateCaches(cacheManager, d, Poolable.class);
            if (ldc != null) DbUtils.updateCaches(cacheManager, d, Poolable.class);
          }
        }
      }

      if (!pool.getExperiments().isEmpty()) {
        ok = (poolNamedTemplate.update(POOL_EXPERIMENT_DELETE_BY_POOL_ID, poolparams) == 1);
        Collection<Experiment> exps = pool.getExperiments();
        for (Experiment e : exps) {
          if (this.cascadeType != null) {
            if (this.cascadeType.equals(CascadeType.PERSIST)) {
              experimentDAO.save(e);
            } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
              DbUtils.updateCaches(cacheManager, e, Experiment.class);
            }
          }
        }
      }
      return ok;
    }
    return false;
  }

  public class PoolMapper extends CacheAwareRowMapper<Pool<? extends Poolable<?, ?>>> {
    public PoolMapper() {
      super((Class<Pool<? extends Poolable<?, ?>>>) ((ParameterizedType) new TypeReference<Pool<? extends Poolable<?, ?>>>() {
      }.getType()).getRawType());
    }

    public PoolMapper(boolean lazy) {
      super((Class<Pool<? extends Poolable<?, ?>>>) ((ParameterizedType) new TypeReference<Pool<? extends Poolable<?, ?>>>() {
      }.getType()).getRawType(), lazy);
    }

    @Override
    public Pool<? extends Poolable<?, ?>> mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("poolId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for Pool " + id);
          return (Pool<? extends Poolable<?, ?>>) element.getObjectValue();
        }
      }

      Pool<? extends Poolable<?, ?>> p = null;
      try {
        p = dataObjectFactory.getPool();
        PlatformType pt = PlatformType.get(rs.getString("platformType"));
        p.setPlatformType(pt);

        if (pt != null) {
          Collection<? extends Poolable<?, ?>> poolables = (isLazy() ? lazyListPoolableElementsByPoolId(id)
              : listPoolableElementsByPoolId(id));
          p.setPoolableElements(poolables);
        }

        p.setId(id);
        p.setName(rs.getString("name"));
        p.setAlias(rs.getString("alias"));
        p.setDescription(rs.getString("description"));
        p.setCreationDate(rs.getDate("creationDate"));
        p.setConcentration(rs.getDouble("concentration"));
        p.setIdentificationBarcode(rs.getString("identificationBarcode"));
        p.setReadyToRun(rs.getBoolean("ready"));
        p.setVolume(rs.getDouble("volume"));
        p.setDiscarded(rs.getBoolean("discarded"));
        p.setBoxPositionId(rs.getLong("boxPositionId"));
        p.setBoxAlias(rs.getString("boxAlias"));
        p.setBoxId(rs.getLong("boxId"));
        p.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
        int row = rs.getInt("boxRow");
        if (!rs.wasNull()) p.setBoxPosition(BoxUtils.getPositionString(row, rs.getInt("boxColumn")));
        p.setBoxLocation(rs.getString("boxLocation"));
        p.setLastModified(rs.getDate("lastModified"));
        p.setQcPassed(rs.getBoolean("qcPassed"));
        // rs.wasNull() needs to be directly after rs.getBoolean("qcPassed") as that's the value which gets checked for null
        if (rs.wasNull()) {
          p.setQcPassed(null);
        }

        p.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        p.setWatchers(new HashSet<>(watcherDAO.getWatchersByEntityName(p.getWatchableIdentifier())));
        if (p.getSecurityProfile() != null && p.getSecurityProfile().getOwner() != null) {
          p.addWatcher(p.getSecurityProfile().getOwner());
        }
        for (User u : watcherDAO.getWatchersByWatcherGroup("PoolWatchers")) {
          p.addWatcher(u);
        }

        if (!isLazy()) {
          p.setExperiments(experimentDAO.listByPoolId(id));

          for (PoolQC qc : poolQcDAO.listByPoolId(id)) {
            p.addQc(qc);
          }
          p.setNotes(noteDAO.listByPool(id));
        }
        p.getChangeLog().addAll(changeLogDAO.listAllById(TABLE_NAME, id));
      } catch (IOException e1) {
        log.error("Cannot map from database to Pool: ", e1);
      } catch (MalformedPoolQcException e) {
        log.error("Cannot add PoolQC to pool: ", e);
      }

      if (poolAlertManager != null) {
        poolAlertManager.push(p);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), p));
      }

      return p;
    }
  }

  public class PoolableMapper extends CacheAwareRowMapper<Poolable<?, ?>> {

    public PoolableMapper() {
      super((Class<Poolable<?, ?>>) ((ParameterizedType) new TypeReference<Poolable<?, ?>>() {
      }.getType()).getRawType());
    }

    public PoolableMapper(boolean lazy) {
      super((Class<Poolable<?, ?>>) ((ParameterizedType) new TypeReference<Poolable<?, ?>>() {
      }.getType()).getRawType(), lazy);
    }

    @Override
    public Poolable<?, ?> mapRow(ResultSet rs, int rowNum) throws SQLException {
      Long poolId = rs.getLong("pool_poolId");
      Long elementId = rs.getLong("elementId");
      String type = rs.getString("elementType");

      try {
        @SuppressWarnings("unchecked")
        Class<? extends Poolable<?, ?>> clz = (Class<? extends Poolable<?, ?>>) Class.forName(type).asSubclass(Poolable.class);
        Store<? extends Poolable<?, ?>> dao = daoLookup.lookup(clz);
        if (dao != null) {
          log.debug("Mapping poolable -> " + poolId + " : " + type + " : " + elementId);
          Poolable<?, ?> p = (isLazy() ? dao.lazyGet(elementId) : dao.get(elementId));

          if (p != null) {
            log.debug("\\_ got " + p.getId() + " : " + p.getName());
          } else {
            log.debug("\\_ got null");
          }
          return p;
        } else {
          throw new SQLException("No DAO found or more than one found.");
        }
      } catch (ClassNotFoundException e) {
        throw new SQLException("Cannot resolve element type to a valid class", e);
      } catch (IOException e) {
        throw new SQLException("Cannot retrieve poolable element: [" + type + " ] " + elementId);
      }
    }
  }

  @Override
  public Map<String, Integer> getPoolColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }

  @Deprecated
  @Override
  @CoverageIgnore
  public List<Pool<? extends Poolable<?, ?>>> listBySearch(String query) {
    List<Pool<? extends Poolable<?, ?>>> rtn;
    if (isStringEmptyOrNull(query)) {
      rtn = new ArrayList<>();
    } else {
      String mySQLQuery = DbUtils.convertStringToSearchQuery(query);
      rtn = template.query(POOL_SELECT_BY_SEARCH, new Object[] { mySQLQuery, mySQLQuery, mySQLQuery }, new PoolMapper(true));
    }
    return rtn;
  }

  @Override
  public List<Pool<? extends Poolable<?, ?>>> listAllPoolsWithLimit(int limit) throws IOException {
    return template.query(POOL_SELECT_LIMIT, new Object[] { limit }, new PoolMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt(POOL_COUNT);
  }

  @Override
  public long countPoolsByPlatform(PlatformType platform) throws IOException {
    return template.queryForLong(POOL_COUNT_BY_PLATFORM, new Object[] { platform.getKey() });
  }

  @Override
  public long countPoolsBySearch(PlatformType platform, String querystr) throws IOException {
    if (isStringEmptyOrNull(querystr)) {
      return (PlatformType.ILLUMINA.equals(platform) ? countPoolsByPlatform(platform) : count());
    } else {
      String mySQLQuery = DbUtils.convertStringToSearchQuery(querystr);
      return template.queryForLong(POOL_COUNT_BY_PLATFORM_AND_SEARCH,
          new Object[] { platform.getKey(), mySQLQuery, mySQLQuery, mySQLQuery, mySQLQuery });
    }
  }

  public String updateSortCol(String sortCol) {
    sortCol = sortCol.replaceAll("[^\\w]", "");
    if ("id".equals(sortCol)) sortCol = "poolId";
    if ("lastModified".equals(sortCol)) {
      sortCol = "pmod.lastModified";
    } else {
      sortCol = "p." + sortCol;
    }
    return sortCol;
  }

  @Override
  public List<Pool<? extends Poolable<?, ?>>> listBySearchOffsetAndNumResultsAndPlatform(int offset, int resultsPerPage, String search,
      String sortDir, String sortCol, PlatformType platform) throws IOException {
    if (isStringEmptyOrNull(search)) {
      return listByOffsetAndNumResults(offset, resultsPerPage, sortDir, sortCol, platform);
    } else {
      sortCol = updateSortCol(sortCol);
      if (offset < 0 || resultsPerPage < 0) throw new IOException("Limit and Offset must be greater than zero");
      if (!"asc".equals(sortDir.toLowerCase()) && !"desc".equals(sortDir.toLowerCase())) sortDir = "desc";
      String querystr = DbUtils.convertStringToSearchQuery(search);
      String query = POOL_SELECT_BY_PLATFORM_AND_SEARCH + " ORDER BY " + sortCol + " " + sortDir + " LIMIT " + resultsPerPage + " OFFSET "
          + offset;
      List<Pool<? extends Poolable<?, ?>>> rtn = template.query(query,
          new Object[] { platform.getKey(), querystr, querystr, querystr, querystr }, new PoolMapper(true));
      return rtn;
    }
  }

  @Override
  public List<Pool<? extends Poolable<?, ?>>> listByOffsetAndNumResults(int offset, int limit, String sortDir, String sortCol,
      PlatformType platform) throws IOException {
    sortCol = updateSortCol(sortCol);
    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must be greater than zero");
    if (!"asc".equals(sortDir.toLowerCase()) && !"desc".equals(sortDir.toLowerCase())) sortDir = "DESC";
    String query = POOL_SELECT_BY_PLATFORM + " ORDER BY " + sortCol + " " + sortDir + " LIMIT " + limit + " OFFSET " + offset;
    return template.query(query, new Object[] { platform.getKey() }, new PoolMapper(true));
  }

}
