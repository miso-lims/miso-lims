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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

import javax.persistence.CascadeType;

import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.PartitionStore;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
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
@Transactional(rollbackFor = Exception.class)
public class SQLSequencerPartitionContainerDAO implements SequencerPartitionContainerStore {
  private static final String TABLE_NAME = "SequencerPartitionContainer";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT = "SELECT c.containerId, c.platform, c.identificationBarcode, c.locationBarcode, c.validationBarcode, c.securityProfile_profileId, c.lastModifier , cmod.lastModified FROM "
      + TABLE_NAME + " c "
      + "LEFT JOIN (SELECT containerId, MAX(changeTime) AS lastModified FROM SequencerPartitionContainerChangeLog GROUP BY containerId) cmod ON c.containerId = cmod.containerId";

  private static final String SEQUENCER_PARTITION_CONTAINER_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE containerId=:containerId";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT_BY_SEARCH = SEQUENCER_PARTITION_CONTAINER_SELECT
      + " WHERE c.platform LIKE ? OR c.identificationBarcode LIKE ?";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT_BY_ID = SEQUENCER_PARTITION_CONTAINER_SELECT + " WHERE c.containerId=?";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT_BY_PARTITION_ID = "SELECT c.containerId, c.platform, c.identificationBarcode, c.locationBarcode, c.validationBarcode, c.securityProfile_profileId, c.lastModifier, cmod.lastModified "
      + "FROM " + TABLE_NAME + " c "
      + "LEFT JOIN (SELECT containerId, MAX(changeTime) AS lastModified FROM SequencerPartitionContainerChangeLog GROUP BY containerId) cmod ON c.containerId = cmod.containerId "
      + "LEFT JOIN SequencerPartitionContainer_Partition sp ON c.containerId=sp.container_containerId "
      + "WHERE sp.partitions_partitionId=?";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT_BY_RELATED_RUN = "SELECT DISTINCT c.containerId, c.platform, c.identificationBarcode, c.locationBarcode, c.validationBarcode, c.securityProfile_profileId, c.lastModifier, cmod.lastModified "
      + "FROM " + TABLE_NAME + " c "
      + "LEFT JOIN (SELECT containerId, MAX(changeTime) AS lastModified FROM SequencerPartitionContainerChangeLog GROUP BY containerId) cmod ON c.containerId = cmod.containerId "
      + "LEFT JOIN Run_SequencerPartitionContainer rf ON c.containerId=rf.containers_containerId " + "WHERE rf.run_runId=?";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT_BY_IDENTIFICATION_BARCODE = SEQUENCER_PARTITION_CONTAINER_SELECT
      + " WHERE c.identificationBarcode=? ORDER BY containerId DESC";

  private static final String SEQUENCER_PARTITION_CONTAINER_PARTITION_DELETE_BY_SEQUENCER_PARTITION_CONTAINER_ID = "DELETE FROM SequencerPartitionContainer_Partition "
      + "WHERE container_containerId=:container_containerId";

  private static final String RUN_SEQUENCER_PARTITION_CONTAINER_DELETE_BY_SEQUENCER_PARTITION_CONTAINER_ID = "DELETE FROM Run_SequencerPartitionContainer "
      + "WHERE containers_containerId=:containers_containerId";

  private static final String SEQUENCER_PARTITION_CONTAINER_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET platform=:platform, identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode, validationBarcode=:validationBarcode, securityProfile_profileId=:securityProfile_profileId, lastModifier=:lastModifier "
      + "WHERE containerId=:containerId";

  protected static final Logger log = LoggerFactory.getLogger(SQLSequencerPartitionContainerDAO.class);

  private PartitionStore partitionDAO;
  private RunStore runDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private JdbcTemplate template;
  private CascadeType cascadeType;

  private PlatformStore platformDAO;
  private ChangeLogStore changeLogDAO;
  private SecurityStore securityDAO;

  @Autowired
  private MisoNamingScheme<SequencerPartitionContainer<SequencerPoolPartition>> namingScheme;

  @Override
  @CoverageIgnore
  public MisoNamingScheme<SequencerPartitionContainer<SequencerPoolPartition>> getNamingScheme() {
    return namingScheme;
  }

