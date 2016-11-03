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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.context.SecurityContextHolder;
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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.core.store.StatusStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.WatcherStore;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.BridgeCollectionUpdater;
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
public class SQLRunDAO implements RunStore {
  private static final String TABLE_NAME = "Run";

  public static final String RUNS_SELECT = "SELECT r.runId, r.name, r.alias, r.description, r.accession, r.platformRunId, r.pairedEnd, r.cycles, r.filePath, r.securityProfile_profileId, "
      + "r.platformType, r.status_statusId, r.sequencerReference_sequencerReferenceId, r.lastModifier, rmod.lastUpdated, r.sequencingParameters_parametersId "
      + "FROM " + TABLE_NAME + " r "
      + " LEFT JOIN (SELECT runId, MAX(changeTime) AS lastUpdated FROM RunChangeLog GROUP BY runId) rmod ON r.runId = rmod.runId";

  public static final String RUNS_SELECT_LIMIT = RUNS_SELECT + " ORDER BY r.runId DESC LIMIT ?";

  public static final String RUN_SELECT_BY_ID = RUNS_SELECT + " WHERE r.runId = ?";

  public static final String RUN_SELECT_BY_ALIAS = RUNS_SELECT + " WHERE r.alias = ?";

  public static final String RUN_SELECT_BY_SEQUENCER_ID = RUNS_SELECT + " WHERE r.sequencerReference_sequencerReferenceId = ?";

  private static final String SEARCH_WHERE = " WHERE UPPER(r.name) LIKE ? OR UPPER(r.alias) LIKE ? OR UPPER(description) LIKE ? ";

  public static final String RUNS_SELECT_BY_SEARCH = RUNS_SELECT + SEARCH_WHERE;

  public static final String RUNS_COUNT_BY_SEARCH = "SELECT COUNT(*) FROM " + TABLE_NAME + " r" + SEARCH_WHERE;

  public static final String RUN_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET name=:name, alias=:alias, description=:description, accession=:accession, platformRunId=:platformRunId, "
      + "pairedEnd=:pairedEnd, cycles=:cycles, filePath=:filePath, securityProfile_profileId=:securityProfile_profileId, "
      + "platformType=:platformType, status_statusId=:status_statusId, sequencerReference_sequencerReferenceId=:sequencerReference_sequencerReferenceId, "
      + "sequencingParameters_parametersId = :sequencingParameters_parametersId, lastModifier=:lastModifier " + "WHERE runId=:runId";

  public static final String RUN_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE runId=:runId";

  @Deprecated
  public static final String RUNS_SELECT_BY_RELATED_EXPERIMENT = RUNS_SELECT
      + " WHERE r.runId IN (SELECT rf.Run_runId FROM Run_SequencerPartitionContainer rf "
      + "LEFT JOIN SequencerPartitionContainer f ON f.containerId = rf.containers_containerId "
      + "LEFT JOIN SequencerPartitionContainer_Partition fl ON f.containerId = fl.container_containerId "
      + "LEFT JOIN _Partition l ON fl.partitions_partitionId = l.partitionId WHERE l.experiment_experimentId = ?)";

  public static final String RUNS_SELECT_BY_PLATFORM_ID = RUNS_SELECT
      + " WHERE r.sequencerReference_sequencerReferenceId IN (SELECT referenceId FROM SequencerReference WHERE platformId=?)";

  public static final String RUNS_SELECT_BY_STATUS_HEALTH = RUNS_SELECT
      + " WHERE r.status_statusId IN (SELECT statusId FROM Status WHERE health=?)";

  public static String RUNS_SELECT_BY_PROJECT_ID = RUNS_SELECT + " WHERE r.runId IN (SELECT rf.Run_runId FROM Project p "
      + "INNER JOIN Study st ON st.project_projectId = p.projectId LEFT JOIN Experiment ex ON st.studyId = ex.study_studyId "
      + "INNER JOIN Pool_Experiment pex ON ex.experimentId = pex.experiments_experimentId "
      + "LEFT JOIN Pool pool ON pool.poolId = pex.pool_poolId " + "LEFT JOIN _Partition c ON pool.poolId = c.pool_poolId "
      + "LEFT JOIN SequencerPartitionContainer_Partition fc ON c.partitionId = fc.partitions_partitionId "
      + "LEFT JOIN _Partition l ON pool.poolId = l.pool_poolId "
      + "LEFT JOIN SequencerPartitionContainer fa ON fc.container_containerId = fa.containerId "
      + "INNER JOIN Run_SequencerPartitionContainer rf ON fa.containerId = rf.containers_containerId WHERE p.projectId=?)";

