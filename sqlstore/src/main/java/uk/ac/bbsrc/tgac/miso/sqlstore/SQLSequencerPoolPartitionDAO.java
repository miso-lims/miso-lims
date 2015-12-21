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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.PartitionStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.1.6
 */
public class SQLSequencerPoolPartitionDAO implements PartitionStore {
  private static final String TABLE_NAME = "_Partition";

  public static final String PARTITIONS_SELECT = "SELECT partitionId, partitionNumber, pool_poolId, securityProfile_profileId " + "FROM "
      + TABLE_NAME;

  public static final String PARTITION_SELECT_BY_ID = PARTITIONS_SELECT + " " + "WHERE partitionId = ?";

  public static final String PARTITION_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE partitionId=:partitionId";

  public static final String PARTITION_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET partitionNumber=:partitionNumber, pool_poolId=:pool_poolId, securityProfile_profileId=:securityProfile_profileId "
      + "WHERE partitionId=:partitionId";

  public static final String PARTITIONS_BY_RELATED_RUN = "SELECT l.partitionId, l.partitionNumber, l.pool_poolId, l.securityProfile_profileId "
      + "FROM " + TABLE_NAME + " l, Run_SequencerPartitionContainer rf, Run r " + "WHERE l.container_containerId=rf.containers_containerId "
      + "AND rf.Run_runId=r.runId " + "AND r.runId=?";

  public static final String PARTITIONS_BY_RELATED_SEQUENCER_PARTITION_CONTAINER = "SELECT l.partitionId, l.partitionNumber, l.pool_poolId, l.securityProfile_profileId "
      + "FROM " + TABLE_NAME + " l " + "INNER JOIN SequencerPartitionContainer_Partition fl ON l.partitionId = fl.partitions_partitionId "
      + "AND fl.container_containerId=?";

  public static final String PARTITIONS_BY_RELATED_POOL = PARTITIONS_SELECT + " WHERE pool_poolId = ?";

  public static String PARTITIONS_BY_RELATED_PROJECT = "SELECT l.* " + "FROM Project p, " + TABLE_NAME + " l "
      + "INNER JOIN Study st ON st.project_projectId = p.projectId " + "LEFT JOIN Experiment ex ON st.studyId = ex.study_studyId "
      + "INNER JOIN Pool_Experiment pex ON ex.experimentId = pex.experiments_experimentId "
      + "LEFT JOIN Pool pool ON pool.poolId = pex.pool_poolId " + "LEFT JOIN " + TABLE_NAME + " l ON pool.poolId = l.pool_poolId "
      + "LEFT JOIN SequencerPartitionContainer_Partition fl ON l.partitionId = fl.partitions_partitionId "
      + "LEFT JOIN SequencerPartitionContainer fa ON fl.container_containerId = fa.containerId " +

  "INNER JOIN Run_SequencerPartitionContainer rf ON fa.containerId = rf.containers_containerId "
      + "LEFT JOIN Run ra ON rf.Run_runId = ra.runId " + "WHERE p.projectId=?";
  // just changed this bit to see if it fixes the missing partition problem...
  public static final String PARTITIONS_BY_RELATED_SUBMISSION = "SELECT l.partitionId, l.partitionNumber, l.pool_poolId, l.securityProfile_profileId "
      + "FROM " + TABLE_NAME + " l, Submission_Partition_Dilution sl " + "WHERE l.partitionId=sl.partition_partitionId "
      + "AND sl.submission_submissionId=?";

  protected static final Logger log = LoggerFactory.getLogger(SQLSequencerPoolPartitionDAO.class);

  private JdbcTemplate template;
  private SequencerPartitionContainerStore sequencerPartitionContainerDAO;
  private PoolStore poolDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private CascadeType cascadeType;

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

  public void setPoolDAO(PoolStore poolDAO) {
    this.poolDAO = poolDAO;
  }