  @Override
  @CoverageIgnore
  public void setNamingScheme(MisoNamingScheme<SequencerPartitionContainer<SequencerPoolPartition>> namingScheme) {
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
  public void setPartitionDAO(PartitionStore partitionDAO) {
    this.partitionDAO = partitionDAO;
  }

  @CoverageIgnore
  public void setRunDAO(RunStore runDAO) {
    this.runDAO = runDAO;
  }

  @CoverageIgnore
  public void setPlatformDAO(PlatformStore platformDAO) {
    this.platformDAO = platformDAO;
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
  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  @CoverageIgnore
  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @CoverageIgnore
  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  @Override
  @Cacheable(cacheName = "sequencerPartitionContainerCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public SequencerPartitionContainer<SequencerPoolPartition> get(long sequencerPartitionContainerId) throws IOException {
    List eResults = template.query(
        SEQUENCER_PARTITION_CONTAINER_SELECT_BY_ID,
        new Object[] { sequencerPartitionContainerId },
        new SequencerPartitionContainerMapper());
    SequencerPartitionContainer<SequencerPoolPartition> f = eResults.size() > 0
        ? (SequencerPartitionContainer<SequencerPoolPartition>) eResults.get(0) : null;
    fillInRun(f);
    return f;
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> lazyGet(long sequencerPartitionContainerId) throws IOException {
    List eResults = template.query(
        SEQUENCER_PARTITION_CONTAINER_SELECT_BY_ID,
        new Object[] { sequencerPartitionContainerId },
        new SequencerPartitionContainerMapper(true));
    SequencerPartitionContainer<SequencerPoolPartition> f = eResults.size() > 0
        ? (SequencerPartitionContainer<SequencerPoolPartition>) eResults.get(0) : null;
    // TODO - this seems to fuck everything up
    // fillInRun(f);
    return f;
  }

  @Override
  @Cacheable(cacheName = "containerListCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAll() throws IOException {
    Collection<SequencerPartitionContainer<SequencerPoolPartition>> lp = template
        .query(SEQUENCER_PARTITION_CONTAINER_SELECT, new SequencerPartitionContainerMapper(true));
    for (SequencerPartitionContainer<SequencerPoolPartition> f : lp) {
      fillInRun(f);
    }
    return lp;
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> lp = template.query(
        SEQUENCER_PARTITION_CONTAINER_SELECT_BY_IDENTIFICATION_BARCODE,
        new Object[] { barcode },
        new SequencerPartitionContainerMapper(true));
    for (SequencerPartitionContainer<SequencerPoolPartition> f : lp) {
      fillInRun(f);
    }
    return lp;
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainersByRunId(long runId)
      throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> lp = template
        .query(SEQUENCER_PARTITION_CONTAINER_SELECT_BY_RELATED_RUN, new Object[] { runId }, new SequencerPartitionContainerMapper(true));
    for (SequencerPartitionContainer<SequencerPoolPartition> f : lp) {
      fillInRun(f, runId);
    }
    return lp;
  }

  @CoverageIgnore
  @Override
  public Collection<? extends SequencerPoolPartition> listPartitionsByContainerId(long sequencerPartitionContainerId) throws IOException {
    return partitionDAO.listBySequencerPartitionContainerId(sequencerPartitionContainerId);
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerByPartitionId(long partitionId)
      throws IOException {
    List eResults = template.query(
        SEQUENCER_PARTITION_CONTAINER_SELECT_BY_PARTITION_ID,
        new Object[] { partitionId },
        new SequencerPartitionContainerMapper(true));
    SequencerPartitionContainer<SequencerPoolPartition> f = eResults.size() > 0
        ? (SequencerPartitionContainer<SequencerPoolPartition>) eResults.get(0) : null;
    fillInRun(f);
    return f;
  }

  private void fillInRun(SequencerPartitionContainer<SequencerPoolPartition> container) throws IOException {
    if (container != null) {
      container.setRun(runDAO.getLatestRunIdRunBySequencerPartitionContainerId(container.getId()));
    }
  }

  private void fillInRun(SequencerPartitionContainer<SequencerPoolPartition> container, long runId) throws IOException {
    Run r = runDAO.lazyGet(runId);
    container.setRun(r);
  }

  private void purgeListCache(SequencerPartitionContainer<SequencerPoolPartition> s, boolean replace) {
    if (cacheManager != null) {
      Cache cache = cacheManager.getCache("containerListCache");
      DbUtils.updateListCache(cache, replace, s, SequencerPartitionContainer.class);
    }
  }

  private void purgeListCache(SequencerPartitionContainer<SequencerPoolPartition> s) {
    purgeListCache(s, true);
  }

  @Override
  @TriggersRemove(cacheName = { "sequencerPartitionContainerCache",
      "lazySequencerPartitionContainerCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public synchronized long save(SequencerPartitionContainer<SequencerPoolPartition> sequencerPartitionContainer) throws IOException {
    Long securityProfileId = sequencerPartitionContainer.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) { // && this.cascadeType.equals(CascadeType.PERSIST))) {
      securityProfileId = securityProfileDAO.save(sequencerPartitionContainer.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();

    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("identificationBarcode", sequencerPartitionContainer.getIdentificationBarcode());
    params.addValue("locationBarcode", sequencerPartitionContainer.getLocationBarcode());
    params.addValue("validationBarcode", sequencerPartitionContainer.getValidationBarcode());
    params.addValue("lastModifier", sequencerPartitionContainer.getLastModifier().getUserId());

    if (sequencerPartitionContainer.getPlatform() != null) {
      params.addValue("platform", sequencerPartitionContainer.getPlatform().getId());
    }

    if (sequencerPartitionContainer.getId() == AbstractSequencerPartitionContainer.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("containerId");
      sequencerPartitionContainer.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));
      Number newId = insert.executeAndReturnKey(params);
      sequencerPartitionContainer.setId(newId.longValue());
    } else {

      params.addValue("containerId", sequencerPartitionContainer.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(SEQUENCER_PARTITION_CONTAINER_UPDATE, params);
    }

    if (sequencerPartitionContainer.getPartitions() != null && !sequencerPartitionContainer.getPartitions().isEmpty()) {
      removeContainerPartitionAssociations(sequencerPartitionContainer);

      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template).withTableName("SequencerPartitionContainer_Partition");

      for (SequencerPoolPartition l : sequencerPartitionContainer.getPartitions()) {
        l.setSecurityProfile(sequencerPartitionContainer.getSecurityProfile());
        long partitionId = partitionDAO.save(l);

        MapSqlParameterSource flParams = new MapSqlParameterSource();
        flParams.addValue("container_containerId", sequencerPartitionContainer.getId()).addValue("partitions_partitionId", partitionId);
        eInsert.execute(flParams);
      }
    }

    if (this.cascadeType != null) {
      purgeListCache(sequencerPartitionContainer);
    }

    return sequencerPartitionContainer.getId();
  }

  public class SequencerPartitionContainerMapper extends CacheAwareRowMapper<SequencerPartitionContainer<SequencerPoolPartition>> {
    public SequencerPartitionContainerMapper() {
      super(
          (Class<SequencerPartitionContainer<SequencerPoolPartition>>) ((ParameterizedType) new TypeReference<SequencerPartitionContainer<SequencerPoolPartition>>() {
          }.getType()).getRawType());
    }

    public SequencerPartitionContainerMapper(boolean lazy) {
      super(
          (Class<SequencerPartitionContainer<SequencerPoolPartition>>) ((ParameterizedType) new TypeReference<SequencerPartitionContainer<SequencerPoolPartition>>() {
          }.getType()).getRawType(), lazy);
    }

    @Override
    @CoverageIgnore
    public SequencerPartitionContainer<SequencerPoolPartition> mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("containerId");

      if (isCacheEnabled() && cacheManager != null && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for SequencerPartitionContainer " + id);
          return (SequencerPartitionContainer<SequencerPoolPartition>) element.getObjectValue();
        }
      }

      SequencerPartitionContainer<SequencerPoolPartition> s = null;
      try {
        s = dataObjectFactory.getSequencerPartitionContainer();
        s.setId(id);
        List<SequencerPoolPartition> partitions = new ArrayList<SequencerPoolPartition>(
            partitionDAO.listBySequencerPartitionContainerId(id));
        for (SequencerPoolPartition part : partitions) {
          part.setSequencerPartitionContainer(s);
        }
        s.setPartitions(partitions);

        if (rs.getLong("platform") != 0) {
          s.setPlatform(platformDAO.get(rs.getLong("platform")));
        }

        s.setIdentificationBarcode(rs.getString("identificationBarcode"));
        s.setLocationBarcode(rs.getString("locationBarcode"));
        s.setValidationBarcode(rs.getString("validationBarcode"));
        s.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        s.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
        s.setLastModified(rs.getDate("lastModified"));
        s.getChangeLog().addAll(changeLogDAO.listAllById(TABLE_NAME, id));
      } catch (IOException e1) {
        log.error("sequencing container row mapper", e1);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), s));
      }

      return s;
    }
  }

  @Override
  @TriggersRemove(cacheName = { "sequencerPartitionContainerCache",
      "lazySequencerPartitionContainerCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public boolean remove(SequencerPartitionContainer container) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (container.isDeletable()) {
      changeLogDAO.deleteAllById(TABLE_NAME, container.getId());
    }
    if (container.isDeletable() && (namedTemplate
        .update(SEQUENCER_PARTITION_CONTAINER_DELETE, new MapSqlParameterSource().addValue("containerId", container.getId())) == 1)) {

      if (!container.getPartitions().isEmpty()) {
        for (SequencerPoolPartition partition : (Iterable<SequencerPoolPartition>) container.getPartitions()) {
          partitionDAO.remove(partition);
        }
      }

      removeContainerPartitionAssociations(container);
      removeContainerFromRun(container);

      purgeListCache(container, false);
      return true;
    }
    return false;
  }

  public boolean removeContainerFromRun(SequencerPartitionContainer container) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if ((namedTemplate.update(
        RUN_SEQUENCER_PARTITION_CONTAINER_DELETE_BY_SEQUENCER_PARTITION_CONTAINER_ID,
        new MapSqlParameterSource().addValue("containers_containerId", container.getId())) == 1)) {
      return true;
    }
    return false;
  }

  public boolean removeContainerPartitionAssociations(SequencerPartitionContainer container) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if ((namedTemplate.update(
        SEQUENCER_PARTITION_CONTAINER_PARTITION_DELETE_BY_SEQUENCER_PARTITION_CONTAINER_ID,
        new MapSqlParameterSource().addValue("container_containerId", container.getId())) == 1)) {
      return true;
    }
    return false;
  }

  @Override
  public long countContainers() throws IOException {
    return Long.valueOf(count());
  }

  @Override
  public long countBySearch(String querystr) throws IOException {
    querystr = "%" + querystr.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
    return template.query(
        SEQUENCER_PARTITION_CONTAINER_SELECT_BY_SEARCH,
        new Object[] { querystr, querystr },
        new SequencerPartitionContainerMapper(true)).size();
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listBySearchOffsetAndNumResults(int offset, int limit, String querystr,
      String sortDir, String sortCol) throws IOException {
    if (isStringEmptyOrNull(querystr)) {
      return listByOffsetAndNumResults(offset, limit, sortDir, sortCol);
    } else {
      if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
      sortCol = updateSortCol(sortCol);
      if (!"asc".equals(sortDir.toLowerCase()) && !"desc".equals(sortDir.toLowerCase())) sortDir = "desc";

      querystr = "%" + querystr.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
      String query = SEQUENCER_PARTITION_CONTAINER_SELECT_BY_SEARCH + " ORDER BY " + sortCol + " " + sortDir + " LIMIT " + limit
          + " OFFSET " + offset;
      List<SequencerPartitionContainer<SequencerPoolPartition>> containers = template
          .query(query, new Object[] { querystr, querystr }, new SequencerPartitionContainerMapper());
      for (SequencerPartitionContainer<SequencerPoolPartition> container : containers) {
        fillInRun(container);
      }
      return containers;
    }
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listByOffsetAndNumResults(int offset, int limit, String sortDir,
      String sortCol) throws IOException {
    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    sortCol = updateSortCol(sortCol);
    if (!"asc".equals(sortDir.toLowerCase()) && !"desc".equals(sortDir.toLowerCase())) sortDir = "desc";

    String query = SEQUENCER_PARTITION_CONTAINER_SELECT + " ORDER BY " + sortCol + " " + sortDir + " LIMIT " + limit + " OFFSET " + offset;
    List<SequencerPartitionContainer<SequencerPoolPartition>> containers = template.query(query, new SequencerPartitionContainerMapper());
    for (SequencerPartitionContainer<SequencerPoolPartition> container : containers) {
      fillInRun(container);
    }
    return containers;
  }

  public String updateSortCol(String sortCol) {
    sortCol = sortCol.replaceAll("[^\\w]", "");
    if ("lastModified".equals(sortCol)) {
      sortCol = "cmod." + sortCol;
    } else if ("id".equals(sortCol)) {
      sortCol = "c.containerId";
    } else {
      sortCol = "c." + sortCol;
    }
    return sortCol;
  }

  @CoverageIgnore
  public ChangeLogStore getChangeLogDAO() {
    return changeLogDAO;
  }

  @CoverageIgnore
  public void setChangeLogDAO(ChangeLogStore changeLogDAO) {
    this.changeLogDAO = changeLogDAO;
  }

  @CoverageIgnore
  public SecurityStore getSecurityDAO() {
    return securityDAO;
  }

  @CoverageIgnore
  public void setSecurityDAO(SecurityStore securityDAO) {
    this.securityDAO = securityDAO;
  }

}
