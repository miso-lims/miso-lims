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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;

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

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractQC;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.3
 */
public class SQLRunQCDAO implements RunQcStore {
  private static final String TABLE_NAME = "RunQC";

  public static final String RUN_QC = "SELECT qcId, run_runId, qcUserName, qcDate, qcMethod, information, doNotProcess " + "FROM "
      + TABLE_NAME;

  public static final String RUN_QC_SELECT_BY_ID = RUN_QC + " WHERE qcId=?";

  public static final String RUN_QC_SELECT_BY_RUN_ID = RUN_QC + " WHERE run_runId=? " + "ORDER BY qcDate ASC";

  public static final String RUN_QC_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET run_runId=:run_runId, qcUserName=:qcUserName, qcDate=:qcDate, qcMethod=:qcMethod, information=:information, doNotProcess=:doNotProcess "
      + "WHERE qcId=:qcId";

  public static final String RUN_QC_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE qcId=:qcId";

  public static final String RUN_QC_TYPE_SELECT = "SELECT qcTypeId, name, description, qcTarget, units "
      + "FROM QCType WHERE qcTarget = 'Run'";

  public static final String RUN_QC_TYPE_SELECT_BY_ID = RUN_QC_TYPE_SELECT + " AND qcTypeId = ?";

  public static final String RUN_QC_TYPE_SELECT_BY_NAME = RUN_QC_TYPE_SELECT + " AND name = ?";

  public static final String PARTITIONS_BY_RUN_QC = "SELECT rqc.runQc_runQcId, rqc.containers_containerId, rqc.partitionNumber "
      + "FROM RunQC_Partition rqc " + "WHERE rqc.runQc_runQcId = ?";

  protected static final Logger log = LoggerFactory.getLogger(SQLRunQCDAO.class);

  private JdbcTemplate template;
  private SequencerPartitionContainerStore sequencerPartitionContainerDAO;
  private RunStore runDAO;
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

  public void setSequencerPartitionContainerDAO(SequencerPartitionContainerStore sequencerPartitionContainerDAO) {
    this.sequencerPartitionContainerDAO = sequencerPartitionContainerDAO;
  }

  public void setRunDAO(RunStore runDAO) {
    this.runDAO = runDAO;
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
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  public long save(RunQC runQC) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("run_runId", runQC.getRun().getId());
    params.addValue("qcUserName", runQC.getQcCreator());
    params.addValue("qcDate", runQC.getQcDate());
    params.addValue("qcMethod", runQC.getQcType().getQcTypeId());
    params.addValue("information", LimsUtils.findHyperlinks(runQC.getInformation()));
    params.addValue("doNotProcess", runQC.getDoNotProcess());

    if (runQC.getId() == AbstractQC.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("qcId");
      Number newId = insert.executeAndReturnKey(params);
      runQC.setId(newId.longValue());
    } else {
      params.addValue("qcId", runQC.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(RUN_QC_UPDATE, params);
    }

    for (Partition p : runQC.getPartitionSelections()) {
      SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName("RunQC_Partition");

      MapSqlParameterSource poParams = new MapSqlParameterSource();
      poParams.addValue("runQc_runQcId", runQC.getId());
      poParams.addValue("containers_containerId", p.getSequencerPartitionContainer().getId());
      poParams.addValue("partitionNumber", p.getPartitionNumber());
      try {
        pInsert.execute(poParams);
      } catch (DuplicateKeyException se) {
        // ignore
      }
    }

    if (this.cascadeType != null) {
      Run r = runQC.getRun();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (r != null) runDAO.save(r);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (r != null) {
          DbUtils.updateCaches(cacheManager, r, Run.class);
        }
      } else if (this.cascadeType.equals(CascadeType.ALL)) {
        if (r != null) {
          runDAO.save(r);
          DbUtils.updateCaches(cacheManager, r, Run.class);
        }
      }
    }
    return runQC.getId();
  }

  @Override
  public RunQC get(long qcId) throws IOException {
    List eResults = template.query(RUN_QC_SELECT_BY_ID, new Object[] { qcId }, new RunQcMapper());
    RunQC e = eResults.size() > 0 ? (RunQC) eResults.get(0) : null;
    return e;
  }

