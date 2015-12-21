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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLSampleQCDAO implements SampleQcStore {
  private static final String TABLE_NAME = "SampleQC";

  public static final String SAMPLE_QC = "SELECT qcId, sample_sampleId, qcUserName, qcDate, qcMethod, results " + "FROM " + TABLE_NAME;

  public static final String SAMPLE_QC_SELECT_BY_ID = SAMPLE_QC + " WHERE qcId=?";

  public static final String SAMPLE_QC_SELECT_BY_SAMPLE_ID = SAMPLE_QC + " WHERE sample_sampleId=? " + "ORDER BY qcDate ASC";

  public static final String SAMPLE_QC_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET sample_sampleId=:sample_sampleId, qcUserName=:qcUserName, qcDate=:qcDate, qcMethod=:qcMethod, results=:results "
      + "WHERE qcId=:qcId";

  public static final String SAMPLE_QC_TYPE_SELECT = "SELECT qcTypeId, name, description, qcTarget, units "
      + "FROM QCType WHERE qcTarget = 'Sample'";

  public static final String SAMPLE_QC_TYPE_SELECT_BY_ID = SAMPLE_QC_TYPE_SELECT + " AND qcTypeId = ?";

  public static final String SAMPLE_QC_TYPE_SELECT_BY_NAME = SAMPLE_QC_TYPE_SELECT + " AND name = ?";

  public static final String SAMPLE_QC_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE qcId=:qcId";

  protected static final Logger log = LoggerFactory.getLogger(SQLSampleQCDAO.class);

  private JdbcTemplate template;
  private SampleStore sampleDAO;
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

  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
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
  @TriggersRemove(cacheName = { "sampleQCCache",
      "lazySampleQCCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long save(SampleQC sampleQC) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("sample_sampleId", sampleQC.getSample().getId());
    params.addValue("qcUserName", sampleQC.getQcCreator());
    params.addValue("qcDate", sampleQC.getQcDate());
    params.addValue("qcMethod", sampleQC.getQcType().getQcTypeId());
    params.addValue("results", sampleQC.getResults());

    if (sampleQC.getId() == AbstractQC.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("qcId");
      Number newId = insert.executeAndReturnKey(params);
      sampleQC.setId(newId.longValue());
    } else {
      params.addValue("qcId", sampleQC.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(SAMPLE_QC_UPDATE, params);
    }

    if (this.cascadeType != null) {
      Sample s = sampleQC.getSample();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (s != null) sampleDAO.save(s);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (s != null) {
          DbUtils.updateCaches(cacheManager, s, Sample.class);
        }
      } else if (this.cascadeType.equals(CascadeType.ALL)) {
        if (s != null) {
          sampleDAO.save(s);
          DbUtils.updateCaches(cacheManager, s, Sample.class);
        }
      }
    }
    return sampleQC.getId();
  }

  @Override
  @Cacheable(cacheName = "sampleQCCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public SampleQC get(long qcId) throws IOException {
    List eResults = template.query(SAMPLE_QC_SELECT_BY_ID, new Object[] { qcId }, new SampleQcMapper());
    SampleQC e = eResults.size() > 0 ? (SampleQC) eResults.get(0) : null;
    return e;
  }

  @Override
  public SampleQC lazyGet(long qcId) throws IOException {
    List eResults = template.query(SAMPLE_QC_SELECT_BY_ID, new Object[] { qcId }, new SampleQcMapper(true));
    SampleQC e = eResults.size() > 0 ? (SampleQC) eResults.get(0) : null;
    return e;
  }

  @Override
  public Collection<SampleQC> listBySampleId(long sampleId) throws IOException {
    return new LinkedList(template.query(SAMPLE_QC_SELECT_BY_SAMPLE_ID, new Object[] { sampleId }, new SampleQcMapper(true)));
  }

  @Override
  public Collection<SampleQC> listAll() throws IOException {
    return template.query(SAMPLE_QC, new SampleQcMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  @TriggersRemove(cacheName = { "sampleQCCache",
      "lazySampleQCCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean remove(SampleQC qc) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (qc.isDeletable() && (namedTemplate.update(SAMPLE_QC_DELETE, new MapSqlParameterSource().addValue("qcId", qc.getId())) == 1)) {
      Sample s = qc.getSample();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (s != null) sampleDAO.save(s);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (s != null) {
          DbUtils.updateCaches(cacheManager, s, Sample.class);
        }
      }
      return true;
    }
    return false;
  }

  public class SampleQcMapper extends CacheAwareRowMapper<SampleQC> {
    public SampleQcMapper() {
      super(SampleQC.class);
    }

    public SampleQcMapper(boolean lazy) {
      super(SampleQC.class, lazy);
    }

    @Override
    public SampleQC mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("qcId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          return (SampleQC) element.getObjectValue();
        }
      }
      SampleQC s = dataObjectFactory.getSampleQC();
      s.setId(id);
      s.setQcCreator(rs.getString("qcUserName"));
      s.setQcDate(rs.getDate("qcDate"));
      s.setResults(rs.getDouble("results"));

      try {
        s.setQcType(getSampleQcTypeById(rs.getLong("qcMethod")));

        if (!isLazy()) {
          s.setSample(sampleDAO.get(rs.getLong("sample_sampleId")));
        }
      } catch (IOException e) {
        log.error("sample QC row mapper", e);
      } catch (MalformedSampleException e) {
        log.error("sample QC row mapper", e);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), s));
      }

      return s;
    }
  }

  @Override
  public Collection<QcType> listAllSampleQcTypes() throws IOException {
    return template.query(SAMPLE_QC_TYPE_SELECT, new SampleQcTypeMapper());
  }

  @Override
  public QcType getSampleQcTypeById(long qcTypeId) throws IOException {
    List eResults = template.query(SAMPLE_QC_TYPE_SELECT_BY_ID, new Object[] { qcTypeId }, new SampleQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }

  @Override
  public QcType getSampleQcTypeByName(String qcName) throws IOException {
    List eResults = template.query(SAMPLE_QC_TYPE_SELECT_BY_NAME, new Object[] { qcName }, new SampleQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }

  public class SampleQcTypeMapper implements RowMapper<QcType> {
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
