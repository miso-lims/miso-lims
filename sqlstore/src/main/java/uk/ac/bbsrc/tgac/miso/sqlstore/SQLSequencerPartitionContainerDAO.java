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
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.*;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import javax.persistence.CascadeType;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.1.6
 */
public class SQLSequencerPartitionContainerDAO implements SequencerPartitionContainerStore {
  private static final String TABLE_NAME = "SequencerPartitionContainer";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT =
      "SELECT containerId, platform, identificationBarcode, locationBarcode, validationBarcode, securityProfile_profileId FROM " + TABLE_NAME;

  public static final String SEQUENCER_PARTITION_CONTAINER_DELETE =
      "DELETE FROM " + TABLE_NAME + " WHERE containerId=:containerId";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT_BY_ID =
      SEQUENCER_PARTITION_CONTAINER_SELECT + " WHERE containerId=?";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT_BY_PARTITION_ID =
      "SELECT s.containerId, s.platform, s.identificationBarcode, s.locationBarcode, s.validationBarcode, s.securityProfile_profileId " +
      "FROM " + TABLE_NAME + " s, SequencerPartitionContainer_Partition sp " +
      "WHERE s.containerId=sp.container_containerId " +
      "AND sp.partitions_partitionId=?";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT_BY_RELATED_RUN =
      "SELECT DISTINCT f.containerId, f.platform, f.identificationBarcode, f.locationBarcode, f.validationBarcode, f.securityProfile_profileId " +
      "FROM " + TABLE_NAME + " f, Run_SequencerPartitionContainer rf " +
      "WHERE f.containerId=rf.containers_containerId " +
      "AND rf.run_runId=?";

  private static final String SEQUENCER_PARTITION_CONTAINER_SELECT_BY_IDENTIFICATION_BARCODE =
      SEQUENCER_PARTITION_CONTAINER_SELECT + " WHERE identificationBarcode=? ORDER BY containerId DESC";

  public static final String SEQUENCER_PARTITION_CONTAINER_PARTITION_DELETE_BY_SEQUENCER_PARTITION_CONTAINER_ID =
      "DELETE FROM SequencerPartitionContainer_Partition " +
      "WHERE container_containerId=:container_containerId";

  public static final String RUN_SEQUENCER_PARTITION_CONTAINER_DELETE_BY_SEQUENCER_PARTITION_CONTAINER_ID =
      "DELETE FROM Run_SequencerPartitionContainer " +
      "WHERE containers_containerId=:containers_containerId";

  public static final String SEQUENCER_PARTITION_CONTAINER_UPDATE =
      "UPDATE " + TABLE_NAME + " " +
      "SET platform=:platform, identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode, validationBarcode=:validationBarcode, securityProfile_profileId:=securityProfile_profileId " +
      "WHERE containerId=:containerId";

  protected static final Logger log = LoggerFactory.getLogger(SQLSequencerPartitionContainerDAO.class);

  private PartitionStore partitionDAO;
  private RunStore runDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private JdbcTemplate template;
  private CascadeType cascadeType;

  private PlatformStore platformDAO;

  @Autowired
  private MisoNamingScheme<SequencerPartitionContainer<SequencerPoolPartition>> namingScheme;

