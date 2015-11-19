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

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.EmPCRDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.EmPCRStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
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
public class SQLEmPCRDAO implements EmPCRStore {
  private static final String TABLE_NAME = "emPCR";

  public static String EMPCR_SELECT = "SELECT pcrId, concentration, pcrUserName, creationDate, name, dilution_dilutionId, securityProfile_profileId "
      + "FROM " + TABLE_NAME;

  public static final String EMPCR_SELECT_BY_PCR_ID = EMPCR_SELECT + " WHERE pcrId=?";

  public static final String EMPCR_SELECT_BY_RELATED_DILUTION = EMPCR_SELECT + " WHERE dilution_dilutionId=?";

  public static String EMPCR_SELECT_BY_PROJECT = "SELECT e.* FROM Project p "
      + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " + "INNER JOIN " + TABLE_NAME
      + " e ON e.dilution_dilutionId = ld.dilutionId " + "WHERE p.projectId=?";

  public static final String EMPCR_UPDATE = "UPDATE " + TABLE_NAME
      + " SET concentration=:concentration, pcrUserName=:pcrUserName, creationDate=:creationDate, name=:name, dilution_dilutionId=:dilution_dilutionId, securityProfile_profileId=:securityProfile_profileId "
      + "WHERE pcrId=:pcrId";

  public static final String EMPCR_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE pcrId=:pcrId";

  protected static final Logger log = LoggerFactory.getLogger(SQLEmPCRDAO.class);

  private JdbcTemplate template;
  private LibraryDilutionStore libraryDilutionDAO;
  private EmPCRDilutionStore emPCRDilutionDAO;
  private CascadeType cascadeType;
  private Store<SecurityProfile> securityProfileDAO;

  @Autowired
  private MisoNamingScheme<emPCR> namingScheme;

  @Override
  public MisoNamingScheme<emPCR> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<emPCR> namingScheme) {
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

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setLibraryDilutionDAO(LibraryDilutionStore libraryDilutionDAO) {
    this.libraryDilutionDAO = libraryDilutionDAO;
  }

  public void setEmPCRDilutionDAO(EmPCRDilutionStore emPCRDilutionDAO) {
    this.emPCRDilutionDAO = emPCRDilutionDAO;
  }

  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "emPCRCache",
      "lazyEmPCRCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long save(emPCR pcr) throws IOException {
    Long securityProfileId = pcr.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) {
      securityProfileId = securityProfileDAO.save(pcr.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("concentration", pcr.getConcentration());
    params.addValue("creationDate", pcr.getCreationDate());
    params.addValue("pcrUserName", pcr.getPcrCreator());
    params.addValue("dilution_dilutionId", pcr.getLibraryDilution().getId());
    params.addValue("securityProfile_profileId", securityProfileId);

    if (pcr.getId() == emPCR.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("pcrId");
      try {
        pcr.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", pcr);
        pcr.setName(name);

        if (namingScheme.validateField("name", pcr.getName())) {
          params.addValue("name", name);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != pcr.getId()) {
            log.error("Expected emPCR ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(EMPCR_DELETE, new MapSqlParameterSource().addValue("pcrId", newId.longValue()));
            throw new IOException("Something bad happened. Expected emPCR ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save emPCR - invalid field:" + pcr.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save emPCR - issue with naming scheme", e);
      }
    } else {
      try {
        if (namingScheme.validateField("name", pcr.getName())) {
          params.addValue("pcrId", pcr.getId());
          params.addValue("name", pcr.getName());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(EMPCR_UPDATE, params);
        } else {
          throw new IOException("Cannot save emPCR - invalid field:" + pcr.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save emPCR - issue with naming scheme", e);
      }
    }

    if (this.cascadeType != null) {
      LibraryDilution ld = pcr.getLibraryDilution();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (ld != null) libraryDilutionDAO.save(ld);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (ld != null) {
          DbUtils.updateCaches(cacheManager, ld, LibraryDilution.class);
        }
      }
    }

    return pcr.getId();
  }

  @Override
  @Cacheable(cacheName = "emPCRCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public emPCR get(long pcrId) throws IOException {
    List eResults = template.query(EMPCR_SELECT_BY_PCR_ID, new Object[] { pcrId }, new EmPCRMapper());
    emPCR e = eResults.size() > 0 ? (emPCR) eResults.get(0) : null;
    return e;
  }

  @Override
  public emPCR lazyGet(long pcrId) throws IOException {
    List eResults = template.query(EMPCR_SELECT_BY_PCR_ID, new Object[] { pcrId }, new EmPCRMapper(true));
    emPCR e = eResults.size() > 0 ? (emPCR) eResults.get(0) : null;
    return e;
  }

  @Override
  public Collection<emPCR> listAllByProjectId(long projectId) throws IOException {
    return template.query(EMPCR_SELECT_BY_PROJECT, new Object[] { projectId }, new EmPCRMapper());
  }

  @Override
  public Collection<emPCR> listAll() throws IOException {
    return template.query(EMPCR_SELECT, new EmPCRMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public Collection<emPCR> listAllByDilutionId(long dilutionId) throws IOException {
    return template.query(EMPCR_SELECT_BY_RELATED_DILUTION, new Object[] { dilutionId }, new EmPCRMapper());
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "emPCRCache",
      "lazyEmPCRCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean remove(emPCR e) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (e.isDeletable() && (namedTemplate.update(EMPCR_DELETE, new MapSqlParameterSource().addValue("pcrId", e.getId())) == 1)) {
      LibraryDilution ld = e.getLibraryDilution();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (ld != null) libraryDilutionDAO.save(ld);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (ld != null) {
          DbUtils.updateCaches(cacheManager, ld, LibraryDilution.class);
        }
      }
      return true;
    }
    return false;
  }

  public class EmPCRMapper extends CacheAwareRowMapper<emPCR> {
    public EmPCRMapper() {
      super(emPCR.class);
    }

    public EmPCRMapper(boolean lazy) {
      super(emPCR.class, lazy);
    }

    @Override
    public emPCR mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("pcrId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for emPCR " + id);
          return (emPCR) element.getObjectValue();
        }
      }

      emPCR pcr = dataObjectFactory.getEmPCR();
      pcr.setId(id);
      pcr.setConcentration(rs.getDouble("concentration"));
      pcr.setName(rs.getString("name"));
      pcr.setCreationDate(rs.getDate("creationDate"));
      pcr.setPcrCreator(rs.getString("pcrUserName"));

      try {
        pcr.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        pcr.setLibraryDilution(libraryDilutionDAO.get(rs.getLong("dilution_dilutionId")));
        if (!isLazy()) {
          pcr.setEmPcrDilutions(emPCRDilutionDAO.listAllByEmPCRId(id));
        }
      } catch (IOException e1) {
        log.error("EmPCR row mapper", e1);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), pcr));
      }

      return pcr;
    }
  }
}
