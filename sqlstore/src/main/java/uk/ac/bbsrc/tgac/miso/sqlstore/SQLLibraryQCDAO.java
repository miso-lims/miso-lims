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

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
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
 * @since 0.0.2
 */
public class SQLLibraryQCDAO implements LibraryQcStore {
  private static final String TABLE_NAME = "LibraryQC";

  public static final String LIBRARY_QC =
          "SELECT qcId, library_libraryId, qcUserName, qcDate, qcMethod, results, insertSize " +
          "FROM "+TABLE_NAME;

  public static final String LIBRARY_QC_SELECT_BY_ID =
         LIBRARY_QC + " WHERE qcId=?";

  public static final String LIBRARY_QC_SELECT_BY_LIBRARY_ID =
          LIBRARY_QC + " WHERE library_libraryId=? " +
          "ORDER BY qcDate ASC";

  public static final String LIBRARY_QC_UPDATE =
          "UPDATE "+TABLE_NAME+
          " SET library_libraryId=:library_libraryId, qcUserName=:qcUserName, qcDate=:qcDate, qcMethod=:qcMethod, results=:results, insertSize=:insertSize " +
          "WHERE qcId=:qcId";

  public static final String LIBRARY_QC_TYPE_SELECT =
          "SELECT qcTypeId, name, description, qcTarget, units " +
          "FROM QCType WHERE qcTarget = 'Library'";

  public static final String LIBRARY_QC_TYPE_SELECT_BY_ID =
          LIBRARY_QC_TYPE_SELECT + " AND qcTypeId = ?";

  public static final String LIBRARY_QC_TYPE_SELECT_BY_NAME =
          LIBRARY_QC_TYPE_SELECT + " AND name = ?";

  public static final String LIBRARY_QC_DELETE =
         "DELETE FROM "+TABLE_NAME+" WHERE qcId=:qcId";

  protected static final Logger log = LoggerFactory.getLogger(SQLLibraryQCDAO.class);

  private JdbcTemplate template;
  private LibraryStore libraryDAO;
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

  public void setLibraryDAO(LibraryStore libraryDAO) {
    this.libraryDAO = libraryDAO;
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
  public long save(LibraryQC libraryQC) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("library_libraryId", libraryQC.getLibrary().getId())
            //.addValue("qcUserName", SecurityContextHolder.getContext().getAuthentication().getName())
            .addValue("qcUserName", libraryQC.getQcCreator())
            .addValue("qcDate", libraryQC.getQcDate())
            .addValue("qcMethod", libraryQC.getQcType().getQcTypeId())
            .addValue("results", libraryQC.getResults())
            .addValue("insertSize", libraryQC.getInsertSize());

    if (libraryQC.getId() == AbstractLibraryQC.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                              .withTableName(TABLE_NAME)
                              .usingGeneratedKeyColumns("qcId");
      Number newId = insert.executeAndReturnKey(params);
      libraryQC.setId(newId.longValue());
    }
    else {
      params.addValue("qcId", libraryQC.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(LIBRARY_QC_UPDATE, params);
    }

    if (this.cascadeType != null) {
      Library l = libraryQC.getLibrary();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (l != null) libraryDAO.save(l);
      }
      else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (l != null) {
          //Cache pc = cacheManager.getCache("libraryCache");
          //pc.remove(DbUtils.hashCodeCacheKeyFor(l.getId()));
          DbUtils.updateCaches(cacheManager, l, Library.class);
        }
      }
      else if (this.cascadeType.equals(CascadeType.ALL)) {
        if (l != null) {
          libraryDAO.save(l);
          //Cache pc = cacheManager.getCache("libraryCache");
          //pc.remove(DbUtils.hashCodeCacheKeyFor(l.getId()));
          DbUtils.updateCaches(cacheManager, l, Library.class);
        }
      }
    }
    return libraryQC.getId();
  }

  public LibraryQC get(long qcId) throws IOException {
    List eResults = template.query(LIBRARY_QC_SELECT_BY_ID, new Object[]{qcId}, new LibraryQcMapper());
    LibraryQC  e = eResults.size() > 0 ? (LibraryQC) eResults.get(0) : null;
    return e;
  }

  public LibraryQC  lazyGet(long qcId) throws IOException {
    List eResults = template.query(LIBRARY_QC_SELECT_BY_ID, new Object[]{qcId}, new LibraryQcMapper(true));
    LibraryQC  e = eResults.size() > 0 ? (LibraryQC) eResults.get(0) : null;
    return e;
  }

  public Collection<LibraryQC> listByLibraryId(long libraryId) throws IOException {
    return template.query(LIBRARY_QC_SELECT_BY_LIBRARY_ID, new Object[]{libraryId}, new LibraryQcMapper(true));
  }

  public Collection<LibraryQC> listAll() throws IOException {
    return template.query(LIBRARY_QC, new LibraryQcMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM "+TABLE_NAME);
  }

  public boolean remove(LibraryQC qc) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (qc.isDeletable() &&
           (namedTemplate.update(LIBRARY_QC_DELETE,
                                 new MapSqlParameterSource().addValue("qcId", qc.getId())) == 1)) {
      Library l = qc.getLibrary();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (l != null) libraryDAO.save(l);
      }
      else if (this.cascadeType.equals(CascadeType.REMOVE) || this.cascadeType.equals(CascadeType.ALL)) {
        if (l != null) {
          DbUtils.updateCaches(cacheManager, l, Library.class);
        }
      }
      return true;
    }
    return false;
  }

  public class LibraryQcMapper extends CacheAwareRowMapper<LibraryQC> {
    public LibraryQcMapper() {
      //library qcs aren't cached at present
      super(LibraryQC.class, false, false);
    }

    public LibraryQcMapper(boolean lazy) {
      //library qcs aren't cached at present
      super(LibraryQC.class, lazy, false);
    }

    @Override
    public LibraryQC mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("qcId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for LibraryQC " + id);
          return (LibraryQC)element.getObjectValue();
        }
      }
      LibraryQC s = dataObjectFactory.getLibraryQC();
      s.setId(rs.getLong("qcId"));
      s.setQcCreator(rs.getString("qcUserName"));
      s.setQcDate(rs.getDate("qcDate"));
      s.setResults(rs.getDouble("results"));
      s.setInsertSize(rs.getInt("insertSize"));

      try {
        s.setQcType(getLibraryQcTypeById(rs.getLong("qcMethod")));

        if (!isLazy()) {
          s.setLibrary(libraryDAO.get(rs.getLong("library_libraryId")));
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      catch (MalformedLibraryException e) {
        e.printStackTrace();
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id) ,s));
      }

      return s;
    }
  }

  public Collection<QcType> listAllLibraryQcTypes() throws IOException {
    return template.query(LIBRARY_QC_TYPE_SELECT, new LibraryQcTypeMapper());
  }

  public QcType getLibraryQcTypeById(long qcTypeId) throws IOException {
    List eResults = template.query(LIBRARY_QC_TYPE_SELECT_BY_ID, new Object[]{qcTypeId}, new LibraryQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }

  public QcType getLibraryQcTypeByName(String qcName) throws IOException {
    List eResults = template.query(LIBRARY_QC_TYPE_SELECT_BY_NAME, new Object[]{qcName}, new LibraryQcTypeMapper());
    QcType e = eResults.size() > 0 ? (QcType) eResults.get(0) : null;
    return e;
  }

  public class LibraryQcTypeMapper implements RowMapper<QcType> {
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
