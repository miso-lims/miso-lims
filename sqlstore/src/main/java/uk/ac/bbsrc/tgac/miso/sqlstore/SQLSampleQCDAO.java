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
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractSampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import javax.persistence.CascadeType;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

  public static final String SAMPLE_QC =
          "SELECT qcId, sample_sampleId, qcUserName, qcDate, qcMethod, results " +
          "FROM "+TABLE_NAME;

  public static final String SAMPLE_QC_SELECT_BY_ID =
         SAMPLE_QC + " WHERE qcId=?";

  public static final String SAMPLE_QC_SELECT_BY_SAMPLE_ID =
          SAMPLE_QC + " WHERE sample_sampleId=? " +
          "ORDER BY qcDate ASC";
  
  public static final String SAMPLE_QC_UPDATE =
          "UPDATE "+TABLE_NAME+" " +
          "SET sample_sampleId=:sample_sampleId, qcUserName=:qcUserName, qcDate=:qcDate, qcMethod=:qcMethod, results=:results " +
          "WHERE qcId=:qcId";

  public static final String SAMPLE_QC_TYPE_SELECT =
          "SELECT qcTypeId, name, description, qcTarget, units " +
          "FROM QCType WHERE qcTarget = 'Sample'";  

  public static final String SAMPLE_QC_TYPE_SELECT_BY_ID =
          SAMPLE_QC_TYPE_SELECT + " AND qcTypeId = ?";

  public static final String SAMPLE_QC_TYPE_SELECT_BY_NAME =
          SAMPLE_QC_TYPE_SELECT + " AND name = ?";

  public static final String SAMPLE_QC_DELETE =
          "DELETE FROM "+TABLE_NAME+" WHERE qcId=:qcId";

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

  @Transactional(readOnly = false, rollbackFor = IOException.class)
  public long save(SampleQC sampleQC) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("sample_sampleId", sampleQC.getSample().getSampleId())
            .addValue("qcUserName", sampleQC.getQcCreator())
            .addValue("qcDate", sampleQC.getQcDate())
            .addValue("qcMethod", sampleQC.getQcType().getQcTypeId())
            .addValue("results", sampleQC.getResults());

    if (sampleQC.getQcId() == AbstractSampleQC.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                              .withTableName(TABLE_NAME)
                              .usingGeneratedKeyColumns("qcId");
      Number newId = insert.executeAndReturnKey(params);
      sampleQC.setQcId(newId.longValue());
    }
    else {
      params.addValue("qcId", sampleQC.getQcId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(SAMPLE_QC_UPDATE, params);
    }

    if (this.cascadeType != null) {
      Sample s = sampleQC.getSample();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (s!=null) sampleDAO.save(s);
      }
      else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (s != null) {
          Cache pc = cacheManager.getCache("sampleCache");
          pc.remove(DbUtils.hashCodeCacheKeyFor(s.getSampleId()));
        }
      }
      else if (this.cascadeType.equals(CascadeType.ALL)) {
        if (s!=null) {
          sampleDAO.save(s);
          Cache pc = cacheManager.getCache("sampleCache");
          pc.remove(DbUtils.hashCodeCacheKeyFor(s.getSampleId()));
        }
      }
    }
    return sampleQC.getQcId();
  }

  public SampleQC get(long qcId) throws IOException {
    List eResults = template.query(SAMPLE_QC_SELECT_BY_ID, new Object[]{qcId}, new SampleQcMapper());
    SampleQC e = eResults.size() > 0 ? (SampleQC) eResults.get(0) : null;
    return e;
  }

  public SampleQC lazyGet(long qcId) throws IOException {
    List eResults = template.query(SAMPLE_QC_SELECT_BY_ID, new Object[]{qcId}, new LazySampleQcMapper());
    SampleQC e = eResults.size() > 0 ? (SampleQC) eResults.get(0) : null;
    return e;
  }

  public Collection<SampleQC> listBySampleId(long sampleId) throws IOException {
    return new LinkedList(template.query(SAMPLE_QC_SELECT_BY_SAMPLE_ID, new Object[]{sampleId}, new LazySampleQcMapper()));
  }

  public Collection<SampleQC> listAll() throws IOException {
    return template.query(SAMPLE_QC, new LazySampleQcMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM "+TABLE_NAME);
  }

  public boolean remove(SampleQC qc) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (qc.isDeletable() &&
           (namedTemplate.update(SAMPLE_QC_DELETE,
                                 new MapSqlParameterSource().addValue("qcId", qc.getQcId())) == 1)) {
      Sample s = qc.getSample();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (s!=null) sampleDAO.save(s);
      }
      else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (s != null) {
          Cache pc = cacheManager.getCache("sampleCache");
          pc.remove(DbUtils.hashCodeCacheKeyFor(s.getSampleId()));
        }
      }
      return true;
    }
    return false;
  }

  public class LazySampleQcMapper implements RowMapper<SampleQC> {
    public SampleQC mapRow(ResultSet rs, int rowNum) throws SQLException {
      SampleQC s = dataObjectFactory.getSampleQC();
      s.setQcId(rs.getLong("qcId"));
      s.setQcCreator(rs.getString("qcUserName"));
      s.setQcDate(rs.getDate("qcDate"));
      s.setResults(rs.getDouble("results"));

      try {
        s.setQcType(getSampleQcTypeById(rs.getLong("qcMethod")));
      }
      catch (IOException e) {
        e.printStackTrace();
      }

      return s;
    }
  }

  public class SampleQcMapper implements RowMapper<SampleQC> {
    public SampleQC mapRow(ResultSet rs, int rowNum) throws SQLException {
      SampleQC s = dataObjectFactory.getSampleQC();
      s.setQcId(rs.getLong("qcId"));
      s.setQcCreator(rs.getString("qcUserName"));
      s.setQcDate(rs.getDate("qcDate"));
      s.setResults(rs.getDouble("results"));

      try {
        s.setSample(sampleDAO.get(rs.getLong("sample_sampleId")));
        s.setQcType(getSampleQcTypeById(rs.getLong("qcMethod")));
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      catch (MalformedSampleException e) {
        e.printStackTrace();
      }
      return s;
    }
  }

  public Collection<QcType> listAllSampleQcTypes() throws IOException {
    return template.query(SAMPLE_QC_TYPE_SELECT, new SampleQcTypeMapper());
  }

  public QcType getSampleQcTypeById(long qcTypeId) throws IOException {
    List eResults = template.query(SAMPLE_QC_TYPE_SELECT_BY_ID, new Object[]{qcTypeId}, new SampleQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }

  public QcType getSampleQcTypeByName(String qcName) throws IOException {
    List eResults = template.query(SAMPLE_QC_TYPE_SELECT_BY_NAME, new Object[]{qcName}, new SampleQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }  

  public class SampleQcTypeMapper implements RowMapper<QcType> {
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
