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

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolQcException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.*;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DaoLookup;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;

import javax.persistence.CascadeType;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLPoolDAO implements PoolStore {
  private static final String TABLE_NAME = "Pool";

  private static final String POOL_SELECT =
      "SELECT poolId, concentration, identificationBarcode, name, alias, creationDate, securityProfile_profileId, platformType, ready, qcPassed " +
      "FROM " + TABLE_NAME;

  public static final String POOL_SELECT_BY_POOL_ID =
      POOL_SELECT + " WHERE poolId=?";

  public static final String POOL_SELECT_BY_PLATFORM =
      POOL_SELECT + " WHERE platformType=?";

  public static final String POOL_SELECT_BY_PLATFORM_AND_SEARCH =
      POOL_SELECT + " WHERE platformType=? AND " +
      "(name LIKE ? OR " +
      "alias LIKE ? OR " +
      "identificationBarcode LIKE ?) ";

  public static final String POOL_SELECT_BY_PLATFORM_AND_READY =
      POOL_SELECT_BY_PLATFORM + " AND ready=1";

  public static final String POOL_SELECT_BY_PLATFORM_AND_READY_AND_SEARCH =
      POOL_SELECT_BY_PLATFORM_AND_SEARCH + " AND ready=1";

  public static final String POOL_UPDATE =
      "UPDATE " + TABLE_NAME + " " +
      "SET alias=:alias, concentration=:concentration, identificationBarcode=:identificationBarcode, creationDate=:creationDate, securityProfile_profileId=:securityProfile_profileId, platformType=:platformType, ready=:ready, qcPassed=:qcPassed " +
      "WHERE poolId=:poolId";

  public static final String POOL_DELETE =
      "DELETE FROM " + TABLE_NAME + " WHERE poolId=:poolId";

  public static final String POOL_ELEMENT_SELECT_BY_POOL_ID =
      "SELECT pool_poolId, elementType, elementId FROM Pool_Elements WHERE pool_poolId = ?";

  public static final String POOL_EXPERIMENT_DELETE_BY_POOL_ID =
      "DELETE FROM Pool_Experiment " +
      "WHERE pool_poolId=:pool_poolId";

  /*
  public static final String POOL_SELECT_BY_RELATED_PROJECT =
    "SELECT DISTINCT pool.* " +
    "FROM Project p " +
    "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " +
    "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId " +
    "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " +

    "LEFT JOIN emPCR e ON e.dilution_dilutionId = ld.dilutionId " +
    "LEFT JOIN emPCRDilution ed ON ed.emPCR_pcrId = e.pcrId " +

    "LEFT JOIN Pool_Elements pld ON pld.elementId = ld.dilutionId " +
    "LEFT JOIN Pool_Elements ple ON ple.elementId = ed.dilutionId " +

    "INNER JOIN " + TABLE_NAME + " pool ON pool.poolId = pld.pool_poolId " +
    "OR pool.poolId = ple.pool_poolId " +
    "WHERE p.projectId=?";
  */

  public static final String EMPCR_POOL_SELECT_BY_RELATED_PROJECT =
      "SELECT DISTINCT pool.* " +
      "FROM Project p " +
      "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " +
      "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId " +
      "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " +

      "LEFT JOIN emPCR e ON e.dilution_dilutionId = ld.dilutionId " +
      "LEFT JOIN emPCRDilution ed ON ed.emPCR_pcrId = e.pcrId " +

      "LEFT JOIN Pool_Elements ple ON ple.elementId = ed.dilutionId " +

      "INNER JOIN " + TABLE_NAME + " pool ON pool.poolId = ple.pool_poolId " +
      "WHERE p.projectId = ? AND ple.elementType = 'uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution'";

  public static final String DILUTION_POOL_SELECT_BY_RELATED_PROJECT =
      "SELECT DISTINCT pool.* " +
      "FROM Project p " +
      "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " +
      "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId " +
      "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " +

      "LEFT JOIN Pool_Elements pld ON pld.elementId = ld.dilutionId " +

      "INNER JOIN " + TABLE_NAME + " pool ON pool.poolId = pld.pool_poolId " +
      "WHERE p.projectId = ? AND pld.elementType = 'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution'";

  public static final String PLATE_POOL_SELECT_BY_RELATED_PROJECT =
      "SELECT DISTINCT pool.* " +
      "FROM Project p " +
      "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " +
      "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId " +
      "INNER JOIN Plate_Elements pe ON li.libraryId = pe.elementId " +
      "INNER JOIN Plate pl ON pl.plateId = pe.plate_plateId " +

      "LEFT JOIN Pool_Elements pld ON pld.elementId = pl.plateId " +

      "INNER JOIN " + TABLE_NAME + " pool ON pool.poolId = pld.pool_poolId " +
      "WHERE p.projectId= ? AND pld.elementType LIKE '%Plate'";

  public static final String POOL_SELECT_BY_RELATED_LIBRARY =
      "SELECT DISTINCT pool.* " +
      "FROM Library li " +
      "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " +

      "LEFT JOIN emPCR e ON e.dilution_dilutionId = ld.dilutionId " +
      "LEFT JOIN emPCRDilution ed ON ed.emPCR_pcrId = e.pcrId " +

      "LEFT JOIN Pool_Elements pld ON pld.elementId = ld.dilutionId " +
      "LEFT JOIN Pool_Elements ple ON ple.elementId = ed.dilutionId " +

      "INNER JOIN " + TABLE_NAME + " pool ON pool.poolId = pld.pool_poolId " +
      "OR pool.poolId = ple.pool_poolId " +
      "WHERE li.libraryId=?";

  public static final String POOL_SELECT_BY_RELATED_SAMPLE =
      "SELECT DISTINCT pool.* " +
      "FROM Sample s " +
      "INNER JOIN Library li ON li.sample_sampleId = s.sampleId " +
      "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " +

      "LEFT JOIN emPCR e ON e.dilution_dilutionId = ld.dilutionId " +
      "LEFT JOIN emPCRDilution ed ON ed.emPCR_pcrId = e.pcrId " +

      "LEFT JOIN Pool_Elements pld ON pld.elementId = ld.dilutionId " +
      "LEFT JOIN Pool_Elements ple ON ple.elementId = ed.dilutionId " +

      "INNER JOIN " + TABLE_NAME + " pool ON pool.poolId = pld.pool_poolId " +
      "OR pool.poolId = ple.pool_poolId " +
      "WHERE s.sampleId=?";

  public static final String POOL_ELEMENT_DELETE_BY_POOL_ID =
      "DELETE FROM Pool_Elements " +
      "WHERE pool_poolId=:pool_poolId";

  //ILLUMINA
  public static final String ILLUMINA_POOL_SELECT =
      POOL_SELECT + " WHERE platformType='Illumina'";

  public static final String ILLUMINA_POOL_SELECT_BY_POOL_ID =
      ILLUMINA_POOL_SELECT + " AND poolId=?";

  public static final String ILLUMINA_POOL_SELECT_BY_READY =
      ILLUMINA_POOL_SELECT + " AND ready=1";

  public static final String ILLUMINA_POOL_SELECT_BY_ID_BARCODE =
      ILLUMINA_POOL_SELECT + " AND identificationBarcode=?";

  public static final String ILLUMINA_POOL_SELECT_BY_EXPERIMENT_ID =
      "SELECT ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready, ip.qcPassed " +
      "FROM " + TABLE_NAME + " ip, Pool_Experiment pe " +
      "WHERE ip.poolId=pe.pool_poolId " +
      "AND ip.platformType='Illumina' " +
      "AND pe.experiments_experimentId=?";

  //454
  public static final String LS454_POOL_SELECT =
      POOL_SELECT + " WHERE platformType='LS454'";

  public static final String LS454_POOL_SELECT_BY_POOL_ID =
      LS454_POOL_SELECT + " AND poolId=?";

  public static final String LS454_POOL_SELECT_BY_READY =
      LS454_POOL_SELECT + " AND ready=1";

  public static final String LS454_POOL_SELECT_BY_ID_BARCODE =
      LS454_POOL_SELECT + " AND identificationBarcode=?";

  public static final String LS454_POOL_SELECT_BY_EXPERIMENT_ID =
      "SELECT ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready, ip.qcPassed " +
      "FROM " + TABLE_NAME + " ip, Pool_Experiment pe " +
      "WHERE ip.poolId=pe.pool_poolId " +
      "AND ip.platformType='LS454' " +
      "AND pe.experiments_experimentId=?";

  public static final String EMPCR_DILUTIONS_BY_RELATED_LS454_POOL_ID =
      "SELECT p.dilutions_dilutionId, l.concentration, l.emPCR_pcrId, l.identificationBarcode, l.name, l.alias, l.creationDate, l.securityProfile_profileId " +
      "FROM emPCRDilution l, Pool_emPCRDilution p " +
      "WHERE l.dilutionId=p.dilutions_dilutionId " +
      "AND p.pool_poolId=?";

  //SOLiD
  public static final String SOLID_POOL_SELECT =
      POOL_SELECT + " WHERE platformType='Solid'";

  public static final String SOLID_POOL_SELECT_BY_POOL_ID =
      SOLID_POOL_SELECT + " AND poolId=?";

  public static final String SOLID_POOL_SELECT_BY_READY =
      SOLID_POOL_SELECT + " AND ready=1";

  public static final String SOLID_POOL_SELECT_BY_ID_BARCODE =
      SOLID_POOL_SELECT + " AND identificationBarcode=?";

  public static final String SOLID_POOL_SELECT_BY_EXPERIMENT_ID =
      "SELECT ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready, ip.qcPassed " +
      "FROM " + TABLE_NAME + " ip, Pool_Experiment pe " +
      "WHERE ip.poolId=pe.pool_poolId " +
      "AND ip.platformType='Solid' " +
      "AND pe.experiments_experimentId=?";

  public static final String EMPCR_DILUTIONS_BY_RELATED_SOLID_POOL_ID =
      "SELECT p.dilutions_dilutionId, l.concentration, l.emPCR_pcrId, l.identificationBarcode, l.name, l.alias, l.creationDate, l.securityProfile_profileId " +
      "FROM emPCRDilution l, Pool_emPCRDilution p " +
      "WHERE l.dilutionId=p.dilutions_dilutionId " +
      "AND p.pool_poolId=?";

  //EMPCR
  public static final String EMPCR_POOL_SELECT =
      POOL_SELECT + " WHERE platformType='Solid' OR platformType='LS454' AND name LIKE 'EFO%'";

  public static final String EMPCR_POOL_SELECT_BY_POOL_ID =
      EMPCR_POOL_SELECT + " AND poolId = ?";

  protected static final Logger log = LoggerFactory.getLogger(SQLPoolDAO.class);

  private JdbcTemplate template;
  private ExperimentStore experimentDAO;
  private PoolQcStore poolQcDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private WatcherStore watcherDAO;
  private CascadeType cascadeType;
  private boolean autoGenerateIdentificationBarcodes;

  @Autowired
  private PoolAlertManager poolAlertManager;

  public void setPoolAlertManager(PoolAlertManager poolAlertManager) {
    this.poolAlertManager = poolAlertManager;
  }

  @Autowired
  private DaoLookup daoLookup;

  public void setDaoLookup(DaoLookup daoLookup) {
    this.daoLookup = daoLookup;
  }

  @Autowired
  private MisoNamingScheme<Pool<? extends Poolable>> namingScheme;

  @Override
  public MisoNamingScheme<Pool<? extends Poolable>> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Pool<? extends Poolable>> namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private CacheManager cacheManager;

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
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

  public void setExperimentDAO(ExperimentStore experimentDAO) {
    this.experimentDAO = experimentDAO;
  }

  public void setPoolQcDAO(PoolQcStore poolQcDAO) {
    this.poolQcDAO = poolQcDAO;
  }

  public void setWatcherDAO(WatcherStore watcherDAO) {
    this.watcherDAO = watcherDAO;
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

  private void purgeListCache(Pool p, boolean replace) {
    Cache cache = cacheManager.getCache("poolListCache");
    DbUtils.updateListCache(cache, replace, p, Pool.class);
  }

  private void purgeListCache(Pool p) {
    purgeListCache(p, true);
  }

  public Pool getPoolByExperiment(Experiment e) {
    if (e.getPlatform() != null) {
      if (e.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
        List<Pool<? extends Poolable>> eResults = template.query(ILLUMINA_POOL_SELECT_BY_EXPERIMENT_ID, new Object[]{e.getId()}, new PoolMapper());
        return eResults.size() > 0 ? eResults.get(0) : null;
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
        List<Pool<? extends Poolable>> eResults = template.query(LS454_POOL_SELECT_BY_EXPERIMENT_ID, new Object[]{e.getId()}, new PoolMapper());
        return eResults.size() > 0 ? eResults.get(0) : null;
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
        List<Pool<? extends Poolable>> eResults = template.query(SOLID_POOL_SELECT_BY_EXPERIMENT_ID, new Object[]{e.getId()}, new PoolMapper());
        return eResults.size() > 0 ? eResults.get(0) : null;
      }
    }
    return null;
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  @TriggersRemove(cacheName = {"poolCache", "lazyPoolCache"},
                  keyGenerator = @KeyGenerator(
                      name = "HashCodeCacheKeyGenerator",
                      properties = {
                          @Property(name = "includeMethod", value = "false"),
                          @Property(name = "includeParameterTypes", value = "false")
                      })
  )
  public long save(Pool<? extends Poolable> pool) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

    Long securityProfileId = pool.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) { // && this.cascadeType.equals(CascadeType.PERSIST))) {
      securityProfileId = securityProfileDAO.save(pool.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("concentration", pool.getConcentration())
        .addValue("alias", pool.getAlias())
        .addValue("creationDate", pool.getCreationDate())
        .addValue("securityProfile_profileId", securityProfileId)
        .addValue("platformType", pool.getPlatformType().getKey())
        .addValue("ready", pool.getReadyToRun());

    if (pool.getQcPassed() != null) {
      params.addValue("qcPassed", pool.getQcPassed().toString());
    }
    else {
      params.addValue("qcPassed", pool.getQcPassed());
    }

    if (pool.getId() == AbstractPool.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
          .withTableName(TABLE_NAME)
          .usingGeneratedKeyColumns("poolId");
      try {
        pool.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", pool);
        pool.setName(name);

        if (namingScheme.validateField("name", pool.getName())) {
          if (autoGenerateIdentificationBarcodes) {
            String barcode = name + "::" + pool.getPlatformType().getKey();
            pool.setIdentificationBarcode(barcode); 
          } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user

          params.addValue("name", name);

          params.addValue("identificationBarcode", pool.getIdentificationBarcode());

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != pool.getId()) {
            log.error("Expected Pool ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(POOL_DELETE, new MapSqlParameterSource().addValue("poolId", newId.longValue()));
            throw new IOException("Something bad happened. Expected Pool ID doesn't match returned value from DB insert");
          }
        }
        else {
          throw new IOException("Cannot save Pool - invalid field:" + pool.toString());
        }
      }
      catch (MisoNamingException e) {
        throw new IOException("Cannot save Pool - issue with naming scheme", e);
      }
    }
    else {
      try {
        if (namingScheme.validateField("name", pool.getName())) {
          params.addValue("poolId", pool.getId())
              .addValue("name", pool.getName());
          
          if (autoGenerateIdentificationBarcodes) {
            String barcode = pool.getName() + "::" + pool.getPlatformType().getKey();
            pool.setIdentificationBarcode(barcode); 
          } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user
          params.addValue("identificationBarcode", pool.getIdentificationBarcode());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(POOL_UPDATE, params);
        }
        else {
          throw new IOException("Cannot save Pool - invalid field:" + pool.toString());
        }
      }
      catch (MisoNamingException e) {
        throw new IOException("Cannot save Pool - issue with naming scheme", e);
      }
    }

    MapSqlParameterSource delparams = new MapSqlParameterSource();
    delparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    namedTemplate.update(POOL_ELEMENT_DELETE_BY_POOL_ID, delparams);

    if (pool.getPoolableElements() != null && !pool.getPoolableElements().isEmpty()) {
      String type = pool.getPoolableElements().iterator().next().getClass().getSimpleName();

      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template).withTableName("Pool_Elements");
      String lc = type.substring(0, 1).toLowerCase() + type.substring(1);

      Cache dc = cacheManager.getCache(lc + "Cache");
      Cache ldc = cacheManager.getCache("lazy" + type + "Cache");

      for (Poolable d : pool.getPoolableElements()) {
        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("elementId", d.getId())
            .addValue("pool_poolId", pool.getId())
            .addValue("elementType", d.getClass().getName());

        eInsert.execute(esParams);

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            Store<? super Poolable> dao = daoLookup.lookup(d.getClass());
            if (dao != null) {
              dao.save(d);
            }
          }
          else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            if (d instanceof Plate) {
              dc = cacheManager.getCache("plateCache");
              ldc = cacheManager.getCache("lazyPlateCache");
            }
            if (dc != null) DbUtils.updateCaches(cacheManager, d, Poolable.class);
            if (ldc != null) DbUtils.updateCaches(cacheManager, d, Poolable.class);
          }
        }
      }
    }

    MapSqlParameterSource poolparams = new MapSqlParameterSource();
    poolparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate poolNamedTemplate = new NamedParameterJdbcTemplate(template);
    poolNamedTemplate.update(POOL_EXPERIMENT_DELETE_BY_POOL_ID, poolparams);

    if (pool.getExperiments() != null && !pool.getExperiments().isEmpty()) {
      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
          .withTableName("Pool_Experiment");

      for (Experiment e : pool.getExperiments()) {
        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("experiments_experimentId", e.getId())
            .addValue("pool_poolId", pool.getId());

        eInsert.execute(esParams);

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            experimentDAO.save(e);
          }
          else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            DbUtils.updateCaches(cacheManager, e, Experiment.class);
          }
        }
      }
    }

    watcherDAO.removeWatchedEntityByUser(pool, user);

    for (User u : pool.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(pool, u);
    }

    purgeListCache(pool);

    return pool.getId();
  }

  @Override
  public Pool<? extends Poolable> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException {
    List<Pool<? extends Poolable>> pools = listAllByPlatformAndSearch(platformType, barcode);
    return pools.size() == 1 ? pools.get(0) : null;
  }

  public Collection<Pool<? extends Poolable>> listBySampleId(long sampleId) throws IOException {
    return template.query(POOL_SELECT_BY_RELATED_SAMPLE, new Object[]{sampleId}, new PoolMapper());
  }

  public Collection<Pool<? extends Poolable>> listByLibraryId(long libraryId) throws IOException {
    return template.query(POOL_SELECT_BY_RELATED_LIBRARY, new Object[]{libraryId}, new PoolMapper());
  }

  public Collection<Pool<? extends Poolable>> listByProjectId(long projectId) throws IOException {
    List<Pool<? extends Poolable>> lpools = template.query(DILUTION_POOL_SELECT_BY_RELATED_PROJECT, new Object[]{projectId}, new PoolMapper());
    List<Pool<? extends Poolable>> epools = template.query(EMPCR_POOL_SELECT_BY_RELATED_PROJECT, new Object[]{projectId}, new PoolMapper());
    List<Pool<? extends Poolable>> ppools = template.query(PLATE_POOL_SELECT_BY_RELATED_PROJECT, new Object[]{projectId}, new PoolMapper());
    lpools.addAll(epools);
    lpools.addAll(ppools);
    return lpools;
  }

  @Cacheable(cacheName = "poolCache",
             keyGenerator = @KeyGenerator(
                 name = "HashCodeCacheKeyGenerator",
                 properties = {
                     @Property(name = "includeMethod", value = "false"),
                     @Property(name = "includeParameterTypes", value = "false")
                 }
             )
  )
  public Pool<? extends Poolable> get(long poolId) throws IOException {
    List<Pool<? extends Poolable>> eResults = template.query(POOL_SELECT_BY_POOL_ID, new Object[]{poolId}, new PoolMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Pool<? extends Poolable> lazyGet(long poolId) throws IOException {
    List<Pool<? extends Poolable>> eResults = template.query(POOL_SELECT_BY_POOL_ID, new Object[]{poolId}, new PoolMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Cacheable(cacheName = "poolListCache",
             keyGenerator = @KeyGenerator(
                 name = "HashCodeCacheKeyGenerator",
                 properties = {
                     @Property(name = "includeMethod", value = "false"),
                     @Property(name = "includeParameterTypes", value = "false")
                 }
             )
  )
  public Collection<Pool<? extends Poolable>> listAll() throws IOException {
    return template.query(POOL_SELECT, new PoolMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  public List<Pool<? extends Poolable>> listAllByPlatform(PlatformType platformType) throws IOException {
    return template.query(POOL_SELECT_BY_PLATFORM, new Object[]{platformType.getKey()}, new PoolMapper());
  }

  public List<Pool<? extends Poolable>> listAllByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    String mySQLQuery = "%" + query + "%";
    return template.query(POOL_SELECT_BY_PLATFORM_AND_SEARCH, new Object[]{platformType.getKey(), mySQLQuery, mySQLQuery, mySQLQuery}, new PoolMapper());
  }

  public List<Pool<? extends Poolable>> listReadyByPlatform(PlatformType platformType) throws IOException {
    return template.query(POOL_SELECT_BY_PLATFORM_AND_READY, new Object[]{platformType.getKey()}, new PoolMapper());
  }

  public List<Pool<? extends Poolable>> listReadyByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    String mySQLQuery = "%" + query + "%";
    return template.query(POOL_SELECT_BY_PLATFORM_AND_READY_AND_SEARCH, new Object[]{platformType.getKey(), mySQLQuery, mySQLQuery, mySQLQuery}, new PoolMapper());
  }

  public Collection<? extends Poolable> listPoolableElementsByPoolId(long poolId) throws IOException {
    return template.query(POOL_ELEMENT_SELECT_BY_POOL_ID, new Object[]{poolId}, new PoolableMapper());
  }

  @TriggersRemove(
      cacheName = {"poolCache", "lazyPoolCache"},
      keyGenerator = @KeyGenerator(
          name = "HashCodeCacheKeyGenerator",
          properties = {
              @Property(name = "includeMethod", value = "false"),
              @Property(name = "includeParameterTypes", value = "false")
          }
      )
  )
  public boolean remove(Pool<? extends Poolable> pool) throws IOException {
    MapSqlParameterSource poolparams = new MapSqlParameterSource();
    poolparams.addValue("pool_poolId", pool.getId());
    poolparams.addValue("poolId", pool.getId());
    NamedParameterJdbcTemplate poolNamedTemplate = new NamedParameterJdbcTemplate(template);

    boolean ok = true;
    if (pool.isDeletable() && poolNamedTemplate.update(POOL_DELETE, poolparams) == 1) {
      if (!pool.getDilutions().isEmpty()) {
        Poolable d = pool.getPoolableElements().iterator().next();
        ok = (poolNamedTemplate.update(POOL_ELEMENT_DELETE_BY_POOL_ID, poolparams) == 1);
        String type = d.getClass().getSimpleName();
        String lc = type.substring(0, 1).toLowerCase() + type.substring(1);
        Cache dc = cacheManager.getCache(lc + "Cache");
        Cache ldc = cacheManager.getCache("lazy" + type + "Cache");

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            Store<? super Poolable> dao = daoLookup.lookup(d.getClass());
            if (dao != null) {
              dao.save(d);
            }
          }
          else if (this.cascadeType.equals(CascadeType.REMOVE)) {
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
            }
            else if (this.cascadeType.equals(CascadeType.REMOVE)) {
              DbUtils.updateCaches(cacheManager, e, Experiment.class);
            }
          }
        }
      }
      return ok;
    }
    return false;
  }

  public class PoolMapper extends CacheAwareRowMapper<Pool<? extends Poolable>> {
    public PoolMapper() {
      super((Class<Pool<? extends Poolable>>) ((ParameterizedType) new TypeReference<Pool<? extends Poolable>>() {
      }.getType()).getRawType());
    }

    public PoolMapper(boolean lazy) {
      super((Class<Pool<? extends Poolable>>) ((ParameterizedType) new TypeReference<Pool<? extends Poolable>>() {
      }.getType()).getRawType(), lazy);
    }

    @Override
    public Pool<? extends Poolable> mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("poolId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for Pool " + id);
          return (Pool<? extends Poolable>) element.getObjectValue();
        }
      }

      Pool<? extends Poolable> p = null;
      try {
        p = dataObjectFactory.getPool();
        PlatformType pt = PlatformType.get(rs.getString("platformType"));
        p.setPlatformType(pt);

        if (pt != null) {
          Collection<? extends Poolable> poolables = listPoolableElementsByPoolId(id);
          p.setPoolableElements(poolables);
        }

        p.setId(id);
        p.setName(rs.getString("name"));
        p.setAlias(rs.getString("alias"));
        p.setCreationDate(rs.getDate("creationDate"));
        p.setConcentration(rs.getDouble("concentration"));
        p.setIdentificationBarcode(rs.getString("identificationBarcode"));
        p.setReadyToRun(rs.getBoolean("ready"));
        if (rs.getString("qcPassed") != null) {
          p.setQcPassed(Boolean.parseBoolean(rs.getString("qcPassed")));
        }
        else {
          p.setQcPassed(null);
        }

        p.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        p.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(p.getWatchableIdentifier())));
        if (p.getSecurityProfile() != null &&
            p.getSecurityProfile().getOwner() != null) {
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
        }
      }
      catch (IOException e1) {
        log.error("Cannot map from database to Pool: ", e1);
        e1.printStackTrace();
      }
      catch (MalformedPoolQcException e) {
        log.error("Cannot add PoolQC to pool: ", e);
        e.printStackTrace();
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

  public class PoolableMapper implements RowMapper<Poolable> {
    public Poolable mapRow(ResultSet rs, int rowNum) throws SQLException {
      Long poolId = rs.getLong("pool_poolId");
      Long elementId = rs.getLong("elementId");
      String type = rs.getString("elementType");

      try {
        Class<? extends Poolable> clz = Class.forName(type).asSubclass(Poolable.class);
        Store<? extends Poolable> dao = daoLookup.lookup(clz);
        if (dao != null) {
          log.debug("Mapping poolable -> " + poolId + " : " + type + " : " + elementId);
          Poolable p = dao.get(elementId);

          if (p != null) {
            log.debug("\\_ got " + p.getId() + " : " + p.getName());
          }
          else {
            log.debug("\\_ got null");
          }
          return p;
        }
        else {
          throw new SQLException("No DAO found or more than one found.");
        }
      }
      catch (ClassNotFoundException e) {
        throw new SQLException("Cannot resolve element type to a valid class", e);
      }
      catch (IOException e) {
        throw new SQLException("Cannot retrieve poolable element: [" + type + " ] " + elementId);
      }
    }
  }
}