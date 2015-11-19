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
import java.util.List;
import java.util.regex.Matcher;

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.store.SecurityStore;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractExperiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * A data access object designed for retrieving Experiments from the LIMS database. This DAO should be configured with a spring
 * {@link JdbcTemplate} object which will be used to query the database.
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLExperimentDAO implements ExperimentStore {
  private static final String TABLE_NAME = "Experiment";

  public static final String EXPERIMENTS_SELECT = "SELECT experimentId, name, description, alias, accession, title, platform_platformId, securityProfile_profileId, study_studyId, lastModifier "
      + "FROM " + TABLE_NAME;

  public static final String EXPERIMENTS_SELECT_LIMIT = EXPERIMENTS_SELECT + " ORDER BY experimentId DESC LIMIT ?";

  public static final String EXPERIMENT_SELECT_BY_ID = EXPERIMENTS_SELECT + " " + "WHERE experimentId = ?";

  public static final String EXPERIMENTS_SELECT_BY_SEARCH = EXPERIMENTS_SELECT + " WHERE " + "name LIKE ? OR " + "alias LIKE ? OR "
      + "description LIKE ? ";

  public static final String EXPERIMENT_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name=:name, description=:description, alias=:alias, accession=:accession, title=:title, platform_platformId=:platform_platformId, securityProfile_profileId=:securityProfile_profileId, lastModifier=:lastModifier "
      + "WHERE experimentId=:experimentId";

  public static final String EXPERIMENT_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE experimentId=:experimentId";

  public static final String PROFILE_SELECT_BY_EXPERIMENT_ID = "SELECT sp.profileId, sp.allowAllInternal, sp.owner_userId " + "FROM "
      + TABLE_NAME + " e, SecurityProfile sp " + "WHERE sp.profileId = e.SecurityProfile_profileId " + "AND e.experimentId=?";

  public static final String EXPERIMENTS_BY_RELATED_STUDY = "SELECT e.experimentId, e.name, e.description, e.alias, e.accession, e.title, e.platform_platformId, e.securityProfile_profileId, e.study_studyId, e.lastModifier "
      + "FROM " + TABLE_NAME + " e, Study s " + "WHERE e.study_studyId=s.studyId " + "AND s.studyId=?";

  public static final String EXPERIMENTS_BY_RELATED_POOL = "SELECT e.experimentId, e.name, e.description, e.alias, e.accession, e.title, e.platform_platformId, e.securityProfile_profileId, e.study_studyId, e.lastModifier, pe.experiments_experimentId "
      + "FROM " + TABLE_NAME + " e, Pool_Experiment pe " + "WHERE pe.experiments_experimentId=e.experimentId " + "AND pe.pool_poolId=?";

  public static final String EXPERIMENT_BY_RELATED_PARTITION = "SELECT e.experimentId, e.name, e.description, e.alias, e.accession, e.title, e.platform_platformId, e.securityProfile_profileId, e.study_studyId, er.runs_runId, e.lastModifier "
      + "FROM " + TABLE_NAME + " e, _Partition l " + "WHERE e.experimentId=l.experiment_experimentId " + "AND l.partitionId=?";

  public static final String EXPERIMENTS_BY_RELATED_SUBMISSION = "SELECT e.experimentId, e.name, e.description, e.alias, e.accession, e.title, e.platform_platformId, e.securityProfile_profileId, e.study_studyId, e.lastModifier "
      + "FROM " + TABLE_NAME + " e, Submission_Experiment se " + "WHERE e.experimentId=se.experiments_experimentId "
      + "AND se.submission_submissionId=?";

  public static final String POOL_EXPERIMENT_DELETE_BY_EXPERIMENT_ID = "DELETE FROM Pool_Experiment "
      + "WHERE experiments_experimentId=:experiments_experimentId";

  protected static final Logger log = LoggerFactory.getLogger(SQLExperimentDAO.class);

  private StudyStore studyDAO;
  private SampleStore sampleDAO;
  private RunStore runDAO;
  private PoolStore poolDAO;
  private PlatformStore platformDAO;
  private KitStore kitDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private CascadeType cascadeType;
  private ChangeLogStore changeLogDAO;
  private SecurityStore securityDAO;

  @Autowired
  private MisoNamingScheme<Experiment> namingScheme;

  @Override
  public MisoNamingScheme<Experiment> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Experiment> namingScheme) {
    this.namingScheme = namingScheme;
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

  public void setStudyDAO(StudyStore studyDAO) {
    this.studyDAO = studyDAO;
  }

  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
  }

  public void setRunDAO(RunStore runDAO) {
    this.runDAO = runDAO;
  }

  public void setPoolDAO(PoolStore poolDAO) {
    this.poolDAO = poolDAO;
  }

  public void setPlatformDAO(PlatformStore platformDAO) {
    this.platformDAO = platformDAO;
  }

  public void setKitDAO(KitStore kitDAO) {
    this.kitDAO = kitDAO;
  }

  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  private JdbcTemplate template;
  private int maxQueryParams = 500;

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  /**
   * Get the maximum allowed number of parameters that can be supplied to a parameterised query. This is effectively the maximum bound for
   * an "IN" list - i.e. SELECT * FROM foo WHERE foo.bar IN (?,?,?,...,?). If unset, this defaults to 500. Typically, the limit for oracle
   * databases is 1000. If, for any query that takes a list, the size of the list is greater than this value, the query will be split into
   * several smaller subqueries and the results aggregated. As a user, you should not notice any difference.
   *
   * @return the maximum bound on the query list size
   */
  public int getMaxQueryParams() {
    return maxQueryParams;
  }

  /**
   * Set the maximum allowed number of parameters that can be supplied to a parameterised query. This is effectively the maximum bound for
   * an "IN" list - i.e. SELECT * FROM foo WHERE foo.bar IN (?,?,?,...,?). If unset, this defaults to 500. Typically, the limit for oracle
   * databases is 1000. If, for any query that takes a list, the size of the list is greater than this value, the query will be split into
   * several smaller subqueries and the results aggregated.
   *
   * @param maxQueryParams
   *          the maximum bound on the query list size - this should never be greater than that allowed by the database, but can be smaller
   */
  public void setMaxQueryParams(int maxQueryParams) {
    this.maxQueryParams = maxQueryParams;
  }

  @Override
  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  private void purgeListCache(Experiment experiment, boolean replace) {
    Cache cache = cacheManager.getCache("experimentListCache");
    DbUtils.updateListCache(cache, replace, experiment, Experiment.class);
  }

  private void purgeListCache(Experiment experiment) {
    purgeListCache(experiment, true);
  }

  /**
   * Writes the given experiment to the database, using the default transaction strategy configured for the datasource.
   *
   * @param experiment
   *          the experiment to write
   */
  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "experimentCache",
      "lazyExperimentCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long save(Experiment experiment) throws IOException {
    Long securityProfileId = experiment.getSecurityProfile().getProfileId();
    if (securityProfileId == null || this.cascadeType != null) {
      securityProfileId = securityProfileDAO.save(experiment.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", experiment.getAlias());
    params.addValue("accession", experiment.getAccession());
    params.addValue("description", experiment.getDescription());
    params.addValue("title", experiment.getTitle());
    params.addValue("platform_platformId", experiment.getPlatform().getPlatformId());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("study_studyId", experiment.getStudy().getId());
    params.addValue("lastModifier", experiment.getLastModifier().getUserId());

    if (experiment.getId() == AbstractExperiment.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("experimentId");
      try {
        experiment.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", experiment);
        experiment.setName(name);

        if (namingScheme.validateField("name", experiment.getName())) {
          params.addValue("name", name);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != experiment.getId()) {
            log.error("Expected Experiment ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(EXPERIMENT_DELETE,
                new MapSqlParameterSource().addValue("experimentId", newId.longValue()));
            throw new IOException("Something bad happened. Expected Experiment ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save Experiment - invalid field:" + experiment.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Experiment - issue with naming scheme", e);
      }
    } else {
      try {
        if (namingScheme.validateField("name", experiment.getName())) {
          params.addValue("experimentId", experiment.getId());
          params.addValue("name", experiment.getName());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(EXPERIMENT_UPDATE, params);
        } else {
          throw new IOException("Cannot save Experiment - invalid field:" + experiment.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Experiment - issue with naming scheme", e);
      }
    }

    if (this.cascadeType != null) {
      MapSqlParameterSource eParams = new MapSqlParameterSource();
      eParams.addValue("experiments_experimentId", experiment.getId());
      NamedParameterJdbcTemplate eNamedTemplate = new NamedParameterJdbcTemplate(template);
      eNamedTemplate.update(POOL_EXPERIMENT_DELETE_BY_EXPERIMENT_ID, eParams);

      if (experiment.getPool() != null) {
        SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template).withTableName("Pool_Experiment");

        MapSqlParameterSource esParams = new MapSqlParameterSource();
        esParams.addValue("experiments_experimentId", experiment.getId()).addValue("pool_poolId", experiment.getPool().getId());
        eInsert.execute(esParams);

        if (this.cascadeType.equals(CascadeType.PERSIST)) {
          DbUtils.flushCache(cacheManager, "poolCache");
        } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
          DbUtils.updateCaches(cacheManager, experiment.getPool(), Pool.class);
        }
      }

      Study s = experiment.getStudy();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (s != null) studyDAO.save(s);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (s != null) {
          DbUtils.updateCaches(cacheManager, s, Study.class);
        }
      }

      if (!experiment.getKits().isEmpty()) {
        for (Kit k : experiment.getKits()) {
          kitDAO.save(k);

          SimpleJdbcInsert kInsert = new SimpleJdbcInsert(template).withTableName("Experiment_Kit");

          MapSqlParameterSource kParams = new MapSqlParameterSource();
          kParams.addValue("experiments_experimentId", experiment.getId()).addValue("kits_kidId", k.getId());
          try {
            kInsert.execute(kParams);
          } catch (DuplicateKeyException dke) {
            // ignore
          }
        }
      }

      purgeListCache(experiment);
    }

    return experiment.getId();
  }

  @Override
  @Cacheable(cacheName = "experimentListCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public List<Experiment> listAll() {
    return template.query(EXPERIMENTS_SELECT, new ExperimentMapper(true));
  }

  @Override
  public List<Experiment> listAllWithLimit(long limit) throws IOException {
    return template.query(EXPERIMENTS_SELECT_LIMIT, new Object[] { limit }, new ExperimentMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<Experiment> listBySearch(String query) {
    String mySQLQuery = "%" + query.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
    return template.query(EXPERIMENTS_SELECT_BY_SEARCH, new Object[] { mySQLQuery, mySQLQuery, mySQLQuery }, new ExperimentMapper(true));
  }

  @Override
  public List<Experiment> listByStudyId(long studyId) {
    List results = template.query(EXPERIMENTS_BY_RELATED_STUDY, new Object[] { studyId }, new ExperimentMapper());
    List<Experiment> es = results;
    return es;
  }

  @Override
  public List<Experiment> listBySubmissionId(long submissionId) throws IOException {
    return template.query(EXPERIMENTS_BY_RELATED_SUBMISSION, new Object[] { submissionId }, new ExperimentMapper());
  }

  @Override
  public List<Experiment> listByPoolId(long poolId) {
    return template.query(EXPERIMENTS_BY_RELATED_POOL, new Object[] { poolId }, new ExperimentMapper(true));
  }

  @Override
  @Cacheable(cacheName = "experimentCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public Experiment get(long experimentId) throws IOException {
    List eResults = template.query(EXPERIMENT_SELECT_BY_ID, new Object[] { experimentId }, new ExperimentMapper());
    Experiment e = eResults.size() > 0 ? (Experiment) eResults.get(0) : null;
    return e;
  }

  @Override
  public Experiment lazyGet(long experimentId) throws IOException {
    List eResults = template.query(EXPERIMENT_SELECT_BY_ID, new Object[] { experimentId }, new ExperimentMapper(true));
    Experiment e = eResults.size() > 0 ? (Experiment) eResults.get(0) : null;
    return e;
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "experimentCache",
      "lazyExperimentCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean remove(Experiment experiment) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (experiment.isDeletable()
        && (namedTemplate.update(EXPERIMENT_DELETE, new MapSqlParameterSource().addValue("experimentId", experiment.getId())) == 1)) {
      Study s = experiment.getStudy();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (s != null) studyDAO.save(s);
        if (experiment.getPool() != null) {
          DbUtils.updateCaches(cacheManager, experiment.getPool(), Pool.class);
        }
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (s != null) {
          DbUtils.updateCaches(cacheManager, s, Study.class);

          if (experiment.getPool() != null) {
            DbUtils.updateCaches(cacheManager, experiment.getPool(), Pool.class);
          }
        }
      }

      purgeListCache(experiment, false);

      return true;
    }
    return false;
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

  public class ExperimentMapper extends CacheAwareRowMapper<Experiment> {
    public ExperimentMapper() {
      super(Experiment.class);
    }

    public ExperimentMapper(boolean lazy) {
      super(Experiment.class, lazy);
    }

    @Override
    public Experiment mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("experimentId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for experiment " + id);
          return (Experiment) element.getObjectValue();
        }
      }
      Experiment e = dataObjectFactory.getExperiment();
      e.setId(id);
      e.setName(rs.getString("name"));
      e.setAlias(rs.getString("alias"));
      e.setAccession(rs.getString("accession"));
      e.setDescription(rs.getString("description"));
      e.setTitle(rs.getString("title"));
      try {
        e.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
        e.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        e.setStudy(studyDAO.lazyGet(rs.getLong("study_studyId")));

        Platform p = platformDAO.get(rs.getLong("platform_platformId"));
        e.setPlatform(p);

        if (!isLazy()) {
          e.setPool(poolDAO.getPoolByExperiment(e));
          e.setKits(kitDAO.listByExperiment(rs.getLong("experimentId")));
        }
        e.getChangeLog().addAll(getChangeLogDAO().listAllById(TABLE_NAME, id));
      } catch (IOException e1) {
        log.error("experiment row mapper", e1);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), e));
      }

      return e;
    }
  }
}
