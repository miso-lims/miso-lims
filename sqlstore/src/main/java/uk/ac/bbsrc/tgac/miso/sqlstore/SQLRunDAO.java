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

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.key.HashCodeCacheKeyGenerator;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.*;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;

import javax.persistence.CascadeType;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLRunDAO implements RunStore {
  private static final String TABLE_NAME = "Run";

  public static final String RUNS_SELECT =
          "SELECT runId, name, alias, description, accession, platformRunId, pairedEnd, cycles, filePath, securityProfile_profileId, platformType, status_statusId, sequencerReference_sequencerReferenceId " +
          "FROM "+TABLE_NAME;

  public static final String RUNS_SELECT_LIMIT =
          RUNS_SELECT + " ORDER BY runId DESC LIMIT ?";

  public static final String RUN_SELECT_BY_ID =
          RUNS_SELECT + " WHERE runId = ?";

  public static final String RUN_SELECT_BY_ALIAS =
          RUNS_SELECT + " WHERE alias = ?";

  public static final String RUNS_SELECT_BY_SEARCH =
          RUNS_SELECT + " WHERE " +
          "name LIKE ? OR " +
          "alias LIKE ? OR " +
          "description LIKE ? ";

  public static final String RUN_UPDATE =
          "UPDATE "+TABLE_NAME+" " +
          "SET name=:name, alias=:alias, description=:description, accession=:accession, platformRunId=:platformRunId, " +
          "pairedEnd=:pairedEnd, cycles=:cycles, filePath=:filePath, securityProfile_profileId=:securityProfile_profileId, " +
          "platformType=:platformType, status_statusId=:status_statusId, sequencerReference_sequencerReferenceId=:sequencerReference_sequencerReferenceId " +
          "WHERE runId=:runId";

  public static final String RUN_DELETE =
          "DELETE FROM "+TABLE_NAME+" WHERE runId=:runId";

  @Deprecated
  public static final String RUNS_SELECT_BY_RELATED_EXPERIMENT =
          "SELECT r.runId, r.name, r.alias, r.description, r.accession, r.platformRunId, r.pairedEnd, r.cycles, r.filePath, " +
          "r.securityProfile_profileId, r.platformType, r.status_statusId, r.sequencerReference_sequencerReferenceId " +
          "FROM "+TABLE_NAME+" r " +

          "INNER JOIN Run_SequencerPartitionContainer rf ON r.runId = rf.Run_runId" +
          "LEFT JOIN SequencerPartitionContainer f ON f.containerId = rf.containers_containerId " +
          "LEFT JOIN SequencerPartitionContainer_Partition fl ON f.containerId = fl.container_containerId " +
          "LEFT JOIN _Partition l ON fl.partitions_partitionId = l.partitionId " +

          "WHERE l.experiment_experimentId = ?";

  public static final String RUNS_SELECT_BY_PLATFORM_ID =
          "SELECT r.name, r.alias, r.description, r.accession, r.platformRunId, r.pairedEnd, r.cycles, r.filePath, r.securityProfile_profileId, r.platformType, r.status_statusId, r.sequencerReference_sequencerReferenceId " +
          "FROM "+TABLE_NAME+" r, Platform p " +
          "WHERE r.platform_platformId=p.platformId " +
          "AND r.platform_platformId=?";

  public static final String RUNS_SELECT_BY_STATUS_HEALTH =
          "SELECT r.name, r.alias, r.description, r.accession, r.platformRunId, r.pairedEnd, r.cycles, r.filePath, r.securityProfile_profileId, r.platformType, r.status_statusId, r.sequencerReference_sequencerReferenceId " +
          "FROM "+TABLE_NAME+" r, Status s " +
          "WHERE r.status_statusId=s.statusId " +
          "AND s.health=?";

  public static String RUNS_SELECT_BY_PROJECT_ID =
          "SELECT DISTINCT ra.* " +
          "FROM Project p " +
          "INNER JOIN Study st ON st.project_projectId = p.projectId " +
          "LEFT JOIN Experiment ex ON st.studyId = ex.study_studyId " +
          "INNER JOIN Pool_Experiment pex ON ex.experimentId = pex.experiments_experimentId " +
          "LEFT JOIN Pool pool ON pool.poolId = pex.pool_poolId " +
          "LEFT JOIN _Partition c ON pool.poolId = c.pool_poolId " +
          "LEFT JOIN SequencerPartitionContainer_Partition fc ON c.partitionId = fc.partitions_partitionId " +
          "LEFT JOIN _Partition l ON pool.poolId = l.pool_poolId " +
          "LEFT JOIN SequencerPartitionContainer fa ON fc.container_containerId = fa.containerId " +

          "INNER JOIN Run_SequencerPartitionContainer rf ON fa.containerId = rf.containers_containerId " +
          "LEFT JOIN "+TABLE_NAME+" ra ON rf.Run_runId = ra.runId " +
          "WHERE p.projectId=?";

  public static String RUNS_SELECT_BY_POOL_ID =
          "SELECT DISTINCT ra.* " +
          "FROM Pool pool " +
          "LEFT JOIN _Partition c ON pool.poolId = c.pool_poolId " +
          "LEFT JOIN SequencerPartitionContainer_Partition fc ON c.partitionId = fc.partitions_partitionId " +

          "LEFT JOIN SequencerPartitionContainer fa ON fc.container_containerId = fa.containerId " +

          "INNER JOIN Run_SequencerPartitionContainer rf ON fa.containerId = rf.containers_containerId " +
          "LEFT JOIN "+TABLE_NAME+" ra ON rf.Run_runId = ra.runId " +
          "WHERE pool.poolId=?";

  public static String RUNS_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID =
          "SELECT ra.* " +
          "FROM SequencerPartitionContainer container " +
          "INNER JOIN Run_SequencerPartitionContainer rf ON container.containerId = rf.containers_containerId " +
          "LEFT JOIN "+TABLE_NAME+" ra ON rf.Run_runId = ra.runId " +
          "WHERE container.containerId=?";

  public static String LATEST_RUN_STARTED_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID =
          "SELECT max(s.startDate), r.runId, r.name, r.alias, r.description, r.accession, r.platformRunId, r.pairedEnd, r.cycles, r.filePath, r.securityProfile_profileId, r.platformType, r.status_statusId, r.sequencerReference_sequencerReferenceId " +
          "FROM SequencerPartitionContainer container " +
          "INNER JOIN Run_SequencerPartitionContainer rf ON container.containerId = rf.containers_containerId " +
          "LEFT JOIN "+TABLE_NAME+" r ON rf.Run_runId = r.runId " +
          "INNER JOIN Status s ON r.status_statusId=s.statusId " +
          "WHERE container.containerId=?";

  public static String LATEST_RUN_ID_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID =
          "SELECT runId, name, alias, description, accession, platformRunId, pairedEnd, cycles, filePath, securityProfile_profileId, platformType, status_statusId, sequencerReference_sequencerReferenceId " +
          "FROM "+TABLE_NAME+" " +
          "INNER JOIN ( " +
          "    SELECT MAX(r.runId) as maxrun " +
          "    FROM SequencerPartitionContainer container " +
          "    INNER JOIN Run_SequencerPartitionContainer rf ON container.containerId = rf.containers_containerId " +
          "    LEFT JOIN "+TABLE_NAME+" r ON rf.Run_runId = r.runId " +
          "    WHERE container.containerId=? GROUP BY r.alias " +
          ") grouprun ON runId = maxrun";

  protected static final Logger log = LoggerFactory.getLogger(SQLRunDAO.class);

  private JdbcTemplate template;
  private Store<SecurityProfile> securityProfileDAO;
  private SequencerReferenceStore sequencerReferenceDAO;
  private RunQcStore runQcDAO;
  private SequencerPartitionContainerStore sequencerPartitionContainerDAO;
  private StatusStore statusDAO;
  private NoteStore noteDAO;
  private WatcherStore watcherDAO;
  private CascadeType cascadeType;

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

  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  private void purgeCaches(Collection<Run> runs) {
    Cache lcache = cacheManager.getCache("runListCache");
    if (lcache != null) {
      BlockingCache listCache = new BlockingCache(lcache);
      if (listCache.getKeys().size() > 0) {
        Object cachekey = listCache.getKeys().get(0);
        if (cachekey != null) {
          List<Run> cachedruns = (List<Run>)listCache.get(cachekey).getValue();
          for (Run run : runs) {
            cachedruns.remove(run);
            cachedruns.add(run);
          }
          listCache.put(new Element(cachekey, cachedruns));
        }
      }
    }

    Cache rcache = cacheManager.getCache("runCache");
    if (rcache != null) {
      BlockingCache cache = new BlockingCache(rcache);
      HashCodeCacheKeyGenerator keygen = new HashCodeCacheKeyGenerator();
      for (Run run : runs) {
        Long cachekey = keygen.generateKey(run);
        cache.remove(cachekey);
        cache.put(new Element(cachekey, run));
      }
    }
  }

  private void purgeListCache(Run run, boolean replace) {
    Cache cache = cacheManager.getCache("runListCache");
    DbUtils.updateListCache(cache, replace, run, Run.class);
  }

  private void purgeListCache(Run run) {
    purgeListCache(run, true);
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = {"runCache", "lazyRunCache"},
                  keyGenerator = @KeyGenerator(
                          name = "HashCodeCacheKeyGenerator",
                          properties = {
                                  @Property(name = "includeMethod", value = "false"),
                                  @Property(name = "includeParameterTypes", value = "false")
                          }
                  )
  )
  public long save(Run run) throws IOException {
    Long securityProfileId = run.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) {// && this.cascadeType.equals(CascadeType.PERSIST))) {
      securityProfileId = securityProfileDAO.save(run.getSecurityProfile());
    }

    Long statusId = null;
    if (run.getStatus() != null) {
      Status s = run.getStatus();
      statusId = s.getStatusId();
      //if no status has ever been saved to the database for this run
      //we want to create one, cascading or not
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
    }
    else {
      log.warn("No status available to save for run: " + run.getAlias());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("accession", run.getAccession())
            .addValue("alias", run.getAlias())
            .addValue("description", run.getDescription())
            .addValue("platformRunId", run.getPlatformRunId())
            .addValue("pairedEnd", run.getPairedEnd())
            .addValue("cycles", run.getCycles())
            .addValue("filePath", run.getFilePath())
            .addValue("platformType", run.getPlatformType().getKey())
            .addValue("securityProfile_profileId", securityProfileId)
            .addValue("status_statusId", statusId)
            .addValue("sequencerReference_sequencerReferenceId", run.getSequencerReference().getId());

    if (run.getId() == AbstractRun.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
              .withTableName(TABLE_NAME)
              .usingGeneratedKeyColumns("runId");
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
        }
        else {
          throw new IOException("Cannot save Run - invalid field:" + run.toString());
        }
      }
      catch (MisoNamingException e) {
        throw new IOException("Cannot save Run - issue with naming scheme", e);
      }
      /*
      String name = "RUN" + DbUtils.getAutoIncrement(template, TABLE_NAME);
      params.addValue("name", name);
      Number newId = insert.executeAndReturnKey(params);
      run.setRunId(newId.longValue());
      run.setName(name);
      */
    }
    else {
      try {
        if (namingScheme.validateField("name", run.getName())) {
          params.addValue("runId", run.getId())
                .addValue("name", run.getName());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(RUN_UPDATE, params);
        }
        else {
          throw new IOException("Cannot save Run - invalid field:" + run.toString());
        }
      }
      catch (MisoNamingException e) {
        throw new IOException("Cannot save Run - issue with naming scheme", e);
      }
      /*
      params.addValue("runId", run.getRunId())
            .addValue("name", run.getName());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(RUN_UPDATE, params);
      */
    }

    if (this.cascadeType != null) {
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        for (SequencerPartitionContainer<SequencerPoolPartition> l : ((RunImpl)run).getSequencerPartitionContainers()) {
          l.setSecurityProfile(run.getSecurityProfile());
          if (l.getPlatform().getPlatformType() == null) {
            l.getPlatform().setPlatformType(run.getPlatformType());
          }
          long containerId = sequencerPartitionContainerDAO.save(l);

          SimpleJdbcInsert fInsert = new SimpleJdbcInsert(template).withTableName("Run_SequencerPartitionContainer");
          MapSqlParameterSource fcParams = new MapSqlParameterSource();
          fcParams.addValue("Run_runId", run.getId())
                  .addValue("containers_containerId", containerId);

          try {
            fInsert.execute(fcParams);
          }
          catch(DuplicateKeyException dke) {
            log.warn("This Run/SequencerPartitionContainer combination already exists - not inserting: " + dke.getMessage());
          }
        }
      }

      if (!run.getNotes().isEmpty()) {
        for (Note n : run.getNotes()) {
          noteDAO.saveRunNote(run, n);
        }
      }

      //if this is saved by a user, and not automatically saved by the notification system
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      watcherDAO.removeWatchedEntityByUser(run, user);

      for (User u : run.getWatchers()) {
        watcherDAO.saveWatchedEntityUser(run, u);
      }

      purgeListCache(run);
    }

    return run.getId();
  }

  public synchronized int[] saveAll(Collection<Run> runs) throws IOException {
    log.debug(">>> Entering saveAll with " + runs.size() + " runs");
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    List<SqlParameterSource> batch = new ArrayList<SqlParameterSource>();
    long autoIncrement = DbUtils.getAutoIncrement(template, TABLE_NAME);

    for (Run run : runs) {
      Long securityProfileId = run.getSecurityProfile().getProfileId();
      if (securityProfileId == null || (this.cascadeType != null)) {// && this.cascadeType.equals(CascadeType.PERSIST))) {
        securityProfileId = securityProfileDAO.save(run.getSecurityProfile());
      }

      Long statusId = null;
      if (run.getStatus() != null) {
        Status s = run.getStatus();
        statusId = s.getStatusId();
        //if no status has ever been saved to the database for this run
        //we want to create one, cascading or not
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
      }
      else {
        log.warn("No status available to save for run: " + run.getAlias());
      }

      try {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("accession", run.getAccession())
                .addValue("alias", run.getAlias())
                .addValue("description", run.getDescription())
                .addValue("platformRunId", run.getPlatformRunId())
                .addValue("pairedEnd", run.getPairedEnd())
                .addValue("cycles", run.getCycles())
                .addValue("filePath", run.getFilePath())
                .addValue("platformType", run.getPlatformType().getKey())
                .addValue("securityProfile_profileId", securityProfileId)
                .addValue("status_statusId", statusId)
                .addValue("sequencerReference_sequencerReferenceId", run.getSequencerReference().getId());

        if (run.getId() == AbstractRun.UNSAVED_ID) {
          SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                  .withTableName(TABLE_NAME)
                  .usingGeneratedKeyColumns("runId");
          try {
            run.setId(autoIncrement);

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
              autoIncrement = newId.longValue() + 1;
              log.debug(run.getName() + ":: Inserted as ID " + run.getId());
            }
            else {
              throw new IOException("Cannot save Run - invalid field:" + run.toString());
            }
          }
          catch (MisoNamingException e) {
            throw new IOException("Cannot save Run - issue with naming scheme", e);
          }

          /*
          String name = "RUN" + autoIncrement;
          params.addValue("name", name);
          Number newId = insert.executeAndReturnKey(params);
          run.setRunId(newId.longValue());
          run.setName(name);
          autoIncrement = newId.longValue() + 1;
          log.debug(run.getName() + ":: Inserted as ID " + run.getRunId());
          */
        }
        else {
          try {
            if (namingScheme.validateField("name", run.getName())) {
              params.addValue("runId", run.getId())
                    .addValue("name", run.getName());
              log.debug(run.getName() + ":: Updating as ID " + run.getId());
              batch.add(params);
            }
            else {
              throw new IOException("Cannot save Run - invalid field:" + run.toString());
            }
          }
          catch (MisoNamingException e) {
            throw new IOException("Cannot save Run - issue with naming scheme", e);
          }
          /*
          params.addValue("runId", run.getRunId())
                .addValue("name", run.getName());
          log.debug(run.getName() + ":: Updating as ID " + run.getRunId());
          batch.add(params);
          */
        }

        if (this.cascadeType != null) {
          if (this.cascadeType.equals(CascadeType.PERSIST)) {
            for (SequencerPartitionContainer<SequencerPoolPartition> l : ((RunImpl)run).getSequencerPartitionContainers()) {
              l.setSecurityProfile(run.getSecurityProfile());
              //if (l.getPlatformType() == null) {
//                l.setPlatformType(run.getPlatformType());
//              }
              if (l.getPlatform() == null) {
                l.setPlatform(run.getSequencerReference().getPlatform());
              }
              long containerId = sequencerPartitionContainerDAO.save(l);

              SimpleJdbcInsert fInsert = new SimpleJdbcInsert(template).withTableName("Run_SequencerPartitionContainer");
              MapSqlParameterSource fcParams = new MapSqlParameterSource();
              fcParams.addValue("Run_runId", run.getId())
                      .addValue("containers_containerId", containerId);

              try {
                fInsert.execute(fcParams);
              }
              catch(DuplicateKeyException dke) {
                log.debug("This Run/SequencerPartitionContainer combination already exists - not inserting: " + dke.getMessage());
              }
            }
          }

          if (!run.getNotes().isEmpty()) {
            for (Note n : run.getNotes()) {
              noteDAO.saveRunNote(run, n);
            }
          }

          //if this is saved by a user, and not automatically saved by the notification system
          User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
          watcherDAO.removeWatchedEntityByUser(run, user);

          for (User u : run.getWatchers()) {
            watcherDAO.saveWatchedEntityUser(run, u);
          }
        }
      }
      catch (IOException e) {
        log.error("Cannot batch save run: " + run.getName());
        e.printStackTrace();
      }
    }

    int[] rows = namedTemplate.batchUpdate(RUN_UPDATE, batch.toArray(new SqlParameterSource[batch.size()]));

    //flush caches
    purgeCaches(runs);

    log.debug("<<< Exiting saveAll");
    return rows;
  }

  @Override
  @Cacheable(cacheName="runListCache")
  public List<Run> listAll() {
    return template.query(RUNS_SELECT, new RunMapper(true));
  }

  public List<Run> listAllWithLimit(long limit) throws IOException {
    return template.query(RUNS_SELECT_LIMIT, new Object[]{limit}, new RunMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM "+TABLE_NAME);
  }

  @Override
  public List<Run> listBySearch(String query) {
    String mySQLQuery = "%" + query.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
    return template.query(RUNS_SELECT_BY_SEARCH, new Object[]{mySQLQuery,mySQLQuery,mySQLQuery}, new RunMapper(true));
  }

  @Override
  public List<Run> listByProjectId(long projectId) throws IOException {
    return template.query(RUNS_SELECT_BY_PROJECT_ID, new Object[]{projectId}, new RunMapper(true));
  }

  @Override
  public List<Run> listByPlatformId(long platformId) throws IOException {
    return template.query(RUNS_SELECT_BY_PLATFORM_ID, new Object[]{platformId}, new RunMapper(true));
  }

  @Override
  public List<Run> listByStatus(String health) throws IOException {
    return template.query(RUNS_SELECT_BY_STATUS_HEALTH, new Object[]{health}, new RunMapper(true));
  }

  @Deprecated
  public List<Run> listByExperimentId(long experimentId) throws IOException {
    //return template.query(RUNS_SELECT_BY_RELATED_EXPERIMENT, new Object[]{experimentId, experimentId}, new RunMapper());
    return Collections.emptyList();
  }

  @Override
  public List<Run> listByPoolId(long poolId) throws IOException {
    return template.query(RUNS_SELECT_BY_POOL_ID, new Object[]{poolId}, new RunMapper());
  }

  @Override
  public List<Run> listBySequencerPartitionContainerId(long containerId) throws IOException {
    return template.query(RUNS_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID, new Object[]{containerId}, new RunMapper(true));
  }

  public Run getLatestStartDateRunBySequencerPartitionContainerId(long containerId) throws IOException {
    List eResults = template.query(LATEST_RUN_STARTED_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID, new Object[]{containerId}, new RunMapper(true));
    Run r = eResults.size() > 0 ? (Run)eResults.get(0) : null;
    if (r == null) { r = getLatestRunIdRunBySequencerPartitionContainerId(containerId); }
    return r;
  }

  public Run getLatestRunIdRunBySequencerPartitionContainerId(long containerId) throws IOException {
    List eResults = template.query(LATEST_RUN_ID_SELECT_BY_SEQUENCER_PARTITION_CONTAINER_ID, new Object[]{containerId}, new RunMapper(true));
    return eResults.size() > 0 ? (Run)eResults.get(0) : null;
  }

  @Override
  @Cacheable(cacheName = "runCache",
                  keyGenerator = @KeyGenerator(
                          name = "HashCodeCacheKeyGenerator",
                          properties = {
                                  @Property(name = "includeMethod", value = "false"),
                                  @Property(name = "includeParameterTypes", value = "false")
                          }
                  )
  )
  public Run get(long runId) throws IOException {
    List eResults = template.query(RUN_SELECT_BY_ID, new Object[]{runId}, new RunMapper());
    return eResults.size() > 0 ? (Run) eResults.get(0) : null;
  }

  @Override
  public Run getByAlias(String alias) throws IOException {
    List eResults = template.query(RUN_SELECT_BY_ALIAS, new Object[]{alias}, new RunMapper());
    return eResults.size() > 0 ? (Run) eResults.get(0) : null;
  }

  @Override
  public Run lazyGet(long runId) throws IOException {
    List eResults = template.query(RUN_SELECT_BY_ID, new Object[]{runId}, new RunMapper(true));
    return eResults.size() > 0 ? (Run) eResults.get(0) : null;
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(
          cacheName = {"runCache", "lazyRunCache"},
          keyGenerator = @KeyGenerator (
              name = "HashCodeCacheKeyGenerator",
              properties = {
                      @Property(name="includeMethod", value="false"),
                      @Property(name="includeParameterTypes", value="false")
              }
          )
  )
  public boolean remove(Run r) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (r.isDeletable() &&
           (namedTemplate.update(RUN_DELETE,
                            new MapSqlParameterSource().addValue("runId", r.getId())) == 1)) {
      purgeListCache(r, false);
      return true;
    }
    return false;
  }

  public class RunMapper extends CacheAwareRowMapper<Run> {
    public RunMapper() {
      super(Run.class);
    }

    public RunMapper(boolean lazy) {
      super(Run.class, lazy);
    }

    public Run mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("runId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for Run " + id);
          return (Run)element.getObjectValue();
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
      r.setPairedEnd(rs.getBoolean("pairedEnd"));
      r.setCycles(rs.getInt("cycles"));
      r.setFilePath(rs.getString("filePath"));
      r.setPlatformType(PlatformType.get(rs.getString("platformType")));

      try {
        r.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        r.setStatus(statusDAO.get(rs.getLong("status_statusId")));
        r.setSequencerReference(sequencerReferenceDAO.get(rs.getLong("sequencerReference_sequencerReferenceId")));
        r.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(r.getWatchableIdentifier())));
        if (r.getSecurityProfile() != null &&
            r.getSecurityProfile().getOwner() != null)
          r.addWatcher(r.getSecurityProfile().getOwner());
        for (User u : watcherDAO.getWatchersByWatcherGroup("RunWatchers")) {
          r.addWatcher(u);
        }

        if (!isLazy()) {
          List<SequencerPartitionContainer<SequencerPoolPartition>> ss =
              sequencerPartitionContainerDAO.listAllSequencerPartitionContainersByRunId(id);
          ((RunImpl)r).setSequencerPartitionContainers(ss);

          for (RunQC qc : runQcDAO.listByRunId(id)) {
            r.addQc(qc);
          }

          r.setNotes(noteDAO.listByRun(id));
        }
      }
      catch (IOException e1) {
        e1.printStackTrace();
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      if (runAlertManager != null) {
        runAlertManager.push(r);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id) ,r));
      }

      return r;
    }
  }
}
