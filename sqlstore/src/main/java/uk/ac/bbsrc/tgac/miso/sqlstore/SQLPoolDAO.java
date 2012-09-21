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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRPool;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
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
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidPool;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;

import javax.persistence.CascadeType;
import java.io.IOException;
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
          "SELECT poolId, concentration, identificationBarcode, name, alias, creationDate, securityProfile_profileId, platformType, ready " +
          "FROM "+TABLE_NAME;

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
          "UPDATE "+TABLE_NAME+" " +
          "SET alias=:alias, concentration=:concentration, identificationBarcode=:identificationBarcode, creationDate=:creationDate, securityProfile_profileId=:securityProfile_profileId, platformType=:platformType, ready=:ready " +
          "WHERE poolId=:poolId";

  public static final String POOL_DELETE =
          "DELETE FROM "+TABLE_NAME+" WHERE poolId=:poolId";

  public static final String POOL_EXPERIMENT_DELETE_BY_POOL_ID =
          "DELETE FROM Pool_Experiment " +
          "WHERE pool_poolId=:pool_poolId";

  public static final String POOL_SELECT_BY_RELATED_PROJECT =
          "SELECT DISTINCT pool.* " +
          "FROM Project p " +
          "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " +
          "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId " +
          "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " +

          "LEFT JOIN emPCR e ON e.dilution_dilutionId = ld.dilutionId " +
          "LEFT JOIN emPCRDilution ed ON ed.emPCR_pcrId = e.pcrId " +

          "LEFT JOIN Pool_LibraryDilution pld ON pld.dilutions_dilutionId = ld.dilutionId " +
          "LEFT JOIN Pool_emPCRDilution ple ON ple.dilutions_dilutionId = ed.dilutionId " +

          "INNER JOIN "+TABLE_NAME+" pool ON pool.poolId = pld.pool_poolId " +
          "OR pool.poolId = ple.pool_poolId " +
          "WHERE p.projectId=?";

  public static final String POOL_SELECT_BY_RELATED_LIBRARY =
          "SELECT DISTINCT pool.* " +
          "FROM Library li " +
          "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " +

          "LEFT JOIN emPCR e ON e.dilution_dilutionId = ld.dilutionId " +
          "LEFT JOIN emPCRDilution ed ON ed.emPCR_pcrId = e.pcrId " +

          "LEFT JOIN Pool_LibraryDilution pld ON pld.dilutions_dilutionId = ld.dilutionId " +
          "LEFT JOIN Pool_emPCRDilution ple ON ple.dilutions_dilutionId = ed.dilutionId " +

          "INNER JOIN "+TABLE_NAME+" pool ON pool.poolId = pld.pool_poolId " +
          "OR pool.poolId = ple.pool_poolId " +
          "WHERE li.libraryId=?";

  public static final String LIBRARY_DILUTION_POOL_DELETE_BY_POOL_ID =
          "DELETE FROM Pool_LibraryDilution " +
          "WHERE pool_poolId=:pool_poolId";

  //ILLUMINA
  public static final String ILLUMINA_POOL_SELECT =
          POOL_SELECT + " WHERE platformType='Illumina'";

  public static final String ILLUMINA_POOL_SELECT_BY_POOL_ID =
          ILLUMINA_POOL_SELECT + " AND poolId=?";

  public static final String ILLUMINA_POOL_SELECT_BY_READY =
          ILLUMINA_POOL_SELECT + " AND ready=1";