  @Override
  public RunQC lazyGet(long qcId) throws IOException {
    List eResults = template.query(RUN_QC_SELECT_BY_ID, new Object[] { qcId }, new RunQcMapper(true));
    RunQC e = eResults.size() > 0 ? (RunQC) eResults.get(0) : null;
    return e;
  }

  @Override
  public Collection<RunQC> listByRunId(long runId) throws IOException {
    return new LinkedList(template.query(RUN_QC_SELECT_BY_RUN_ID, new Object[] { runId }, new RunQcMapper(true)));
  }

  @Override
  public Collection<RunQC> listAll() throws IOException {
    return template.query(RUN_QC, new RunQcMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  public Collection<Partition> listPartitionSelectionsByRunQcId(long runQcId) throws IOException {
    return template.query(PARTITIONS_BY_RUN_QC, new Object[] { runQcId }, new PartitionMapper());
  }

  @Override
  public boolean remove(RunQC qc) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (qc.isDeletable() && (namedTemplate.update(RUN_QC_DELETE, new MapSqlParameterSource().addValue("qcId", qc.getId())) == 1)) {
      Run r = qc.getRun();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (r != null) runDAO.save(r);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (r != null) {
          DbUtils.updateCaches(cacheManager, r, Run.class);
        }
      }
      return true;
    }
    return false;
  }

  public class PartitionMapper implements RowMapper<Partition> {
    @Override
    public Partition mapRow(ResultSet rs, int rowNum) throws SQLException {
      try {
        SequencerPartitionContainer<SequencerPoolPartition> f = sequencerPartitionContainerDAO
            .lazyGet(rs.getLong("containers_containerId"));
        for (Partition p : f.getPartitions()) {
          if (rs.getLong("partitionNumber") == p.getPartitionNumber()) {
            p.setSequencerPartitionContainer(f);
            return p;
          }
        }
      } catch (IOException e) {
        log.error("parition row mapper", e);
      }
      return null;
    }
  }

  public class RunQcMapper extends CacheAwareRowMapper<RunQC> {
    public RunQcMapper() {
      // run qcs aren't cached at present
      super(RunQC.class, false, false);
    }

    public RunQcMapper(boolean lazy) {
      // run qcs aren't cached at present
      super(RunQC.class, lazy, false);
    }

    @Override
    public RunQC mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("qcId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for RunQC " + id);
          return (RunQC) element.getObjectValue();
        }
      }
      RunQC s = dataObjectFactory.getRunQC();
      s.setId(id);
      s.setQcCreator(rs.getString("qcUserName"));
      s.setQcDate(rs.getDate("qcDate"));
      s.setInformation(rs.getString("information"));
      s.setDoNotProcess(rs.getBoolean("doNotProcess"));

      try {
        s.setQcType(getRunQcTypeById(rs.getLong("qcMethod")));
        s.setPartitionSelections(new ArrayList<Partition>(listPartitionSelectionsByRunQcId(id)));

        if (!isLazy()) {
          s.setRun(runDAO.get(rs.getLong("run_runId")));
        }
      } catch (IOException e) {
        log.error("run QC row mapper", e);
      } catch (MalformedRunException e) {
        log.error("run QC row mapper", e);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), s));
      }

      return s;
    }
  }

  @Override
  public Collection<QcType> listAllRunQcTypes() throws IOException {
    return template.query(RUN_QC_TYPE_SELECT, new RunQcTypeMapper());
  }

  @Override
  public QcType getRunQcTypeById(long qcTypeId) throws IOException {
    List eResults = template.query(RUN_QC_TYPE_SELECT_BY_ID, new Object[] { qcTypeId }, new RunQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }

  @Override
  public QcType getRunQcTypeByName(String qcName) throws IOException {
    List eResults = template.query(RUN_QC_TYPE_SELECT_BY_NAME, new Object[] { qcName }, new RunQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }

  public class RunQcTypeMapper implements RowMapper<QcType> {
    @Override
    public QcType mapRow(ResultSet rs, int rowNum) throws SQLException {
      QcType qt = new QcType();
      qt.setQcTypeId(rs.getLong("qcTypeId"));
      qt.setName(rs.getString("name"));
      qt.setDescription(rs.getString("description"));
      qt.setUnits(rs.getString("units"));
      return qt;
    }
  }
}