  public static String RUNS_SELECT_BY_POOL_ID = RUNS_SELECT + " WHERE r.runId IN (SELECT rf.Run_runId FROM Pool pool "
      + "LEFT JOIN _Partition c ON pool.poolId = c.pool_poolId "
      + "LEFT JOIN SequencerPartitionContainer_Partition fc ON c.partitionId = fc.partitions_partitionId "
      + "LEFT JOIN SequencerPartitionContainer fa ON fc.container_containerId = fa.containerId "
      + "INNER JOIN Run_SequencerPartitionContainer rf ON fa.containerId = rf.containers_containerId WHERE pool.poolId=?)";

  public static String RUNS_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID = RUNS_SELECT
      + " WHERE r.runId IN (SELECT rf.Run_runId FROM Run_SequencerPartitionContainer rf WHERE rf.containers_containerId=?)";

  public static String LATEST_RUN_STARTED_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID = RUNS_SELECT
      + " INNER JOIN Status s ON r.status_statusId=s.statusId"
      + " WHERE r.runId IN (SELECT Run_runId FROM Run_SequencerPartitionContainer WHERE containers_containerId=?)"
      + " ORDER BY s.startDate DESC LIMIT 1";

  public static String LATEST_RUN_ID_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID = RUNS_SELECT
      + " WHERE r.runId IN (SELECT Run_runId FROM Run_SequencerPartitionContainer WHERE containers_containerId=?)"
      + " ORDER BY r.runId DESC LIMIT 1";

  protected static final Logger log = LoggerFactory.getLogger(SQLRunDAO.class);

  private static final BridgeCollectionUpdater<SequencerPartitionContainer<SequencerPoolPartition>> SEQ_PART_CONTAINER_WRITER = new BridgeCollectionUpdater<SequencerPartitionContainer<SequencerPoolPartition>>(
      "Run_SequencerPartitionContainer", "Run_runId", "containers_containerId") {

    @Override
    protected Object getId(SequencerPartitionContainer<SequencerPoolPartition> item) {
      return item.getId();
    }

  };

  private JdbcTemplate template;
  private Store<SecurityProfile> securityProfileDAO;
  private SequencerReferenceStore sequencerReferenceDAO;
  private RunQcStore runQcDAO;
  private SequencerPartitionContainerStore sequencerPartitionContainerDAO;
  private StatusStore statusDAO;
  private NoteStore noteDAO;
  private WatcherStore watcherDAO;
  private CascadeType cascadeType;
  private ChangeLogStore changeLogDAO;
  private SecurityStore securityDAO;
  @Autowired
  private SequencingParametersDao sequencingParametersDao;

  @Autowired
  private RunAlertManager runAlertManager;

  public void setRunAlertManager(RunAlertManager runAlertManager) {
    this.runAlertManager = runAlertManager;
  }

  @Autowired
  private MisoNamingScheme<Run> namingScheme;

  @Override
  public MisoNamingScheme<Run> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Run> namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Autowired
  private CacheManager cacheManager;

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setSequencerReferenceDAO(SequencerReferenceStore sequencerReferenceDAO) {
    this.sequencerReferenceDAO = sequencerReferenceDAO;
  }

  public void setRunQcDAO(RunQcStore runQcDAO) {
    this.runQcDAO = runQcDAO;
  }

  public void setSequencerPartitionContainerDAO(SequencerPartitionContainerStore sequencerPartitionContainerDAO) {
    this.sequencerPartitionContainerDAO = sequencerPartitionContainerDAO;
  }

  public void setStatusDAO(StatusStore statusDAO) {
    this.statusDAO = statusDAO;
  }

  public void setNoteDAO(NoteStore noteDAO) {
    this.noteDAO = noteDAO;
  }

