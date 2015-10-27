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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractPoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.PoolQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import javax.persistence.CascadeType;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.1.9
 */
public class SQLPoolQCDAO implements PoolQcStore {
  private static final String TABLE_NAME = "PoolQC";

  public static final String POOL_QC = "SELECT qcId, pool_poolId, qcUserName, qcDate, qcMethod, results " + "FROM " + TABLE_NAME;

  public static final String POOL_QC_SELECT_BY_ID = POOL_QC + " WHERE qcId=?";

  public static final String POOL_QC_SELECT_BY_POOL_ID = POOL_QC + " WHERE pool_poolId=? " + "ORDER BY qcDate ASC";

  public static final String POOL_QC_UPDATE = "UPDATE " + TABLE_NAME
      + " SET pool_poolId=:pool_poolId, qcUserName=:qcUserName, qcDate=:qcDate, qcMethod=:qcMethod, results=:results " + "WHERE qcId=:qcId";

  public static final String POOL_QC_TYPE_SELECT = "SELECT qcTypeId, name, description, qcTarget, units "
      + "FROM QCType WHERE qcTarget = 'Pool'";

  public static final String POOL_QC_TYPE_SELECT_BY_ID = POOL_QC_TYPE_SELECT + " AND qcTypeId = ?";

  public static final String POOL_QC_TYPE_SELECT_BY_NAME = POOL_QC_TYPE_SELECT + " AND name = ?";

  public static final String POOL_QC_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE qcId=:qcId";

  protected static final Logger log = LoggerFactory.getLogger(SQLPoolQCDAO.class);

  private JdbcTemplate template;
  private PoolStore poolDAO;
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

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  @Transactional(readOnly = false, rollbackFor = IOException.class)
  public long save(PoolQC poolQC) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("pool_poolId", poolQC.getPool().getId()).addValue("qcUserName", poolQC.getQcCreator())
        .addValue("qcDate", poolQC.getQcDate()).addValue("qcMethod", poolQC.getQcType().getQcTypeId())
        .addValue("results", poolQC.getResults());

    if (poolQC.getId() == AbstractPoolQC.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("qcId");
      Number newId = insert.executeAndReturnKey(params);
      poolQC.setId(newId.longValue());
    } else {
      params.addValue("qcId", poolQC.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(POOL_QC_UPDATE, params);
    }

    if (this.cascadeType != null) {
      Pool l = poolQC.getPool();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (l != null) poolDAO.save(l);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (l != null) {
          // Cache pc = cacheManager.getCache("poolCache");
          // pc.remove(DbUtils.hashCodeCacheKeyFor(l.getId()));
          DbUtils.updateCaches(cacheManager, l, Pool.class);
        }
      } else if (this.cascadeType.equals(CascadeType.ALL)) {
        if (l != null) {
          poolDAO.save(l);
          // Cache pc = cacheManager.getCache("poolCache");
          // pc.remove(DbUtils.hashCodeCacheKeyFor(l.getId()));
          DbUtils.updateCaches(cacheManager, l, Pool.class);
        }
      }
    }
    return poolQC.getId();
  }

  public PoolQC get(long qcId) throws IOException {
    List<PoolQC> eResults = template.query(POOL_QC_SELECT_BY_ID, new Object[] { qcId }, new PoolQcMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  public PoolQC lazyGet(long qcId) throws IOException {
    List<PoolQC> eResults = template.query(POOL_QC_SELECT_BY_ID, new Object[] { qcId }, new PoolQcMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  public Collection<PoolQC> listByPoolId(long poolId) throws IOException {
    return template.query(POOL_QC_SELECT_BY_POOL_ID, new Object[] { poolId }, new PoolQcMapper(true));
  }

  public Collection<PoolQC> listAll() throws IOException {
    return template.query(POOL_QC, new PoolQcMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  public boolean remove(PoolQC qc) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (qc.isDeletable() && (namedTemplate.update(POOL_QC_DELETE, new MapSqlParameterSource().addValue("qcId", qc.getId())) == 1)) {
      Pool l = qc.getPool();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (l != null) poolDAO.save(l);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (l != null) {
          // Cache pc = cacheManager.getCache("poolCache");
          // pc.remove(DbUtils.hashCodeCacheKeyFor(l.getId()));
          DbUtils.updateCaches(cacheManager, l, Pool.class);
        }
      }
      return true;
    }
    return false;
  }

  public class PoolQcMapper extends CacheAwareRowMapper<PoolQC> {
    public PoolQcMapper() {
      // pool qcs aren't cached at present
      super(PoolQC.class, false, false);
    }

    public PoolQcMapper(boolean lazy) {
      // pool qcs aren't cached at present
      super(PoolQC.class, lazy, false);
    }

    public PoolQC mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("qcId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for PoolQC " + id);
          return (PoolQC) element.getObjectValue();
        }
      }

      PoolQC s = dataObjectFactory.getPoolQC();
      s.setId(id);
      s.setQcCreator(rs.getString("qcUserName"));
      s.setQcDate(rs.getDate("qcDate"));
      s.setResults(rs.getDouble("results"));

      try {
        s.setQcType(getPoolQcTypeById(rs.getLong("qcMethod")));
        if (!isLazy()) {
          s.setPool(poolDAO.get(rs.getLong("pool_poolId")));
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (MalformedPoolException e) {
        e.printStackTrace();
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), s));
      }

      return s;
    }
  }

  public Collection<QcType> listAllPoolQcTypes() throws IOException {
    return template.query(POOL_QC_TYPE_SELECT, new PoolQcTypeMapper());
  }

  public QcType getPoolQcTypeById(long qcTypeId) throws IOException {
    List eResults = template.query(POOL_QC_TYPE_SELECT_BY_ID, new Object[] { qcTypeId }, new PoolQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }

  public QcType getPoolQcTypeByName(String qcName) throws IOException {
    List eResults = template.query(POOL_QC_TYPE_SELECT_BY_NAME, new Object[] { qcName }, new PoolQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }

  public class PoolQcTypeMapper implements RowMapper<QcType> {
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