//  public static final String ILLUMINA_POOL_SELECT_BY_EXPERIMENT_ID =
//          ILLUMINA_POOL_SELECT + " AND experiment_experimentId=?";

  public static final String ILLUMINA_POOL_SELECT_BY_ID_BARCODE =
          ILLUMINA_POOL_SELECT + " AND identificationBarcode=?";

  public static final String ILLUMINA_POOL_BY_RELATED_LANE =
          "SELECT l.laneId, ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready " +
          "FROM Lane l, "+TABLE_NAME+" ip " +
          "WHERE l.poolId=ip.poolId " +
          "AND ip.platformType='Illumina' " +
          "AND l.laneId=?";

  public static final String ILLUMINA_POOLS_BY_RELATED_LIBRARY_DILUTION =
          "SELECT ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready " +
          "FROM "+TABLE_NAME+" ip, Pool_LibraryDilution p " +
          "WHERE ip.poolId=p.pool_poolId " +
          "AND ip.platformType='Illumina' " +
          "AND p.dilutions_dilutionId=?";

  public static final String ILLUMINA_POOL_SELECT_BY_EXPERIMENT_ID =
          "SELECT ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready " +
          "FROM "+TABLE_NAME+" ip, Pool_Experiment pe " +
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

  public static final String LS454_POOL_BY_RELATED_CHAMBER =
          "SELECT c.chamberId, ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready " +
          "FROM Chamber c, "+TABLE_NAME+" ip " +
          "WHERE c.poolId=ip.poolId " +
          "AND ip.platformType='LS454' " +
          "AND c.chamberId=?";

  public static final String LS454_POOLS_BY_RELATED_EMPCR_DILUTION =
          "SELECT ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready " +
          "FROM "+TABLE_NAME+" ip, Pool_emPCRDilution p " +
          "WHERE ip.poolId=ip.pool_poolId " +
          "AND ip.platformType='LS454' " +
          "AND p.dilutions_dilutionId=?";

  public static final String LS454_POOL_SELECT_BY_EXPERIMENT_ID =
          "SELECT ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready " +
          "FROM "+TABLE_NAME+" ip, Pool_Experiment pe " +
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

  public static final String SOLID_POOL_BY_RELATED_CHAMBER =
          "SELECT c.chamberId, ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready " +
          "FROM Chamber c, "+TABLE_NAME+" ip " +
          "WHERE c.poolId=ip.poolId " +
          "AND ip.platformType='Solid' " +
          "AND c.chamberId=?";

  public static final String SOLID_POOLS_BY_RELATED_EMPCR_DILUTION =
          "SELECT ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready " +
          "FROM "+TABLE_NAME+" ip, Pool_emPCRDilution p " +
          "WHERE ip.poolId=ip.pool_poolId " +
          "AND ip.platformType='Solid' " +
          "AND p.dilutions_dilutionId=?";

  public static final String SOLID_POOL_SELECT_BY_EXPERIMENT_ID =
          "SELECT ip.poolId, ip.concentration, ip.identificationBarcode, ip.name, ip.alias, ip.creationDate, ip.securityProfile_profileId, ip.platformType, ip.ready " +
          "FROM "+TABLE_NAME+" ip, Pool_Experiment pe " +
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

  public static final String EMPCR_DILUTION_POOL_DELETE_BY_POOL_ID =
          "DELETE FROM Pool_emPCRDilution " +
          "WHERE pool_poolId=:pool_poolId";

  protected static final Logger log = LoggerFactory.getLogger(SQLPoolDAO.class);

  private JdbcTemplate template;
  private DilutionStore dilutionDAO;
  private ExperimentStore experimentDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private WatcherStore watcherDAO;
  private CascadeType cascadeType;

  @Autowired
  private MisoNamingScheme<Pool> namingScheme;

  @Override
  public MisoNamingScheme<Pool> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Pool> namingScheme) {
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

  public void setDilutionDAO(DilutionStore dilutionDAO) {
    this.dilutionDAO = dilutionDAO;
  }

  public void setExperimentDAO(ExperimentStore experimentDAO) {
    this.experimentDAO = experimentDAO;
  }

  public void setWatcherDAO(WatcherStore watcherDAO) {
    this.watcherDAO = watcherDAO;
  }

  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  public Pool getPoolByExperiment(Experiment e) {
    if (e.getPlatform() != null) {
      if (e.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
        List eResults = template.query(ILLUMINA_POOL_SELECT_BY_EXPERIMENT_ID, new Object[]{e.getId()}, new PoolMapper());
        Pool p = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
        return p;
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
        List eResults = template.query(LS454_POOL_SELECT_BY_EXPERIMENT_ID, new Object[]{e.getId()}, new PoolMapper());
        Pool p = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
        return p;
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
        List eResults = template.query(SOLID_POOL_SELECT_BY_EXPERIMENT_ID, new Object[]{e.getId()}, new PoolMapper());
        Pool p = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
        return p;
      }
    }
    return null;
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
  public Pool getPoolById(long poolId) throws IOException {
    List eResults = template.query(POOL_SELECT_BY_POOL_ID, new Object[]{poolId}, new PoolMapper());
    Pool e = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
    return e;
  }

  public Pool getIlluminaPoolByBarcode(String barcode) throws IOException {
    List eResults = template.query(ILLUMINA_POOL_SELECT_BY_ID_BARCODE, new Object[]{barcode}, new PoolMapper());
    Pool e = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
    return e;
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
  public Pool getIlluminaPoolById(long poolId) throws IOException {
    List eResults = template.query(ILLUMINA_POOL_SELECT_BY_POOL_ID, new Object[]{poolId}, new PoolMapper());
    return eResults.size() > 0 ? (Pool)eResults.get(0) : null;
  }

  public List<Pool<? extends Poolable>> listAllIlluminaPools() throws IOException {
    return template.query(ILLUMINA_POOL_SELECT, new PoolMapper());
  }

  public List<Pool<? extends Poolable>> listReadyIlluminaPools() throws IOException {
    return template.query(ILLUMINA_POOL_SELECT_BY_READY, new PoolMapper());
  }

  @Deprecated
  public long saveIlluminaPool(IlluminaPool pool) throws IOException {
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
                    //.addValue("identificationBarcode", pool.getIdentificationBarcode())
            .addValue("platformType", PlatformType.ILLUMINA.getKey())
            .addValue("ready", pool.getReadyToRun());

    if (pool.getId() == AbstractPool.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
              .withTableName(TABLE_NAME)
              .usingGeneratedKeyColumns("poolId");
      String name = IlluminaPool.PREFIX + DbUtils.getAutoIncrement(template, TABLE_NAME);
      params.addValue("name", name);
      params.addValue("identificationBarcode", name + "::" + PlatformType.ILLUMINA.getKey());
      Number newId = insert.executeAndReturnKey(params);
      pool.setId(newId.longValue());
      pool.setName(name);
    }
    else {
      params.addValue("poolId", pool.getId())
              .addValue("name", pool.getName())
              .addValue("identificationBarcode", pool.getName() + "::" + PlatformType.ILLUMINA.getKey());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(POOL_UPDATE, params);
    }

    MapSqlParameterSource delparams = new MapSqlParameterSource();
    delparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    namedTemplate.update(LIBRARY_DILUTION_POOL_DELETE_BY_POOL_ID, delparams);

    if (pool.getDilutions() != null && !pool.getDilutions().isEmpty()) {
      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
              .withTableName("Pool_LibraryDilution");

      Cache dc = cacheManager.getCache("libraryDilutionCache");

      for (Dilution d : pool.getDilutions()) {
        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("dilutions_dilutionId", d.getId())
                .addValue("pool_poolId", pool.getId());

        eInsert.execute(esParams);

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            dilutionDAO.save(d);
          }
          else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            dc.remove(DbUtils.hashCodeCacheKeyFor(d.getId()));
          }
        }
      }
    }

    MapSqlParameterSource poolparams = new MapSqlParameterSource();
    poolparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate poolNamedTemplate = new NamedParameterJdbcTemplate(template);
    poolNamedTemplate.update(POOL_EXPERIMENT_DELETE_BY_POOL_ID, poolparams);

    if (pool.getExperiments() != null && !pool.getExperiments().isEmpty()) {
      Cache ec = cacheManager.getCache("experimentCache");

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
            ec.remove(DbUtils.hashCodeCacheKeyFor(e.getId()));
          }
        }
      }
    }

    watcherDAO.removeWatchedEntityByUser(pool, user);

    for (User u : pool.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(pool, u);
    }

    return pool.getId();
  }

  public Pool get454PoolByBarcode(String barcode) throws IOException {
    List eResults = template.query(LS454_POOL_SELECT_BY_ID_BARCODE, new Object[]{barcode}, new PoolMapper());
    Pool e = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
    return e;
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
  public Pool get454PoolById(long poolId) throws IOException {
    List eResults = template.query(LS454_POOL_SELECT_BY_POOL_ID, new Object[]{poolId}, new PoolMapper());
    Pool e = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
    return e;
  }

  public List<Pool<? extends Poolable>> listAll454Pools() throws IOException {
    return template.query(LS454_POOL_SELECT, new PoolMapper());
  }

  public List<Pool<? extends Poolable>> listReady454Pools() throws IOException {
    return template.query(LS454_POOL_SELECT_BY_READY, new PoolMapper());
  }

  @Deprecated
  public long save454Pool(LS454Pool pool) throws IOException {
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
            .addValue("platformType", PlatformType.LS454.getKey())
            .addValue("ready", pool.getReadyToRun());

    if (pool.getId() == AbstractPool.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
              .withTableName(TABLE_NAME)
              .usingGeneratedKeyColumns("poolId");
      String name = LS454Pool.PREFIX + DbUtils.getAutoIncrement(template, TABLE_NAME);
      params.addValue("name", name);
      params.addValue("identificationBarcode", name + "::" + PlatformType.LS454.getKey());
      Number newId = insert.executeAndReturnKey(params);
      pool.setId(newId.longValue());
      pool.setName(name);
    }
    else {
      params.addValue("poolId", pool.getId())
              .addValue("name", pool.getName())
              .addValue("identificationBarcode", pool.getName() + "::" + PlatformType.LS454.getKey());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(POOL_UPDATE, params);
    }

    MapSqlParameterSource delparams = new MapSqlParameterSource();
    delparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    namedTemplate.update(EMPCR_DILUTION_POOL_DELETE_BY_POOL_ID, delparams);

    if (pool.getDilutions() != null && !pool.getDilutions().isEmpty()) {
      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
              .withTableName("Pool_emPCRDilution");
      Cache dc = cacheManager.getCache("emPCRDilutionCache");
      for (Dilution d : pool.getDilutions()) {
        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("dilutions_dilutionId", d.getId())
                .addValue("pool_poolId", pool.getId());

        eInsert.execute(esParams);

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            dilutionDAO.save(d);
          }
          else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            dc.remove(DbUtils.hashCodeCacheKeyFor(d.getId()));
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
      Cache ec = cacheManager.getCache("experimentCache");
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
            ec.remove(DbUtils.hashCodeCacheKeyFor(e.getId()));
          }
        }
      }
    }

    watcherDAO.removeWatchedEntityByUser(pool, user);

    for (User u : pool.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(pool, u);
    }

    return pool.getId();
  }

  public Pool getSolidPoolByBarcode(String barcode) throws IOException {
    List eResults = template.query(SOLID_POOL_SELECT_BY_ID_BARCODE, new Object[]{barcode}, new PoolMapper());
    Pool e = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
    return e;
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
  public Pool getSolidPoolById(long poolId) throws IOException {
    List eResults = template.query(SOLID_POOL_SELECT_BY_POOL_ID, new Object[]{poolId}, new PoolMapper());
    Pool e = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
    return e;
  }

  public List<Pool<? extends Poolable>> listAllSolidPools() throws IOException {
    return template.query(SOLID_POOL_SELECT, new PoolMapper());
  }

  public List<Pool<? extends Poolable>> listReadySolidPools() throws IOException {
    return template.query(SOLID_POOL_SELECT_BY_READY, new PoolMapper());
  }

  @Deprecated
  public long saveSolidPool(SolidPool pool) throws IOException {
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
            .addValue("platformType", PlatformType.SOLID.getKey())
            .addValue("ready", pool.getReadyToRun());

    if (pool.getId() == AbstractPool.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
              .withTableName(TABLE_NAME)
              .usingGeneratedKeyColumns("poolId");
      String name = SolidPool.PREFIX + DbUtils.getAutoIncrement(template, TABLE_NAME);
      params.addValue("name", name);
      params.addValue("identificationBarcode", name + "::" + PlatformType.SOLID.getKey());
      Number newId = insert.executeAndReturnKey(params);
      pool.setId(newId.longValue());
      pool.setName(name);
    }
    else {
      params.addValue("poolId", pool.getId())
              .addValue("name", pool.getName())
              .addValue("identificationBarcode", pool.getName() + "::" + PlatformType.SOLID.getKey());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(POOL_UPDATE, params);
    }

    MapSqlParameterSource delparams = new MapSqlParameterSource();
    delparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    namedTemplate.update(EMPCR_DILUTION_POOL_DELETE_BY_POOL_ID, delparams);

    if (pool.getDilutions() != null && !pool.getDilutions().isEmpty()) {
      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
              .withTableName("Pool_emPCRDilution");
      Cache dc = cacheManager.getCache("emPCRDilutionCache");
      for (Dilution d : pool.getDilutions()) {
        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("dilutions_dilutionId", d.getId())
                .addValue("pool_poolId", pool.getId());

        eInsert.execute(esParams);

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            dilutionDAO.save(d);
          }
          else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            dc.remove(DbUtils.hashCodeCacheKeyFor(d.getId()));
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
      Cache ec = cacheManager.getCache("experimentCache");
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
            ec.remove(DbUtils.hashCodeCacheKeyFor(e.getId()));
          }
        }
      }
    }

    watcherDAO.removeWatchedEntityByUser(pool, user);

    for (User u : pool.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(pool, u);
    }

    return pool.getId();
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
  public emPCRPool getEmPCRPoolById(long poolId) throws IOException {
    List eResults = template.query(EMPCR_POOL_SELECT_BY_POOL_ID, new Object[]{poolId}, new EmPCRPoolMapper());
    emPCRPool e = eResults.size() > 0 ? (emPCRPool) eResults.get(0) : null;
    return e;
  }

  public List<emPCRPool> listAllEmPCRPools() throws IOException {
    return template.query(EMPCR_POOL_SELECT, new EmPCRPoolMapper());
  }

  @Deprecated
  public long saveEmPCRPool(emPCRPool pool) throws IOException {
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

    if (pool.getId() == AbstractPool.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
              .withTableName(TABLE_NAME)
              .usingGeneratedKeyColumns("poolId");
      String name = emPCRPool.PREFIX + DbUtils.getAutoIncrement(template, TABLE_NAME);
      params.addValue("name", name);
      params.addValue("identificationBarcode", name + "::" + pool.getPlatformType().getKey());
      Number newId = insert.executeAndReturnKey(params);
      pool.setId(newId.longValue());
      pool.setName(name);
    }
    else {
      params.addValue("poolId", pool.getId())
              .addValue("name", pool.getName())
              .addValue("identificationBarcode", pool.getName() + "::" + pool.getPlatformType().getKey());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(POOL_UPDATE, params);
    }

    MapSqlParameterSource delparams = new MapSqlParameterSource();
    delparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    namedTemplate.update(LIBRARY_DILUTION_POOL_DELETE_BY_POOL_ID, delparams);

    if (pool.getDilutions() != null && !pool.getDilutions().isEmpty()) {
      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
              .withTableName("Pool_LibraryDilution");
      Cache dc = cacheManager.getCache("libraryDilutionCache");
      for (Dilution d : pool.getDilutions()) {
        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("dilutions_dilutionId", d.getId())
                .addValue("pool_poolId", pool.getId());

        eInsert.execute(esParams);

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            dilutionDAO.save(d);
          }
          else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            dc.remove(DbUtils.hashCodeCacheKeyFor(d.getId()));
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
      Cache ec = cacheManager.getCache("experimentCache");
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
            ec.remove(DbUtils.hashCodeCacheKeyFor(e.getId()));
          }
        }
      }
    }

    watcherDAO.removeWatchedEntityByUser(pool, user);

    for (User u : pool.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(pool, u);
    }

    return pool.getId();
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  @TriggersRemove(cacheName = "poolCache",
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

    if (pool.getId() == AbstractPool.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
              .withTableName(TABLE_NAME)
              .usingGeneratedKeyColumns("poolId");
      try {
        pool.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", pool);
        pool.setName(name);

        if (namingScheme.validateField("name", pool.getName())) {
          String barcode = name + "::" + pool.getPlatformType().getKey();
          params.addValue("name", name);

          params.addValue("identificationBarcode", barcode);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != pool.getId()) {
            log.error("Expected Pool ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(POOL_DELETE, new MapSqlParameterSource().addValue("poolId", pool.getId()));
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
      /*
      String name = AbstractPool.lookupPrefix(pool.getPlatformType())+ DbUtils.getAutoIncrement(template, TABLE_NAME);
      params.addValue("name", name);
      params.addValue("identificationBarcode", name + "::" + pool.getPlatformType().getKey());
      Number newId = insert.executeAndReturnKey(params);
      pool.setPoolId(newId.longValue());
      pool.setName(name);
      */
    }
    else {
      try {
        if (namingScheme.validateField("name", pool.getName())) {
          params.addValue("poolId", pool.getId())
                .addValue("name", pool.getName())
                .addValue("identificationBarcode", pool.getName() + "::" + pool.getPlatformType().getKey());
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
      /*
      params.addValue("poolId", pool.getPoolId())
              .addValue("name", pool.getName())
              .addValue("identificationBarcode", pool.getName() + "::" + pool.getPlatformType().getKey());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(POOL_UPDATE, params);
      */
    }

    MapSqlParameterSource delparams = new MapSqlParameterSource();
    delparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    namedTemplate.update(LIBRARY_DILUTION_POOL_DELETE_BY_POOL_ID, delparams);
    namedTemplate.update(EMPCR_DILUTION_POOL_DELETE_BY_POOL_ID, delparams);

    if (pool.getDilutions() != null && !pool.getDilutions().isEmpty()) {
      String type = pool.getDilutions().iterator().next().getClass().getSimpleName();

      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template).withTableName("Pool_"+type);
      String lc = type.substring(0,1).toLowerCase() + type.substring(1);

      Cache dc = cacheManager.getCache(lc+"Cache");

      for (Dilution d : pool.getDilutions()) {
        log.debug("Linking "+d.getName() + " to " + pool.getName());
        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("dilutions_dilutionId", d.getId())
                .addValue("pool_poolId", pool.getId());

        eInsert.execute(esParams);

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            dilutionDAO.save(d);
          }
          else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            dc.remove(DbUtils.hashCodeCacheKeyFor(d.getId()));
          }
        }
      }
    }

    MapSqlParameterSource poolparams = new MapSqlParameterSource();
    poolparams.addValue("pool_poolId", pool.getId());
    NamedParameterJdbcTemplate poolNamedTemplate = new NamedParameterJdbcTemplate(template);
    poolNamedTemplate.update(POOL_EXPERIMENT_DELETE_BY_POOL_ID, poolparams);

    if (pool.getExperiments() != null && !pool.getExperiments().isEmpty()) {
      Cache ec = cacheManager.getCache("experimentCache");

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
            ec.remove(DbUtils.hashCodeCacheKeyFor(e.getId()));
          }
        }
      }
    }

    watcherDAO.removeWatchedEntityByUser(pool, user);

    for (User u : pool.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(pool, u);
    }

    return pool.getId();
  }

  @Override
  public Pool<? extends Poolable> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException {
    List<Pool<? extends Poolable>> pools = listAllByPlatformAndSearch(platformType, barcode);
    return pools.size() == 1 ? pools.get(0) : null;
  }

  public Collection<Pool<? extends Poolable>> listByLibraryId(long libraryId) throws IOException {
    return template.query(POOL_SELECT_BY_RELATED_LIBRARY, new Object[]{libraryId}, new PoolMapper());
  }

  public Collection<Pool<? extends Poolable>> listByProjectId(long projectId) throws IOException {
    return template.query(POOL_SELECT_BY_RELATED_PROJECT, new Object[]{projectId}, new PoolMapper());
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
  public Pool get(long poolId) throws IOException {
    List eResults = template.query(POOL_SELECT_BY_POOL_ID, new Object[]{poolId}, new PoolMapper());
    Pool e = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
    return e;
  }

  public Pool lazyGet(long poolId) throws IOException {
    List eResults = template.query(POOL_SELECT_BY_POOL_ID, new Object[]{poolId}, new LazyPoolMapper());
    Pool e = eResults.size() > 0 ? (Pool) eResults.get(0) : null;
    return e;
  }

  public Collection<Pool<? extends Poolable>> listAll() throws IOException {
    return template.query(POOL_SELECT, new PoolMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM "+TABLE_NAME);
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

  @TriggersRemove(
          cacheName = "poolCache",
          keyGenerator = @KeyGenerator(
                  name = "HashCodeCacheKeyGenerator",
                  properties = {
                          @Property(name = "includeMethod", value = "false"),
                          @Property(name = "includeParameterTypes", value = "false")
                  }
          )
  )
  public boolean remove(Pool pool) throws IOException {
    MapSqlParameterSource poolparams = new MapSqlParameterSource();
    poolparams.addValue("pool_poolId", pool.getId());
    poolparams.addValue("poolId", pool.getId());
    NamedParameterJdbcTemplate poolNamedTemplate = new NamedParameterJdbcTemplate(template);

    boolean ok = true;
    if (pool.isDeletable() && poolNamedTemplate.update(POOL_DELETE, poolparams) == 1) {
      if (!pool.getDilutions().isEmpty()) {
        Dilution d = (Dilution) pool.getDilutions().iterator().next();
        Cache dc = null;
        if (d instanceof LibraryDilution) {
          ok = (poolNamedTemplate.update(LIBRARY_DILUTION_POOL_DELETE_BY_POOL_ID, poolparams) == 1);
          dc = cacheManager.getCache("libraryDilutionCache");
        }
        else {
          ok = (poolNamedTemplate.update(EMPCR_DILUTION_POOL_DELETE_BY_POOL_ID, poolparams) == 1);
          dc = cacheManager.getCache("emPCRDilutionCache");
        }

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            dilutionDAO.save(d);
          }
          else if (this.cascadeType.equals(CascadeType.REMOVE)) {
            if (dc != null) dc.remove(DbUtils.hashCodeCacheKeyFor(d.getId()));
          }
        }
      }

      if (!pool.getExperiments().isEmpty()) {
        ok = (poolNamedTemplate.update(POOL_EXPERIMENT_DELETE_BY_POOL_ID, poolparams) == 1);
        Cache ec = cacheManager.getCache("experimentCache");
        Collection<Experiment> exps = pool.getExperiments();
        for (Experiment e : exps) {
          if (this.cascadeType != null) {
            if (this.cascadeType.equals(CascadeType.PERSIST)) {
              experimentDAO.save(e);
            }
            else if (this.cascadeType.equals(CascadeType.REMOVE)) {
              ec.remove(DbUtils.hashCodeCacheKeyFor(e.getId()));
            }
          }
        }
      }
      return ok;
    }
    return false;
  }

  public class PoolMapper implements RowMapper<Pool<? extends Poolable>> {
    public Pool<? extends Poolable> mapRow(ResultSet rs, int rowNum) throws SQLException {
      Pool p = null;
      try {
        p = dataObjectFactory.getPool();
        PlatformType pt = PlatformType.get(rs.getString("platformType"));
        p.setPlatformType(pt);

        if (pt != null) {
          List<? extends Dilution> dilutions = new ArrayList<Dilution>(dilutionDAO.listAllDilutionsByPoolAndPlatform(rs.getLong("poolId"), pt));
          Collections.sort(dilutions);
          p.setPoolableElements(dilutions);
        }

        p.setId(rs.getLong("poolId"));
        p.setName(rs.getString("name"));
        p.setAlias(rs.getString("alias"));
        p.setCreationDate(rs.getDate("creationDate"));
        p.setConcentration(rs.getDouble("concentration"));
        p.setIdentificationBarcode(rs.getString("identificationBarcode"));
        p.setReadyToRun(rs.getBoolean("ready"));

        p.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));

        p.setExperiments(experimentDAO.listByPoolId(rs.getLong("poolId")));

        p.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(p.getWatchableIdentifier())));
        if (p.getSecurityProfile() != null &&
            p.getSecurityProfile().getOwner() != null) {
          p.addWatcher(p.getSecurityProfile().getOwner());
        }
        for (User u : watcherDAO.getWatchersByWatcherGroup("PoolWatchers")) {
          p.addWatcher(u);
        }
      }
      catch (IOException e1) {
        log.error("Cannot map from database to Pool: ", e1);
        e1.printStackTrace();
      }
      return p;
    }
  }

  public class LazyPoolMapper implements RowMapper<Pool<? extends Poolable>> {
    public Pool<? extends Poolable> mapRow(ResultSet rs, int rowNum) throws SQLException {
      Pool p = null;
      try {
        p = dataObjectFactory.getPool();
        PlatformType pt = PlatformType.get(rs.getString("platformType"));
        p.setPlatformType(pt);

        if (pt != null) {
          List<? extends Dilution> dilutions = new ArrayList<Dilution>(dilutionDAO.listAllDilutionsByPoolAndPlatform(rs.getLong("poolId"), pt));
          Collections.sort(dilutions);
          p.setPoolableElements(dilutions);
        }

        p.setId(rs.getLong("poolId"));
        p.setName(rs.getString("name"));
        p.setAlias(rs.getString("alias"));
        p.setCreationDate(rs.getDate("creationDate"));
        p.setConcentration(rs.getDouble("concentration"));
        p.setIdentificationBarcode(rs.getString("identificationBarcode"));
        p.setReadyToRun(rs.getBoolean("ready"));

        p.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));

        //p.setExperiments(experimentDAO.listByPoolId(rs.getLong("poolId")));

        p.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(p.getWatchableIdentifier())));
        if (p.getSecurityProfile() != null &&
            p.getSecurityProfile().getOwner() != null) {
          p.addWatcher(p.getSecurityProfile().getOwner());
        }
        for (User u : watcherDAO.getWatchersByWatcherGroup("PoolWatchers")) {
          p.addWatcher(u);
        }
      }
      catch (IOException e1) {
        log.error("Cannot map from database to Pool: ", e1);
        e1.printStackTrace();
      }
      return p;
    }
  }

  public class IlluminaPoolMapper implements RowMapper<IlluminaPool> {
    public IlluminaPool mapRow(ResultSet rs, int rowNum) throws SQLException {
      IlluminaPool illuminaPool = dataObjectFactory.getIlluminaPool();
      illuminaPool.setId(rs.getLong("poolId"));
      illuminaPool.setName(rs.getString("name"));
      illuminaPool.setAlias(rs.getString("alias"));
      illuminaPool.setCreationDate(rs.getDate("creationDate"));
      illuminaPool.setConcentration(rs.getDouble("concentration"));
      illuminaPool.setIdentificationBarcode(rs.getString("identificationBarcode"));
      illuminaPool.setPlatformType(PlatformType.ILLUMINA);
      illuminaPool.setReadyToRun(rs.getBoolean("ready"));

      //illuminaPool.setLastUpdated(rs.getTimestamp("lastUpdated"));

      try {
        illuminaPool.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        List<LibraryDilution> dilutions = new ArrayList<LibraryDilution>(dilutionDAO.listByIlluminaPoolId(rs.getLong("poolId")));
        Collections.sort(dilutions);
        illuminaPool.setPoolableElements(dilutions);
        illuminaPool.setExperiments(experimentDAO.listByPoolId(rs.getLong("poolId")));

        illuminaPool.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(illuminaPool.getWatchableIdentifier())));
        if (illuminaPool.getSecurityProfile() != null &&
            illuminaPool.getSecurityProfile().getOwner() != null) {
          illuminaPool.addWatcher(illuminaPool.getSecurityProfile().getOwner());
        }
        for (User u : watcherDAO.getWatchersByWatcherGroup("PoolWatchers")) {
          illuminaPool.addWatcher(u);
        }
      }
      catch (IOException e1) {
        log.error("Cannot map from database to IlluminaPool: ", e1);
        e1.printStackTrace();
      }
      return illuminaPool;
    }
  }

  public class LS454PoolMapper implements RowMapper<LS454Pool> {
    public LS454Pool mapRow(ResultSet rs, int rowNum) throws SQLException {
      LS454Pool ls454Pool = dataObjectFactory.getLS454Pool();
      ls454Pool.setId(rs.getLong("poolId"));
      ls454Pool.setName(rs.getString("name"));
      ls454Pool.setAlias(rs.getString("alias"));
      ls454Pool.setCreationDate(rs.getDate("creationDate"));
      ls454Pool.setConcentration(rs.getDouble("concentration"));
      ls454Pool.setIdentificationBarcode(rs.getString("identificationBarcode"));
      ls454Pool.setPlatformType(PlatformType.LS454);
      ls454Pool.setReadyToRun(rs.getBoolean("ready"));

      //ls454Pool.setLastUpdated(rs.getTimestamp("lastUpdated"));

      try {
        ls454Pool.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        List<emPCRDilution> dilutions = new ArrayList<emPCRDilution>(dilutionDAO.listByLS454PoolId(rs.getLong("poolId")));
        ls454Pool.setPoolableElements(dilutions);
        Collections.sort(dilutions);
        ls454Pool.setExperiments(experimentDAO.listByPoolId(rs.getLong("poolId")));

        ls454Pool.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(ls454Pool.getWatchableIdentifier())));
        if (ls454Pool.getSecurityProfile() != null &&
            ls454Pool.getSecurityProfile().getOwner() != null) {
          ls454Pool.addWatcher(ls454Pool.getSecurityProfile().getOwner());
        }
        for (User u : watcherDAO.getWatchersByWatcherGroup("PoolWatchers")) {
          ls454Pool.addWatcher(u);
        }
      }
      catch (IOException e1) {
        log.error("Cannot map from database to LS454Pool: ", e1);
        e1.printStackTrace();
      }
      return ls454Pool;
    }
  }

  public class SolidPoolMapper implements RowMapper<SolidPool> {
    public SolidPool mapRow(ResultSet rs, int rowNum) throws SQLException {
      SolidPool solidPool = dataObjectFactory.getSolidPool();
      solidPool.setId(rs.getLong("poolId"));
      solidPool.setName(rs.getString("name"));
      solidPool.setAlias(rs.getString("alias"));
      solidPool.setCreationDate(rs.getDate("creationDate"));
      solidPool.setConcentration(rs.getDouble("concentration"));
      solidPool.setIdentificationBarcode(rs.getString("identificationBarcode"));
      solidPool.setPlatformType(PlatformType.SOLID);
      solidPool.setReadyToRun(rs.getBoolean("ready"));

      //solidPool.setLastUpdated(rs.getTimestamp("lastUpdated"));

      try {
        solidPool.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        List<emPCRDilution> dilutions = new ArrayList<emPCRDilution>(dilutionDAO.listBySolidPoolId(rs.getLong("poolId")));
        Collections.sort(dilutions);
        solidPool.setPoolableElements(dilutions);

        solidPool.setExperiments(experimentDAO.listByPoolId(rs.getLong("poolId")));

        solidPool.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(solidPool.getWatchableIdentifier())));
        if (solidPool.getSecurityProfile() != null &&
            solidPool.getSecurityProfile().getOwner() != null) {
          solidPool.addWatcher(solidPool.getSecurityProfile().getOwner());
        }
        for (User u : watcherDAO.getWatchersByWatcherGroup("PoolWatchers")) {
          solidPool.addWatcher(u);
        }
      }
      catch (IOException e1) {
        log.error("Cannot map from database to SolidPool: ", e1);
        e1.printStackTrace();
      }
      return solidPool;
    }
  }

  public class EmPCRPoolMapper implements RowMapper<emPCRPool> {
    public emPCRPool mapRow(ResultSet rs, int rowNum) throws SQLException {
      PlatformType platformType = PlatformType.get(rs.getString("platformType"));
      if (platformType != null) {
        emPCRPool pool = dataObjectFactory.getEmPCRPool(platformType);
        pool.setId(rs.getLong("poolId"));
        pool.setName(rs.getString("name"));
        pool.setAlias(rs.getString("alias"));
        pool.setCreationDate(rs.getDate("creationDate"));
        pool.setConcentration(rs.getDouble("concentration"));
        pool.setIdentificationBarcode(rs.getString("identificationBarcode"));
        pool.setPlatformType(platformType);
        pool.setReadyToRun(rs.getBoolean("ready"));

        //pool.setLastUpdated(rs.getTimestamp("lastUpdated"));

        try {
          pool.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
          List<LibraryDilution> dilutions = new ArrayList<LibraryDilution>(dilutionDAO.listByEmPCRPoolId(rs.getLong("poolId")));
          Collections.sort(dilutions);
          pool.setPoolableElements(dilutions);

          pool.setExperiments(experimentDAO.listByPoolId(rs.getLong("poolId")));

          pool.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(pool.getWatchableIdentifier())));
          if (pool.getSecurityProfile() != null &&
              pool.getSecurityProfile().getOwner() != null) {
            pool.addWatcher(pool.getSecurityProfile().getOwner());
          }
          for (User u : watcherDAO.getWatchersByWatcherGroup("PoolWatchers")) {
            pool.addWatcher(u);
          }
        }
        catch (IOException e1) {
          log.error("Cannot map from database to emPCRPool: ", e1);
          e1.printStackTrace();
        }
        return pool;
      }
      return null;
    }
  }
}