  public void setWatcherDAO(WatcherStore watcherDAO) {
    this.watcherDAO = watcherDAO;
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

  @Override
  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  private void purgeCaches(Collection<Run> runs) {
    if (cacheManager == null) return;

    for (Run run : runs) {
      purgeListCache(run, true);
      DbUtils.updateCaches(cacheManager, run, Run.class);
    }
  }

  private void purgeListCache(Run run, boolean replace) {
    if (cacheManager == null) return;

    Cache cache = cacheManager.getCache("runListCache");
    DbUtils.updateListCache(cache, replace, run);
  }

  private void purgeListCache(Run run) {
    purgeListCache(run, true);
  }

  @Override
  @TriggersRemove(cacheName = { "runCache",
      "lazyRunCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public long save(Run run) throws IOException {
    Long securityProfileId = run.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) {// && this.cascadeType.equals(CascadeType.PERSIST))) {
      securityProfileId = securityProfileDAO.save(run.getSecurityProfile());
    }

    Long statusId = null;
    if (run.getStatus() != null) {
      Status s = run.getStatus();
      statusId = s.getId();
      // if no status has ever been saved to the database for this run
      // we want to create one, cascading or not
      if (statusId == null || (this.cascadeType != null && this.cascadeType.equals(CascadeType.PERSIST))) {
        if (s.getRunName() == null) {
          s.setRunName(run.getAlias());
        }

        if (s.getInstrumentName() == null && run.getSequencerReference() != null) {
          s.setInstrumentName(run.getSequencerReference().getName());
        }
      }
      statusId = statusDAO.save(s);
      run.setStatus(s);
    } else {
      log.warn("No status available to save for run: " + run.getAlias());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("accession", run.getAccession());
    params.addValue("alias", run.getAlias());
    params.addValue("description", run.getDescription());
    params.addValue("platformRunId", run.getPlatformRunId());
    params.addValue("pairedEnd", run.getPairedEnd());
    params.addValue("cycles", run.getCycles());
    params.addValue("filePath", run.getFilePath());
    params.addValue("platformType", run.getPlatformType().getKey());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("status_statusId", statusId);
    params.addValue("sequencerReference_sequencerReferenceId", run.getSequencerReference().getId());
    params.addValue("lastModifier", run.getLastModifier().getUserId());
    params.addValue("sequencingParameters_parametersId",
        run.getSequencingParameters() == null ? null : run.getSequencingParameters().getId());

    if (run.getId() == AbstractRun.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("runId");
      try {
        run.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", run);
        run.setName(name);

        if (namingScheme.validateField("name", run.getName())) {
          params.addValue("name", name);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != run.getId()) {
            log.error("Expected Run ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(RUN_DELETE, new MapSqlParameterSource().addValue("runId", newId.longValue()));
            throw new IOException("Something bad happened. Expected Run ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save Run - invalid field:" + run.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Run - issue with naming scheme", e);
      }
    } else {
      try {
        if (namingScheme.validateField("name", run.getName())) {
          params.addValue("runId", run.getId());
          params.addValue("name", run.getName());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(RUN_UPDATE, params);
        } else {
          throw new IOException("Cannot save Run - invalid field:" + run.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Run - issue with naming scheme", e);
      }
    }

    if (this.cascadeType != null) {
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        for (SequencerPartitionContainer<SequencerPoolPartition> container : run.getSequencerPartitionContainers()) {
          container.setId(sequencerPartitionContainerDAO.save(container));
        }
        SEQ_PART_CONTAINER_WRITER.saveAll(template, run.getId(), run.getSequencerPartitionContainers());
      }

      if (!run.getNotes().isEmpty()) {
        for (Note n : run.getNotes()) {
          noteDAO.saveRunNote(run, n);
        }
      }

      // if this is saved by a user, and not automatically saved by the notification system
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      watcherDAO.removeWatchedEntityByUser(run, user);

      for (User u : run.getWatchers()) {
        watcherDAO.saveWatchedEntityUser(run, u);
      }

      purgeListCache(run);
    }

    return run.getId();
  }

  @Override
  public synchronized int[] saveAll(Collection<Run> runs) throws IOException {
    log.debug(">>> Entering saveAll with " + runs.size() + " runs");
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    List<SqlParameterSource> batch = new ArrayList<>();
    long autoIncrement = DbUtils.getAutoIncrement(template, TABLE_NAME);

    for (Run run : runs) {
      Long securityProfileId = run.getSecurityProfile().getProfileId();
      if (securityProfileId == null || (this.cascadeType != null)) {// && this.cascadeType.equals(CascadeType.PERSIST))) {
        securityProfileId = securityProfileDAO.save(run.getSecurityProfile());
      }

      Long statusId = null;
      if (run.getStatus() != null) {
        Status s = run.getStatus();
        statusId = s.getId();
        // if no status has ever been saved to the database for this run
        // we want to create one, cascading or not
        if (statusId == StatusImpl.UNSAVED_ID || (this.cascadeType != null && this.cascadeType.equals(CascadeType.PERSIST))) {
          if (s.getRunName() == null) {
            s.setRunName(run.getAlias());
          }

          if (s.getInstrumentName() == null && run.getSequencerReference() != null) {
            s.setInstrumentName(run.getSequencerReference().getName());
          }
        }
        statusId = statusDAO.save(s);
        run.setStatus(s);
      } else {
        log.warn("No status available to save for run: " + run.getAlias());
      }

      try {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("accession", run.getAccession());
        params.addValue("alias", run.getAlias());
        params.addValue("description", run.getDescription());
        params.addValue("platformRunId", run.getPlatformRunId());
        params.addValue("pairedEnd", run.getPairedEnd());
        params.addValue("cycles", run.getCycles());
        params.addValue("filePath", run.getFilePath());
        params.addValue("platformType", run.getPlatformType().getKey());
        params.addValue("securityProfile_profileId", securityProfileId);
        params.addValue("status_statusId", statusId);
        params.addValue("sequencerReference_sequencerReferenceId", run.getSequencerReference().getId());
        params.addValue("lastModifier", run.getLastModifier().getUserId());
        params.addValue("sequencingParameters_parametersId",
            run.getSequencingParameters() == null ? null : run.getSequencingParameters().getId());

        if (run.getId() == AbstractRun.UNSAVED_ID) {
          SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("runId");
          try {
            run.setId(autoIncrement);

            String name = namingScheme.generateNameFor("name", run);
            run.setName(name);

            if (namingScheme.validateField("name", run.getName())) {
              params.addValue("name", name);

              Number newId = insert.executeAndReturnKey(params);
              if (newId.longValue() != run.getId()) {
                log.error("Expected Run ID doesn't match returned value from database insert: rolling back...");
                new NamedParameterJdbcTemplate(template).update(RUN_DELETE,
                    new MapSqlParameterSource().addValue("runId", newId.longValue()));
                throw new IOException("Something bad happened. Expected Run ID doesn't match returned value from DB insert");
              }
              autoIncrement = newId.longValue() + 1;
              log.debug(run.getName() + ":: Inserted as ID " + run.getId());
            } else {
              throw new IOException("Cannot save Run - invalid field:" + run.toString());
            }
          } catch (MisoNamingException e) {
            throw new IOException("Cannot save Run - issue with naming scheme", e);
          }
        } else {
          try {
            if (namingScheme.validateField("name", run.getName())) {
              params.addValue("runId", run.getId());
              params.addValue("name", run.getName());
              log.debug(run.getName() + ":: Updating as ID " + run.getId());
              batch.add(params);
            } else {
              throw new IOException("Cannot save Run - invalid field:" + run.toString());
            }
          } catch (MisoNamingException e) {
            throw new IOException("Cannot save Run - issue with naming scheme", e);
          }
        }

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            for (SequencerPartitionContainer<SequencerPoolPartition> l : run.getSequencerPartitionContainers()) {
              l.setSecurityProfile(run.getSecurityProfile());
              if (l.getPlatform() == null) {
                l.setPlatform(run.getSequencerReference().getPlatform());
              }
              l.setId(sequencerPartitionContainerDAO.save(l));
            }
            SEQ_PART_CONTAINER_WRITER.saveAll(template, run.getId(), run.getSequencerPartitionContainers());
          }

          if (!run.getNotes().isEmpty()) {
            for (Note n : run.getNotes()) {
              noteDAO.saveRunNote(run, n);
            }
          }

          // if this is saved by a user, and not automatically saved by the notification system
          User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
          watcherDAO.removeWatchedEntityByUser(run, user);

          for (User u : run.getWatchers()) {
            watcherDAO.saveWatchedEntityUser(run, u);
          }
        }
      } catch (IOException e) {
        log.error("Cannot batch save run: " + run.getName(), e);
      }
    }

    int[] rows = namedTemplate.batchUpdate(RUN_UPDATE, batch.toArray(new SqlParameterSource[batch.size()]));

    // flush caches
    purgeCaches(runs);

    log.debug("<<< Exiting saveAll");
    return rows;
  }

  @Override
  @Cacheable(cacheName = "runListCache")
  public List<Run> listAll() {
    return template.query(RUNS_SELECT, new RunMapper(true));
  }

  @Override
  public List<Run> listAllWithLimit(long limit) throws IOException {
    return template.query(RUNS_SELECT_LIMIT, new Object[] { limit }, new RunMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<Run> listBySearch(String query) {
    String mySQLQuery = DbUtils.convertStringToSearchQuery(query);
    return template.query(RUNS_SELECT_BY_SEARCH, new Object[] { mySQLQuery, mySQLQuery, mySQLQuery }, new RunMapper(true));
  }

  @Override
  public List<Run> listByProjectId(long projectId) throws IOException {
    return template.query(RUNS_SELECT_BY_PROJECT_ID, new Object[] { projectId }, new RunMapper(true));
  }

  @Override
  public List<Run> listByPlatformId(long platformId) throws IOException {
    return template.query(RUNS_SELECT_BY_PLATFORM_ID, new Object[] { platformId }, new RunMapper(true));
  }

  @Override
  public List<Run> listByStatus(String health) throws IOException {
    return template.query(RUNS_SELECT_BY_STATUS_HEALTH, new Object[] { health }, new RunMapper(true));
  }

  @Override
  public List<Run> listBySequencerId(long sequencerReferenceId) throws IOException {
    return template.query(RUN_SELECT_BY_SEQUENCER_ID, new Object[] { sequencerReferenceId }, new RunMapper(true));
  }

  @Override
  @Deprecated
  public List<Run> listByExperimentId(long experimentId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public List<Run> listByPoolId(long poolId) throws IOException {
    return template.query(RUNS_SELECT_BY_POOL_ID, new Object[] { poolId }, new RunMapper());
  }

  @Override
  public List<Run> listBySequencerPartitionContainerId(long containerId) throws IOException {
    return template.query(RUNS_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID, new Object[] { containerId }, new RunMapper(true));
  }

  @Override
  public Run getLatestStartDateRunBySequencerPartitionContainerId(long containerId) throws IOException {
    List<Run> eResults = template.query(LATEST_RUN_STARTED_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID, new Object[] { containerId },
        new RunMapper(true));
    Run r = eResults.size() > 0 ? eResults.get(0) : null;
    if (r == null) {
      r = getLatestRunIdRunBySequencerPartitionContainerId(containerId);
    }
    return r;
  }

  @Override
  public Run getLatestRunIdRunBySequencerPartitionContainerId(long containerId) throws IOException {
    List<Run> eResults = template.query(LATEST_RUN_ID_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID, new Object[] { containerId },
        new RunMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  @Cacheable(cacheName = "runCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public Run get(long runId) throws IOException {
    List<Run> eResults = template.query(RUN_SELECT_BY_ID, new Object[] { runId }, new RunMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Run getByAlias(String alias) throws IOException {
    List<Run> eResults = template.query(RUN_SELECT_BY_ALIAS, new Object[] { alias }, new RunMapper());
    if (eResults.size() > 1) throw new IOException("Found more than one run by this name");
    return eResults.size() == 1 ? eResults.get(0) : null;
  }

  @Override
  public Run lazyGet(long runId) throws IOException {
    List<Run> eResults = template.query(RUN_SELECT_BY_ID, new Object[] { runId }, new RunMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  @TriggersRemove(cacheName = { "runCache",
      "lazyRunCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public boolean remove(Run r) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (r.isDeletable()) {
      changeLogDAO.deleteAllById(TABLE_NAME, r.getId());
    }
    if (r.isDeletable() && (namedTemplate.update(RUN_DELETE, new MapSqlParameterSource().addValue("runId", r.getId())) == 1)) {
      purgeListCache(r, false);
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

  public class RunMapper extends CacheAwareRowMapper<Run> {
    public RunMapper() {
      super(Run.class);
    }

    public RunMapper(boolean lazy) {
      super(Run.class, lazy);
    }

    @Override
    public Run mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("runId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for Run " + id);
          return (Run) element.getObjectValue();
        }
      }

      PlatformType platformtype = PlatformType.get(rs.getString("platformType"));
      Run r = dataObjectFactory.getRunOfType(platformtype);
      r.setId(id);
      r.setAlias(rs.getString("alias"));
      r.setAccession(rs.getString("accession"));
      r.setName(rs.getString("name"));
      r.setDescription(rs.getString("description"));
      r.setPlatformRunId(rs.getInt("platformRunId"));
      if (rs.wasNull()) {
        r.setPlatformRunId(null);
      }
      r.setPairedEnd(rs.getBoolean("pairedEnd"));
      r.setCycles(rs.getInt("cycles"));
      if (rs.wasNull()) {
        r.setCycles(null);
      }
      r.setFilePath(rs.getString("filePath"));
      r.setPlatformType(PlatformType.get(rs.getString("platformType")));
      r.setLastUpdated(rs.getDate("lastUpdated"));

      try {
        r.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
        r.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        r.setStatus(statusDAO.get(rs.getLong("status_statusId")));
        r.setWatchers(new HashSet<>(watcherDAO.getWatchersByEntityName(r.getWatchableIdentifier())));
        if (r.getSecurityProfile() != null && r.getSecurityProfile().getOwner() != null) r.addWatcher(r.getSecurityProfile().getOwner());
        for (User u : watcherDAO.getWatchersByWatcherGroup("RunWatchers")) {
          r.addWatcher(u);
        }
        long parameterId = rs.getLong("sequencingParameters_parametersId");
        if (rs.wasNull()) {
          r.setSequencingParameters(null);
        } else {
          r.setSequencingParameters(getSequencingParametersDao().getSequencingParameters(parameterId));
        }

        if (!isLazy()) {
          r.setSequencerReference(sequencerReferenceDAO.get(rs.getLong("sequencerReference_sequencerReferenceId")));

          List<SequencerPartitionContainer<SequencerPoolPartition>> ss = sequencerPartitionContainerDAO
              .listAllSequencerPartitionContainersByRunId(id);
          r.setSequencerPartitionContainers(ss);

          for (RunQC qc : runQcDAO.listByRunId(id)) {
            r.addQc(qc);
          }

          r.setNotes(noteDAO.listByRun(id));
        }
        r.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
        r.getChangeLog().addAll(changeLogDAO.listAllById(TABLE_NAME, id));
      } catch (IOException e1) {
        log.error("run row mapper", e1);
      } catch (Exception e) {
        log.error("run row mapper", e);
      }

      if (runAlertManager != null) {
        runAlertManager.push(r);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), r));
      }

      return r;
    }
  }

  @Override
  public Map<String, Integer> getRunColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }

  @Override
  public long countRuns() throws IOException {
    return Long.valueOf(listAll().size());
  }

  @Override
  public List<Run> listBySearchOffsetAndNumResults(int offset, int limit, String search, String sortDir, String sortCol)
      throws IOException {
    if (isStringEmptyOrNull(search)) {
      return listByOffsetAndNumResults(offset, limit, sortDir, sortCol);
    } else {
      if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
      sortCol = updateSortCol(sortCol);
      if (!"asc".equals(sortDir.toLowerCase()) && !"desc".equals(sortDir.toLowerCase())) sortDir = "desc";

      String querystr = DbUtils.convertStringToSearchQuery(search);
      String query = RUNS_SELECT_BY_SEARCH + " ORDER BY " + sortCol + " " + sortDir + " LIMIT " + limit + " OFFSET " + offset;
      return template.query(query, new Object[] { querystr, querystr, querystr }, new RunMapper(true));
    }
  }

  @Override
  public List<Run> listByOffsetAndNumResults(int offset, int limit, String sortDir, String sortCol) throws IOException {
    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    sortCol = updateSortCol(sortCol);
    if (!"asc".equals(sortDir.toLowerCase()) && !"desc".equals(sortDir.toLowerCase())) sortDir = "desc";

    String query = RUNS_SELECT + " ORDER BY " + sortCol + " " + sortDir + " LIMIT " + limit + " OFFSET " + offset;
    return template.query(query, new RunMapper(true));
  }

  @Override
  public long countBySearch(String querystr) throws IOException {
    String mySQLQuery = DbUtils.convertStringToSearchQuery(querystr);
    return template.queryForLong(RUNS_COUNT_BY_SEARCH, new Object[] { mySQLQuery, mySQLQuery, mySQLQuery });
  }

  public String updateSortCol(String sortCol) {
    sortCol = sortCol.replaceAll("[^\\w]", "");
    if ("lastModified".equals(sortCol) || "lastUpdated".equals(sortCol)) {
      // because for some reason, the field on Run is called "lastUpdated", unlike everything else.
      sortCol = "rmod.lastUpdated";
    } else {
      switch (sortCol) {
      case "id":
        sortCol = "runId";
        break;
      case "name":
        break;
      case "alias":
        break;
      case "platformType":
        break;
      }
      sortCol = "r." + sortCol;
    }
    return sortCol;
  }

  public SequencingParametersDao getSequencingParametersDao() {
    return sequencingParametersDao;
  }

  public void setSequencingParametersDao(SequencingParametersDao sequencingParametersDao) {
    this.sequencingParametersDao = sequencingParametersDao;
  }

}