  public void setSequencerPartitionContainerDAO(SequencerPartitionContainerStore sequencerPartitionContainerDAO) {
    this.sequencerPartitionContainerDAO = sequencerPartitionContainerDAO;
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

  private void purgeListCache(SequencerPoolPartition s, boolean replace) {
    Cache cache = cacheManager.getCache("partitionListCache");
    DbUtils.updateListCache(cache, replace, s, SequencerPoolPartition.class);
  }

  private void purgeListCache(SequencerPoolPartition s) {
    purgeListCache(s, true);
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "sequencerPoolPartitionCache",
      "lazySequencerPoolPartitionCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long save(SequencerPoolPartition partition) throws IOException {
    Long securityProfileId = partition.getSecurityProfile().getProfileId();
    if (securityProfileId == null || this.cascadeType != null) { // && this.cascadeType.equals(CascadeType.PERSIST)) {
      securityProfileDAO.save(partition.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("partitionNumber", partition.getPartitionNumber());
    params.addValue("securityProfile_profileId", securityProfileId);

    if (partition.getPool() != null) {
      params.addValue("pool_poolId", partition.getPool().getId());

      // if this pool is marked as ready to run, and is now added to a partition, unmark it
      if (partition.getPool().getReadyToRun()) {
        partition.getPool().setReadyToRun(false);
      }
      poolDAO.save(partition.getPool());
    } else {
      params.addValue("pool_poolId", null);
    }

    if (partition.getId() == AbstractPartition.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("partitionId");
      Number newId = insert.executeAndReturnKey(params);
      partition.setId(newId.longValue());
    } else {
      params.addValue("partitionId", partition.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(PARTITION_UPDATE, params);
    }

    if (this.cascadeType != null) {
      purgeListCache(partition);
    }

    return partition.getId();
  }

  @Override
  @Cacheable(cacheName = "partitionListCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public List<SequencerPoolPartition> listAll() throws IOException {
    return template.query(PARTITIONS_SELECT, new PartitionMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  @Cacheable(cacheName = "sequencerPoolPartitionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public SequencerPoolPartition get(long partitionId) throws IOException {
    List eResults = template.query(PARTITION_SELECT_BY_ID, new Object[] { partitionId }, new PartitionMapper());
    return eResults.size() > 0 ? (SequencerPoolPartition) eResults.get(0) : null;
  }

  @Override
  public SequencerPoolPartition lazyGet(long partitionId) throws IOException {
    List eResults = template.query(PARTITION_SELECT_BY_ID, new Object[] { partitionId }, new PartitionMapper(true));
    return eResults.size() > 0 ? (SequencerPoolPartition) eResults.get(0) : null;
  }

  @Override
  public Collection<SequencerPoolPartition> listByRunId(long runId) throws IOException {
    return template.query(PARTITIONS_BY_RELATED_RUN, new Object[] { runId }, new PartitionMapper());
  }

  @Override
  public Collection<SequencerPoolPartition> listBySequencerPartitionContainerId(long sequencerPartitionContainerId) throws IOException {
    return template.query(PARTITIONS_BY_RELATED_SEQUENCER_PARTITION_CONTAINER, new Object[] { sequencerPartitionContainerId },
        new PartitionMapper(true));
  }

  @Override
  public List<SequencerPoolPartition> listByPoolId(long poolId) throws IOException {
    return template.query(PARTITIONS_BY_RELATED_POOL, new Object[] { poolId }, new PartitionMapper(true));
  }

  @Override
  public List<SequencerPoolPartition> listBySubmissionId(long submissionId) throws IOException {
    return template.query(PARTITIONS_BY_RELATED_SUBMISSION, new Object[] { submissionId }, new PartitionMapper());
  }

  public List<SequencerPoolPartition> listByProjectId(long projectId) throws IOException {
    return template.query(PARTITIONS_BY_RELATED_PROJECT, new Object[] { projectId }, new PartitionMapper(true));
  }

  public class PartitionMapper extends CacheAwareRowMapper<SequencerPoolPartition> {
    public PartitionMapper() {
      super(SequencerPoolPartition.class);
    }

    public PartitionMapper(boolean lazy) {
      super(SequencerPoolPartition.class, lazy);
    }

    @Override
    public SequencerPoolPartition mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("partitionId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for SequencerPoolPartition " + id);
          return (SequencerPoolPartition) element.getObjectValue();
        }
      }
      SequencerPoolPartition l = dataObjectFactory.getSequencerPoolPartition();
      l.setId(id);
      l.setPartitionNumber(rs.getInt("partitionNumber"));
      try {
        l.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        l.setPool(poolDAO.get(rs.getLong("pool_poolId")));
        if (!isLazy()) {
          l.setSequencerPartitionContainer(sequencerPartitionContainerDAO.getSequencerPartitionContainerByPartitionId(id));
        }
      } catch (IOException e1) {
        log.error("sequencer pool partition row mapper", e1);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), l));
      }

      return l;
    }
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "sequencerPoolPartitionCache",
      "lazySequencerPoolPartitionCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean remove(SequencerPoolPartition partition) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (partition.isDeletable()
        && (namedTemplate.update(PARTITION_DELETE, new MapSqlParameterSource().addValue("partitionId", partition.getId())) == 1)) {
      purgeListCache(partition, false);
      return true;
    }
    return false;
  }
}