  @Override
  public MisoNamingScheme<SequencerPartitionContainer<SequencerPoolPartition>> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<SequencerPartitionContainer<SequencerPoolPartition>> namingScheme) {
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

  public void setPartitionDAO(PartitionStore partitionDAO) {
    this.partitionDAO = partitionDAO;
  }

  public void setRunDAO(RunStore runDAO) {
    this.runDAO = runDAO;
  }

  public void setPlatformDAO(PlatformStore platformDAO) {
    this.platformDAO = platformDAO;
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

  @Override
  @Cacheable(cacheName = "sequencerPartitionContainerCache",
             keyGenerator = @KeyGenerator(
                 name = "HashCodeCacheKeyGenerator",
                 properties = {
                     @Property(name = "includeMethod", value = "false"),
                     @Property(name = "includeParameterTypes", value = "false")
                 }
             )
  )
  public SequencerPartitionContainer<SequencerPoolPartition> get(long sequencerPartitionContainerId) throws IOException {
    List eResults = template.query(SEQUENCER_PARTITION_CONTAINER_SELECT_BY_ID, new Object[]{sequencerPartitionContainerId}, new SequencerPartitionContainerMapper());
    SequencerPartitionContainer<SequencerPoolPartition> f = eResults.size() > 0 ? (SequencerPartitionContainer<SequencerPoolPartition>) eResults.get(0) : null;
    fillInRun(f);
    return f;
  }

  public SequencerPartitionContainer<SequencerPoolPartition> lazyGet(long sequencerPartitionContainerId) throws IOException {
    List eResults = template.query(SEQUENCER_PARTITION_CONTAINER_SELECT_BY_ID, new Object[]{sequencerPartitionContainerId}, new SequencerPartitionContainerMapper(true));
    SequencerPartitionContainer<SequencerPoolPartition> f = eResults.size() > 0 ? (SequencerPartitionContainer<SequencerPoolPartition>) eResults.get(0) : null;
    //TODO - this seems to fuck everything up
    //fillInRun(f);
    return f;
  }

  @Override
  @Cacheable(cacheName = "containerListCache",
             keyGenerator = @KeyGenerator(
                 name = "HashCodeCacheKeyGenerator",
                 properties = {
                     @Property(name = "includeMethod", value = "false"),
                     @Property(name = "includeParameterTypes", value = "false")
                 }
             )
  )
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAll() throws IOException {
    Collection<SequencerPartitionContainer<SequencerPoolPartition>> lp = template.query(SEQUENCER_PARTITION_CONTAINER_SELECT, new SequencerPartitionContainerMapper(true));
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
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode) throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> lp = template.query(SEQUENCER_PARTITION_CONTAINER_SELECT_BY_IDENTIFICATION_BARCODE, new Object[]{barcode}, new SequencerPartitionContainerMapper(true));
    for (SequencerPartitionContainer<SequencerPoolPartition> f : lp) {
      fillInRun(f);
    }
    return lp;
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainersByRunId(long runId) throws IOException {
    List<SequencerPartitionContainer<SequencerPoolPartition>> lp = template.query(SEQUENCER_PARTITION_CONTAINER_SELECT_BY_RELATED_RUN, new Object[]{runId}, new SequencerPartitionContainerMapper(true));
    for (SequencerPartitionContainer<SequencerPoolPartition> f : lp) {
      fillInRun(f, runId);
    }
    return lp;
  }

  @Override
  public Collection<? extends SequencerPoolPartition> listPartitionsByContainerId(long sequencerPartitionContainerId) throws IOException {
    return partitionDAO.listBySequencerPartitionContainerId(sequencerPartitionContainerId);
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerByPartitionId(long partitionId) throws IOException {
    List eResults = template.query(SEQUENCER_PARTITION_CONTAINER_SELECT_BY_PARTITION_ID, new Object[]{partitionId}, new SequencerPartitionContainerMapper(true));
    SequencerPartitionContainer<SequencerPoolPartition> f = eResults.size() > 0 ? (SequencerPartitionContainer<SequencerPoolPartition>) eResults.get(0) : null;
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
    Cache cache = cacheManager.getCache("containerListCache");
    DbUtils.updateListCache(cache, replace, s, SequencerPartitionContainer.class);
  }

  private void purgeListCache(SequencerPartitionContainer<SequencerPoolPartition> s) {
    purgeListCache(s, true);
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = {"sequencerPartitionContainerCache", "lazySequencerPartitionContainerCache"},
                  keyGenerator = @KeyGenerator(
                      name = "HashCodeCacheKeyGenerator",
                      properties = {
                          @Property(name = "includeMethod", value = "false"),
                          @Property(name = "includeParameterTypes", value = "false")
                      }
                  )
  )
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

//    if (sequencerPartitionContainer.getPlatformType() != null) {
//      params.addValue("platformType", sequencerPartitionContainer.getPlatformType().getKey());
//    }

    if (sequencerPartitionContainer.getPlatform() != null) {
      params.addValue("platform", sequencerPartitionContainer.getPlatform().getPlatformId());
    }

    if (sequencerPartitionContainer.getId() == AbstractSequencerPartitionContainer.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
          .withTableName(TABLE_NAME)
          .usingGeneratedKeyColumns("containerId");
      //try {
      sequencerPartitionContainer.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        /*
        String name = namingScheme.generateNameFor("name", sequencerPartitionContainer);
        sequencerPartitionContainer.setName(name);

        if (namingScheme.validateField("name", sequencerPartitionContainer.getName())) {
          params.addValue("name", name);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != sequencerPartitionContainer.getId()) {
            log.error("Expected SequencerPartitionContainer ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(SEQUENCER_PARTITION_CONTAINER_DELETE, new MapSqlParameterSource().addValue("containerId", newId.longValue()));
            throw new IOException("Something bad happened. Expected SequencerPartitionContainer ID doesn't match returned value from DB insert");
          }
        }
        else {
          throw new IOException("Cannot save SequencerPartitionContainer - invalid field:" + sequencerPartitionContainer.toString());
        }

      }
      catch (MisoNamingException e) {
        throw new IOException("Cannot save SequencerPartitionContainer - issue with naming scheme", e);
      }
      */
      Number newId = insert.executeAndReturnKey(params);
      sequencerPartitionContainer.setId(newId.longValue());
    }
    else {
      /*
      try {
        if (namingScheme.validateField("name", sequencerPartitionContainer.getName())) {
          params.addValue("containerId", sequencerPartitionContainer.getId())
                .addValue("name", sequencerPartitionContainer.getName());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(SEQUENCER_PARTITION_CONTAINER_UPDATE, params);
        }
        else {
          throw new IOException("Cannot save SequencerPartitionContainer - invalid field:" + sequencerPartitionContainer.toString());
        }
      }
      catch (MisoNamingException e) {
        throw new IOException("Cannot save SequencerPartitionContainer - issue with naming scheme", e);
      }
      */

      params.addValue("containerId", sequencerPartitionContainer.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(SEQUENCER_PARTITION_CONTAINER_UPDATE, params);
    }

    //MapSqlParameterSource delparams = new MapSqlParameterSource();
    //delparams.addValue("container_containerId", sequencerPartitionContainer.getContainerId());
    //NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    //namedTemplate.update(SEQUENCER_PARTITION_CONTAINER_PARTITION_DELETE_BY_SEQUENCER_PARTITION_CONTAINER_ID, delparams);

    if (sequencerPartitionContainer.getPartitions() != null && !sequencerPartitionContainer.getPartitions().isEmpty()) {
      //log.info(sequencerPartitionContainer.getName()+":: Saving " + sequencerPartitionContainer.getPartitions().size() + " partitions...");

      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
          .withTableName("SequencerPartitionContainer_Partition");

      for (SequencerPoolPartition l : sequencerPartitionContainer.getPartitions()) {
        l.setSecurityProfile(sequencerPartitionContainer.getSecurityProfile());
        long partitionId = partitionDAO.save(l);

        //log.info(sequencerPartitionContainer.getName()+":: Saved partition " + l.getPartitionNumber() + " ("+partitionId+")");

        MapSqlParameterSource flParams = new MapSqlParameterSource();
        flParams.addValue("container_containerId", sequencerPartitionContainer.getId())
            .addValue("partitions_partitionId", partitionId);
        try {
          eInsert.execute(flParams);
        }
        catch (DuplicateKeyException dke) {
          log.debug("This Container/Partition combination already exists - not inserting: " + dke.getMessage());
        }
      }
    }

    if (this.cascadeType != null) {
      purgeListCache(sequencerPartitionContainer);
    }

    return sequencerPartitionContainer.getId();
  }

  /*
  public class SequencerPartitionContainerMapper<T extends SequencerPartitionContainer<SequencerPoolPartition>> extends CacheAwareRowMapper<T> {
    public SequencerPartitionContainerMapper() {
      super((Class<T>)((ParameterizedType)new TypeReference<T>(){}.getType()).getRawType());

    }

    public SequencerPartitionContainerMapper(boolean lazy) {
      super((Class<T>)((ParameterizedType)new TypeReference<T>(){}.getType()).getRawType(), lazy);

    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("containerId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for SequencerPartitionContainer " + id);
          return (T)element.getObjectValue();
        }
      }

      SequencerPartitionContainer<SequencerPoolPartition> s = null;
      try {
        s = dataObjectFactory.getSequencerPartitionContainer();
        s.setId(id);
        List<SequencerPoolPartition> partitions = new ArrayList<SequencerPoolPartition>(partitionDAO.listBySequencerPartitionContainerId(id));
        for (SequencerPoolPartition part : partitions) {
          part.setSequencerPartitionContainer(s);
        }
        s.setPartitions(partitions);

        if ((rs.getString("platformType") == null || "".equals(rs.getString("platformType"))) && s.getRun() != null) {
          s.setPlatformType(s.getRun().getPlatformType());
        }
        else {
          s.setPlatformType(PlatformType.get(rs.getString("platformType")));
        }

        s.setIdentificationBarcode(rs.getString("identificationBarcode"));
        s.setLocationBarcode(rs.getString("locationBarcode"));
        s.setValidationBarcode(rs.getString("validationBarcode"));
        s.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
      }
      catch (IOException e1) {
        e1.printStackTrace();
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id) ,s));
      }

      return (T)s;
    }
  }
  */

  public class SequencerPartitionContainerMapper extends CacheAwareRowMapper<SequencerPartitionContainer<SequencerPoolPartition>> {
    public SequencerPartitionContainerMapper() {
      super((Class<SequencerPartitionContainer<SequencerPoolPartition>>) ((ParameterizedType) new TypeReference<SequencerPartitionContainer<SequencerPoolPartition>>() {
      }.getType()).getRawType());
    }

    public SequencerPartitionContainerMapper(boolean lazy) {
      super((Class<SequencerPartitionContainer<SequencerPoolPartition>>) ((ParameterizedType) new TypeReference<SequencerPartitionContainer<SequencerPoolPartition>>() {
      }.getType()).getRawType(), lazy);
    }

    @Override
    public SequencerPartitionContainer<SequencerPoolPartition> mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("containerId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
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
        List<SequencerPoolPartition> partitions = new ArrayList<SequencerPoolPartition>(partitionDAO.listBySequencerPartitionContainerId(id));
        for (SequencerPoolPartition part : partitions) {
          part.setSequencerPartitionContainer(s);
        }
        s.setPartitions(partitions);

//        if ((rs.getString("platformType") == null || "".equals(rs.getString("platformType"))) && s.getRun() != null) {
//          s.setPlatformType(s.getRun().getPlatformType());
//        }
//        else {
//          s.setPlatformType(PlatformType.get(rs.getString("platformType")));
//        }

//        if () {
//          s.setPlatform(s.getRun().getSequencerReference().getPlatform());
//        }
//        else {
        if (rs.getLong("platform") != 0) {
          s.setPlatform(platformDAO.get(rs.getLong("platform")));
        }
//        }

        s.setIdentificationBarcode(rs.getString("identificationBarcode"));
        s.setLocationBarcode(rs.getString("locationBarcode"));
        s.setValidationBarcode(rs.getString("validationBarcode"));
        s.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
      }
      catch (IOException e1) {
        e1.printStackTrace();
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), s));
      }

      return s;
    }
  }

  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(
      cacheName = {"sequencerPartitionContainerCache", "lazySequencerPartitionContainerCache"},
      keyGenerator = @KeyGenerator(
          name = "HashCodeCacheKeyGenerator",
          properties = {
              @Property(name = "includeMethod", value = "false"),
              @Property(name = "includeParameterTypes", value = "false")
          }
      )
  )
  public boolean remove(SequencerPartitionContainer container) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (container.isDeletable()
        && (namedTemplate.update(SEQUENCER_PARTITION_CONTAINER_DELETE,
                                 new MapSqlParameterSource().addValue("containerId", container.getId())) == 1)
        ) {

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
    if ((namedTemplate.update(RUN_SEQUENCER_PARTITION_CONTAINER_DELETE_BY_SEQUENCER_PARTITION_CONTAINER_ID,
                              new MapSqlParameterSource().addValue("containers_containerId", container.getId())) == 1)) {
      return true;
    }
    return false;
  }

  public boolean removeContainerPartitionAssociations(SequencerPartitionContainer container) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if ((namedTemplate.update(SEQUENCER_PARTITION_CONTAINER_PARTITION_DELETE_BY_SEQUENCER_PARTITION_CONTAINER_ID,
                              new MapSqlParameterSource().addValue("container_containerId", container.getId())) == 1)) {
      return true;
    }
    return false;
  }
}
